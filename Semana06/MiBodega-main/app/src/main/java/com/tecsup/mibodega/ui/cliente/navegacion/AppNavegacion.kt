package com.tecsup.mibodega.ui.cliente.navegacion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

/**
 * Estructura de navegación de la app cliente (NavHost + estado del carrito).
 * Todas las rutas se registran aquí; mientras una pantalla no exista,
 * su ruta muestra un placeholder que se reemplaza en cada hito.
 */
@Composable
fun AppNavegacion() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Rutas.LOGIN
    ) {
        composable(Rutas.LOGIN) { PantallaPlaceholder("Login") }
        composable(Rutas.CREAR_CUENTA) { PantallaPlaceholder("Crear cuenta") }
        composable(Rutas.INICIO) { PantallaPlaceholder("Inicio") }
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
