package com.quispe.coderole.ui.components

import android.graphics.BitmapFactory
import android.graphics.RectF
import android.util.Base64
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.caverock.androidsvg.SVG
import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.CapasPersonaje
import com.quispe.coderole.domain.model.CharacterEmotion
import com.quispe.coderole.domain.model.GrupoCapa

/** Ciclo completo de respiración del mentor (sube y baja suavemente). */
private const val DURACION_RESPIRACION = 3000

/** Segundos entre parpadeos. */
private const val DURACION_PARPADEO = 4200

/** Fracción del ciclo en la que los ojos están cerrados (~100 ms). */
private const val INICIO_CIERRE = 0.972f
private const val FIN_CIERRE = 0.996f

/**
 * Capas PNG personalizadas desde la administración. Si una pieza no tiene PNG
 * se dibuja el SVG incluido en `assets/character/` y, si tampoco existe, la
 * pieza no aparece (así se agregan orejas o accesorios bajo demanda).
 */
val LocalCapasPersonaje = staticCompositionLocalOf { CapasPersonaje() }

/** Provee las capas personalizadas a toda la interfaz. */
@Composable
fun ConCapasPersonaje(capas: CapasPersonaje, contenido: @Composable () -> Unit) {
    CompositionLocalProvider(LocalCapasPersonaje provides capas, content = contenido)
}

/**
 * Mentor animado: pinta las piezas en orden (cabello → cabeza → orejas →
 * flequillo → cejas → ojos → boca → accesorios) y las mantiene vivas con
 * respiración, parpadeo y fundido entre expresiones.
 */
@Composable
fun ModularCharacterView(
    emotion: CharacterEmotion,
    modifier: Modifier = Modifier,
    /** true mientras la mentora escribe la respuesta (muestra la boca de habla). */
    isSpeaking: Boolean = false,
) {
    val capas = LocalCapasPersonaje.current
    val infinita = rememberInfiniteTransition(label = "mentora")

    val respiracion by infinita.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(DURACION_RESPIRACION, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "respiracion",
    )
    val ciclo by infinita.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(DURACION_PARPADEO, easing = LinearEasing)),
        label = "parpadeo",
    )
    val cerrando = ciclo in INICIO_CIERRE..FIN_CIERRE
    val alturaFlotar = with(LocalDensity.current) { 4.dp.toPx() }

    Box(modifier = modifier.aspectRatio(0.8f).semantics { contentDescription = "Mentora de programación" }) {
        Box(
            Modifier
                .fillMaxSize()
                .graphicsLayer {
                    val escala = 1f + respiracion * 0.015f
                    scaleX = escala
                    scaleY = escala
                    translationY = (respiracion - 0.5f) * alturaFlotar
                    rotationZ = (respiracion - 0.5f) * 1.2f
                    transformOrigin = TransformOrigin(0.5f, 0.9f)
                },
        ) {
            // Piezas fijas, de atrás hacia adelante.
            CapaDibujada(CapaPersonaje.CABELLO, Modifier.fillMaxSize())
            CapaDibujada(CapaPersonaje.BASE, Modifier.fillMaxSize())
            CapaDibujada(CapaPersonaje.OREJAS, Modifier.fillMaxSize())
            CapaDibujada(CapaPersonaje.FLEQUILLO, Modifier.fillMaxSize())

            // Expresión: cada cambio de emoción se funde en ~240 ms.
            CapaExpresiva(CapaPersonaje.CEJAS_NEUTRAL, emotion, Modifier.fillMaxSize())
            OjosConParpadeo(emotion, cerrando, Modifier.fillMaxSize())
            BocaConHabla(emotion, isSpeaking, Modifier.fillMaxSize())

            // Lo último pinta encima de todo (gafas, aretes, sombreros…).
            CapaDibujada(CapaPersonaje.ACCESORIOS, Modifier.fillMaxSize())
        }
    }
}

/** Pieza fija: PNG propio, o el SVG incluido, o nada si no tiene diseño. */
@Composable
private fun CapaDibujada(capa: CapaPersonaje, modifier: Modifier) {
    val png = LocalCapasPersonaje.current.pngDe(capa)
    val asset = capa.asset
    when {
        !png.isNullOrBlank() -> CapaPng(png, modifier)
        asset != null -> CapaSvg(asset, modifier)
    }
}

/** Pieza que depende de la expresión, con fundido al cambiar de emoción. */
@Composable
private fun CapaExpresiva(pieza: CapaPersonaje, emocion: CharacterEmotion, modifier: Modifier) {
    AnimatedContent(
        targetState = emocion,
        transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(160)) },
        label = "expresion_${pieza.grupo}",
    ) { actual ->
        val capas = LocalCapasPersonaje.current
        val piezaActual = pieza.paraEmocion(actual)
        // Si solo existe un PNG para la emoción neutral, se reutiliza en las demás.
        val png = capas.pngDe(piezaActual) ?: capas.pngDe(pieza.paraEmocion(CharacterEmotion.NEUTRAL))
        if (png != null) CapaPng(png, modifier) else CapaDibujada(piezaActual, modifier)
    }
}

/**
 * Boca: mientras la mentora escribe se usa la pieza `boca_speaking` (si el
 * administrador le asignó un PNG) y, si no, la boca de la emoción actual.
 */
@Composable
private fun BocaConHabla(emocion: CharacterEmotion, hablando: Boolean, modifier: Modifier) {
    val pngHablando = LocalCapasPersonaje.current.pngDe(CapaPersonaje.BOCA_SPEAKING)
    if (hablando && !pngHablando.isNullOrBlank()) {
        CapaPng(pngHablando, modifier)
    } else {
        CapaExpresiva(CapaPersonaje.BOCA_NEUTRAL, emocion, modifier)
    }
}

/**
 * Ojos con parpadeo: mientras están cerrados se muestra la pieza
 * `ojos_cerrado` y, si no existe, se comprime la mirada abierta.
 */
@Composable
private fun OjosConParpadeo(emocion: CharacterEmotion, cerrando: Boolean, modifier: Modifier) {
    val capas = LocalCapasPersonaje.current
    val cerrado = CapaPersonaje.OJOS_CERRADO
    val hayCerrado = capas.tienePng(cerrado) || cerrado.asset != null

    if (cerrando && hayCerrado) {
        CapaDibujada(cerrado, modifier)
    } else {
        AnimatedContent(
            targetState = emocion,
            transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(160)) },
            label = "expresion_ojos",
        ) { actual ->
            CapaDibujada(
                CapaPersonaje.OJOS_NEUTRAL.paraEmocion(actual),
                modifier.graphicsLayer { if (cerrando) scaleY = 0.08f },
            )
        }
    }
}

@Composable
private fun CapaPng(base64: String, modifier: Modifier) {
    val bitmap = rememberPng(base64) ?: return
    Canvas(modifier = modifier) {
        drawImage(
            image = bitmap,
            dstSize = IntSize(size.width.toInt(), size.height.toInt()),
        )
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
private fun CapaSvg(asset: String, modifier: Modifier) {
    val context = LocalContext.current
    val picture = remember(context, asset) {
        SVG.getFromAsset(context.assets, asset).renderToPicture(ANCHO_LIENZO, ALTO_LIENZO)
    }
    Canvas(modifier = modifier) {
        drawIntoCanvas { composeCanvas ->
            composeCanvas.nativeCanvas.drawPicture(picture, RectF(0f, 0f, size.width, size.height))
        }
    }
}

/** Miniatura de un SVG incluido (la usa el panel de administración). */
@Composable
fun VistaSvg(asset: String, modifier: Modifier = Modifier) = CapaSvg(asset, modifier)

private const val ANCHO_LIENZO = 320
private const val ALTO_LIENZO = 400

/** Referencia útil para el panel: agrupa las piezas según su grupo. */
fun piezasDe(grupo: GrupoCapa): List<CapaPersonaje> = CapaPersonaje.entries.filter { it.grupo == grupo }
