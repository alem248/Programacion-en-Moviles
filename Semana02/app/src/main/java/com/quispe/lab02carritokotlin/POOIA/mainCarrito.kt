package com.quispe.lab02carritokotlin.POOIA

/**
 * Punto de entrada de la versión Orientada a Objetos del carrito.
 * Compare con el main() del proyecto original: aquí solo se crea el
 * objeto Tienda y se delega todo el trabajo en sus clases.
 */
fun main() {
    val tienda = Tienda()
    tienda.iniciar()
}
