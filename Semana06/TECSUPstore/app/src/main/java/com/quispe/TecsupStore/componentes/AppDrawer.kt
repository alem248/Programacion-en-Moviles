package com.quispe.TecsupStore.componentes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

data class OpcionDrawer(
    val titulo: String,
    val icono: ImageVector
)

@Composable
fun AppDrawer(
    rutaActual: String,
    navegarA: (String) -> Unit,
    cerrarDrawer: () -> Unit
) {
    ModalDrawerSheet {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.padding(end = 12.dp)
            ) {
                Text(
                    text = "AQ",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(16.dp)
                )
            }
            Column {
                Text(
                    text = "Alexandra Quispe",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "alexandra.quispe.m@tecsup.edu.pe",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(12.dp))

        // Lista de opciones del menú con sus respectivos íconos
        val opciones = listOf(
            OpcionDrawer("Inicio", Icons.Default.Home),
            OpcionDrawer("Mis pedidos", Icons.Default.ShoppingBag),
            OpcionDrawer("Favoritos", Icons.Default.Favorite),
            OpcionDrawer("Perfil", Icons.Default.Person),
            OpcionDrawer("Cerrar sesión", Icons.Default.ExitToApp)
        )

        opciones.forEach { opcion ->
            val estaActivo = rutaActual == opcion.titulo

            NavigationDrawerItem(
                label = { Text(opcion.titulo) },
                icon = {
                    Icon(
                        imageVector = opcion.icono,
                        contentDescription = opcion.titulo
                    )
                },
                selected = estaActivo,
                onClick = {
                    navegarA(opcion.titulo)
                    cerrarDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
            )
        }
    }
}