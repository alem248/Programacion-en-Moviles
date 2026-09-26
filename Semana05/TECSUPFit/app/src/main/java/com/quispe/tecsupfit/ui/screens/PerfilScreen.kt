package com.quispe.tecsupfit.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.quispe.tecsupfit.ui.theme.TextoPrincipal
import com.quispe.tecsupfit.ui.theme.TextoSecundario
import com.quispe.tecsupfit.ui.theme.VerdeClaro
import com.quispe.tecsupfit.ui.theme.VerdeOscuro

@Composable
fun PerfilScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Perfil",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp)
        )

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(VerdeOscuro, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "AQ",
                color = Color.White,
                fontSize = 34.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Text(
            text = "Alexandra Quispe",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextoPrincipal,
            modifier = Modifier.padding(top = 14.dp)
        )

        Surface(
            modifier = Modifier.padding(top = 8.dp),
            shape = RoundedCornerShape(50),
            color = VerdeClaro
        ) {
            Text(
                text = "Plan Premium",
                color = VerdeOscuro,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        Spacer(modifier = Modifier.size(24.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            TarjetaEstadistica(
                valor = "14",
                etiqueta = "Clases",
                modifier = Modifier.weight(1f)
            )
            TarjetaEstadistica(
                valor = "3",
                etiqueta = "Rachas",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun TarjetaEstadistica(valor: String, etiqueta: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = VerdeOscuro
            )
            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodyLarge,
                color = TextoSecundario,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
