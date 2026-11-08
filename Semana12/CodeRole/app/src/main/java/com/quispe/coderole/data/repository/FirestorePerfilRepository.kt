package com.quispe.coderole.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.quispe.coderole.domain.model.PerfilUsuario
import com.quispe.coderole.domain.repository.PerfilRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Perfil del usuario en `usuarios/{uid}`. La foto viaja como PNG/JPEG en
 * Base64 para no depender de Firebase Storage; se reescala antes de guardarla
 * para no superar el límite de un documento de Firestore.
 */
class FirestorePerfilRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : PerfilRepository {

    override fun observar(uid: String, correo: String): Flow<PerfilUsuario> = callbackFlow {
        val registration = firestore.collection(COLECCION).document(uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(
                    PerfilUsuario(
                        uid = uid,
                        correo = correo,
                        nombre = snapshot?.getString("nombre").orEmpty(),
                        fotoBase64 = snapshot?.getString("fotoBase64"),
                    ),
                )
            }
        awaitClose { registration.remove() }
    }

    override suspend fun actualizarNombre(nombre: String): Result<Unit> = try {
        val user = requireNotNull(auth.currentUser) { "La sesión terminó. Vuelve a iniciar sesión." }
        val limpio = nombre.trim().take(LARGO_MAXIMO)
        require(limpio.length >= LARGO_MINIMO) {
            "El nombre debe tener al menos $LARGO_MINIMO caracteres."
        }
        val datos = mutableMapOf<String, Any>(
            "nombre" to limpio,
        )
        user.email?.takeIf { it.isNotBlank() }?.let { datos["correo"] = it }
        documento().set(datos, SetOptions.merge()).await()
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    override suspend fun actualizarFoto(fotoBase64: String?): Result<Unit> = try {
        val user = requireNotNull(auth.currentUser) { "La sesión terminó. Vuelve a iniciar sesión." }
        if (fotoBase64 != null) {
            require(fotoBase64.length <= FOTO_MAXIMA) {
                "La imagen pesa demasiado. Elige una foto más ligera."
            }
        }
        val datos = mutableMapOf<String, Any?>(
            "fotoBase64" to fotoBase64,
        )
        user.email?.takeIf { it.isNotBlank() }?.let { datos["correo"] = it }
        documento().set(datos, SetOptions.merge()).await()
        Result.success(Unit)
    } catch (error: Exception) {
        Result.failure(error)
    }

    private fun documento() = firestore.collection(COLECCION)
        .document(requireNotNull(auth.currentUser) { "La sesión terminó. Vuelve a iniciar sesión." }.uid)

    private companion object {
        const val COLECCION = "usuarios"
        const val LARGO_MINIMO = 2
        const val LARGO_MAXIMO = 60
        const val FOTO_MAXIMA = 700_000
    }
}
