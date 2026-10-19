package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.data.repository.CafeRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MenuAdminViewModel(private val repo: CafeRepository) : ViewModel() {

    val productos: StateFlow<List<Producto>> = repo.observarProductos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun producto(id: Int): Producto? = productos.value.find { it.id == id }

    fun guardar(producto: Producto) {
        viewModelScope.launch {
            repo.guardarProducto(producto)
        }
    }

    fun eliminar(producto: Producto) {
        viewModelScope.launch {
            repo.eliminarProducto(producto)
        }
    }

    fun alternarDisponibilidad(producto: Producto) {
        viewModelScope.launch {
            repo.cambiarDisponibilidad(producto.id, !producto.disponible)
        }
    }
}
