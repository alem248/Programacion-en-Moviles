package com.quispe.coderole.data.remote

import retrofit2.http.GET
import retrofit2.http.Path

interface PersonajeApiService {
    @GET("users")
    suspend fun listar(): List<PersonajeDto>

    @GET("users/{id}")
    suspend fun detalle(@Path("id") id: Int): PersonajeDto
}
