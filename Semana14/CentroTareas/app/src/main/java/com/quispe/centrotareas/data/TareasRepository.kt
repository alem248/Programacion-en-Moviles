package com.quispe.centrotareas.data

import kotlinx.coroutines.flow.Flow

interface TareasRepository {
    suspend fun contarPrimos(hasta: Int): Int
    suspend fun cargarPerfil(): String
    suspend fun cargarPedidos(simularError: Boolean): List<String>

    // Mejora 1: la tarea simulada vive en el Repository y publica su progreso (0f..1f)
    fun progresoTarea(): Flow<Float>

    // Mejora 3: últimos resultados persistidos con DataStore
    fun historial(): Flow<List<String>>
    suspend fun agregarAlHistorial(resultado: String)
}
