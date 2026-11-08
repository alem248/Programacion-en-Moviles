package com.quispe.coderole.data.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.domain.repository.ChatRepository
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.IOException

/**
 * Conversación por usuario en Firestore. El historial se guarda en la nube y la
 * respuesta de la mentora se genera con [GeminiChatService] (Google Gemini).
 */
class FirebaseChatRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val gemini: GeminiChatService,
) : ChatRepository {
    private fun mensajesDelUsuario() = firestore.collection("usuarios")
        .document(requireNotNull(auth.currentUser) { "Inicia sesión para abrir la conversación." }.uid)
        .collection("conversaciones").document(ID_CONVERSACION)
        .collection("mensajes")

    override fun observarConversacion(): Flow<List<MensajeConversacion>> = callbackFlow {
        val uid = auth.currentUser?.uid
        if (uid == null) {
            close(IllegalStateException("La sesión terminó. Vuelve a iniciar sesión."))
            return@callbackFlow
        }
        val registration = firestore.collection("usuarios").document(uid)
            .collection("conversaciones").document(ID_CONVERSACION)
            .collection("mensajes").orderBy("creadoEn", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                } else if (snapshot != null) {
                    val mensajes = snapshot.documents.mapNotNull { document ->
                        val texto = document.getString("texto") ?: return@mapNotNull null
                        MensajeConversacion(
                            id = document.id,
                            texto = texto,
                            esUsuario = document.getString("rol") == "usuario",
                            creadoEn = document.getTimestamp("creadoEn")?.toDate()?.time ?: 0L,
                        )
                    }
                    trySend(mensajes)
                }
            }
        awaitClose { registration.remove() }
    }

    override suspend fun enviarMensaje(texto: String): Result<Unit> {
        return try {
            val mensaje = texto.trim()
            require(mensaje.isNotBlank()) { "Escribe un mensaje antes de enviarlo." }
            require(mensaje.length <= GeminiConfig.LIMITE_CARACTERES) {
                "El mensaje supera el límite de ${GeminiConfig.LIMITE_CARACTERES} caracteres."
            }
            check(auth.currentUser != null) { "La sesión terminó. Vuelve a iniciar sesión." }

            val mensajes = mensajesDelUsuario()
            val historial = mensajes.orderBy("creadoEn", Query.Direction.DESCENDING)
                .limit(GeminiConfig.CONTEXTO_MENSAJES).get().await()
                .documents.reversed()
                .mapNotNull { documento ->
                    val contenido = documento.getString("texto") ?: return@mapNotNull null
                    val rol = documento.getString("rol")
                    if (rol == "usuario" || rol == "mentora") {
                        TurnoChat(esUsuario = rol == "usuario", texto = contenido)
                    } else {
                        null
                    }
                }

            guardar(mensajes, mensaje, "usuario")
            val respuesta = gemini.responder(historial, mensaje)
            guardar(mensajes, respuesta, "mentora")

            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    /**
     * Firestore deja la escritura en cola cuando no hay red y la tarea nunca se
     * completa; con este tope la pantalla muestra el error en lugar de quedar
     * pensando para siempre.
     */
    private suspend fun guardar(
        mensajes: CollectionReference,
        texto: String,
        rol: String,
    ) {
        try {
            withTimeout(ESPERA_ESCRITURA_MS) {
                mensajes.add(
                    mapOf(
                        "texto" to texto,
                        "rol" to rol,
                        "creadoEn" to FieldValue.serverTimestamp(),
                    ),
                ).await()
            }
        } catch (error: TimeoutCancellationException) {
            throw IOException("Sin conexión con Firestore. Vuelve a intentarlo cuando tengas red.")
        }
    }

    private companion object {
        const val ID_CONVERSACION = "mentor"
        const val ESPERA_ESCRITURA_MS = 15_000L
    }
}
