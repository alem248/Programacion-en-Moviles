package com.quispe.avisoscampus

import android.app.Application
import com.quispe.avisoscampus.notificacion.Notificador

class AvisosApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Notificador.crearCanal(this)
    }
}