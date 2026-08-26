package com.quispe.lab02carritokotlin.POOIA

/**
 * Responsable únicamente de "imprimir" el recibo de compra
 * (principio de responsabilidad única: el carrito calcula,
 * el recibo presenta).
 *
 * Recibe la calculadora de descuento por la INTERFAZ, no por la clase
 * concreta: se le puede inyectar cualquier otra estrategia de descuento
 * sin tocar esta clase (POLIMORFISMO).
 */
class Recibo(
    private val cliente: Cliente,
    private val carrito: Carrito,
    private val calculadoraDescuento: CalculadoraDescuento
) {

    fun imprimir() {
        println("\n=========================================")
        println("             RECIBO DE COMPRA            ")
        println("=========================================")
        println("Cliente: ${cliente.nombre}")

        imprimirDetalle()
        imprimirTotales()
        imprimirProductoMasCaro()
        imprimirDescuento()

        println("\n${cliente.saludar()}")
    }

    private fun imprimirDetalle() {
        println("DETALLE DEL CARRITO")
        var i = 1
        for (item in carrito.obtenerItems()) {
            println(
                String.format(
                    "%d. %-20s x%d S/ %8.2f",
                    i, item.producto.nombre, item.cantidad, item.calcularImporte()
                )
            )
            i++
        }
    }

    private fun imprimirTotales() {
        println(String.format("%-22s : %d", "Cantidad de productos", carrito.cantidadDeProductos()))
        println(String.format("%-22s : S/ %7.2f", "Subtotal", carrito.calcularSubtotal()))
        println(String.format("%-22s : S/ %7.2f", "IGV (18%)", carrito.calcularIGV()))
        println(String.format("%-22s : S/ %7.2f", "TOTAL A PAGAR", carrito.calcularTotal()))
    }

    private fun imprimirProductoMasCaro() {
        val masCaro = carrito.productoMasCaro()
        if (masCaro != null) {
            // Gracias al polimorfismo, descripcion() muestra la garantía
            // si el producto más caro es un ProductoElectronico.
            println("\nProducto mas caro: ${masCaro.nombre} " + String.format("(S/%.2f)", masCaro.precio))
        }
    }

    private fun imprimirDescuento() {
        val total = carrito.calcularTotal()
        val descuento = calculadoraDescuento.calcular(total)

        if (descuento > 0.0) {
            println(calculadoraDescuento.describir(total))
            println(String.format("%-22s : S/ %7.2f", "TOTAL CON DESCUENTO", total - descuento))
        }
    }
}
