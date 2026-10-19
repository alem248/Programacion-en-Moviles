package com.quispe.cafeCampus.model

import androidx.compose.ui.graphics.Color

enum class EstadoPedido(val titulo: String, val color: Color) {
    RECIBIDO("Recibido", Color(0xFF3B82F6)),
    PREPARANDO("Preparando", Color(0xFFF59E0B)),
    LISTO("Listo", Color(0xFF22C55E)),
    ENTREGADO("Entregado", Color(0xFF6B7280)),
    CANCELADO("Cancelado", Color(0xFFEF4444));

    fun siguiente(): EstadoPedido = when (this) {
        RECIBIDO -> PREPARANDO
        PREPARANDO -> LISTO
        LISTO -> ENTREGADO
        ENTREGADO -> this
        CANCELADO -> this
    }
}
