package com.quispe.coderole.ui.perfil

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quispe.coderole.domain.model.Rol
import com.quispe.coderole.domain.model.Sesion
import com.quispe.coderole.ui.components.FotoPerfil
import com.quispe.coderole.ui.theme.colorExito

/**
 * Pantalla de perfil: foto, nombre de usuario, cierre de sesión, acceso al
 * panel de administración y conmutador del modo oscuro.
 */
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel,
    sesion: Sesion,
    cerrarSesion: () -> Unit,
    abrirAdmin: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val perfil = state.perfil
    val contexto = LocalContext.current
    val temaSistema = isSystemInDarkTheme()
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.cambiarFoto(uri, contexto)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
    ) {
        Text(
            "Mi perfil",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Surface(
            Modifier.fillMaxWidth().padding(top = 18.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp),
        ) {
            Column(Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Box {
                        FotoPerfil(perfil, 92.dp)
                        Box(
                            Modifier
                                .align(Alignment.BottomEnd)
                                .size(30.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.primary)
                                .clickable { selectorFoto.launch("image/*") },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text("📷", fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimary)
                        }
                    }
                    Column {
                        Text(
                            perfil?.nombreVisible ?: "Cargando…",
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            sesion.correo,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        Text(
                            "Rol: ${sesion.rol.name.lowercase().replaceFirstChar { it.uppercase() }}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(top = 2.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    "Nombre de usuario",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                OutlinedTextField(
                    value = state.nombreEditado,
                    onValueChange = viewModel::cambiarNombreEditado,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    label = { Text("Tu nombre") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp),
                )
                Row(
                    Modifier.padding(top = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(
                        onClick = viewModel::guardarNombre,
                        enabled = !state.guardando &&
                            state.nombreEditado.isNotBlank() &&
                            state.nombreEditado.trim() != perfil?.nombre,
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    ) {
                        if (state.guardando) {
                            CircularProgressIndicator(
                                Modifier.size(18.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                            )
                        } else {
                            Text("Guardar nombre")
                        }
                    }
                    OutlinedButton(
                        onClick = { selectorFoto.launch("image/*") },
                        enabled = !state.procesandoFoto,
                        shape = RoundedCornerShape(18.dp),
                    ) {
                        Text(if (state.procesandoFoto) "Subiendo…" else "Elegir foto")
                    }
                    if (perfil?.fotoBase64 != null) {
                        TextButton(onClick = viewModel::quitarFoto, enabled = !state.procesandoFoto) {
                            Text("Quitar foto", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }

        Surface(
            Modifier.fillMaxWidth().padding(top = 14.dp),
            color = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(24.dp),
        ) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Modo oscuro",
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        "Cambia entre el tema claro y el oscuro de CodeRole.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 2.dp),
                    )
                }
                Switch(
                    checked = state.modoOscuro ?: temaSistema,
                    onCheckedChange = viewModel::cambiarModoOscuro,
                )
            }
        }

        state.mensaje?.let {
            Text(it, color = colorExito(), modifier = Modifier.padding(top = 14.dp))
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 14.dp))
        }

        if (sesion.rol == Rol.ADMIN) {
            TextButton(onClick = abrirAdmin, modifier = Modifier.padding(top = 12.dp)) {
                Text("Abrir panel Admin", color = MaterialTheme.colorScheme.primary)
            }
        }
        TextButton(onClick = cerrarSesion, modifier = Modifier.padding(top = 8.dp)) {
            Text("⇥  Cerrar sesión", color = MaterialTheme.colorScheme.error)
        }
    }
}
