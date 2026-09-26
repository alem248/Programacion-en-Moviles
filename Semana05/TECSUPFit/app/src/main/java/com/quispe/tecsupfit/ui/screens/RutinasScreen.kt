package com.quispe.tecsupfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.tecsupfit.ui.theme.TextoPrincipal
import com.quispe.tecsupfit.ui.theme.TextoSecundario
import com.quispe.tecsupfit.ui.theme.VerdeClaro
import com.quispe.tecsupfit.ui.theme.VerdeOscuro

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
            color = TextoPrincipal,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )
        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(listaRutinas) { rutina ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(VerdeClaro, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.FitnessCenter,
                                contentDescription = null,
                                tint = VerdeOscuro
                            )
                        }
                        Column(modifier = Modifier.padding(start = 12.dp)) {
                            Text(
                                text = rutina.nombre,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextoPrincipal
                            )
                            Text(
                                text = rutina.dias,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextoSecundario
                            )
                            Text(
                                text = rutina.detalle,
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextoSecundario,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
