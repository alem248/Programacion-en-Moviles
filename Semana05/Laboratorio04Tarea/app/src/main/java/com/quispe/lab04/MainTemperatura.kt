package com.quispe.lab04

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Composable
fun TemperatureDisplay() {
    var temperatura by remember { mutableStateOf(20) }

    val textColor = when {
        temperatura > 30 -> Color.Red
        temperatura < 10 -> Color.Blue
        else -> Color.Unspecified
    }

    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Temperatura: $temperatura",
            color = textColor,
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = { temperatura++ }) {
                Text("Subir")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(onClick = { temperatura-- }) {
                Text("Bajar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { temperatura = 20 }) {
            Text("Resetear")
        }
    }
}
