package com.quispe.tecsupfit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.quispe.tecsupfit.ui.screens.MainScreen
import com.quispe.tecsupfit.ui.theme.TECSUPFitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TECSUPFitTheme {
                MainScreen()
            }
        }
    }
}
