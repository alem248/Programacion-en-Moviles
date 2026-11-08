package com.quispe.tecsupfit.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.tecsupfit.ui.components.RutinaCard

private data class Rutina(val nombre: String, val dias: String, val detalle: String)

private val listaRutinas = listOf(
    Rutina("Rutina de fuerza", "Lunes, Miercoles y Viernes", "5 ejercicios de tren inferior y superior"),
    Rutina("Rutina de cardio", "Martes y Sabado", "20 min de trote continuo y intervalos de sprint"),
    Rutina("Movilidad", "Domingo", "15 min de estiramientos y respiracion")
)

@Composable
fun RutinasScreen(modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Mis rutinas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(listaRutinas) { rutina ->
                RutinaCard(
                    nombre = rutina.nombre,
                    dias = rutina.dias,
                    detalle = rutina.detalle
                )
            }
        }
    }
}
