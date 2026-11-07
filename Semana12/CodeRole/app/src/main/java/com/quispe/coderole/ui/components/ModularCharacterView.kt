package com.quispe.coderole.ui.components

import android.graphics.RectF
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.caverock.androidsvg.SVG
import com.quispe.coderole.domain.model.CharacterEmotion

/** Capas SVG independientes: la base no incluye ojos ni boca y se puede reemplazar por PNG luego. */
@Composable
fun ModularCharacterView(emotion: CharacterEmotion, modifier: Modifier = Modifier) {
    Box(modifier = modifier.aspectRatio(0.8f).semantics { contentDescription = "Mentora de programación" }) {
        SvgLayer("character/base.svg", Modifier.fillMaxSize())
        AnimatedContent(targetState = emotion, label = "expresion_ojos") { actual ->
            SvgLayer("character/${actual.assetOjos()}.svg", Modifier.fillMaxSize())
        }
        AnimatedContent(targetState = emotion, label = "gesto_boca") { actual ->
            SvgLayer("character/${actual.assetBoca()}.svg", Modifier.fillMaxSize())
        }
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
