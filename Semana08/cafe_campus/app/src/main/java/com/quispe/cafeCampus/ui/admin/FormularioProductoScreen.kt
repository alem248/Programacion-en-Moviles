package com.quispe.cafeCampus.ui.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.quispe.cafeCampus.data.local.entity.Producto
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.viewmodel.MenuAdminViewModel

private val categorias = listOf("Bebidas", "Desayunos", "Almuerzos", "Snacks")
private val imagenes = listOf("cafe", "plato", "menu_dia", "jugo", "pan", "botella", "dulce")

@Composable
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
fun FormularioProductoScreen(
    vm: MenuAdminViewModel,
    productoId: Int?,
    onGuardado: () -> Unit,
    onBack: () -> Unit
) {
    val existente = productoId?.takeIf { it > 0 }?.let { vm.producto(it) }

    var nombre by remember { mutableStateOf(existente?.nombre ?: "") }
    var categoria by remember { mutableStateOf(existente?.categoria ?: categorias.first()) }
    var precio by remember { mutableStateOf(existente?.precio?.let { "%.2f".format(it) } ?: "") }
    var descripcion by remember { mutableStateOf(existente?.descripcion ?: "") }
    var imagen by remember { mutableStateOf(existente?.icono ?: imagenes.first()) }
    var disponible by remember { mutableStateOf(existente?.disponible ?: true) }

    var intentado by remember { mutableStateOf(false) }
    var expandedCat by remember { mutableStateOf(false) }
    var expandedImg by remember { mutableStateOf(false) }

    val precioValor = precio.toDoubleOrNull()
    val nombreError = intentado && nombre.isBlank()
    val precioError = intentado && (precioValor == null || precioValor <= 0.0)

    Column(Modifier.fillMaxSize()) {
        AppTopBar(
            titulo = if (existente == null) "Nuevo producto" else "Editar producto",
            onBack = onBack
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth(),
                isError = nombreError,
                supportingText = if (nombreError) {
                    { Text("El nombre es obligatorio") }
                } else null,
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedCat,
                onExpandedChange = { expandedCat = it }
            ) {
                OutlinedTextField(
                    value = categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedCat)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedCat,
                    onDismissRequest = { expandedCat = false }
                ) {
                    categorias.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                categoria = opcion
                                expandedCat = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = precio,
                onValueChange = { precio = it },
                label = { Text("Precio (S/) *") },
                modifier = Modifier.fillMaxWidth(),
                isError = precioError,
                supportingText = if (precioError) {
                    { Text("Ingresa un precio mayor a 0") }
                } else null,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(12.dp))

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary
                )
            )
            Spacer(Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expandedImg,
                onExpandedChange = { expandedImg = it }
            ) {
                OutlinedTextField(
                    value = imagen,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Imagen") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedImg)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary
                    )
                )
                ExposedDropdownMenu(
                    expanded = expandedImg,
                    onDismissRequest = { expandedImg = false }
                ) {
                    imagenes.forEach { opcion ->
                        DropdownMenuItem(
                            text = { Text(opcion) },
                            onClick = {
                                imagen = opcion
                                expandedImg = false
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text("Disponible", fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "Los estudiantes pueden pedirlo",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Switch(checked = disponible, onCheckedChange = { disponible = it })
            }

            Spacer(Modifier.height(24.dp))
            CafeButton(
                texto = if (existente == null) "Guardar producto" else "Guardar cambios",
                onClick = {
                    intentado = true
                    if (nombre.isBlank() || precioValor == null || precioValor <= 0.0) {
                        return@CafeButton
                    }
                    vm.guardar(
                        Producto(
                            id = existente?.id ?: 0,
                            nombre = nombre.trim(),
                            descripcion = descripcion.trim(),
                            categoria = categoria,
                            precio = precioValor,
                            disponible = disponible,
                            icono = imagen
                        )
                    )
                    onGuardado()
                }
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}
