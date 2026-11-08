package com.quispe.coderole.ui.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun LoginScreen(viewModel: LoginViewModel) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        Modifier.fillMaxSize().imePadding().verticalScroll(rememberScrollState()).background(Color(0xFFFFF2F1)).padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text("Code", color = Color(0xFF351F35), fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall)
        Text("Role 🐾", color = Color(0xFFEE5268), fontWeight = FontWeight.Black, style = MaterialTheme.typography.displaySmall)
        Text("Aprende · Explora · Conversa", color = Color(0xFF633A50), modifier = Modifier.padding(top = 8.dp))
        Spacer(Modifier.height(30.dp))
        Text(
            if (state.modoRegistro) "Crea tu cuenta para comenzar" else "Inicia sesión para continuar",
            style = MaterialTheme.typography.titleMedium,
            color = Color(0xFF351F35),
        )
        Spacer(Modifier.height(14.dp))
        OutlinedTextField(state.correo, viewModel::cambiarCorreo, modifier = Modifier.fillMaxWidth(), label = { Text("Correo electrónico") }, singleLine = true, shape = RoundedCornerShape(18.dp))
        Spacer(Modifier.height(10.dp))
        OutlinedTextField(state.clave, viewModel::cambiarClave, modifier = Modifier.fillMaxWidth(), label = { Text("Contraseña") }, singleLine = true, visualTransformation = PasswordVisualTransformation(), shape = RoundedCornerShape(18.dp))
        state.error?.let { Text(it, color = Color(0xFFB42342), modifier = Modifier.padding(top = 10.dp)) }
        state.mensaje?.let { Text(it, color = Color(0xFF287A55), modifier = Modifier.padding(top = 10.dp)) }
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = if (state.modoRegistro) viewModel::registrar else viewModel::ingresar,
            enabled = !state.cargando,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEE5268)),
        ) {
            if (state.cargando) CircularProgressIndicator(color = Color.White, modifier = Modifier.height(22.dp), strokeWidth = 2.dp)
            else Text(if (state.modoRegistro) "Crear cuenta  →" else "Ingresar  →", fontWeight = FontWeight.Bold)
        }
        TextButton(
            onClick = if (state.modoRegistro) viewModel::mostrarLogin else viewModel::mostrarRegistro,
            enabled = !state.cargando,
            modifier = Modifier.padding(top = 4.dp),
        ) {
            Text(
                if (state.modoRegistro) "¿Ya tienes cuenta? Inicia sesión" else "¿No tienes cuenta? Regístrate",
                color = Color(0xFFB23D57),
            )
        }
        Text("Tu mentora de programación, siempre contigo", color = Color(0xFF633A50), modifier = Modifier.padding(top = 24.dp), style = MaterialTheme.typography.bodySmall)
    }
}
