package com.quispe.clinicasalud.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.clinicasalud.modelos.Cita
import com.quispe.clinicasalud.modelos.citasAgendadas
import com.quispe.clinicasalud.modelos.doctorPorId
import com.quispe.clinicasalud.modelos.listaFechas
import com.quispe.clinicasalud.modelos.listaHorarios
import androidx.compose.foundation.Image

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AgendarCitaScreen(
    doctorId: Int,
    onBack: () -> Unit,
    onConfirmada: () -> Unit
) {
    val doctor = doctorPorId(doctorId)
    var fechaSeleccionada by remember { mutableStateOf(listaFechas.first()) }
    var horaSeleccionada by remember { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Agendar cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Image(
                painter = painterResource(doctor.imagenRes),
                contentDescription = doctor.nombre,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = doctor.nombre,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = 12.dp)
            )
            Text(
                text = doctor.especialidad,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Selecciona fecha",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listaFechas.forEach { fecha ->
                    FilterChip(
                        selected = fecha == fechaSeleccionada,
                        onClick = { fechaSeleccionada = fecha },
                        label = { Text(fecha) }
                    )
                }
            }

            Text(
                text = "Selecciona hora",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 24.dp, bottom = 8.dp)
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listaHorarios.forEach { hora ->
                    FilterChip(
                        selected = hora == horaSeleccionada,
                        onClick = {
                            horaSeleccionada = if (hora == horaSeleccionada) null else hora
                        },
                        label = { Text(hora) }
                    )
                }
            }

            Text(
                text = horaSeleccionada?.let { "Seleccion: $fechaSeleccionada - $it" }
                    ?: "Seleccione un horario para continuar",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 24.dp)
            )

            Button(
                onClick = {
                    val hora = horaSeleccionada ?: return@Button
                    citasAgendadas.add(Cita(doctor, fechaSeleccionada, hora))
                    onConfirmada()
                },
                enabled = horaSeleccionada != null,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp)
            ) {
                Text("Confirmar cita")
            }
        }
    }
}
