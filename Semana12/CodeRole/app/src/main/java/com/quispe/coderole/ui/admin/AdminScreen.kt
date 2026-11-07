package com.quispe.coderole.ui.admin

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun AdminScreen(viewModel: AdminViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val pedirPermiso = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { _ ->
        // La sincronización se completa aunque el usuario no autorice notificaciones.
        viewModel.sincronizar()
    }
    Column(Modifier.fillMaxSize().background(Color(0xFFFFF2F1)).padding(20.dp)) {
        Text("Panel Admin", style = MaterialTheme.typography.headlineSmall, color = Color(0xFF351F35), fontWeight = FontWeight.Bold)
        Text("Gestiona y sincroniza los datos de CodeRole.", color = Color(0xFF633A50), modifier = Modifier.padding(top = 4.dp))
        Spacer(Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AdminDato("Mentor guardado", state.totalPersonajes.toString(), Modifier.weight(1f))
            AdminDato("Favoritos", state.totalFavoritos.toString(), Modifier.weight(1f))
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
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE5268)),
        ) {
            if (state.procesando) CircularProgressIndicator(color = Color.White, modifier = Modifier.height(22.dp), strokeWidth = 2.dp)
            else Text("⟳  Sincronizar datos")
        }
        Spacer(Modifier.height(10.dp))
        OutlinedButton(onClick = viewModel::limpiarCache, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(22.dp)) {
            Text("⌫  Limpiar caché")
        }
        state.mensaje?.let { Text(it, color = Color(0xFF287A55), modifier = Modifier.padding(top = 14.dp)) }
        state.error?.let { Text(it, color = Color(0xFFB42342), modifier = Modifier.padding(top = 14.dp)) }
    }
}

@Composable
private fun AdminDato(titulo: String, valor: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(22.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxWidth().padding(18.dp)) {
            Text(titulo, color = Color(0xFF633A50), style = MaterialTheme.typography.labelMedium)
            Text(valor, color = Color(0xFF351F35), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}
