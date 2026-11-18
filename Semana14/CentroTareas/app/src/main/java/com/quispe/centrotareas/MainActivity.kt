package com.quispe.centrotareas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.quispe.centrotareas.ui.PantallaTareas
import com.quispe.centrotareas.ui.theme.CentroTareasTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CentroTareasTheme {
                PantallaTareas(modifier = Modifier)
            }
        }
    }
}
