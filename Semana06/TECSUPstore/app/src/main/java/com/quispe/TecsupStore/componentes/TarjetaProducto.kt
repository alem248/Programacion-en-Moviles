package com.quispe.TecsupStore.componentes

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

var expanded by remember { mutableStateOf(false) }

Box(contentAlignment = Alignment.TopEnd) @Composable {
    IconButton(onClick = { expanded = true }) {
        Icon(Icons.Default.MoreVert, contentDescription = "Opciones")
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        DropdownMenuItem(text = { Text("Favoritos") }, onClick = { expanded = false })
        DropdownMenuItem(text = { Text("Compartir") }, onClick = { expanded = false })
        DropdownMenuItem(text = { Text("Reportar") }, onClick = { expanded = false })
    }
}
