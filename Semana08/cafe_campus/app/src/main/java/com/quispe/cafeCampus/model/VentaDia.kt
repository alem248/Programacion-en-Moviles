package com.quispe.cafeCampus.model

data class VentaDia(
    val dia: String,          // e.g. "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"
    val diaLargo: String,     // e.g. "Lunes", "Martes", "Miércoles"...
    val total: Double,        // Monto total vendido en S/
    val esHoy: Boolean = false
)

data class VentasPorDiaUiState(
    val cargando: Boolean = true,
    val ventas: List<VentaDia> = emptyList(),
    val totalSemanal: Double = 0.0,
    val diaMaximo: String = ""
)
