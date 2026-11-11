package com.quispe.coderole.data.media

import android.content.Context
import android.net.Uri
import android.util.Base64
import com.quispe.coderole.domain.repository.ProcesadorImagenCapa

class ProcesadorImagenCapaAndroid(contexto: Context) : ProcesadorImagenCapa {
    private val appContext = contexto.applicationContext

    override suspend fun convertir(uri: String): Result<String> =
        Imagenes.pngParaCapa(appContext, Uri.parse(uri)).map { Base64.encodeToString(it, Base64.NO_WRAP) }
}
