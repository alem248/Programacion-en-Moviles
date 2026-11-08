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
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Badge
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun AppDrawer(
    rutaActual: String,
    cantidadFavoritos: Int = 0,
    navegarA: (String) -> Unit,
    cerrarDrawer: () -> Unit
) {
    val moradoTecsup = Color(0xFF4A148C)
    val moradoClaro = Color(0xFFEADDFF)

    ModalDrawerSheet {
        // Encabezado (Avatar "MR" o "AQ" e información)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = CircleShape,
                color = moradoClaro,
                modifier = Modifier.padding(end = 16.dp)
            ) {
                Text(
                    text = "AQ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = moradoTecsup,
                    modifier = Modifier.padding(14.dp)
                )
            }
            Column {
                Text(
                    text = "Alexandra Quispe",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "ximenaperu13@gmail.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(horizontal = 20.dp))
        Spacer(modifier = Modifier.height(16.dp))

        val opciones = listOf("Inicio", "Mis pedidos", "Favoritos", "Perfil", "Cerrar sesion")

        val badgeFavoritos: (@Composable () -> Unit)? = {
            Badge(
                containerColor = moradoTecsup,
                contentColor = Color.White
            ) {
                Text(text = cantidadFavoritos.toString())
            }
        }

        opciones.forEach { opcion ->
            val estaActivo = rutaActual == opcion

            NavigationDrawerItem(
                label = {
                    Text(
                        text = opcion,
                        fontWeight = if (estaActivo) FontWeight.Bold else FontWeight.Normal
                    )
                },
                icon = {
                    Icon(
                        imageVector = Icons.Outlined.Circle,
                        contentDescription = null,
                        tint = if (estaActivo) moradoTecsup else Color.Gray
                    )
                },
                badge = if (opcion == "Favoritos") badgeFavoritos else null,
                selected = estaActivo,
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = moradoClaro,
                    selectedIconColor = moradoTecsup,
                    selectedTextColor = moradoTecsup
                ),
                onClick = {
                    navegarA(opcion)
                    cerrarDrawer()
                },
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
            )
        }
    }
}