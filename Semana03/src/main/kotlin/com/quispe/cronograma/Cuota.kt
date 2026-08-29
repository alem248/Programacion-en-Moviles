package com.quispe.cronograma

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Representa una cuota del cronograma de pagos.
 * @param numeroCuota N - número secuencial de la cuota
 * @param fechaPago fecha programada de pago (mensual)
 * @param deudaActual monto de deuda al inicio de la cuota (antes de pagar)
 * @param pagoMensual monto a pagar en esta cuota
 * @param deudaRestante deuda después de realizar el pago
 */
data class Cuota(
    val numeroCuota: Int,
    val fechaPago: LocalDate,
    val deudaActual: Double,
    val pagoMensual: Double,
    val deudaRestante: Double
) {
    fun fechaFormateada(): String = fechaPago.format(formatter)

    companion object {
        private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}
