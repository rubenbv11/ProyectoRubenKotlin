package com.example.proyectoruben

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.proyectoruben.ui.ProyectoRubenApp
import com.example.proyectoruben.ui.pantallas.PantallaLogin
import com.example.proyectoruben.ui.theme.ProyectoRubenTheme
import com.example.proyectoruben.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        // Splash screen — debe ir ANTES de super.onCreate
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Fuerza español en DatePicker
        val config = resources.configuration
        config.setLocale(java.util.Locale("es", "ES"))
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)

        enableEdgeToEdge()
        setContent {
            ProyectoRubenTheme {
                val authViewModel: AuthViewModel = viewModel()
                val haySession by authViewModel.haySession.collectAsState(initial = false)
                if (haySession) {
                    ProyectoRubenApp(authViewModel = authViewModel)
                } else {
                    PantallaLogin(
                        viewModel = authViewModel,
                        onLoginExitoso = { }
                    )
                }
            }
        }
    }
}