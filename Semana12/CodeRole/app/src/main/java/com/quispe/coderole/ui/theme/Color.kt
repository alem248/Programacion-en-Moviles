package com.quispe.coderole.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

// ── Paleta personalizada de CodeRole ──────────────────────────────────────
val RojoPrincipal = Color(0xFFE04A4E)
val RosaSecundario = Color(0xFFF4777F)
val Crema = Color(0xFFFEEBB7)
val RosaPastel = Color(0xFFFBD2CC)

// ── Neutros derivados de la paleta ────────────────────────────────────────
val FondoClaro = Color(0xFFFFF6F4)
val SuperficieClara = Color(0xFFFFFFFF)
val TextoClaro = Color(0xFF2A1B20)
val TextoSuaveClaro = Color(0xFF6B4B54)

val FondoOscuro = Color(0xFF1B1417)
val SuperficieOscura = Color(0xFF251B1F)
val TextoOscuro = Color(0xFFFBEDEA)
val TextoSuaveOscuro = Color(0xFFC9A9AF)

val ErrorClaro = Color(0xFFB42342)
val ErrorOscuro = Color(0xFFFF9AA6)
val ExitoClaro = Color(0xFF1F7A55)
val ExitoOscuro = Color(0xFF7BDCB0)

/** Verde de éxito legible sobre el fondo claro u oscuro actual. */
@Composable
fun colorExito(): Color =
    if (MaterialTheme.colorScheme.background.luminance() < 0.5f) ExitoOscuro else ExitoClaro

val EsquemaClaro = androidx.compose.material3.lightColorScheme(
    primary = RojoPrincipal,
    onPrimary = Color.White,
    primaryContainer = RosaPastel,
    onPrimaryContainer = Color(0xFF4A1418),
    secondary = RosaSecundario,
    onSecondary = Color.White,
    secondaryContainer = RosaPastel,
    onSecondaryContainer = Color(0xFF4A1418),
    tertiary = Crema,
    onTertiary = TextoClaro,
    onTertiaryContainer = TextoClaro,
    background = FondoClaro,
    onBackground = TextoClaro,
    surface = SuperficieClara,
    onSurface = TextoClaro,
    surfaceVariant = RosaPastel,
    onSurfaceVariant = TextoSuaveClaro,
    surfaceContainer = SuperficieClara,
    surfaceContainerLow = Color(0xFFFFFBFA),
    surfaceContainerHigh = Color(0xFFF6E9E7),
    outline = Color(0xFF9B7580),
    outlineVariant = Color(0xFFEAD7D4),
    error = ErrorClaro,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD9),
    onErrorContainer = Color(0xFF410002),
)

val EsquemaOscuro = androidx.compose.material3.darkColorScheme(
    primary = RosaSecundario,
    onPrimary = TextoClaro,
    primaryContainer = Color(0xFF5A2226),
    onPrimaryContainer = Color(0xFFFFD9DC),
    secondary = RojoPrincipal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF4A2A2F),
    onSecondaryContainer = Color(0xFFFFD9DC),
    tertiary = Crema,
    onTertiary = TextoClaro,
    onTertiaryContainer = TextoClaro,
    background = FondoOscuro,
    onBackground = TextoOscuro,
    surface = SuperficieOscura,
    onSurface = TextoOscuro,
    surfaceVariant = Color(0xFF3A2A30),
    onSurfaceVariant = TextoSuaveOscuro,
    surfaceContainer = Color(0xFF2C2126),
    surfaceContainerLow = Color(0xFF241B1F),
    surfaceContainerHigh = Color(0xFF36282E),
    outline = Color(0xFF9B7580),
    outlineVariant = Color(0xFF4A3A40),
    error = ErrorOscuro,
    onError = Color(0xFF3A0F16),
    errorContainer = Color(0xFF5C111B),
    onErrorContainer = Color(0xFFFFDAD9),
)
