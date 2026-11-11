package com.quispe.coderole.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.ui.UiState

@Composable
fun PersonajeDetalleScreen(
    state: UiState<Personaje>,
    favorito: Boolean,
    volver: () -> Unit,
    reintentar: () -> Unit,
    alternarFavorito: (Personaje) -> Unit,
    conversar: () -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(20.dp)) {
        TextButton(onClick = volver) { Text("← Volver al catálogo") }
        when (state) {
            UiState.Cargando -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                CircularProgressIndicator()
                Text("Cargando detalle…", modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            is UiState.Error -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(state.mensaje, color = MaterialTheme.colorScheme.error)
                OutlinedButton(onClick = reintentar, modifier = Modifier.padding(top = 12.dp)) { Text("Reintentar") }
            }
            is UiState.Exito -> {
                val personaje = state.datos
                Card(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                ) {
                    Column(Modifier.padding(22.dp)) {
                        Text(personaje.nombre, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Text(personaje.subtitulo, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 4.dp))
                        Text(personaje.descripcion, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
                        Row(Modifier.fillMaxWidth().padding(top = 20.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedButton(onClick = { alternarFavorito(personaje) }, modifier = Modifier.weight(1f)) {
                                Text(if (favorito) "★ Quitar favorito" else "☆ Guardar favorito")
                            }
                            Button(onClick = conversar, modifier = Modifier.weight(1f)) { Text("Conversar") }
                        }
                    }
                }
            }
        }
    }
}
