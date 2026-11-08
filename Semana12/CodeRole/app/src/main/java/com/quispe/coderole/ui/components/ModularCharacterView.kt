package com.quispe.coderole.ui.components

import android.graphics.BitmapFactory
import android.graphics.RectF
import android.util.Base64
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.IntSize
import com.caverock.androidsvg.SVG
import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.CapasPersonaje
import com.quispe.coderole.domain.model.CharacterEmotion

/**
 * Capas PNG personalizadas desde la administración. Si una capa no tiene PNG
 * se dibuja la capa SVG o diseño por defecto.
 */
val LocalCapasPersonaje = staticCompositionLocalOf { CapasPersonaje() }

/** Provee las capas personalizadas a toda la interfaz. */
@Composable
fun ConCapasPersonaje(capas: CapasPersonaje, contenido: @Composable () -> Unit) {
    CompositionLocalProvider(LocalCapasPersonaje provides capas, content = contenido)
}

/**
 * Capas SVG/PNG independientes (estilo anime / nekogirl).
 * Superpone reactivamente en un Box de Jetpack Compose:
 * 1. Capa Base / Cuerpo (suéter cuello alto, chaqueta rosada, falda)
 * 2. Capa de Accesorios (opcional)
 * 3. Capa de Ojos (animación reactiva según emoción: NEUTRAL, THINKING, HAPPY, SURPRISED)
 * 4. Capa de Boca / Gestos (animación reactiva según emoción / habla: NEUTRAL, SPEAKING, HAPPY, SURPRISED)
 * 5. Capa de Cabello y Orejas (estructura fija superior)
 */
@Composable
fun ModularCharacterView(
    emotion: CharacterEmotion,
    modifier: Modifier = Modifier,
    isSpeaking: Boolean = false,
) {
    val capas = LocalCapasPersonaje.current
    Box(modifier = modifier.aspectRatio(0.8f).semantics { contentDescription = "Mentora de programación" }) {
        // 1. Capa Base / Cuerpo
        CapaLayer(capas.pngDe(CapaPersonaje.BASE), "character/base.svg", Modifier.fillMaxSize())

        // 2. Capa de Accesorios (opcional)
        capas.pngDe(CapaPersonaje.ACCESORIOS)?.let { png ->
            CapaPngOnlyLayer(png, Modifier.fillMaxSize())
        }

        // 3. Capa de Ojos (animación según emoción)
        AnimatedContent(targetState = emotion, label = "expresion_ojos") { actual ->
            val pngOjos = capas.pngDe(actual.capaOjos()) ?: capas.pngDe(CapaPersonaje.OJOS_NEUTRAL)
            CapaLayer(pngOjos, "character/${actual.assetOjos()}.svg", Modifier.fillMaxSize())
        }

        // 4. Capa de Boca / Gestos (animación según emoción o estado de habla)
        AnimatedContent(targetState = Pair(emotion, isSpeaking), label = "gesto_boca") { (actualEmotion, speaking) ->
            val capaBocaBuscada = if (speaking) CapaPersonaje.BOCA_SPEAKING else actualEmotion.capaBoca()
            val pngBoca = capas.pngDe(capaBocaBuscada)
                ?: (if (speaking) capas.pngDe(CapaPersonaje.BOCA_SPEAKING) else null)
                ?: capas.pngDe(CapaPersonaje.BOCA_NEUTRAL)
            CapaLayer(pngBoca, "character/${actualEmotion.assetBoca()}.svg", Modifier.fillMaxSize())
        }

        // 5. Capa de Cabello y Orejas (estructura superior)
        capas.pngDe(CapaPersonaje.CABELLO)?.let { png ->
            CapaPngOnlyLayer(png, Modifier.fillMaxSize())
        }
    }
}

@Composable
private fun CapaLayer(pngBase64: String?, asset: String, modifier: Modifier) {
    val bitmap = if (pngBase64.isNullOrBlank()) null else rememberPng(pngBase64)
    if (bitmap != null) {
        PngLayer(bitmap, modifier)
    } else {
        SvgLayer(asset, modifier)
    }
}

@Composable
private fun CapaPngOnlyLayer(pngBase64: String, modifier: Modifier) {
    val bitmap = rememberPng(pngBase64)
    if (bitmap != null) {
        PngLayer(bitmap, modifier)
    }
}

@Composable
private fun rememberPng(base64: String): ImageBitmap? = remember(base64) {
    runCatching {
        val bytes = Base64.decode(base64, Base64.DEFAULT)
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size)?.asImageBitmap()
    }.getOrNull()
}

@Composable
private fun PngLayer(bitmap: ImageBitmap, modifier: Modifier) {
    Canvas(modifier = modifier) {
        drawImage(
            image = bitmap,
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
        )
    }
}

@Composable
private fun SvgLayer(asset: String, modifier: Modifier) {
    val context = LocalContext.current
    val picture = remember(context, asset) {
        runCatching {
            SVG.getFromAsset(context.assets, asset).renderToPicture(320, 400)
        }.getOrNull()
    }
    if (picture != null) {
        Canvas(modifier = modifier) {
            drawIntoCanvas { composeCanvas ->
                composeCanvas.nativeCanvas.drawPicture(picture, RectF(0f, 0f, size.width, size.height))
            }
        }
    }
}

private fun CharacterEmotion.capaOjos(): CapaPersonaje = when (this) {
    CharacterEmotion.NEUTRAL -> CapaPersonaje.OJOS_NEUTRAL
    CharacterEmotion.THINKING -> CapaPersonaje.OJOS_THINKING
    CharacterEmotion.HAPPY -> CapaPersonaje.OJOS_HAPPY
    CharacterEmotion.SURPRISED -> CapaPersonaje.OJOS_SURPRISED
}

private fun CharacterEmotion.capaBoca(): CapaPersonaje = when (this) {
    CharacterEmotion.NEUTRAL -> CapaPersonaje.BOCA_NEUTRAL
    CharacterEmotion.THINKING -> CapaPersonaje.BOCA_NEUTRAL
    CharacterEmotion.HAPPY -> CapaPersonaje.BOCA_HAPPY
    CharacterEmotion.SURPRISED -> CapaPersonaje.BOCA_SURPRISED
}

private fun CharacterEmotion.assetOjos() = when (this) {
    CharacterEmotion.NEUTRAL -> "eyes_neutral"
    CharacterEmotion.THINKING -> "eyes_thinking"
    CharacterEmotion.HAPPY -> "eyes_happy"
    CharacterEmotion.SURPRISED -> "eyes_surprised"
}

private fun CharacterEmotion.assetBoca() = when (this) {
    CharacterEmotion.NEUTRAL -> "mouth_neutral"
    CharacterEmotion.THINKING -> "mouth_thinking"
    CharacterEmotion.HAPPY -> "mouth_happy"
    CharacterEmotion.SURPRISED -> "mouth_surprised"
}
