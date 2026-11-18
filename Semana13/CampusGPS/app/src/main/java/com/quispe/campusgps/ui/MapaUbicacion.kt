package com.quispe.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Circle
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberUpdatedMarkerState
import com.quispe.campusgps.model.Lugar
import com.quispe.campusgps.model.LugarConDistancia
import com.quispe.campusgps.model.UbicacionUi

private val Lima = LatLng(-12.0464, -77.0428)

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    masCercano: Lugar?,
    radio: Float?,
    tienePermisoUbicacion: Boolean,
    modifier: Modifier = Modifier
) {
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(Lima, 12f)
    }

    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            camera.animate(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(
                        LatLng(estado.posicion.latitud, estado.posicion.longitud), 16f
                    )
                )
            )
        }
    }
    LaunchedEffect(seleccionado, lugares) {
        val item = lugares.firstOrNull { it.lugar.id == seleccionado } ?: return@LaunchedEffect
        camera.animate(
            CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(LatLng(item.lugar.latitud, item.lugar.longitud), 17f)
            )
        )
    }

    GoogleMap(
        modifier = modifier,
        cameraPositionState = camera,
        properties = MapProperties(isMyLocationEnabled = tienePermisoUbicacion),
        uiSettings = MapUiSettings(myLocationButtonEnabled = tienePermisoUbicacion)
    ) {
        if (estado is UbicacionUi.Lista) {
            val miPosicion = LatLng(estado.posicion.latitud, estado.posicion.longitud)
            Marker(
                state = rememberUpdatedMarkerState(position = miPosicion),
                title = "Estás aquí"
            )
            if (radio != null) {
                Circle(
                    center = miPosicion,
                    radius = radio.toDouble(),
                    strokeColor = Color(0x7F1A73E8),
                    fillColor = Color(0x221A73E8),
                    strokeWidth = 2f
                )
            }
        }
        lugares.forEach { item ->
            key(item.lugar.id) {
                Marker(
                    state = rememberUpdatedMarkerState(position = LatLng(item.lugar.latitud, item.lugar.longitud)),
                    title = item.lugar.nombre,
                    snippet = item.lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(
                        if (item.lugar.id == masCercano?.id) BitmapDescriptorFactory.HUE_GREEN
                        else BitmapDescriptorFactory.HUE_AZURE
                    )
                )
            }
        }
    }
}
