package com.tecsup.mibodega.ui.cliente.navegacion

/**
 * Catálogo de rutas de la app cliente.
 * Centralizarlas aquí evita que "inicio" o "detalle/{productoId}"
 * se escriban distinto en pantallas distintas.
 */
object Rutas {
    const val LOGIN = "login"
    const val CREAR_CUENTA = "crear_cuenta"
    const val INICIO = "inicio"
    const val DETALLE = "detalle/{productoId}"
    const val CARRITO = "carrito"
    const val ENTREGA = "datos_entrega"
    const val CONFIRMACION = "confirmacion/{total}"

    /** Ruta con parámetro: detalle de un producto por su id. */
    fun detalle(productoId: Int) = "detalle/$productoId"

    /** Ruta con parámetro: confirmación que muestra el total del pedido. */
    fun confirmacion(total: Double) = "confirmacion/${total.toFloat()}"
}
