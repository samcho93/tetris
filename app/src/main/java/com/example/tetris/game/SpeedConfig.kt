package com.example.tetris.game

object SpeedConfig {
    fun tickIntervalMs(level: Int): Long = when (level) {
        1 -> 1000L
        2 -> 500L
        3 -> 250L
        else -> 1000L
    }
}
