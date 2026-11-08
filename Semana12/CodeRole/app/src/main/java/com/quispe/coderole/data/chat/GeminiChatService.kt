package com.quispe.coderole.data.chat

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/** Un turno de la conversación: mensaje del usuario o respuesta de la mentora. */
data class TurnoChat(val esUsuario: Boolean, val texto: String)

/**
 * Cliente mínimo de la Google Generative Language API (REST) para generar la
 * respuesta de la mentora con Gemini, sin depender de Cloud Functions.
 *
 * La llamada usa `streamGenerateContent` (SSE): cada fragmento de texto se
 * emite en el [Flow] en cuanto llega, de modo que la pantalla puede pintar la
 * respuesta progresivamente en lugar de esperar a que termine la generación.
 * OkHttp trabaja con `enqueue`, así que ningún hilo del pool queda bloqueado
 * esperando la red y la cancelación de la corrutina corta la llamada.
 */
class GeminiChatService(
    private val apiKey: String = GeminiConfig.API_KEY,
    private val modelos: List<String> = GeminiConfig.MODELOS,
) {
    private val cliente = OkHttpClient.Builder()
        .connectTimeout(CONEXION_SEGUNDOS, TimeUnit.SECONDS)
        .readTimeout(LECTURA_SEGUNDOS, TimeUnit.SECONDS)
        .writeTimeout(ESCRITURA_SEGUNDOS, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    /**
     * Genera la respuesta de la mentora en streaming a partir del historial y
     * el mensaje reciente (que deben incluir ya el turno del usuario).
     * Emite cada fragmento conforme llega y lanza [IOException] o
     * [IllegalStateException] con un mensaje apto para la pantalla del chat.
     */
    fun fluir(turnos: List<TurnoChat>): Flow<String> = flow {
        if (apiKey.isBlank()) {
            throw IllegalStateException("Falta GEMINI_API_KEY en local.properties. Sin esa clave no se puede contactar con Gemini.")
        }
        val cuerpo = construirCuerpo(normalizar(turnos))
        var ultimoError: Exception? = null

        for (modelo in modelos) {
            val respuesta = try {
                abrir(modelo, cuerpo)
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                // Fallo de red antes de recibir respuesta: se prueba el siguiente modelo.
                ultimoError = error
                continue
            }

            var falloNoRecuperable: Exception? = null
            var emitidos = 0
            try {
                if (respuesta.code != 200) {
                    val cuerpoError = respuesta.body?.string().orEmpty()
                    android.util.Log.w(TAG, "Gemini falló en $modelo (${respuesta.code}): $cuerpoError")
                    val error = exceptionPara(respuesta.code, cuerpoError)
                    if (reintentable(respuesta.code)) ultimoError = error else falloNoRecuperable = error
                } else {
                    val cuerpoRespuesta = respuesta.body
                        ?: throw IOException("Gemini no devolvió contenido.")
                    val parciales = cuerpoRespuesta.source()
                    while (true) {
                        currentCoroutineContext().ensureActive()
                        val linea = parciales.readUtf8Line() ?: break
                        if (!linea.startsWith(PREFIJO_DATOS)) continue
                        val datos = linea.removePrefix(PREFIJO_DATOS).trim()
                        if (datos == FIN_STREAM) break
                        val parcial = extraerParcial(datos)
                        if (parcial == Bloqueo.SOLICITUD_BLOQUEADA) {
                            falloNoRecuperable = IllegalStateException(
                                "La mentora no pudo responder a ese mensaje. Prueba a reformularlo.",
                            )
                            break
                        }
                        if (parcial is Bloqueo.Texto && parcial.valor.isNotEmpty()) {
                            emitidos++
                            emit(parcial.valor)
                        }
                    }
                    if (falloNoRecuperable == null && emitidos == 0) {
                        ultimoError = IllegalStateException("La mentora devolvió una respuesta vacía.")
                    }
                }
            } catch (cancelacion: CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                // La conexión se cortó a mitad de la lectura: si ya se mostró
                // texto parcial no se reintenta, porque duplicaría la respuesta.
                if (emitidos > 0 || falloNoRecuperable != null) falloNoRecuperable = falloNoRecuperable ?: error
                else ultimoError = error
            } finally {
                respuesta.close()
            }

            falloNoRecuperable?.let { throw it }
            if (emitidos > 0) return@flow
        }
        throw ultimoError ?: IllegalStateException("No se pudo generar una respuesta ahora. Inténtalo de nuevo.")
    }.flowOn(Dispatchers.IO)

    /**
     * Abre la llamada de forma asíncrona: la corrutina espera sin ocupar ningún
     * hilo y se cancela (cortando la petición) si la pantalla deja de pedirla.
     */
    private suspend fun abrir(modelo: String, cuerpo: String): Response {
        val peticion = Request.Builder()
            .url("${GeminiConfig.BASE_URL}$modelo:$STREAM_ENDPOINT")
            .addHeader("x-goog-api-key", apiKey)
            .post(cuerpo.toRequestBody(jsonMediaType))
            .build()

        return suspendCancellableCoroutine { continuacion ->
            val llamada = cliente.newCall(peticion)
            continuacion.invokeOnCancellation { llamada.cancel() }
            llamada.enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    if (continuacion.isActive) continuacion.resumeWithException(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if (continuacion.isActive) continuacion.resume(response) else response.close()
                }
            })
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

    /** Interpreta un evento SSE de `streamGenerateContent`. */
    private fun extraerParcial(datos: String): Bloqueo {
        return runCatching {
            val raiz = JsonParser.parseString(datos).asJsonObject
            val bloqueo = raiz.getAsJsonObject("promptFeedback")?.get("blockReason")
            if (bloqueo != null && !bloqueo.isJsonNull && bloqueo.asString.isNotBlank()) {
                return Bloqueo.SOLICITUD_BLOQUEADA
            }
            val candidatos = raiz.getAsJsonArray("candidates") ?: return Bloqueo.SIN_TEXTO
            if (candidatos.size() == 0) return Bloqueo.SIN_TEXTO
            val contenido = candidatos[0].asJsonObject.get("content") ?: return Bloqueo.SIN_TEXTO
            if (!contenido.isJsonObject) return Bloqueo.SIN_TEXTO
            val partes = contenido.asJsonObject.get("parts") ?: return Bloqueo.SIN_TEXTO
            if (!partes.isJsonArray) return Bloqueo.SIN_TEXTO
            val texto = partes.asJsonArray.mapNotNull { parte ->
                if (!parte.isJsonObject) return@mapNotNull null
                parte.asJsonObject.get("text")?.takeIf { !it.isJsonNull }?.asString
            }.joinToString(separator = "")
            Bloqueo.Texto(texto)
        }.getOrDefault(Bloqueo.SIN_TEXTO)
    }

    /** Solo los códigos de agotamiento o modelo no disponible pasan al siguiente modelo. */
    private fun reintentable(codigo: Int) = codigo in CODIGOS_RECUPERABLES

    private fun exceptionPara(codigo: Int, cuerpo: String): Exception = when (codigo) {
        400 -> IllegalStateException("Gemini rechazó la solicitud. Revisa la respuesta de error del registro GeminiChat.")
        401, 403 -> IllegalStateException("La clave de Gemini no tiene permisos. Contacta con el docente.")
        429 -> IllegalStateException("Se agotó la cuota de Gemini. Espera unos minutos y vuelve a intentar.")
        500, 502, 503, 504 -> IllegalStateException("Gemini está con alta demanda ahora. Inténtalo de nuevo en un momento.")
        404 -> IllegalStateException("El modelo de Gemini no está disponible para esta clave o región.")
        else -> IOException("No se pudo conectar con Gemini (código $codigo). Comprueba tu conexión.")
    }

    private sealed interface Bloqueo {
        data class Texto(val valor: String) : Bloqueo
        data object SIN_TEXTO : Bloqueo
        data object SOLICITUD_BLOQUEADA : Bloqueo
    }

    private companion object {
        const val TAG = "GeminiChat"
        const val PREFIJO_DATOS = "data:"
        const val FIN_STREAM = "[DONE]"
        const val STREAM_ENDPOINT = "streamGenerateContent?alt=sse"
        const val CONEXION_SEGUNDOS = 10L
        const val LECTURA_SEGUNDOS = 45L
        const val ESCRITURA_SEGUNDOS = 10L
        val CODIGOS_RECUPERABLES = setOf(404, 429, 500, 502, 503, 504)
    }
}
