package com.quispe.coderole.domain.model

/** Tipos de interacción que el panel de actividad registra por día. */
enum class TipoActividad { MENSAJE, RESPUESTA, FAVORITO, SESION }

/** Una interacción concreta dentro de un día. */
data class RegistroActividad(
    val id: String,
    val tipo: TipoActividad,
    val detalle: String,
    val creadoEn: Long,
)

/**
 * Métricas de interacción de un día calendario (`fecha` con formato
 * `aaaa-MM-dd`) para el panel de actividad del mentor.
 */
data class ActividadDiaria(
    val fecha: String,
    val mensajes: Int = 0,
    val respuestas: Int = 0,
    val favoritos: Int = 0,
    val sesiones: Int = 0,
    val registros: List<RegistroActividad> = emptyList(),
) {
    /** Total de interacciones del día. */
    val total: Int get() = mensajes + respuestas + favoritos + sesiones
}
