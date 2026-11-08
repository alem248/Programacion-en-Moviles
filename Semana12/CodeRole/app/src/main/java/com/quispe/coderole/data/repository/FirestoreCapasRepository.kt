package com.quispe.coderole.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.CapasPersonaje
import com.quispe.coderole.domain.repository.CapasRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Capas del personaje guardadas en `configuraciones/personaje` y su subcolección
 * `capas/{clave}` como PNG en Base64. El listener mantiene el estado en tiempo real.
 */
class FirestoreCapasRepository(
    private val firestore: FirebaseFirestore,
) : CapasRepository {
    private fun configuracion() = firestore.collection(COLECCION).document(DOCUMENTO)
    private fun subcoleccionCapas() = configuracion().collection("capas")

    override fun observar(): Flow<CapasPersonaje> = callbackFlow {
        val mapaCapas = mutableMapOf<CapaPersonaje, String>()

        fun emitir() {
            trySend(CapasPersonaje(mapaCapas.toMap()))
        }

        val registrationDoc = configuracion().addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null && snapshot.exists()) {
                CapaPersonaje.entries.forEach { capa ->
                    val valor = snapshot.getString(capa.campo)
                    if (!valor.isNullOrBlank()) {
                        mapaCapas[capa] = valor
                    } else if (capa == CapaPersonaje.OJOS_NEUTRAL) {
                        snapshot.getString("ojos")?.takeIf { it.isNotBlank() }?.let { mapaCapas[capa] = it }
                    } else if (capa == CapaPersonaje.BOCA_NEUTRAL) {
                        snapshot.getString("boca")?.takeIf { it.isNotBlank() }?.let { mapaCapas[capa] = it }
                    }
                }
                emitir()
            }
        }

        val registrationSub = subcoleccionCapas().addSnapshotListener { snapshot, error ->
            if (error != null) return@addSnapshotListener
            if (snapshot != null) {
                snapshot.documents.forEach { doc ->
                    val capa = CapaPersonaje.desdeCampo(doc.id)
                    val base64 = doc.getString("base64")
                    if (capa != null) {
                        if (!base64.isNullOrBlank()) {
                            mapaCapas[capa] = base64
                        } else {
                            mapaCapas.remove(capa)
                        }
                    }
                }
                emitir()
            }
        }

        awaitClose {
            registrationDoc.remove()
            registrationSub.remove()
        }
    }

    override suspend fun guardar(capa: CapaPersonaje, pngBase64: String): Result<Unit> = try {
        require(pngBase64.isNotBlank()) { "La imagen elegida está vacía." }
        require(pngBase64.length <= LIMITE_BASE64) {
            "La imagen pesa demasiado. Usa un PNG de hasta $LIMITE_BASE64 caracteres (unos 660 KB)."
        }
        subcoleccionCapas().document(capa.campo)
            .set(mapOf("base64" to pngBase64), SetOptions.merge()).await()
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    override suspend fun quitar(capa: CapaPersonaje): Result<Unit> = try {
        subcoleccionCapas().document(capa.campo).delete().await()
        if (capa == CapaPersonaje.BASE || capa == CapaPersonaje.OJOS_NEUTRAL || capa == CapaPersonaje.BOCA_NEUTRAL) {
            configuracion().set(mapOf(capa.campo to null), SetOptions.merge()).await()
        }
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    private companion object {
        const val COLECCION = "configuraciones"
        const val DOCUMENTO = "personaje"
        const val LIMITE_BASE64 = 900_000
    }
}
