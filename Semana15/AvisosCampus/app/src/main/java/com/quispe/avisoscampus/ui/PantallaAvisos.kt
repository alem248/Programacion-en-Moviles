package com.quispe.avisoscampus.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.quispe.avisoscampus.model.PermisoUi
import com.quispe.avisoscampus.notificacion.Notificador
import com.quispe.avisoscampus.viewmodel.AvisosViewModel

@Composable
fun PantallaAvisos(
    vm: AvisosViewModel,
    modifier: Modifier = Modifier
) {
    val contexto = LocalContext.current
    val permiso by vm.permiso.collectAsState()
    val lanzadorPermiso = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { concedido ->
        vm.permisoRespondido(concedido)
    }

    LaunchedEffect(Unit) {
        if (Notificador.tienePermiso(contexto)) {
            vm.permisoRespondido(true)
        }
    }

    LaunchedEffect(vm) {
        vm.eventos.collect { aviso ->
            Notificador.mostrar(contexto, aviso)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = "AvisosCampus",
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                style = MaterialTheme.typography.titleLarge,
                color = Color.White
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Permiso de notificaciones",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )

                    val estado = when (permiso) {
                        PermisoUi.SinPedir -> "Sin pedir"
                        PermisoUi.Concedido -> "Permitido"
                        PermisoUi.Denegado -> "Denegado"
                    }
                    Text("Estado: $estado")

                    Button(
                        onClick = {
                            if (Build.VERSION.SDK_INT >= 33) {
                                lanzadorPermiso.launch(
                                    Manifest.permission.POST_NOTIFICATIONS
                                )
                            } else {
                                vm.permisoRespondido(true)
                            }
                        }
                    ) {
                        Text("Permitir notificaciones")
                    }
                }
            }
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Avisos",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Button(onClick = vm::avisarAhora) {
                        Text("Mostrar aviso")
                    }
                }
            }
            TarjetaBase("Recordatorio")
            TarjetaBase("Receptor de anuncios")
            TarjetaBase("Historial")
        }
    }
}

@Composable
private fun TarjetaBase(titulo: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(
                text = titulo,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
