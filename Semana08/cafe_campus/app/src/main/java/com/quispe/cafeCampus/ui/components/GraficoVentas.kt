package com.quispe.cafeCampus.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.model.VentaDia
import com.quispe.cafeCampus.model.VentasPorDiaUiState

@Composable
fun GraficoVentasCard(
    state: VentasPorDiaUiState,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Ventas por Día",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Lunes a Domingo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (!state.cargando) {
                    Text(
                        text = "S/ ${"%.2f".format(state.totalSemanal)}",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 16.sp
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            if (state.cargando) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Cargando gráfico...",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                GraficoBarrasCanvas(
                    ventas = state.ventas,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

@Composable
fun GraficoBarrasCanvas(
    ventas: List<VentaDia>,
    modifier: Modifier = Modifier
) {
    var animar by remember { mutableStateOf(false) }
    var itemSeleccionado by remember { mutableStateOf<VentaDia?>(null) }

    LaunchedEffect(Unit) {
        animar = true
    }

    val progresoAnimacion by animateFloatAsState(
        targetValue = if (animar) 1f else 0f,
        animationSpec = tween(durationMillis = 800),
        label = "animacion_barras"
    )

    val colorBarra = MaterialTheme.colorScheme.primary
    val colorBarraHoy = Color(0xFF22C55E)
    val colorEjes = MaterialTheme.colorScheme.outlineVariant
    val colorTexto = MaterialTheme.colorScheme.onSurface
    val colorTextoVariante = MaterialTheme.colorScheme.onSurfaceVariant

    val maxMonto = (ventas.maxOfOrNull { it.total } ?: 100.0).coerceAtLeast(100.0)

    Column(modifier = modifier) {
        itemSeleccionado?.let { item ->
            Text(
                text = "📌 ${item.diaLargo}: S/ ${"%.2f".format(item.total)}",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .pointerInput(ventas) {
                    detectTapGestures { tapOffset ->
                        val anchoTotal = size.width
                        val margenIzquierdo = 70f
                        val anchoGrafico = anchoTotal - margenIzquierdo
                        val numBarras = ventas.size
                        if (numBarras > 0) {
                            val pasoBarra = anchoGrafico / numBarras
                            val index = ((tapOffset.x - margenIzquierdo) / pasoBarra).toInt()
                            if (index in ventas.indices) {
                                itemSeleccionado = ventas[index]
                            }
                        }
                    }
                }
        ) {
            val width = size.width
            val height = size.height

            val paddingLeft = 70f
            val paddingBottom = 40f
            val paddingTop = 30f

            val chartWidth = width - paddingLeft
            val chartHeight = height - paddingBottom - paddingTop

            // Lineas de guia Eje Y
            val pasosY = 3
            val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)

            for (i in 0..pasosY) {
                val valorY = maxMonto * (i.toFloat() / pasosY)
                val y = height - paddingBottom - (chartHeight * (i.toFloat() / pasosY))

                // Linea horizontal de fondo
                drawLine(
                    color = colorEjes,
                    start = Offset(paddingLeft, y),
                    end = Offset(width, y),
                    strokeWidth = 1f,
                    pathEffect = pathEffect
                )

                // Etiqueta Eje Y
                drawContext.canvas.nativeCanvas.drawText(
                    "S/${valorY.toInt()}",
                    10f,
                    y + 10f,
                    android.graphics.Paint().apply {
                        color = colorTextoVariante.toArgb()
                        textSize = 26f
                        isAntiAlias = true
                    }
                )
            }

            // Dibujar Barras Eje X
            if (ventas.isNotEmpty()) {
                val numBarras = ventas.size
                val barSpace = chartWidth / numBarras
                val barWidth = barSpace * 0.55f

                ventas.forEachIndexed { index, item ->
                    val xCenter = paddingLeft + (index * barSpace) + (barSpace / 2)
                    val barLeft = xCenter - (barWidth / 2)

                    val alturaBarra = ((item.total / maxMonto) * chartHeight * progresoAnimacion).toFloat()
                    val barTop = (height - paddingBottom - alturaBarra).coerceAtMost(height - paddingBottom)

                    val colorFinal = if (item.esHoy) colorBarraHoy else colorBarra

                    // Dibuja la barra con esquina superior redondeada
                    drawRoundRect(
                        color = colorFinal,
                        topLeft = Offset(barLeft, barTop),
                        size = Size(barWidth, alturaBarra),
                        cornerRadius = CornerRadius(8f, 8f)
                    )

                    // Texto de monto encima de la barra
                    if (progresoAnimacion > 0.8f && item.total > 0) {
                        drawContext.canvas.nativeCanvas.drawText(
                            "${item.total.toInt()}",
                            xCenter - 15f,
                            barTop - 8f,
                            android.graphics.Paint().apply {
                                color = colorTexto.toArgb()
                                textSize = 24f
                                isFakeBoldText = true
                                isAntiAlias = true
                            }
                        )
                    }

                    // Etiqueta Eje X (Días)
                    drawContext.canvas.nativeCanvas.drawText(
                        item.dia,
                        xCenter - 20f,
                        height - 8f,
                        android.graphics.Paint().apply {
                            color = if (item.esHoy) colorBarraHoy.toArgb() else colorTexto.toArgb()
                            textSize = 28f
                            isFakeBoldText = item.esHoy
                            isAntiAlias = true
                        }
                    )
                }
            }
        }
    }
}
