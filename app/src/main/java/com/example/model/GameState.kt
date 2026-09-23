package com.example.model

/**
 * Screen modes and high-level game states.
 */
enum class GameScreenState {
    HOME,
    PLAYING,
    PAUSED,
    GAME_OVER,
    SETTINGS,
    HOW_TO_PLAY,
    ABOUT
}

/**
 * Theme mode preference.
 */
enum class ThemeMode {
    SYSTEM,
    DARK,
    LIGHT
}
