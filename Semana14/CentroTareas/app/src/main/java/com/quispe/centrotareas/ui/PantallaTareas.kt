package com.quispe.centrotareas.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.centrotareas.viewmodel.TareasViewModel
import com.quispe.centrotareas.viewmodel.TareasViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(modifier: Modifier = Modifier) {
    val vm: TareasViewModel = viewModel(factory = TareasViewModelFactory())

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text("CentroTareas") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ----- Tarjeta 1: Tarea con progreso
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Tarea con progreso",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    // TODO Paso 1: botón Tócame y botón Tarea larga (MAL)
                    // TODO Pasos 2 a 4: botones Iniciar/Cancelar, progreso y mensaje
                }
            }

            // ----- Tarjeta 2: Contar números primos
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Contar números primos",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    // TODO Paso 5: campo numérico, botones Contar/Cancelar y resultado
                }
            }

            // ----- Tarjeta 3: Dos cargas
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Dos cargas",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    // TODO Pasos 6 y 7: botones de carga, interruptor de error y resultado
                }
            }
        }
    }
}
