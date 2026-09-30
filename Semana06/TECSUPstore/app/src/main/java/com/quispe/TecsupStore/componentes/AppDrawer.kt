package com.quispe.TecsupStore.componentes

import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.runtime.Composable

@Composable
fun AppDrawer(rutaActual: String, navegarA: (String) -> Unit, cerrarDrawer: () -> Unit) {
    ModalDrawerSheet {
        val items = listOf("Inicio", "Mis pedidos", "Favoritos", "Perfil")
        items.forEach { item ->
            NavigationDrawerItem(
                label = { Text(item) },
                selected = false, // Se actualizará en el siguiente commit
                onClick = { /* Lógica pendiente */ }
            )
        }
    }
}