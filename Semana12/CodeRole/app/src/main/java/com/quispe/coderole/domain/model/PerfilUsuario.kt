package com.quispe.coderole.domain.model

/**
 * Perfil visible en toda la app: se lee de Firestore en tiempo real, así que
 * un cambio hecho desde el dispositivo del usuario se refleja en el resto de
 * las pantallas y en los demás dispositivos.
 */
data class PerfilUsuario(
    val uid: String,
    val correo: String,
    val nombre: String = "",
    val fotoBase64: String? = null,
) {
    /** Nombre mostrado en la interfaz; si falta, se usa la parte del correo. */
    val nombreVisible: String
        get() = nombre.trim().ifBlank { correo.substringBefore('@') }
}
