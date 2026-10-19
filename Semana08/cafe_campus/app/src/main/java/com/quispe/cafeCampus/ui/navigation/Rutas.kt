package com.quispe.cafeCampus.ui.navigation

object Rutas {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val MENU = "menu"
    const val DETALLE = "detalle/{productoId}"
    const val CONFIRMAR = "confirmar"
    const val SEGUIMIENTO = "seguimiento/{pedidoId}"
    const val MIS_PEDIDOS = "mis_pedidos"
    const val PANEL = "panel"
    const val PEDIDOS_ADMIN = "pedidos_admin"
    const val DETALLE_PEDIDO_ADMIN = "detalle_pedido_admin/{pedidoId}"
    const val MENU_ADMIN = "menu_admin"
    const val FORMULARIO_PRODUCTO = "formulario_producto?productoId={productoId}"
    const val AJUSTES = "ajustes"

    fun detalle(productoId: Int) = "detalle/$productoId"
    fun seguimiento(pedidoId: Int) = "seguimiento/$pedidoId"
    fun detallePedidoAdmin(pedidoId: Int) = "detalle_pedido_admin/$pedidoId"
    fun formularioProducto(productoId: Int? = null) =
        if (productoId != null) "formulario_producto?productoId=$productoId" else "formulario_producto"
}
