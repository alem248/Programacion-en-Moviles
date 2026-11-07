package com.quispe.coderole.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.data.notification.Notificador
import com.quispe.coderole.domain.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val totalPersonajes: Int = 0,
    val totalFavoritos: Int = 0,
    val procesando: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null,
)

class AdminViewModel(
    private val repositorio: ItemRepository,
    private val notificador: Notificador,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()

    init { actualizarTotales() }

    fun sincronizar() {
        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true, mensaje = null, error = null) }
            repositorio.refrescar()
                .onSuccess { _ ->
                    actualizarTotales()
                    _uiState.update { it.copy(procesando = false, mensaje = "Se sincronizó el perfil de tu mentora.") }
                    notificador.mostrar("CodeRole", "Se guardó el perfil de tu mentora en el dispositivo.")
                }
                .onFailure { e -> _uiState.update { it.copy(procesando = false, error = e.message ?: "No se pudo sincronizar.") } }
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            repositorio.limpiarCache()
            actualizarTotales()
            _uiState.update { it.copy(mensaje = "Se limpió la caché local.", error = null) }
        }
    }

    private fun actualizarTotales() {
        viewModelScope.launch {
            val guardados = repositorio.cantidadGuardada()
            val favoritos = repositorio.cantidadFavoritos()
            _uiState.update { it.copy(totalPersonajes = guardados, totalFavoritos = favoritos) }
        }
    }

    class Factory(private val repo: ItemRepository, private val notificador: Notificador) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = AdminViewModel(repo, notificador) as T
    }
}
