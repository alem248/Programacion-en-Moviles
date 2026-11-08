package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.quispe.cafeCampus.data.repository.CafeRepository
import com.quispe.cafeCampus.model.ProductoVendido
import com.quispe.cafeCampus.model.TotalPorPago
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class ReporteViewModel(private val repo: CafeRepository) : ViewModel() {

    /** Inicio del día actual (medianoche) en millis. */
    private val inicioDia: Long = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    val ventasHoy: StateFlow<Double> = repo.observarVentasHoy(inicioDia)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val masVendido: StateFlow<ProductoVendido?> = repo.observarMasVendido(inicioDia)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val totalesPorPago: StateFlow<List<TotalPorPago>> = repo.observarTotalesPorPago(inicioDia)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
