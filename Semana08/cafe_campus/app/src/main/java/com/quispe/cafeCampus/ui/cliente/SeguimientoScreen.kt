package com.quispe.cafeCampus.ui.cliente

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.EstadoPedido
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.EstadoChip
import com.quispe.cafeCampus.ui.components.StepperEstado
import com.quispe.cafeCampus.viewmodel.PedidosClienteViewModel

@Composable
fun SeguimientoScreen(
    vm: PedidosClienteViewModel,
    pedidoId: Int,
    onBack: () -> Unit
) {
    val pedido by vm.pedido(pedidoId).collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Seguimiento", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            if (pedido == null) {
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Cargando pedido...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                return@Column
            }

            val dato = pedido ?: return@Column

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Pedido #${dato.pedido.id}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        EstadoChip(EstadoPedido.valueOf(dato.pedido.estado))
                    }
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = dato.resumen(),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                    Spacer(Modifier.height(8.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Recojo: ${dato.pedido.horaRecojo} · Pago: ${dato.pedido.pago}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "S/ ${"%.2f".format(dato.pedido.total)}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Estado del pedido", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(16.dp))

            val estado = EstadoPedido.valueOf(dato.pedido.estado)
            if (estado == EstadoPedido.CANCELADO) {
                Text(
                    text = "Este pedido fue cancelado",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.SemiBold
                )
            } else {
                StepperEstado(estadoActual = estado)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
