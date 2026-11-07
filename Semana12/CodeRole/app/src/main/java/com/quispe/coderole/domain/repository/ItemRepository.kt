package com.quispe.coderole.domain.repository

import com.quispe.coderole.domain.model.Personaje
import kotlinx.coroutines.flow.Flow

/** Contrato que permite a ViewModels depender de una interfaz y no de Room o Retrofit. */
interface ItemRepository {
    val personajes: Flow<List<Personaje>>
    val favoritos: Flow<List<Personaje>>
    suspend fun refrescar(): Result<Int>
    suspend fun obtenerDetalle(id: Int): Result<Personaje>
    suspend fun alternarFavorito(id: Int)
    suspend fun cantidadGuardada(): Int
    suspend fun cantidadFavoritos(): Int
    suspend fun limpiarCache()
}

typealias PersonajeRepository = ItemRepository
