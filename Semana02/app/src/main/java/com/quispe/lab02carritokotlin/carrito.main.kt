#!/usr/bin/env kotlin

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

fun