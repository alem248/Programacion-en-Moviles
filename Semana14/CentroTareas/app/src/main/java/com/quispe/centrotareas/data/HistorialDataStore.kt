package com.quispe.centrotareas.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Propiedad de extensión a nivel de archivo: una única instancia DataStore por proceso
private val Context.historialDataStore by preferencesDataStore(name = "historial_resultados")

/**
 * Mejora 3: guarda los últimos [MAXIMO] resultados (primos y cargas) en DataStore.
 * El más reciente va primero y se conserva entre sesiones.
 */
class HistorialDataStore(private val contexto: Context) {

    private val claveResultados = stringPreferencesKey("resultados")

    val resultados: Flow<List<String>> = contexto.historialDataStore.data.map { preferencias ->
        preferencias[claveResultados]
            .orEmpty()
            .split(SEPARADOR)
            .filter { it.isNotBlank() }
    }

    suspend fun agregar(resultado: String) {
        try {
            contexto.historialDataStore.edit { preferencias ->
                val actuales = preferencias[claveResultados]
                    .orEmpty()
                    .split(SEPARADOR)
                    .filter { it.isNotBlank() }
                preferencias[claveResultados] =
                    (listOf(resultado) + actuales).take(MAXIMO).joinToString(SEPARADOR)
            }
        } catch (e: CancellationException) {
            throw e // la cancelación se relanza siempre
        } catch (_: Exception) {
            // El historial es secundario: un fallo de E/S de DataStore no interrumpe la tarea
        }
    }

    private companion object {
        const val SEPARADOR = "\n"
        const val MAXIMO = 5
    }
}
