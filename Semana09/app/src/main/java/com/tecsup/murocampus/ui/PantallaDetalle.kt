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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.viewmodel.DetalleUi

@Composable
fun PantallaDetalle(
    state: UiState<DetalleUi>,
    actualizacion: UiState<Post>?,
    eliminacion: UiState<Unit>?,
    onVolver: () -> Unit,
    onReintentar: () -> Unit,
    onEditarTitulo: (String) -> Unit,
    onLimpiarResultadoActualizacion: () -> Unit,
    onEliminar: (Int) -> Unit,
) {
    var mostrarConfirmacion by rememberSaveable { mutableStateOf(false) }

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
            var editando by rememberSaveable(detalle.post.id) { mutableStateOf(false) }
            var titulo by rememberSaveable(detalle.post.id, detalle.post.title) {
                mutableStateOf(detalle.post.title)
            }
            LaunchedEffect(actualizacion) {
                if (actualizacion is UiState.Exito) editando = false
            }

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
                    Spacer(Modifier.height(12.dp))

                    if (editando) {
                        OutlinedTextField(
                            value = titulo,
                            onValueChange = { titulo = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Nuevo título") },
                            singleLine = true,
                        )
                        Button(
                            onClick = { onEditarTitulo(titulo) },
                            enabled = titulo.isNotBlank() && actualizacion !is UiState.Cargando,
                        ) { Text("Guardar título") }
                        TextButton(onClick = { editando = false }) { Text("Cancelar edición") }
                    } else {
                        TextButton(onClick = {
                            onLimpiarResultadoActualizacion()
                            editando = true
                        }, enabled = eliminacion !is UiState.Cargando && actualizacion !is UiState.Cargando) {
                            Text("Editar título")
                        }
                    }

                    when (actualizacion) {
                        UiState.Cargando -> CircularProgressIndicator()
                        is UiState.Error -> Text(actualizacion.mensaje, color = MaterialTheme.colorScheme.error)
                        is UiState.Exito -> Text(
                            "Título actualizado. Respuesta del servidor: ${actualizacion.dato.title}",
                            color = MaterialTheme.colorScheme.primary,
                        )
                        null -> Unit
                    }

                    TextButton(
                        onClick = { mostrarConfirmacion = true },
                        enabled = eliminacion !is UiState.Cargando && actualizacion !is UiState.Cargando,
                    ) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
                    when (eliminacion) {
                        UiState.Cargando -> CircularProgressIndicator()
                        is UiState.Error -> Text(eliminacion.mensaje, color = MaterialTheme.colorScheme.error)
                        else -> Unit
                    }

                    Spacer(Modifier.height(12.dp))
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

            if (mostrarConfirmacion) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacion = false },
                    title = { Text("Eliminar publicación") },
                    text = { Text("¿Quieres eliminar la publicación #${detalle.post.id}?") },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                mostrarConfirmacion = false
                                onEliminar(detalle.post.id)
                            },
                        ) { Text("Eliminar") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacion = false }) { Text("Cancelar") }
                    },
                )
            }
        }
    }
}
