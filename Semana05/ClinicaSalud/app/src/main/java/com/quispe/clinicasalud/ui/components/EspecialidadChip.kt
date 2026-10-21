package com.quispe.clinicasalud.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.quispe.clinicasalud.modelos.Especialidad

@Composable
fun EspecialidadChip(
    especialidad: Especialidad,
    seleccionada: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animacion de escala con efecto rebote suave al seleccionar
    val escala by animateFloatAsState(
        targetValue = if (seleccionada) 1.05f else 1.0f,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 400f),
        label = "escalaEspecialidad"
    )

    // Animacion de color de contenedor y texto para soporte dinamico de M3
    val containerColor by animateColorAsState(
        targetValue = if (seleccionada) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.surfaceContainerHigh
        },
        animationSpec = tween(durationMillis = 200),
        label = "containerColorEspecialidad"
    )

    val labelColor by animateColorAsState(
        targetValue = if (seleccionada) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onSurface
        },
        animationSpec = tween(durationMillis = 200),
        label = "labelColorEspecialidad"
    )

    FilterChip(
        selected = seleccionada,
        onClick = onSelect,
        modifier = modifier.scale(escala),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = containerColor,
            labelColor = labelColor,
            selectedContainerColor = containerColor,
            selectedLabelColor = labelColor
        ),
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(especialidad.imagenRes),
                    contentDescription = especialidad.nombre,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = especialidad.nombre,
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    )
}
