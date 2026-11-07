package com.quispe.coderole.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.domain.model.CharacterEmotion
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.domain.repository.ItemRepository
import com.quispe.coderole.ui.UiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MensajeChat(val id: Long, val texto: String, val esUsuario: Boolean)

data class DetalleUiState(
    val personaje: Personaje? = null,
    val conversacion: List<MensajeChat> = listOf(
        MensajeChat(0, "¡Hola! Soy tu mentora de programación. ¿Qué te gustaría aprender hoy?", false),
    ),
    val textoMensaje: String = "",
    val emocion: CharacterEmotion = CharacterEmotion.NEUTRAL,
    val estado: UiState<Unit> = UiState.Cargando,
    val sinConexion: Boolean = false,
    val enviando: Boolean = false,
)

class DetalleViewModel(private val repositorio: ItemRepository) : ViewModel() {
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
        // Consulta el endpoint de detalle; la pantalla sigue leyendo exclusivamente de Room.
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
        _uiState.update { actual ->
            actual.copy(
                textoMensaje = "",
                emocion = CharacterEmotion.THINKING,
                enviando = true,
                conversacion = actual.conversacion + MensajeChat(System.nanoTime(), texto, true),
            )
        }
        viewModelScope.launch {
            delay(850)
            val sorprendido = texto.contains("?", ignoreCase = true) &&
                listOf("error", "no funciona", "crash", "falla").any { texto.contains(it, true) }
            val respuesta = responderEnFaseInicial(texto)
            _uiState.update { actual ->
                actual.copy(
                    conversacion = actual.conversacion + MensajeChat(System.nanoTime(), respuesta, false),
                    emocion = if (sorprendido) CharacterEmotion.SURPRISED else CharacterEmotion.HAPPY,
                    enviando = false,
                )
            }
        }
    }

    fun alternarFavorito() {
        val id = _uiState.value.personaje?.id ?: return
        viewModelScope.launch { repositorio.alternarFavorito(id) }
    }

    private fun responderEnFaseInicial(texto: String): String {
        val tema = when {
            texto.contains("variable", true) -> "Una variable guarda un valor que puedes consultar o cambiar. En Kotlin puedes declarar una mutable con var y una inmutable con val."
            texto.contains("compose", true) || texto.contains("interfaz", true) -> "En Compose describes la interfaz con funciones @Composable. La pantalla se vuelve a dibujar cuando cambia el estado que observa."
            texto.contains("viewmodel", true) || texto.contains("mvvm", true) -> "En MVVM, la pantalla observa el UiState y envía eventos al ViewModel. El Repository reúne las fuentes de datos y Room mantiene la lectura local."
            texto.contains("retrofit", true) || texto.contains("api", true) -> "Retrofit convierte una interfaz Kotlin en llamadas HTTP. El Repository puede guardar su respuesta en Room para que la app siga mostrando datos sin conexión."
            texto.contains("error", true) || texto.contains("no funciona", true) -> "Vamos paso a paso: revisa el mensaje de error completo, la línea indicada y qué valor tiene el estado justo antes de fallar."
            else -> "¡Buena pregunta! Cuéntame qué parte de programación estás practicando y lo revisamos paso a paso."
        }
        return "$tema\n\nEsta respuesta es una demostración local de la Fase 1."
    }

    class Factory(private val repositorio: ItemRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = DetalleViewModel(repositorio) as T
    }
}
