package com.quispe.cronogramadepagos.cronograma

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.system.exitProcess

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

private fun leerLineaOSalir(prompt: String): String {
    print(prompt)
    val input = readlnOrNull()
    if (input == null) {
        println()
        println(" -> No se pudo leer la entrada (flujo de entrada cerrado / EOF).")
        println(" -> Si ejecuta esto desde Android Studio, active la opcion")
        println("    'Emulate terminal in output console' en la configuracion de ejecucion")
        println("    (Run/Debug Configurations) para poder escribir por teclado.")
        exitProcess(1)
    }
    return input.trim()
}

private fun leerNombre(): String {
    while (true) {
        val input = leerLineaOSalir("Ingrese su nombre: ")
        if (input.isNotBlank()) return input
        println(" -> El nombre no puede estar vacio. Intente de nuevo.")
    }
}

private fun leerMonto(): Double {
    while (true) {
        val input = leerLineaOSalir("Ingrese la cantidad a prestar (ej. 5000): S/ ")
        val valor = input.toDoubleOrNull()
        if (valor != null && valor > 0) return valor
        println(" -> Monto invalido. Debe ser un numero mayor a 0.")
    }
}

private fun leerNumeroCuotas(): Int {
    println()
    println(" Opciones de cuotas e interes:")
    println("  -  6 cuotas  -> 20% interes")
    println("  - 12 cuotas  -> 40% interes")
    println("  - 24 cuotas  -> 60% interes")
    while (true) {
        val input = leerLineaOSalir("Ingrese el numero de cuotas (6, 12 o 24): ")
        val valor = input.toIntOrNull()
        if (valor != null && valor > 0) {
            if (valor != 6 && valor != 12 && valor != 24) {
                println(" -> Advertencia: solo 6, 12 y 24 tienen interes definido (otros = 0%).")
                val conf = leerLineaOSalir(" ¿Desea continuar con $valor cuotas? (s/n): ").lowercase()
                if (conf == "s" || conf == "si" || conf == "sí") return valor
                else continue
            }
            return valor
        }
        println(" -> Numero de cuotas invalido. Ingrese 6, 12 o 24.")
    }
}

private fun leerFechaPrimerPago(): LocalDate {
    // Soporta 4 formatos comunes; se prueba en orden
    val formatters = listOf(
        DateTimeFormatter.ofPattern("dd/MM/yyyy"),
        DateTimeFormatter.ofPattern("d/M/yyyy"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("dd-MM-yyyy")
    )
    while (true) {
        val input = leerLineaOSalir("Ingrese la primera fecha de pago (dd/MM/yyyy, ej. 15/09/2026): ")
        for (fmt in formatters) {
            try {
                return LocalDate.parse(input.trim(), fmt)
            } catch (_: DateTimeParseException) {
                // probar siguiente formato
            }
        }
        println(" -> Fecha invalida. Use formato dd/MM/yyyy (ej. 15/09/2026).")
    }
}

