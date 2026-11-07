package com.quispe.coderole.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.data.auth.AuthRepository
import com.quispe.coderole.domain.model.Sesion
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val cargando: Boolean = true,
    val error: String? = null,
    val sesion: Sesion? = null,
)

class LoginViewModel(private val auth: AuthRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch { _uiState.update { it.copy(cargando = false, sesion = auth.sesionActual()) } }
    }

    fun cambiarCorreo(valor: String) = _uiState.update { it.copy(correo = valor, error = null) }
    fun cambiarClave(valor: String) = _uiState.update { it.copy(clave = valor, error = null) }

    fun ingresar() {
        val actual = _uiState.value
        if (actual.correo.isBlank() || actual.clave.isBlank()) {
            _uiState.update { it.copy(error = "Completa tu correo y contraseña.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null) }
            auth.iniciarSesion(actual.correo, actual.clave)
                .onSuccess { sesion -> _uiState.update { it.copy(cargando = false, sesion = sesion) } }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: "No se pudo iniciar sesión.") } }
        }
    }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _uiState.update { LoginUiState(cargando = false) }
    }

    class Factory(private val auth: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = LoginViewModel(auth) as T
    }
}
