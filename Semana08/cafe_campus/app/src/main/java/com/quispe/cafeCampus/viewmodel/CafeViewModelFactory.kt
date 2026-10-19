package com.quispe.cafeCampus.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quispe.cafeCampus.data.prefs.PreferenciasStore
import com.quispe.cafeCampus.data.repository.CafeRepository

class CafeViewModelFactory(
    private val repo: CafeRepository,
    private val prefs: PreferenciasStore
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(SesionViewModel::class.java) ->
                SesionViewModel(repo, prefs) as T
            modelClass.isAssignableFrom(MenuViewModel::class.java) ->
                MenuViewModel(repo) as T
            modelClass.isAssignableFrom(PedidosClienteViewModel::class.java) ->
                PedidosClienteViewModel(repo, prefs) as T
            modelClass.isAssignableFrom(PanelAdminViewModel::class.java) ->
                PanelAdminViewModel(repo) as T
            modelClass.isAssignableFrom(PedidosAdminViewModel::class.java) ->
                PedidosAdminViewModel(repo) as T
            modelClass.isAssignableFrom(MenuAdminViewModel::class.java) ->
                MenuAdminViewModel(repo) as T
            // TODO (commit 12): registrar los ViewModels restantes si los hay
            else -> throw IllegalArgumentException("ViewModel desconocida: ${modelClass.name}")
        }
    }
}
