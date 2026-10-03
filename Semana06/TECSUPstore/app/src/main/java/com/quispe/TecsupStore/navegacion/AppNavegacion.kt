package com.quispe.TecsupStore.navegacion

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.TecsupStore.componentes.AppDrawer
import com.quispe.TecsupStore.componentes.Producto
import com.quispe.TecsupStore.pantallas.PantallaFavoritos
import com.quispe.TecsupStore.pantallas.PantallaInicio
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavegacion() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var rutaActual by remember { mutableStateOf("Inicio") }

    // Estado global de favoritos elevado a este contenedor (state hoisting)
    val favoritos = remember { mutableStateListOf<Producto>() }

    // Agrega o elimina el producto controlando que no se duplique en la lista
    val onToggleFavorito: (Producto) -> Unit = { producto ->
        val indice = favoritos.indexOfFirst { it.id == producto.id }
        if (indice >= 0) {
            favoritos.removeAt(indice)
        } else {
            favoritos.add(producto)
        }
    }

    val moradoTecsup = Color(0xFF4A148C)

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                rutaActual = rutaActual,
                cantidadFavoritos = favoritos.size,
                navegarA = { nuevaRuta -> rutaActual = nuevaRuta },
                cerrarDrawer = { scope.launch { drawerState.close() } }
            )
        }
    ) {
        Scaffold(
            topBar = {
                // TopBar personalizada en morado con título y subtítulo idéntica a la imagen
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(moradoTecsup)
                ) {
                    TopAppBar(
                        title = {
                            Text(
                                text = "TECSUP Store",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = "Abrir menú",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                    Text(
                        text = when (rutaActual) {
                            "Inicio" -> "Mas vendidos"
                            "Favoritos" -> "Tus productos guardados"
                            "Mis pedidos" -> "Historial de compras"
                            "Perfil" -> "Datos de la cuenta"
                            else -> "Mas vendidos"
                        },
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 16.dp, bottom = 12.dp)
                    )
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.TopCenter
            ) {
                when (rutaActual) {
                    "Inicio" -> PantallaInicio(
                        favoritos = favoritos,
                        onToggleFavorito = onToggleFavorito
                    )
                    "Mis pedidos" -> Text("Pantalla de Mis Pedidos", modifier = Modifier.padding(16.dp))
                    "Favoritos" -> PantallaFavoritos(
                        listaFavoritos = favoritos,
                        onToggleFavorito = onToggleFavorito
                    )
                    "Perfil" -> Text("Pantalla de Perfil", modifier = Modifier.padding(16.dp))
                    else -> PantallaInicio(
                        favoritos = favoritos,
                        onToggleFavorito = onToggleFavorito
                    )
                }
            }
        }
    }
}