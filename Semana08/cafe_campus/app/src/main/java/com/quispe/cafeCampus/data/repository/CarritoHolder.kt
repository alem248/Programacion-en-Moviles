package com.quispe.cafeCampus.data.repository

import com.quispe.cafeCampus.model.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Comparte el carrito entre distintos ViewModels (MenuViewModel y
 * PedidosClienteViewModel) sin acoplarlos entre sí.
 *
 * Es el único punto de verdad del carrito: lo usa quien arma el pedido
 * (menú/detalle) y quien lo rellena desde un pedido anterior (repetir).
 * Se instancia una sola vez en [com.quispe.cafeCampus.CafeApp].
 */
class CarritoHolder {

    private val _items = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val items: StateFlow<List<ItemCarrito>> = _items.asStateFlow()

    fun agregar(item: ItemCarrito) {
        _items.update { actual ->
            val existente = actual.find { it.producto.id == item.producto.id }
            if (existente != null) {
                actual.map {
                    if (it.producto.id == item.producto.id) {
                        it.copy(
                            cantidad = it.cantidad + item.cantidad,
                            nota = item.nota.ifBlank { it.nota }
                        )
                    } else it
                }
            } else actual + item
        }
    }

    fun cargar(items: List<ItemCarrito>) {
        _items.value = items
    }

    fun quitar(productoId: Int) {
        _items.update { actual -> actual.filterNot { it.producto.id == productoId } }
    }

    fun vaciar() {
        _items.value = emptyList()
    }
}
