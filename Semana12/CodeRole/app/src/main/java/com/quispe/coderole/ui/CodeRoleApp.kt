package com.quispe.coderole.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.quispe.coderole.domain.model.Sesion
import com.quispe.coderole.ui.admin.AdminScreen
import com.quispe.coderole.ui.admin.AdminViewModel
import com.quispe.coderole.ui.detail.DetalleScreen
import com.quispe.coderole.ui.detail.DetalleViewModel
import com.quispe.coderole.ui.lista.ListaViewModel
import com.quispe.coderole.ui.login.LoginScreen
import com.quispe.coderole.ui.login.LoginViewModel
import com.quispe.coderole.ui.theme.CodeRoleTheme

private enum class Seccion { CHAT, FAVORITOS, PERFIL, ADMIN }

@Composable
fun CodeRoleApp(container: AppContainer) {
    val login: LoginViewModel = viewModel(factory = LoginViewModel.Factory(container.auth))
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
        val detalle: DetalleViewModel = viewModel(key = "detalle-${sesion.uid}", factory = DetalleViewModel.Factory(container.personajes))
        val listaState by lista.uiState.collectAsStateWithLifecycle()
        val favorites by lista.favoritos.collectAsStateWithLifecycle()
        var section by remember(sesion.uid) { mutableStateOf(Seccion.CHAT) }

        Column(Modifier.fillMaxSize().background(Color(0xFFFFF2F1))) {
            Box(Modifier.weight(1f)) {
                when (section) {
                    Seccion.CHAT -> DetalleScreen(detalle, listaState.sinConexion) {
                        lista.sincronizar()
                        detalle.reintentarDetalle()
                    }
                    Seccion.FAVORITOS -> FavoritesScreen(favorites)
                    Seccion.PERFIL -> ProfileScreen(sesion, login::cerrarSesion) {
                        if (sesion.rol == Rol.ADMIN) section = Seccion.ADMIN
                    }
                    Seccion.ADMIN -> {
                        val admin: AdminViewModel = viewModel(key = "admin-${sesion.uid}", factory = AdminViewModel.Factory(container.personajes, container.notificador))
                        AdminScreen(admin)
                    }
                }
            }
            BottomMenu(sesion.rol == Rol.ADMIN, section, onSelect = { section = it })
        }
    }
}

@Composable
private fun BottomMenu(esAdmin: Boolean, actual: Seccion, onSelect: (Seccion) -> Unit) {
    Surface(color = Color.White, shadowElevation = 8.dp) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 3.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            MenuOpcion("⌂", "Inicio", actual == Seccion.CHAT) { onSelect(Seccion.CHAT) }
            MenuOpcion("♡", "Favoritos", actual == Seccion.FAVORITOS) { onSelect(Seccion.FAVORITOS) }
            MenuOpcion("◌", "Chat", actual == Seccion.CHAT) { onSelect(Seccion.CHAT) }
            MenuOpcion("♙", "Perfil", actual == Seccion.PERFIL) { onSelect(Seccion.PERFIL) }
            if (esAdmin) MenuOpcion("✚", "Admin", actual == Seccion.ADMIN) { onSelect(Seccion.ADMIN) }
        }
    }
}

@Composable
private fun MenuOpcion(icono: String, titulo: String, seleccionado: Boolean, onClick: () -> Unit) {
    TextButton(onClick = onClick) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(icono, color = if (seleccionado) Color(0xFFEE5268) else Color(0xFF633A50), style = MaterialTheme.typography.titleMedium)
            Text(titulo, color = if (seleccionado) Color(0xFFEE5268) else Color(0xFF633A50), style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
private fun FavoritesScreen(personajes: List<com.quispe.coderole.domain.model.Personaje>) {
    Column(Modifier.fillMaxSize().background(Color(0xFFFFF2F1)).padding(20.dp)) {
        Text("♥  Favoritos", color = Color(0xFF351F35), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        if (personajes.isEmpty()) {
            Text("Aún no tienes favoritos. Guarda a tu mentora para acceder más rápido.", color = Color(0xFF633A50), modifier = Modifier.padding(top = 20.dp))
        } else {
            Surface(Modifier.fillMaxWidth().padding(top = 16.dp), color = Color.White, shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.padding(18.dp)) {
                    Text("Tu mentora de programación", color = Color(0xFF351F35), fontWeight = FontWeight.Bold)
                    Text("Siempre contigo · aprende a tu ritmo", color = Color(0xFF633A50))
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
