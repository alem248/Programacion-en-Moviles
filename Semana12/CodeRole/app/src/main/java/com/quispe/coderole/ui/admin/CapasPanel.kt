package com.quispe.coderole.ui.admin

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.CharacterEmotion
import com.quispe.coderole.domain.model.GrupoCapa
import com.quispe.coderole.ui.components.LocalCapasPersonaje
import com.quispe.coderole.ui.components.ModularCharacterView
import com.quispe.coderole.ui.components.VistaSvg

/**
 * Panel de administración de las piezas del personaje: agrupa las 18 piezas
 * (estructura, cejas, ojos, boca y parpadeo) y permite asociar o quitar su PNG
 * mientras se previsualiza el mentor con la expresión elegida.
 */
@Composable
fun CapasPanel(
    state: AdminUiState,
    onGuardar: (CapaPersonaje, Uri, Context) -> Unit,
    onQuitar: (CapaPersonaje) -> Unit,
) {
    val contexto = LocalContext.current
    val capas = LocalCapasPersonaje.current
    var emocion by remember { mutableStateOf(CharacterEmotion.NEUTRAL) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            "Piezas del personaje",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "El mentor se dibuja con ${CapaPersonaje.entries.size} piezas apiladas. Asocia un PNG " +
                "con fondo transparente a la que quieras cambiar; el cambio se aplica en tiempo " +
                "real a todos los dispositivos.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        TarjetaAdmin {
            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(vertical = 14.dp),
                contentAlignment = Alignment.Center,
            ) {
                ModularCharacterView(emocion, Modifier.size(width = 150.dp, height = 188.dp))
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                CharacterEmotion.entries.forEach { opcion ->
                    FilterChip(
                        selected = emocion == opcion,
                        onClick = { emocion = opcion },
                        label = { Text(opcion.titulo, style = MaterialTheme.typography.labelSmall) },
                    )
                }
            }
            Column(Modifier.padding(horizontal = 14.dp, vertical = 4.dp)) {
                Text(
                    if (capas.algunaPersonalizada) "Personaje personalizado" else "Diseño original (SVG)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    capas.resumen,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        GrupoCapa.entries.forEach { grupo ->
            val piezas = CapaPersonaje.entries.filter { it.grupo == grupo }
            if (piezas.isEmpty()) return@forEach

            Text(
                grupo.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
            )
            piezas.forEach { pieza ->
                CapaCard(
                    capa = pieza,
                    pngBase64 = capas.pngDe(pieza),
                    procesando = state.procesandoCapa == pieza,
                    onElegir = { uri -> onGuardar(pieza, uri, contexto) },
                    onQuitar = { onQuitar(pieza) },
                )
                Spacer(Modifier.height(10.dp))
            }
        }

        Text(
            "Las piezas sin diseño incluido (orejas y accesorios) permanecen ocultas hasta que " +
                "les asocias un PNG.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
        )
    }
}

@Composable
private fun CapaCard(
    capa: CapaPersonaje,
    pngBase64: String?,
    procesando: Boolean,
    onElegir: (Uri) -> Unit,
    onQuitar: () -> Unit,
) {
    val selector = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) onElegir(uri)
    }
    val tienePng = !pngBase64.isNullOrBlank()

    TarjetaAdmin {
        Row(
            Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MiniaturaCapa(pngBase64, capa.asset, Modifier.size(width = 54.dp, height = 68.dp))
            Column(Modifier.weight(1f)) {
                Text(capa.titulo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    when {
                        tienePng -> "PNG personalizado"
                        capa.asset != null -> "Diseño incluido en la app"
                        else -> "Sin pieza todavía"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    Modifier.padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    OutlinedButton(
                        onClick = { selector.launch("image/*") },
                        enabled = !procesando,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.height(38.dp),
                    ) {
                        Text(if (tienePng) "Cambiar PNG" else "Elegir PNG", style = MaterialTheme.typography.labelMedium)
                    }
                    if (tienePng) {
                        TextButton(onClick = onQuitar, enabled = !procesando, modifier = Modifier.height(38.dp)) {
                            Text("Quitar", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            if (procesando) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}

/** Miniatura de la pieza: el PNG elegido, el SVG incluido o un hueco. */
@Composable
private fun MiniaturaCapa(pngBase64: String?, asset: String?, modifier: Modifier) {
    val bitmap = remember(pngBase64) {
        if (pngBase64.isNullOrBlank()) null
        else runCatching {
            val bytes = Base64.decode(pngBase64, Base64.DEFAULT)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    Box(
        modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center,
    ) {
        when {
            bitmap != null -> Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
            )
            asset != null -> VistaSvg(asset, Modifier.fillMaxSize().padding(3.dp))
            else -> Text("—", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
