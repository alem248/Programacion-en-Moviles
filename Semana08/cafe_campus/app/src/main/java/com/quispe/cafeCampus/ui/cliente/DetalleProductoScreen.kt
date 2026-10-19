package com.quispe.cafeCampus.ui.cliente

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.ui.components.ProductoIcono
import com.quispe.cafeCampus.viewmodel.MenuViewModel

@Composable
fun DetalleProductoScreen(
    vm: MenuViewModel,
    productoId: Int,
    onBack: () -> Unit
) {
    val productos by vm.productos.collectAsState()
    val producto = productos.find { it.id == productoId }

    var cantidad by remember { mutableIntStateOf(1) }
    var nota by remember { mutableStateOf("") }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Detalle", onBack = onBack)

        if (producto == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Producto no encontrado",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            ProductoIcono(
                icono = producto.icono,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = producto.nombre,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = if (producto.disponible) "Disponible" else "Agotado",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (producto.disponible) ColorVerde else MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (producto.disponible) ColorVerde.copy(alpha = 0.15f)
                            else MaterialTheme.colorScheme.error.copy(alpha = 0.12f)
                        )
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
            Spacer(Modifier.height(8.dp))
            Text(
                text = "S/ ${"%.2f".format(producto.precio)}",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = producto.descripcion,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Cantidad", fontWeight = FontWeight.SemiBold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = { if (cantidad > 1) cantidad-- },
                        enabled = cantidad > 1,
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Restar")
                    }
                    Text(
                        text = "$cantidad",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        modifier = Modifier.width(32.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    IconButton(
                        onClick = { cantidad++ },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Sumar")
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = nota,
                onValueChange = { nota = it },
                label = { Text("Nota para la cocina (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(24.dp))

            CafeButton(
                texto = "Agregar al pedido · S/ ${"%.2f".format(producto.precio * cantidad)}",
                onClick = {
                    vm.agregar(producto, cantidad, nota)
                    onBack()
                },
                habilitado = producto.disponible
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

private val ColorVerde = androidx.compose.ui.graphics.Color(0xFF22C55E)
