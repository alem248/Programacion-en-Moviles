package com.quispe.coderole.domain.repository

import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.CapasPersonaje
import kotlinx.coroutines.flow.Flow

/**
 * Gestión de las piezas modulares del personaje (cabeza, cabello, cejas, ojos,
 * boca, accesorios…) desde el panel de administración. Los PNG se guardan en
 * Firestore como Base64 para que el cambio se refleje en todos los
 * dispositivos en tiempo real.
 */
interface CapasRepository {
    /** Estado actual de las piezas; cada emisión trae la última configuración. */
    fun observar(): Flow<CapasPersonaje>

    /** Asocia o reemplaza el PNG de una pieza. */
    suspend fun guardar(capa: CapaPersonaje, pngBase64: String): Result<Unit>

    /** Quita el PNG de una pieza y vuelve al SVG incluido en la app. */
    suspend fun quitar(capa: CapaPersonaje): Result<Unit>
}
