package com.quispe.tecsupfit.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.tecsupfit.modelos.clasesDisponibles
import com.quispe.tecsupfit.ui.components.ClaseCard
import com.quispe.tecsupfit.ui.components.HeaderSeccion

private val filtros = listOf("Hoy", "Esta semana", "Proximos dias")

@Composable
fun InicioScreen(
    onClaseClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var filtroActual by remember { mutableStateOf(filtros.first()) }

    Column(modifier = modifier.fillMaxSize()) {
        HeaderSeccion(
            titulo = "TECSUP Fit",
            subtitulo = "Hola, Alexandra"
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtros) { filtro ->
                val seleccionado = filtro == filtroActual

                // Animacion de color de fondo y texto al cambiar de filtro
                val fondoFiltro by animateColorAsState(
                    targetValue = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    animationSpec = tween(durationMillis = 200),
                    label = "FondoFiltroColor"
                )

                val textoFiltro by animateColorAsState(
                    targetValue = if (seleccionado) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = tween(durationMillis = 200),
                    label = "TextoFiltroColor"
                )

                val bordeFiltro by animateColorAsState(
                    targetValue = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                    animationSpec = tween(durationMillis = 200),
                    label = "BordeFiltroColor"
                )

                Box(
                    modifier = Modifier
                        .background(
                            color = fondoFiltro,
                            shape = RoundedCornerShape(50)
                        )
                        .border(
                            width = 1.dp,
                            color = bordeFiltro,
                            shape = RoundedCornerShape(50)
                        )
                        .clickable { filtroActual = filtro }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = filtro,
                        color = textoFiltro,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Clases disponibles",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                )
            }
            items(clasesDisponibles, key = { it.id }) { clase ->
                ClaseCard(clase = clase, onClick = { onClaseClick(clase.id) })
            }
        }
    }
}
