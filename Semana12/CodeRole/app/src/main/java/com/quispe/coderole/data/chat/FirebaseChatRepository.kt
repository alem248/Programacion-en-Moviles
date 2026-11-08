package com.quispe.coderole.data.chat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quispe.coderole.domain.model.ChatUpdate
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.domain.model.TipoActividad
import com.quispe.coderole.domain.repository.ActividadRepository
import com.quispe.coderole.domain.repository.ChatRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withTimeout
import java.io.IOException

/**
 * Conversación por usuario en Firestore. El historial se guarda en la nube y la
 * respuesta de la mentora se genera con [GeminiChatService] (Google Gemini).
 *
 * Ruta crítica de latencia: antes cada mensaje hacía cuatro llamadas de red en
 * serie (leer historial → guardar mensaje → Gemini → guardar respuesta). Ahora
 * el historial se mantiene en memoria con el propio listener, el mensaje del
 * usuario se guarda en paralelo a la generación y la respuesta se recibe en
 * streaming, por lo que el tiempo de espera pasa a ser el de Gemini solo.
 */
class FirebaseChatRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val gemini: GeminiChatService,
    private val actividad: ActividadRepository,
) : ChatRepository {
    /** Historial ya descargado: evita una consulta a Firestore por cada envío. */
    private val historial = MutableStateFlow<List<MensajeConversacion>>(emptyList())

    private fun mensajesDelUsuario(): CollectionReference? {
        val uid = auth.currentUser?.uid ?: return null
        return firestore.collection("usuarios").document(uid)
            .collection("conversaciones").document(ID_CONVERSACION)
            .collection("mensajes")
    }

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
                            favorita = document.getBoolean("favorita") ?: false,
                        )
                    }
                    historial.value = mensajes
                    trySend(mensajes)
                }
            }
        awaitClose { registration.remove() }
    }

    override fun enviarMensaje(texto: String): Flow<ChatUpdate> = channelFlow {
        val mensaje = texto.trim()
        val mensajes = mensajesDelUsuario()
        val errorValidacion = when {
            mensajes == null -> "La sesión terminó. Vuelve a iniciar sesión."
            mensaje.isBlank() -> "Escribe un mensaje antes de enviarlo."
            mensaje.length > GeminiConfig.LIMITE_CARACTERES ->
                "El mensaje supera el límite de ${GeminiConfig.LIMITE_CARACTERES} caracteres."
            else -> null
        }
        if (errorValidacion != null) {
            send(ChatUpdate.Fallo(IllegalArgumentException(errorValidacion)))
            return@channelFlow
        }

        val referencias = mensajes ?: return@channelFlow
        val turnos = construirTurnos(mensaje)

        // El mensaje del usuario se guarda mientras Gemini genera la respuesta:
        // las dos operaciones de red ocurren a la vez y no se suman.
        val escrituraUsuario = async { guardar(referencias, mensaje, "usuario") }

        val respuesta = StringBuilder()
        var fallo: Exception? = null
        try {
            gemini.fluir(turnos).collect { parcial ->
                respuesta.append(parcial)
                send(ChatUpdate.Parcial(respuesta.toString()))
            }
            if (respuesta.isBlank()) throw IllegalStateException("La mentora devolvió una respuesta vacía.")
            guardar(referencias, respuesta.toString(), "mentora").getOrThrow()
            actividad.registrar(TipoActividad.RESPUESTA)
        } catch (cancelacion: CancellationException) {
            throw cancelacion
        } catch (error: Exception) {
            fallo = error
        }

        val resultadoUsuario = escrituraUsuario.await()
        resultadoUsuario.getOrNull()?.let { id ->
            publicarEnHistorial(id, mensaje, esUsuario = true)
            actividad.registrar(TipoActividad.MENSAJE)
        }
        if (fallo == null) {
            resultadoUsuario.exceptionOrNull()?.let { error -> fallo = error as? Exception ?: Exception(error) }
        }

        val error = fallo
        if (error != null) send(ChatUpdate.Fallo(error)) else send(ChatUpdate.Completado)
    }

    /** Turnos enviados a Gemini: caché local o, si está vacía, la última copia remota. */
    private suspend fun construirTurnos(mensaje: String): List<TurnoChat> {
        val enCache = historial.value
        val previos = if (enCache.isEmpty()) leerHistorialRemoto() else enCache
        return previos.map { TurnoChat(it.esUsuario, it.texto) } + TurnoChat(esUsuario = true, texto = mensaje)
    }

    /**
     * Solo se consulta cuando la caché está vacía (primer envío antes de que el
     * listener entregue los mensajes); con tope, para no dejar la pantalla
     * esperando sin límite cuando Firestore no responde.
     */
    private suspend fun leerHistorialRemoto(): List<MensajeConversacion> {
        val mensajes = mensajesDelUsuario() ?: return emptyList()
        return try {
            withTimeout(ESPERA_LECTURA_MS) {
                mensajes.orderBy("creadoEn", Query.Direction.DESCENDING)
                    .limit(GeminiConfig.CONTEXTO_MENSAJES).get().await()
                    .documents.reversed()
                    .mapNotNull { documento ->
                        val contenido = documento.getString("texto") ?: return@mapNotNull null
                        MensajeConversacion(
                            id = documento.id,
                            texto = contenido,
                            esUsuario = documento.getString("rol") == "usuario",
                            creadoEn = documento.getTimestamp("creadoEn")?.toDate()?.time ?: 0L,
                            favorita = documento.getBoolean("favorita") ?: false,
                        )
                    }
                    .also { guardado -> historial.update { actual -> if (actual.isEmpty()) guardado else actual } }
            }
        } catch (error: TimeoutCancellationException) {
            emptyList()
        }
    }

    override suspend fun cambiarRespuestaFavorita(id: String, favorita: Boolean): Result<Unit> {
        return try {
            val mensajes = mensajesDelUsuario()
                ?: return Result.failure(IllegalStateException("La sesión terminó. Vuelve a iniciar sesión."))
            val referencia = mensajes.document(id)
            val mensaje = referencia.get().await()
            check(mensaje.getString("rol") == "mentora") { "Solo puedes guardar respuestas de tu mentora." }
            referencia.update("favorita", favorita).await()
            if (favorita) actividad.registrar(TipoActividad.FAVORITO, "Respuesta de la mentora")
            Result.success(Unit)
        } catch (error: Exception) {
            Result.failure(error)
        }
    }

    /**
     * Escribe el mensaje y devuelve su identificador; en cuanto existe se
     * publica en la caché para que el historial no dependa del siguiente
     * envío del listener (que puede tardar unos cientos de milisegundos).
     */
    private suspend fun guardar(
        mensajes: CollectionReference,
        texto: String,
        rol: String,
    ): Result<String> = try {
        val id = withTimeout(ESPERA_ESCRITURA_MS) {
            mensajes.add(
                mapOf(
                    "texto" to texto,
                    "rol" to rol,
                    "favorita" to false,
                    "creadoEn" to FieldValue.serverTimestamp(),
                ),
            ).await().id
        }
        publicarEnHistorial(id, texto, rol == "usuario")
        Result.success(id)
    } catch (error: TimeoutCancellationException) {
        Result.failure(IOException("Sin conexión con Firestore. Vuelve a intentarlo cuando tengas red."))
    } catch (cancelacion: CancellationException) {
        throw cancelacion
    } catch (error: Exception) {
        Result.failure(error)
    }

    private fun publicarEnHistorial(id: String, texto: String, esUsuario: Boolean) {
        historial.update { actual ->
            if (actual.any { it.id == id }) actual
            else actual + MensajeConversacion(
                id = id,
                texto = texto,
                esUsuario = esUsuario,
                creadoEn = System.currentTimeMillis(),
                favorita = false,
            )
        }
    }

    private companion object {
        const val ID_CONVERSACION = "mentor"
        const val ESPERA_ESCRITURA_MS = 15_000L
        const val ESPERA_LECTURA_MS = 8_000L
    }
}
