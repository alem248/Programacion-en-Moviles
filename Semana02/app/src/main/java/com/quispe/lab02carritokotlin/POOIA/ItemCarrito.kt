package com.quispe.lab02carritokotlin.POOIA

/**
 * Representa una línea del carrito: un producto y la cantidad elegida.
 * ENCAPSULAMIENTO: cantidad solo puede crecer mediante agregarCantidad(),
 * que valida que el valor sea positivo. Nadie puede asignarla directamente.
 */
class ItemCarrito(
    val producto: Producto,
    cantidad: Int
) {
    var cantidad: Int = cantidad
        private set

    fun agregarCantidad(adicional: Int) {
        if (adicional > 0) {
            cantidad += adicional
        }
    }

    /**
     * Cada ítem sabe calcular su propio importe
     * (la lógica vive junto a los datos: principio básico de la POO).
     */
    fun calcularImporte(): Double {
        return producto.precio * cantidad
    }
}
