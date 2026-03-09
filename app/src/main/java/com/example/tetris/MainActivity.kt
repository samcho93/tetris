package com.example.tetris

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.tetris.game.GameAction
import com.example.tetris.model.GamePhase
import com.example.tetris.ui.screens.GameScreen
import com.example.tetris.ui.screens.MenuScreen
import com.example.tetris.ui.theme.InfiniteTetrisTheme
import com.example.tetris.viewmodel.GameViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfiniteTetrisTheme {
                val viewModel: GameViewModel = viewModel()
                val state by viewModel.gameState.collectAsState()

                // Pause game when app goes to background
                val lifecycleOwner = LocalLifecycleOwner.current
                DisposableEffect(lifecycleOwner) {
                    val observer = LifecycleEventObserver { _, event ->
                        if (event == Lifecycle.Event.ON_PAUSE) {
                            viewModel.onPauseLifecycle()
                        }
                    }
                    lifecycleOwner.lifecycle.addObserver(observer)
                    onDispose {
                        lifecycleOwner.lifecycle.removeObserver(observer)
                    }
                }

                when (state.phase) {
                    GamePhase.MENU -> {
                        MenuScreen(
                            onStartGame = { level ->
                                viewModel.onAction(GameAction.StartGame(level))
                            }
                        )
                    }
                    GamePhase.PLAYING,
                    GamePhase.PAUSED,
                    GamePhase.LINE_CLEAR_ANIMATION -> {
                        GameScreen(
                            state = state,
                            onAction = { viewModel.onAction(it) }
                        )
                    }
                }
            }
        }
    }
}
