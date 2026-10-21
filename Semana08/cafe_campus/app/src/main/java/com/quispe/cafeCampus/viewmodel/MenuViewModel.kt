package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.data.repository.CafeRepository
import com.quispe.cafeCampus.data.repository.CarritoHolder
import com.quispe.cafeCampus.model.ItemCarrito
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

class MenuViewModel(
    private val repo: CafeRepository,
    private val carritoHolder: CarritoHolder
) : ViewModel() {

    val productos: StateFlow<List<Producto>> = repo.observarProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // El carrito vive en un holder compartido para que otros ViewModels
    // (p. ej. PedidosClienteViewModel al repetir un pedido) puedan llenarlo
    // sin acoplar esta ViewModel con ellas.
    val carrito: StateFlow<List<ItemCarrito>> = carritoHolder.items

    private val _categoriaSeleccionada = MutableStateFlow("Todo")
    val categoriaSeleccionada: StateFlow<String> = _categoriaSeleccionada.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()

    fun agregar(producto: Producto, cantidad: Int, nota: String) {
        carritoHolder.agregar(ItemCarrito(producto, cantidad, nota))
    }

    fun quitar(productoId: Int) {
        carritoHolder.quitar(productoId)
    }

    fun vaciar() {
        carritoHolder.vaciar()
    }

    fun total(): Double = carritoHolder.items.value.sumOf { it.subtotal }

    fun contarCarrito(): Int = carritoHolder.items.value.sumOf { it.cantidad }

    fun seleccionarCategoria(categoria: String) {
        _categoriaSeleccionada.value = categoria
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
    }
}
