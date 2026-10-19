package com.quispe.cafeCampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quispe.cafeCampus.data.prefs.PreferenciasStore
import com.quispe.cafeCampus.ui.navigation.NavCafe
import com.quispe.cafeCampus.ui.theme.CafeTheme
import com.quispe.cafeCampus.viewmodel.CafeViewModelFactory
import com.quispe.cafeCampus.viewmodel.SesionViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CafeApp
        val factory = CafeViewModelFactory(app.repository, PreferenciasStore(app))

        setContent {
            val sesion: SesionViewModel = viewModel(factory = factory)
            val rol by sesion.rolActivo.collectAsState()
            val modoOscuro by sesion.modoOscuro.collectAsState()

            CafeTheme(rol = rol, darkTheme = modoOscuro) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavCafe(
                        factory = factory,
                        sesion = sesion,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
