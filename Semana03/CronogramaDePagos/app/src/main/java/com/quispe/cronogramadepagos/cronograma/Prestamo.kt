package com.quispe.cronogramadepagos.cronograma

import java.time.LocalDate


class Prestamo(
    private val nombreCliente: String,
    private val montoPrestado: Double,
    private val numeroCuotas: Int,
    private val fechaPrimerPago: LocalDate
) {
    init {
        require(montoPrestado > 0) { "El monto prestado debe ser mayor a 0" }
        require(numeroCuotas > 0) { "El número de cuotas debe ser mayor a 0" }
    }

    fun getNombreCliente(): String = nombreCliente
    fun getMontoPrestado(): Double = montoPrestado
    fun getNumeroCuotas(): Int = numeroCuotas
    fun getFechaPrimerPago(): LocalDate = fechaPrimerPago
    fun obtenerTasaInteres(): Double = when (numeroCuotas) {
        6 -> 0.20
        12 -> 0.40
        40 -> 0.60
        else -> 0.0
    }

    fun obtenerPorcentajeInteres(): Int = (obtenerTasaInteres() * 100).toInt()

    fun calcularMontoTotalConInteres(): Double {
        return montoPrestado * (1 + obtenerTasaInteres())
    }

    fun calcularInteresTotal(): Double {
        return calcularMontoTotalConInteres() - montoPrestado
    }

    fun calcularPagoMensual(): Double {
        return calcularMontoTotalConInteres() / numeroCuotas
    }

    fun generarCronograma(): List<Cuota> {
        val pagoMensual = calcularPagoMensual()
        val totalConInteres = calcularMontoTotalConInteres()
        val lista = mutableListOf<Cuota>()

        var deudaActual = totalConInteres

        for (i in 1..numeroCuotas) {
            val fechaPago = fechaPrimerPago.plusMonths((i - 1).toLong())
            val deudaRestante = (deudaActual - pagoMensual).coerceAtLeast(0.0).let {
                if (it < 0.005) 0.0 else it // evitar -0.00 por redondeo
            }
            lista.add(
                Cuota(
                    numeroCuota = i,
                    fechaPago = fechaPago,
                    deudaActual = deudaActual,
                    pagoMensual = pagoMensual,
                    deudaRestante = deudaRestante
                )
            )
            deudaActual = deudaRestante
        }
        return lista
    }
}
