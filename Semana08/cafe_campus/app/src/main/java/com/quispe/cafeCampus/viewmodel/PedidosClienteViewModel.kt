package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.local.relation.PedidoConDetalle
import com.quispe.cafeCampus.data.prefs.PreferenciasStore
import com.quispe.cafeCampus.data.repository.CafeRepository
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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosClienteViewModel(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _pedidoCreado = MutableStateFlow<Long?>(null)
    val pedidoCreado: StateFlow<Long?> = _pedidoCreado.asStateFlow()

    private val clienteId: Flow<Int> = prefs.perfilActivo.map { nombre ->
        val rol = Rol.entries.find { it.name == nombre }
        if (rol == null) -1 else repo.usuarioPorRol(rol)?.id ?: -1
    }

    val misPedidos: StateFlow<List<PedidoConDetalle>> = clienteId
        .flatMapLatest { id -> repo.observarPedidosCliente(id) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
}
