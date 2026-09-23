package com.example.model

/**
 * Coordinate offset (row, col) relative to top-left of the shape.
 */
data class CellOffset(val row: Int, val col: Int)

/**
 * Geometric shape definition with normalized coordinates and dimensions.
 */
data class BlockShape(
    val name: String,
    val cells: List<CellOffset>,
    val width: Int,
    val height: Int,
    val defaultColorIndex: Int
) {
    val cellCount: Int get() = cells.size

    companion object {
        // --- 1. Singles & Lines ---
        val DOT = BlockShape("DOT", listOf(CellOffset(0, 0)), 1, 1, 0)
        
        val H_LINE_2 = BlockShape("H2", listOf(CellOffset(0, 0), CellOffset(0, 1)), 2, 1, 1)
        val H_LINE_3 = BlockShape("H3", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2)), 3, 1, 1)
        val H_LINE_4 = BlockShape("H4", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(0, 3)), 4, 1, 6)
        val H_LINE_5 = BlockShape("H5", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(0, 3), CellOffset(0, 4)), 5, 1, 6)

        val V_LINE_2 = BlockShape("V2", listOf(CellOffset(0, 0), CellOffset(1, 0)), 1, 2, 1)
        val V_LINE_3 = BlockShape("V3", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0)), 1, 3, 1)
        val V_LINE_4 = BlockShape("V4", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(3, 0)), 1, 4, 6)
        val V_LINE_5 = BlockShape("V5", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(3, 0), CellOffset(4, 0)), 1, 5, 6)

        // --- 2. Squares ---
        val SQUARE_2X2 = BlockShape(
            "SQ2",
            listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 0), CellOffset(1, 1)),
            2, 2, 2
        )
        val SQUARE_3X3 = BlockShape(
            "SQ3",
            listOf(
                CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2),
                CellOffset(1, 0), CellOffset(1, 1), CellOffset(1, 2),
                CellOffset(2, 0), CellOffset(2, 1), CellOffset(2, 2)
            ),
            3, 3, 7
        )

        // --- 3. Small Corners (2x2, 3 cells) ---
        val CORNER_TL = BlockShape("CTL", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 0)), 2, 2, 5)
        val CORNER_TR = BlockShape("CTR", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 1)), 2, 2, 5)
        val CORNER_BL = BlockShape("CBL", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(1, 1)), 2, 2, 5)
        val CORNER_BR = BlockShape("CBR", listOf(CellOffset(0, 1), CellOffset(1, 0), CellOffset(1, 1)), 2, 2, 5)

        // --- 4. Large Corners (3x3, 5 cells) ---
        val BIG_CORNER_BL = BlockShape(
            "BC_BL",
            listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(2, 1), CellOffset(2, 2)),
            3, 3, 4
        )
        val BIG_CORNER_TL = BlockShape(
            "BC_TL",
            listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(1, 0), CellOffset(2, 0)),
            3, 3, 4
        )
        val BIG_CORNER_TR = BlockShape(
            "BC_TR",
            listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(1, 2), CellOffset(2, 2)),
            3, 3, 4
        )
        val BIG_CORNER_BR = BlockShape(
            "BC_BR",
            listOf(CellOffset(2, 0), CellOffset(2, 1), CellOffset(2, 2), CellOffset(0, 2), CellOffset(1, 2)),
            3, 3, 4
        )

        // --- 5. L-Shapes (3x2 or 2x3, 4 cells) ---
        val L_NORMAL = BlockShape("L_N", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(2, 1)), 2, 3, 3)
        val L_REV = BlockShape("L_R", listOf(CellOffset(0, 1), CellOffset(1, 1), CellOffset(2, 1), CellOffset(2, 0)), 2, 3, 3)
        val L_INV = BlockShape("L_I", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 0), CellOffset(2, 0)), 2, 3, 3)
        val L_INV_REV = BlockShape("L_IR", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 1), CellOffset(2, 1)), 2, 3, 3)
        val L_H1 = BlockShape("L_H1", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(1, 1), CellOffset(1, 2)), 3, 2, 3)
        val L_H2 = BlockShape("L_H2", listOf(CellOffset(0, 2), CellOffset(1, 0), CellOffset(1, 1), CellOffset(1, 2)), 3, 2, 3)
        val L_H3 = BlockShape("L_H3", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(1, 0)), 3, 2, 3)
        val L_H4 = BlockShape("L_H4", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(1, 2)), 3, 2, 3)

        // --- 6. T-Shapes (4 cells) ---
        val T_DOWN = BlockShape("T_D", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(0, 2), CellOffset(1, 1)), 3, 2, 8)
        val T_UP = BlockShape("T_U", listOf(CellOffset(1, 0), CellOffset(1, 1), CellOffset(1, 2), CellOffset(0, 1)), 3, 2, 8)
        val T_RIGHT = BlockShape("T_R", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(2, 0), CellOffset(1, 1)), 2, 3, 8)
        val T_LEFT = BlockShape("T_L", listOf(CellOffset(0, 1), CellOffset(1, 1), CellOffset(2, 1), CellOffset(1, 0)), 2, 3, 8)

        // --- 7. S and Z Shapes (4 cells) ---
        val S_HORIZ = BlockShape("S_H", listOf(CellOffset(1, 0), CellOffset(1, 1), CellOffset(0, 1), CellOffset(0, 2)), 3, 2, 9)
        val S_VERT = BlockShape("S_V", listOf(CellOffset(0, 0), CellOffset(1, 0), CellOffset(1, 1), CellOffset(2, 1)), 2, 3, 9)
        val Z_HORIZ = BlockShape("Z_H", listOf(CellOffset(0, 0), CellOffset(0, 1), CellOffset(1, 1), CellOffset(1, 2)), 3, 2, 2)
        val Z_VERT = BlockShape("Z_V", listOf(CellOffset(0, 1), CellOffset(1, 1), CellOffset(1, 0), CellOffset(2, 0)), 2, 3, 2)

        // --- 8. Plus / Cross (5 cells) ---
        val PLUS = BlockShape(
            "PLUS",
            listOf(CellOffset(0, 1), CellOffset(1, 0), CellOffset(1, 1), CellOffset(1, 2), CellOffset(2, 1)),
            3, 3, 4
        )

        /**
         * Comprehensive catalog of playable shapes.
         */
        val ALL_SHAPES: List<BlockShape> = listOf(
            DOT,
            H_LINE_2, H_LINE_3, H_LINE_4, H_LINE_5,
            V_LINE_2, V_LINE_3, V_LINE_4, V_LINE_5,
            SQUARE_2X2, SQUARE_3X3,
            CORNER_TL, CORNER_TR, CORNER_BL, CORNER_BR,
            BIG_CORNER_BL, BIG_CORNER_TL, BIG_CORNER_TR, BIG_CORNER_BR,
            L_NORMAL, L_REV, L_INV, L_INV_REV, L_H1, L_H2, L_H3, L_H4,
            T_DOWN, T_UP, T_RIGHT, T_LEFT,
            S_HORIZ, S_VERT, Z_HORIZ, Z_VERT,
            PLUS
        )
    }
}
