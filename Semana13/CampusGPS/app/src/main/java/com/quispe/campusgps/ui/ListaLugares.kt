package com.quispe.campusgps.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.campusgps.model.LugarConDistancia
import com.quispe.campusgps.util.formatearDistancia

@Composable
fun ListaLugares(
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        items(lugares, key = { it.lugar.id }) { item ->
            val selected = item.lugar.id == seleccionado
            Card(
                onClick = { onClick(item.lugar.id) },
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (selected) MaterialTheme.colorScheme.secondaryContainer
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
                border = if (selected) BorderStroke(1.dp, MaterialTheme.colorScheme.primary) else null
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        Modifier.size(10.dp).background(MaterialTheme.colorScheme.primary, CircleShape)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(item.lugar.nombre, fontWeight = FontWeight.SemiBold)
                        Text(item.lugar.categoria, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodySmall)
                    }
                    Text(item.metros?.let(::formatearDistancia) ?: "Sin calcular",
                        style = MaterialTheme.typography.labelLarge)
                }
            }
        }
    }
}
