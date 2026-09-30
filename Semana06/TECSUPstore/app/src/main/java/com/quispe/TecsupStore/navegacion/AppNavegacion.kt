package com.quispe.TecsupStore.navegacion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.quispe.TecsupStore.componentes.AppDrawer
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var rutaActual by remember { mutableStateOf("Inicio") }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                rutaActual = rutaActual,
                navegarA = { nuevaRuta ->
                    rutaActual = nuevaRuta
                },
                cerrarDrawer = {
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = "TECSUP Store") },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch { drawerState.open() }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Abrir menú"
                            )
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                when (rutaActual) {
                    "Inicio" -> Text("Pantalla de Inicio")
                    "Mis pedidos" -> Text("Pantalla de Mis Pedidos")
                    "Favoritos" -> Text("Pantalla de Favoritos")
                    "Perfil" -> Text("Pantalla de Perfil")
                    else -> Text("Pantalla de Inicio")
                }
            }
        }
    }
}