package com.quispe.cafeCampus.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.quispe.cafeCampus.data.local.entity.DetallePedido
import com.quispe.cafeCampus.data.local.entity.Pedido
import com.quispe.cafeCampus.data.local.entity.Usuario

data class PedidoConDetalle(
    @Embedded
    val pedido: Pedido,
    @Relation(parentColumn = "id", entityColumn = "pedidoId")
    val detalles: List<DetallePedido>,
    @Relation(parentColumn = "clienteId", entityColumn = "id")
    val cliente: Usuario
) {
    val total: Double
        get() = pedido.total

    fun resumen(): String =
        detalles.joinToString(", ") { "${it.cantidad} x ${it.nombreProducto}" }
}
