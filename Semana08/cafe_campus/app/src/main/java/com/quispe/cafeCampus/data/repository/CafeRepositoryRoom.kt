package com.quispe.cafeCampus.data.repository

import com.quispe.cafeCampus.data.local.CafeDatabase
import com.quispe.cafeCampus.data.local.entity.DetallePedido
import com.quispe.cafeCampus.data.local.entity.Pedido
import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.data.local.entity.Usuario
import com.quispe.cafeCampus.data.local.relation.PedidoConDetalle
import com.quispe.cafeCampus.model.EstadoPedido
import com.quispe.cafeCampus.model.ItemCarrito
import kotlinx.coroutines.flow.Flow

class CafeRepositoryRoom(private val db: CafeDatabase) : CafeRepository {

    private val productoDao = db.productoDao()
    private val usuarioDao = db.usuarioDao()
    private val pedidoDao = db.pedidoDao()

    override suspend fun login(correo: String, clave: String): Usuario? =
        usuarioDao.login(correo, clave)

    override fun observarProductos(): Flow<List<Producto>> = productoDao.observarTodos()

    override suspend fun guardarProducto(p: Producto) {
        productoDao.insertar(p)
    }

    override suspend fun eliminarProducto(p: Producto) {
        productoDao.eliminar(p)
    }

    override suspend fun cambiarDisponibilidad(id: Int, disponible: Boolean) {
        productoDao.cambiarDisponibilidad(id, disponible)
    }

    override suspend fun crearPedido(
        clienteId: Int,
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ): Long {
        val detalles = items.map { item ->
            DetallePedido(
                pedidoId = 0,
                productoId = item.producto.id,
                nombreProducto = item.producto.nombre,
                cantidad = item.cantidad,
                precioUnitario = item.producto.precio
            )
        }
        val total = items.sumOf { it.subtotal }
        val pedido = Pedido(
            clienteId = clienteId,
            estado = EstadoPedido.RECIBIDO.name,
            horaRecojo = horaRecojo,
            pago = pago,
            nota = nota,
            total = total
        )
        return pedidoDao.crearPedido(pedido, detalles)
    }

    override fun observarPedidosCliente(clienteId: Int): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorCliente(clienteId)

    override fun observarPedidosPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>> =
        pedidoDao.observarPorEstado(estado.name)

    override fun observarPedido(id: Int): Flow<PedidoConDetalle?> =
        pedidoDao.observarUno(id)

    override suspend fun cambiarEstado(pedidoId: Int, estado: EstadoPedido) {
        pedidoDao.cambiarEstado(pedidoId, estado.name)
    }

    override fun observarVentasHoy(inicioDia: Long): Flow<Double> =
        pedidoDao.ventasDesde(inicioDia)

    override fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int> =
        pedidoDao.cantidadPorEstado(estado.name)
}
