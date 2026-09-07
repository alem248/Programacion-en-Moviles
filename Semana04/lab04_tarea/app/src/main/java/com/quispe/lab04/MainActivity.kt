package com.quispe.lab04

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.lab04.ui.theme.Lab04Theme
import kotlin.math.roundToInt

import androidx.compose.ui.tooling.preview.Preview

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegistroNotasPreview() {
    Lab04Theme {
        RegistroNotasScreen()
    }
}
data class Curso(val id: Int, val nombre: String, val peso: Int, val nota: Int = 0)

class NotasViewModel : ViewModel() {

    var cursos = mutableStateListOf(
        Curso(1, "Fundamentos de Programación", 20),
        Curso(2, "Programación Orientada a Objetos", 25),
        Curso(3, "Programación en Móviles", 30),
        Curso(4, "Base de Datos", 25)
    )
        private set

    var redondear by mutableStateOf(false)
        private set

    var confirmado by mutableStateOf(false)
        private set

    var mostrarResultados by mutableStateOf(false)
        private set

    var promedioPonderado by mutableDoubleStateOf(0.0)
        private set

    var promedioFinal by mutableDoubleStateOf(0.0)
        private set

    var observacion by mutableStateOf("")
        private set

    var colorObservacion by mutableStateOf(Color.Unspecified)
        private set

    fun actualizarNota(id: Int, nuevaNota: Int) {
        val index = cursos.indexOfFirst { it.id == id }
        if (index != -1) {
            cursos[index] = cursos[index].copy(nota = nuevaNota)
            mostrarResultados = false
        }
    }

    fun toggleRedondear(valor: Boolean) {
        redondear = valor
        if (mostrarResultados) calcularPromedio()
    }

    fun toggleConfirmacion(valor: Boolean) {
        confirmado = valor
    }

    fun calcularPromedio() {

        promedioPonderado = cursos.sumOf { it.nota * (it.peso / 100.0) }

        promedioFinal = if (redondear) promedioPonderado.roundToInt().toDouble() else promedioPonderado

        when {
            promedioFinal >= 17 -> {
                observacion = "EXCELENTE"
                colorObservacion = Color(0xFF1B5E20)
            }
            promedioFinal >= 13 -> {
                observacion = "APROBADO"
                colorObservacion = Color(0xFF4CAF50)
            }
            promedioFinal >= 10 -> {
                observacion = "EN RECUPERACIÓN"
                colorObservacion = Color(0xFFFFB300)
            }
            else -> {
                observacion = "DESAPROBADO"
                colorObservacion = Color(0xFFE53935)
            }
        }
        mostrarResultados = true
    }

    fun limpiar() {
        cursos.forEachIndexed { index, curso ->
            cursos[index] = curso.copy(nota = 0)
        }
        redondear = false
        confirmado = false
        mostrarResultados = false
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab04Theme {
                RegistroNotasScreen()
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroNotasScreen(viewModel: NotasViewModel = viewModel()) {
    val degradado = Brush.verticalGradient(
        colors = listOf(Color(0xFFEDE7F6), Color(0xFFFFFFFF))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registro de Notas", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF5E35B1),
                    titleContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(degradado)
                .padding(innerPadding)
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Notas del ciclo",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Desliza para asignar cada nota (0 a 20)",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )

            viewModel.cursos.forEach { curso ->
                CursoSliderRow(
                    curso = curso,
                    onNotaChange = { viewModel.actualizarNota(curso.id, it) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Redondear promedio final", fontSize = 15.sp)
                Switch(
                    checked = viewModel.redondear,
                    onCheckedChange = { viewModel.toggleRedondear(it) }
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = viewModel.confirmado,
                    onCheckedChange = { viewModel.toggleConfirmacion(it) }
                )
                Text("Confirmo que las notas son correctas", fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { viewModel.calcularPromedio() },
                enabled = viewModel.confirmado,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF5E35B1),
                    disabledContainerColor = Color(0xFFC2B8D9)
                )
            ) {
                Text("CALCULAR PROMEDIO", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (viewModel.mostrarResultados) {
                ResultadosCard(viewModel)

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "✓ Promedio calculado correctamente",
                    color = Color(0xFF2E7D32),
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(4.dp))
                TextButton(onClick = { viewModel.limpiar() }) {
                    Text("LIMPIAR", color = Color.Gray)
                }
            } else {
                Text(
                    text = "Asigna las notas y confirma para calcular",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.weight(1f, fill = false))

            Text(
                text = "Desarrollado por: Alexandra Ximena Quispe Mallqui",
                color = Color.Gray,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 32.dp, bottom = 8.dp)
            )
        }
    }
}
@Composable
fun CursoSliderRow(curso: Curso, onNotaChange: (Int) -> Unit) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = curso.nombre, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                Text(
                    text = " (${curso.peso}%)",
                    fontSize = 12.sp,
                    color = Color(0xFF5E35B1)
                )
            }

            val esAprobatorio = curso.nota >= 13
            val badgeColor = if (esAprobatorio) Color(0xFFE8F5E9) else Color(0xFFF3E5F5)
            val textColor = if (esAprobatorio) Color(0xFF2E7D32) else Color(0xFF5E35B1)

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = badgeColor,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(
                    text = curso.nota.toString(),
                    color = textColor,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }

        Slider(
            value = curso.nota.toFloat(),
            onValueChange = { onNotaChange(it.toInt()) },
            valueRange = 0f..20f,
            steps = 19,
            colors = SliderDefaults.colors(
                thumbColor = Color(0xFF5E35B1),
                activeTrackColor = Color(0xFF5E35B1),
                activeTickColor = Color.Transparent,
                inactiveTickColor = Color.Transparent
            )
        )
    }
}

