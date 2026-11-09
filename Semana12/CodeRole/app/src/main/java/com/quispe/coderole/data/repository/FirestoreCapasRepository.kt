package com.quispe.coderole.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.CapasPersonaje
import com.quispe.coderole.domain.repository.CapasRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Capas del personaje en Firestore. Cada pieza vive en un documento propio de
 * la subcolección `configuraciones/personaje/capas` (un PNG por documento, para
 * no acercarse al límite de 1 MiB) y los tres campos heredados del documento
 * principal (`base`, `ojos`, `boca`) siguen leyéndose como respaldo, así los
 * PNG que ya había guardado el administrador no se pierden.
 *
 * Los dos listeners mantienen el estado en tiempo real: cuando el mentor
 * actualiza una pieza desde la administración, todos la ven al instante.
 */
class FirestoreCapasRepository(
    private val firestore: FirebaseFirestore,
) : CapasRepository {
    private fun configuracion() = firestore.collection(COLECCION).document(DOCUMENTO)

    private fun capas() = configuracion().collection(SUBCOLECCION)

    override fun observar(): Flow<CapasPersonaje> = callbackFlow {
        // PNG guardados antes de la subdivisión: base, ojos y boca.
        var legado: Map<CapaPersonaje, String> = emptyMap()
        // Piezas actuales; el valor null significa «volver al diseño incluido».
        var actuales: Map<CapaPersonaje, String?> = emptyMap()

        fun emitir() {
            val combinadas = mutableMapOf<CapaPersonaje, String>()
            CapaPersonaje.entries.forEach { capa ->
                val png = if (actuales.containsKey(capa)) actuales[capa] else legado[capa]
                if (!png.isNullOrBlank()) combinadas[capa] = png
            }
            trySend(CapasPersonaje(combinadas))
        }

        val documento = configuracion().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            legado = if (snapshot == null || !snapshot.exists()) {
                emptyMap()
            } else {
                CapaPersonaje.entries.mapNotNull { capa ->
                    // Primero el campo con el nombre exacto de la pieza (datos
                    // guardados por la rama IA) y después el campo heredado
                    // único (base, ojos, boca) del documento principal.
                    val png = snapshot.getString(capa.clave)?.takeIf(String::isNotBlank)
                        ?: capa.legado?.let { snapshot.getString(it)?.takeIf(String::isNotBlank) }
                    png?.let { capa to it }
                }.toMap()
            }
            emitir()
        }

        val subcoleccion = capas().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            actuales = snapshot?.documents?.mapNotNull { documento ->
                val capa = CapaPersonaje.entries.firstOrNull { it.clave == documento.id } ?: return@mapNotNull null
                capa to documento.getString(CAMPO_BASE64)
            }?.toMap() ?: emptyMap()
            emitir()
        }

        awaitClose {
            documento.remove()
            subcoleccion.remove()
        }
    }

    override suspend fun guardar(capa: CapaPersonaje, pngBase64: String): Result<Unit> = try {
        require(pngBase64.isNotBlank()) { "La imagen elegida está vacía." }
        require(pngBase64.length <= LIMITE_BASE64) {
            "La imagen pesa demasiado. Usa un PNG de hasta $LIMITE_BASE64 caracteres (unos 660 KB)."
        }
        capas().document(capa.clave).set(mapOf(CAMPO_BASE64 to pngBase64)).await()
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    /** Escribe null: la pieza vuelve al SVG incluido en la app. */
    override suspend fun quitar(capa: CapaPersonaje): Result<Unit> = try {
        capas().document(capa.clave).set(mapOf(CAMPO_BASE64 to null)).await()
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    private companion object {
        const val COLECCION = "configuraciones"
        const val DOCUMENTO = "personaje"
        const val SUBCOLECCION = "capas"
        const val CAMPO_BASE64 = "base64"
        const val LIMITE_BASE64 = 900_000
    }
}
