package com.quispe.cafeCampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.cafeCampus.model.EstadoPedido
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.PedidoCard
import com.quispe.cafeCampus.viewmodel.PedidosClienteViewModel

private val estadosActivos = setOf(
    EstadoPedido.RECIBIDO,
    EstadoPedido.PREPARANDO,
    EstadoPedido.LISTO
)

@Composable
fun MisPedidosScreen(
    vm: PedidosClienteViewModel,
    onPedido: (Int) -> Unit,
    onBack: () -> Unit
) {
    val pedidos by vm.misPedidos.collectAsState()
    var tab by remember { mutableIntStateOf(0) }

    val activos = pedidos.filter { EstadoPedido.valueOf(it.pedido.estado) in estadosActivos }
    val historial = pedidos.filter { EstadoPedido.valueOf(it.pedido.estado) !in estadosActivos }
    val lista = if (tab == 0) activos else historial

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Mis pedidos", onBack = onBack)

        TabRow(selectedTabIndex = tab) {
            Tab(
                selected = tab == 0,
                onClick = { tab = 0 },
                text = { Text("Activos (${activos.size})") }
            )
            Tab(
                selected = tab == 1,
                onClick = { tab = 1 },
                text = { Text("Historial (${historial.size})") }
            )
        }

        if (lista.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(48.dp))
                Text(
                    text = if (tab == 0) "No tienes pedidos activos" else "Aún no hay historial",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(lista, key = { it.pedido.id }) { dato ->
                    PedidoCard(
                        numero = dato.pedido.id,
                        cliente = dato.cliente.nombre,
                        resumen = dato.resumen(),
                        fecha = dato.pedido.fecha,
                        total = dato.pedido.total,
                        estado = EstadoPedido.valueOf(dato.pedido.estado),
                        onClick = { onPedido(dato.pedido.id) },
                        mostrarCliente = false
                    )
                }
            }
        }
    }
}
