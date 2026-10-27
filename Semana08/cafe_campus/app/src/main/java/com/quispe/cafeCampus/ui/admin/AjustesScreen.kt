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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.Rol
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.viewmodel.PagoViewModel
import com.quispe.cafeCampus.viewmodel.SesionViewModel

@Composable
fun AjustesScreen(
    vm: SesionViewModel,
    vmPago: PagoViewModel,
    onCerrarSesion: () -> Unit
) {
    val modoOscuro by vm.modoOscuro.collectAsState()
    val recibirPedidos by vm.recibirPedidos.collectAsState()
    val usuario by vm.usuarioActual.collectAsState()
    val rol by vm.rolActivo.collectAsState()

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

                Spacer(Modifier.height(16.dp))
                SeccionPagosQr(vmPago)
            }

            Spacer(Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            Spacer(Modifier.height(20.dp))

            Text(
                text = "Tu rol lo defines al iniciar sesión. Para cambiar de rol, cierra sesión y vuelve a entrar con otra cuenta.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

/**
 * Configuración del QR de pago (Yape/Plin): el admin pega el payload de
 * SU QR real; la app le inyectará el monto de cada pedido automáticamente.
 */
@Composable
private fun SeccionPagosQr(vm: PagoViewModel) {
    val payload by vm.payloadBase.collectAsState()
    val mensaje by vm.mensaje.collectAsState()
    var texto by remember(payload) { mutableStateOf(payload) }
    val activo = payload.isNotBlank()

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(Modifier.padding(14.dp)) {
            Text("Pagos con QR (Yape / Plin)", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Pega el texto de tu QR real para que los pedidos se paguen " +
                        "con el monto automático. Obténlo escaneando tu QR con " +
                        "cualquier lector de códigos.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(10.dp))
            OutlinedTextField(
                value = texto,
                onValueChange = {
                    texto = it
                    vm.limpiarMensaje()
                },
                label = { Text("Payload de tu QR") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            mensaje?.let {
                Spacer(Modifier.height(6.dp))
                Text(it, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
            }

            Spacer(Modifier.height(10.dp))
            CafeButton(
                texto = if (activo) "Actualizar QR" else "Guardar QR",
                onClick = { vm.guardarPayload(texto) }
            )

            if (activo) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "✓ QR activo: los pedidos en Yape/Plin muestran el código con monto",
                    fontSize = 12.sp,
                    color = Color(0xFF16A34A)
                )
            }
        }
    }
}
