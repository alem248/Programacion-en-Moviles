package com.quispe.campusgps.data

import com.quispe.campusgps.model.Lugar

interface LugaresRepository {
    fun obtenerLugares(): List<Lugar>
}
