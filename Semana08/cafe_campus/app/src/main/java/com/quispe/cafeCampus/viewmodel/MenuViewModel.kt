package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.data.repository.CafeRepository
import com.quispe.cafeCampus.model.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class MenuViewModel(private val repo: CafeRepository) : ViewModel() {

    val productos: StateFlow<List<Producto>> = repo.observarProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _carrito = MutableStateFlow<List<ItemCarrito>>(emptyList())
    val carrito: StateFlow<List<ItemCarrito>> = _carrito.asStateFlow()

    private val _categoriaSeleccionada = MutableStateFlow("Todo")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    fun agregar(producto: Producto, cantidad: Int, nota: String) {
        _carrito.update { actual ->
            val existente = actual.find { it.producto.id == producto.id }
            if (existente != null) {
                actual.map {
                    if (it.producto.id == producto.id) {
                        it.copy(cantidad = it.cantidad + cantidad, nota = nota.ifBlank { it.nota })
                    } else it
                }
            } else {
                actual + ItemCarrito(producto, cantidad, nota)
            }
        }
    }

    fun quitar(productoId: Int) {
        _carrito.update { actual -> actual.filterNot { it.producto.id == productoId } }
    }

    fun vaciar() {
        _carrito.value = emptyList()
    }

    fun total(): Double = _carrito.value.sumOf { it.subtotal }

    fun totalCarrito(): StateFlow<Int> =
        MutableStateFlow(_carrito.value.sumOf { it.cantidad }).asStateFlow()

    fun contarCarrito(): Int = _carrito.value.sumOf { it.cantidad }

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }
}
