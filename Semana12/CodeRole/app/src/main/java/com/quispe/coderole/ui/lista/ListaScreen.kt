package com.quispe.coderole.ui.lista

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.ui.UiState

@Composable
fun ListaScreen(
    state: ListaUiState,
    personajes: List<Personaje>,
    favoritos: Set<Int>,
    reintentar: () -> Unit,
    abrirDetalle: (Personaje) -> Unit,
    alternarFavorito: (Personaje) -> Unit,
) {
    Column(Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Catálogo", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text(
            "Explora el perfil de tu mentora y abre una conversación.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )
        state.mensaje?.let { aviso ->
            Text(aviso, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 10.dp))
        }

        when (val contenido = state.estado) {
            UiState.Cargando -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                androidx.compose.material3.CircularProgressIndicator()
                Text("Cargando catálogo…", modifier = Modifier.padding(top = 12.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            is UiState.Error -> Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(contenido.mensaje, color = MaterialTheme.colorScheme.error)
                Button(onClick = reintentar, modifier = Modifier.padding(top = 12.dp)) { Text("Reintentar") }
            }
            is UiState.Exito -> if (personajes.isEmpty()) {
                Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("El catálogo está vacío", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("Sincroniza para volver a cargar los datos.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                    Button(onClick = reintentar, modifier = Modifier.padding(top = 12.dp)) { Text("Sincronizar") }
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(personajes, key = { it.id }) { personaje ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { abrirDetalle(personaje) },
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        ) {
                            Row(
                                Modifier.fillMaxWidth().padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(personaje.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(personaje.subtitulo, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
                                }
                                TextButton(onClick = { alternarFavorito(personaje) }) {
                                    Text(if (personaje.id in favoritos) "★ Guardado" else "☆ Favorito")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
