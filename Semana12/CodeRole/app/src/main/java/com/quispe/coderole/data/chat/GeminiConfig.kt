package com.quispe.coderole.data.chat

import com.quispe.coderole.BuildConfig

/**
 * Configuración del cliente de Google Gemini para el chat de la mentora.
 *
 * La clave se carga desde local.properties para no dejarla escrita en el código.
 * La API todavía se invoca desde el cliente; para producción debe trasladarse a
 * una función de servidor, porque una clave incluida en el APK puede extraerse.
 */
object GeminiConfig {
    const val API_KEY = BuildConfig.GEMINI_API_KEY

    /** Endpoint base de la Google Generative Language API (REST v1beta). */
    const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/"

    /**
     * Modelos probados en orden: si uno devuelve 404, 429 o 503 se pasa al
     * siguiente, así el chat sigue respondiendo cuando hay alta demanda.
     */
    val MODELOS = listOf(
        "gemini-2.5-flash",
        "gemini-2.0-flash",
        "gemini-1.5-flash",
    )

    /** Máximo de mensajes del historial que se envían como contexto. */
    const val CONTEXTO_MENSAJES = 20L

    /** Límite de caracteres del mensaje del usuario. */
    const val LIMITE_CARACTERES = 5_000

    /**
     * Presupuesto de salida: los modelos con pensamiento interno consumen parte
     * del límite antes de escribir la respuesta, por eso no se ajusta a la baja.
     */
    const val MAX_TOKENS_RESPUESTA = 4_096

    const val INSTRUCCION = "Eres la mentora de programación de CodeRole, una compañera amable con " +
        "personalidad cálida y expresiva. Respondes en español, explicas conceptos con claridad y " +
        "ejemplos pequeños, guías paso a paso y adaptas la profundidad al nivel del usuario. " +
        "Mantén el foco en aprendizaje y desarrollo de software. No afirmes haber ejecutado código " +
        "ni acceder a archivos del usuario."
}
