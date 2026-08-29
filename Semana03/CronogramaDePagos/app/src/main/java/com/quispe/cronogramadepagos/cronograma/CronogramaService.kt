package com.quispe.cronogramadepagos.cronograma

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.format.DateTimeFormatter
class CronogramaService {

    companion object {
        @RequiresApi(Build.VERSION_CODES.O)
        private val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun imprimirCronograma(prestamo: Prestamo) {
        val tasa = prestamo.obtenerTasaInteres()
        val total = prestamo.calcularMontoTotalConInteres()
        val pagoMensual = prestamo.calcularPagoMensual()
        val interesTotal = prestamo.calcularInteresTotal()
        val cronograma = prestamo.generarCronograma()

        println()
        println("=".repeat(90))
        println("              CRONOGRAMA DE PAGOS - SISTEMA DE PRESTAMOS")
        println("=".repeat(90))
        println(String.format(" Cliente               : %s", prestamo.getNombreCliente()))
        println(String.format(" Monto prestado        : S/ %10.2f", prestamo.getMontoPrestado()))
        println(String.format(" Numero de cuotas      : %d", prestamo.getNumeroCuotas()))
        println(String.format(" Tasa de interes       : %d%%", prestamo.obtenerPorcentajeInteres()))
        if (tasa == 0.0) {
            println("   -> Advertencia: Nro. de cuotas no es 6, 12 ni 40. Se aplica 0% de interes.")
        }
        println(String.format(" Interes total         : S/ %10.2f", interesTotal))
        println(String.format(" Monto total a pagar   : S/ %10.2f", total))
        println(String.format(" Pago mensual          : S/ %10.2f", pagoMensual))
        println(String.format(" Primera fecha de pago : %s", prestamo.getFechaPrimerPago().format(dateFormatter)))
        println("=".repeat(90))

        // Encabezado de tabla con las 5 columnas solicitadas:
        // 1) N. de cuota  2) Fecha de pago  3) Monto a pagar  4) Pago mensual  5) Monto restante
        val linea = "-".repeat(90)
        println(linea)
        println(
            String.format(
                " %-4s | %-12s | %14s | %14s | %14s ",
                "N. Cuota", "Fecha Pago", "Monto a Pagar", "Pago Mensual", "Monto Restante"
            )
        )
        println(linea)

        for (cuota in cronograma) {
            println(
                String.format(
                    " %-4d | %-12s | S/ %10.2f | S/ %10.2f | S/ %10.2f ",
                    cuota.numeroCuota,
                    cuota.fechaFormateada(),
                    cuota.deudaActual,
                    cuota.pagoMensual,
                    cuota.deudaRestante
                )
            )
        }
        println(linea)
        println(String.format(" Total pagado al finalizar: S/ %.2f en %d cuotas", total, prestamo.getNumeroCuotas()))
        println("=".repeat(90))
        println(" ¡Gracias por usar el sistema, ${prestamo.getNombreCliente()}!")
        println()
    }
}


