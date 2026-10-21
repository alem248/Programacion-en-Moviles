package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.local.relation.PedidoConDetalle
import com.quispe.cafeCampus.data.prefs.PreferenciasStore
import com.quispe.cafeCampus.data.repository.CafeRepository
import com.quispe.cafeCampus.data.repository.CarritoHolder
import com.quispe.cafeCampus.model.ItemCarrito
import com.quispe.cafeCampus.model.Rol
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosClienteViewModel(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore,
    private val carritoHolder: CarritoHolder
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _pedidoCreado = MutableStateFlow<Long?>(null)
    val pedidoCreado: StateFlow<Long?> = _pedidoCreado.asStateFlow()

    private val _aviso = MutableStateFlow<String?>(null)
    val aviso: StateFlow<String?> = _aviso.asStateFlow()

    private val clienteId: Flow<Int> = prefs.perfilActivo.map { nombre ->
        val rol = Rol.entries.find { it.name == nombre }
        if (rol == null) -1 else repo.usuarioPorRol(rol)?.id ?: -1
    }

    val misPedidos: StateFlow<List<PedidoConDetalle>> = clienteId
        .flatMapLatest { id -> repo.observarPedidosCliente(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    /** Productos vigentes, para reconstruir un pedido anterior con precios actuales. */
    private val productosActuales: StateFlow<Map<Int, com.quispe.cafeCampus.data.local.entity.Producto>> =
        repo.observarProductos()
            .map { lista -> lista.associateBy { it.id } }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    private val observadores = mutableMapOf<Int, StateFlow<PedidoConDetalle?>>()

    fun pedido(id: Int): StateFlow<PedidoConDetalle?> =
        observadores.getOrPut(id) {
            repo.observarPedido(id)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        }

    fun confirmar(
        items: List<ItemCarrito>,
        horaRecojo: String,
        pago: String,
        nota: String
    ) {
        viewModelScope.launch {
            _error.value = null
            if (items.isEmpty()) {
                _error.value = "Tu carrito está vacío"
                return@launch
            }
            if (!prefs.recibirPedidos.first()) {
                _error.value = "La cafetería está cerrada por ahora"
                return@launch
            }
            val rol = Rol.entries.find { it.name == prefs.perfilActivo.first() }
            val usuario = rol?.let { repo.usuarioPorRol(it) }
            if (usuario == null) {
                _error.value = "Debes iniciar sesión para confirmar"
                return@launch
            }
            val id = repo.crearPedido(usuario.id, items, horaRecojo, pago, nota)
            _pedidoCreado.value = id
        }
    }

    fun limpiarPedidoCreado() {
        _pedidoCreado.value = null
    }

    fun limpiarError() {
        _error.value = null
    }

    fun limpiarAviso() {
        _aviso.value = null
    }

    /**
     * Vuelve a cargar al carrito los productos de un pedido anterior.
     * Reconstruye cada línea con el producto VIGENTE (precio y disponibilidad
     * actuales); si un producto ya no existe en el menú, se omite.
     */
    fun repetirPedido(pedido: PedidoConDetalle) {
        val actuales = productosActuales.value
        val items = pedido.detalles.mapNotNull { detalle ->
            actuales[detalle.productoId]?.let { producto ->
                ItemCarrito(producto = producto, cantidad = detalle.cantidad, nota = "")
            }
        }
        if (items.isEmpty()) {
            _aviso.value = "Los productos de este pedido ya no están disponibles"
            return
        }
        val omitidos = pedido.detalles.size - items.size
        carritoHolder.vaciar()
        items.forEach { carritoHolder.agregar(it) }
        _aviso.value = if (omitidos > 0) {
            "Pedido cargado al carrito ($omitidos producto(s) ya no disponibles)"
        } else {
            "Pedido cargado al carrito"
        }
    }
}
