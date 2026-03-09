package com.example.tetris.game

import com.example.tetris.model.*

class GameEngine {
    val board = GameBoard()
    private val pieceBag = PieceBag()

    var cameraTopRow: Int = 0
        private set
    var currentPiece: Tetromino? = null
        private set
    var nextType: TetrominoType = TetrominoType.T
        private set
    var score: Long = 0
        private set
    var linesCleared: Int = 0
        private set

    private var hasStarted = false

    fun start() {
        board.reset()
        pieceBag.reset()
        cameraTopRow = 0
        score = 0
        linesCleared = 0
        hasStarted = true
        nextType = pieceBag.next()
        spawnNextPiece()
    }

    fun spawnNextPiece(): Boolean {
        val type = nextType
        nextType = pieceBag.next()

        val spawnX = COLUMNS / 2 - 1
        val spawnY = cameraTopRow + 1

        val piece = Tetromino(type, Position(spawnX, spawnY))

        if (!isValidPosition(piece)) {
            // Force scroll camera up to create space (infinite mode – no game over)
            while (!isValidPosition(Tetromino(type, Position(spawnX, cameraTopRow + 1)))) {
                cameraTopRow -= 1
            }
            currentPiece = Tetromino(type, Position(spawnX, cameraTopRow + 1))
            return true
        }

        currentPiece = piece
        return true
    }

    fun isValidPosition(piece: Tetromino): Boolean {
        return piece.cells().all { pos ->
            pos.x in 0 until COLUMNS &&
            pos.y < board.floorY &&
            !board.isOccupied(pos.x, pos.y)
        }
    }

    fun tryMoveLeft(): Boolean {
        val piece = currentPiece ?: return false
        val moved = piece.movedLeft()
        if (isValidPosition(moved)) {
            currentPiece = moved
            return true
        }
        return false
    }

    fun tryMoveRight(): Boolean {
        val piece = currentPiece ?: return false
        val moved = piece.movedRight()
        if (isValidPosition(moved)) {
            currentPiece = moved
            return true
        }
        return false
    }

    fun tryMoveDown(): Boolean {
        val piece = currentPiece ?: return false
        val moved = piece.movedDown()
        if (isValidPosition(moved)) {
            currentPiece = moved
            return true
        }
        return false
    }

    fun tryRotateCW(): Boolean {
        val piece = currentPiece ?: return false
        val rotated = piece.rotatedCW()
        val kicks = piece.getWallKicks(clockwise = true)

        for (kick in kicks) {
            val kicked = rotated.copy(
                position = Position(rotated.position.x + kick.x, rotated.position.y + kick.y)
            )
            if (isValidPosition(kicked)) {
                currentPiece = kicked
                return true
            }
        }
        return false
    }

    fun hardDrop(): Int {
        val piece = currentPiece ?: return 0
        var dropDistance = 0
        var dropped = piece
        while (isValidPosition(dropped.movedDown())) {
            dropped = dropped.movedDown()
            dropDistance++
        }
        currentPiece = dropped
        return dropDistance
    }

    fun calculateGhostY(): Int {
        val piece = currentPiece ?: return 0
        var ghost = piece
        while (isValidPosition(ghost.movedDown())) {
            ghost = ghost.movedDown()
        }
        return ghost.position.y
    }

    fun lockPiece(): LockResult {
        val piece = currentPiece ?: return LockResult(0, false)
        board.placeTetromino(piece)

        val cleared = board.clearCompleteLines(cameraTopRow)
        linesCleared += cleared
        score += calculateScore(cleared)

        var scrolled = false

        // After clearing lines, scroll camera back down to reveal hidden blocks
        if (cleared > 0) {
            scrolled = checkScrollBack() || scrolled
        }

        // Check if pile reached top 1/3 – need to scroll camera up
        scrolled = checkScrollUp() || scrolled

        currentPiece = null
        return LockResult(cleared, scrolled)
    }

    private fun calculateScore(lines: Int): Long = when (lines) {
        1 -> 100L
        2 -> 300L
        3 -> 500L
        4 -> 800L
        else -> 0L
    }

    /**
     * When the pile's top reaches the upper 1/3 of the viewport,
     * scroll the camera UP (cameraTopRow decreases).
     *
     * Effect: bottom rows of the pile slide below the viewport (hidden),
     * and empty space appears at the top for new pieces.
     */
    private fun checkScrollUp(): Boolean {
        val highest = board.highestOccupiedRow() ?: return false
        val relativeRow = highest - cameraTopRow

        if (relativeRow < SCROLL_THRESHOLD) {
            val needed = SCROLL_THRESHOLD - relativeRow
            cameraTopRow -= needed
            return true
        }
        return false
    }

    /**
     * After clearing lines, if too much empty space at the top,
     * scroll the camera back DOWN (cameraTopRow increases toward 0).
     *
     * Effect: hidden blocks below the viewport become visible again,
     * and excess empty space at the top is removed.
     */
    private fun checkScrollBack(): Boolean {
        if (cameraTopRow >= 0) return false

        val highest = board.highestOccupiedRow()
        if (highest == null) {
            // All blocks cleared – reset camera to origin
            cameraTopRow = 0
            return true
        }

        val relativeRow = highest - cameraTopRow

        // If pile top has dropped past the 1/3 mark, scroll back
        if (relativeRow > SCROLL_THRESHOLD) {
            val targetRow = highest - SCROLL_THRESHOLD
            val newCamera = targetRow.coerceAtMost(0)
            if (newCamera > cameraTopRow) {
                cameraTopRow = newCamera
                return true
            }
        }
        return false
    }

    /** Number of hidden block-rows below the visible viewport. */
    fun getHiddenRowsBelow(): Int {
        return board.countHiddenRowsBelow(cameraTopRow + VISIBLE_ROWS)
    }

    fun getVisibleBoardSnapshot(): Map<Int, Array<Cell?>> {
        return board.getVisibleRows(cameraTopRow)
    }

    data class LockResult(
        val linesCleared: Int,
        val cameraScrolled: Boolean
    )

    companion object {
        const val COLUMNS = 10
        const val VISIBLE_ROWS = 24
        /** Pile top must stay at least this many rows from the viewport top. */
        const val SCROLL_THRESHOLD = VISIBLE_ROWS / 3  // 8
    }
}
