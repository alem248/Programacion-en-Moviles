package com.quispe.coderole.ui.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quispe.coderole.domain.model.CharacterEmotion
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.ui.UiState
import com.quispe.coderole.ui.components.ModularCharacterView

private val FondoRosado = Color(0xFFFFF2F1)
private val Rosa = Color(0xFFEE5268)
private val Tinta = Color(0xFF351F35)
private val TintaSecundaria = Color(0xFF514151)

@Composable
fun DetalleScreen(
    viewModel: DetalleViewModel,
    sinConexion: Boolean,
    onVolverAIntentar: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val listaConversacion = rememberLazyListState()
    LaunchedEffect(state.conversacion.size, state.enviando, state.errorConversacion, state.respuestaParcial) {
        val mensajes = state.conversacion.size.coerceAtLeast(1)
        val extras = (if (state.enviando) 1 else 0) + (if (state.errorConversacion != null) 1 else 0)
        listaConversacion.animateScrollToItem(1 + mensajes + extras)
    }
    Column(Modifier.fillMaxSize().background(FondoRosado)) {
        Row(
            Modifier.fillMaxWidth().background(Color.White.copy(alpha = .88f)).padding(horizontal = 18.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(Modifier.weight(1f).padding(start = 10.dp)) {
                Text("Tu mentora", style = MaterialTheme.typography.titleLarge, color = Tinta, fontWeight = FontWeight.Bold)
                Text("●  Siempre contigo · programación", style = MaterialTheme.typography.labelMedium, color = Color(0xFF4B9C77))
            }
            TextButton(onClick = viewModel::alternarFavorito) {
                Text(if (state.personaje?.esFavorito == true) "♥" else "♡", color = Rosa, style = MaterialTheme.typography.headlineMedium)
            }
        }

        if (sinConexion) {
            Surface(color = Color(0xFFFFE0E3), modifier = Modifier.fillMaxWidth()) {
                Row(Modifier.padding(horizontal = 16.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("No se sincronizó el perfil de referencia · se conserva la copia local", Modifier.weight(1f), color = Tinta, style = MaterialTheme.typography.labelMedium)
                    TextButton(onClick = onVolverAIntentar) { Text("Reintentar", color = Rosa) }
                }
            }
        }

        when (val estado = state.estado) {
            UiState.Cargando -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Rosa) }
            is UiState.Error -> Column(Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
                Text(estado.mensaje, color = Tinta)
                Button(onClick = onVolverAIntentar, colors = ButtonDefaults.buttonColors(containerColor = Rosa)) { Text("Reintentar") }
            }
            is UiState.Exito -> {
                LazyColumn(
                    state = listaConversacion,
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(28.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Box(Modifier.fillMaxWidth().height(214.dp).background(Color(0xFFFFD9DD)), contentAlignment = Alignment.BottomCenter) {
                                    ModularCharacterView(state.emocion, Modifier.size(width = 190.dp, height = 238.dp))
                                }
                                Column(Modifier.fillMaxWidth().padding(16.dp)) {
                                    Text(state.personaje?.nombre ?: "Tu mentora de programación", color = Tinta, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleLarge)
                                    Text("Un personaje que te acompaña en tu aprendizaje de programación.", color = Tinta, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(top = 4.dp))
                                    Spacer(Modifier.height(12.dp))
                                    Text("¿Qué puedes hacer?", color = Tinta, fontWeight = FontWeight.Bold)
                                    Text("✓  Consultar consejos y tipos de datos\n✓  Resolver dudas de código\n✓  Guardar a tu mentora en Favoritos", color = Tinta, modifier = Modifier.padding(top = 6.dp))
                                }
                            }
                        }
                    }
                    item { Text("Conversación", color = Tinta, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium) }
                    val mensajesVisibles = state.conversacion.ifEmpty {
                        listOf(MensajeConversacion("bienvenida", "¡Hola! Soy tu mentora de programación. ¿Qué te gustaría aprender hoy?", false, 0L))
                    }
                    items(mensajesVisibles, key = { it.id }) { mensaje ->
                        MensajeBubble(mensaje) { viewModel.alternarRespuestaFavorita(mensaje) }
                    }
                    if (state.enviando) item {
                        val parcial = state.respuestaParcial
                        if (parcial.isNullOrBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                CircularProgressIndicator(Modifier.size(16.dp), color = Rosa, strokeWidth = 2.dp)
                                Text("Tu mentora está pensando…", color = Tinta, style = MaterialTheme.typography.bodySmall)
                            }
                        } else {
                            MensajeBubble(MensajeConversacion(id = "parcial", texto = parcial, esUsuario = false, creadoEn = 0L)) { }
                        }
                    }
                    state.errorConversacion?.let { error ->
                        item {
                            Surface(color = Color(0xFFFFE0E3), shape = RoundedCornerShape(16.dp)) {
                                Text(error, Modifier.fillMaxWidth().padding(12.dp), color = Color(0xFF9B2440), style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
                Row(
                    Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 12.dp, vertical = 10.dp),
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
                    ) { Text("➤", color = Color.White) }
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
                color = if (mensaje.esUsuario) Color(0xFFFFC3CC) else Color.White,
                shape = RoundedCornerShape(if (mensaje.esUsuario) 22.dp else 6.dp, 22.dp, 22.dp, 22.dp),
                shadowElevation = 1.dp,
            ) {
                Text(mensaje.texto, Modifier.padding(horizontal = 15.dp, vertical = 12.dp), color = Tinta, style = MaterialTheme.typography.bodyMedium)
            }
            if (!mensaje.esUsuario && mensaje.id != "bienvenida" && mensaje.id != "parcial") {
                TextButton(onClick = onToggleFavorite) {
                    Text(
                        if (mensaje.favorita) "★ Respuesta guardada" else "☆ Guardar respuesta",
                        color = if (mensaje.favorita) Rosa else TintaSecundaria,
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
        }
    }
}
