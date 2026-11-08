package com.quispe.coderole.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.coderole.di.AppContainer
import com.quispe.coderole.domain.model.Rol
import com.quispe.coderole.domain.model.CapasPersonaje
import com.quispe.coderole.domain.model.MensajeConversacion
import com.quispe.coderole.ui.admin.AdminScreen
import com.quispe.coderole.ui.admin.AdminViewModel
import com.quispe.coderole.ui.components.ConCapasPersonaje
import com.quispe.coderole.ui.detail.DetalleScreen
import com.quispe.coderole.ui.detail.DetalleViewModel
import com.quispe.coderole.ui.lista.ListaViewModel
import com.quispe.coderole.ui.login.LoginScreen
import com.quispe.coderole.ui.login.LoginViewModel
import com.quispe.coderole.ui.perfil.PerfilScreen
import com.quispe.coderole.ui.perfil.PerfilViewModel
import com.quispe.coderole.ui.theme.CodeRoleTheme
import kotlinx.coroutines.flow.catch

private enum class Seccion { CHAT, FAVORITOS, PERFIL, ADMIN }

@Composable
fun CodeRoleApp(container: AppContainer) {
    val login: LoginViewModel = viewModel(factory = LoginViewModel.Factory(container.auth, container.actividad))
    val loginState by login.uiState.collectAsStateWithLifecycle()
    // Preferencia del modo oscuro: `null` = aún no eligió, así que se respeta
    // el tema del sistema hasta que el usuario la cambia desde el perfil.
    val temaElegido = container.tema.oscuro.collectAsStateWithLifecycle().value
    CodeRoleTheme(darkTheme = temaElegido ?: isSystemInDarkTheme()) {
        val sesion = loginState.sesion
        if (sesion == null) {
            if (loginState.cargando) Box(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            } else LoginScreen(login)
            return@CodeRoleTheme
        }

        val lista: ListaViewModel = viewModel(key = "lista-${sesion.uid}", factory = ListaViewModel.Factory(container.personajes))
        val detalle: DetalleViewModel = viewModel(
            key = "detalle-${sesion.uid}",
            factory = DetalleViewModel.Factory(container.personajes, container.chat, container.actividad),
        )
        // Foto y nombre en Firestore: el snapshot listener refleja cualquier
        // cambio en el chat, el perfil y el resto de pantallas al instante.
        val perfil: PerfilViewModel = viewModel(
            key = "perfil-${sesion.uid}",
            factory = PerfilViewModel.Factory(sesion.uid, sesion.correo, container.perfil, container.tema),
        )
        val listaState by lista.uiState.collectAsStateWithLifecycle()
        val favorites by lista.favoritos.collectAsStateWithLifecycle()
        val detalleState by detalle.uiState.collectAsStateWithLifecycle()
        val perfilState by perfil.uiState.collectAsStateWithLifecycle()
        var section by remember(sesion.uid) { mutableStateOf(Seccion.CHAT) }

        // Capas PNG del personaje: se escuchan una vez por sesión para que el
        // panel de administración refleje los cambios en tiempo real.
        var capas by remember(sesion.uid) { mutableStateOf(CapasPersonaje()) }
        LaunchedEffect(sesion.uid) {
            container.capas.observar().catch { }.collect { capas = it }
        }

        ConCapasPersonaje(capas) {
            Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).imePadding()) {
                Box(Modifier.weight(1f)) {
                    when (section) {
                        Seccion.CHAT -> DetalleScreen(
                            viewModel = detalle,
                            perfil = perfilState.perfil,
                            sinConexion = listaState.sinConexion,
                            onVolverAIntentar = { lista.sincronizar() },
                        )
                        Seccion.FAVORITOS -> FavoritesScreen(
                            respuestas = detalleState.conversacion.filter { it.favorita && !it.esUsuario },
                            mentoraGuardada = favorites.isNotEmpty(),
                            error = detalleState.errorConversacion,
                            quitarRespuesta = detalle::alternarRespuestaFavorita,
                        ) { section = Seccion.CHAT }
                        Seccion.PERFIL -> PerfilScreen(
                            viewModel = perfil,
                            sesion = sesion,
                            cerrarSesion = login::cerrarSesion,
                            abrirAdmin = {
                                if (sesion.rol == Rol.ADMIN) section = Seccion.ADMIN
                            },
                        )
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
        color = MaterialTheme.colorScheme.surface,
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
        Text(icono, color = if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.titleMedium)
        Text(titulo, color = if (seleccionada) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelSmall)
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
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Respuestas guardadas", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Tus explicaciones favoritas de la mentora", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 10.dp)) }
        if (respuestas.isEmpty() && !mentoraGuardada) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("☆", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.displayMedium)
                Text("Aún no guardaste respuestas", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                Text("En el chat, toca «Guardar respuesta» debajo de una explicación para tenerla a mano.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                TextButton(onClick = abrirChat, modifier = Modifier.padding(top = 10.dp)) { Text("Ir a la conversación", color = MaterialTheme.colorScheme.primary) }
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (respuestas.isEmpty()) {
                    item {
                        Text(
                            "Aún no guardaste explicaciones. En el chat puedes guardar cualquier respuesta de la mentora.",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(vertical = 8.dp),
                        )
                    }
                }
                items(respuestas, key = { it.id }) { respuesta ->
                    Surface(color = MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(20.dp), shadowElevation = 1.dp) {
                        Column(Modifier.fillMaxWidth().padding(16.dp)) {
                            Text(respuesta.texto, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { quitarRespuesta(respuesta) }, modifier = Modifier.align(Alignment.End)) {
                                Text("★ Quitar de guardadas", color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                if (mentoraGuardada) {
                    item {
                        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(18.dp)) {
                            Text("♥  También tienes a tu mentora marcada como favorita.", Modifier.fillMaxWidth().padding(14.dp), color = MaterialTheme.colorScheme.onSecondaryContainer)
                        }
                    }
                }
            }
        }
    }
}

