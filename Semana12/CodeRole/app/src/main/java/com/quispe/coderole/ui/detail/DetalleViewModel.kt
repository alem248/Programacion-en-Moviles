package com.quispe.coderole.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.domain.model.CharacterEmotion
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.domain.repository.ChatRepository
import com.quispe.coderole.domain.repository.ItemRepository
import com.quispe.coderole.ui.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class DetalleUiState(
    val personaje: Personaje? = null,
    val conversacion: List<MensajeConversacion> = emptyList(),
    val textoMensaje: String = "",
    val emocion: CharacterEmotion = CharacterEmotion.NEUTRAL,
    val estado: UiState<Unit> = UiState.Cargando,
    val sinConexion: Boolean = false,
    val enviando: Boolean = false,
    val errorConversacion: String? = null,
)

class DetalleViewModel(
    private val repositorio: ItemRepository,
    private val chatRepository: ChatRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repositorio.personajes.collect { lista ->
                val mentor = lista.firstOrNull()
                _uiState.update { actual ->
                    actual.copy(
                        personaje = mentor,
                        estado = if (mentor != null) UiState.Exito(Unit) else UiState.Error("Aún no hay datos guardados."),
                    )
                }
            }
        }
        viewModelScope.launch {
            chatRepository.observarConversacion()
                .catch { error ->
                    _uiState.update { it.copy(errorConversacion = error.message ?: "No se pudo cargar el historial.") }
                }
                .collect { mensajes ->
                    _uiState.update { it.copy(conversacion = mensajes, errorConversacion = null) }
                }
        }
        // Se refresca el detalle remoto, pero la pantalla sigue leyendo el personaje exclusivamente desde Room.
        reintentarDetalle()
    }

    fun cambiarTexto(texto: String) = _uiState.update { it.copy(textoMensaje = texto) }

    fun reintentarDetalle() {
        viewModelScope.launch {
            repositorio.obtenerDetalle(1)
                .onSuccess { _uiState.update { it.copy(sinConexion = false) } }
                .onFailure { _uiState.update { it.copy(sinConexion = true) } }
        }
    }

    fun enviarMensaje() {
        val texto = _uiState.value.textoMensaje.trim()
        if (texto.isBlank() || _uiState.value.enviando) return
        _uiState.update {
            it.copy(textoMensaje = "", emocion = CharacterEmotion.THINKING, enviando = true, errorConversacion = null)
        }
        viewModelScope.launch {
            chatRepository.enviarMensaje(texto)
                .onSuccess {
                    _uiState.update { it.copy(emocion = CharacterEmotion.HAPPY, enviando = false) }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            emocion = CharacterEmotion.SURPRISED,
                            enviando = false,
                            errorConversacion = error.message ?: "No se pudo enviar el mensaje. Comprueba tu conexión.",
                        )
                    }
                }
        }
    }

    fun alternarFavorito() {
        val id = _uiState.value.personaje?.id ?: return
        viewModelScope.launch { repositorio.alternarFavorito(id) }
    }

    class Factory(
        private val repositorio: ItemRepository,
        private val chatRepository: ChatRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetalleViewModel(repositorio, chatRepository) as T
    }
}
