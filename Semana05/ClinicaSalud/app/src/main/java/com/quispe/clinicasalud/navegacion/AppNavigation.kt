package com.quispe.clinicasalud.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.quispe.clinicasalud.ui.screens.AgendarCitaScreen
import com.quispe.clinicasalud.ui.screens.ConfirmacionScreen
import com.quispe.clinicasalud.ui.screens.HistorialMedicoScreen
import com.quispe.clinicasalud.ui.screens.InicioScreen
import com.quispe.clinicasalud.ui.screens.MisCitasScreen
import com.quispe.clinicasalud.ui.screens.PerfilMedicoScreen
import com.quispe.clinicasalud.ui.screens.PerfilScreen

object Rutas {
    const val INICIO = "inicio"
    const val PERFIL = "perfil/{doctorId}"
    const val AGENDAR = "agendar/{doctorId}"
    const val CONFIRMACION = "confirmacion"
    const val MIS_CITAS = "mis_citas"
    const val HISTORIAL = "historial"
    const val PERFIL_USUARIO = "perfil_usuario"

    fun perfil(doctorId: Int) = "perfil/$doctorId"

    fun agendar(doctorId: Int) = "agendar/$doctorId"
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    onMenuClick: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Rutas.INICIO
    ) {
        composable(Rutas.INICIO) {
            InicioScreen(
                onDoctorSelected = { id -> navController.navigate(Rutas.perfil(id)) },
                onMenuClick = onMenuClick
            )
        }

        composable(
            route = Rutas.PERFIL,
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { entry ->
            val doctorId = entry.arguments?.getInt("doctorId") ?: 0
            PerfilMedicoScreen(
                doctorId = doctorId,
                onBack = { navController.popBackStack() },
                onAgendar = { navController.navigate(Rutas.agendar(doctorId)) }
            )
        }

        composable(
            route = Rutas.AGENDAR,
            arguments = listOf(navArgument("doctorId") { type = NavType.IntType })
        ) { entry ->
            val doctorId = entry.arguments?.getInt("doctorId") ?: 0
            AgendarCitaScreen(
                doctorId = doctorId,
                onBack = { navController.popBackStack() },
                onConfirmada = {
                    navController.navigate(Rutas.CONFIRMACION) {
                        popUpTo(Rutas.INICIO)
                    }
                }
            )
        }

        composable(Rutas.CONFIRMACION) {
            ConfirmacionScreen(
                onInicio = { navController.popBackStack(Rutas.INICIO, inclusive = false) },
                onMisCitas = { navController.navigate(Rutas.MIS_CITAS) }
            )
        }

        composable(Rutas.MIS_CITAS) {
            MisCitasScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.HISTORIAL) {
            HistorialMedicoScreen(onBack = { navController.popBackStack() })
        }

        composable(Rutas.PERFIL_USUARIO) {
            PerfilScreen(onBack = { navController.popBackStack() })
        }
    }
}
