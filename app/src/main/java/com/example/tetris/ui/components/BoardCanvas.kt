package com.example.tetris.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import com.example.tetris.model.Cell
import com.example.tetris.model.GameState
import com.example.tetris.model.Tetromino
import com.example.tetris.ui.theme.BoardBackground
import com.example.tetris.ui.theme.BoardBorder
import com.example.tetris.ui.theme.GhostPiece
import com.example.tetris.ui.theme.GridLine

@Composable
fun BoardCanvas(
    state: GameState,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cellWidth = size.width / COLUMNS
        val cellHeight = size.height / VISIBLE_ROWS

        // Background
        drawRect(color = BoardBackground)

        // Grid lines
        drawGrid(cellWidth, cellHeight)

        // Placed blocks
        drawPlacedBlocks(state.boardSnapshot, state.cameraTopRow, cellWidth, cellHeight)

        // Ghost piece
        state.currentPiece?.let { piece ->
            drawGhostPiece(piece, state.ghostY, state.cameraTopRow, cellWidth, cellHeight)
        }

        // Current piece
        state.currentPiece?.let { piece ->
            drawTetromino(piece, state.cameraTopRow, cellWidth, cellHeight, piece.type.color)
        }

        // Hidden-blocks-below indicator
        if (state.hiddenRowsBelow > 0) {
            drawHiddenIndicator(cellWidth, cellHeight, state.hiddenRowsBelow)
        }

        // Border
        drawRect(
            color = BoardBorder,
            style = Stroke(width = 2f)
        )
    }
}

private fun DrawScope.drawGrid(cellWidth: Float, cellHeight: Float) {
    for (col in 1 until COLUMNS) {
        drawLine(
            color = GridLine,
            start = Offset(col * cellWidth, 0f),
            end = Offset(col * cellWidth, size.height),
            strokeWidth = 0.5f
        )
    }
    for (row in 1 until VISIBLE_ROWS) {
        drawLine(
            color = GridLine,
            start = Offset(0f, row * cellHeight),
            end = Offset(size.width, row * cellHeight),
            strokeWidth = 0.5f
        )
    }
}

private fun DrawScope.drawPlacedBlocks(
    boardSnapshot: Map<Int, Array<Cell?>>,
    cameraTopRow: Int,
    cellWidth: Float,
    cellHeight: Float
) {
    for ((worldY, row) in boardSnapshot) {
        val screenRow = worldY - cameraTopRow
        if (screenRow !in 0 until VISIBLE_ROWS) continue
        for (col in row.indices) {
            val cell = row[col] ?: continue
            drawBlock(col, screenRow, cellWidth, cellHeight, cell.type.color)
        }
    }
}

private fun DrawScope.drawTetromino(
    piece: Tetromino,
    cameraTopRow: Int,
    cellWidth: Float,
    cellHeight: Float,
    color: Color
) {
    piece.cells().forEach { pos ->
        val screenRow = pos.y - cameraTopRow
        if (screenRow in 0 until VISIBLE_ROWS) {
            drawBlock(pos.x, screenRow, cellWidth, cellHeight, color)
        }
    }
}

private fun DrawScope.drawGhostPiece(
    piece: Tetromino,
    ghostY: Int,
    cameraTopRow: Int,
    cellWidth: Float,
    cellHeight: Float
) {
    val ghostPiece = piece.copy(position = piece.position.copy(y = ghostY))
    ghostPiece.cells().forEach { pos ->
        val screenRow = pos.y - cameraTopRow
        if (screenRow in 0 until VISIBLE_ROWS) {
            drawBlock(pos.x, screenRow, cellWidth, cellHeight, GhostPiece)
        }
    }
}

/**
 * Visual indicator at the bottom of the board showing that
 * there are hidden block-rows below the viewport.
 */
@Suppress("UNUSED_PARAMETER")
private fun DrawScope.drawHiddenIndicator(
    cellWidth: Float,
    cellHeight: Float,
    hiddenRows: Int
) {
    // Gradient overlay on the bottom 2 rows
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color.Transparent, Color(0xFFFF6B00).copy(alpha = 0.25f)),
            startY = size.height - cellHeight * 2,
            endY = size.height
        )
    )

    // Solid indicator bar at the very bottom
    drawRect(
        color = Color(0xFFFF6B00).copy(alpha = 0.7f),
        topLeft = Offset(0f, size.height - 4f),
        size = Size(size.width, 4f)
    )

    // Small chevrons (▼) to hint at hidden content
    val chevronCount = hiddenRows.coerceAtMost(5)
    val spacing = size.width / (chevronCount + 1)
    for (i in 1..chevronCount) {
        val cx = spacing * i
        val cy = size.height - 8f
        val halfW = 4f
        val path = androidx.compose.ui.graphics.Path().apply {
            moveTo(cx - halfW, cy - 3f)
            lineTo(cx, cy + 3f)
            lineTo(cx + halfW, cy - 3f)
        }
        drawPath(path, color = Color.White.copy(alpha = 0.8f))
    }
}

private fun DrawScope.drawBlock(
    col: Int,
    row: Int,
    cellWidth: Float,
    cellHeight: Float,
    color: Color
) {
    val padding = 1f
    drawRect(
        color = color,
        topLeft = Offset(col * cellWidth + padding, row * cellHeight + padding),
        size = Size(cellWidth - padding * 2, cellHeight - padding * 2)
    )
    // Highlight (top-left shine)
    drawRect(
        color = Color.White.copy(alpha = 0.3f),
        topLeft = Offset(col * cellWidth + padding, row * cellHeight + padding),
        size = Size(cellWidth - padding * 2, 2f)
    )
    drawRect(
        color = Color.White.copy(alpha = 0.2f),
        topLeft = Offset(col * cellWidth + padding, row * cellHeight + padding),
        size = Size(2f, cellHeight - padding * 2)
    )
    // Shadow (bottom-right)
    drawRect(
        color = Color.Black.copy(alpha = 0.3f),
        topLeft = Offset(col * cellWidth + padding, row * cellHeight + cellHeight - padding - 2f),
        size = Size(cellWidth - padding * 2, 2f)
    )
}

private const val COLUMNS = 10
private const val VISIBLE_ROWS = 24
