package com.quispe.coderole.data.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import java.io.ByteArrayOutputStream
import java.io.IOException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Lectura y preparación de los PNG que el mentor asocia a las capas del
 * personaje: valida que sea PNG, recorta a un tamaño razonable y vuelve a
 * codificar para que el archivo que viaja a Firestore no supere su límite.
 */
object ImagenesPng {
    /** Lado máximo del PNG reescalado. */
    private const val LADO_MAXIMO = 440

    private val FIRMA_PNG = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)

    suspend fun desdeUri(contexto: Context, uri: Uri): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val original = contexto.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("No se pudo abrir la imagen elegida.")
            if (!esPng(original)) throw IllegalArgumentException("Elige un archivo con extensión .png.")
            reescalar(original) ?: throw IOException("No se pudo procesar la imagen elegida.")
        }
    }

    fun esPng(bytes: ByteArray): Boolean {
        if (bytes.size < FIRMA_PNG.size) return false
        return FIRMA_PNG.indices.all { bytes[it] == FIRMA_PNG[it] }
    }

    /** Reduce la imagen y la vuelve a guardar como PNG para mantenerla ligera. */
    private fun reescalar(bytes: ByteArray): ByteArray? {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, limites)
        if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

        var muestra = 1
        while (limites.outWidth / muestra > LADO_MAXIMO || limites.outHeight / muestra > LADO_MAXIMO) {
            muestra *= 2
        }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, BitmapFactory.Options().apply { inSampleSize = muestra })
            ?: return null

        val salida = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, salida)
        bitmap.recycle()
        return salida.toByteArray()
    }
}
