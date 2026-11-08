package com.quispe.coderole.ui.admin

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

private enum class PestanaAdmin(val titulo: String) {
    RESUMEN("Resumen"),
    ACTIVIDAD("Actividad"),
    CAPAS("Capas"),
}

@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var pestana by remember { mutableStateOf(PestanaAdmin.RESUMEN) }
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        // La sincronización se completa aunque el usuario no autorice notificaciones.
        viewModel.sincronizar()
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp),
    ) {
        Text(
            "Panel Admin",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Bold,
        )
        Text(
            "Gestiona los datos, la actividad y el aspecto de CodeRole.",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp),
        )
        Spacer(Modifier.height(14.dp))
        PrimaryTabRow(
            selectedTabIndex = pestana.ordinal,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            PestanaAdmin.entries.forEach { opcion ->
                Tab(
                    selected = pestana == opcion,
                    onClick = { pestana = opcion },
                    text = { Text(opcion.titulo, fontWeight = if (pestana == opcion) FontWeight.Bold else FontWeight.Normal) },
                )
            }
        }
        Spacer(Modifier.height(14.dp))
        Box(Modifier.weight(1f)) {
            when (pestana) {
                PestanaAdmin.RESUMEN -> ResumenPanel(state, pedirPermiso, viewModel)
                PestanaAdmin.ACTIVIDAD -> ActividadPanel(state, viewModel::seleccionarDia)
                PestanaAdmin.CAPAS -> CapasPanel(state, viewModel::guardarCapa, viewModel::quitarCapa)
            }
        }
        state.mensaje?.let {
            Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 12.dp))
        }
        state.error?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 12.dp))
        }
    }
}

@Composable
private fun ResumenPanel(
    state: AdminUiState,
    pedirPermiso: androidx.activity.result.ActivityResultLauncher<String>,
    viewModel: AdminViewModel,
) {
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminDato("Mentor guardado", state.totalPersonajes.toString(), Modifier.weight(1f))
            AdminDato("Mentora favorita", state.totalFavoritos.toString(), Modifier.weight(1f))
        }
        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= 33) pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
                else viewModel.sincronizar()
            },
            enabled = !state.procesando,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(22.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
        ) {
            if (state.procesando) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.height(22.dp),
                    strokeWidth = 2.dp,
                )
            } else {
                Text("⟳  Sincronizar datos")
            }
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(
            onClick = viewModel::limpiarCache,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(22.dp),
        ) {
            Text("⌫  Limpiar caché")
        }
    }
}

@Composable
private fun AdminDato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Text(titulo, color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.labelMedium)
            Text(valor, color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

/** Tarjeta reutilizable de los paneles internos. */
@Composable
internal fun TarjetaAdmin(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.surface,
    contenido: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Card(
        modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        contenido()
    }
}
