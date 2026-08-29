package com.quispe.cronogramadepagos.cronograma

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Cuota(
    val numeroCuota: Int,
    val fechaPago: LocalDate,
    val deudaActual: Double,
    val pagoMensual: Double,
    val deudaRestante: Double
) {
    @RequiresApi(Build.VERSION_CODES.O)
    fun fechaFormateada(): String = fechaPago.format(formatter)

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }
}