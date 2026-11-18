package com.quispe.campusgps.data

import android.content.Context
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.quispe.campusgps.model.Posicion
import kotlinx.coroutines.tasks.await

class UbicacionRepositoryGoogle(context: Context) : UbicacionRepository {
    private val appContext = context.applicationContext
    private val client = LocationServices.getFusedLocationProviderClient(appContext)

    override fun playServicesDisponible(): Boolean =
        GoogleApiAvailability.getInstance()
            .isGooglePlayServicesAvailable(appContext) == ConnectionResult.SUCCESS

    override suspend fun obtenerPosicion(): Result<Posicion> = runCatching {
        val location = client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null).await()
            ?: throw GpsApagadoException()
        Posicion(location.latitude, location.longitude)
    }
}

class GpsApagadoException : Exception("Activa el GPS e intenta de nuevo")
