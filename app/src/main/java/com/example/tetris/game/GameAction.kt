package com.example.tetris.game

sealed class GameAction {
    object MoveLeft : GameAction()
    object MoveRight : GameAction()
    object RotateCW : GameAction()
    object SoftDrop : GameAction()
    object HardDrop : GameAction()
    object Pause : GameAction()
    object Resume : GameAction()
    object Tick : GameAction()
    object ToggleMusic : GameAction()
    data class StartGame(val level: Int) : GameAction()
}
