package com.tecsup.murocampus.data

import com.tecsup.murocampus.data.remote.PostApi
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.ActualizacionPost
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import kotlinx.coroutines.CancellationException

interface PostRepository {
    suspend fun listar(): Result<List<Post>>
    suspend fun detalle(id: Int): Result<Post>
    suspend fun comentarios(id: Int): Result<List<Comentario>>
    suspend fun porUsuario(userId: Int): Result<List<Post>>
    suspend fun crear(nuevo: NuevoPost): Result<Post>
    suspend fun actualizar(id: Int, actualizacion: ActualizacionPost): Result<Post>
    suspend fun eliminar(id: Int): Result<Unit>
}

class PostRepositoryRemote(private val api: PostApi) : PostRepository {
    override suspend fun listar() = ejecutar { api.listar() }
    override suspend fun detalle(id: Int) = ejecutar { api.detalle(id) }
    override suspend fun comentarios(id: Int) = ejecutar { api.comentarios(id) }
    override suspend fun porUsuario(userId: Int) = ejecutar { api.porUsuario(userId) }
    override suspend fun crear(nuevo: NuevoPost) = ejecutar { api.crear(nuevo) }
    override suspend fun actualizar(id: Int, actualizacion: ActualizacionPost) = ejecutar {
        val respuesta = api.actualizar(id, actualizacion)
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("No se pudo actualizar (HTTP ${respuesta.code()})")
        }
        respuesta.body() ?: throw IllegalStateException("La respuesta de actualización llegó vacía")
    }

    override suspend fun eliminar(id: Int) = ejecutar {
        val respuesta = api.eliminar(id)
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("No se pudo eliminar (HTTP ${respuesta.code()})")
        }
        Unit
    }
    private suspend fun <T> ejecutar(llamada: suspend () -> T): Result<T> = try {
        Result.success(llamada())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
