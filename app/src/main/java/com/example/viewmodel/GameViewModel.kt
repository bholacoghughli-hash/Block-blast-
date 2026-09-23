package com.example.viewmodel

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.SoundManager
import com.example.data.GameStorage
import com.example.haptics.HapticManager
import com.example.model.Block
import com.example.model.BlockGenerator
import com.example.model.Board
import com.example.model.GameScreenState
import com.example.model.ThemeMode
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val currentScreen: GameScreenState = GameScreenState.HOME,
    val previousScreen: GameScreenState = GameScreenState.HOME,
    val score: Int = 0,
    val bestScore: Int = 0,
    val isNewBestScore: Boolean = false,
    val comboCount: Int = 0,
    val comboBannerText: String? = null,
    val board: Board = Board(),
    val trayBlocks: List<Block?> = listOf(null, null, null),
    val clearingCells: Set<Pair<Int, Int>> = emptySet(),
    val isBoardLocked: Boolean = false,
    // Drag state
    val isDragging: Boolean = false,
    val draggedSlotIndex: Int? = null,
    val dragTouchOffset: Offset = Offset.Zero,
    val hoverStartRow: Int? = null,
    val hoverStartCol: Int? = null,
    val isHoverValid: Boolean = false,
    val hoverClearingCells: Set<Pair<Int, Int>> = emptySet(),
    // Dialogs
    val showRestartDialog: Boolean = false,
    val showResetBestScoreDialog: Boolean = false,
    // Settings
    val soundEnabled: Boolean = true,
    val musicEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val storage = GameStorage(application)
    val soundManager = SoundManager()
    val hapticManager = HapticManager(application)

    private val _uiState = MutableStateFlow(
        GameUiState(
            bestScore = storage.getBestScore(),
            soundEnabled = storage.isSoundEnabled(),
            musicEnabled = storage.isMusicEnabled(),
            vibrationEnabled = storage.isVibrationEnabled(),
            themeMode = storage.getThemeMode()
        )
    )
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    // Screen board bounds for coordinate mapping
    var boardBounds: Rect = Rect.Zero
    var boardCellSizePx: Float = 0f

    init {
        soundManager.isSoundEnabled = _uiState.value.soundEnabled
        hapticManager.isVibrationEnabled = _uiState.value.vibrationEnabled
    }

    /**
     * Start a new game or resume playing.
     */
    fun startGame() {
        soundManager.playClick()
        if (_uiState.value.trayBlocks.all { it == null }) {
            resetGame(playSfx = false)
        }
        _uiState.value = _uiState.value.copy(
            currentScreen = GameScreenState.PLAYING
        )
    }

    /**
     * Resets game state, clears board, generates 3 new blocks.
     */
    fun resetGame(playSfx: Boolean = true) {
        if (playSfx) soundManager.playClick()
        val newBlocks = BlockGenerator.generateBlockSet()
        _uiState.value = _uiState.value.copy(
            board = Board(),
            trayBlocks = newBlocks,
            score = 0,
            comboCount = 0,
            comboBannerText = null,
            isNewBestScore = false,
            clearingCells = emptySet(),
            isBoardLocked = false,
            showRestartDialog = false,
            currentScreen = GameScreenState.PLAYING
        )
    }

    fun navigateTo(screen: GameScreenState) {
        soundManager.playClick()
        val current = _uiState.value.currentScreen
        _uiState.value = _uiState.value.copy(
            currentScreen = screen,
            previousScreen = current
        )
    }

    fun pauseGame() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(
            currentScreen = GameScreenState.PAUSED,
            previousScreen = GameScreenState.PLAYING
        )
    }

    fun resumeGame() {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(
            currentScreen = GameScreenState.PLAYING
        )
    }

    fun handleBackPress(): Boolean {
        return when (_uiState.value.currentScreen) {
            GameScreenState.PLAYING -> {
                pauseGame()
                true
            }
            GameScreenState.PAUSED -> {
                resumeGame()
                true
            }
            GameScreenState.SETTINGS,
            GameScreenState.HOW_TO_PLAY,
            GameScreenState.ABOUT -> {
                val dest = _uiState.value.previousScreen
                _uiState.value = _uiState.value.copy(
                    currentScreen = if (dest == GameScreenState.SETTINGS || dest == GameScreenState.ABOUT || dest == GameScreenState.HOW_TO_PLAY) {
                        GameScreenState.HOME
                    } else dest
                )
                true
            }
            GameScreenState.GAME_OVER -> {
                _uiState.value = _uiState.value.copy(currentScreen = GameScreenState.HOME)
                true
            }
            GameScreenState.HOME -> {
                false // Allow system to exit
            }
        }
    }

    fun showRestartConfirm(show: Boolean) {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(showRestartDialog = show)
    }

    fun showResetScoreConfirm(show: Boolean) {
        soundManager.playClick()
        _uiState.value = _uiState.value.copy(showResetBestScoreDialog = show)
    }

    fun confirmResetBestScore() {
        soundManager.playClick()
        storage.resetBestScore()
        _uiState.value = _uiState.value.copy(
            bestScore = 0,
            showResetBestScoreDialog = false
        )
    }

    // --- Sound, Music, Vibration, Theme Toggles ---

    fun toggleSound() {
        val next = !_uiState.value.soundEnabled
        storage.setSoundEnabled(next)
        soundManager.isSoundEnabled = next
        _uiState.value = _uiState.value.copy(soundEnabled = next)
        if (next) soundManager.playClick()
    }

    fun toggleMusic() {
        soundManager.playClick()
        val next = !_uiState.value.musicEnabled
        storage.setMusicEnabled(next)
        _uiState.value = _uiState.value.copy(musicEnabled = next)
    }

    fun toggleVibration() {
        soundManager.playClick()
        val next = !_uiState.value.vibrationEnabled
        storage.setVibrationEnabled(next)
        hapticManager.isVibrationEnabled = next
        _uiState.value = _uiState.value.copy(vibrationEnabled = next)
        if (next) hapticManager.onBlockPlaced()
    }

    fun setThemeMode(mode: ThemeMode) {
        soundManager.playClick()
        storage.setThemeMode(mode)
        _uiState.value = _uiState.value.copy(themeMode = mode)
    }

    // --- Drag and Drop Handling ---

    fun onDragStart(slotIndex: Int, initialTouchPos: Offset) {
        if (_uiState.value.isBoardLocked) return
        val block = _uiState.value.trayBlocks.getOrNull(slotIndex) ?: return

        soundManager.playPickUp()
        _uiState.value = _uiState.value.copy(
            isDragging = true,
            draggedSlotIndex = slotIndex,
            dragTouchOffset = initialTouchPos,
            hoverStartRow = null,
            hoverStartCol = null,
            isHoverValid = false,
            hoverClearingCells = emptySet()
        )
    }

    fun onDragMove(touchPos: Offset, fingerLiftPx: Float) {
        val state = _uiState.value
        if (!state.isDragging || state.draggedSlotIndex == null) return
        val block = state.trayBlocks.getOrNull(state.draggedSlotIndex) ?: return

        _uiState.value = _uiState.value.copy(dragTouchOffset = touchPos)

        // Calculate visual block center positioned slightly above the user's finger
        if (boardBounds.width <= 0f || boardCellSizePx <= 0f) return

        val blockWidthPx = block.width * boardCellSizePx
        val blockHeightPx = block.height * boardCellSizePx

        val elevatedY = touchPos.y - fingerLiftPx
        val targetLeft = touchPos.x - (blockWidthPx / 2f)
        val targetTop = elevatedY - (blockHeightPx / 2f)

        // Convert target bounds to board grid coordinate (row, col)
        val relX = targetLeft - boardBounds.left
        val relY = targetTop - boardBounds.top

        val col = Math.round(relX / boardCellSizePx)
        val row = Math.round(relY / boardCellSizePx)

        // Check if block can be placed
        val canPlace = state.board.canPlace(block, row, col)
        val hypotheticClears = if (canPlace) {
            state.board.getHypotheticalClearingCells(block, row, col)
        } else {
            emptySet()
        }

        _uiState.value = _uiState.value.copy(
            hoverStartRow = row,
            hoverStartCol = col,
            isHoverValid = canPlace,
            hoverClearingCells = hypotheticClears
        )
    }

    fun onDragEnd() {
        val state = _uiState.value
        if (!state.isDragging || state.draggedSlotIndex == null) return

        val slotIndex = state.draggedSlotIndex
        val block = state.trayBlocks.getOrNull(slotIndex)
        val row = state.hoverStartRow
        val col = state.hoverStartCol
        val isValid = state.isHoverValid && block != null && row != null && col != null

        if (isValid && block != null && row != null && col != null) {
            commitBlockPlacement(slotIndex, block, row, col)
        } else {
            // Invalid placement
            soundManager.playInvalid()
            hapticManager.onInvalid()
            _uiState.value = _uiState.value.copy(
                isDragging = false,
                draggedSlotIndex = null,
                hoverStartRow = null,
                hoverStartCol = null,
                isHoverValid = false,
                hoverClearingCells = emptySet()
            )
        }
    }

    private fun commitBlockPlacement(slotIndex: Int, block: Block, startRow: Int, startCol: Int) {
        hapticManager.onBlockPlaced()
        soundManager.playPlace()

        val oldState = _uiState.value
        val placedBoard = oldState.board.place(block, startRow, startCol)
        val placementScore = block.cellCount * 10
        val intermediateScore = oldState.score + placementScore

        // Remove placed block from tray slot
        val newTray = oldState.trayBlocks.toMutableList()
        newTray[slotIndex] = null

        // Check line clearing
        val clearResult = placedBoard.findCompletedLines()

        if (clearResult.hasClears) {
            val newCombo = oldState.comboCount + 1
            val linesCount = clearResult.totalLines

            val lineScore = when (linesCount) {
                1 -> 100
                2 -> 250
                3 -> 450
                4 -> 700
                else -> 1000 + ((linesCount - 5) * 300)
            }
            val comboBonus = if (newCombo > 1) (newCombo - 1) * 50 else 0
            val totalEarned = placementScore + lineScore + comboBonus
            val finalScore = oldState.score + totalEarned

            val banner = if (newCombo > 1) "COMBO x$newCombo! +$comboBonus" else if (linesCount > 1) "$linesCount LINES! +$lineScore" else null

            hapticManager.onLineCleared(linesCount)
            if (newCombo > 1) {
                soundManager.playCombo(newCombo)
                hapticManager.onCombo()
            } else {
                soundManager.playLineClear(linesCount)
            }

            // Check high score
            val isNewBest = finalScore > oldState.bestScore
            val currentBest = maxOf(finalScore, oldState.bestScore)
            if (isNewBest) {
                storage.checkAndSaveBestScore(finalScore)
                if (!oldState.isNewBestScore) {
                    soundManager.playNewBest()
                    hapticManager.onHighScore()
                }
            }

            // Lock board during line clearing animation
            _uiState.value = oldState.copy(
                board = placedBoard,
                trayBlocks = newTray,
                score = finalScore,
                bestScore = currentBest,
                isNewBestScore = isNewBest || oldState.isNewBestScore,
                comboCount = newCombo,
                comboBannerText = banner,
                clearingCells = clearResult.clearedCells,
                isBoardLocked = true,
                isDragging = false,
                draggedSlotIndex = null,
                hoverStartRow = null,
                hoverStartCol = null,
                isHoverValid = false,
                hoverClearingCells = emptySet()
            )

            viewModelScope.launch {
                delay(220) // Line clear visual animation duration
                val clearedBoard = placedBoard.clearCells(clearResult.clearedCells)
                postPlacementChecks(clearedBoard, newTray)
            }
        } else {
            // No lines cleared
            val finalScore = intermediateScore
            val isNewBest = finalScore > oldState.bestScore
            val currentBest = maxOf(finalScore, oldState.bestScore)
            if (isNewBest) {
                storage.checkAndSaveBestScore(finalScore)
                if (!oldState.isNewBestScore) {
                    soundManager.playNewBest()
                    hapticManager.onHighScore()
                }
            }

            _uiState.value = oldState.copy(
                board = placedBoard,
                trayBlocks = newTray,
                score = finalScore,
                bestScore = currentBest,
                isNewBestScore = isNewBest || oldState.isNewBestScore,
                comboCount = 0,
                comboBannerText = null,
                clearingCells = emptySet(),
                isBoardLocked = false,
                isDragging = false,
                draggedSlotIndex = null,
                hoverStartRow = null,
                hoverStartCol = null,
                isHoverValid = false,
                hoverClearingCells = emptySet()
            )

            postPlacementChecks(placedBoard, newTray)
        }
    }

    private fun postPlacementChecks(currentBoard: Board, currentTray: List<Block?>) {
        var updatedTray = currentTray
        // If all 3 blocks placed, generate a fresh batch of 3 blocks!
        if (updatedTray.all { it == null }) {
            updatedTray = BlockGenerator.generateBlockSet()
        }

        // Check Game Over condition
        val gameOver = currentBoard.isGameOver(updatedTray)

        if (gameOver) {
            soundManager.playGameOver()
            _uiState.value = _uiState.value.copy(
                board = currentBoard,
                trayBlocks = updatedTray,
                clearingCells = emptySet(),
                isBoardLocked = false,
                currentScreen = GameScreenState.GAME_OVER
            )
        } else {
            _uiState.value = _uiState.value.copy(
                board = currentBoard,
                trayBlocks = updatedTray,
                clearingCells = emptySet(),
                isBoardLocked = false
            )
        }
    }
}
