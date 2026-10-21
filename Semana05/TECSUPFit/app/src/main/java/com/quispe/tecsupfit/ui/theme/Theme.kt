package com.quispe.tecsupfit.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = VerdeOscuro,
    onPrimary = Color.White,
    primaryContainer = VerdeClaro,
    onPrimaryContainer = VerdeOscuro,
    secondary = Color(0xFF4C635A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFCEE9DD),
    onSecondaryContainer = Color(0xFF082018),
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = Color.White,
    onSurface = TextoPrincipal,
    surfaceVariant = GrisClaro,
    onSurfaceVariant = TextoSecundario,
    outline = Color(0xFF707975),
    outlineVariant = Color(0xFFDDDDDD)
)

private val DarkColorScheme = darkColorScheme(
    primary = VerdeEsmeraldaOscuro,
    onPrimary = Color(0xFF00382B),
    primaryContainer = VerdeContenedorOscuro,
    onPrimaryContainer = Color(0xFF9CF1D7),
    secondary = Color(0xFFB2CCC1),
    onSecondary = Color(0xFF1D352D),
    secondaryContainer = Color(0xFF344C43),
    onSecondaryContainer = Color(0xFFCEE9DD),
    background = FondoAppOscuro,
    onBackground = TextoPrincipalOscuro,
    surface = SuperficieOscura,
    onSurface = TextoPrincipalOscuro,
    surfaceVariant = Color(0xFF242C28),
    onSurfaceVariant = TextoSecundarioOscuro,
    outline = Color(0xFF89938F),
    outlineVariant = Color(0xFF3F4945)
)

@Composable
fun TECSUPFitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
