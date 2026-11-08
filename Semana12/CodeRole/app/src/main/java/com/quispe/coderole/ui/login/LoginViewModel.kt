package com.quispe.coderole.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.data.auth.AuthRepository
import com.quispe.coderole.domain.model.Sesion
import com.quispe.coderole.domain.model.TipoActividad
import com.quispe.coderole.domain.repository.ActividadRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val correo: String = "",
    val clave: String = "",
    val cargando: Boolean = true,
    val error: String? = null,
    val mensaje: String? = null,
    val modoRegistro: Boolean = false,
    val sesion: Sesion? = null,
)

class LoginViewModel(
    private val auth: AuthRepository,
    private val actividad: ActividadRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val sesion = auth.sesionActual()
            _uiState.update { it.copy(cargando = false, sesion = sesion) }
            if (sesion != null) actividad.registrar(TipoActividad.SESION)
        }
    }

    fun cambiarCorreo(valor: String) = _uiState.update { it.copy(correo = valor, error = null) }
    fun cambiarClave(valor: String) = _uiState.update { it.copy(clave = valor, error = null) }

    fun mostrarRegistro() = _uiState.update { it.copy(modoRegistro = true, error = null, mensaje = null) }
    fun mostrarLogin() = _uiState.update { it.copy(modoRegistro = false, error = null, mensaje = null) }

    fun registrar() {
        val actual = _uiState.value
        if (actual.correo.isBlank() || actual.clave.isBlank()) {
            _uiState.update { it.copy(error = "Completa tu correo y contraseña.", mensaje = null) }
            return
        }
        if (actual.clave.length < 6) {
            _uiState.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres.", mensaje = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, mensaje = null) }
            auth.registrar(actual.correo, actual.clave)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            clave = "",
                            modoRegistro = false,
                            mensaje = "Cuenta creada. Inicia sesión con tu correo y contraseña.",
                        )
                    }
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: "No se pudo crear la cuenta.") } }
        }
    }

    fun ingresar() {
        val actual = _uiState.value
        if (actual.correo.isBlank() || actual.clave.isBlank()) {
            _uiState.update { it.copy(error = "Completa tu correo y contraseña.", mensaje = null) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, error = null, mensaje = null) }
            auth.iniciarSesion(actual.correo, actual.clave)
                .onSuccess { sesion ->
                    actividad.registrar(TipoActividad.SESION)
                    _uiState.update { it.copy(cargando = false, sesion = sesion) }
                }
                .onFailure { e -> _uiState.update { it.copy(cargando = false, error = e.message ?: "No se pudo iniciar sesión.") } }
        }
    }

    fun cerrarSesion() {
        auth.cerrarSesion()
        _uiState.update { LoginUiState(cargando = false) }
    }

    class Factory(
        private val auth: AuthRepository,
        private val actividad: ActividadRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = LoginViewModel(auth, actividad) as T
    }
}
