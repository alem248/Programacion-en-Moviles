package com.tecsup.murocampus.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.tecsup.murocampus.model.Post

@Composable
fun PantallaCrear(
    state: UiState<Post>?,
    onPublicar: (String, String) -> Unit,
    onLimpiarResultado: () -> Unit,
    onVolver: () -> Unit,
) {
    var titulo by rememberSaveable { mutableStateOf("") }
    var texto by rememberSaveable { mutableStateOf("") }
    LaunchedEffect(state) {
        if (state is UiState.Exito) {
            titulo = ""
            texto = ""
        }
    }

    Column(
        Modifier.fillMaxSize().padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Button(onClick = onVolver) { Text("← Volver") }
        Text("Nueva publicación", style = androidx.compose.material3.MaterialTheme.typography.headlineSmall)
        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        OutlinedTextField(
            value = texto,
            onValueChange = { texto = it },
            label = { Text("Contenido") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
        )
        Button(
            onClick = { onPublicar(titulo, texto) },
            enabled = titulo.isNotBlank() && texto.isNotBlank() && state !is UiState.Cargando,
            modifier = Modifier.fillMaxWidth(),
        ) { Text("Publicar") }
        when (state) {
            UiState.Cargando -> CircularProgressIndicator(Modifier.align(Alignment.CenterHorizontally))
            is UiState.Error -> Text(state.mensaje, color = androidx.compose.material3.MaterialTheme.colorScheme.error)
            is UiState.Exito -> {
                Text("Publicado con id ${state.dato.id}", color = androidx.compose.material3.MaterialTheme.colorScheme.primary)
                Spacer(Modifier.height(2.dp))
                Button(onClick = onLimpiarResultado) { Text("Listo") }
            }
            null -> Unit
        }
    }
}
