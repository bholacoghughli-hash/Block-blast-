package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.ThemeMode

/**
 * Local persistent storage for high scores and user preferences using SharedPreferences.
 * Works 100% offline without external servers.
 */
class GameStorage(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "block_blast_prefs"
        private const val KEY_BEST_SCORE = "key_best_score"
        private const val KEY_SOUND_ENABLED = "key_sound_enabled"
        private const val KEY_MUSIC_ENABLED = "key_music_enabled"
        private const val KEY_VIBRATION_ENABLED = "key_vibration_enabled"
        private const val KEY_THEME_MODE = "key_theme_mode"
    }

    fun getBestScore(): Int = prefs.getInt(KEY_BEST_SCORE, 0)

    /**
     * Saves the score if it is higher than the previous best.
     * Returns true if a new high score was set.
     */
    fun checkAndSaveBestScore(score: Int): Boolean {
        val currentBest = getBestScore()
        if (score > currentBest) {
            prefs.edit().putInt(KEY_BEST_SCORE, score).apply()
            return true
        }
        return false
    }

    fun resetBestScore() {
        prefs.edit().putInt(KEY_BEST_SCORE, 0).apply()
    }

    fun isSoundEnabled(): Boolean = prefs.getBoolean(KEY_SOUND_ENABLED, true)

    fun setSoundEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_SOUND_ENABLED, enabled).apply()
    }

    fun isMusicEnabled(): Boolean = prefs.getBoolean(KEY_MUSIC_ENABLED, true)

    fun setMusicEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_MUSIC_ENABLED, enabled).apply()
    }

    fun isVibrationEnabled(): Boolean = prefs.getBoolean(KEY_VIBRATION_ENABLED, true)

    fun setVibrationEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_VIBRATION_ENABLED, enabled).apply()
    }

    fun getThemeMode(): ThemeMode {
        val name = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name) ?: ThemeMode.SYSTEM.name
        return try {
            ThemeMode.valueOf(name)
        } catch (_: Exception) {
            ThemeMode.SYSTEM
        }
    }

    fun setThemeMode(mode: ThemeMode) {
        prefs.edit().putString(KEY_THEME_MODE, mode.name).apply()
    }
}
