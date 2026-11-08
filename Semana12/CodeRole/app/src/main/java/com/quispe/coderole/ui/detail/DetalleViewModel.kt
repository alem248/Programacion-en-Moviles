package com.quispe.coderole.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.domain.model.ChatUpdate
import com.quispe.coderole.domain.model.CharacterEmotion
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.domain.model.TipoActividad
import com.quispe.coderole.domain.repository.ActividadRepository
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
    val enviando: Boolean = false,
    /** Respuesta parcial recibida en streaming mientras la mentora escribe. */
    val respuestaParcial: String? = null,
    val errorConversacion: String? = null,
)

class DetalleViewModel(
    private val repositorio: ItemRepository,
    private val chatRepository: ChatRepository,
    private val actividad: ActividadRepository,
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
    }

    fun cambiarTexto(texto: String) = _uiState.update { it.copy(textoMensaje = texto) }

    fun enviarMensaje() {
        val texto = _uiState.value.textoMensaje.trim()
        if (texto.isBlank() || _uiState.value.enviando) return
        _uiState.update {
            it.copy(
                textoMensaje = "",
                emocion = CharacterEmotion.THINKING,
                enviando = true,
                respuestaParcial = null,
                errorConversacion = null,
            )
        }
        viewModelScope.launch {
            try {
                chatRepository.enviarMensaje(texto).collect { actualizacion ->
                    when (actualizacion) {
                        is ChatUpdate.Parcial -> _uiState.update { it.copy(respuestaParcial = actualizacion.texto) }
                        ChatUpdate.Completado -> _uiState.update {
                            it.copy(emocion = CharacterEmotion.HAPPY, enviando = false, respuestaParcial = null)
                        }
                        is ChatUpdate.Fallo -> _uiState.update {
                            it.copy(
                                emocion = CharacterEmotion.SURPRISED,
                                enviando = false,
                                respuestaParcial = null,
                                errorConversacion = actualizacion.error.message
                                    ?: "No se pudo enviar el mensaje. Comprueba tu conexión.",
                            )
                        }
                    }
                }
            } catch (cancelacion: kotlinx.coroutines.CancellationException) {
                throw cancelacion
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        emocion = CharacterEmotion.SURPRISED,
                        enviando = false,
                        respuestaParcial = null,
                        errorConversacion = error.message ?: "No se pudo enviar el mensaje. Comprueba tu conexión.",
                    )
                }
            }
        }
    }

    fun alternarFavorito() {
        val personaje = _uiState.value.personaje ?: return
        viewModelScope.launch {
            repositorio.alternarFavorito(personaje.id)
            if (!personaje.esFavorito) actividad.registrar(TipoActividad.FAVORITO, "Mentora")
        }
    }

    fun alternarRespuestaFavorita(mensaje: MensajeConversacion) {
        if (mensaje.esUsuario) return
        viewModelScope.launch {
            chatRepository.cambiarRespuestaFavorita(mensaje.id, !mensaje.favorita)
                .onFailure { error ->
                    _uiState.update {
                        it.copy(errorConversacion = error.message ?: "No se pudo actualizar la respuesta guardada.")
                    }
                }
        }
    }

    class Factory(
        private val repositorio: ItemRepository,
        private val chatRepository: ChatRepository,
        private val actividad: ActividadRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            DetalleViewModel(repositorio, chatRepository, actividad) as T
    }
}
