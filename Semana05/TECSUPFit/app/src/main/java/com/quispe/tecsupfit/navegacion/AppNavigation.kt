package com.quispe.tecsupfit.navegacion

import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quispe.tecsupfit.modelos.EstadoReserva
import com.quispe.tecsupfit.modelos.Reserva
import com.quispe.tecsupfit.modelos.clasePorId
import com.quispe.tecsupfit.modelos.descontarCupo
import com.quispe.tecsupfit.ui.screens.ConfirmacionScreen
import com.quispe.tecsupfit.ui.screens.DetalleClaseScreen
import com.quispe.tecsupfit.ui.screens.InicioScreen
import com.quispe.tecsupfit.ui.screens.MisReservasScreen
import com.quispe.tecsupfit.ui.screens.PerfilScreen
import com.quispe.tecsupfit.ui.screens.RutinasScreen

object Rutas {
    const val INICIO = "inicio"
    const val RESERVAS = "reservas"
    const val RUTINAS = "rutinas"
    const val PERFIL = "perfil"
    const val DETALLE = "detalle/{claseId}"
    const val CONFIRMACION = "confirmacion/{claseId}"

    fun detalle(claseId: Int) = "detalle/$claseId"
    fun confirmacion(claseId: Int) = "confirmacion/$claseId"

    // Pantallas que si muestran la barra de navegacion inferior
    val conBarraInferior = listOf(INICIO, RESERVAS, RUTINAS, PERFIL)
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    reservas: SnapshotStateList<Reserva>,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.INICIO,
        modifier = modifier
    ) {
        composable(Rutas.INICIO) {
            InicioScreen(
                onClaseClick = { claseId -> navController.navigate(Rutas.detalle(claseId)) }
            )
        }

        composable(Rutas.RESERVAS) {
            MisReservasScreen(reservas = reservas)
        }

        composable(Rutas.RUTINAS) {
            RutinasScreen()
        }

        composable(Rutas.PERFIL) {
            PerfilScreen()
        }

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("claseId") { type = NavType.IntType })
        ) { entry ->
            val claseId = entry.arguments?.getInt("claseId") ?: 1
            DetalleClaseScreen(
                claseId = claseId,
                onBack = { navController.popBackStack() },
                onReservar = {
                    // Registra la reserva, descuenta el cupo y abre la confirmacion
                    descontarCupo(claseId)
                    reservas.add(0, Reserva(clasePorId(claseId), EstadoReserva.CONFIRMADA))
                    navController.navigate(Rutas.confirmacion(claseId)) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            )
        }

        composable(
            route = Rutas.CONFIRMACION,
            arguments = listOf(navArgument("claseId") { type = NavType.IntType })
        ) { entry ->
            val claseId = entry.arguments?.getInt("claseId") ?: 1
            ConfirmacionScreen(
                claseId = claseId,
                onVerReservas = {
                    navController.navigate(Rutas.RESERVAS) {
                        popUpTo(Rutas.INICIO) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
