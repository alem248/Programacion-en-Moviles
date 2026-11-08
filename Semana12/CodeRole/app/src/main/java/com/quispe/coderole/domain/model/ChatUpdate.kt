package com.quispe.coderole.domain.model

/**
 * Eventos que emite [com.quispe.coderole.domain.repository.ChatRepository.enviarMensaje]
 * mientras se genera la respuesta: los parciales llegan en streaming para que la
 * pantalla pueda pintar el texto a medida que Gemini lo produce.
 */
sealed interface ChatUpdate {
    /** Fragmento acumulado de la respuesta que se está generando. */
    data class Parcial(val texto: String) : ChatUpdate

    /** La respuesta terminó y ya quedó guardada en Firestore. */
    data object Completado : ChatUpdate

    /** Algo falló; el usuario puede volver a intentarlo. */
    data class Fallo(val error: Exception) : ChatUpdate
}
