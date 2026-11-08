package com.quispe.coderole.domain.repository

import com.quispe.coderole.domain.model.ChatUpdate
import com.quispe.coderole.domain.model.MensajeConversacion
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observarConversacion(): Flow<List<MensajeConversacion>>

    /**
     * Envía el mensaje y devuelve la respuesta en streaming: cada [ChatUpdate.Parcial]
     * trae el texto acumulado hasta ese momento y el flujo termina con
     * [ChatUpdate.Completado] o [ChatUpdate.Fallo].
     */
    fun enviarMensaje(texto: String): Flow<ChatUpdate>

    suspend fun cambiarRespuestaFavorita(id: String, favorita: Boolean): Result<Unit>
}
