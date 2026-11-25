package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun PantallaDetalle(state: UiState<DetalleUi>, onVolver: () -> Unit, onReintentar: () -> Unit) {
    when (state) {
        UiState.Cargando -> Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) { CircularProgressIndicator() }
        is UiState.Error -> Column(Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {
            Text(state.mensaje)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onReintentar) { Text("Reintentar") }
            Button(onClick = onVolver) { Text("Volver") }
        }
        is UiState.Exito -> {
            val detalle = state.dato
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Button(onClick = onVolver) { Text("← Volver") }
                    Spacer(Modifier.height(12.dp))
                    Text("Publicación #${detalle.post.id} · Usuario ${detalle.post.userId}",
                        style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.height(8.dp))
                    Text(detalle.post.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    Text(detalle.post.body, style = MaterialTheme.typography.bodyLarge)
                    Spacer(Modifier.height(24.dp))
                    Text("Comentarios (${detalle.comentarios.size})", style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold)
                }
                items(detalle.comentarios, key = { it.id }) { comentario ->
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(comentario.name, style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold)
                            Text(comentario.email, style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(8.dp))
                            Text(comentario.body, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
