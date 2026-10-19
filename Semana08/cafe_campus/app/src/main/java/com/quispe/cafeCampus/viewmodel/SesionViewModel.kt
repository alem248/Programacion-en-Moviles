package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.local.entity.Usuario
import com.quispe.cafeCampus.data.prefs.PreferenciasStore
import com.quispe.cafeCampus.data.repository.CafeRepository
import com.quispe.cafeCampus.model.Rol
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SesionViewModel(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModel() {

    private val _usuarioActual = MutableStateFlow<Usuario?>(null)
    val usuarioActual: StateFlow<Usuario?> = _usuarioActual.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _rolActivo = MutableStateFlow(Rol.CLIENTE)
    val rolActivo: StateFlow<Rol> = _rolActivo.asStateFlow()

    private val _sesionLista = MutableStateFlow(false)
    val sesionLista: StateFlow<Boolean> = _sesionLista.asStateFlow()

    val modoOscuro: StateFlow<Boolean> = prefs.modoOscuro
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val recibirPedidos: StateFlow<Boolean> = prefs.recibirPedidos
        .stateIn(viewModelScope, SharingStarted.Eagerly, true)

    init {
        viewModelScope.launch {
            val perfil = prefs.perfilActivo.first()
            if (perfil.isNotEmpty()) {
                val rol = Rol.entries.find { it.name == perfil }
                if (rol != null) {
                    val usuario = repo.usuarioPorRol(rol)
                    if (usuario != null) {
                        _usuarioActual.value = usuario
                        _rolActivo.value = rol
                    }
                }
            }
            _sesionLista.value = true
        }
    }

    fun login(correo: String, clave: String, rolSeleccionado: Rol) {
        viewModelScope.launch {
            _error.value = null
            if (correo.isBlank() || clave.isBlank()) {
                _error.value = "Completa correo y contraseña"
                return@launch
            }
            val usuario = repo.login(correo.trim(), clave)
            if (usuario == null) {
                _error.value = "Correo o contraseña incorrectos"
                return@launch
            }
            val rolUsuario = Rol.entries.find { it.name == usuario.rol }
            if (rolUsuario == null || rolUsuario != rolSeleccionado) {
                _error.value = "Este usuario no pertenece al rol seleccionado"
                return@launch
            }
            prefs.guardarPerfil(rolUsuario)
            _usuarioActual.value = usuario
            _rolActivo.value = rolUsuario
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            prefs.cerrarSesion()
            _usuarioActual.value = null
            _rolActivo.value = Rol.CLIENTE
        }
    }

    fun cambiarModoOscuro(valor: Boolean) {
        viewModelScope.launch {
            prefs.guardarModoOscuro(valor)
        }
    }

    fun cambiarRecibirPedidos(valor: Boolean) {
        viewModelScope.launch {
            prefs.guardarRecibirPedidos(valor)
        }
    }

    fun cambiarPerfil(rol: Rol) {
        viewModelScope.launch {
            prefs.guardarPerfil(rol)
            _usuarioActual.value = repo.usuarioPorRol(rol)
            _rolActivo.value = rol
        }
    }
}
