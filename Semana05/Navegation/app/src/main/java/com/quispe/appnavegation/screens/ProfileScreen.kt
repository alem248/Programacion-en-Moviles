package com.quispe.appnavegation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.quispe.appnavegation.navigation.Screen
import com.quispe.appnavegation.ui.theme.LogoutRed
import com.quispe.appnavegation.ui.theme.PurpleDark
import com.quispe.appnavegation.ui.theme.PurpleLight
import com.quispe.appnavegation.ui.theme.PurplePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Configuración de Perfil",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PurpleDark
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            // Cabecera con fondo de color entero morado
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PurpleDark)
                    .padding(vertical = 28.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar circular del usuario centrado
                    Surface(
                        modifier = Modifier.size(90.dp),
                        shape = CircleShape,
                        color = PurpleLight,
                        shadowElevation = 6.dp
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "JL",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = PurplePrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Nombre del usuario centrado
                    Text(
                        text = "Juan León Suiyon",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Estudiante Tecsup",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.85f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección 1: INFORMACIÓN PERSONAL
            Text(
                text = "INFORMACIÓN PERSONAL",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = PurpleDark,
                modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 4.dp)
            )

            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = PurplePrimary
                    )
                },
                headlineContent = { Text("Nombre Completo") },
                supportingContent = { Text("Juan León Suiyon") }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                        tint = PurplePrimary
                    )
                },
                headlineContent = { Text("Correo de Tecsup") },
                supportingContent = { Text("juan.leon@tecsup.edu.pe") }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null,
                        tint = PurplePrimary
                    )
                },
                headlineContent = { Text("Teléfono") },
                supportingContent = { Text("+51 987 654 321") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Sección 2: ACADÉMICO
            Text(
                text = "ACADÉMICO",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = PurpleDark,
                modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
            )

            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = null,
                        tint = PurplePrimary
                    )
                },
                headlineContent = { Text("Carrera Profesional") },
                supportingContent = { Text("Diseño y Desarrollo de Software / Ingeniería de Software") }
            )
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            ListItem(
                leadingContent = {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        tint = PurplePrimary
                    )
                },
                headlineContent = { Text("Ciclo Actual") },
                supportingContent = { Text("V Ciclo - Semestre 2026-I") }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de Cerrar Sesión
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Button(
                    onClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LogoutRed,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Logout,
                            contentDescription = "Cerrar Sesión",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cerrar Sesión",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
