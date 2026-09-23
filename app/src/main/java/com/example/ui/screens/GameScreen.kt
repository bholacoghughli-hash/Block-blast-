package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.GameScreenState
import com.example.ui.components.BlockTrayView
import com.example.ui.components.BoardView
import com.example.ui.components.DraggedBlockOverlay
import com.example.ui.components.GameOverDialog
import com.example.ui.components.PauseDialog
import com.example.ui.components.RestartConfirmDialog
import com.example.ui.components.ScoreHeader
import com.example.viewmodel.GameUiState
import com.example.viewmodel.GameViewModel

@Composable
fun GameScreen(
    viewModel: GameViewModel,
    uiState: GameUiState,
    onNavigateToSettings: () -> Unit,
    onNavigateToHome: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    // 72dp elevation to position block comfortably above the player's finger
    val fingerLiftPx = with(density) { 72.dp.toPx() }

    val draggedBlock = uiState.draggedSlotIndex?.let { uiState.trayBlocks.getOrNull(it) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testTag("game_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // 1. Top Section: Score Header & Controls
            ScoreHeader(
                score = uiState.score,
                bestScore = uiState.bestScore,
                isNewBest = uiState.isNewBestScore,
                comboCount = uiState.comboCount,
                comboBannerText = uiState.comboBannerText,
                onPauseClick = { viewModel.pauseGame() },
                onRestartClick = { viewModel.showRestartConfirm(true) },
                onSettingsClick = onNavigateToSettings
            )

            // 2. Main Section: 8x8 Centered Game Board
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                contentAlignment = Alignment.Center
            ) {
                BoardView(
                    board = uiState.board,
                    clearingCells = uiState.clearingCells,
                    hoverRow = uiState.hoverStartRow,
                    hoverCol = uiState.hoverStartCol,
                    draggedBlock = draggedBlock,
                    isHoverValid = uiState.isHoverValid,
                    hoverClearingCells = uiState.hoverClearingCells,
                    themeMode = uiState.themeMode,
                    onBoundsChanged = { bounds, cellSize ->
                        viewModel.boardBounds = bounds
                        viewModel.boardCellSizePx = cellSize
                    }
                )
            }

            // 3. Bottom Section: Available Blocks Tray
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BlockTrayView(
                    trayBlocks = uiState.trayBlocks,
                    draggedSlotIndex = uiState.draggedSlotIndex,
                    fingerLiftPx = fingerLiftPx,
                    onDragStart = { slotIndex, touchPos ->
                        viewModel.onDragStart(slotIndex, touchPos)
                    },
                    onDragMove = { touchPos, liftPx ->
                        viewModel.onDragMove(touchPos, liftPx)
                    },
                    onDragEnd = {
                        viewModel.onDragEnd()
                    }
                )

                Spacer(modifier = Modifier.height(2.dp))

                // Developer attribution footer (Mandatory requirement)
                Text(
                    text = "Created by Bhaskar",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.testTag("game_screen_developer_credit")
                )
            }
        }

        // 4. Dragged Block Floating Overlay (Rendered above board & tray)
        if (uiState.isDragging && draggedBlock != null) {
            DraggedBlockOverlay(
                block = draggedBlock,
                touchOffset = uiState.dragTouchOffset,
                fingerLiftPx = fingerLiftPx,
                boardCellSizePx = viewModel.boardCellSizePx
            )
        }

        // 5. Overlays and Dialogs
        if (uiState.currentScreen == GameScreenState.PAUSED) {
            PauseDialog(
                onResume = { viewModel.resumeGame() },
                onRestart = { viewModel.resetGame() },
                onHome = onNavigateToHome
            )
        }

        if (uiState.currentScreen == GameScreenState.GAME_OVER) {
            GameOverDialog(
                score = uiState.score,
                bestScore = uiState.bestScore,
                isNewBest = uiState.isNewBestScore,
                onPlayAgain = { viewModel.resetGame() },
                onHome = onNavigateToHome
            )
        }

        if (uiState.showRestartDialog) {
            RestartConfirmDialog(
                onConfirm = { viewModel.resetGame() },
                onDismiss = { viewModel.showRestartConfirm(false) }
            )
        }
    }
}
