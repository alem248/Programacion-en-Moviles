#!/usr/bin/env kotlin

import java.util.Scanner

data class Producto(
    val nombre: String,
    val precio: Double,
    var cantidad: Int
)

fun calcularSubtotal(productos: List<Producto>):Double {
    var subtotal = 0.0
    for (p in productos) {
        subtotal += p.precio * p.cantidad
    }
    return subtotal
}

fun calcularIGV(subtotal: Double): Double {
    return subtotal * 0.18
}

fun calcularTotal(subtotal:Double, igv: Double): Double {
    return subtotal + igv
}
fun mostrarDetalle(producto: List<Producto>){
    println("DETALLE DEL CARRITO")
    var i = 1
    for (p in productos) {
        val importe = p.precio * p.cantidad
        println(String.format("%d. %-20s x%d S/ %8.2f", i, p.nombre, p.cantidad, importe))
        i++
    }
}

fun buscar producto(productos List<Producto>, nombre: String): Producto? {
    return productos.find{it.nombre.equals, ignoreCase = true }
}

fun calcularDescuento(total: Double):Doble {
    return when{
        total > 5000 -> total * 0.10;
        total > 3000 -> total * 0.05
        else -> 0.0
    }
}

fun main(){
val scanner = Scanner(System. `in`)

    println("=========================================")
    println("   CARRITO DE COMPRAS - TIENDA TECSUP    ")
    println("=========================================")

    print("Ingrese su nombre")
    val nombreCLiente = scanner.nextLine().ifBlank{"Cliente Tecsup"}
    println()

    val productosDisponibles = listOf(
        Producto("Laptop HP", 2500.0, 1),
        Producto("Mouse Logitech", 45.5, 1),
        Producto("Audifonos Sony", 120.0, 1),
        Producto("USB Kingston 64GB", 25.0, 1),
        Producto("Teclado Mecanico", 180.0, 1)
    )

    val carrito = mutableListOf<Producto>()
    var opcion: Int

    do {
        println("--- PRODUCTOS DISPONIBLES ---")
        for (i in productosDisponibles.indices) {
            val prod = productosDisponibles[i]
            println("${i + 1}. ${prod.nombre} - S/ ${prod.precio}")
        }
        println("0. Finalizar compra y generar recibo")
        print("Seleccione una opción: ")

        opcion = scanner.nextInt()

        if (opcion in 1..productosDisponibles.size) {
            val prodSeleccionado = productosDisponibles[opcion - 1]

            print("Ingrese la cantidad para ${prodSeleccionado.nombre}: ")
            val cantidad = scanner.nextInt()

            if (cantidad >0){
                val existe = carrito.find { it.nombre == prodSeleccionado.nombre }
                if (existe != null) {
                    existe.cantidad += cantidad
                } else {
                    carrito.add(Producto(prodSeleccionado.nombre, prodSeleccionado.precio, cantidad))
                }
                println("-> Producto agregado al carrito.\n")
            } else {
                println("-> Cantidad no válida.\n")
            }
        } else if (opcion != 0) {
            println("-> Opción no válida. Intente de nuevo.\n")
        }

    } while (opcion != 0

        if (carrito.isEmpty()){
            println("\nEl carrito está vacío. ¡Gracias por su visita, $nombreCliente!")
            return
            }



}
