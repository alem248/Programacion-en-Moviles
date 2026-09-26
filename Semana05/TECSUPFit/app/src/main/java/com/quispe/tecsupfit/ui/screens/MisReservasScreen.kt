package com.quispe.tecsupfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.tecsupfit.modelos.EstadoReserva
import com.quispe.tecsupfit.modelos.Reserva
import com.quispe.tecsupfit.ui.theme.GrisClaro
import com.quispe.tecsupfit.ui.theme.TextoPrincipal
import com.quispe.tecsupfit.ui.theme.TextoSecundario
import com.quispe.tecsupfit.ui.theme.VerdeClaro
import com.quispe.tecsupfit.ui.theme.VerdeOscuro

@Composable
fun MisReservasScreen(
    reservas: List<Reserva>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        Text(
            text = "Mis reservas",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        if (reservas.isEmpty()) {
            Text(
                text = "Todavia no tienes reservas.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(reservas) { reserva ->
                    TarjetaReserva(reserva = reserva)
                }
            }
        }
    }
}

@Composable
private fun TarjetaReserva(reserva: Reserva) {
    val confirmada = reserva.estado == EstadoReserva.CONFIRMADA
    // Borde izquierdo verde solo para reservas confirmadas
    val colorBorde = if (confirmada) VerdeOscuro else Color.Transparent

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.height(IntrinsicSize.Min)) {
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .background(colorBorde)
            )
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = reserva.clase.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextoPrincipal
                )
                Text(
                    text = "${reserva.clase.dia} - ${reserva.clase.horario}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextoSecundario,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = if (confirmada) "Confirmada" else "Completada",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (confirmada) VerdeOscuro else TextoSecundario,
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .background(
                            color = if (confirmada) VerdeClaro else GrisClaro,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}
