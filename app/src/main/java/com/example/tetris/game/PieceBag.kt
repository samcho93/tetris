package com.example.tetris.game

import com.example.tetris.model.TetrominoType

class PieceBag {
    private val bag: MutableList<TetrominoType> = mutableListOf()

    fun next(): TetrominoType {
        if (bag.isEmpty()) {
            bag.addAll(TetrominoType.entries.shuffled())
        }
        return bag.removeFirst()
    }

    fun reset() {
        bag.clear()
    }
}
