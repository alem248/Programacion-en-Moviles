package com.tecsup.mibodega.ui.cliente.screens.confirmacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.tecsup.mibodega.ui.componentes.BotonSecundario
import com.tecsup.mibodega.ui.theme.BodegaTheme
import com.tecsup.mibodega.ui.theme.GrisClaro
import com.tecsup.mibodega.ui.theme.VerdeBodega

private const val NUMERO_PEDIDO = "1024"

/**
 * Pantalla 7: Pedido confirmado (mockup "Cliente").
 * Se llega aquí desde Datos de entrega con popUpTo, por lo que es la
 * única pantalla del back stack: "Volver al inicio" reconstruye el
 * flujo desde cero.
 *
 * @param total total del pedido (subtotal + delivery) pasado por ruta
 * @param direccion dirección entregada en el paso anterior
 */
@Composable
fun PantallaConfirmacion(
    total: Float,
    direccion: String,
    onVolverInicio: () -> Unit
) {
    var mostrarEstado by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(40.dp))

        PalomitaVerde()

        Spacer(Modifier.height(20.dp))

        Text(
            text = "¡Pedido realizado!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Tu pedido está siendo preparado\ny será entregado pronto.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium,
            colors = CardDefaults.cardColors(containerColor = GrisClaro)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pedido #$NUMERO_PEDIDO",
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "S/ %.2f".format(total.toDouble()),
                        fontWeight = FontWeight.Bold,
                        color = VerdeBodega
                    )
                }

                Spacer(Modifier.height(8.dp))

                HorizontalDivider()

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Dirección",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = direccion.ifBlank { "Sin dirección registrada" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.weight(1f))

        BotonSecundario(
            texto = "Ver estado del pedido",
            onClick = { mostrarEstado = true }
        )

        Spacer(Modifier.height(12.dp))

        BotonSecundario(
            texto = "Volver al inicio",
            onClick = onVolverInicio
        )

        Spacer(Modifier.height(24.dp))
    }

    if (mostrarEstado) {
        DialogoEstadoPedido(onCerrar = { mostrarEstado = false })
    }
}

@Composable
private fun PalomitaVerde() {
    Box(
        modifier = Modifier
            .size(96.dp)
            .background(VerdeBodega, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = "Pedido confirmado",
            tint = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.size(56.dp)
        )
    }
}

/** Diálogo con el estado actual del pedido (botón "Ver estado"). */
@Composable
private fun DialogoEstadoPedido(onCerrar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCerrar,
        title = { Text("Estado del pedido #$NUMERO_PEDIDO") },
        text = {
            Text(
                text = "Tu pedido está en preparación. En unos minutos el repartidor " +
                    "saldrá hacia tu dirección y podrás seguirlo desde la sección Pedidos."
            )
        },
        confirmButton = {
            TextButton(onClick = onCerrar) { Text("Cerrar") }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PantallaConfirmacionPreview() {
    BodegaTheme {
        PantallaConfirmacion(
            total = 25.90f,
            direccion = "Av. Los Olivos 123 (Frente al parque)",
            onVolverInicio = {}
        )
    }
}
