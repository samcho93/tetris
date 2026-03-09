package com.example.tetris.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.game.GameAction
import com.example.tetris.model.GamePhase
import com.example.tetris.model.GameState
import com.example.tetris.ui.components.BoardCanvas
import com.example.tetris.ui.components.ControlPad
import com.example.tetris.ui.components.NextPiecePreview
import com.example.tetris.ui.components.ScoreDisplay
import com.example.tetris.ui.theme.MenuBackground
import com.example.tetris.ui.theme.TextSecondary

@Composable
fun GameScreen(
    state: GameState,
    onAction: (GameAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MenuBackground)
            .navigationBarsPadding()
    ) {
        // Top bar with music toggle + pause button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (state.musicEnabled) "\u266A ON" else "\u266A OFF",
                color = if (state.musicEnabled) Color(0xFF00D4FF) else TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .clickable { onAction(GameAction.ToggleMusic) }
                    .padding(8.dp)
            )
            Text(
                text = "II PAUSE",
                color = TextSecondary,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier
                    .clickable { onAction(GameAction.Pause) }
                    .padding(8.dp)
            )
        }

        // Main content: board + side panel
        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp)
        ) {
            // Game board
            BoardCanvas(
                state = state,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
                    .padding(end = 8.dp)
            )

            // Side panel
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                NextPiecePreview(nextPiece = state.nextPiece)
                Spacer(modifier = Modifier.height(16.dp))
                ScoreDisplay(
                    score = state.score,
                    lines = state.linesCleared,
                    level = state.level
                )
            }
        }

        // Control pad
        ControlPad(
            onLeft = { onAction(GameAction.MoveLeft) },
            onRight = { onAction(GameAction.MoveRight) },
            onRotate = { onAction(GameAction.RotateCW) },
            onSoftDrop = { onAction(GameAction.SoftDrop) },
            onHardDrop = { onAction(GameAction.HardDrop) }
        )

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Pause overlay
    if (state.phase == GamePhase.PAUSED) {
        PauseOverlay(
            score = state.score,
            lines = state.linesCleared,
            onResume = { onAction(GameAction.Resume) },
            onQuit = { onAction(GameAction.StartGame(0)) }
        )
    }
}
