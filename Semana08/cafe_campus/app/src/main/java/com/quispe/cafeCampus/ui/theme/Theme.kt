package com.quispe.cafeCampus.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.quispe.cafeCampus.model.Rol

private val ClienteLightColors = lightColorScheme(
    primary = NaranjaPrimary,
    onPrimary = Color.White,
    primaryContainer = NaranjaPrimaryContainer,
    onPrimaryContainer = NaranjaPrimary,
    background = ClienteBackground,
    surface = Color.White
)

private val ClienteDarkColors = darkColorScheme(
    primary = NaranjaPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF9A3412),
    onPrimaryContainer = NaranjaPrimaryContainer,
    background = DarkClienteBackground,
    surface = DarkSurface
)

private val AdminLightColors = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = IndigoPrimaryContainer,
    onPrimaryContainer = IndigoPrimary,
    background = AdminBackground,
    surface = Color.White
)

// Modo oscuro del administrador: todos los roles con tonos índigo/violeta
// para que fondo, superficies, acentos y textos vivan en la misma familia.
private val AdminDarkColors = darkColorScheme(
    primary = DarkAdminPrimary,
    onPrimary = DarkAdminOnPrimary,
    primaryContainer = DarkAdminPrimaryContainer,
    onPrimaryContainer = IndigoPrimaryContainer,
    secondary = Color(0xFFC7D2FE),
    onSecondary = Color(0xFF1B1840),
    secondaryContainer = Color(0xFF2F2B63),
    onSecondaryContainer = Color(0xFFE4E2F5),
    tertiary = Color(0xFFC4B5FD),
    onTertiary = Color(0xFF2A1B5E),
    tertiaryContainer = Color(0xFF4A3F86),
    onTertiaryContainer = Color(0xFFEADDFF),
    background = DarkAdminBackground,
    onBackground = Color(0xFFE7E5F8),
    surface = DarkAdminSurface,
    onSurface = Color(0xFFE7E5F8),
    surfaceVariant = DarkAdminSurfaceVariant,
    onSurfaceVariant = Color(0xFFB9B6D9),
    surfaceContainerLowest = Color(0xFF0F0D24),
    surfaceContainerLow = Color(0xFF17153A),
    surfaceContainer = DarkAdminSurface,
    surfaceContainerHigh = DarkAdminSurfaceHigh,
    surfaceContainerHighest = Color(0xFF2C2860),
    outline = Color(0xFF565183),
    outlineVariant = Color(0xFF3B3766)
)

@Composable
fun CafeTheme(
    rol: Rol = Rol.CLIENTE,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        rol == Rol.CLIENTE && darkTheme -> ClienteDarkColors
        rol == Rol.CLIENTE -> ClienteLightColors
        rol == Rol.ADMIN && darkTheme -> AdminDarkColors
        else -> AdminLightColors
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
