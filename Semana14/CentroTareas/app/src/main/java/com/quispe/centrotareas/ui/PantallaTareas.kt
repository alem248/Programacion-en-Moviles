package com.quispe.centrotareas.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.centrotareas.model.CargaUi
import com.quispe.centrotareas.model.PrimosUi
import com.quispe.centrotareas.viewmodel.TareasViewModel
import com.quispe.centrotareas.viewmodel.TareasViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaTareas(modifier: Modifier = Modifier) {
    val contexto = LocalContext.current
    val vm: TareasViewModel = viewModel(factory = TareasViewModelFactory(contexto))
    val mensaje by vm.mensaje.collectAsState()
    val progreso by vm.progreso.collectAsState()
    val corriendo by vm.corriendo.collectAsState()
    val primos by vm.primos.collectAsState()
    val carga by vm.carga.collectAsState()
    val simularError by vm.simularError.collectAsState()
    val historial by vm.historial.collectAsState()
    var toques by remember { mutableIntStateOf(0) }
    var nTexto by remember { mutableStateOf("1000") }

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
                    // Paso 1: prueba de vida y tarea larga (MAL)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { toques++ }) {
                            Text("Tócame: $toques")
                        }
                        Button(onClick = { vm.tareaLarga() }, enabled = !corriendo) {
                            Text("Iniciar")
                        }
                        OutlinedButton(onClick = { vm.cancelar() }, enabled = corriendo) {
                            Text("Cancelar")
                        }
                    }
                    LinearProgressIndicator(
                        progress = { progreso },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(text = "${(progreso * 100).toInt()} %")
                    Text(text = mensaje)
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
                    // Paso 5: cálculo pesado de números primos
                    OutlinedTextField(
                        value = nTexto,
                        onValueChange = { nTexto = it },
                        label = { Text("N") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                nTexto.toIntOrNull()
                                    ?.takeIf { it >= 2 }
                                    ?.let { vm.contar(it) }
                            },
                            enabled = primos !is PrimosUi.Calculando
                        ) {
                            Text("Contar")
                        }
                        OutlinedButton(
                            onClick = { vm.cancelarPrimos() },
                            enabled = primos is PrimosUi.Calculando
                        ) {
                            Text("Cancelar")
                        }
                    }
                    when (val p = primos) {
                        is PrimosUi.Listo -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2E7D32))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "${p.cantidad} primos · ${p.milisegundos} ms",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        PrimosUi.Calculando -> Text("Calculando...")
                        PrimosUi.Cancelado -> Text(
                            text = "Cancelado",
                            color = MaterialTheme.colorScheme.error
                        )

                        PrimosUi.Inicial -> Unit
                    }
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
                    // Paso 7: interruptor para provocar un error simulado
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Switch(
                            checked = simularError,
                            onCheckedChange = { vm.cambiarSimularError(it) }
                        )
                        Text("Simular error")
                    }
                    // Pasos 6 y 7: comparar carga secuencial contra paralela
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(
                            onClick = { vm.cargarUnaPorUna() },
                            enabled = carga !is CargaUi.Cargando
                        ) {
                            Text("Una por una")
                        }
                        Button(
                            onClick = { vm.cargarALaVez() },
                            enabled = carga !is CargaUi.Cargando
                        ) {
                            Text("A la vez")
                        }
                    }
                    when (val c = carga) {
                        is CargaUi.Cargando -> Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Text("Cargando...")
                        }

                        is CargaUi.Exito -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2E7D32))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = "${c.modo} · ${c.perfil} · " +
                                    "${c.pedidos} pedidos · ${c.milisegundos} ms",
                                color = Color.White,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        // Paso 7: error con recuadro rojo y botón de reintento
                        is CargaUi.Error -> Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.errorContainer)
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = c.mensaje,
                                    color = MaterialTheme.colorScheme.onErrorContainer
                                )
                            }
                            Button(onClick = { vm.cargarALaVez() }) {
                                Text("Reintentar")
                            }
                        }

                        CargaUi.Inicial -> Unit
                    }
                }
            }

            // ----- Tarjeta 4: Historial de resultados (Mejora 3)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Historial de resultados",
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    if (historial.isEmpty()) {
                        Text("Sin resultados todavía")
                    } else {
                        historial.forEach { entrada ->
                            Text(text = "• $entrada")
                        }
                    }
                }
            }
        }
    }
}
