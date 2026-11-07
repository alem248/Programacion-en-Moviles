package com.quispe.coderole.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.quispe.coderole.domain.model.Rol
import com.quispe.coderole.domain.model.Sesion
import kotlinx.coroutines.tasks.await

class FirebaseAuthRepository(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
) : AuthRepository {
    override suspend fun iniciarSesion(correo: String, clave: String): Result<Sesion> = runCatching {
        require(correo.isNotBlank()) { "Ingresa tu correo electrónico." }
        require(clave.isNotBlank()) { "Ingresa tu contraseña." }
        val usuario = auth.signInWithEmailAndPassword(correo.trim(), clave).await().user
            ?: error("Firebase no devolvió una sesión válida.")
        val documento = firestore.collection("usuarios").document(usuario.uid).get().await()
        val rolFirestore = documento.getString("rol")?.lowercase()
            ?: error("No se encontró el rol de esta cuenta en Firestore.")
        val rol = when (rolFirestore) {
            "admin" -> Rol.ADMIN
            "usuario" -> Rol.USUARIO
            else -> error("El rol configurado en Firestore no es válido.")
        }
        Sesion(usuario.uid, usuario.email ?: correo.trim(), rol)
    }.onFailure { auth.signOut() }

    override suspend fun sesionActual(): Sesion? {
        val usuario = auth.currentUser ?: return null
        return runCatching {
            val documento = firestore.collection("usuarios").document(usuario.uid).get().await()
            val rol = when (documento.getString("rol")?.lowercase()) {
                "admin" -> Rol.ADMIN
                "usuario" -> Rol.USUARIO
                else -> {
                    auth.signOut()
                    return null
                }
            }
            Sesion(usuario.uid, usuario.email.orEmpty(), rol)
        }.getOrNull()
    }

    override fun cerrarSesion() = auth.signOut()
}
