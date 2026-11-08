package com.quispe.coderole.ui.perfil

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.data.media.Imagenes
import com.quispe.coderole.data.prefs.PreferenciasTema
import com.quispe.coderole.domain.model.PerfilUsuario
import com.quispe.coderole.domain.repository.PerfilRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PerfilUiState(
    val perfil: PerfilUsuario? = null,
    val nombreEditado: String = "",
    val guardando: Boolean = false,
    val procesandoFoto: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null,
    /** `null` significa que la app sigue el tema del sistema. */
    val modoOscuro: Boolean? = null,
)

class PerfilViewModel(
    private val uid: String,
    private val correo: String,
    private val perfilRepository: PerfilRepository,
    private val tema: PreferenciasTema,
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            tema.oscuro.collect { valor -> _uiState.update { it.copy(modoOscuro = valor) } }
        }
        viewModelScope.launch {
            perfilRepository.observar(uid, correo)
                .catch { error ->
                    _uiState.update {
                        it.copy(error = it.error ?: error.message ?: "No se pudo cargar el perfil.")
                    }
                }
                .collect { perfil -> _uiState.update { it.aplicar(perfil) } }
        }
    }

    /** Actualiza el campo de nombre sin pisar lo que el usuario está escribiendo. */
    private fun PerfilUiState.aplicar(perfil: PerfilUsuario): PerfilUiState {
        val enEdicion = nombreEditado.isNotBlank() && nombreEditado != this.perfil?.nombre
        return copy(perfil = perfil, nombreEditado = if (enEdicion) nombreEditado else perfil.nombre)
    }

    fun cambiarNombreEditado(valor: String) =
        _uiState.update { it.copy(nombreEditado = valor, mensaje = null, error = null) }

    fun guardarNombre() {
        val nombre = _uiState.value.nombreEditado
        viewModelScope.launch {
            _uiState.update { it.copy(guardando = true, mensaje = null, error = null) }
            perfilRepository.actualizarNombre(nombre)
                .onSuccess {
                    _uiState.update { it.copy(guardando = false, mensaje = "Nombre actualizado en toda la app.") }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(guardando = false, error = error.message ?: "No se pudo guardar el nombre.")
                    }
                }
        }
    }

    /** Sube la foto elegida desde la galería. */
    fun cambiarFoto(uri: Uri, contexto: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(procesandoFoto = true, mensaje = null, error = null) }
            Imagenes.jpegParaPerfil(contexto, uri).fold(
                onSuccess = { bytes ->
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    perfilRepository.actualizarFoto(base64)
                        .onSuccess {
                            _uiState.update { it.copy(procesandoFoto = false, mensaje = "Foto actualizada en toda la app.") }
                        }
                        .onFailure { error ->
                            _uiState.update {
                                it.copy(procesandoFoto = false, error = error.message ?: "No se pudo guardar la foto.")
                            }
                        }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(procesandoFoto = false, error = error.message ?: "No se pudo leer la imagen.")
                    }
                },
            )
        }
    }

    fun quitarFoto() {
        viewModelScope.launch {
            _uiState.update { it.copy(procesandoFoto = true, mensaje = null, error = null) }
            perfilRepository.actualizarFoto(null)
                .onSuccess { _uiState.update { it.copy(procesandoFoto = false, mensaje = "Se quitó la foto de perfil.") } }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(procesandoFoto = false, error = error.message ?: "No se pudo quitar la foto.")
                    }
                }
        }
    }

    /** Guarda la preferencia de tema: `true` = oscuro, `false` = claro. */
    fun cambiarModoOscuro(valor: Boolean) = tema.establecerOscuro(valor)

    class Factory(
        private val uid: String,
        private val correo: String,
        private val perfilRepository: PerfilRepository,
        private val tema: PreferenciasTema,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            PerfilViewModel(uid, correo, perfilRepository, tema) as T
    }
}
