package com.quispe.cronograma

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun main() {
    println("=========================================")
    println("   SISTEMA DE CRONOGRAMA DE PAGOS")
    println("=========================================")
    println()

    val nombre = leerNombre()
    val monto = leerMonto()
    val cuotas = leerNumeroCuotas()
    val fechaPrimerPago = leerFechaPrimerPago()

    val prestamo = Prestamo(
        nombreCliente = nombre,
        montoPrestado = monto,
        numeroCuotas = cuotas,
        fechaPrimerPago = fechaPrimerPago
    )

    val service = CronogramaService()
    service.imprimirCronograma(prestamo)
}

// ---------- Funciones de lectura segura ----------

private fun leerNombre(): String {
    while (true) {
        print("Ingrese su nombre: ")
        val input = readlnOrNull()?.trim()
        if (!input.isNullOrBlank()) return input
        println(" -> El nombre no puede estar vacio. Intente de nuevo.")
    }
}

private fun leerMonto(): Double {
    while (true) {
        print("Ingrese la cantidad a prestar (ej. 5000): S/ ")
        val input = readlnOrNull()?.trim()
        val valor = input?.toDoubleOrNull()
        if (valor != null && valor > 0) return valor
        println(" -> Monto invalido. Debe ser un numero mayor a 0.")
    }
}

private fun leerNumeroCuotas(): Int {
    println()
    println(" Opciones de cuotas e interes:")
    println("  -  6 cuotas  -> 20% interes")
    println("  - 12 cuotas  -> 40% interes")
    println("  - 40 cuotas  -> 60% interes")
    while (true) {
        print("Ingrese el numero de cuotas (6, 12 o 40): ")
        val input = readlnOrNull()?.trim()
        val valor = input?.toIntOrNull()
        if (valor != null && valor > 0) {
            if (valor != 6 && valor != 12 && valor != 40) {
                println(" -> Advertencia: solo 6, 12 y 40 tienen interes definido (otros = 0%). ¿Desea continuar con $valor cuotas? (s/n): ")
                val conf = readlnOrNull()?.trim()?.lowercase()
                if (conf == "s" || conf == "si" || conf == "sí") return valor
                else continue
            }
            return valor
        }
        println(" -> Numero de cuotas invalido. Ingrese 6, 12 o 40.")
    }
}

private fun leerFechaPrimerPago(): LocalDate {
    val formatters = listOf(
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    )
    while (true) {
        print("Ingrese la primera fecha de pago (dd/MM/yyyy, ej. 15/09/2026): ")
        val input = readlnOrNull()?.trim() ?: ""
        for (fmt in formatters) {
            try {
                return LocalDate.parse(input, fmt)
            } catch (_: DateTimeParseException) {
                // probar siguiente formato
            }
        }
        println(" -> Fecha invalida. Use formato dd/MM/yyyy (ej. 15/09/2026).")
    }
}
