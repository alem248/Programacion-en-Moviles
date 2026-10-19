package com.quispe.cafeCampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "productos")
data class Producto(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val nombre: String,
    val descripcion: String,
    val categoria: String,
    val precio: Double,
    val disponible: Boolean = true,
    val icono: String
)
