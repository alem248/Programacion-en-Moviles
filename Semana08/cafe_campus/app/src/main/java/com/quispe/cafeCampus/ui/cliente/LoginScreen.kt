package com.quispe.cafeCampus.ui.cliente

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import com.quispe.cafeCampus.model.Rol
import com.quispe.cafeCampus.ui.components.CafeButton
import com.quispe.cafeCampus.ui.components.CafeTextField
import com.quispe.cafeCampus.viewmodel.SesionViewModel

@Composable
fun LoginScreen(
    vm: SesionViewModel,
    onIngresar: (Rol) -> Unit
) {
    val error by vm.error.collectAsState()
    val usuario by vm.usuarioActual.collectAsState()

    var rolSeleccionado by remember { mutableStateOf(Rol.CLIENTE) }
    var correo by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }

    LaunchedEffect(usuario) {
        usuario?.let { onIngresar(rolSeleccionado) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = "Elige tu perfil e ingresa con tus credenciales",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Rol.entries.forEach { rol ->
                val seleccionado = rol == rolSeleccionado
                FilterChip(
                    selected = seleccionado,
                    onClick = {
                        rolSeleccionado = rol
                        vm.limpiarError()
                    },
                    label = { Text(rol.titulo) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }
        }
        Spacer(Modifier.height(20.dp))

        CafeTextField(
            valor = correo,
            onValor = { correo = it; vm.limpiarError() },
            etiqueta = "Correo",
            icono = Icons.Default.Email,
            keyboardType = androidx.compose.ui.text.input.KeyboardType.Email
        )
        Spacer(Modifier.height(12.dp))
        CafeTextField(
            valor = clave,
            onValor = { clave = it; vm.limpiarError() },
            etiqueta = "Contraseña",
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

        Spacer(Modifier.height(20.dp))
        CafeButton(
            texto = "Ingresar",
            onClick = { vm.login(correo, clave, rolSeleccionado) },
            color = if (rolSeleccionado == Rol.CLIENTE) {
                com.quispe.cafeCampus.ui.theme.NaranjaPrimary
            } else {
                com.quispe.cafeCampus.ui.theme.IndigoPrimary
            }
        )
    }
}
