package com.quispe.cafeCampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.quispe.cafeCampus.data.local.entity.DetallePedido
import com.quispe.cafeCampus.data.local.entity.Pedido
import com.quispe.cafeCampus.data.local.relation.PedidoConDetalle
import kotlinx.coroutines.flow.Flow

/** Fila del reporte: producto más vendido del día. */
data class FilaMasVendido(
    val nombre: String,
    val cantidadTotal: Int
)

/** Fila del reporte: total vendido por forma de pago. */
data class FilaTotalPorPago(
    val pago: String,
    val total: Double
)

@Dao
interface PedidoDao {

    @Insert
    suspend fun insertarPedido(p: Pedido): Long

    @Insert
    suspend fun insertarDetalles(lista: List<DetallePedido>)

    @Query("UPDATE pedidos SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Int, estado: String)

    @Transaction
    suspend fun crearPedido(pedido: Pedido, detalles: List<DetallePedido>): Long {
        val id = insertarPedido(pedido)
        insertarDetalles(detalles.map { it.copy(pedidoId = id.toInt()) })
        return id
    }

    @Transaction
    @Query("SELECT * FROM pedidos WHERE clienteId = :clienteId ORDER BY fecha DESC")
    fun observarPorCliente(clienteId: Int): Flow<List<PedidoConDetalle>>

    @Transaction
    @Query("SELECT * FROM pedidos WHERE estado = :estado ORDER BY fecha DESC")
    fun observarPorEstado(estado: String): Flow<List<PedidoConDetalle>>

    @Transaction
    @Query("SELECT * FROM pedidos WHERE id = :id")
    fun observarUno(id: Int): Flow<PedidoConDetalle?>

    @Query("SELECT COALESCE(SUM(total), 0.0) FROM pedidos WHERE fecha >= :inicio AND estado != 'CANCELADO'")
    fun ventasDesde(inicio: Long): Flow<Double>

    @Query("SELECT COUNT(*) FROM pedidos WHERE estado = :estado")
    fun cantidadPorEstado(estado: String): Flow<Int>

    @Query(
        "SELECT dp.nombreProducto AS nombre, SUM(dp.cantidad) AS cantidadTotal " +
            "FROM detalle_pedido dp " +
            "JOIN pedidos p ON p.id = dp.pedidoId " +
            "WHERE p.fecha >= :inicio AND p.estado != 'CANCELADO' " +
            "GROUP BY dp.nombreProducto " +
            "ORDER BY cantidadTotal DESC " +
            "LIMIT 1"
    )
    fun masVendido(inicio: Long): Flow<List<FilaMasVendido>>

    @Query(
        "SELECT pago, SUM(total) AS total " +
            "FROM pedidos " +
            "WHERE fecha >= :inicio AND estado != 'CANCELADO' " +
            "GROUP BY pago " +
            "ORDER BY total DESC"
    )
    fun totalesPorPago(inicio: Long): Flow<List<FilaTotalPorPago>>
}
