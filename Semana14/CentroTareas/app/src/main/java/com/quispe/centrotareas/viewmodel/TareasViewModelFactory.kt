package com.quispe.centrotareas.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quispe.centrotareas.data.TareasRepository
import com.quispe.centrotareas.data.TareasRepositoryLocal

class TareasViewModelFactory(
    private val repo: TareasRepository = TareasRepositoryLocal()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        require(modelClass.isAssignableFrom(TareasViewModel::class.java)) {
            "ViewModel desconocido: ${modelClass.name}"
        }
        return TareasViewModel(repo) as T
    }
}
