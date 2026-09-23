package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.sin

/**
 * Procedural audio synthesizer using Android AudioTrack PCM.
 * Generates custom, pleasing sound effects entirely on the device with zero external files,
 * ensuring 100% offline reliability and no copyright liabilities.
 */
class SoundManager {
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    var isSoundEnabled: Boolean = true

    companion object {
        private const val TAG = "SoundManager"
        private const val SAMPLE_RATE = 22050
    }

    /**
     * UI button click: short crisp tap.
     */
    fun playClick() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 700.0, durationMs = 30, decay = true)
        }
    }

    /**
     * Block picked up from tray: rising pleasant blip.
     */
    fun playPickUp() {
        if (!isSoundEnabled) return
        scope.launch {
            generateChirp(startFreq = 380.0, endFreq = 540.0, durationMs = 60)
        }
    }

    /**
     * Block legally placed on board: tactile soft pop.
     */
    fun playPlace() {
        if (!isSoundEnabled) return
        scope.launch {
            generateChirp(startFreq = 260.0, endFreq = 180.0, durationMs = 80)
        }
    }

    /**
     * Invalid placement attempt: subtle low double tone.
     */
    fun playInvalid() {
        if (!isSoundEnabled) return
        scope.launch {
            generateTone(frequency = 180.0, durationMs = 60, decay = true)
            kotlinx.coroutines.delay(80)
            generateTone(frequency = 140.0, durationMs = 60, decay = true)
        }
    }

    /**
     * Line cleared: cheerful ascending arpeggio (C5 - E5 - G5 - C6).
     */
    fun playLineClear(linesCount: Int = 1) {
        if (!isSoundEnabled) return
        scope.launch {
            val baseFreq = 523.25 // C5
            val notes = when (linesCount) {
                1 -> listOf(baseFreq, baseFreq * 1.25, baseFreq * 1.5) // C, E, G
                2 -> listOf(baseFreq, baseFreq * 1.25, baseFreq * 1.5, baseFreq * 2.0) // C, E, G, C6
                else -> listOf(baseFreq * 0.75, baseFreq, baseFreq * 1.25, baseFreq * 1.5, baseFreq * 2.0)
            }
            for (freq in notes) {
                generateTone(frequency = freq, durationMs = 70, decay = true)
                kotlinx.coroutines.delay(45)
            }
        }
    }

    /**
     * Combo celebration: energetic ascending sweep with harmonic sparkle.
     */
    fun playCombo(comboCount: Int) {
        if (!isSoundEnabled) return
        scope.launch {
            val multiplier = (1.0 + (comboCount.coerceAtMost(6) * 0.12))
            val f1 = 587.33 * multiplier // D5 base
            val f2 = 739.99 * multiplier // F#5
            val f3 = 880.00 * multiplier // A5
            val f4 = 1174.66 * multiplier // D6

            generateTone(frequency = f1, durationMs = 60, decay = true)
            kotlinx.coroutines.delay(50)
            generateTone(frequency = f2, durationMs = 60, decay = true)
            kotlinx.coroutines.delay(50)
            generateTone(frequency = f3, durationMs = 70, decay = true)
            kotlinx.coroutines.delay(50)
            generateTone(frequency = f4, durationMs = 120, decay = true)
        }
    }

    /**
     * New high score fanfare: triumphant melody.
     */
    fun playNewBest() {
        if (!isSoundEnabled) return
        scope.launch {
            val fanfare = listOf(
                523.25 to 100L, // C5
                659.25 to 100L, // E5
                783.99 to 100L, // G5
                1046.50 to 250L // C6
            )
            for ((freq, dur) in fanfare) {
                generateTone(frequency = freq, durationMs = dur.toInt(), decay = true)
                kotlinx.coroutines.delay(dur + 15)
            }
        }
    }

    /**
     * Game over: descending mellow triad.
     */
    fun playGameOver() {
        if (!isSoundEnabled) return
        scope.launch {
            val notes = listOf(440.0, 392.0, 329.63, 261.63)
            for (freq in notes) {
                generateTone(frequency = freq, durationMs = 110, decay = true)
                kotlinx.coroutines.delay(95)
            }
        }
    }

    private fun generateTone(frequency: Double, durationMs: Int, decay: Boolean = true) {
        try {
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val samples = ShortArray(numSamples)
            val angleStep = 2.0 * PI * frequency / SAMPLE_RATE

            for (i in 0 until numSamples) {
                val envelope = if (decay) {
                    val progress = i.toDouble() / numSamples
                    // Attack-decay envelope: rapid 10% attack, smooth 90% exponential decay
                    if (progress < 0.1) progress / 0.1 else (1.0 - progress) * (1.0 - progress)
                } else 1.0

                val sampleVal = (sin(i * angleStep) * 32767 * 0.45 * envelope).toInt()
                samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            playPcm(samples)
        } catch (e: Exception) {
            Log.w(TAG, "Audio synthesis error", e)
        }
    }

    private fun generateChirp(startFreq: Double, endFreq: Double, durationMs: Int) {
        try {
            val numSamples = (SAMPLE_RATE * (durationMs / 1000.0)).toInt().coerceAtLeast(1)
            val samples = ShortArray(numSamples)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / numSamples
                val currentFreq = startFreq + (endFreq - startFreq) * t
                val phase = 2.0 * PI * currentFreq * (i.toDouble() / SAMPLE_RATE)
                val envelope = (1.0 - t) * (1.0 - t)
                val sampleVal = (sin(phase) * 32767 * 0.45 * envelope).toInt()
                samples[i] = sampleVal.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            playPcm(samples)
        } catch (e: Exception) {
            Log.w(TAG, "Audio chirp synthesis error", e)
        }
    }

    private fun playPcm(samples: ShortArray) {
        var track: AudioTrack? = null
        try {
            val bufferSize = samples.size * 2
            track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(SAMPLE_RATE)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(samples, 0, samples.size)
            track.play()
            // Wait for track to finish playing then release
            val durationMs = (samples.size.toDouble() / SAMPLE_RATE * 1000).toLong()
            Thread.sleep(durationMs.coerceAtLeast(20))
        } catch (e: Exception) {
            Log.w(TAG, "Audio playback error", e)
        } finally {
            try {
                track?.stop()
                track?.release()
            } catch (_: Exception) {}
        }
    }
}
