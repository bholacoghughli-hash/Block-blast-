package com.example.model

/**
 * Result of row and column clearing analysis.
 */
data class LineClearResult(
    val completedRows: List<Int>,
    val completedCols: List<Int>,
    val clearedCells: Set<Pair<Int, Int>>
) {
    val totalLines: Int get() = completedRows.size + completedCols.size
    val hasClears: Boolean get() = totalLines > 0
}

/**
 * Immutable 8x8 Board representation.
 * Cell value is null if empty, or an integer colorIndex (0..9) if occupied.
 */
data class Board(
    val grid: List<List<Int?>> = List(GRID_SIZE) { List(GRID_SIZE) { null } }
) {
    companion object {
        const val GRID_SIZE = 8
    }

    operator fun get(row: Int, col: Int): Int? {
        if (row !in 0 until GRID_SIZE || col !in 0 until GRID_SIZE) return null
        return grid[row][col]
    }

    /**
     * Checks if a block can legally be placed with its top-left at (startRow, startCol).
     */
    fun canPlace(block: Block, startRow: Int, startCol: Int): Boolean {
        for (cell in block.cells) {
            val r = startRow + cell.row
            val c = startCol + cell.col
            if (r !in 0 until GRID_SIZE || c !in 0 until GRID_SIZE) {
                return false
            }
            if (grid[r][c] != null) {
                return false
            }
        }
        return true
    }

    /**
     * Returns the set of board cell coordinates (row, col) that the block would occupy.
     */
    fun getTargetCells(block: Block, startRow: Int, startCol: Int): Set<Pair<Int, Int>> {
        val set = mutableSetOf<Pair<Int, Int>>()
        for (cell in block.cells) {
            val r = startRow + cell.row
            val c = startCol + cell.col
            if (r in 0 until GRID_SIZE && c in 0 until GRID_SIZE) {
                set.add(Pair(r, c))
            }
        }
        return set
    }

    /**
     * Returns which board cells would be cleared if this block were placed at (startRow, startCol).
     * Used for real-time visual line highlight preview.
     */
    fun getHypotheticalClearingCells(block: Block, startRow: Int, startCol: Int): Set<Pair<Int, Int>> {
        if (!canPlace(block, startRow, startCol)) return emptySet()

        val temp = place(block, startRow, startCol)
        val clearResult = temp.findCompletedLines()
        return clearResult.clearedCells
    }

    /**
     * Places the block on the board and returns a new Board instance.
     */
    fun place(block: Block, startRow: Int, startCol: Int): Board {
        val newGrid = grid.map { it.toMutableList() }
        for (cell in block.cells) {
            val r = startRow + cell.row
            val c = startCol + cell.col
            if (r in 0 until GRID_SIZE && c in 0 until GRID_SIZE) {
                newGrid[r][c] = block.colorIndex
            }
        }
        return Board(newGrid.map { it.toList() })
    }

    /**
     * Identifies all full rows and columns on the board.
     */
    fun findCompletedLines(): LineClearResult {
        val fullRows = mutableListOf<Int>()
        for (r in 0 until GRID_SIZE) {
            var rowFull = true
            for (c in 0 until GRID_SIZE) {
                if (grid[r][c] == null) {
                    rowFull = false
                    break
                }
            }
            if (rowFull) fullRows.add(r)
        }

        val fullCols = mutableListOf<Int>()
        for (c in 0 until GRID_SIZE) {
            var colFull = true
            for (r in 0 until GRID_SIZE) {
                if (grid[r][c] == null) {
                    colFull = false
                    break
                }
            }
            if (colFull) fullCols.add(c)
        }

        val cells = mutableSetOf<Pair<Int, Int>>()
        for (r in fullRows) {
            for (c in 0 until GRID_SIZE) {
                cells.add(Pair(r, c))
            }
        }
        for (c in fullCols) {
            for (r in 0 until GRID_SIZE) {
                cells.add(Pair(r, c))
            }
        }

        return LineClearResult(fullRows, fullCols, cells)
    }

    /**
     * Clears the specified set of cell coordinates and returns the new Board.
     */
    fun clearCells(cellsToClear: Set<Pair<Int, Int>>): Board {
        val newGrid = grid.map { it.toMutableList() }
        for ((r, c) in cellsToClear) {
            if (r in 0 until GRID_SIZE && c in 0 until GRID_SIZE) {
                newGrid[r][c] = null
            }
        }
        return Board(newGrid.map { it.toList() })
    }

    /**
     * Checks if a specific block can legally fit ANYWHERE on the board.
     */
    fun canBlockFitAnywhere(block: Block): Boolean {
        val maxR = GRID_SIZE - block.height
        val maxC = GRID_SIZE - block.width
        if (maxR < 0 || maxC < 0) return false

        for (r in 0..maxR) {
            for (c in 0..maxC) {
                if (canPlace(block, r, c)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Checks if the game is over.
     * Game Over occurs when there are remaining blocks in the tray,
     * AND none of them can fit in any legal position on the board.
     */
    fun isGameOver(availableBlocks: List<Block?>): Boolean {
        val remaining = availableBlocks.filterNotNull()
        if (remaining.isEmpty()) return false

        for (block in remaining) {
            if (canBlockFitAnywhere(block)) {
                return false // At least one block can still be placed
            }
        }
        return true // No block fits!
    }

    /**
     * Clears entire board (for restart).
     */
    fun clear(): Board = Board()
}
