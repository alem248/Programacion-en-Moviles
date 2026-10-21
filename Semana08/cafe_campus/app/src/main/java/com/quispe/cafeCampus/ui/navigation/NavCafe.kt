package com.quispe.cafeCampus.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.quispe.cafeCampus.model.Rol
import com.quispe.cafeCampus.ui.admin.AjustesScreen
import com.quispe.cafeCampus.ui.admin.DetallePedidoAdminScreen
import com.quispe.cafeCampus.ui.admin.FormularioProductoScreen
import com.quispe.cafeCampus.ui.admin.MenuAdminScreen
import com.quispe.cafeCampus.ui.admin.PanelScreen
import com.quispe.cafeCampus.ui.admin.PedidosAdminScreen
import com.quispe.cafeCampus.ui.cliente.ConfirmarPedidoScreen
import com.quispe.cafeCampus.ui.cliente.DetalleProductoScreen
import com.quispe.cafeCampus.ui.cliente.LoginScreen
import com.quispe.cafeCampus.ui.cliente.MenuScreen
import com.quispe.cafeCampus.ui.cliente.MisPedidosScreen
import com.quispe.cafeCampus.ui.cliente.SeguimientoScreen
import com.quispe.cafeCampus.ui.cliente.SplashScreen
import com.quispe.cafeCampus.viewmodel.MenuAdminViewModel
import com.quispe.cafeCampus.viewmodel.MenuViewModel
import com.quispe.cafeCampus.viewmodel.PanelAdminViewModel
import com.quispe.cafeCampus.viewmodel.PedidosAdminViewModel
import com.quispe.cafeCampus.viewmodel.PedidosClienteViewModel
import com.quispe.cafeCampus.viewmodel.SesionViewModel

private data class Pestania(val ruta: String, val titulo: String, val icono: ImageVector)

@Composable
fun NavCafe(
    factory: ViewModelProvider.Factory,
    sesion: SesionViewModel,
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    // Compartidas entre pantallas para conservar carrito y sesiones
    val menuVm: MenuViewModel = viewModel(factory = factory)
    val pedidosVm: PedidosClienteViewModel = viewModel(factory = factory)
    val panelVm: PanelAdminViewModel = viewModel(factory = factory)
    val pedidosAdminVm: PedidosAdminViewModel = viewModel(factory = factory)
    val menuAdminVm: MenuAdminViewModel = viewModel(factory = factory)

    val rol by sesion.rolActivo.collectAsState()

    fun limpiarPila() {
        while (navController.popBackStack()) {
            // vacía toda la pila de navegación
        }
    }

    fun irADestinoPrincipal() {
        val destino = if (sesion.rolActivo.value == Rol.ADMIN) Rutas.PANEL else Rutas.MENU
        limpiarPila()
        navController.navigate(destino)
    }

    fun irALogin() {
        limpiarPila()
        navController.navigate(Rutas.LOGIN)
    }

    val pestanias = when (rol) {
        Rol.ADMIN -> listOf(
            Pestania(Rutas.PANEL, "Panel", Icons.Default.Dashboard),
            Pestania(Rutas.PEDIDOS_ADMIN, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
            Pestania(Rutas.AJUSTES, "Perfil", Icons.Default.Settings)
        )
        else -> listOf(
            Pestania(Rutas.MENU, "Menú", Icons.Default.LocalCafe),
            Pestania(Rutas.MIS_PEDIDOS, "Pedidos", Icons.AutoMirrored.Filled.ReceiptLong),
            Pestania(Rutas.AJUSTES, "Perfil", Icons.Default.Settings)
        )
    }
    val rutasPestanias = pestanias.map { it.ruta }

    val entrada by navController.currentBackStackEntryAsState()
    val rutaActual = entrada?.destination?.route
    val mostrarBarra = rutaActual != null && rutaActual in rutasPestanias

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (mostrarBarra) {
                NavigationBar {
                    pestanias.forEach { pestania ->
                        NavigationBarItem(
                            selected = rutaActual == pestania.ruta,
                            onClick = {
                                navController.navigate(pestania.ruta) {
                                    if (rutaActual != null && rutaActual in rutasPestanias) {
                                        popUpTo(rutaActual) { inclusive = true }
                                    }
                                    launchSingleTop = true
                                }
                            },
                            icon = {
                                Icon(
                                    imageVector = pestania.icono,
                                    contentDescription = pestania.titulo
                                )
                            },
                            label = { Text(pestania.titulo) },
                            colors = NavigationBarItemDefaults.colors()
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Rutas.SPLASH,
            modifier = Modifier.padding(padding)
        ) {
            composable(Rutas.SPLASH) {
                val lista by sesion.sesionLista.collectAsState()
                var tiempoCumplido by remember { mutableStateOf(false) }

                SplashScreen(onTimeout = { tiempoCumplido = true })

                LaunchedEffect(tiempoCumplido, lista) {
                    if (tiempoCumplido && lista) {
                        if (sesion.usuarioActual.value != null) {
                            irADestinoPrincipal()
                        } else {
                            irALogin()
                        }
                    }
                }
            }

            composable(Rutas.LOGIN) {
                LoginScreen(
                    vm = sesion,
                    onIngresar = { irADestinoPrincipal() }
                )
            }

            composable(Rutas.MENU) {
                val usuario by sesion.usuarioActual.collectAsState()
                MenuScreen(
                    vm = menuVm,
                    nombreUsuario = usuario?.nombre?.split(" ")?.firstOrNull() ?: "estudiante",
                    onProducto = { productoId ->
                        navController.navigate(Rutas.detalle(productoId))
                    },
                    onConfirmar = { navController.navigate(Rutas.CONFIRMAR) }
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

            composable(Rutas.PANEL) {
                PanelScreen(
                    vm = panelVm,
                    onPedidos = { navController.navigate(Rutas.PEDIDOS_ADMIN) },
                    onMenu = { navController.navigate(Rutas.MENU_ADMIN) }
                )
            }

            composable(Rutas.PEDIDOS_ADMIN) {
                PedidosAdminScreen(
                    vm = pedidosAdminVm,
                    onPedido = { pedidoId ->
                        navController.navigate(Rutas.detallePedidoAdmin(pedidoId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Rutas.DETALLE_PEDIDO_ADMIN,
                arguments = listOf(navArgument("pedidoId") { type = NavType.IntType })
            ) { entry ->
                val pedidoId = entry.arguments?.getInt("pedidoId") ?: -1
                DetallePedidoAdminScreen(
                    vm = pedidosAdminVm,
                    pedidoId = pedidoId,
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Rutas.MENU_ADMIN) {
                MenuAdminScreen(
                    vm = menuAdminVm,
                    onAgregar = { navController.navigate(Rutas.formularioProducto()) },
                    onEditar = { productoId ->
                        navController.navigate(Rutas.formularioProducto(productoId))
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(
                route = Rutas.FORMULARIO_PRODUCTO,
                arguments = listOf(navArgument("productoId") {
                    type = NavType.IntType
                    defaultValue = -1
                })
            ) { entry ->
                val productoId = entry.arguments?.getInt("productoId")?.takeIf { it > 0 }
                FormularioProductoScreen(
                    vm = menuAdminVm,
                    productoId = productoId,
                    onGuardado = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Rutas.AJUSTES) {
                AjustesScreen(
                    vm = sesion,
                    onCambiarPerfil = { nuevoRol ->
                        sesion.cambiarPerfil(nuevoRol)
                        irADestinoPrincipal()
                    },
                    onCerrarSesion = {
                        sesion.cerrarSesion()
                        irALogin()
                    }
                )
            }

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
}
