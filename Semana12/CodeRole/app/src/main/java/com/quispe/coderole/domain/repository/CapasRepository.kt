package com.quispe.coderole.domain.repository

import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.CapasPersonaje
import kotlinx.coroutines.flow.Flow

/**
 * Gestión de las capas modulares del personaje (base, ojos y boca) desde el
 * panel de administración. Los PNG se guardan en Firestore como Base64 para
 * que el cambio se refleje en todos los dispositivos en tiempo real.
 */
interface CapasRepository {
    /** Estado actual de las capas; cada emisión trae la última configuración. */
    fun observar(): Flow<CapasPersonaje>

    /** Asocia o reemplaza el PNG de una capa. */
    suspend fun guardar(capa: CapaPersonaje, pngBase64: String): Result<Unit>

    /** Quita el PNG de una capa y vuelve al SVG incluido en la app. */
    suspend fun quitar(capa: CapaPersonaje): Result<Unit>
}
