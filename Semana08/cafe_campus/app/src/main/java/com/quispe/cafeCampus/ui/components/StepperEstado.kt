package com.quispe.cafeCampus.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.EstadoPedido

private val pasos = listOf(
    EstadoPedido.RECIBIDO,
    EstadoPedido.PREPARANDO,
    EstadoPedido.LISTO,
    EstadoPedido.ENTREGADO
)

private val descripciones = mapOf(
    EstadoPedido.RECIBIDO to "Tu pedido llegó a la cocina",
    EstadoPedido.PREPARANDO to "Está cocinando tu pedido",
    EstadoPedido.LISTO to "Puedes pasar a recogerlo",
    EstadoPedido.ENTREGADO to "¡Buen provecho!"
)

@Composable
fun StepperEstado(
    estadoActual: EstadoPedido,
    modifier: Modifier = Modifier
) {
    val indiceActual = pasos.indexOf(estadoActual)
    val verdeCompletado = Color(0xFF22C55E)

    Column(modifier = modifier.fillMaxWidth()) {
        pasos.forEachIndexed { indice, paso ->
            val completado = indice < indiceActual ||
                    estadoActual == EstadoPedido.ENTREGADO && indice <= indiceActual
            val actual = indice == indiceActual && estadoActual != EstadoPedido.CANCELADO

            // Animación del círculo: crece suavemente cuando es el paso actual.
            val escala by animateFloatAsState(
                targetValue = if (actual) 1.15f else 1f,
                animationSpec = tween(durationMillis = 350),
                label = "escalaPaso"
            )

            // Animación de color al completar/activar un paso.
            val colorCirculo by animateColorAsState(
                targetValue = when {
                    completado -> verdeCompletado
                    actual -> paso.color
                    else -> MaterialTheme.colorScheme.outlineVariant
                },
                animationSpec = tween(durationMillis = 350),
                label = "colorCirculo"
            )
            val colorConector by animateColorAsState(
                targetValue = if (completado) verdeCompletado
                else MaterialTheme.colorScheme.outlineVariant,
                animationSpec = tween(durationMillis = 350),
                label = "colorConector"
            )

            Row(verticalAlignment = Alignment.Top) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Círculo
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .scale(escala)
                            .clip(CircleShape)
                            .background(colorCirculo),
                        contentAlignment = Alignment.Center
                    ) {
                        if (completado) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    // Conector
                    if (indice < pasos.lastIndex) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(36.dp)
                                .background(colorConector)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.padding(bottom = if (indice < pasos.lastIndex) 8.dp else 0.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = paso.titulo,
                        fontWeight = if (actual || completado) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 15.sp,
                        color = if (actual || completado) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    Text(
                        text = descripciones[paso] ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
