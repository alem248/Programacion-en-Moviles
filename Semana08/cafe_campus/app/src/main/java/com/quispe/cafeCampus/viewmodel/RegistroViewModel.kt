package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.repository.CafeRepository
import com.quispe.cafeCampus.model.ResultadoRegistro
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class RegistroViewModel(private val repo: CafeRepository) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _cargando = MutableStateFlow(false)
    val cargando: StateFlow<Boolean> = _cargando.asStateFlow()

    private val _exito = MutableStateFlow(false)
    val exito: StateFlow<Boolean> = _exito.asStateFlow()

    fun registrar(nombre: String, correo: String, clave: String, repetirClave: String) {
        viewModelScope.launch {
            _error.value = null
            if (clave != repetirClave) {
                _error.value = "Las contraseñas no coinciden"
                return@launch
            }
            _cargando.value = true
            when (val resultado = repo.registrar(nombre, correo, clave)) {
                is ResultadoRegistro.Exito -> _exito.value = true
                is ResultadoRegistro.Error -> _error.value = resultado.mensaje
            }
            _cargando.value = false
        }
    }

    fun limpiarError() {
        _error.value = null
    }

    fun limpiarExito() {
        _exito.value = false
    }
}
