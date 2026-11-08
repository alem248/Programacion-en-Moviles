package com.quispe.cafeCampus.ui.admin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.ui.components.EstadoChip
import com.quispe.cafeCampus.ui.components.GraficoVentasCard
import com.quispe.cafeCampus.viewmodel.PanelAdminViewModel

@Composable
fun PanelScreen(
    vm: PanelAdminViewModel,
    onPedidos: () -> Unit,
    onMenu: () -> Unit = {},
    onReporte: () -> Unit = {}
) {
    val nuevos by vm.nuevos.collectAsState()
    val preparando by vm.preparando.collectAsState()
    val listos by vm.listos.collectAsState()
    val ventas by vm.ventasHoy.collectAsState()
    val ventasSemana by vm.ventasPorDiaState.collectAsState()
    val pedidosNuevos by vm.pedidosNuevos.collectAsState()

    Column(Modifier.fillMaxSize()) {
        AppTopBar(
            titulo = "Panel de control",
            actions = {
                IconButton(onClick = onMenu) {
                    Icon(
                        imageVector = Icons.Default.RestaurantMenu,
                        contentDescription = "Menú",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                IconButton(onClick = onPedidos) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                        contentDescription = "Pedidos",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Contador(
                    valor = nuevos,
                    etiqueta = "Nuevos",
                    color = Color(0xFF3B82F6),
                    modifier = Modifier.weight(1f)
                )
                Contador(
                    valor = preparando,
                    etiqueta = "Preparando",
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.weight(1f)
                )
                Contador(
                    valor = listos,
                    etiqueta = "Listos",
                    color = Color(0xFF22C55E),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text(
                        text = "Ventas del día",
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "S/ ${"%.2f".format(ventas)}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(14.dp))
            GraficoVentasCard(state = ventasSemana)

            Spacer(Modifier.height(20.dp))
            Text("Nuevos pedidos", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(10.dp))

            if (pedidosNuevos.isEmpty()) {
                Text(
                    text = "No hay pedidos nuevos por ahora",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(8.dp))
            } else {
                pedidosNuevos.take(4).forEach { dato ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp),
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
                                    modifier = Modifier.weight(1f)
                                )
                                EstadoChip(
                                    estado = com.quispe.cafeCampus.model.EstadoPedido.valueOf(
                                        dato.pedido.estado
                                    )
                                )
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = "${dato.resumen()} · Recojo ${dato.pedido.horaRecojo}",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(8.dp))
                            HorizontalDivider(
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "S/ ${"%.2f".format(dato.pedido.total)}",
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                CafeButton(
                                    texto = "Preparar",
                                    onClick = { vm.preparar(dato.pedido.id) },
                                    modifier = Modifier.width(140.dp),
                                    color = Color(0xFFF59E0B)
                                )
                            }
                        }
                    }
                }
                Spacer(Modifier.height(4.dp))
            }

            Spacer(Modifier.height(8.dp))
            CafeButton(
                texto = "Ver todos los pedidos",
                onClick = onPedidos,
                outlined = true
            )
            Spacer(Modifier.height(10.dp))
            CafeButton(
                texto = "Ver reporte del día",
                onClick = onReporte,
                outlined = true
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun Contador(
    valor: Int,
    etiqueta: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$valor",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = etiqueta,
                fontSize = 12.sp,
                color = color
            )
        }
    }
}
