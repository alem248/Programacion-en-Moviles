package com.quispe.TecsupStore.componentes

import androidx.compose.foundation.layout.Box
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@Composable
fun MenuOpcionesProducto() {
    var expanded by remember { mutableStateOf(false) }

    Box(contentAlignment = Alignment.TopEnd) {
        IconButton(onClick = { expanded = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("Favoritos") },
                leadingIcon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                onClick = { expanded = false }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Compartir") },
                leadingIcon = { Icon(Icons.Default.Share, contentDescription = null) },
                onClick = { expanded = false }
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text("Reportar") },
                leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null) },
                onClick = { expanded = false }
            )
        }
    }
}