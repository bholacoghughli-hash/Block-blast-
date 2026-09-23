package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Block
import com.example.model.Board
import com.example.model.ThemeMode
import com.example.ui.theme.BoardBackgroundDark
import com.example.ui.theme.BoardBackgroundLight
import com.example.ui.theme.CellEmptyBorderDark
import com.example.ui.theme.CellEmptyBorderLight
import com.example.ui.theme.CellEmptyDark
import com.example.ui.theme.CellEmptyLight
import com.example.ui.theme.getBlockPalette

@Composable
fun BoardView(
    board: Board,
    clearingCells: Set<Pair<Int, Int>>,
    hoverRow: Int?,
    hoverCol: Int?,
    draggedBlock: Block?,
    isHoverValid: Boolean,
    hoverClearingCells: Set<Pair<Int, Int>>,
    themeMode: ThemeMode,
    onBoundsChanged: (bounds: androidx.compose.ui.geometry.Rect, cellSize: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
    }

    val boardBg = if (isDark) BoardBackgroundDark else BoardBackgroundLight
    val cellBg = if (isDark) CellEmptyDark else CellEmptyLight
    val cellBorder = if (isDark) CellEmptyBorderDark else CellEmptyBorderLight

    // Pulse animation for clearing and previewing cells
    val infiniteTransition = rememberInfiniteTransition(label = "boardPulse")
    val previewPulse by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    // Precalculate target cells if dragging
    val targetCells = if (draggedBlock != null && hoverRow != null && hoverCol != null) {
        board.getTargetCells(draggedBlock, hoverRow, hoverCol)
    } else {
        emptySet()
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp)
            .aspectRatio(1f)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x66000000))
            .clip(RoundedCornerShape(20.dp))
            .background(boardBg)
            .border(2.dp, cellBorder.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(8.dp)
            .onGloballyPositioned { coordinates ->
                val bounds = coordinates.boundsInRoot()
                val cellSize = bounds.width / Board.GRID_SIZE
                onBoundsChanged(bounds, cellSize)
            }
            .testTag("game_board_canvas")
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val totalSize = size.width
            val cellSize = totalSize / Board.GRID_SIZE
            val cellGap = 2.5.dp.toPx()
            val innerCellSize = cellSize - (cellGap * 2)
            val cornerRadius = CornerRadius(innerCellSize * 0.22f)

            // Draw each of the 8x8 cells
            for (r in 0 until Board.GRID_SIZE) {
                for (c in 0 until Board.GRID_SIZE) {
                    val left = c * cellSize + cellGap
                    val top = r * cellSize + cellGap
                    val cellPos = Pair(r, c)
                    val cellColorIndex = board[r, c]
                    val isClearing = clearingCells.contains(cellPos)
                    val isHoveredTarget = targetCells.contains(cellPos)
                    val isHoverClearing = hoverClearingCells.contains(cellPos)

                    // 1. Draw base slot (empty grid background)
                    drawRoundRect(
                        color = cellBg,
                        topLeft = Offset(left, top),
                        size = Size(innerCellSize, innerCellSize),
                        cornerRadius = cornerRadius
                    )
                    drawRoundRect(
                        color = cellBorder,
                        topLeft = Offset(left, top),
                        size = Size(innerCellSize, innerCellSize),
                        cornerRadius = cornerRadius,
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // 2. Draw placed block cell
                    if (cellColorIndex != null && !isClearing) {
                        val palette = getBlockPalette(cellColorIndex)
                        drawBlockCell(
                            left = left,
                            top = top,
                            size = innerCellSize,
                            cornerRadius = cornerRadius,
                            mainColor = palette.main,
                            lightColor = palette.light,
                            darkColor = palette.dark
                        )
                    }

                    // 3. Draw clearing animation cell (burst glow)
                    if (isClearing) {
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(left, top),
                            size = Size(innerCellSize, innerCellSize),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = Color(0xFFFFD600),
                            topLeft = Offset(left - 2, top - 2),
                            size = Size(innerCellSize + 4, innerCellSize + 4),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }

                    // 4. Draw drag hover preview
                    if (isHoveredTarget && draggedBlock != null) {
                        if (isHoverValid) {
                            val palette = getBlockPalette(draggedBlock.colorIndex)
                            // Semi-transparent ghost preview
                            drawRoundRect(
                                color = palette.main.copy(alpha = previewPulse * 0.75f),
                                topLeft = Offset(left, top),
                                size = Size(innerCellSize, innerCellSize),
                                cornerRadius = cornerRadius
                            )
                            drawRoundRect(
                                color = palette.light,
                                topLeft = Offset(left, top),
                                size = Size(innerCellSize, innerCellSize),
                                cornerRadius = cornerRadius,
                                style = Stroke(width = 2.dp.toPx())
                            )
                        } else {
                            // Invalid preview indicator
                            drawRoundRect(
                                color = Color(0xFFFF5252).copy(alpha = 0.35f),
                                topLeft = Offset(left, top),
                                size = Size(innerCellSize, innerCellSize),
                                cornerRadius = cornerRadius
                            )
                        }
                    }

                    // 5. Draw hypothetical blast/clear row & column preview
                    if (isHoverClearing && isHoverValid) {
                        drawRoundRect(
                            color = Color(0xFFFFD600).copy(alpha = 0.35f),
                            topLeft = Offset(left, top),
                            size = Size(innerCellSize, innerCellSize),
                            cornerRadius = cornerRadius
                        )
                        drawRoundRect(
                            color = Color(0xFFFFEB3B),
                            topLeft = Offset(left, top),
                            size = Size(innerCellSize, innerCellSize),
                            cornerRadius = cornerRadius,
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }
        }
    }
}

/**
 * Helper to render an individual 3D glossy block cell with rounded corners,
 * light top/left highlight, and deep bottom/right shadow.
 */
private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawBlockCell(
    left: Float,
    top: Float,
    size: Float,
    cornerRadius: CornerRadius,
    mainColor: Color,
    lightColor: Color,
    darkColor: Color
) {
    // Base block face
    drawRoundRect(
        color = mainColor,
        topLeft = Offset(left, top),
        size = Size(size, size),
        cornerRadius = cornerRadius
    )

    // Inner bevel highlight (top edge)
    val bevel = size * 0.16f
    drawRoundRect(
        color = lightColor.copy(alpha = 0.7f),
        topLeft = Offset(left + bevel * 0.5f, top + bevel * 0.4f),
        size = Size(size - bevel, bevel),
        cornerRadius = CornerRadius(cornerRadius.x * 0.6f)
    )

    // Inner bevel shadow (bottom edge)
    drawRoundRect(
        color = darkColor.copy(alpha = 0.55f),
        topLeft = Offset(left + bevel * 0.5f, top + size - (bevel * 1.1f)),
        size = Size(size - bevel, bevel * 0.8f),
        cornerRadius = CornerRadius(cornerRadius.x * 0.6f)
    )

    // Crisp outer border
    drawRoundRect(
        color = darkColor.copy(alpha = 0.3f),
        topLeft = Offset(left, top),
        size = Size(size, size),
        cornerRadius = cornerRadius,
        style = Stroke(width = 1.2f)
    )
}
