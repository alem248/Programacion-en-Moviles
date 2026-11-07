package com.quispe.coderole.data.remote

data class PersonajeDto(
    val id: Int,
    val name: String,
    val username: String,
    val email: String,
    val phone: String,
    val website: String,
    val company: EmpresaDto?,
)

data class EmpresaDto(val name: String?)
