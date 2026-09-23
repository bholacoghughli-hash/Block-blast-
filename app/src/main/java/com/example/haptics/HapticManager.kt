package com.example.haptics

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

/**
 * Manages tactile vibration and haptic feedback according to user preferences.
 */
class HapticManager(context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    var isVibrationEnabled: Boolean = true

    /**
     * Light crisp tick when a block is placed on the board.
     */
    fun onBlockPlaced() {
        if (!isVibrationEnabled || vibrator?.hasVibrator() != true) return
        vibrate(durationMs = 25, amplitude = 120)
    }

    /**
     * Satisfying resonant pulse when rows or columns are cleared.
     */
    fun onLineCleared(linesCount: Int = 1) {
        if (!isVibrationEnabled || vibrator?.hasVibrator() != true) return
        val duration = (35 + (linesCount * 15)).toLong().coerceAtMost(90)
        vibrate(durationMs = duration, amplitude = 220)
    }

    /**
     * Punchy double-burst haptic for combos.
     */
    fun onCombo() {
        if (!isVibrationEnabled || vibrator?.hasVibrator() != true) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 40, 45)
            val amplitudes = intArrayOf(0, 180, 0, 240)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(50)
        }
    }

    /**
     * Subtle gentle buzz for invalid drag release.
     */
    fun onInvalid() {
        if (!isVibrationEnabled || vibrator?.hasVibrator() != true) return
        vibrate(durationMs = 18, amplitude = 70)
    }

    /**
     * Celebratory rhythm for new high score.
     */
    fun onHighScore() {
        if (!isVibrationEnabled || vibrator?.hasVibrator() != true) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 50, 40, 50, 60)
            val amplitudes = intArrayOf(0, 160, 0, 200, 0, 255)
            vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 30, 50, 40, 50, 60), -1)
        }
    }

    private fun vibrate(durationMs: Long, amplitude: Int) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val clampedAmp = amplitude.coerceIn(1, 255)
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, clampedAmp))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {}
    }
}
