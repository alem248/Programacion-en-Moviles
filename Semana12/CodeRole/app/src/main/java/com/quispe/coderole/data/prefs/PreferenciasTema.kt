package com.quispe.coderole.data.prefs

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Preferencia del modo oscuro. `null` significa que la app sigue el tema del
 * sistema hasta que el usuario elige una opción en el perfil.
 */
class PreferenciasTema(contexto: Context) {
    private val preferencias: SharedPreferences =
        contexto.getSharedPreferences(NOMBRE, Context.MODE_PRIVATE)

    private val _oscuro = MutableStateFlow(
        if (preferencias.contains(CLAVE)) preferencias.getBoolean(CLAVE, false) else null,
    )
    val oscuro: StateFlow<Boolean?> = _oscuro.asStateFlow()

    fun establecerOscuro(valor: Boolean) {
        preferencias.edit().putBoolean(CLAVE, valor).apply()
        _oscuro.value = valor
    }

    private companion object {
        const val NOMBRE = "coderole_tema"
        const val CLAVE = "modo_oscuro"
    }
}
