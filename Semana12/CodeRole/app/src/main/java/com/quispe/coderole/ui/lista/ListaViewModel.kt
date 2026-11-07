package com.quispe.coderole.ui.lista

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.domain.repository.ItemRepository
import com.quispe.coderole.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ListaUiState(
    val estado: UiState<List<Personaje>> = UiState.Cargando,
    val sinConexion: Boolean = false,
    val mensaje: String? = null,
)

class ListaViewModel(private val repositorio: ItemRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(ListaUiState())
    val uiState: StateFlow<ListaUiState> = _uiState.asStateFlow()
    val personajes: StateFlow<List<Personaje>> = repositorio.personajes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )
    val favoritos: StateFlow<List<Personaje>> = repositorio.favoritos.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )

    init {
        viewModelScope.launch {
            repositorio.personajes.collect { datos ->
                _uiState.update { actual ->
                    actual.copy(estado = if (datos.isEmpty() && actual.estado is UiState.Cargando) {
                        UiState.Cargando
                    } else UiState.Exito(datos))
                }
            }
        }
        sincronizar()
    }

    fun sincronizar() {
        viewModelScope.launch {
            _uiState.update { it.copy(mensaje = null) }
            repositorio.refrescar()
                .onSuccess { total -> _uiState.update { it.copy(sinConexion = false, mensaje = "Se guardó $total mentor en el dispositivo.") } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            sinConexion = true,
                            estado = if (it.estado is UiState.Exito) it.estado else UiState.Error("No fue posible cargar el mentor. Revisa tu conexión e inténtalo de nuevo."),
                            mensaje = "Sin conexión. Se muestran los datos guardados en este dispositivo.",
                        )
                    }
                }
        }
    }

    class Factory(private val repositorio: ItemRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ListaViewModel(repositorio) as T
    }
}
