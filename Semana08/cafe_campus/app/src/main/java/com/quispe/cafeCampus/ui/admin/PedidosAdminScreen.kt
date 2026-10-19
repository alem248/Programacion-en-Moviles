package com.quispe.cafeCampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.EstadoPedido
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.EstadoChip
import com.quispe.cafeCampus.viewmodel.PedidosAdminViewModel

@Composable
fun PedidosAdminScreen(
    vm: PedidosAdminViewModel,
    onPedido: (Int) -> Unit,
    onBack: () -> Unit
) {
    val estado by vm.estadoSeleccionado.collectAsState()
    val pedidos by vm.pedidos.collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Pedidos", onBack = onBack)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            EstadoPedido.entries.forEach { opcion ->
                FilterChip(
                    selected = estado == opcion,
                    onClick = { vm.seleccionarEstado(opcion) },
                    label = { Text(opcion.titulo, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                        selectedLabelColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        }

        if (pedidos.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))
                Text(
                    text = "No hay pedidos en '${estado.titulo}'",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(pedidos, key = { it.pedido.id }) { dato ->
                    val actual = EstadoPedido.valueOf(dato.pedido.estado)
                    val siguiente = actual.siguiente()

                    Card(
                        onClick = { onPedido(dato.pedido.id) },
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
                        Column(Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "#${dato.pedido.id} ${dato.cliente.nombre}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                EstadoChip(actual)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = dato.resumen(),
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Recojo: ${dato.pedido.horaRecojo} · ${dato.pedido.pago}",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "S/ ${"%.2f".format(dato.pedido.total)}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            if (dato.pedido.nota.isNotBlank()) {
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Nota: ${dato.pedido.nota}",
                                    fontSize = 12.sp,
                                    color = Color(0xFFF59E0B)
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val puedeAvanzar = actual != EstadoPedido.ENTREGADO &&
                                        actual != EstadoPedido.CANCELADO
                                OutlinedButton(
                                    onClick = { vm.avanzar(dato) },
                                    enabled = puedeAvanzar,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = if (puedeAvanzar) "Pasar a ${siguiente.titulo}" else "Completado",
                                        fontSize = 13.sp
                                    )
                                }
                                OutlinedButton(
                                    onClick = { vm.cancelar(dato) },
                                    enabled = actual != EstadoPedido.CANCELADO &&
                                            actual != EstadoPedido.ENTREGADO,
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text(
                                        text = "Cancelar",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
