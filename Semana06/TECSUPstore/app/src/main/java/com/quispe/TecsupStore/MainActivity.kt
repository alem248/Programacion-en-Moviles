package com.quispe.TecsupStore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.quispe.TecsupStore.navegacion.AppNavegacion
import com.quispe.TecsupStore.ui.theme.TECSUPstoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TECSUPstoreTheme {
                AppNavegacion()
            }
        }
    }
}