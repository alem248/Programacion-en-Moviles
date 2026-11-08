package com.quispe.clinicasalud.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp

@Composable
fun HorarioFilterChip(
    texto: String,
    seleccionado: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Escala animada con resorte para retroalimentacion tactil limpia
    val escala by animateFloatAsState(
        targetValue = if (seleccionado) 1.06f else 1.0f,
        animationSpec = spring(dampingRatio = 0.5f, stiffness = 500f),
        label = "escalaHorario"
    )

    // Interpolacion de color acorde a Material 3 y modo oscuro
    val colorFondo by animateColorAsState(
        targetValue = if (seleccionado) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerLow
        },
        animationSpec = tween(durationMillis = 200),
        label = "colorFondoHorario"
    )

    val colorTexto by animateColorAsState(
        targetValue = if (seleccionado) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 200),
        label = "colorTextoHorario"
    )

    FilterChip(
        selected = seleccionado,
        onClick = onSelect,
        modifier = modifier
            .scale(escala)
            .padding(2.dp),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = colorFondo,
            labelColor = colorTexto,
            selectedContainerColor = colorFondo,
            selectedLabelColor = colorTexto
        ),
        label = {
            Text(
                text = texto,
                style = MaterialTheme.typography.labelLarge
            )
        }
    )
}
