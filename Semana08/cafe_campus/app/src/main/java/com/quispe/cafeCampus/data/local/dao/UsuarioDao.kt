package com.quispe.cafeCampus.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.quispe.cafeCampus.data.local.entity.Usuario

@Dao
interface UsuarioDao {

    @Query("SELECT * FROM usuarios WHERE correo = :correo AND clave = :clave LIMIT 1")
    suspend fun login(correo: String, clave: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE rol = :rol LIMIT 1")
    suspend fun porRol(rol: String): Usuario?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(u: Usuario)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertarNuevo(u: Usuario): Long

    @Query("SELECT COUNT(*) FROM usuarios WHERE correo = :correo")
    suspend fun existeCorreo(correo: String): Int

    @Query("SELECT COUNT(*) FROM usuarios")
    suspend fun contar(): Int
}
