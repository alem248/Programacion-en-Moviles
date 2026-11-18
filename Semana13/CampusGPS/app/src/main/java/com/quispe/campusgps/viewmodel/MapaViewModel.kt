package com.quispe.campusgps.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.campusgps.data.LugaresRepository
import com.quispe.campusgps.data.UbicacionRepository
import com.quispe.campusgps.model.LugarConDistancia
import com.quispe.campusgps.model.TipoError
import com.quispe.campusgps.model.UbicacionUi
import com.quispe.campusgps.model.Posicion
import com.quispe.campusgps.util.distanciaMetros
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapaViewModel(
    private val ubicacionRepo: UbicacionRepository,
    private val lugaresRepo: LugaresRepository
) : ViewModel() {
    private val _estado = MutableStateFlow<UbicacionUi>(UbicacionUi.Inicial)
    val estado = _estado.asStateFlow()

    private val _lugares = MutableStateFlow(lugaresRepo.obtenerLugares().map { LugarConDistancia(it, null) })
    val lugares = _lugares.asStateFlow()
    val masCercano: StateFlow<com.quispe.campusgps.model.Lugar?> = lugares
        .map { lista -> lista.firstOrNull { it.metros != null }?.lugar }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val _seleccionado = MutableStateFlow<Int?>(null)
    val seleccionado = _seleccionado.asStateFlow()

    fun buscar() {
        if (_estado.value is UbicacionUi.Buscando) return
        _estado.value = UbicacionUi.Buscando
        viewModelScope.launch {
            if (!ubicacionRepo.playServicesDisponible()) {
                _estado.value = UbicacionUi.Error(
                    "Este dispositivo no tiene Google Play Services.", TipoError.SIN_PLAY_SERVICES
                )
                return@launch
            }
            ubicacionRepo.obtenerPosicion()
                .onSuccess {
                    actualizarDistancias(it)
                    _estado.value = UbicacionUi.Lista(it)
                }
                .onFailure { error ->
                    val tipo = if (error is SecurityException) TipoError.SIN_PERMISO else TipoError.GPS_APAGADO
                    _estado.value = UbicacionUi.Error(
                        if (tipo == TipoError.SIN_PERMISO) "Falta el permiso de ubicación" else "Activa el GPS e intenta de nuevo",
                        tipo
                    )
                }
        }
    }

    fun permisoDenegado() {
        _estado.value = UbicacionUi.Error(
            "Sin permiso de ubicación no puedo mostrar dónde estás", TipoError.SIN_PERMISO
        )
    }

    fun seleccionar(id: Int?) {
        _seleccionado.value = id
    }

    private fun actualizarDistancias(posicion: Posicion) {
        _lugares.value = lugaresRepo.obtenerLugares()
            .map { LugarConDistancia(it, distanciaMetros(posicion, it)) }
            .sortedBy { it.metros }
    }
}
