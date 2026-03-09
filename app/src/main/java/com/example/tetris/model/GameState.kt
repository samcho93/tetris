package com.example.tetris.model

enum class GamePhase {
    MENU,
    PLAYING,
    PAUSED,
    LINE_CLEAR_ANIMATION
}

data class GameState(
    val currentPiece: Tetromino? = null,
    val nextPiece: TetrominoType = TetrominoType.T,
    val ghostY: Int = 0,
    val cameraTopRow: Int = 0,
    val score: Long = 0,
    val linesCleared: Int = 0,
    val level: Int = 1,
    val phase: GamePhase = GamePhase.MENU,
    val boardSnapshot: Map<Int, Array<Cell?>> = emptyMap(),
    val hiddenRowsBelow: Int = 0,
    val musicEnabled: Boolean = true,
    val tick: Long = 0 // incremented to force recomposition
)
