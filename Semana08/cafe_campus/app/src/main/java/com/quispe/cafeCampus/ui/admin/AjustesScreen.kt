package com.quispe.cafeCampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.Rol
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    vm: SesionViewModel,
    onCambiarPerfil: (Rol) -> Unit,
    onCerrarSesion: () -> Unit
) {
    val modoOscuro by vm.modoOscuro.collectAsState()
    val recibirPedidos by vm.recibirPedidos.collectAsState()
    val usuario by vm.usuarioActual.collectAsState()
    val rol by vm.rolActivo.collectAsState()
    val otroRol = if (rol == Rol.CLIENTE) Rol.ADMIN else Rol.CLIENTE

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Perfil y ajustes")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = usuario?.nombre ?: rol.titulo,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = usuario?.correo ?: "",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = rol.titulo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            OpcionSwitch(
                titulo = "Modo oscuro",
                descripcion = "Cambia el tema de toda la app",
                valor = modoOscuro,
                onValor = vm::cambiarModoOscuro
            )

            if (rol == Rol.ADMIN) {
                Spacer(Modifier.height(8.dp))
                OpcionSwitch(
                    titulo = "Recibir pedidos",
                    descripcion = "Si está apagado, el estudiante ve 'Cafetería cerrada'",
                    valor = recibirPedidos,
                    onValor = vm::cambiarRecibirPedidos
                )
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            Spacer(Modifier.height(20.dp))

            Text("Modo práctica", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Cambia entre los dos perfiles sin volver a iniciar sesión",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            CafeButton(
                texto = "Cambiar a ${otroRol.titulo}",
                onClick = { onCambiarPerfil(otroRol) },
                outlined = true
            )

            Spacer(Modifier.height(16.dp))
            CafeButton(
                texto = "Cerrar sesión",
                onClick = onCerrarSesion,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun OpcionSwitch(
    titulo: String,
    descripcion: String,
    valor: Boolean,
    onValor: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text(titulo, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    text = descripcion,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Switch(checked = valor, onCheckedChange = onValor)
        }
    }
}
