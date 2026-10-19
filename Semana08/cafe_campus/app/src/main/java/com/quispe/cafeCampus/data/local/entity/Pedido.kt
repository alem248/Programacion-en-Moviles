package com.quispe.cafeCampus.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "pedidos",
    foreignKeys = [
        ForeignKey(
            entity = Usuario::class,
            parentColumns = ["id"],
            childColumns = ["clienteId"],
            onDelete = ForeignKey.NO_ACTION
        )
    ],
    indices = [Index(value = ["clienteId"])]
)
data class Pedido(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "clienteId")
    val clienteId: Int,
    val estado: String,
    val horaRecojo: String,
    val pago: String,
    val nota: String,
    val total: Double,
    val fecha: Long = System.currentTimeMillis()
)
