package com.tecsup.mibodega.ui.cliente.navegacion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.tecsup.mibodega.ui.cliente.modelo.ItemCarrito
import com.tecsup.mibodega.ui.cliente.modelo.Producto
import com.tecsup.mibodega.ui.cliente.modelo.listaProductosFake
import com.tecsup.mibodega.ui.cliente.screens.inicio.PantallaInicio
import com.tecsup.mibodega.ui.cliente.screens.login.PantallaLogin
import com.tecsup.mibodega.ui.cliente.screens.registro.PantallaCrearCuenta

/**
 * Estructura de navegación de la app cliente (NavHost + estado del carrito).
 * Todas las rutas se registran aquí; mientras una pantalla no exista,
 * su ruta muestra un placeholder que se reemplaza en cada hito.
 */
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    // El carrito vive aquí arriba (state hoisting), no en ninguna pantalla.
    var carrito by remember { mutableStateOf<List<ItemCarrito>>(emptyList()) }

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) {
            PantallaLogin(
                onRegistrarse = { navController.navigate(Rutas.CREAR_CUENTA) },
                onIniciarSesion = {
                    // Login -> Inicio: se limpia el back stack para no
                    // volver a la pantalla de acceso con el botón atrás.
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Rutas.CREAR_CUENTA) {
            PantallaCrearCuenta(
                onVolver = { navController.popBackStack() },
                onCrearCuenta = { _, _, _, _ ->
                    navController.navigate(Rutas.INICIO) {
                        popUpTo(Rutas.LOGIN) { inclusive = true }
                    }
                }
            )
        }
        composable(Rutas.INICIO) {
            PantallaInicio(
                productos = listaProductosFake,
                cantidadCarrito = carrito.sumOf { it.cantidad },
                onVerCarrito = { navController.navigate(Rutas.CARRITO) },
                onProductoClick = { producto ->
                    navController.navigate(Rutas.detalle(producto.id))
                },
                onAgregarProducto = { producto ->
                    carrito = agregarOSumarProducto(carrito, producto, cantidad = 1)
                }
            )
        }
        composable(
            route = Rutas.DETALLE,
            arguments = listOf(navArgument("productoId") { type = NavType.IntType })
        ) { PantallaPlaceholder("Detalle") }
        composable(Rutas.CARRITO) { PantallaPlaceholder("Carrito") }
        composable(Rutas.ENTREGA) { PantallaPlaceholder("Datos de entrega") }
        composable(
            route = Rutas.CONFIRMACION,
            arguments = listOf(navArgument("total") { type = NavType.FloatType })
        ) { PantallaPlaceholder("Confirmación") }
    }
}

/**
 * Contenido temporal de una ruta cuya pantalla aún no está implementada.
 */
@Composable
private fun PantallaPlaceholder(nombre: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = nombre)
    }
}

/**
 * Si el producto ya está en el carrito, le suma la cantidad;
 * si no, lo agrega como un ItemCarrito nuevo.
 */
private fun agregarOSumarProducto(
    carrito: List<ItemCarrito>,
    producto: Producto,
    cantidad: Int
): List<ItemCarrito> {
    val itemExistente = carrito.find { it.producto.id == producto.id }
    return if (itemExistente != null) {
        carrito.map {
            if (it.producto.id == producto.id) it.copy(cantidad = it.cantidad + cantidad) else it
        }
    } else {
        carrito + ItemCarrito(producto = producto, cantidad = cantidad)
    }
}
