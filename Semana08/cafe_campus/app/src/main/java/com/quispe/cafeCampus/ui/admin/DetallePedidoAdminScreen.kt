package com.quispe.cafeCampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.EstadoPedido
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.EstadoChip
import com.quispe.cafeCampus.viewmodel.PedidosAdminViewModel

@Composable
fun DetallePedidoAdminScreen(
    vm: PedidosAdminViewModel,
    pedidoId: Int,
    onBack: () -> Unit
) {
    val pedido by vm.pedido(pedidoId).collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Detalle del pedido", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            val dato = pedido
            if (dato == null) {
                Spacer(Modifier.height(32.dp))
                Text(
                    text = "Cargando pedido...",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                return@Column
            }

            val estado = EstadoPedido.valueOf(dato.pedido.estado)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = "Pedido #${dato.pedido.id}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Text(
                                text = dato.cliente.nombre,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        EstadoChip(estado)
                    }
                    Spacer(Modifier.height(10.dp))
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                    )
                    Spacer(Modifier.height(10.dp))

                    dato.detalles.forEach { linea ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${linea.cantidad} x ${linea.nombreProducto}",
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "S/ ${"%.2f".format(linea.precioUnitario * linea.cantidad)}",
                                fontSize = 14.sp
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total", fontWeight = FontWeight.Bold)
                        Text(
                            text = "S/ ${"%.2f".format(dato.pedido.total)}",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Recojo: ${dato.pedido.horaRecojo} · Pago: ${dato.pedido.pago}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            if (dato.pedido.nota.isNotBlank()) {
                Spacer(Modifier.height(12.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF59E0B).copy(alpha = 0.15f))
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(
                            text = "Nota del cliente",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFB45309),
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(text = dato.pedido.nota, fontSize = 14.sp)
                    }
                }
            }

            Spacer(Modifier.height(20.dp))
            Text("Estado", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                EstadoPedido.entries.forEach { opcion ->
                    FilterChip(
                        selected = estado == opcion,
                        onClick = { vm.cambiarEstado(dato.pedido.id, opcion) },
                        label = { Text(opcion.titulo, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.primary
                        )
                    )
                }
            }

            Spacer(Modifier.height(20.dp))
            val siguiente = estado.siguiente()
            val puedeAvanzar = estado != EstadoPedido.ENTREGADO && estado != EstadoPedido.CANCELADO
            OutlinedButton(
                onClick = { vm.avanzar(dato) },
                enabled = puedeAvanzar,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text(
                    text = if (puedeAvanzar) "Pasar a ${siguiente.titulo}" else "Pedido completado"
                )
            }
            Spacer(Modifier.height(10.dp))
            OutlinedButton(
                onClick = { vm.cancelar(dato) },
                enabled = estado != EstadoPedido.CANCELADO && estado != EstadoPedido.ENTREGADO,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Cancelar pedido", color = MaterialTheme.colorScheme.error)
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
