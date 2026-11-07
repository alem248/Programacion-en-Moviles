package com.quispe.coderole.data.mapper

import com.quispe.coderole.data.local.PersonajeEntity
import com.quispe.coderole.data.remote.PersonajeDto
import com.quispe.coderole.domain.model.Personaje

fun PersonajeDto.aEntidad(esFavorito: Boolean = false) = PersonajeEntity(
    id = 1,
    nombre = "Tu mentora de programación",
    subtitulo = "Siempre contigo · aprende a tu ritmo",
    descripcion = "Soy tu compañera de desarrollo. Puedo ayudarte con Kotlin, " +
        "Compose, arquitectura y buenas prácticas. En esta primera fase, mis " +
        "respuestas son demostrativas. Perfil de referencia: ${company?.name ?: username}.",
    avatarUrl = null,
    esFavorito = esFavorito,
)

fun PersonajeEntity.aModelo() = Personaje(
    id = id,
    nombre = nombre,
    subtitulo = subtitulo,
    descripcion = descripcion,
    avatarUrl = avatarUrl,
    esFavorito = esFavorito,
)
