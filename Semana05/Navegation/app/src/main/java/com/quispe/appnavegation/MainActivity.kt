package com.quispe.appnavegation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.quispe.appnavegation.navigation.AppNavigation
import com.quispe.appnavegation.screens.HomeScreen
import com.quispe.appnavegation.ui.theme.AppNavegationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppNavegationTheme {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AppPreview() {
    AppNavegationTheme {
        HomeScreen(navController = rememberNavController())
    }
}