package com.quispe.coderole.ui.admin

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.quispe.coderole.ui.components.LocalCapasPersonaje
import com.quispe.coderole.ui.components.ModularCharacterView

/**
 * Sección del panel de administración para gestionar las capas modulares del
 * personaje (base, cabello/orejas, ojos por emoción, boca por emoción y accesorios)
 * asociando o reemplazando archivos PNG en tiempo real.
 */
@Composable
fun CapasPanel(
    state: AdminUiState,
    onGuardar: (CapaPersonaje, Uri, Context) -> Unit,
    onQuitar: (CapaPersonaje) -> Unit,
) {
    val contexto = LocalContext.current
    val capas = LocalCapasPersonaje.current
    var emocionPrueba by remember { mutableStateOf(CharacterEmotion.NEUTRAL) }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        Text(
            "Capas del personaje",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
        )
        Text(
            "Asocia archivos PNG (con fondo transparente) para personalizar las capas del personaje " +
                "(cuerpo, cabello/orejas, ojos, boca y accesorios). " +
                "El cambio se aplica en tiempo real a todos los dispositivos.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 12.dp),
        )

        TarjetaAdmin {
            Column(
                Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primaryContainer),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    Modifier.padding(vertical = 14.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    ModularCharacterView(emocionPrueba, Modifier.size(width = 160.dp, height = 200.dp))
                }
                Row(
                    Modifier.padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    CharacterEmotion.entries.forEach { emocion ->
                        FilterChip(
                            selected = emocionPrueba == emocion,
                            onClick = { emocionPrueba = emocion },
                            label = { Text(emocion.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        )
                    }
                }
            }
            Column(Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Text(
                    if (capas.algunaPersonalizada) "Personaje personalizado" else "Diseño original (SVG)",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    "${CapaPersonaje.entries.count { capas.tienePng(it) }} de ${CapaPersonaje.entries.size} capas con PNG propio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        val categorias = CapaPersonaje.entries.groupBy { it.categoria }

        categorias.forEach { (cat, listaCapas) ->
            Text(
                cat,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 6.dp),
            )
            listaCapas.forEach { capa ->
                CapaCard(
                    capa = capa,
                    pngBase64 = capas.pngDe(capa),
                    procesando = state.procesandoCapa == capa,
                    onElegir = { uri -> onGuardar(capa, uri, contexto) },
                    onQuitar = { onQuitar(capa) },
                )
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(8.dp))
        }
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
            MiniaturaCapa(pngBase64, Modifier.size(width = 54.dp, height = 68.dp))
            Column(Modifier.weight(1f)) {
                Text(capa.titulo, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    if (tienePng) "PNG personalizado" else "Capa por defecto",
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

/** Miniatura de la capa; muestra el PNG elegido o un marcador de diseño por defecto. */
@Composable
private fun MiniaturaCapa(pngBase64: String?, modifier: Modifier) {
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
        if (bitmap != null) {
            Image(bitmap = bitmap.asImageBitmap(), contentDescription = null, modifier = Modifier.fillMaxSize())
        } else {
            Text("DEF", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
