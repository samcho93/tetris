package com.example.tetris.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tetris.model.Position
import com.example.tetris.model.Tetromino
import com.example.tetris.model.TetrominoType
import com.example.tetris.ui.theme.BoardBackground
import com.example.tetris.ui.theme.BoardBorder
import com.example.tetris.ui.theme.TextSecondary

@Composable
fun NextPiecePreview(
    nextPiece: TetrominoType,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "NEXT",
            color = TextSecondary,
            fontSize = 12.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Canvas(
            modifier = Modifier
                .size(60.dp)
                .background(BoardBackground)
        ) {
            val piece = Tetromino(nextPiece, Position(1, 1))
            val cells = piece.cells()
            val cellSize = size.width / 4f

            cells.forEach { pos ->
                val padding = 1f
                drawRect(
                    color = nextPiece.color,
                    topLeft = Offset(pos.x * cellSize + padding, pos.y * cellSize + padding),
                    size = Size(cellSize - padding * 2, cellSize - padding * 2)
                )
            }

            drawRect(
                color = BoardBorder,
                size = size,
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1f)
            )
        }
    }
}
