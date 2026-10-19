package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.local.relation.PedidoConDetalle
import com.quispe.cafeCampus.data.repository.CafeRepository
import com.quispe.cafeCampus.model.EstadoPedido
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PedidosAdminViewModel(private val repo: CafeRepository) : ViewModel() {

    private val _estadoSeleccionado = MutableStateFlow(EstadoPedido.RECIBIDO)
    val estadoSeleccionado: StateFlow<EstadoPedido> = _estadoSeleccionado.asStateFlow()

    val pedidos: StateFlow<List<PedidoConDetalle>> = _estadoSeleccionado
        .flatMapLatest { estado -> repo.observarPedidosPorEstado(estado) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun seleccionarEstado(estado: EstadoPedido) {
        _estadoSeleccionado.value = estado
    }

    private val observadores = mutableMapOf<Int, StateFlow<PedidoConDetalle?>>()

    fun pedido(id: Int): StateFlow<PedidoConDetalle?> =
        observadores.getOrPut(id) {
            repo.observarPedido(id)
                .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        }

    fun cambiarEstado(pedidoId: Int, estado: EstadoPedido) {
        viewModelScope.launch {
            repo.cambiarEstado(pedidoId, estado)
        }
    }

    fun avanzar(pedido: PedidoConDetalle) {
        viewModelScope.launch {
            val actual = EstadoPedido.valueOf(pedido.pedido.estado)
            repo.cambiarEstado(pedido.pedido.id, actual.siguiente())
        }
    }

    fun cancelar(pedido: PedidoConDetalle) {
        viewModelScope.launch {
            repo.cambiarEstado(pedido.pedido.id, EstadoPedido.CANCELADO)
        }
    }
}
