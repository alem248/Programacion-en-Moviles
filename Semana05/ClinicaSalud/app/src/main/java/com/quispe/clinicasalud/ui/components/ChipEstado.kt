package com.quispe.clinicasalud.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quispe.clinicasalud.modelos.EstadoCita

@Composable
fun ChipEstado(
    estado: EstadoCita,
    modifier: Modifier = Modifier
) {
    // Seleccion dinamica de contenedor e icongrafia adaptada a M3 y modo oscuro
    val colorFondoTarget = when (estado) {
        EstadoCita.CONFIRMADA -> MaterialTheme.colorScheme.primaryContainer
        EstadoCita.COMPLETADA -> MaterialTheme.colorScheme.secondaryContainer
    }

    val colorTextoTarget = when (estado) {
        EstadoCita.CONFIRMADA -> MaterialTheme.colorScheme.onPrimaryContainer
        EstadoCita.COMPLETADA -> MaterialTheme.colorScheme.onSecondaryContainer
    }

    // Transicion suave de color para evitar cambios bruscos en cambios de estado
    val colorFondo by animateColorAsState(
        targetValue = colorFondoTarget,
        animationSpec = tween(durationMillis = 250),
        label = "colorFondoChip"
    )
    val colorTexto by animateColorAsState(
        targetValue = colorTextoTarget,
        animationSpec = tween(durationMillis = 250),
        label = "colorTextoChip"
    )

    val texto = when (estado) {
        EstadoCita.CONFIRMADA -> "Confirmada"
        EstadoCita.COMPLETADA -> "Completada"
    }

    Text(
        text = texto,
        color = colorTexto,
        style = MaterialTheme.typography.labelMedium,
        modifier = modifier
            .background(colorFondo, RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    )
}
