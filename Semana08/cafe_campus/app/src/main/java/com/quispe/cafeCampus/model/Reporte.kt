package com.quispe.cafeCampus.model

data class ProductoVendido(
    val nombre: String,
    val cantidadTotal: Int
)

data class TotalPorPago(
    val pago: String,
    val total: Double
)
