package com.quispe.coderole.domain.repository

import com.quispe.coderole.domain.model.MensajeConversacion
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observarConversacion(): Flow<List<MensajeConversacion>>
    suspend fun enviarMensaje(texto: String): Result<Unit>
    suspend fun cambiarRespuestaFavorita(id: String, favorita: Boolean): Result<Unit>
}
