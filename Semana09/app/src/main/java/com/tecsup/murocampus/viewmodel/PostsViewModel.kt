package com.tecsup.murocampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tecsup.murocampus.data.PostRepository
import com.tecsup.murocampus.model.Comentario
import com.tecsup.murocampus.model.NuevoPost
import com.tecsup.murocampus.model.Post
import com.tecsup.murocampus.ui.UiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetalleUi(val post: Post, val comentarios: List<Comentario>)

class PostsViewModel(private val repo: PostRepository) : ViewModel() {
    private val _posts = MutableStateFlow<UiState<List<Post>>>(UiState.Cargando)
    val posts: StateFlow<UiState<List<Post>>> = _posts.asStateFlow()

    private val _detalle = MutableStateFlow<UiState<DetalleUi>>(UiState.Cargando)
    val detalle: StateFlow<UiState<DetalleUi>> = _detalle.asStateFlow()

    private val _usuarioFiltro = MutableStateFlow<Int?>(null)
    val usuarioFiltro: StateFlow<Int?> = _usuarioFiltro.asStateFlow()

    private val _busqueda = MutableStateFlow("")
    val busqueda: StateFlow<String> = _busqueda.asStateFlow()
    private var publicacionesCargadas: List<Post> = emptyList()

    private val _publicacion = MutableStateFlow<UiState<Post>?>(null)
    val publicacion: StateFlow<UiState<Post>?> = _publicacion.asStateFlow()

    init { cargar() }

    fun cargar() {
        viewModelScope.launch {
            _posts.value = UiState.Cargando
            val resultado = _usuarioFiltro.value?.let { repo.porUsuario(it) } ?: repo.listar()
            _posts.value = resultado.fold(
                onSuccess = {
                    publicacionesCargadas = it
                    UiState.Exito(listaVisible())
                },
                onFailure = { UiState.Error(it.message ?: "No se pudieron cargar las publicaciones") },
            )
        }
    }

    fun filtrar(userId: Int?) {
        _usuarioFiltro.value = userId
        cargar()
    }

    fun buscar(texto: String) {
        _busqueda.value = texto
        if (_posts.value is UiState.Exito) _posts.value = UiState.Exito(listaVisible())
    }

    fun abrir(id: Int) {
        viewModelScope.launch {
            _detalle.value = UiState.Cargando
            try {
                val resultado = coroutineScope {
                    val post = async { repo.detalle(id) }
                    val comentarios = async { repo.comentarios(id) }
                    post.await().getOrThrow() to comentarios.await().getOrThrow()
                }
                _detalle.value = UiState.Exito(DetalleUi(resultado.first, resultado.second))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _detalle.value = UiState.Error(e.message ?: "No se pudo cargar el detalle")
            }
        }
    }

    fun publicar(titulo: String, texto: String) {
        viewModelScope.launch {
            _publicacion.value = UiState.Cargando
            val resultado = repo.crear(NuevoPost(userId = 1, title = titulo.trim(), body = texto.trim()))
            _publicacion.value = resultado.fold(
                onSuccess = { UiState.Exito(it) },
                onFailure = { UiState.Error(it.message ?: "No se pudo publicar") },
            )
        }
    }

    fun limpiarResultadoPublicacion() { _publicacion.value = null }

    private fun listaVisible(): List<Post> {
        val consulta = _busqueda.value.trim()
        return if (consulta.isEmpty()) publicacionesCargadas
        else publicacionesCargadas.filter { it.title.contains(consulta, ignoreCase = true) }
    }
}
