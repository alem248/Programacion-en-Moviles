package com.quispe.avisoscampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quispe.avisoscampus.model.Aviso

@Composable
fun HistorialAvisos(avisos: List<Aviso>) {
    if (avisos.isEmpty()) {
        Text("Sin avisos todavía")
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        avisos.take(20).forEach { aviso ->
            Card {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = aviso.origen.texto,
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = aviso.titulo,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(aviso.texto)
                    Text(
                        text = aviso.hora,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
