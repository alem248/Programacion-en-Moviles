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
 * se dibuja el SVG incluido en `assets/character/`.
 */
val LocalCapasPersonaje = staticCompositionLocalOf { CapasPersonaje() }

/** Provee las capas personalizadas a toda la interfaz. */
@Composable
fun ConCapasPersonaje(capas: CapasPersonaje, contenido: @Composable () -> Unit) {
    CompositionLocalProvider(LocalCapasPersonaje provides capas, content = contenido)
}

/** Capas SVG/PNG independientes: la base no incluye ojos ni boca. */
@Composable
fun ModularCharacterView(emotion: CharacterEmotion, modifier: Modifier = Modifier) {
    val capas = LocalCapasPersonaje.current
    Box(modifier = modifier.aspectRatio(0.8f).semantics { contentDescription = "Mentora de programación" }) {
        CapaLayer(capas.pngDe(CapaPersonaje.BASE), "character/base.svg", Modifier.fillMaxSize())
        AnimatedContent(targetState = emotion, label = "expresion_ojos") { actual ->
            CapaLayer(capas.pngDe(CapaPersonaje.OJOS), "character/${actual.assetOjos()}.svg", Modifier.fillMaxSize())
        }
        AnimatedContent(targetState = emotion, label = "gesto_boca") { actual ->
            CapaLayer(capas.pngDe(CapaPersonaje.BOCA), "character/${actual.assetBoca()}.svg", Modifier.fillMaxSize())
        }
    }
}

/** Dibuja el PNG personalizado de la capa o, si no hay, el SVG por defecto. */
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
        SVG.getFromAsset(context.assets, asset).renderToPicture(320, 400)
    }
    Canvas(modifier = modifier) {
        drawIntoCanvas { composeCanvas ->
            composeCanvas.nativeCanvas.drawPicture(picture, RectF(0f, 0f, size.width, size.height))
        }
    }
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
