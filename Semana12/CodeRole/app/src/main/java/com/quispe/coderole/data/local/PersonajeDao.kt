package com.quispe.coderole.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonajeDao {
    @Query("SELECT * FROM personajes ORDER BY nombre")
    fun observarTodos(): Flow<List<PersonajeEntity>>

    @Query("SELECT * FROM personajes WHERE esFavorito = 1 ORDER BY nombre")
    fun observarFavoritos(): Flow<List<PersonajeEntity>>

    @Query("SELECT * FROM personajes WHERE id = :id LIMIT 1")
    fun observarPorId(id: Int): Flow<PersonajeEntity?>

    @Query("SELECT * FROM personajes WHERE id = :id LIMIT 1")
    suspend fun obtenerPorId(id: Int): PersonajeEntity?

    @Query("SELECT id FROM personajes WHERE esFavorito = 1")
    suspend fun idsFavoritos(): List<Int>

    @Query("SELECT COUNT(*) FROM personajes")
    suspend fun contar(): Int

    @Query("SELECT COUNT(*) FROM personajes WHERE esFavorito = 1")
    suspend fun contarFavoritos(): Int

    @Upsert
    suspend fun guardarTodos(personajes: List<PersonajeEntity>)

    @Upsert
    suspend fun guardar(personaje: PersonajeEntity)

    @Query("UPDATE personajes SET esFavorito = NOT esFavorito WHERE id = :id")
    suspend fun alternarFavorito(id: Int)

    @Query("DELETE FROM personajes")
    suspend fun limpiar()
}
