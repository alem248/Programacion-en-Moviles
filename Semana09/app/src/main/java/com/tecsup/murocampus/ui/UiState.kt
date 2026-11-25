package com.tecsup.murocampus.ui

sealed interface UiState<out T> {
    data object Cargando : UiState<Nothing>
    data class Error(val mensaje: String) : UiState<Nothing>
    data class Exito<T>(val dato: T) : UiState<T>
}
