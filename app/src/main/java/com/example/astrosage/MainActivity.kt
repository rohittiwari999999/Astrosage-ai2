package com.example.astrosage

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.astrosage.ui.MainViewModel
import com.example.astrosage.ui.screens.KundaliDetailScreen
import com.example.astrosage.ui.screens.PanchangScreen
import com.example.astrosage.ui.theme.AstroSageTheme

sealed class Screen {
    object Panchang : Screen()
    object Kundali : Screen()
}

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AstroSageTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentScreen by remember { mutableStateOf<Screen>(Screen.Panchang) }

                    when (currentScreen) {
                        is Screen.Panchang -> PanchangScreen(
                            viewModel = viewModel,
                            onNavigateToKundali = { currentScreen = Screen.Kundali }
                        )
                        is Screen.Kundali -> KundaliDetailScreen(
                            viewModel = viewModel,
                            onBack = { currentScreen = Screen.Panchang }
                        )
                    }
                }
            }
        }
    }
}
