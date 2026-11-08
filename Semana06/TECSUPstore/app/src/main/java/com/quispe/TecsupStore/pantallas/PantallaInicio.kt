package com.quispe.TecsupStore.pantallas

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.quispe.TecsupStore.componentes.Producto
import com.quispe.TecsupStore.componentes.TarjetaProducto

val listaProductosEjemplo = listOf(
    Producto(id = 1, nombre = "Audífonos", precio = 89.00),
    Producto(id = 2, nombre = "Smartwatch", precio = 199.00),
    Producto(id = 3, nombre = "Funda celular", precio = 25.00)
)

@Composable
fun PantallaInicio(
    favoritos: List<Producto>,
    onToggleFavorito: (Producto) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        items(
            items = listaProductosEjemplo,
            key = { it.id }
        ) { producto ->
            TarjetaProducto(
                producto = producto,
                esFavorito = favoritos.any { it.id == producto.id },
                onToggleFavorito = onToggleFavorito
            )
        }
    }
}