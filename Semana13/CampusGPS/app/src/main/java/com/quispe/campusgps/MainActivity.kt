package com.quispe.campusgps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.campusgps.ui.PantallaMapa
import com.quispe.campusgps.ui.theme.CampusGPSTheme
import com.quispe.campusgps.viewmodel.CampusViewModelFactory
import com.quispe.campusgps.viewmodel.MapaViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CampusGPSTheme {
                val vm: MapaViewModel = viewModel(factory = CampusViewModelFactory(applicationContext))
                Surface(modifier = Modifier.fillMaxSize()) { PantallaMapa(vm) }
            }
        }
    }
}
