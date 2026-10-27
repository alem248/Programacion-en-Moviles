package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.pago.QrEmvCo
import com.quispe.cafeCampus.data.prefs.PreferenciasStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Gestiona el pago con QR (Yape/Plin):
 * - El admin configura el payload base de SU QR real (cuenta destino).
 * - La app genera un QR dinámico con el monto automático del pedido.
 */
class PagoViewModel(private val prefs: PreferenciasStore) : ViewModel() {

    private val _payloadBase = MutableStateFlow("")
    val payloadBase: StateFlow<String> = _payloadBase.asStateFlow()

    private val _mensaje = MutableStateFlow<String?>(null)
    val mensaje: StateFlow<String?> = _mensaje.asStateFlow()

    init {
        viewModelScope.launch {
            _payloadBase.value = prefs.payloadQr.first()
        }
    }

    /** Valida y guarda el payload que pega el administrador. */
    fun guardarPayload(texto: String) {
        viewModelScope.launch {
            val limpio = texto.trim()
            when {
                limpio.isEmpty() -> {
                    prefs.guardarPayloadQr("")
                    _payloadBase.value = ""
                    _mensaje.value = "QR de pagos desactivado"
                }
                QrEmvCo.construir(limpio, 1.0) == null -> {
                    _mensaje.value =
                        "Código no válido: pega el texto completo de tu QR de Yape/Plin (debe empezar con 000201)"
                }
                else -> {
                    prefs.guardarPayloadQr(limpio)
                    _payloadBase.value = limpio
                    _mensaje.value = "QR configurado correctamente"
                }
            }
        }
    }

    /** Payload final con el monto inyectado, o null si aún no hay QR configurado. */
    fun generarPayload(monto: Double, referencia: String = ""): String? {
        val base = _payloadBase.value
        if (base.isBlank()) return null
        return QrEmvCo.construir(base, monto, referencia)
    }

    fun limpiarMensaje() {
        _mensaje.value = null
    }
}
