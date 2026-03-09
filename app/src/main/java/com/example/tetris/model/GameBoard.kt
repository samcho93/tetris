package com.example.tetris.model

class GameBoard {
    private val rows: MutableMap<Int, Array<Cell?>> = mutableMapOf()

    // Floor is fixed at the bottom – never changes
    val floorY: Int = VISIBLE_ROWS

    fun getCell(x: Int, y: Int): Cell? {
        if (x !in 0 until COLUMNS) return null
        return rows[y]?.get(x)
    }

    fun setCell(x: Int, y: Int, cell: Cell) {
        if (x !in 0 until COLUMNS) return
        val row = rows.getOrPut(y) { arrayOfNulls(COLUMNS) }
        row[x] = cell
    }

    fun isOccupied(x: Int, y: Int): Boolean {
        if (x !in 0 until COLUMNS) return true
        if (y >= floorY) return true
        // No upper boundary – blocks can exist at any negative Y
        return rows[y]?.get(x) != null
    }

    fun placeTetromino(tetromino: Tetromino) {
        val cell = Cell(tetromino.type)
        tetromino.cells().forEach { pos ->
            setCell(pos.x, pos.y, cell)
        }
    }

    /** Returns the topmost (smallest Y) row that has at least one block. */
    fun highestOccupiedRow(): Int? {
        return rows.keys.filter { y ->
            rows[y]?.any { it != null } == true
        }.minOrNull()
    }

    /**
     * Clear complete lines within the VISIBLE viewport only.
     * Hidden rows below the viewport are frozen until revealed.
     */
    fun clearCompleteLines(cameraTopRow: Int): Int {
        val completedRows = mutableListOf<Int>()
        val viewportBottom = cameraTopRow + VISIBLE_ROWS

        // Only check rows inside the visible viewport
        for (y in cameraTopRow until viewportBottom) {
            val row = rows[y] ?: continue
            if (row.all { it != null }) {
                completedRows.add(y)
            }
        }

        if (completedRows.isEmpty()) return 0

        val clearedSet = completedRows.toSet()

        // Rebuild: shift rows above cleared area downward to fill gaps
        val newRows = mutableMapOf<Int, Array<Cell?>>()
        val minRow = rows.keys.filter { it <= completedRows.max() }.minOrNull()
            ?: return completedRows.size
        var writeY = completedRows.max()

        for (readY in completedRows.max() downTo minRow) {
            if (readY in clearedSet) continue
            val row = rows[readY] ?: continue
            if (row.any { it != null }) {
                newRows[writeY] = row
            }
            writeY--
        }

        // Remove old rows in the affected range and put compacted ones
        for (y in minRow..completedRows.max()) {
            rows.remove(y)
        }
        rows.putAll(newRows)

        return completedRows.size
    }

    /** Count how many rows with blocks exist below the viewport bottom. */
    fun countHiddenRowsBelow(viewportBottom: Int): Int {
        return rows.keys.count { y ->
            y >= viewportBottom && rows[y]?.any { it != null } == true
        }
    }

    fun getVisibleRows(cameraTopRow: Int): Map<Int, Array<Cell?>> {
        val result = mutableMapOf<Int, Array<Cell?>>()
        for (y in cameraTopRow until cameraTopRow + VISIBLE_ROWS) {
            rows[y]?.let { row ->
                if (row.any { it != null }) {
                    result[y] = row.copyOf()
                }
            }
        }
        return result
    }

    fun reset() {
        rows.clear()
    }

    companion object {
        const val COLUMNS = 10
        const val VISIBLE_ROWS = 24
    }
}
