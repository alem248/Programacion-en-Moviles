package com.quispe.clinicasalud.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.quispe.clinicasalud.navegacion.AppNavigation
import com.quispe.clinicasalud.navegacion.Rutas
import kotlinx.coroutines.launch

private data class OpcionMenu(val titulo: String, val ruta: String)

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route

    val opciones = listOf(
        OpcionMenu("Inicio", Rutas.INICIO),
        OpcionMenu("Mis citas", Rutas.MIS_CITAS),
        OpcionMenu("Historial médico", Rutas.HISTORIAL),
        OpcionMenu("Perfil", Rutas.PERFIL_USUARIO)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "JP",
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column(modifier = Modifier.padding(start = 12.dp)) {
                        Text(text = "Juan Pérez", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "Paciente",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                HorizontalDivider()
                opciones.forEach { opcion ->
                    NavigationDrawerItem(
                        label = { Text(opcion.titulo) },
                        icon = { Icon(Icons.Outlined.Circle, contentDescription = null) },
                        selected = rutaActual == opcion.ruta,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(opcion.ruta) {
                                popUpTo(Rutas.INICIO)
                                launchSingleTop = true
                            }
                        },
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }
        }
    ) {
        AppNavigation(
            navController = navController,
            onMenuClick = { scope.launch { drawerState.open() } }
        )
    }
}
