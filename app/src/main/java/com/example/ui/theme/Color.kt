package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Default M3 theme colors
val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

// Game Background and Canvas
val GameBackgroundDark = Color(0xFF0C0F1D)
val GameSurfaceDark = Color(0xFF141933)
val BoardBackgroundDark = Color(0xFF181E3D)
val CellEmptyDark = Color(0xFF222B52)
val CellEmptyBorderDark = Color(0xFF2A3462)

val GameBackgroundLight = Color(0xFFF0F4F8)
val GameSurfaceLight = Color(0xFFFFFFFF)
val BoardBackgroundLight = Color(0xFFE1E7F0)
val CellEmptyLight = Color(0xFFCAD5E2)
val CellEmptyBorderLight = Color(0xFFB8C6D6)

// 10 Vibrant Block Colors with Light Highlight & Deep Shadow for 3D look
data class BlockColorPalette(
    val main: Color,
    val light: Color,
    val dark: Color
)

val BLOCK_PALETTES = listOf(
    // 0: Amber Gold
    BlockColorPalette(Color(0xFFFFB300), Color(0xFFFFD54F), Color(0xFFFF8F00)),
    // 1: Electric Cyan
    BlockColorPalette(Color(0xFF00E5FF), Color(0xFF84FFFF), Color(0xFF00B0FF)),
    // 2: Vivid Orange
    BlockColorPalette(Color(0xFFFF6D00), Color(0xFFFFAB40), Color(0xFFE65100)),
    // 3: Hot Magenta / Pink
    BlockColorPalette(Color(0xFFFF2A6D), Color(0xFFFF80AB), Color(0xFFC51162)),
    // 4: Royal Purple
    BlockColorPalette(Color(0xFF7C4DFF), Color(0xFFB388FF), Color(0xFF651FFF)),
    // 5: Emerald Green
    BlockColorPalette(Color(0xFF00E676), Color(0xFFB9F6CA), Color(0xFF00C853)),
    // 6: Electric Blue
    BlockColorPalette(Color(0xFF2979FF), Color(0xFF82B1FF), Color(0xFF2962FF)),
    // 7: Coral Red
    BlockColorPalette(Color(0xFFFF5252), Color(0xFFFF8A80), Color(0xFFD50000)),
    // 8: Lime Green
    BlockColorPalette(Color(0xFF76FF03), Color(0xFFCCFF90), Color(0xFF64DD17)),
    // 9: Bright Teal
    BlockColorPalette(Color(0xFF1DE9B6), Color(0xFFA7FFEB), Color(0xFF00BFA5))
)

fun getBlockPalette(colorIndex: Int): BlockColorPalette {
    val idx = (colorIndex % BLOCK_PALETTES.size + BLOCK_PALETTES.size) % BLOCK_PALETTES.size
    return BLOCK_PALETTES[idx]
}
