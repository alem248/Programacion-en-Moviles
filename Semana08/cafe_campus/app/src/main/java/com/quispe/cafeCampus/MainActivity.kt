package com.quispe.cafeCampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.quispe.cafeCampus.model.Rol
import com.quispe.cafeCampus.ui.navigation.NavCafe
import com.quispe.cafeCampus.ui.theme.CafeTheme
import com.quispe.cafeCampus.viewmodel.CafeViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val app = application as CafeApp
        val factory = CafeViewModelFactory(app.repository)

        setContent {
            CafeTheme(rol = Rol.CLIENTE) {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NavCafe(
                        factory = factory,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
