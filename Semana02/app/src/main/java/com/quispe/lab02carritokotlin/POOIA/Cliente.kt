package com.quispe.lab02carritokotlin.POOIA

/**
 * Modela al cliente de la tienda.
 * ENCAPSULAMIENTO: el nombre se normaliza en el constructor; si llega
 * en blanco se usa un valor por defecto, igual que en el proyecto original.
 */
class Cliente(nombre: String) {

    val nombre: String = nombre.ifBlank { NOMBRE_POR_DEFECTO }

    fun saludar(): String = "¡Gracias por su compra, $nombre!"

    companion object {
        const val NOMBRE_POR_DEFECTO = "Cliente Tecsup"
    }
}
