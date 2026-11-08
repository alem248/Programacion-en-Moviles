package com.quispe.coderole.domain.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Fechas del panel de actividad: clave `aaaa-MM-dd`, etiqueta legible y hora. */
object Fechas {
    private val bloqueo = Any()
    private val clave = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    private val etiqueta = SimpleDateFormat("dd 'de' MMMM", Locale.forLanguageTag("es"))
    private val hora = SimpleDateFormat("HH:mm", Locale.US)

    fun hoy(): String = de(System.currentTimeMillis())

    fun de(millis: Long): String = synchronized(bloqueo) { clave.format(Date(millis)) }

    /** Convierte `2026-11-08` en `8 de noviembre`; devuelve la clave si no se puede interpretar. */
    fun legible(fecha: String): String = synchronized(bloqueo) {
        val fechaParseada = runCatching { clave.parse(fecha) }.getOrNull()
        if (fechaParseada == null) fecha else etiqueta.format(fechaParseada)
    }

    fun horaDe(millis: Long): String = synchronized(bloqueo) { hora.format(Date(millis)) }
}
