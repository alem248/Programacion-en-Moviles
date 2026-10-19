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

private val AdminDarkColors = darkColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFF3730A3),
    onPrimaryContainer = IndigoPrimaryContainer,
    background = DarkAdminBackground,
    surface = DarkSurface
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
