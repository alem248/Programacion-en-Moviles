package com.quispe.coderole.data.auth

import com.quispe.coderole.domain.model.Sesion

interface AuthRepository {
    suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion>
    suspend fun registrar(correo: String, clave: String): Result<Unit>
    suspend fun sesionActual(): Sesion?
    fun cerrarSesion()
}
