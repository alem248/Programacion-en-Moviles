package com.quispe.campusgps.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState
import com.quispe.campusgps.model.LugarConDistancia
import com.quispe.campusgps.model.UbicacionUi

private val Lima = LatLng(-12.0464, -77.0428)

@Composable
fun MapaUbicacion(
    estado: UbicacionUi,
    lugares: List<LugarConDistancia>,
    seleccionado: Int?,
    modifier: Modifier = Modifier
) {
    val camera = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(Lima, 12f)
    }

    LaunchedEffect(estado) {
        if (estado is UbicacionUi.Lista) {
            camera.animate(CameraPosition.fromLatLngZoom(
                LatLng(estado.posicion.latitud, estado.posicion.longitud), 16f
            ))
        }
    }

    GoogleMap(modifier = modifier, cameraPositionState = camera) {
        if (estado is UbicacionUi.Lista) {
            Marker(
                state = rememberMarkerState(LatLng(estado.posicion.latitud, estado.posicion.longitud)),
                title = "Estás aquí"
            )
        }
        lugares.forEach { item ->
            key(item.lugar.id) {
                Marker(
                    state = rememberMarkerState(LatLng(item.lugar.latitud, item.lugar.longitud)),
                    title = item.lugar.nombre,
                    snippet = item.lugar.categoria,
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)
                )
            }
        }
    }
}
