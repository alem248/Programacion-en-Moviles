package com.quispe.coderole.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.coderole.di.AppContainer
import com.quispe.coderole.domain.model.Rol
import com.quispe.coderole.domain.model.CapasPersonaje
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.domain.model.Sesion
import com.quispe.coderole.ui.admin.AdminScreen
import com.quispe.coderole.ui.admin.AdminViewModel
import com.quispe.coderole.ui.components.ConCapasPersonaje
import com.quispe.coderole.ui.detail.DetalleScreen
import com.quispe.coderole.ui.detail.DetalleViewModel
import com.quispe.coderole.ui.lista.ListaViewModel
import com.quispe.coderole.ui.login.LoginScreen
import com.quispe.coderole.ui.login.LoginViewModel
import com.quispe.coderole.ui.theme.CodeRoleTheme
import kotlinx.coroutines.flow.catch

private enum class Seccion { CHAT, FAVORITOS, PERFIL, ADMIN }

@Composable
fun CodeRoleApp(container: AppContainer) {
    val login: LoginViewModel = viewModel(factory = LoginViewModel.Factory(container.auth, container.actividad))
    val loginState by login.uiState.collectAsStateWithLifecycle()
    CodeRoleTheme {
        val sesion = loginState.sesion
        if (sesion == null) {
            if (loginState.cargando) Box(Modifier.fillMaxSize().background(Color(0xFFFFF2F1)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFFEE5268))
            } else LoginScreen(login)
            return@CodeRoleTheme
        }

        val lista: ListaViewModel = viewModel(key = "lista-${sesion.uid}", factory = ListaViewModel.Factory(container.personajes))
        val detalle: DetalleViewModel = viewModel(
            key = "detalle-${sesion.uid}",
            factory = DetalleViewModel.Factory(container.personajes, container.chat, container.actividad),
        )
        val listaState by lista.uiState.collectAsStateWithLifecycle()
        val favorites by lista.favoritos.collectAsStateWithLifecycle()
        val detalleState by detalle.uiState.collectAsStateWithLifecycle()
        var section by remember(sesion.uid) { mutableStateOf(Seccion.CHAT) }

        // Capas PNG del personaje: se escuchan una vez por sesión para que el
        // panel de administración refleje los cambios en tiempo real.
        var capas by remember(sesion.uid) { mutableStateOf(CapasPersonaje()) }
        LaunchedEffect(sesion.uid) {
            container.capas.observar().catch { }.collect { capas = it }
        }

        ConCapasPersonaje(capas) {
            Column(Modifier.fillMaxSize().background(Color(0xFFFFF2F1)).imePadding()) {
                Box(Modifier.weight(1f)) {
                    when (section) {
                        Seccion.CHAT -> DetalleScreen(detalle, listaState.sinConexion) {
                            lista.sincronizar()
                        }
                        Seccion.FAVORITOS -> FavoritesScreen(
                            respuestas = detalleState.conversacion.filter { it.favorita && !it.esUsuario },
                            mentoraGuardada = favorites.isNotEmpty(),
                            error = detalleState.errorConversacion,
                            quitarRespuesta = detalle::alternarRespuestaFavorita,
                        ) { section = Seccion.CHAT }
                        Seccion.PERFIL -> ProfileScreen(sesion, login::cerrarSesion) {
                            if (sesion.rol == Rol.ADMIN) section = Seccion.ADMIN
                        }
                        Seccion.ADMIN -> {
                            val admin: AdminViewModel = viewModel(
                                key = "admin-${sesion.uid}",
                                factory = AdminViewModel.Factory(
                                    container.personajes,
                                    container.notificador,
                                    container.actividad,
                                    container.capas,
                                ),
                            )
                            AdminScreen(admin)
                        }
                    }
                }
                BottomMenu(sesion.rol == Rol.ADMIN, section, onSelect = { section = it })
            }
        }
    }
}

@Composable
private fun BottomMenu(esAdmin: Boolean, actual: Seccion, onSelect: (Seccion) -> Unit) {
    Surface(
        modifier = Modifier.navigationBarsPadding(),
        color = Color.White,
        shadowElevation = 8.dp,
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            OpcionNavegacion("♥", "Mentora", actual == Seccion.CHAT, Modifier.weight(1f)) { onSelect(Seccion.CHAT) }
            OpcionNavegacion("★", "Respuestas", actual == Seccion.FAVORITOS, Modifier.weight(1f)) { onSelect(Seccion.FAVORITOS) }
            OpcionNavegacion("♙", "Perfil", actual == Seccion.PERFIL, Modifier.weight(1f)) { onSelect(Seccion.PERFIL) }
            if (esAdmin) OpcionNavegacion("⚙", "Admin", actual == Seccion.ADMIN, Modifier.weight(1f)) { onSelect(Seccion.ADMIN) }
        }
    }
}

@Composable
private fun OpcionNavegacion(icono: String, titulo: String, seleccionada: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Column(
        modifier = modifier.clickable(onClick = onClick).padding(vertical = 9.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(icono, color = if (seleccionada) Color(0xFFEE5268) else Color(0xFF633A50), style = MaterialTheme.typography.titleMedium)
        Text(titulo, color = if (seleccionada) Color(0xFFEE5268) else Color(0xFF633A50), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun FavoritesScreen(
    respuestas: List<MensajeConversacion>,
    mentoraGuardada: Boolean,
    error: String?,
    quitarRespuesta: (MensajeConversacion) -> Unit,
    abrirChat: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(Color(0xFFFFF2F1)).padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Respuestas guardadas", color = Color(0xFF351F35), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Tus explicaciones favoritas de la mentora", color = Color(0xFF514151), modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
        error?.let { Text(it, color = Color(0xFF9B314B), modifier = Modifier.padding(bottom = 10.dp)) }
        if (respuestas.isEmpty() && !mentoraGuardada) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("☆", color = Color(0xFFEE5268), style = MaterialTheme.typography.displayMedium)
                Text("Aún no guardaste respuestas", color = Color(0xFF281A29), fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                Text("En el chat, toca «Guardar respuesta» debajo de una explicación para tenerla a mano.", color = Color(0xFF514151), modifier = Modifier.padding(top = 6.dp))
                TextButton(onClick = abrirChat, modifier = Modifier.padding(top = 10.dp)) { Text("Ir a la conversación", color = Color(0xFFB23D57)) }
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (respuestas.isEmpty()) {
                    item {
                        Text(
                            "Aún no guardaste explicaciones. En el chat puedes guardar cualquier respuesta de la mentora.",
                            color = Color(0xFF514151),
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }
                items(respuestas, key = { it.id }) { respuesta ->
                    Surface(color = Color.White, shape = RoundedCornerShape(20.dp), shadowElevation = 1.dp) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(respuesta.texto, color = Color(0xFF281A29), style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { quitarRespuesta(respuesta) }, modifier = Modifier.align(Alignment.End)) {
                                Text("★ Quitar de guardadas", color = Color(0xFF9B314B))
                            }
                        }
                    }
                }
                if (mentoraGuardada) {
                    item {
                        Surface(color = Color(0xFFFFE5E8), shape = RoundedCornerShape(18.dp)) {
                            Text("♥  También tienes a tu mentora marcada como favorita.", Modifier.fillMaxWidth().padding(14.dp), color = Color(0xFF351F35))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileScreen(sesion: Sesion, cerrarSesion: () -> Unit, abrirAdmin: () -> Unit) {
    Column(Modifier.fillMaxSize().background(Color(0xFFFFF2F1)).padding(20.dp)) {
        Text("Mi perfil", color = Color(0xFF351F35), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Surface(Modifier.fillMaxWidth().padding(top = 18.dp), color = Color.White, shape = RoundedCornerShape(24.dp)) {
            Column(Modifier.padding(20.dp)) {
                Text("CodeRole 🐾", color = Color(0xFFEE5268), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(sesion.correo, color = Color(0xFF351F35), modifier = Modifier.padding(top = 12.dp))
                Text("Rol: ${sesion.rol.name.lowercase().replaceFirstChar { it.uppercase() }}", color = Color(0xFF633A50), modifier = Modifier.padding(top = 4.dp))
            }
        }
        if (sesion.rol == Rol.ADMIN) TextButton(onClick = abrirAdmin, modifier = Modifier.padding(top = 10.dp)) { Text("Abrir panel Admin", color = Color(0xFFEE5268)) }
        TextButton(onClick = cerrarSesion, modifier = Modifier.padding(top = 10.dp)) { Text("⇥  Cerrar sesión", color = Color(0xFFB42342)) }
    }
}
