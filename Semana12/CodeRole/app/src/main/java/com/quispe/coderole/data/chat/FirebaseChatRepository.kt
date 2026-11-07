package com.quispe.coderole.data.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.functions.FirebaseFunctions
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.domain.repository.ChatRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseChatRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val functions: FirebaseFunctions,
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
            require(texto.isNotBlank()) { "Escribe un mensaje antes de enviarlo." }
            check(auth.currentUser != null) { "La sesión terminó. Vuelve a iniciar sesión." }
            functions.getHttpsCallable("responderMentor").call(
                mapOf("conversationId" to ID_CONVERSACION, "texto" to texto.trim()),
            ).await()
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    private companion object {
        const val ID_CONVERSACION = "mentor"
    }
}
