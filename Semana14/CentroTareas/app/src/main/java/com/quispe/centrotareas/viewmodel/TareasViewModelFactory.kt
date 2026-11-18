package com.quispe.centrotareas.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quispe.centrotareas.data.HistorialDataStore
import com.quispe.centrotareas.data.TareasRepository
import com.quispe.centrotareas.data.TareasRepositoryLocal

// El Contexto entra por la fábrica (para DataStore); el ViewModel nunca lo recibe
class TareasViewModelFactory(
    contexto: Context,
    private val repo: TareasRepository = TareasRepositoryLocal(
        HistorialDataStore(contexto.applicationContext)
    )
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            "ViewModel desconocido: ${modelClass.name}"
        }
        return TareasViewModel(repo) as T
    }
}
