package com.quispe.coderole.domain.model

data class Personaje(
    val id: Int,
    val nombre: String,
    val subtitulo: String,
    val descripcion: String,
    val avatarUrl: String? = null,
    val esFavorito: Boolean = false,
)
