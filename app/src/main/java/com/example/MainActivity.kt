package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.CalculatorDatabase
import com.example.data.repository.CalculatorRepository
import com.example.ui.CalculatorScreen
import com.example.ui.theme.NavyCalculatorTheme
import com.example.ui.viewmodel.CalculatorViewModel
import com.example.ui.viewmodel.CalculatorViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = CalculatorDatabase.getDatabase(this)
        val repository = CalculatorRepository(database.historyDao())
        val factory = CalculatorViewModelFactory(repository)

        setContent {
            val viewModel: CalculatorViewModel = viewModel(factory = factory)
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val systemInDark = isSystemInDarkTheme()
            val darkTheme = uiState.themeMode ?: systemInDark

            NavyCalculatorTheme(darkTheme = darkTheme) {
                CalculatorScreen(viewModel = viewModel)
            }
        }
    }
}
