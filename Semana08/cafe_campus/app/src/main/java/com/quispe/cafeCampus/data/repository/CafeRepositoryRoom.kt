package com.quispe.cafeCampus.data.repository

import com.quispe.cafeCampus.data.local.CafeDatabase
import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.data.local.entity.Usuario
import com.quispe.cafeCampus.data.local.relation.PedidoConDetalle
import com.quispe.cafeCampus.model.EstadoPedido
import com.quispe.cafeCampus.model.ItemCarrito
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class CafeRepositoryRoom(private val db: CafeDatabase) : CafeRepository {

    private val productoDao = db.productoDao()

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

    // TODO (Paso 4): implementar login, crearPedido, observar*, cambiarEstado, panel
    override suspend fun login(correo: String, clave: String): Usuario? = null

    override suspend fun crearPedido(
        clienteId: Int,
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ): Long = 0L

    override fun observarPedidosCliente(clienteId: Int): Flow<List<PedidoConDetalle>> = flowOf(emptyList())

    override fun observarPedidosPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>> = flowOf(emptyList())

    override fun observarPedido(id: Int): Flow<PedidoConDetalle?> = flowOf(null)

    override suspend fun cambiarEstado(pedidoId: Int, estado: EstadoPedido) {}

    override fun observarVentasHoy(inicioDia: Long): Flow<Double> = flowOf(0.0)

    override fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int> = flowOf(0)
}
