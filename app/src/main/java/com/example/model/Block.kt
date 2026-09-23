package com.example.model

import java.util.UUID
import kotlin.random.Random

/**
 * An individual playable block instance in the tray or currently being dragged.
 */
data class Block(
    val id: String = UUID.randomUUID().toString(),
    val shape: BlockShape,
    val colorIndex: Int = shape.defaultColorIndex
) {
    val cells: List<CellOffset> get() = shape.cells
    val width: Int get() = shape.width
    val height: Int get() = shape.height
    val cellCount: Int get() = shape.cellCount
}

/**
 * Balanced block generator that provides a varied, engaging selection of shapes.
 */
object BlockGenerator {
    private val random = Random(System.currentTimeMillis())

    // Easy/small shapes that help clear or fit into tight spaces
    private val SMALL_SHAPES = listOf(
        BlockShape.DOT,
        BlockShape.H_LINE_2, BlockShape.V_LINE_2,
        BlockShape.H_LINE_3, BlockShape.V_LINE_3,
        BlockShape.CORNER_TL, BlockShape.CORNER_TR, BlockShape.CORNER_BL, BlockShape.CORNER_BR,
        BlockShape.SQUARE_2X2
    )

    // Medium shapes (classic Tetrominoes, Ls, Ts)
    private val MEDIUM_SHAPES = listOf(
        BlockShape.H_LINE_4, BlockShape.V_LINE_4,
        BlockShape.L_NORMAL, BlockShape.L_REV, BlockShape.L_INV, BlockShape.L_INV_REV,
        BlockShape.L_H1, BlockShape.L_H2, BlockShape.L_H3, BlockShape.L_H4,
        BlockShape.T_DOWN, BlockShape.T_UP, BlockShape.T_RIGHT, BlockShape.T_LEFT,
        BlockShape.S_HORIZ, BlockShape.S_VERT, BlockShape.Z_HORIZ, BlockShape.Z_VERT
    )

    // Large/challenging shapes
    private val LARGE_SHAPES = listOf(
        BlockShape.H_LINE_5, BlockShape.V_LINE_5,
        BlockShape.SQUARE_3X3,
        BlockShape.BIG_CORNER_BL, BlockShape.BIG_CORNER_TL, BlockShape.BIG_CORNER_TR, BlockShape.BIG_CORNER_BR,
        BlockShape.PLUS
    )

    /**
     * Generates a balanced set of 3 blocks for the player tray.
     * Ensures at least one accessible shape per set so player isn't stuck with 3 giant shapes at once.
     */
    fun generateBlockSet(): List<Block> {
        val result = mutableListOf<Block>()

        // 1. Guaranteed small/medium playable shape
        val firstShape = if (random.nextFloat() < 0.65f) {
            SMALL_SHAPES.random(random)
        } else {
            MEDIUM_SHAPES.random(random)
        }
        result.add(createBlockWithColor(firstShape))

        // 2. Medium or variety shape
        val secondShape = if (random.nextFloat() < 0.40f) {
            SMALL_SHAPES.random(random)
        } else if (random.nextFloat() < 0.75f) {
            MEDIUM_SHAPES.random(random)
        } else {
            LARGE_SHAPES.random(random)
        }
        result.add(createBlockWithColor(secondShape))

        // 3. Third shape from full catalog
        val thirdShape = if (random.nextFloat() < 0.30f) {
            SMALL_SHAPES.random(random)
        } else if (random.nextFloat() < 0.70f) {
            MEDIUM_SHAPES.random(random)
        } else {
            LARGE_SHAPES.random(random)
        }
        result.add(createBlockWithColor(thirdShape))

        return result
    }

    private fun createBlockWithColor(shape: BlockShape): Block {
        // Pick a color index (0 to 9), varying slightly from shape default for rich visual diversity
        val color = if (random.nextFloat() < 0.4f) {
            random.nextInt(10)
        } else {
            shape.defaultColorIndex
        }
        return Block(shape = shape, colorIndex = color)
    }
}
