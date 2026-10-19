package com.quispe.cafeCampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.quispe.cafeCampus.ui.cliente.LoginScreen
import com.quispe.cafeCampus.ui.cliente.MenuScreen
import com.quispe.cafeCampus.ui.cliente.SplashScreen
import com.quispe.cafeCampus.viewmodel.MenuViewModel
import com.quispe.cafeCampus.viewmodel.SesionViewModel

@Composable
fun NavCafe(
    factory: ViewModelProvider.Factory,
    sesion: SesionViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.SPLASH,
        modifier = modifier
    ) {
        composable(Rutas.SPLASH) {
            val lista by sesion.sesionLista.collectAsState()
            var tiempoCumplido by remember { mutableStateOf(false) }

            SplashScreen(onTimeout = { tiempoCumplido = true })

            LaunchedEffect(tiempoCumplido, lista) {
                if (tiempoCumplido && lista) {
                    val destino =
                        if (sesion.usuarioActual.value != null) Rutas.MENU else Rutas.LOGIN
                    navController.navigate(destino) {
                        popUpTo(Rutas.SPLASH) { inclusive = true }
                    }
                }
            }
        }

        composable(Rutas.LOGIN) {
            LoginScreen(
                vm = sesion,
                onIngresar = {
                    // TODO (commit 10): el administrador debe ir al Panel de control
                    navController.navigate(Rutas.MENU) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.MENU) {
            val vm: MenuViewModel = viewModel(factory = factory)
            MenuScreen(
                vm = vm,
                onProducto = { /* TODO (commit 8): navegar al detalle */ }
            )
        }
        // TODO (Pasos 8-12): Detalle, Confirmar, Seguimiento, MisPedidos, Panel, etc.
    }
}
