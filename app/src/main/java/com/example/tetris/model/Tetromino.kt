package com.example.tetris.model

data class Tetromino(
    val type: TetrominoType,
    val position: Position,
    val rotation: Int = 0
) {
    fun cells(): List<Position> {
        return SHAPES[type]!![rotation].map { offset ->
            Position(position.x + offset.x, position.y + offset.y)
        }
    }

    fun movedLeft() = copy(position = Position(position.x - 1, position.y))
    fun movedRight() = copy(position = Position(position.x + 1, position.y))
    fun movedDown() = copy(position = Position(position.x, position.y + 1))

    fun rotatedCW(): Tetromino {
        val newRotation = (rotation + 1) % 4
        return copy(rotation = newRotation)
    }

    fun rotatedCCW(): Tetromino {
        val newRotation = (rotation + 3) % 4
        return copy(rotation = newRotation)
    }

    fun getWallKicks(clockwise: Boolean): List<Position> {
        val fromRotation = rotation
        val toRotation = if (clockwise) (rotation + 1) % 4 else (rotation + 3) % 4
        val kickKey = fromRotation to toRotation
        return if (type == TetrominoType.I) {
            I_WALL_KICKS[kickKey] ?: emptyList()
        } else {
            JLSTZ_WALL_KICKS[kickKey] ?: emptyList()
        }
    }

    companion object {
        // SRS shape definitions: offsets from pivot for each rotation state
        // Y increases downward
        val SHAPES: Map<TetrominoType, Array<List<Position>>> = mapOf(
            TetrominoType.I to arrayOf(
                listOf(Position(-1, 0), Position(0, 0), Position(1, 0), Position(2, 0)),
                listOf(Position(1, -1), Position(1, 0), Position(1, 1), Position(1, 2)),
                listOf(Position(-1, 1), Position(0, 1), Position(1, 1), Position(2, 1)),
                listOf(Position(0, -1), Position(0, 0), Position(0, 1), Position(0, 2))
            ),
            TetrominoType.O to arrayOf(
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1)),
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1)),
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1)),
                listOf(Position(0, 0), Position(1, 0), Position(0, 1), Position(1, 1))
            ),
            TetrominoType.T to arrayOf(
                listOf(Position(-1, 0), Position(0, 0), Position(1, 0), Position(0, -1)),
                listOf(Position(0, -1), Position(0, 0), Position(0, 1), Position(1, 0)),
                listOf(Position(-1, 0), Position(0, 0), Position(1, 0), Position(0, 1)),
                listOf(Position(0, -1), Position(0, 0), Position(0, 1), Position(-1, 0))
            ),
            TetrominoType.S to arrayOf(
                listOf(Position(-1, 0), Position(0, 0), Position(0, -1), Position(1, -1)),
                listOf(Position(0, -1), Position(0, 0), Position(1, 0), Position(1, 1)),
                listOf(Position(-1, 1), Position(0, 1), Position(0, 0), Position(1, 0)),
                listOf(Position(-1, -1), Position(-1, 0), Position(0, 0), Position(0, 1))
            ),
            TetrominoType.Z to arrayOf(
                listOf(Position(-1, -1), Position(0, -1), Position(0, 0), Position(1, 0)),
                listOf(Position(1, -1), Position(1, 0), Position(0, 0), Position(0, 1)),
                listOf(Position(-1, 0), Position(0, 0), Position(0, 1), Position(1, 1)),
                listOf(Position(0, -1), Position(0, 0), Position(-1, 0), Position(-1, 1))
            ),
            TetrominoType.J to arrayOf(
                listOf(Position(-1, -1), Position(-1, 0), Position(0, 0), Position(1, 0)),
                listOf(Position(0, -1), Position(0, 0), Position(0, 1), Position(1, -1)),
                listOf(Position(-1, 0), Position(0, 0), Position(1, 0), Position(1, 1)),
                listOf(Position(-1, 1), Position(0, -1), Position(0, 0), Position(0, 1))
            ),
            TetrominoType.L to arrayOf(
                listOf(Position(-1, 0), Position(0, 0), Position(1, 0), Position(1, -1)),
                listOf(Position(0, -1), Position(0, 0), Position(0, 1), Position(1, 1)),
                listOf(Position(-1, 0), Position(0, 0), Position(1, 0), Position(-1, 1)),
                listOf(Position(-1, -1), Position(0, -1), Position(0, 0), Position(0, 1))
            )
        )

        // SRS Wall Kick data for J, L, S, T, Z pieces
        private val JLSTZ_WALL_KICKS: Map<Pair<Int, Int>, List<Position>> = mapOf(
            (0 to 1) to listOf(Position(0, 0), Position(-1, 0), Position(-1, -1), Position(0, 2), Position(-1, 2)),
            (1 to 0) to listOf(Position(0, 0), Position(1, 0), Position(1, 1), Position(0, -2), Position(1, -2)),
            (1 to 2) to listOf(Position(0, 0), Position(1, 0), Position(1, 1), Position(0, -2), Position(1, -2)),
            (2 to 1) to listOf(Position(0, 0), Position(-1, 0), Position(-1, -1), Position(0, 2), Position(-1, 2)),
            (2 to 3) to listOf(Position(0, 0), Position(1, 0), Position(1, -1), Position(0, 2), Position(1, 2)),
            (3 to 2) to listOf(Position(0, 0), Position(-1, 0), Position(-1, 1), Position(0, -2), Position(-1, -2)),
            (3 to 0) to listOf(Position(0, 0), Position(-1, 0), Position(-1, 1), Position(0, -2), Position(-1, -2)),
            (0 to 3) to listOf(Position(0, 0), Position(1, 0), Position(1, -1), Position(0, 2), Position(1, 2))
        )

        // SRS Wall Kick data for I piece
        private val I_WALL_KICKS: Map<Pair<Int, Int>, List<Position>> = mapOf(
            (0 to 1) to listOf(Position(0, 0), Position(-2, 0), Position(1, 0), Position(-2, 1), Position(1, -2)),
            (1 to 0) to listOf(Position(0, 0), Position(2, 0), Position(-1, 0), Position(2, -1), Position(-1, 2)),
            (1 to 2) to listOf(Position(0, 0), Position(-1, 0), Position(2, 0), Position(-1, -2), Position(2, 1)),
            (2 to 1) to listOf(Position(0, 0), Position(1, 0), Position(-2, 0), Position(1, 2), Position(-2, -1)),
            (2 to 3) to listOf(Position(0, 0), Position(2, 0), Position(-1, 0), Position(2, -1), Position(-1, 2)),
            (3 to 2) to listOf(Position(0, 0), Position(-2, 0), Position(1, 0), Position(-2, 1), Position(1, -2)),
            (3 to 0) to listOf(Position(0, 0), Position(1, 0), Position(-2, 0), Position(1, 2), Position(-2, -1)),
            (0 to 3) to listOf(Position(0, 0), Position(-1, 0), Position(2, 0), Position(-1, -2), Position(2, 1))
        )
    }
}
