package com.quispe.coderole.data.repository

import com.quispe.coderole.data.local.PersonajeDao
import com.quispe.coderole.data.mapper.aEntidad
import com.quispe.coderole.data.mapper.aModelo
import com.quispe.coderole.data.remote.PersonajeApiService
import com.quispe.coderole.domain.model.Personaje
import com.quispe.coderole.domain.repository.ItemRepository
import kotlinx.coroutines.flow.map
import java.io.IOException

class PersonajeRepositoryImpl(
    private val api: PersonajeApiService,
    private val dao: PersonajeDao,
) : ItemRepository {
    override val personajes = dao.observarTodos().map { filas -> filas.map { it.aModelo() } }
    override val favoritos = dao.observarFavoritos().map { filas -> filas.map { it.aModelo() } }

    override suspend fun refrescar(): Result<Int> = runCatching {
        val referencia = api.listar().firstOrNull()
            ?: throw IOException("El servicio no devolvió el perfil de referencia.")
        val favorito = dao.obtenerPorId(1)?.esFavorito ?: false
        dao.guardar(referencia.aEntidad(favorito))
        1
    }

    override suspend fun obtenerDetalle(id: Int): Result<Personaje> = runCatching {
        val remoto = api.detalle(id)
        val favorito = dao.obtenerPorId(1)?.esFavorito ?: false
        val entidad = remoto.aEntidad(favorito)
        dao.guardar(entidad)
        entidad.aModelo()
    }

    override suspend fun alternarFavorito(id: Int) = dao.alternarFavorito(id)
    override suspend fun cantidadGuardada() = dao.contar()
    override suspend fun cantidadFavoritos() = dao.contarFavoritos()
    override suspend fun limpiarCache() = dao.limpiar()
}
