package com.quispe.cafeCampus.data.repository

import com.quispe.cafeCampus.data.local.CafeDatabase
import com.quispe.cafeCampus.data.local.entity.DetallePedido
import com.quispe.cafeCampus.data.local.entity.Pedido
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
import kotlinx.coroutines.flow.map

class CafeRepositoryRoom(private val db: CafeDatabase) : CafeRepository {

    private val productoDao = db.productoDao()
    private val usuarioDao = db.usuarioDao()
    private val pedidoDao = db.pedidoDao()

    override suspend fun login(correo: String, clave: String): Usuario? =
        usuarioDao.login(correo, clave)

    override suspend fun usuarioPorRol(rol: Rol): Usuario? =
        usuarioDao.porRol(rol.name)

    override suspend fun usuarioPorCorreo(correo: String): Usuario? =
        usuarioDao.porCorreo(correo)

    override suspend fun registrar(
        nombre: String,
        correo: String,
        clave: String
    ): ResultadoRegistro {
        val nombreLimpio = nombre.trim()
        val correoLimpio = correo.trim().lowercase()

        if (nombreLimpio.isEmpty() || correoLimpio.isEmpty() || clave.isEmpty()) {
            return ResultadoRegistro.Error("Completa todos los campos")
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(correoLimpio).matches()) {
            return ResultadoRegistro.Error("El correo no tiene un formato válido")
        }
        if (clave.length < 4) {
            return ResultadoRegistro.Error("La clave debe tener al menos 4 caracteres")
        }
        if (usuarioDao.existeCorreo(correoLimpio) > 0) {
            return ResultadoRegistro.Error("Este correo ya está registrado")
        }

        usuarioDao.insertarNuevo(
            Usuario(
                nombre = nombreLimpio,
                correo = correoLimpio,
                clave = clave,
                rol = Rol.CLIENTE.name
            )
        )
        return ResultadoRegistro.Exito
    }

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

    override fun observarVentasPorDia(inicioSemana: Long): Flow<List<com.quispe.cafeCampus.model.VentaDia>> {
        val nombresDias = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
        val nombresLargos = listOf("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo")
        val calHoy = java.util.Calendar.getInstance()
        val diaHoyIndex = (calHoy.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7

        return pedidoDao.observarPedidosDesde(inicioSemana).map { pedidos ->
            val sumasReales = DoubleArray(7) { 0.0 }
            val cal = java.util.Calendar.getInstance()
            for (p in pedidos) {
                cal.timeInMillis = p.fecha
                val idx = (cal.get(java.util.Calendar.DAY_OF_WEEK) + 5) % 7
                if (idx in 0..6) {
                    sumasReales[idx] += p.total
                }
            }

            List(7) { i ->
                com.quispe.cafeCampus.model.VentaDia(
                    dia = nombresDias[i],
                    diaLargo = nombresLargos[i],
                    total = sumasReales[i],
                    esHoy = (i == diaHoyIndex)
                )
            }
        }
    }

    override fun observarCantidadPorEstado(estado: EstadoPedido): Flow<Int> =
        pedidoDao.cantidadPorEstado(estado.name)

    override fun observarMasVendido(inicioDia: Long): Flow<ProductoVendido?> =
        pedidoDao.masVendido(inicioDia).map { filas ->
            filas.firstOrNull()?.let { ProductoVendido(it.nombre, it.cantidadTotal) }
        }

    override fun observarTotalesPorPago(inicioDia: Long): Flow<List<TotalPorPago>> =
        pedidoDao.totalesPorPago(inicioDia).map { filas ->
            filas.map { TotalPorPago(it.pago, it.total) }
        }
}
