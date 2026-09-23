package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.GameStorage
import com.example.model.Block
import com.example.model.BlockGenerator
import com.example.model.BlockShape
import com.example.model.Board
import com.example.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BlockBlastLogicTest {

    @Test
    fun testInitialBoardIsEmpty() {
        val board = Board()
        for (r in 0 until Board.GRID_SIZE) {
            for (c in 0 until Board.GRID_SIZE) {
                assertEquals(null, board[r, c])
            }
        }
    }

    @Test
    fun testBlockPlacementValidity() {
        val board = Board()
        val dotBlock = Block(shape = BlockShape.DOT, colorIndex = 1)
        val line3Block = Block(shape = BlockShape.H_LINE_3, colorIndex = 2)

        // Valid placements
        assertTrue(board.canPlace(dotBlock, 0, 0))
        assertTrue(board.canPlace(dotBlock, 7, 7))
        assertTrue(board.canPlace(line3Block, 0, 0))
        assertTrue(board.canPlace(line3Block, 0, 5)) // length 3 at col 5 -> cols 5,6,7

        // Out of bounds
        assertFalse(board.canPlace(line3Block, 0, 6)) // length 3 at col 6 -> cols 6,7,8 (8 out of bounds)
        assertFalse(board.canPlace(dotBlock, -1, 0))
        assertFalse(board.canPlace(dotBlock, 8, 0))

        // Collision test
        val placedBoard = board.place(dotBlock, 2, 2)
        assertEquals(1, placedBoard[2, 2])
        assertFalse(placedBoard.canPlace(dotBlock, 2, 2))
    }

    @Test
    fun testHorizontalRowLineClear() {
        var board = Board()
        val hLine4 = Block(shape = BlockShape.H_LINE_4, colorIndex = 3)

        // Place two 4-wide horizontal lines to fill row 0 completely
        board = board.place(hLine4, 0, 0)
        board = board.place(hLine4, 0, 4)

        val clearResult = board.findCompletedLines()
        assertTrue(clearResult.hasClears)
        assertEquals(1, clearResult.totalLines)
        assertEquals(8, clearResult.clearedCells.size)

        // Clear cells
        val clearedBoard = board.clearCells(clearResult.clearedCells)
        for (c in 0 until Board.GRID_SIZE) {
            assertEquals(null, clearedBoard[0, c])
        }
    }

    @Test
    fun testVerticalColumnLineClear() {
        var board = Board()
        val vLine4 = Block(shape = BlockShape.V_LINE_4, colorIndex = 4)

        // Place two 4-high vertical lines to fill column 3 completely
        board = board.place(vLine4, 0, 3)
        board = board.place(vLine4, 4, 3)

        val clearResult = board.findCompletedLines()
        assertTrue(clearResult.hasClears)
        assertEquals(1, clearResult.totalLines)
        assertEquals(8, clearResult.clearedCells.size)

        val clearedBoard = board.clearCells(clearResult.clearedCells)
        for (r in 0 until Board.GRID_SIZE) {
            assertEquals(null, clearedBoard[r, 3])
        }
    }

    @Test
    fun testSimultaneousCrossClearing() {
        var board = Board()
        val dot = Block(shape = BlockShape.DOT, colorIndex = 5)

        // Fill row 2 and column 2 completely
        for (c in 0 until Board.GRID_SIZE) {
            board = board.place(dot, 2, c)
        }
        for (r in 0 until Board.GRID_SIZE) {
            if (r != 2) {
                board = board.place(dot, r, 2)
            }
        }

        val clearResult = board.findCompletedLines()
        assertTrue(clearResult.hasClears)
        assertEquals(2, clearResult.totalLines)
        // 8 for row + 8 for col - 1 intersection = 15 distinct cells
        assertEquals(15, clearResult.clearedCells.size)

        val clearedBoard = board.clearCells(clearResult.clearedCells)
        for (i in 0 until Board.GRID_SIZE) {
            assertEquals(null, clearedBoard[2, i])
            assertEquals(null, clearedBoard[i, 2])
        }
    }

    @Test
    fun testGameOverDetection() {
        var board = Board()
        val dot = Block(shape = BlockShape.DOT, colorIndex = 1)
        val square3x3 = Block(shape = BlockShape.SQUARE_3X3, colorIndex = 2)

        // Empty board is not game over
        assertFalse(board.isGameOver(listOf(square3x3)))

        // Fill board completely
        for (r in 0 until Board.GRID_SIZE) {
            for (c in 0 until Board.GRID_SIZE) {
                board = board.place(dot, r, c)
            }
        }

        // With all cells full, a dot cannot be placed
        assertTrue(board.isGameOver(listOf(dot)))
    }

    @Test
    fun testBlockGenerator() {
        val set = BlockGenerator.generateBlockSet()
        assertEquals(3, set.size)
        for (block in set) {
            assertNotNull(block)
            assertTrue(block.width in 1..8)
            assertTrue(block.height in 1..8)
            assertTrue(block.cells.isNotEmpty())
        }
    }

    @Test
    fun testGameStoragePersistence() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val storage = GameStorage(context)

        storage.resetBestScore()
        assertEquals(0, storage.getBestScore())

        val isNew = storage.checkAndSaveBestScore(1500)
        assertTrue(isNew)
        assertEquals(1500, storage.getBestScore())

        val isNotNew = storage.checkAndSaveBestScore(1200)
        assertFalse(isNotNew)
        assertEquals(1500, storage.getBestScore())

        storage.setSoundEnabled(false)
        assertFalse(storage.isSoundEnabled())
        storage.setSoundEnabled(true)
        assertTrue(storage.isSoundEnabled())

        storage.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, storage.getThemeMode())
    }
}
