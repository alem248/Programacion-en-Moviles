package com.quispe.avisoscampus

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.quispe.avisoscampus.data.AvisosRepositoryMemoria
import com.quispe.avisoscampus.ui.PantallaAvisos
import com.quispe.avisoscampus.ui.theme.AvisosCampusTheme
import com.quispe.avisoscampus.viewmodel.AvisosViewModel
import com.quispe.avisoscampus.viewmodel.AvisosViewModelFactory

class MainActivity : ComponentActivity() {
    private lateinit var avisosViewModel: AvisosViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        avisosViewModel = ViewModelProvider(
            this,
            AvisosViewModelFactory(AvisosRepositoryMemoria())
        )[AvisosViewModel::class.java]

        setContent {
            AvisosCampusTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PantallaAvisos(
                        vm = avisosViewModel,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}
