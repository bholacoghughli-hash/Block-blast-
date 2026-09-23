package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.model.Block
import com.example.ui.theme.getBlockPalette

@Composable
fun DraggedBlockOverlay(
    block: Block?,
    touchOffset: Offset,
    fingerLiftPx: Float,
    boardCellSizePx: Float,
    modifier: Modifier = Modifier
) {
    if (block == null || boardCellSizePx <= 0f) return

    val cellSize = boardCellSizePx
    val blockWidthPx = block.width * cellSize
    val blockHeightPx = block.height * cellSize

    val elevatedY = touchOffset.y - fingerLiftPx
    val startX = touchOffset.x - (blockWidthPx / 2f)
    val startY = elevatedY - (blockHeightPx / 2f)

    val palette = getBlockPalette(block.colorIndex)

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellGap = 2.5.dp.toPx()
            val innerSize = cellSize - (cellGap * 2)
            val cornerRadius = CornerRadius(innerSize * 0.22f)

            // Draw elevation shadow beneath cells
            for (cell in block.cells) {
                val left = startX + (cell.col * cellSize) + cellGap
                val top = startY + (cell.row * cellSize) + cellGap

                drawRoundRect(
                    color = Color.Black.copy(alpha = 0.35f),
                    topLeft = Offset(left + 3.dp.toPx(), top + 8.dp.toPx()),
                    size = Size(innerSize, innerSize),
                    cornerRadius = cornerRadius
                )
            }

            // Draw block cells
            for (cell in block.cells) {
                val left = startX + (cell.col * cellSize) + cellGap
                val top = startY + (cell.row * cellSize) + cellGap

                // Base block
                drawRoundRect(
                    color = palette.main,
                    topLeft = Offset(left, top),
                    size = Size(innerSize, innerSize),
                    cornerRadius = cornerRadius
                )

                // Inner highlight
                val bevel = innerSize * 0.16f
                drawRoundRect(
                    color = palette.light.copy(alpha = 0.8f),
                    topLeft = Offset(left + bevel * 0.5f, top + bevel * 0.4f),
                    size = Size(innerSize - bevel, bevel),
                    cornerRadius = CornerRadius(cornerRadius.x * 0.6f)
                )

                // Inner shadow
                drawRoundRect(
                    color = palette.dark.copy(alpha = 0.6f),
                    topLeft = Offset(left + bevel * 0.5f, top + innerSize - (bevel * 1.1f)),
                    size = Size(innerSize - bevel, bevel * 0.8f),
                    cornerRadius = CornerRadius(cornerRadius.x * 0.6f)
                )

                // Crisp border
                drawRoundRect(
                    color = palette.dark.copy(alpha = 0.4f),
                    topLeft = Offset(left, top),
                    size = Size(innerSize, innerSize),
                    cornerRadius = cornerRadius,
                    style = Stroke(width = 1.2f)
                )
            }
        }
    }
}
