package com.quispe.campusgps.util

import android.location.Location
import com.quispe.campusgps.model.Lugar
import com.quispe.campusgps.model.Posicion

fun distanciaMetros(desde: Posicion, hasta: Lugar): Float {
    val resultado = FloatArray(1)
    Location.distanceBetween(
        desde.latitud, desde.longitud,
        hasta.latitud, hasta.longitud,
        resultado
    )
    return resultado[0]
}
