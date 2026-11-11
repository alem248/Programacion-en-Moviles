package com.quispe.coderole.data.media

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.quispe.coderole.domain.repository.ProcesadorImagenPerfil

class ProcesadorImagenPerfilAndroid(contexto: Context) : ProcesadorImagenPerfil {
    private val appContext = contexto.applicationContext

    override suspend fun convertir(uri: String): Result<String> =
        Imagenes.jpegParaPerfil(appContext, Uri.parse(uri)).map { Base64.encodeToString(it, Base64.NO_WRAP) }
}
