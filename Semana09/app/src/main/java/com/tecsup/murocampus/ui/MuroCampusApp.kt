package com.tecsup.murocampus.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tecsup.murocampus.viewmodel.PostsViewModel

@Composable
fun MuroCampusApp(viewModel: PostsViewModel) {
    val posts by viewModel.posts.collectAsStateWithLifecycle()
    val detalle by viewModel.detalle.collectAsStateWithLifecycle()
    val usuarioFiltro by viewModel.usuarioFiltro.collectAsStateWithLifecycle()
    val publicacion by viewModel.publicacion.collectAsStateWithLifecycle()
    var idSeleccionado by remember { mutableStateOf<Int?>(null) }
    var creando by remember { mutableStateOf(false) }

    BackHandler(enabled = idSeleccionado != null || creando) {
        if (idSeleccionado != null) idSeleccionado = null else creando = false
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        when {
            idSeleccionado != null -> {
                val id = idSeleccionado!!
                PantallaDetalle(
                    state = detalle,
                    onVolver = { idSeleccionado = null },
                    onReintentar = { viewModel.abrir(id) },
                )
            }
            creando -> PantallaCrear(
                state = publicacion,
                onPublicar = viewModel::publicar,
                onLimpiarResultado = viewModel::limpiarResultadoPublicacion,
                onVolver = { creando = false; viewModel.limpiarResultadoPublicacion() },
            )
            else -> PantallaLista(
                state = posts,
                usuarioFiltro = usuarioFiltro,
                onCargar = viewModel::cargar,
                onFiltrar = viewModel::filtrar,
                onAbrir = { id -> idSeleccionado = id; viewModel.abrir(id) },
                onCrear = { creando = true; viewModel.limpiarResultadoPublicacion() },
            )
        }
    }
}
