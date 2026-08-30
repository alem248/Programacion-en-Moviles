package com.quispe.cronogramadepagos.cronograma

import java.time.LocalDate
import java.time.format.DateTimeFormatter

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