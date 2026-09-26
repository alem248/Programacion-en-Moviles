package com.quispe.clinicasalud.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.clinicasalud.modelos.EstadoCita
import com.quispe.clinicasalud.modelos.citasAgendadas

@Composable
fun ChipEstado(estado: EstadoCita) {
    // Colores fijos para distinguir estados sin depender del tema
    val colorTexto = when (estado) {
        EstadoCita.CONFIRMADA -> Color(0xFF1B7B4A)
        EstadoCita.COMPLETADA -> Color(0xFF5B6472)
    }
    val colorFondo = when (estado) {
        EstadoCita.CONFIRMADA -> Color(0xFFD7F5E3)
        EstadoCita.COMPLETADA -> Color(0xFFE5E8EE)
    }
    val texto = when (estado) {
        EstadoCita.CONFIRMADA -> "Confirmada"
        EstadoCita.COMPLETADA -> "Completada"
    }

    Text(
        text = texto,
        color = colorTexto,
        style = MaterialTheme.typography.labelMedium,
        modifier = Modifier
            .background(colorFondo, RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis citas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        if (citasAgendadas.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "No hay citas agendadas",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(citasAgendadas) { cita ->
                    val colorAcento = when (cita.estado) {
                        EstadoCita.CONFIRMADA -> MaterialTheme.colorScheme.primary
                        EstadoCita.COMPLETADA -> MaterialTheme.colorScheme.outline
                    }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .fillMaxHeight()
                                    .background(colorAcento)
                            )
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = cita.doctor.nombre,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "${cita.fecha}, ${cita.hora}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                                Row(modifier = Modifier.padding(top = 12.dp)) {
                                    ChipEstado(cita.estado)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
