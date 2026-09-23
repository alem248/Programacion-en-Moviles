package com.quispe.appnavegation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.quispe.appnavegation.navigation.Screen
import com.quispe.appnavegation.ui.theme.AppNavegationTheme

@Composable
fun HomeScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Pantalla Tecsup",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = { navController.navigate(Screen.List.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver lista de elementos")
        }
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(
            onClick = { navController.navigate(Screen.Profile.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Mi perfil")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AppNavegationTheme {
        HomeScreen(rememberNavController())
    }
}
