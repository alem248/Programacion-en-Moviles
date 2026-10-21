package com.quispe.cafeCampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.quispe.cafeCampus.ui.components.AppTopBar
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.ui.components.CafeTextField
import com.quispe.cafeCampus.viewmodel.RegistroViewModel

@Composable
fun RegistroScreen(
    vm: RegistroViewModel,
    onRegistrado: () -> Unit,
    onBack: () -> Unit
) {
    val error by vm.error.collectAsState()
    val cargando by vm.cargando.collectAsState()
    val exito by vm.exito.collectAsState()

    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var repetirClave by remember { mutableStateOf("") }

    LaunchedEffect(exito) {
        if (exito) {
            vm.limpiarExito()
            onRegistrado()
        }
    }

    Column(Modifier.fillMaxSize()) {
        AppTopBar(titulo = "Crear cuenta", onBack = onBack)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Registro de estudiante",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Crea tu cuenta para pedir en la cafetería",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(24.dp))

            CafeTextField(
                valor = nombre,
                onValor = { nombre = it; vm.limpiarError() },
                etiqueta = "Nombre completo",
                icono = Icons.Default.Person
            )
            Spacer(Modifier.height(12.dp))
            CafeTextField(
                valor = correo,
                onValor = { correo = it; vm.limpiarError() },
                etiqueta = "Correo",
                icono = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )
            Spacer(Modifier.height(12.dp))
            CafeTextField(
                valor = clave,
                onValor = { clave = it; vm.limpiarError() },
                etiqueta = "Contraseña (mínimo 4)",
                icono = Icons.Default.Lock,
                password = true
            )
            Spacer(Modifier.height(12.dp))
            CafeTextField(
                valor = repetirClave,
                onValor = { repetirClave = it; vm.limpiarError() },
                etiqueta = "Repite la contraseña",
                icono = Icons.Default.Lock,
                password = true
            )

            if (error != null) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = error ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(Modifier.height(24.dp))
            CafeButton(
                texto = if (cargando) "Creando cuenta..." else "Crear cuenta",
                onClick = { vm.registrar(nombre, correo, clave, repetirClave) },
                habilitado = !cargando
            )
        }
    }
}
