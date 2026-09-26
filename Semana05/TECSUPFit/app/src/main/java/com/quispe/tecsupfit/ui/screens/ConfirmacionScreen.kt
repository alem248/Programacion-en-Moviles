package com.quispe.tecsupfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.tecsupfit.modelos.clasePorId
import com.quispe.tecsupfit.ui.theme.GrisClaro
import com.quispe.tecsupfit.ui.theme.TextoPrincipal
import com.quispe.tecsupfit.ui.theme.TextoSecundario
import com.quispe.tecsupfit.ui.theme.VerdeOscuro

@Composable
fun ConfirmacionScreen(
    claseId: Int,
    onVerReservas: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clase = clasePorId(claseId)

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .padding(top = 48.dp)
                .size(110.dp)
                .background(VerdeOscuro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(76.dp),
                tint = Color.White
            )
        }

        Text(
            text = "¡Cupo reservado!",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            modifier = Modifier.padding(top = 24.dp)
        )
        Text(
            text = "Tu reserva se registro correctamente.",
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario,
            modifier = Modifier.padding(top = 8.dp)
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 28.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                FilaResumen(etiqueta = "Clase", valor = clase.nombre)
                HorizontalDivider()
                FilaResumen(etiqueta = "Dia", valor = clase.dia)
                HorizontalDivider()
                FilaResumen(etiqueta = "Horario", valor = clase.horario)
                HorizontalDivider()
                FilaResumen(etiqueta = "Sala", valor = clase.sala)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onVerReservas,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 16.dp)
                .height(52.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GrisClaro,
                contentColor = Color(0xFF333333)
            )
        ) {
            Text(
                text = "Ver mis reservas",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FilaResumen(etiqueta: String, valor: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = etiqueta,
            style = MaterialTheme.typography.bodyMedium,
            color = TextoSecundario
        )
        Text(
            text = valor,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal
        )
    }
}
