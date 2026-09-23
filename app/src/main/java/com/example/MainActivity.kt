package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ads.AdManager
import com.example.model.GameScreenState
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.GameScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HowToPlayScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Safe AdMob initialization
        try {
            AdManager.initialize(applicationContext)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            BackHandler(enabled = true) {
                val handled = viewModel.handleBackPress()
                if (!handled) {
                    finish()
                }
            }

            // Game over hone par interstitial ad call
            LaunchedEffect(uiState.currentScreen) {
                if (uiState.currentScreen == GameScreenState.GAME_OVER) {
                    try {
                        AdManager.showInterstitial(this@MainActivity)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            MyApplicationTheme(themeMode = uiState.themeMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (uiState.currentScreen) {
                        GameScreenState.HOME -> {
                            HomeScreen(
                                bestScore = uiState.bestScore,
                                onPlayClick = { viewModel.startGame() },
                                onHowToPlayClick = { viewModel.navigateTo(GameScreenState.HOW_TO_PLAY) },
                                onSettingsClick = { viewModel.navigateTo(GameScreenState.SETTINGS) },
                                onAboutClick = { viewModel.navigateTo(GameScreenState.ABOUT) }
                            )
                        }

                        GameScreenState.PLAYING,
                        GameScreenState.PAUSED,
                        GameScreenState.GAME_OVER -> {
                            GameScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onNavigateToSettings = { viewModel.navigateTo(GameScreenState.SETTINGS) },
                                onNavigateToHome = { viewModel.navigateTo(GameScreenState.HOME) }
                            )
                        }

                        GameScreenState.SETTINGS -> {
                            SettingsScreen(
                                viewModel = viewModel,
                                uiState = uiState,
                                onBack = { viewModel.handleBackPress() }
                            )
                        }

                        GameScreenState.HOW_TO_PLAY -> {
                            HowToPlayScreen(
                                onGotItClick = { viewModel.handleBackPress() }
                            )
                        }

                        GameScreenState.ABOUT -> {
                            AboutScreen(
                                onBack = { viewModel.handleBackPress() }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
