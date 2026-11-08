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
 * Lectura y preparación de imágenes: valida el formato, reduce el tamaño y
 * vuelve a codificar para que el archivo que viaja a Firestore no supere su
 * límite de un documento.
 */
object Imagenes {
    /** Lado máximo del PNG de las capas del personaje. */
    private const val LADO_CAPA = 440

    /** Lado máximo de la foto de perfil. */
    private const val LADO_PERFIL = 360

    private const val CALIDAD_PERFIL = 85

    private val FIRMA_PNG = byteArrayOf(0x89.toByte(), 0x50, 0x4E, 0x47)

    /** PNG reescalado listo para asociar a una capa del personaje. */
    suspend fun pngParaCapa(contexto: Context, uri: Uri): Result<ByteArray> =
        preparar(contexto, uri, LADO_CAPA, Bitmap.CompressFormat.PNG, 100, exigirPng = true)

    /** Foto de perfil en JPEG ligero; acepta JPG, PNG u otros formatos. */
    suspend fun jpegParaPerfil(contexto: Context, uri: Uri): Result<ByteArray> =
        preparar(contexto, uri, LADO_PERFIL, Bitmap.CompressFormat.JPEG, CALIDAD_PERFIL, exigirPng = false)

    fun esPng(bytes: ByteArray): Boolean {
        if (bytes.size < FIRMA_PNG.size) return false
        return FIRMA_PNG.indices.all { bytes[it] == FIRMA_PNG[it] }
    }

    private suspend fun preparar(
        contexto: Context,
        uri: Uri,
        ladoMaximo: Int,
        formato: Bitmap.CompressFormat,
        calidad: Int,
        exigirPng: Boolean,
    ): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val original = contexto.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                ?: throw IOException("No se pudo abrir la imagen elegida.")
            if (exigirPng && !esPng(original)) {
                throw IllegalArgumentException("Elige un archivo con extensión .png.")
            }
            reescalar(original, ladoMaximo, formato, calidad)
                ?: throw IOException("No se pudo procesar la imagen elegida.")
        }
    }

    /** Reduce la imagen y la vuelve a guardar en el formato indicado. */
    private fun reescalar(
        bytes: ByteArray,
        ladoMaximo: Int,
        formato: Bitmap.CompressFormat,
        calidad: Int,
    ): ByteArray? {
        val limites = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, limites)
        if (limites.outWidth <= 0 || limites.outHeight <= 0) return null

        var muestra = 1
        while (limites.outWidth / muestra > ladoMaximo || limites.outHeight / muestra > ladoMaximo) {
            muestra *= 2
        }
        val opciones = BitmapFactory.Options().apply { inSampleSize = muestra }
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size, opciones) ?: return null

        val salida = ByteArrayOutputStream()
        bitmap.compress(formato, calidad, salida)
        bitmap.recycle()
        return salida.toByteArray()
    }
}
