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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quispe.cafeCampus.ui.cliente.ConfirmarPedidoScreen
import com.quispe.cafeCampus.ui.cliente.DetalleProductoScreen
import com.quispe.cafeCampus.ui.cliente.LoginScreen
import com.quispe.cafeCampus.ui.cliente.MenuScreen
import com.quispe.cafeCampus.ui.cliente.MisPedidosScreen
import com.quispe.cafeCampus.ui.cliente.SeguimientoScreen
import com.quispe.cafeCampus.ui.cliente.SplashScreen
import com.quispe.cafeCampus.viewmodel.MenuViewModel
import com.quispe.cafeCampus.viewmodel.PedidosClienteViewModel
import com.quispe.cafeCampus.viewmodel.SesionViewModel

@Composable
fun NavCafe(
    factory: ViewModelProvider.Factory,
    sesion: SesionViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    // Compartida entre Menú, Detalle y Confirmar para que el carrito se mantenga
    val menuVm: MenuViewModel = viewModel(factory = factory)
    val pedidosVm: PedidosClienteViewModel = viewModel(factory = factory)

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
            MenuScreen(
                vm = menuVm,
                onProducto = { productoId ->
                    navController.navigate(Rutas.detalle(productoId))
                },
                onConfirmar = { navController.navigate(Rutas.CONFIRMAR) },
                onMisPedidos = { navController.navigate(Rutas.MIS_PEDIDOS) }
            )
        }

        composable(Rutas.CONFIRMAR) {
            ConfirmarPedidoScreen(
                menuVm = menuVm,
                vm = pedidosVm,
                onConfirmado = { pedidoId ->
                    navController.navigate(Rutas.seguimiento(pedidoId.toInt())) {
                        popUpTo(Rutas.CONFIRMAR) { inclusive = true }
                    }
                },
                onSeguirPidiendo = { navController.popBackStack(Rutas.MENU, false) },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Rutas.SEGUIMIENTO,
            arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
        ) { entry ->
            val pedidoId = entry.arguments?.getInt("pedidoId") ?: -1
            SeguimientoScreen(
                vm = pedidosVm,
                pedidoId = pedidoId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Rutas.MIS_PEDIDOS) {
            MisPedidosScreen(
                vm = pedidosVm,
                onPedido = { pedidoId ->
                    navController.navigate(Rutas.seguimiento(pedidoId))
                },
                onBack = { navController.popBackStack() }
            )
        }
        // TODO (Pasos 10-12): Panel, pedidos admin, detalle admin, menú admin, ajustes

        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { entry ->
            val productoId = entry.arguments?.getInt("productoId") ?: -1
            DetalleProductoScreen(
                vm = menuVm,
                productoId = productoId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
