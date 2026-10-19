package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quispe.cafeCampus.data.repository.CafeRepository

class CafeViewModelFactory(private val repo: CafeRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MenuViewModel::class.java) -> MenuViewModel(repo) as T
            // TODO (Pasos 6-11): registrar los demás ViewModels
            else -> throw IllegalArgumentException("ViewModel desconocida: ${modelClass.name}")
        }
    }
}
