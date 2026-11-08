package com.quispe.coderole.ui.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.quispe.coderole.domain.model.ActividadDiaria
import com.quispe.coderole.domain.model.RegistroActividad
import com.quispe.coderole.domain.model.TipoActividad
import com.quispe.coderole.domain.util.Fechas

/**
 * Panel de actividad diaria del mentor: métricas del periodo, desglose por día
 * y registros concretos de interacción del día seleccionado.
 */
@Composable
fun ActividadPanel(state: AdminUiState, seleccionarDia: (String) -> Unit) {
    if (state.cargandoActividad) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        return
    }

    if (state.actividad.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("📅", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.primary)
                Text(
                    "Aún no hay actividad registrada",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 10.dp),
                )
                Text(
                    "Los mensajes, respuestas y sesiones del día aparecerán aquí solos.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
        return
    }

    val totales = remember(state.actividad) {
        state.actividad.fold(Totales()) { acumulado, dia ->
            Totales(
                mensajes = acumulado.mensajes + dia.mensajes,
                respuestas = acumulado.respuestas + dia.respuestas,
                favoritos = acumulado.favoritos + dia.favoritos,
                sesiones = acumulado.sesiones + dia.sesiones,
            )
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricaTotal("Mensajes", totales.mensajes, Modifier.weight(1f))
                MetricaTotal("Respuestas", totales.respuestas, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricaTotal("Favoritos", totales.favoritos, Modifier.weight(1f))
                MetricaTotal("Sesiones", totales.sesiones, Modifier.weight(1f))
            }
        }
        item {
            Text(
                "Actividad por día",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
        items(state.actividad, key = { it.fecha }) { dia ->
            DiaCard(dia, seleccionado = dia.fecha == state.fechaSeleccionada) { seleccionarDia(dia.fecha) }
        }
        item {
            Text(
                "Registros del ${Fechas.legible(state.fechaSeleccionada)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        if (state.registros.isEmpty()) {
            item {
                Text(
                    "Este día aún no tiene interacciones registradas.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
        items(state.registros, key = { it.id }) { registro -> RegistroFila(registro) }
    }
}

private data class Totales(
    val mensajes: Int = 0,
    val respuestas: Int = 0,
    val favoritos: Int = 0,
    val sesiones: Int = 0,
)

@Composable
private fun MetricaTotal(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    TarjetaAdmin(modifier) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Text(
                valor.toString(),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun DiaCard(dia: ActividadDiaria, seleccionado: Boolean, onClick: () -> Unit) {
    TarjetaAdmin(
        Modifier.clickable(onClick = onClick),
        color = if (seleccionado) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    Fechas.legible(dia.fecha),
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                if (seleccionado) Text("●", color = MaterialTheme.colorScheme.primary)
            }
            Text(
                "${dia.total} interacciones",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
            Spacer(Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Contador("💬", dia.mensajes)
                Contador("✦", dia.respuestas)
                Contador("★", dia.favoritos)
                Contador("👤", dia.sesiones)
            }
        }
    }
}

@Composable
private fun Contador(icono: String, valor: Int) {
    Text("$icono $valor", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Composable
private fun RegistroFila(registro: RegistroActividad) {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(registro.tipo.icono, modifier = Modifier.width(26.dp))
        Column(Modifier.weight(1f)) {
            Text(
                registro.tipo.etiqueta,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            if (registro.detalle.isNotBlank()) {
                Text(
                    registro.detalle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Text(
            if (registro.creadoEn > 0) Fechas.horaDe(registro.creadoEn) else "",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private val TipoActividad.icono: String
    get() = when (this) {
        TipoActividad.MENSAJE -> "💬"
        TipoActividad.RESPUESTA -> "✦"
        TipoActividad.FAVORITO -> "★"
        TipoActividad.SESION -> "👤"
    }

private val TipoActividad.etiqueta: String
    get() = when (this) {
        TipoActividad.MENSAJE -> "Mensaje enviado"
        TipoActividad.RESPUESTA -> "Respuesta de la mentora"
        TipoActividad.FAVORITO -> "Favorito marcado"
        TipoActividad.SESION -> "Sesión iniciada"
    }
