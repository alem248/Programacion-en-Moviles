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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
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
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.ui.admin.AdminScreen
import com.quispe.coderole.ui.admin.AdminViewModel
import com.quispe.coderole.ui.components.ConCapasPersonaje
import com.quispe.coderole.ui.detail.DetalleScreen
import com.quispe.coderole.ui.detail.DetalleViewModel
import com.quispe.coderole.ui.detail.PersonajeDetalleScreen
import com.quispe.coderole.ui.lista.ListaScreen
import com.quispe.coderole.ui.lista.ListaViewModel
import com.quispe.coderole.ui.login.LoginScreen
import com.quispe.coderole.ui.login.LoginViewModel
import com.quispe.coderole.ui.perfil.PerfilScreen
import com.quispe.coderole.ui.perfil.PerfilViewModel
import com.quispe.coderole.ui.theme.CodeRoleTheme
import kotlinx.coroutines.flow.catch

private enum class Seccion { CATALOGO, DETALLE, CHAT, FAVORITOS, PERFIL, ADMIN }

@Composable
fun CodeRoleApp(container: AppContainer) {
    val login: LoginViewModel = viewModel(factory = LoginViewModel.Factory(container))
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

        val lista: ListaViewModel = viewModel(key = "lista-${sesion.uid}", factory = ListaViewModel.Factory(container))
        val detalle: DetalleViewModel = viewModel(
            key = "detalle-${sesion.uid}",
            factory = DetalleViewModel.Factory(container),
        )
        // Foto y nombre en Firestore: el snapshot listener refleja cualquier
        // cambio en el chat, el perfil y el resto de pantallas al instante.
        val perfil: PerfilViewModel = viewModel(
            key = "perfil-${sesion.uid}",
            factory = PerfilViewModel.Factory(container, sesion.uid, sesion.correo),
        )
        val listaState by lista.uiState.collectAsStateWithLifecycle()
        val personajes by lista.personajes.collectAsStateWithLifecycle()
        val detallePersonaje by lista.detalle.collectAsStateWithLifecycle()
        val favorites by lista.favoritos.collectAsStateWithLifecycle()
        val detalleState by detalle.uiState.collectAsStateWithLifecycle()
        val perfilState by perfil.uiState.collectAsStateWithLifecycle()
        var section by remember(sesion.uid) { mutableStateOf(Seccion.CATALOGO) }
        var personajeSeleccionado by remember(sesion.uid) { mutableStateOf<Int?>(null) }

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
                        Seccion.CATALOGO -> ListaScreen(
                            state = listaState,
                            personajes = personajes,
                            favoritos = favorites.map { it.id }.toSet(),
                            reintentar = lista::sincronizar,
                            abrirDetalle = { personaje ->
                                personajeSeleccionado = personaje.id
                                lista.cargarDetalle(personaje.id)
                                section = Seccion.DETALLE
                            },
                            alternarFavorito = { personaje -> lista.alternarFavorito(personaje.id) },
                        )
                        Seccion.DETALLE -> PersonajeDetalleScreen(
                            state = detallePersonaje,
                            favorito = favorites.any { it.id == personajeSeleccionado },
                            volver = { section = Seccion.CATALOGO },
                            reintentar = { personajeSeleccionado?.let(lista::cargarDetalle) },
                            alternarFavorito = { personaje -> lista.alternarFavorito(personaje.id) },
                            conversar = { section = Seccion.CHAT },
                        )
                        Seccion.CHAT -> DetalleScreen(
                            viewModel = detalle,
                            perfil = perfilState.perfil,
                            sinConexion = listaState.sinConexion,
                            onVolverAIntentar = { lista.sincronizar() },
                        )
                        Seccion.FAVORITOS -> FavoritesScreen(
                            respuestas = detalleState.conversacion.filter { it.favorita && !it.esUsuario },
                            personajesFavoritos = favorites,
                            error = detalleState.errorConversacion,
                            quitarRespuesta = detalle::alternarRespuestaFavorita,
                            abrirPersonaje = { personaje ->
                                personajeSeleccionado = personaje.id
                                lista.cargarDetalle(personaje.id)
                                section = Seccion.DETALLE
                            },
                            quitarFavorito = { personaje -> lista.alternarFavorito(personaje.id) },
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
                                factory = AdminViewModel.Factory(container),
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
        if (esAdmin && actual == Seccion.ADMIN) {
            FilledTonalButton(
                onClick = { onSelect(Seccion.CATALOGO) },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp).height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                ),
            ) {
                Text("◉  Vista previa de usuario", fontWeight = FontWeight.Bold)
            }
        } else {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
                OpcionNavegacion("♥", "Mentora", actual == Seccion.CHAT, Modifier.weight(1f)) { onSelect(Seccion.CHAT) }
                OpcionNavegacion("★", "Respuestas", actual == Seccion.FAVORITOS, Modifier.weight(1f)) { onSelect(Seccion.FAVORITOS) }
                OpcionNavegacion("♙", "Perfil", actual == Seccion.PERFIL, Modifier.weight(1f)) { onSelect(Seccion.PERFIL) }
                if (esAdmin) OpcionNavegacion("⚙", "Admin", actual == Seccion.ADMIN, Modifier.weight(1f)) { onSelect(Seccion.ADMIN) }
            }
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
    personajesFavoritos: List<Personaje>,
    error: String?,
    quitarRespuesta: (MensajeConversacion) -> Unit,
    abrirPersonaje: (Personaje) -> Unit,
    quitarFavorito: (Personaje) -> Unit,
    abrirChat: () -> Unit,
) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(horizontal = 20.dp, vertical = 16.dp)) {
        Text("Guardados", color = MaterialTheme.colorScheme.onBackground, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Text("Tus respuestas y elementos favoritos del catálogo", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 4.dp, bottom = 12.dp))
        error?.let { Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(bottom = 10.dp)) }
        if (respuestas.isEmpty() && personajesFavoritos.isEmpty()) {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                Text("☆", color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.displayMedium)
                Text("Aún no guardaste respuestas", color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 10.dp))
                Text("Guarda respuestas en el chat o marca un elemento del catálogo como favorito.", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(top = 6.dp))
                TextButton(onClick = abrirChat, modifier = Modifier.padding(top = 10.dp)) { Text("Ir a la conversación", color = MaterialTheme.colorScheme.primary) }
            }
        } else {
            LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                if (respuestas.isNotEmpty()) {
                    item { Text("Respuestas guardadas", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                }
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
                if (personajesFavoritos.isNotEmpty()) {
                    item { Text("Favoritos del catálogo", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) }
                }
                items(personajesFavoritos, key = { "personaje-${it.id}" }) { personaje ->
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(18.dp)) {
                        Row(Modifier.fillMaxWidth().padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f).clickable { abrirPersonaje(personaje) }) {
                                Text(personaje.nombre, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSecondaryContainer)
                                Text(personaje.subtitulo, color = MaterialTheme.colorScheme.onSecondaryContainer)
                            }
                            TextButton(onClick = { quitarFavorito(personaje) }) { Text("★", color = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }
    }
}
