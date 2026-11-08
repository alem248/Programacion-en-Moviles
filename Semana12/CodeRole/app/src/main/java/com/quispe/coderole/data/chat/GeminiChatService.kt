package com.quispe.coderole.data.chat

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

/** Un turno de la conversación: mensaje del usuario o respuesta de la mentora. */
data class TurnoChat(val esUsuario: Boolean, val texto: String)

/**
 * Cliente mínimo de la Google Generative Language API (REST) para generar la
 * respuesta de la mentora con Gemini, sin depender de Cloud Functions.
 */
class GeminiChatService(
    private val apiKey: String = GeminiConfig.API_KEY,
    private val modelos: List<String> = GeminiConfig.MODELOS,
) {
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Genera la respuesta de la mentora a partir del historial previo y el
     * mensaje reciente. Lanza [IOException] o [IllegalStateException] con un
     * mensaje apto para mostrarse en la pantalla del chat.
     */
    suspend fun responder(historial: List<TurnoChat>, mensaje: String): String =
        withContext(Dispatchers.IO) {
            val cuerpo = construirCuerpo(normalizar(historial + TurnoChat(esUsuario = true, texto = mensaje)))
            var ultimoError: Exception? = null

            modelos.forEach { modelo ->
                val (codigo, cuerpoRespuesta) = llamar(modelo, cuerpo)
                if (codigo == 200) {
                    val respuesta = extraerRespuesta(cuerpoRespuesta)
                    if (respuesta.isNotBlank()) return@withContext respuesta
                    ultimoError = IllegalStateException("La mentora devolvió una respuesta vacía.")
                } else {
                    val error = exceptionPara(codigo, cuerpoRespuesta)
                    ultimoError = error
                    android.util.Log.w(TAG, "Gemini falló en $modelo ($codigo): $cuerpoRespuesta")
                    // Solo tiene sentido reintentar con otro modelo ante agotamiento o modelo no disponible.
                    if (codigo != 404 && codigo != 429 && codigo != 503 && codigo != 500) throw error
                }
            }
            throw ultimoError ?: IllegalStateException("No se pudo generar una respuesta ahora. Inténtalo de nuevo.")
        }

    private fun llamar(modelo: String, cuerpo: String): Pair<Int, String> {
        val peticion = Request.Builder()
            .url("${GeminiConfig.BASE_URL}$modelo:generateContent")
            .addHeader("x-goog-api-key", apiKey)
            .post(cuerpo.toRequestBody(jsonMediaType))
            .build()

        cliente.newCall(peticion).execute().use { respuesta ->
            val texto = respuesta.body?.string().orEmpty()
            return respuesta.code to texto
        }
    }

    private fun construirCuerpo(turnos: List<TurnoChat>): String {
        val raiz = JsonObject()
        raiz.add(
            "systemInstruction",
            JsonObject().apply {
                add("parts", JsonArray().apply { add(JsonObject().apply { addProperty("text", GeminiConfig.INSTRUCCION) }) })
            },
        )
        raiz.add(
            "contents",
            JsonArray().apply {
                turnos.forEach { turno ->
                    add(
                        JsonObject().apply {
                            addProperty("role", if (turno.esUsuario) "user" else "model")
                            add("parts", JsonArray().apply { add(JsonObject().apply { addProperty("text", turno.texto) }) })
                        },
                    )
                }
            },
        )
        raiz.add(
            "generationConfig",
            JsonObject().apply {
                addProperty("maxOutputTokens", GeminiConfig.MAX_TOKENS_RESPUESTA)
                addProperty("temperature", 0.8)
            },
        )
        return raiz.toString()
    }

    /**
     * Gemini exige que la conversación empiece con el usuario y prefiere turnos
     * alternados: descarta el inicio con la mentora y fusiona turnos repetidos.
     */
    private fun normalizar(turnos: List<TurnoChat>): List<TurnoChat> {
        val filtrados = turnos.filter { it.texto.isNotBlank() }.dropWhile { !it.esUsuario }
        val combinados = mutableListOf<TurnoChat>()
        filtrados.forEach { turno ->
            val anterior = combinados.lastOrNull()
            if (anterior != null && anterior.esUsuario == turno.esUsuario) {
                combinados[combinados.lastIndex] = anterior.copy(texto = "${anterior.texto}\n${turno.texto}")
            } else {
                combinados.add(turno)
            }
        }
        return combinados
    }

    private fun extraerRespuesta(cuerpo: String): String {
        return runCatching {
            val raiz = JsonParser.parseString(cuerpo).asJsonObject
            val candidatos = raiz.getAsJsonArray("candidates") ?: return ""
            candidatos.joinToString(separator = "\n") { candidato ->
                val contenido = candidato.asJsonObject.get("content")
                if (contenido == null || !contenido.isJsonObject) return@joinToString ""
                val partes = contenido.asJsonObject.get("parts") ?: return@joinToString ""
                if (!partes.isJsonArray) return@joinToString ""
                partes.asJsonArray.mapNotNull { parte ->
                    if (!parte.isJsonObject) return@mapNotNull null
                    parte.asJsonObject.get("text")?.takeIf { !it.isJsonNull }?.asString
                }.joinToString(separator = "")
            }.trim()
        }.getOrDefault("")
    }

    private fun exceptionPara(codigo: Int, cuerpo: String): Exception = when (codigo) {
        400 -> IllegalStateException("Revisa el mensaje: la API de Gemini rechazó la solicitud.")
        401, 403 -> IllegalStateException("La clave de Gemini no tiene permisos. Contacta con el docente.")
        429 -> IllegalStateException("Se agotó la cuota de Gemini. Espera unos minutos y vuelve a intentar.")
        503, 500 -> IllegalStateException("Gemini está con alta demanda ahora. Inténtalo de nuevo en un momento.")
        else -> IOException("No se pudo conectar con Gemini (código $codigo). Comprueba tu conexión.")
    }

    private companion object {
        const val TAG = "GeminiChat"
    }
}
