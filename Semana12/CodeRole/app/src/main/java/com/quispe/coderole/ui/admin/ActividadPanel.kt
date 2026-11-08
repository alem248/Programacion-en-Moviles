package com.quispe.coderole.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.coderole.domain.model.ActividadDiaria
import com.quispe.coderole.domain.model.RegistroActividad
import com.quispe.coderole.domain.model.TipoActividad
import com.quispe.coderole.domain.util.Fechas

/**
 * Panel de actividad diaria del mentor: métricas del periodo, gráfico de barras
 * de tiempo de uso diario de todos los usuarios y registros concretos de interacción.
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
                    "Los mensajes, respuestas y tiempo de uso del mentor aparecerán aquí automáticamente.",
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
                tiempoMinutos = acumulado.tiempoMinutos + dia.tiempoUsoMinutos,
            )
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        // Gráfico de tiempo de uso de los usuarios
        item {
            GraficoTiempoUso(
                actividad = state.actividad,
                fechaSeleccionada = state.fechaSeleccionada,
                tiempoTotalMinutos = totales.tiempoMinutos,
                onSeleccionarDia = seleccionarDia,
            )
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricaTotal("Mensajes", totales.mensajes, Modifier.weight(1f))
                MetricaTotal("Respuestas", totales.respuestas, Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricaTotal("Favoritos", totales.favoritos, Modifier.weight(1f))
                MetricaTotal("Tiempo total", formatearTiempo(totales.tiempoMinutos), Modifier.weight(1f))
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

/** Gráfico de barras interactivo de tiempo de uso diario de la mentora. */
@Composable
private fun GraficoTiempoUso(
    actividad: List<ActividadDiaria>,
    fechaSeleccionada: String,
    tiempoTotalMinutos: Int,
    onSeleccionarDia: (String) -> Unit,
) {
    val diasOrdenados = remember(actividad) { actividad.reversed() }
    val maxVal = remember(diasOrdenados) {
        diasOrdenados.maxOfOrNull { it.tiempoUsoMinutos.coerceAtLeast(it.total) }?.coerceAtLeast(1) ?: 1
    }

    TarjetaAdmin {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "Tiempo de uso de los usuarios",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        "Uso acumulado por todos los estudiantes",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Text(
                    formatearTiempo(tiempoTotalMinutos),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(Modifier.height(16.dp))

            LazyRow(
                Modifier.fillMaxWidth().height(140.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                items(diasOrdenados, key = { it.fecha }) { dia ->
                    val seleccionado = dia.fecha == fechaSeleccionada
                    val valor = if (dia.tiempoUsoMinutos > 0) dia.tiempoUsoMinutos else dia.total
                    val fraccion = (valor.toFloat() / maxVal.toFloat()).coerceIn(0.12f, 1.0f)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .width(42.dp)
                            .fillMaxHeight()
                            .clickable { onSeleccionarDia(dia.fecha) },
                        verticalArrangement = Arrangement.Bottom,
                    ) {
                        Text(
                            text = if (dia.tiempoUsoMinutos > 0) "${dia.tiempoUsoMinutos}m" else "${dia.total}i",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                        )
                        Spacer(Modifier.height(4.dp))
                        Box(
                            Modifier
                                .width(28.dp)
                                .weight(fraccion, fill = true)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                                .background(
                                    if (seleccionado) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.primaryContainer,
                                ),
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = Fechas.legible(dia.fecha).take(3),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }
        }
    }
}

private data class Totales(
    val mensajes: Int = 0,
    val respuestas: Int = 0,
    val favoritos: Int = 0,
    val sesiones: Int = 0,
    val tiempoMinutos: Int = 0,
)

private fun formatearTiempo(minutos: Int): String {
    if (minutos < 60) return "${minutos}m"
    val horas = minutos / 60
    val restoMin = minutos % 60
    return if (restoMin == 0) "${horas}h" else "${horas}h ${restoMin}m"
}

@Composable
private fun MetricaTotal(titulo: String, valor: String, modifier: Modifier = Modifier) {
    TarjetaAdmin(modifier) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Text(titulo, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1)
            Text(
                valor,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}

@Composable
private fun MetricaTotal(titulo: String, valor: Int, modifier: Modifier = Modifier) {
    MetricaTotal(titulo, valor.toString(), modifier)
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
                "${dia.total} interacciones · ${formatearTiempo(dia.tiempoUsoMinutos)} de uso",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
            Spacer(Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Contador("💬", dia.mensajes)
                Contador("✦", dia.respuestas)
                Contador("★", dia.favoritos)
                Contador("⏱", dia.tiempoUsoMinutos, "m")
            }
        }
    }
}

@Composable
private fun Contador(icono: String, valor: Int, sufijo: String = "") {
    Text("$icono $valor$sufijo", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
        TipoActividad.TIEMPO_USO -> "⏱"
    }

private val TipoActividad.etiqueta: String
    get() = when (this) {
        TipoActividad.MENSAJE -> "Mensaje enviado"
        TipoActividad.RESPUESTA -> "Respuesta de la mentora"
        TipoActividad.FAVORITO -> "Favorito marcado"
        TipoActividad.SESION -> "Sesión iniciada"
        TipoActividad.TIEMPO_USO -> "Tiempo de uso activo"
    }
