package com.quispe.coderole.ui.detail

import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.domain.model.PerfilUsuario
import com.quispe.coderole.ui.UiState
import com.quispe.coderole.ui.components.FotoPerfil
import com.quispe.coderole.ui.components.ModularCharacterView
import com.quispe.coderole.ui.theme.colorExito

private val FondoRosado: Color @Composable get() = MaterialTheme.colorScheme.background
private val Rosa: Color @Composable get() = MaterialTheme.colorScheme.primary
private val Tinta: Color @Composable get() = MaterialTheme.colorScheme.onBackground
private val TintaSecundaria: Color @Composable get() = MaterialTheme.colorScheme.onSurfaceVariant

@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    perfil: PerfilUsuario?,
    sinConexion: Boolean,
    onVolverAIntentar: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listaConversacion = rememberLazyListState()

    // Contador de tiempo de uso activo en la pantalla de la mentora
    LaunchedEffect(Unit) {
        var segundosActivos = 0
        while (isActive) {
            delay(1_000L)
            segundosActivos++
            if (segundosActivos >= 60) {
                viewModel.registrarTiempoActivo(1)
                segundosActivos = 0
            }
        }
    }

    LaunchedEffect(state.conversacion.size, state.enviando, state.errorConversacion, state.respuestaParcial) {
        val mensajes = state.conversacion.size.coerceAtLeast(1)
        val extras = (if (state.enviando) 1 else 0) + (if (state.errorConversacion != null) 1 else 0)
        listaConversacion.animateScrollToItem((mensajes + extras - 1).coerceAtLeast(0))
    }

    Column(Modifier.fillMaxSize().background(FondoRosado)) {
        // 1. Barra Superior Fija
        Row(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = .95f))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f).padding(start = 6.dp)) {
                Text(
                    text = "Tu mentora",
                    style = MaterialTheme.typography.titleLarge,
                    color = Tinta,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "●  Siempre contigo · programación",
                    style = MaterialTheme.typography.labelMedium,
                    color = colorExito(),
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val nombre = perfil?.nombreVisible
                if (!nombre.isNullOrBlank()) {
                    Text(
                        text = nombre,
                        style = MaterialTheme.typography.labelMedium,
                        color = TintaSecundaria,
                        maxLines = 1,
                    )
                }
                FotoPerfil(perfil, 32.dp)
            }
            TextButton(onClick = viewModel::alternarFavorito) {
                Text(
                    text = if (state.personaje?.esFavorito == true) "♥" else "♡",
                    color = Rosa,
                    style = MaterialTheme.typography.headlineMedium,
                )
            }
        }

        if (sinConexion) {
            Surface(color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "No se sincronizó el perfil de referencia · se conserva la copia local",
                        modifier = Modifier.weight(1f),
                        color = Tinta,
                        style = MaterialTheme.typography.labelMedium,
                    )
                    TextButton(onClick = onVolverAIntentar) { Text("Reintentar", color = Rosa) }
                }
            }
        }

        when (val estado = state.estado) {
            UiState.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Rosa) }
            is UiState.Error -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(estado.mensaje, color = Tinta)
                Button(onClick = onVolverAIntentar, colors = ButtonDefaults.buttonColors(containerColor = Rosa)) { Text("Reintentar") }
            }
            is UiState.Exito -> {
                // 2. Contenedor Fijo / Sticky del Personaje (Mentora)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.personaje?.nombre ?: "Mentora CodeRole",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Tinta,
                            )
                            Text(
                                text = when {
                                    state.enviando -> "Pensando respuesta…"
                                    state.textoMensaje.isNotBlank() -> "Escuchándote…"
                                    else -> "En línea para resolver tus dudas"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.enviando) Rosa else colorExito(),
                                modifier = Modifier.padding(top = 2.dp),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(width = 110.dp, height = 138.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.BottomCenter,
                        ) {
                            ModularCharacterView(
                                emotion = state.emocion,
                                modifier = Modifier.size(width = 100.dp, height = 128.dp),
                                isSpeaking = state.estaHablando,
                            )
                        }
                    }
                }

                // 3. Área de Conversación del Chat (Desplazable / Scrollable independientemente)
                val mensajesVisibles = state.conversacion.ifEmpty {
                    listOf(
                        MensajeConversacion(
                            id = "bienvenida",
                            texto = "¡Hola! Soy tu mentora de programación. ¿Qué te gustaría aprender o consultar hoy?",
                            esUsuario = false,
                            creadoEn = 0L,
                        ),
                    )
                }

                LazyColumn(
                    state = listaConversacion,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    items(mensajesVisibles, key = { it.id }) { mensaje ->
                        MensajeBubble(mensaje) { viewModel.alternarRespuestaFavorita(mensaje) }
                    }
                    if (state.enviando) {
                        item {
                            val parcial = state.respuestaParcial
                            if (parcial.isNullOrBlank()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(vertical = 4.dp),
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Rosa,
                                        strokeWidth = 2.dp,
                                    )
                                    Text(
                                        "Tu mentora está pensando la respuesta…",
                                        color = TintaSecundaria,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                }
                            } else {
                                MensajeBubble(
                                    mensaje = MensajeConversacion(
                                        id = "parcial",
                                        texto = parcial,
                                        esUsuario = false,
                                        creadoEn = 0L,
                                    ),
                                    onToggleFavorite = {},
                                )
                            }
                        }
                    }
                    state.errorConversacion?.let { error ->
                        item {
                            Surface(
                                color = MaterialTheme.colorScheme.errorContainer,
                                shape = RoundedCornerShape(16.dp),
                            ) {
                                Text(
                                    text = error,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    color = MaterialTheme.colorScheme.onErrorContainer,
                                    style = MaterialTheme.typography.bodySmall,
                                )
                            }
                        }
                    }
                }

                // 4. Barra Inferior Fija de Entrada de Texto
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = state.textoMensaje,
                        onValueChange = viewModel::cambiarTexto,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe tu mensaje…") },
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 3,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                        keyboardActions = KeyboardActions(onSend = { viewModel.enviarMensaje() }),
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Tinta,
                            unfocusedTextColor = Tinta,
                            focusedBorderColor = Rosa,
                            unfocusedBorderColor = TintaSecundaria,
                            focusedPlaceholderColor = TintaSecundaria,
                            unfocusedPlaceholderColor = TintaSecundaria,
                            cursorColor = Rosa,
                        ),
                    )
                    Button(
                        onClick = viewModel::enviarMensaje,
                        enabled = state.textoMensaje.isNotBlank() && !state.enviando,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Rosa),
                    ) { Text("➤", color = MaterialTheme.colorScheme.onPrimary) }
                }
            }
        }
    }
}

@Composable
private fun MensajeBubble(mensaje: MensajeConversacion, onToggleFavorite: () -> Unit) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = if (mensaje.esUsuario) Arrangement.End else Arrangement.Start) {
        Column(Modifier.fillMaxWidth(.9f), horizontalAlignment = if (mensaje.esUsuario) Alignment.End else Alignment.Start) {
            Surface(
                color = if (mensaje.esUsuario) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(if (mensaje.esUsuario) 22.dp else 6.dp, 22.dp, 22.dp, 22.dp),
                shadowElevation = 1.dp,
            ) {
                Text(mensaje.texto, Modifier.padding(horizontal = 15.dp, vertical = 12.dp), color = Tinta, style = MaterialTheme.typography.bodyMedium)
            }
            if (!mensaje.esUsuario && mensaje.id != "bienvenida" && mensaje.id != "parcial") {
                TextButton(onClick = onToggleFavorite) {
                    Text(
                        text = if (mensaje.favorita) "★ Respuesta guardada" else "☆ Guardar respuesta",
                        color = if (mensaje.favorita) Rosa else TintaSecundaria,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}
