package com.quispe.clinicasalud.modelos

import androidx.compose.runtime.mutableStateListOf
import com.quispe.clinicasalud.R

data class Doctor(
    val id: Int,
    val nombre: String,
    val especialidad: String,
    val biografia: String,
    val imagenRes: Int,
    val calificacion: Double,
    val resenas: Int
)

data class Especialidad(
    val nombre: String,
    val imagenRes: Int
)

enum class EstadoCita {
    CONFIRMADA,
    COMPLETADA
}

data class Cita(
    val doctor: Doctor,
    val fecha: String,
    val hora: String,
    val estado: EstadoCita = EstadoCita.CONFIRMADA
)

val listaEspecialidades = listOf(
    Especialidad("Cardiología", R.drawable.ic_cardiologia),
    Especialidad("Pediatría", R.drawable.ic_pediatria),
    Especialidad("Dermatología", R.drawable.ic_dermatologia),
    Especialidad("General", R.drawable.ic_general)
)

val listaDoctores = listOf(
    Doctor(
        id = 1,
        nombre = "Dra. Ana Torres",
        especialidad = "Cardiología",
        biografia = "Especialista en salud cardiovascular con 10 años de experiencia en electrocardiografía y prevención.",
        imagenRes = R.drawable.ic_cardiologia,
        calificacion = 4.9,
        resenas = 120
    ),
    Doctor(
        id = 2,
        nombre = "Dr. Luis Vega",
        especialidad = "Cardiología",
        biografia = "Cardiólogo dedicado al control de hipertensión y a la rehabilitación de pacientes posoperatorios.",
        imagenRes = R.drawable.ic_cardiologia,
        calificacion = 4.7,
        resenas = 86
    ),
    Doctor(
        id = 3,
        nombre = "Dra. Rosa Díaz",
        especialidad = "Dermatología",
        biografia = "Experta en tratamientos cutáneos, control de lesiones y procedimientos estéticos.",
        imagenRes = R.drawable.ic_dermatologia,
        calificacion = 4.6,
        resenas = 74
    ),
    Doctor(
        id = 4,
        nombre = "Dr. Marcos Solís",
        especialidad = "Pediatría",
        biografia = "Atención integral para niños y adolescentes, con enfoque en prevención y crecimiento.",
        imagenRes = R.drawable.ic_pediatria,
        calificacion = 4.8,
        resenas = 95
    ),
    Doctor(
        id = 5,
        nombre = "Dra. Carla Ibáñez",
        especialidad = "Pediatría",
        biografia = "Neonatóloga con 8 años de experiencia en atención hospitalaria pediátrica.",
        imagenRes = R.drawable.ic_pediatria,
        calificacion = 4.5,
        resenas = 61
    ),
    Doctor(
        id = 6,
        nombre = "Dr. Jorge Paredes",
        especialidad = "Medicina General",
        biografia = "Consulta de primer contacto, chequeos generales y seguimiento de enfermedades crónicas.",
        imagenRes = R.drawable.ic_general,
        calificacion = 4.4,
        resenas = 52
    )
)

val listaFechas = listOf("Vie 26", "Sáb 27", "Dom 28", "Lun 30")

val listaHorarios = listOf(
    "9:00 am", "10:30 am", "12:00 pm",
    "2:00 pm", "4:00 pm", "5:30 pm"
)

fun doctorPorId(id: Int): Doctor = listaDoctores.firstOrNull { it.id == id } ?: listaDoctores.first()

// Estado global de citas, con datos iniciales de demostracion
val citasAgendadas = mutableStateListOf(
    Cita(doctorPorId(1), "Sáb 27", "10:30 am", EstadoCita.CONFIRMADA),
    Cita(doctorPorId(4), "Mié 24", "3:00 pm", EstadoCita.COMPLETADA),
    Cita(doctorPorId(3), "Lun 22", "9:00 am", EstadoCita.COMPLETADA)
)
