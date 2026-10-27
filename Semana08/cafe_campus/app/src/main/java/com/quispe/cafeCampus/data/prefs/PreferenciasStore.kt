package com.quispe.cafeCampus.data.prefs

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.quispe.cafeCampus.BuildConfig
import com.quispe.cafeCampus.model.Rol
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "ajustes")

class PreferenciasStore(private val context: Context) {

    private object Llaves {
        val PERFIL_ACTIVO = stringPreferencesKey("perfilActivo")
        val CORREO_ACTIVO = stringPreferencesKey("correoActivo")
        val MODO_OSCURO = booleanPreferencesKey("modoOscuro")
        val RECIBIR_PEDIDOS = booleanPreferencesKey("recibirPedidos")
        val PAGO_QR = stringPreferencesKey("pagoQrPayload")
    }

    val perfilActivo: Flow<String> = context.dataStore.data.map { it[Llaves.PERFIL_ACTIVO] ?: "" }

    /** Correo del usuario logueado: identifica al usuario exacto (no solo al rol). */
    val correoActivo: Flow<String> = context.dataStore.data.map { it[Llaves.CORREO_ACTIVO] ?: "" }

    val modoOscuro: Flow<Boolean> = context.dataStore.data.map { it[Llaves.MODO_OSCURO] ?: false }

    val recibirPedidos: Flow<Boolean> = context.dataStore.data.map { it[Llaves.RECIBIR_PEDIDOS] ?: true }

    /**
     * Payload base del QR de pago (cuenta destino Yape/Plin) configurado por el admin.
     * Mientras el admin no guarde uno, se usa el de local.properties (fuera de Git);
     * si lo desactiva se guarda "" y ya no vuelve el de por defecto.
     */
    val payloadQr: Flow<String> =
        context.dataStore.data.map { it[Llaves.PAGO_QR] ?: BuildConfig.QR_PAGO_DEFECTO }

    suspend fun guardarPerfil(rol: Rol, correo: String) {
        context.dataStore.edit {
            it[Llaves.PERFIL_ACTIVO] = rol.name
            it[Llaves.CORREO_ACTIVO] = correo
        }
    }

    suspend fun guardarModoOscuro(valor: Boolean) {
        context.dataStore.edit { it[Llaves.MODO_OSCURO] = valor }
    }

    suspend fun guardarRecibirPedidos(valor: Boolean) {
        context.dataStore.edit { it[Llaves.RECIBIR_PEDIDOS] = valor }
    }

    suspend fun guardarPayloadQr(payload: String) {
        context.dataStore.edit { it[Llaves.PAGO_QR] = payload }
    }

    suspend fun cerrarSesion() {
        context.dataStore.edit {
            it[Llaves.PERFIL_ACTIVO] = ""
            it[Llaves.CORREO_ACTIVO] = ""
        }
    }
}
