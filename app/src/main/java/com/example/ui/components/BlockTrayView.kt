package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.Block
import com.example.ui.theme.getBlockPalette

@Composable
fun BlockTrayView(
    trayBlocks: List<Block?>,
    draggedSlotIndex: Int?,
    fingerLiftPx: Float,
    onDragStart: (slotIndex: Int, initialTouchPos: Offset) -> Unit,
    onDragMove: (touchPos: Offset, fingerLiftPx: Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0..2) {
            val block = trayBlocks.getOrNull(i)
            val isBeingDragged = draggedSlotIndex == i

            TraySlot(
                slotIndex = i,
                block = block,
                isBeingDragged = isBeingDragged,
                fingerLiftPx = fingerLiftPx,
                onDragStart = { pos -> onDragStart(i, pos) },
                onDragMove = onDragMove,
                onDragEnd = onDragEnd,
                modifier = Modifier
                    .weight(1f)
                    .height(116.dp)
            )
        }
    }
}

@Composable
private fun TraySlot(
    slotIndex: Int,
    block: Block?,
    isBeingDragged: Boolean,
    fingerLiftPx: Float,
    onDragStart: (initialTouchPos: Offset) -> Unit,
    onDragMove: (touchPos: Offset, fingerLiftPx: Float) -> Unit,
    onDragEnd: () -> Unit,
    modifier: Modifier = Modifier
) {
    var slotCoordinates by remember { mutableStateOf<LayoutCoordinates?>(null) }
    var currentTouchPos by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                shape = RoundedCornerShape(16.dp)
            )
            .onGloballyPositioned { coords ->
                slotCoordinates = coords
            }
            .pointerInput(block) {
                if (block == null) return@pointerInput
                detectDragGestures(
                    onDragStart = { localOffset ->
                        val coords = slotCoordinates
                        if (coords != null && coords.isAttached) {
                            currentTouchPos = coords.localToRoot(localOffset)
                            onDragStart(currentTouchPos)
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        currentTouchPos += dragAmount
                        onDragMove(currentTouchPos, fingerLiftPx)
                    },
                    onDragEnd = {
                        onDragEnd()
                    },
                    onDragCancel = {
                        onDragEnd()
                    }
                )
            }
            .testTag("tray_slot_$slotIndex"),
        contentAlignment = Alignment.Center
    ) {
        if (block != null) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(if (isBeingDragged) 0.25f else 1f)
                    .padding(8.dp)
            ) {
                renderBlockCentered(block = block, canvasSize = size)
            }
        }
    }
}

/**
 * Renders a block scaled nicely to fit inside a designated container canvas.
 */
fun DrawScope.renderBlockCentered(block: Block, canvasSize: Size) {
    val maxGridDim = maxOf(block.width, block.height, 3)
    val maxCellW = (canvasSize.width - 12.dp.toPx()) / maxGridDim
    val maxCellH = (canvasSize.height - 12.dp.toPx()) / maxGridDim
    val cellSize = minOf(maxCellW, maxCellH).coerceAtMost(32.dp.toPx())

    val totalW = block.width * cellSize
    val totalH = block.height * cellSize
    val startX = (canvasSize.width - totalW) / 2f
    val startY = (canvasSize.height - totalH) / 2f

    val palette = getBlockPalette(block.colorIndex)
    val cellGap = 1.5.dp.toPx()
    val innerSize = cellSize - (cellGap * 2)
    val cornerRadius = CornerRadius(innerSize * 0.24f)

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

        // Highlight
        val bevel = innerSize * 0.18f
        drawRoundRect(
            color = palette.light.copy(alpha = 0.7f),
            topLeft = Offset(left + bevel * 0.4f, top + bevel * 0.3f),
            size = Size(innerSize - bevel, bevel),
            cornerRadius = CornerRadius(cornerRadius.x * 0.6f)
        )

        // Shadow
        drawRoundRect(
            color = palette.dark.copy(alpha = 0.5f),
            topLeft = Offset(left + bevel * 0.4f, top + innerSize - (bevel * 1.1f)),
            size = Size(innerSize - bevel, bevel * 0.8f),
            cornerRadius = CornerRadius(cornerRadius.x * 0.6f)
        )

        // Crisp border
        drawRoundRect(
            color = palette.dark.copy(alpha = 0.3f),
            topLeft = Offset(left, top),
            size = Size(innerSize, innerSize),
            cornerRadius = cornerRadius,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}
