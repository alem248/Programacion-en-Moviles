package com.quispe.cronograma

import java.time.LocalDate

/**
 * Clase principal del dominio que encapsula la lógica de negocio del préstamo.
 * Aplica principios POO: encapsulamiento, responsabilidad única.
 *
 * @param nombreCliente nombre del titular del préstamo
 * @param montoPrestado cantidad solicitada sin interés
 * @param numeroCuotas número de cuotas mensuales (6, 12 o 40 según regla de negocio)
 * @param fechaPrimerPago fecha de la primera cuota
 */
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

    /**
     * Determina la tasa de interés según el número de cuotas:
     * 6 cuotas -> 20%
     * 12 cuotas -> 40%
     * 40 cuotas -> 60%
     * Para otros valores se retorna 0 y se advierte al usuario (regla extensible)
     */
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

    /**
     * Genera el cronograma completo de pagos.
     * Cada cuota es mensual a partir de fechaPrimerPago.
     */
    fun generarCronograma(): List<Cuota> {
        val pagoMensual = calcularPagoMensual()
        val totalConInteres = calcularMontoTotalConInteres()
        val lista = mutableListOf<Cuota>()

        var deudaActual = totalConInteres

        for (i in 1..numeroCuotas) {
            val fechaPago = fechaPrimerPago.plusMonths((i - 1).toLong())
            val deudaRestante = (deudaActual - pagoMensual).let {
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
