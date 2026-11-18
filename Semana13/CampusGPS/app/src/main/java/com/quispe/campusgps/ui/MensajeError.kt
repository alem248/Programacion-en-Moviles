package com.quispe.campusgps.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quispe.campusgps.model.TipoError
import com.quispe.campusgps.model.UbicacionUi

@Composable
fun MensajeError(
    error: UbicacionUi.Error,
    onOpenSettings: () -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(error.mensaje, color = MaterialTheme.colorScheme.onErrorContainer)
            when (error.tipo) {
                TipoError.SIN_PERMISO -> Button(onClick = onOpenSettings) { Text("Abrir ajustes") }
                TipoError.GPS_APAGADO -> Button(onClick = onRetry) { Text("Reintentar") }
                TipoError.SIN_PLAY_SERVICES, TipoError.OTRO -> Unit
            }
        }
    }
}
