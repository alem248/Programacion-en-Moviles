package com.quispe.cafeCampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.ProductoCard
import com.quispe.cafeCampus.viewmodel.MenuViewModel

@Composable
fun MenuScreen(
    vm: MenuViewModel,
    onProducto: (Int) -> Unit
) {
    val productos by vm.productos.collectAsState()
    val categoria by vm.categoriaSeleccionada.collectAsState()
    val busqueda by vm.busqueda.collectAsState()
    val carrito by vm.carrito.collectAsState()

    val categorias = listOf("Todo") + productos.map { it.categoria }.distinct().sorted()

    val filtrados = productos.filter { producto ->
        val coincideCategoria = categoria == "Todo" || producto.categoria == categoria
        val coincideBusqueda = busqueda.isBlank() ||
                producto.nombre.contains(busqueda, ignoreCase = true) ||
                producto.descripcion.contains(busqueda, ignoreCase = true)
        coincideCategoria && coincideBusqueda
    }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(
            titulo = "CafeCampus",
            actions = {
                BadgedBox(
                    badge = { if (carrito.isNotEmpty()) Badge { Text("${carrito.sumOf { it.cantidad }}") } },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    IconButton(onClick = { /* TODO (Paso 7): abrir carrito */ }) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag,
                            contentDescription = "Bolsa",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "¡Hola, Juan!",
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "¿Qué vas a pedir hoy?",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = busqueda,
                onValueChange = vm::buscar,
                placeholder = { Text("Buscar productos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(10.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categorias) { opcion ->
                    val seleccionada = opcion == categoria
                    SuggestionChip(
                        onClick = { vm.seleccionarCategoria(opcion) },
                        label = { Text(opcion, fontSize = 13.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = if (seleccionada) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceVariant
                            },
                            labelColor = if (seleccionada) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            }
                        )
                    )
                }
            }
            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                if (filtrados.isEmpty()) {
                    item(span = { GridItemSpan(2) }) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 48.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No se encontraron productos",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(filtrados, key = { it.id }) { producto ->
                        ProductoCard(
                            producto = producto,
                            onProducto = { onProducto(producto.id) },
                            onAgregar = {
                                vm.agregar(producto, cantidad = 1, nota = "")
                            }
                        )
                    }
                }
            }
        }
    }
}
