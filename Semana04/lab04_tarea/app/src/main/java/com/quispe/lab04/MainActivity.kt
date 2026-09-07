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

