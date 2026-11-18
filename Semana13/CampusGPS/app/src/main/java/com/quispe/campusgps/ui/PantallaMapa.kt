package com.quispe.campusgps.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.quispe.campusgps.model.UbicacionUi
import com.quispe.campusgps.util.formatearDistancia
import com.quispe.campusgps.viewmodel.MapaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMapa(vm: MapaViewModel) {
    val context = LocalContext.current
    val estado by vm.estado.collectAsState()
    val lugares by vm.lugares.collectAsState()
    val seleccionado by vm.seleccionado.collectAsState()
    val masCercano by vm.masCercano.collectAsState()
    val categoriaSeleccionada by vm.categoriaSeleccionada.collectAsState()
    val radioSeleccionado by vm.radioSeleccionado.collectAsState()

    var tienePermisoUbicacion by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    val distanciaMasCercano = lugares.firstOrNull { it.lugar.id == masCercano?.id }?.metros
    val textoMasCercano = masCercano?.let { lugar ->
        distanciaMasCercano?.let { "Más cercano: ${lugar.nombre} (${formatearDistancia(it)})" }
    } ?: "Más cercano: aún sin calcular"

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val concedido = result.values.any { it }
        tienePermisoUbicacion = concedido
        if (concedido) vm.buscar() else vm.permisoDenegado()
    }

    fun alPulsar() {
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (fine || coarse) {
            tienePermisoUbicacion = true
            vm.buscar()
        } else {
            launcher.launch(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION))
        }
    }

    Scaffold(topBar = { TopAppBar(title = { Text("CampusGPS") }) }) { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Fila de Filtros de Categoría
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Categoría: ", modifier = Modifier.padding(end = 4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(vm.categorias) { cat ->
                        FilterChip(
                            selected = cat == categoriaSeleccionada,
                            onClick = { vm.seleccionarCategoria(cat) },
                            label = { Text(cat) }
                        )
                    }
                }
            }

            // Fila de Filtros de Radio de Cercanía
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("Radio: ", modifier = Modifier.padding(end = 4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(vm.radiosDisponibles) { (etiqueta, radio) ->
                        FilterChip(
                            selected = radio == radioSeleccionado,
                            onClick = { vm.seleccionarRadio(radio) },
                            label = { Text(etiqueta) }
                        )
                    }
                }
            }

            Button(onClick = ::alPulsar, enabled = estado !is UbicacionUi.Buscando, modifier = Modifier.fillMaxWidth()) {
                Text("Obtener mi ubicación")
            }
            OutlinedButton(
                onClick = { masCercano?.let { vm.seleccionar(it.id) } },
                enabled = masCercano != null,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Ir al más cercano") }
            Text(text = textoMasCercano, modifier = Modifier.fillMaxWidth())
            MapaUbicacion(
                estado = estado,
                lugares = lugares,
                seleccionado = seleccionado,
                masCercano = masCercano,
                radio = radioSeleccionado,
                tienePermisoUbicacion = tienePermisoUbicacion,
                modifier = Modifier.weight(1f)
            )
            if (estado !is UbicacionUi.Error) {
                ListaLugares(
                    lugares = lugares,
                    seleccionado = seleccionado,
                    onClick = vm::seleccionar,
                    modifier = Modifier.fillMaxWidth().height(180.dp)
                )
            }
            when (val current = estado) {
                UbicacionUi.Inicial -> Text("Encuentra los lugares del campus más cercanos a ti", modifier = Modifier.height(48.dp))
                UbicacionUi.Buscando -> CircularProgressIndicator(modifier = Modifier.height(48.dp))
                is UbicacionUi.Lista -> Text(
                    "Latitud: ${current.posicion.latitud}  ·  Longitud: ${current.posicion.longitud}",
                    modifier = Modifier.height(48.dp)
                )
                is UbicacionUi.Error -> MensajeError(
                    error = current,
                    onOpenSettings = {
                        val intent = Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                        context.startActivity(intent)
                    },
                    onRetry = vm::buscar,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
