package com.quispe.coderole.domain.model

data class MensajeConversacion(
    val id: String,
    val texto: String,
    val esUsuario: Boolean,
    val creadoEn: Long,
)
