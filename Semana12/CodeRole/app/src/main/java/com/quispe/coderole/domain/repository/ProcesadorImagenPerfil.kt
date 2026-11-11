package com.quispe.coderole.domain.repository

/** Procesa la foto elegida sin exponer tipos Android al ViewModel. */
fun interface ProcesadorImagenPerfil {
    suspend fun convertir(uri: String): Result<String>
}
