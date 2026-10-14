package com.quispe.cafeCampus.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pedidos")
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val fecha: Long,
    val estado: String,
    val total: Double
)