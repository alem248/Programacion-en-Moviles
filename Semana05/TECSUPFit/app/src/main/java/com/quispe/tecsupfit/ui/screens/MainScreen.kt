package com.quispe.tecsupfit.ui.screens

import android.app.Activity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quispe.tecsupfit.modelos.EstadoReserva
import com.quispe.tecsupfit.modelos.Reserva
import com.quispe.tecsupfit.modelos.clasePorId
import com.quispe.tecsupfit.navegacion.AppNavigation
import com.quispe.tecsupfit.navegacion.Rutas
import com.quispe.tecsupfit.ui.theme.TextoSecundario
import com.quispe.tecsupfit.ui.theme.VerdeClaro
import com.quispe.tecsupfit.ui.theme.VerdeOscuro

private data class Pestana(val titulo: String, val ruta: String, val icono: ImageVector)

private val pestanas = listOf(
    Pestana("Inicio", Rutas.INICIO, Icons.Filled.Home),
    Pestana("Reservas", Rutas.RESERVAS, Icons.Filled.CalendarMonth),
    Pestana("Rutinas", Rutas.RUTINAS, Icons.Filled.FitnessCenter),
    Pestana("Perfil", Rutas.PERFIL, Icons.Filled.Person)
)

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    // Estado global de reservas compartido por todas las pantallas
    val reservas = remember {
        mutableStateListOf(
            Reserva(clasePorId(2), EstadoReserva.CONFIRMADA),
            Reserva(clasePorId(3), EstadoReserva.COMPLETADA)
        )
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    // La barra inferior solo se muestra en las cuatro pestanas principales
    val mostrarBarra = rutaActual in Rutas.conBarraInferior

    val view = LocalView.current
    if (!view.isInEditMode) {
        // Iconos blancos sobre el header verde de inicio, oscuros en el resto
        SideEffect {
            val activity = view.context as Activity
            WindowCompat.getInsetsController(activity.window, view).isAppearanceLightStatusBars =
                rutaActual != Rutas.INICIO
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar(containerColor = Color.White) {
                    pestanas.forEach { pestana ->
                        NavigationBarItem(
                            selected = rutaActual == pestana.ruta,
                            onClick = {
                                navController.navigate(pestana.ruta) {
                                    popUpTo(Rutas.INICIO) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = pestana.icono,
                                    contentDescription = pestana.titulo
                                )
                            },
                            label = { Text(pestana.titulo) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = VerdeOscuro,
                                selectedTextColor = VerdeOscuro,
                                indicatorColor = VerdeClaro,
                                unselectedIconColor = TextoSecundario,
                                unselectedTextColor = TextoSecundario
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        AppNavigation(
            navController = navController,
            reservas = reservas,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
