package com.quispe.coderole.domain.repository

import com.quispe.coderole.domain.model.PerfilUsuario
import kotlinx.coroutines.flow.Flow

/** Lectura reactiva y edición del perfil del usuario en Firestore. */
interface PerfilRepository {
    /**
     * Escucha el documento del usuario: cada emisión trae el nombre y la foto
     * actuales, de modo que la interfaz se actualiza en tiempo real.
     */
    fun observar(uid: String, correo: String): Flow<PerfilUsuario>

    /** Guarda el nombre de usuario. */
    suspend fun actualizarNombre(nombre: String): Result<Unit>

    /** Guarda la foto en Base64; `null` la elimina. */
    suspend fun actualizarFoto(fotoBase64: String?): Result<Unit>
}
