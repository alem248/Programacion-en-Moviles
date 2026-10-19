package com.quispe.cafeCampus

import android.app.Application
import com.quispe.cafeCampus.data.local.CafeDatabase
import com.quispe.cafeCampus.data.local.DatosIniciales
import com.quispe.cafeCampus.data.repository.CafeRepositoryRoom
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class CafeApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val database: CafeDatabase by lazy { CafeDatabase.obtener(this) }

    val repository: CafeRepositoryRoom by lazy { CafeRepositoryRoom(database) }

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch {
            DatosIniciales.sembrar(database)
        }
    }
}
