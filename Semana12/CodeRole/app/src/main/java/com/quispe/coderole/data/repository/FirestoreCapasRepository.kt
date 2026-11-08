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
 * Capas del personaje guardadas en `configuraciones/personaje` como PNG en
 * Base64. El listener mantiene el estado en tiempo real: cuando el mentor
 * actualiza una capa desde la administración, todos los dispositivos la ven.
 */
class FirestoreCapasRepository(
    private val firestore: FirebaseFirestore,
) : CapasRepository {
    private fun configuracion() = firestore.collection(COLECCION).document(DOCUMENTO)

    override fun observar(): Flow<CapasPersonaje> = callbackFlow {
        val registration = configuracion().addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot == null || !snapshot.exists()) {
                trySend(CapasPersonaje())
                return@addSnapshotListener
            }
            val personalizadas = mutableMapOf<CapaPersonaje, String>()
            CapaPersonaje.entries.forEach { capa ->
                snapshot.getString(capa.campo)?.takeIf { it.isNotBlank() }?.let { personalizadas[capa] = it }
            }
            trySend(CapasPersonaje(personalizadas))
        }
        awaitClose { registration.remove() }
    }

    override suspend fun guardar(capa: CapaPersonaje, pngBase64: String): Result<Unit> = try {
        require(pngBase64.isNotBlank()) { "La imagen elegida está vacía." }
        require(pngBase64.length <= LIMITE_BASE64) {
            "La imagen pesa demasiado. Usa un PNG de hasta $LIMITE_BASE64 caracteres (unos 660 KB)."
        }
        configuracion().set(mapOf(capa.campo to pngBase64), SetOptions.merge()).await()
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    override suspend fun quitar(capa: CapaPersonaje): Result<Unit> = try {
        configuracion().set(mapOf(capa.campo to null), SetOptions.merge()).await()
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
