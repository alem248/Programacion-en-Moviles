package com.tecsup.mibodega.ui.cliente.modelo

/** Costo fijo de delivery mostrado en el carrito y usado al confirmar. */
const val COSTO_DELIVERY = 4.00

/**
 * Datos capturados en la pantalla de entrega. Se guardan en
 * AppNavegacion para que la confirmación pueda mostrar la dirección
 * del pedido sin volver a pedírsela al formulario.
 */
data class DatosEntrega(
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val referencia: String,
    val metodoPago: String
)

val listaMetodosPago = listOf("Efectivo al entregarse", "Yape", "Plin")
