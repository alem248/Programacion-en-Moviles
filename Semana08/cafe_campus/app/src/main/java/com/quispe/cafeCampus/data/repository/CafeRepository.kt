package com.quispe.cafeCampus.data.repository

import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.data.local.entity.Usuario
import com.quispe.cafeCampus.data.local.relation.PedidoConDetalle
import com.quispe.cafeCampus.model.EstadoPedido
import com.quispe.cafeCampus.model.ItemCarrito
import com.quispe.cafeCampus.model.ProductoVendido
import com.quispe.cafeCampus.model.ResultadoRegistro
import com.quispe.cafeCampus.model.Rol
import com.quispe.cafeCampus.model.TotalPorPago
import kotlinx.coroutines.flow.Flow

interface CafeRepository {

    // Usuarios
    suspend fun login(correo: String, clave: String): Usuario?
    suspend fun usuarioPorRol(rol: Rol): Usuario?
    suspend fun usuarioPorCorreo(correo: String): Usuario?
    suspend fun registrar(nombre: String, correo: String, clave: String): ResultadoRegistro

    // Productos (ejemplo resuelto)
    fun observarProductos(): Flow<List<Producto>>
    suspend fun guardarProducto(p: Producto)
    suspend fun eliminarProducto(p: Producto)
    suspend fun cambiarDisponibilidad(id: Int, disponible: Boolean)

    // Pedidos
    suspend fun crearPedido(
        clienteId: Int,
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ): Long

    fun observarPedidosCliente(clienteId: Int): Flow<List<PedidoConDetalle>>
    fun observarPedidosPorEstado(estado: EstadoPedido): Flow<List<PedidoConDetalle>>
    fun observarPedido(id: Int): Flow<PedidoConDetalle?>
    suspend fun cambiarEstado(pedidoId: Int, estado: EstadoPedido)

    // Panel
    fun observarVentasHoy(inicioDia: Long): Flow<Double>
    fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int>

    // Reporte del día
    fun observarMasVendido(inicioDia: Long): Flow<ProductoVendido?>
    fun observarTotalesPorPago(inicioDia: Long): Flow<List<TotalPorPago>>
}
