package com.quispe.coderole.ui.admin

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.quispe.coderole.data.media.ImagenesPng
import com.quispe.coderole.data.notification.Notificador
import com.quispe.coderole.domain.model.ActividadDiaria
import com.quispe.coderole.domain.model.CapaPersonaje
import com.quispe.coderole.domain.model.RegistroActividad
import com.quispe.coderole.domain.repository.ActividadRepository
import com.quispe.coderole.domain.repository.CapasRepository
import com.quispe.coderole.domain.repository.ItemRepository
import com.quispe.coderole.domain.util.Fechas
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AdminUiState(
    val totalPersonajes: Int = 0,
    val totalFavoritos: Int = 0,
    val procesando: Boolean = false,
    val mensaje: String? = null,
    val error: String? = null,
    val actividad: List<ActividadDiaria> = emptyList(),
    val registros: List<RegistroActividad> = emptyList(),
    val fechaSeleccionada: String = Fechas.hoy(),
    val cargandoActividad: Boolean = true,
    val procesandoCapa: CapaPersonaje? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
class AdminViewModel(
    private val repositorio: ItemRepository,
    private val notificador: Notificador,
    private val actividad: ActividadRepository,
    private val capas: CapasRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdminUiState())
    val uiState = _uiState.asStateFlow()

    init {
        actualizarTotales()
        viewModelScope.launch {
            actividad.observarDias()
                .catch { error ->
                    _uiState.update {
                        it.copy(cargandoActividad = false, error = it.error ?: error.message ?: "No se pudo cargar la actividad.")
                    }
                }
                .collect { dias -> _uiState.update { it.copy(actividad = dias, cargandoActividad = false) } }
        }
        viewModelScope.launch {
            _uiState.map { it.fechaSeleccionada }
                .distinctUntilChanged()
                .flatMapLatest { fecha ->
                    if (fecha.isBlank()) flowOf(emptyList<RegistroActividad>())
                    else actividad.observarRegistros(fecha)
                }
                .catch { /* Un día sin registros no debe romper el panel. */ }
                .collect { registros -> _uiState.update { it.copy(registros = registros) } }
        }
    }

    fun sincronizar() {
        viewModelScope.launch {
            _uiState.update { it.copy(procesando = true, mensaje = null, error = null) }
            repositorio.refrescar()
                .onSuccess { _ ->
                    actualizarTotales()
                    _uiState.update { it.copy(procesando = false, mensaje = "Se sincronizó el perfil de tu mentora.") }
                    notificador.mostrar("CodeRole", "Se guardó el perfil de tu mentora en el dispositivo.")
                }
                .onFailure { e -> _uiState.update { it.copy(procesando = false, error = e.message ?: "No se pudo sincronizar.") } }
        }
    }

    fun limpiarCache() {
        viewModelScope.launch {
            repositorio.limpiarCache()
            actualizarTotales()
            _uiState.update { it.copy(mensaje = "Se limpió la caché local.", error = null) }
        }
    }

    fun seleccionarDia(fecha: String) {
        _uiState.update { it.copy(fechaSeleccionada = fecha, registros = emptyList()) }
    }

    /** Lee el PNG elegido, lo prepara y lo asocia a la capa del personaje. */
    fun guardarCapa(capa: CapaPersonaje, uri: Uri, contexto: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(procesandoCapa = capa, mensaje = null, error = null) }
            ImagenesPng.desdeUri(contexto, uri).fold(
                onSuccess = { bytes ->
                    val base64 = Base64.encodeToString(bytes, Base64.NO_WRAP)
                    capas.guardar(capa, base64)
                        .onSuccess {
                            _uiState.update {
                                it.copy(procesandoCapa = null, mensaje = "Capa «${capa.titulo}» actualizada para todos los usuarios.")
                            }
                        }
                        .onFailure { e ->
                            _uiState.update { it.copy(procesandoCapa = null, error = e.message ?: "No se pudo guardar la capa.") }
                        }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(procesandoCapa = null, error = e.message ?: "No se pudo leer la imagen.") }
                },
            )
        }
    }

    /** Vuelve al SVG incluido en la app para la capa indicada. */
    fun quitarCapa(capa: CapaPersonaje) {
        viewModelScope.launch {
            _uiState.update { it.copy(procesandoCapa = capa, mensaje = null, error = null) }
            capas.quitar(capa)
                .onSuccess {
                    _uiState.update { it.copy(procesandoCapa = null, mensaje = "Se restableció la capa «${capa.titulo}».") }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(procesandoCapa = null, error = e.message ?: "No se pudo restablecer la capa.") }
                }
        }
    }

    private fun actualizarTotales() {
        viewModelScope.launch {
            val guardados = repositorio.cantidadGuardada()
            val favoritos = repositorio.cantidadFavoritos()
            _uiState.update { it.copy(totalPersonajes = guardados, totalFavoritos = favoritos) }
        }
    }

    class Factory(
        private val repo: ItemRepository,
        private val notificador: Notificador,
        private val actividad: ActividadRepository,
        private val capas: CapasRepository,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T =
            AdminViewModel(repo, notificador, actividad, capas) as T
    }
}
