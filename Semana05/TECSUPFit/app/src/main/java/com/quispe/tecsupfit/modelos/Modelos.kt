package com.quispe.tecsupfit.modelos

import androidx.compose.runtime.mutableStateListOf

data class ClaseFit(
    val id: Int,
    val nombre: String,
    val dia: String,
    val horario: String,
    val sala: String,
    val duracion: String,
    val descripcion: String,
    val cupos: Int
)

enum class EstadoReserva {
    CONFIRMADA,
    COMPLETADA
}

data class Reserva(
    val clase: ClaseFit,
    val estado: EstadoReserva = EstadoReserva.CONFIRMADA
)

val clasesDisponibles = mutableStateListOf(
    ClaseFit(
        id = 1,
        nombre = "Yoga funcional",
        dia = "Lunes",
        horario = "8:00 am - 9:00 am",
        sala = "Sala 1",
        duracion = "60 min",
        descripcion = "Sesion de yoga con ejercicios de movilidad y control muscular para mejorar la postura y la flexibilidad.",
        cupos = 12
    ),
    ClaseFit(
        id = 2,
        nombre = "Cross Training",
        dia = "Martes",
        horario = "6:00 pm - 7:00 pm",
        sala = "Sala 2",
        duracion = "60 min",
        descripcion = "Entrenamiento funcional de alta intensidad que combina pesas, remo y ejercicios de cuerpo completo.",
        cupos = 8
    ),
    ClaseFit(
        id = 3,
        nombre = "Spinning",
        dia = "Miercoles",
        horario = "7:00 am - 7:45 am",
        sala = "Sala 3",
        duracion = "45 min",
        descripcion = "Clase de ciclismo indoor con ritmo musical y cambios de intensidad para quemar calorias.",
        cupos = 15
    ),
    ClaseFit(
        id = 4,
        nombre = "HIIT Express",
        dia = "Jueves",
        horario = "1:00 pm - 1:30 pm",
        sala = "Sala 2",
        duracion = "30 min",
        descripcion = "Bloques cortos de ejercicios de alta intensidad con descanso minimo, ideal para la hora de almuerzo.",
        cupos = 10
    )
)

fun clasePorId(id: Int): ClaseFit = clasesDisponibles.firstOrNull { it.id == id } ?: clasesDisponibles.first()

// Descuenta un cupo de la clase seleccionada cuando se confirma la reserva
fun descontarCupo(id: Int) {
    val indice = clasesDisponibles.indexOfFirst { it.id == id }
    if (indice >= 0 && clasesDisponibles[indice].cupos > 0) {
        clasesDisponibles[indice] = clasesDisponibles[indice].copy(cupos = clasesDisponibles[indice].cupos - 1)
    }
}
