package com.quispe.campusgps.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.quispe.campusgps.data.LugaresRepositoryLocal
import com.quispe.campusgps.data.UbicacionRepositoryGoogle

class CampusViewModelFactory(context: Context) : ViewModelProvider.Factory {
    private val appContext = context.applicationContext

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (!modelClass.isAssignableFrom(MapaViewModel::class.java)) {
            throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
        }
        return MapaViewModel(
            UbicacionRepositoryGoogle(appContext), LugaresRepositoryLocal()
        ) as T
    }
}
