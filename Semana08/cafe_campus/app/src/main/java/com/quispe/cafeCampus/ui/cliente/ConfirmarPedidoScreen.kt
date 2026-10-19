package com.quispe.cafeCampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.ItemCarrito
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.viewmodel.MenuViewModel
import com.quispe.cafeCampus.viewmodel.PedidosClienteViewModel

private val horas = listOf("Ahora", "12:30", "13:00", "14:00")
private val pagos = linkedMapOf("CAJA" to "Caja", "YAPE" to "Yape-Plin")

@Composable
fun ConfirmarPedidoScreen(
    menuVm: MenuViewModel,
    vm: PedidosClienteViewModel,
    onConfirmado: (Long) -> Unit,
    onSeguirPidiendo: () -> Unit,
    onBack: () -> Unit
) {
    val carrito by menuVm.carrito.collectAsState()
    val error by vm.error.collectAsState()
    val pedidoCreado by vm.pedidoCreado.collectAsState()

    var hora by remember { mutableStateOf(horas.first()) }
    var pago by remember { mutableStateOf("CAJA") }
    var nota by remember { mutableStateOf("") }

    LaunchedEffect(pedidoCreado) {
        pedidoCreado?.let { id ->
            menuVm.vaciar()
            vm.limpiarPedidoCreado()
            onConfirmado(id)
        }
    }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Confirmar pedido", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (carrito.isEmpty()) {
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Tu carrito está vacío",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(Modifier.height(16.dp))
                CafeButton(texto = "Seguir pidiendo", onClick = onSeguirPidiendo, outlined = true)
                Spacer(Modifier.height(24.dp))
                return@Column
            }

            Text("Tu pedido", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))

            carrito.forEach { item ->
                LineaResumen(item)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(8.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
            Spacer(Modifier.height(12.dp))

            Text("Hora de recojo", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                horas.forEach { opcion ->
                    FilterChip(
                        selected = hora == opcion,
                        onClick = { hora = opcion },
                        label = { Text(opcion, fontSize = 13.sp) },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.AccessTime,
                                contentDescription = null,
                                modifier = Modifier.height(16.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (hora == opcion) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("Forma de pago", fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                pagos.forEach { (valor, etiqueta) ->
                    FilterChip(
                        selected = pago == valor,
                        onClick = { pago = valor },
                        label = { Text(etiqueta, fontSize = 13.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = if (pago == valor) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            }
                        )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))
            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                label = { Text("Nota para la cocina (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("S/ ${"%.2f".format(carrito.sumOf { it.subtotal })}")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "S/ ${"%.2f".format(carrito.sumOf { it.subtotal })}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(20.dp))
            CafeButton(
                texto = "Confirmar pedido",
                onClick = { vm.confirmar(carrito, hora, pago, nota) }
            )
            Spacer(Modifier.height(10.dp))
            CafeButton(
                texto = "Seguir pidiendo",
                onClick = onSeguirPidiendo,
                outlined = true
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun LineaResumen(item: ItemCarrito) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(item.producto.nombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text(
                text = "${item.cantidad} x S/ ${"%.2f".format(item.producto.precio)}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (item.nota.isNotBlank()) {
                Text(
                    text = "Nota: ${item.nota}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        Text(
            text = "S/ ${"%.2f".format(item.subtotal)}",
            fontWeight = FontWeight.SemiBold
        )
    }
}
