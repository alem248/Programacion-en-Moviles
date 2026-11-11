package com.quispe.coderole.domain.repository

/** Procesa la imagen elegida sin exponer tipos Android al ViewModel. */
fun interface ProcesadorImagenCapa {
    suspend fun convertir(uri: String): Result<String>
}
