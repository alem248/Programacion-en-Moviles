package com.quispe.cafeCampus.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.cafeCampus.ui.cliente.MenuScreen
import com.quispe.cafeCampus.viewmodel.MenuViewModel

@Composable
fun NavCafe(
    factory: ViewModelProvider.Factory,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.MENU,
        modifier = modifier
    ) {
        composable(Rutas.MENU) {
            val vm: MenuViewModel = viewModel(factory = factory)
            MenuScreen(
                vm = vm,
                onProducto = { /* TODO (Paso 7): navegar al detalle */ }
            )
        }
        // TODO (Pasos 6-12): agregar Login, Detalle, Confirmar, Seguimiento, MisPedidos, Panel, etc.
    }
}
