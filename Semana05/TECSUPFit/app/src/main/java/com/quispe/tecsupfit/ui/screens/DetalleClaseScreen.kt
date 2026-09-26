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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.EventAvailable
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.tecsupfit.modelos.clasePorId
import com.quispe.tecsupfit.ui.theme.TextoPrincipal
import com.quispe.tecsupfit.ui.theme.TextoSecundario
import com.quispe.tecsupfit.ui.theme.VerdeClaro
import com.quispe.tecsupfit.ui.theme.VerdeOscuro

@Composable
fun DetalleClaseScreen(
    claseId: Int,
    onBack: () -> Unit,
    onReservar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clase = clasePorId(claseId)

    Column(modifier = modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver"
                )
            }
            Text(
                text = "Detalle de clase",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .background(VerdeClaro, RoundedCornerShape(20.dp))
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.FitnessCenter,
                    contentDescription = null,
                    modifier = Modifier.size(72.dp),
                    tint = VerdeOscuro
                )
            }

            Text(
                text = clase.nombre,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                modifier = Modifier.padding(top = 20.dp)
            )

            FilaInformacion(icono = Icons.Outlined.EventAvailable, texto = "${clase.dia} - ${clase.horario}")
            FilaInformacion(icono = Icons.Outlined.MeetingRoom, texto = clase.sala)
            FilaInformacion(icono = Icons.Outlined.AccessTime, texto = "Duracion ${clase.duracion}")
            FilaInformacion(icono = Icons.Outlined.Group, texto = "${clase.cupos} cupos disponibles")

            Text(
                text = "Descripcion",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextoPrincipal,
                modifier = Modifier.padding(top = 16.dp)
            )
            Text(
                text = clase.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = TextoSecundario,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Cupos restantes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextoSecundario
                        )
                        Text(
                            text = "${clase.cupos} de 20",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = VerdeOscuro
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(VerdeClaro, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = clase.dia.take(3),
                            color = VerdeOscuro,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shadowElevation = 8.dp
        ) {
            Button(
                onClick = onReservar,
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = VerdeOscuro)
            ) {
                Text(
                    text = "Reservar cupo",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FilaInformacion(
    icono: androidx.compose.ui.graphics.vector.ImageVector,
    texto: String
) {
    Row(
        modifier = Modifier.padding(top = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        Icon(
            imageVector = icono,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = VerdeOscuro
        )
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
