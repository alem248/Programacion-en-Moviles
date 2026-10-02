package com.quispe.appnavegation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.quispe.appnavegation.navigation.AppNavigation
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
