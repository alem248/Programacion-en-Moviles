package com.quispe.controldetareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.quispe.controldetareas.ui.theme.ControlDeTareasTheme
import com.quispe.controldetareas.ui.theme.NavyBlue
import com.quispe.controldetareas.ui.theme.LightBackground
import com.quispe.controldetareas.ui.theme.CardBackground

data class Tarea(
    val id: Int,
    val nombre: String,
    val completada: Boolean = false
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ControlDeTareasTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = LightBackground
                ) { innerPadding ->
                    PantallaTareas(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun ItemTarea(
    tarea: Tarea,
    onEliminar: () -> Unit,
    onCambiarEstado: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = tarea.completada,
                    onCheckedChange = { onCambiarEstado(it) },
                    colors = CheckboxDefaults.colors(checkedColor = NavyBlue)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = tarea.nombre,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = FontWeight.Medium,
                        color = if (tarea.completada) Color.Gray else NavyBlue
                    )
                )
            }
            IconButton(onClick = onEliminar) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Eliminar tarea",
                    tint = Color(0xFFB0BEC5) // Gris azulado como en la imagen
                )
            }
        }
    }
}

@Composable
fun PantallaTareas(modifier: Modifier = Modifier) {
    var textoTarea by remember { mutableStateOf("") }
    var contadorId by remember { mutableStateOf(1) }
    val listaTareas = remember { mutableStateListOf<Tarea>() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Lista de tareas - Tecsup",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NavyBlue
            )
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = textoTarea,
            onValueChange = { textoTarea = it },
            label = { Text("¿Qué tarea tienes pendiente?") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = NavyBlue,
                unfocusedBorderColor = NavyBlue.copy(alpha = 0.5f),
                focusedLabelColor = NavyBlue
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (textoTarea.isNotBlank()) {
                    listaTareas.add(
                        Tarea(
                            id = contadorId,
                            nombre = textoTarea
                        )
                    )
                    contadorId++
                    textoTarea = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyBlue)
        ) {
            Text("Agregar tarea", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Total de tareas: ${listaTareas.size}",
            style = MaterialTheme.typography.titleMedium.copy(
                color = Color.DarkGray
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(listaTareas, key = { it.id }) { tarea ->
                ItemTarea(
                    tarea = tarea,
                    onEliminar = {
                        listaTareas.remove(tarea)
                    },
                    onCambiarEstado = { completada ->
                        val index = listaTareas.indexOf(tarea)
                        if (index != -1) {
                            listaTareas[index] = listaTareas[index].copy(completada = completada)
                        }
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPantallaTareas() {
    ControlDeTareasTheme {
        PantallaTareas()
    }
}