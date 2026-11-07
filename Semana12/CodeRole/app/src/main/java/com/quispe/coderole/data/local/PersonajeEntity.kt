package com.quispe.coderole.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "personajes")
data class PersonajeEntity(
    @PrimaryKey val id: Int,
    val nombre: String,
    val subtitulo: String,
    val descripcion: String,
    val avatarUrl: String?,
    val esFavorito: Boolean = false,
)
