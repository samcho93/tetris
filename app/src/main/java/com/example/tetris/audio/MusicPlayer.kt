package com.example.tetris.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.sin

class MusicPlayer {
    private var audioTrack: AudioTrack? = null
    private var isPlaying = false
    private val sampleRate = 22050

    // Simple original melody using pentatonic scale notes
    // Each pair: (frequency in Hz, duration in ms)
    private val melody = listOf(
        660.0 to 300, 528.0 to 150, 594.0 to 150, 495.0 to 300,
        440.0 to 150, 528.0 to 150, 396.0 to 300, 440.0 to 300,
        528.0 to 300, 594.0 to 150, 660.0 to 150, 528.0 to 300,
        495.0 to 150, 440.0 to 150, 396.0 to 300, 440.0 to 150,
        495.0 to 150, 528.0 to 300, 660.0 to 300,
        594.0 to 300, 528.0 to 300, 495.0 to 600,
        0.0 to 300, // rest
    )

    fun start() {
        if (isPlaying) return
        isPlaying = true

        Thread {
            try {
                val buffer = generateMelodyBuffer()
                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build()
                    )
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.setLoopPoints(0, buffer.size, -1) // infinite loop
                track.play()
                audioTrack = track
            } catch (_: Exception) {
                // Silently ignore audio errors
            }
        }.start()
    }

    fun pause() {
        try {
            audioTrack?.pause()
        } catch (_: Exception) {}
    }

    fun resume() {
        try {
            if (isPlaying) {
                audioTrack?.play()
            }
        } catch (_: Exception) {}
    }

    fun stop() {
        try {
            audioTrack?.stop()
        } catch (_: Exception) {}
        isPlaying = false
    }

    fun release() {
        stop()
        try {
            audioTrack?.release()
        } catch (_: Exception) {}
        audioTrack = null
    }

    private fun generateMelodyBuffer(): ShortArray {
        val totalSamples = melody.sumOf { (_, durationMs) ->
            (sampleRate * durationMs / 1000.0).toInt()
        }
        val buffer = ShortArray(totalSamples)
        var offset = 0
        val twoPi = 2.0 * Math.PI
        val volume = 0.25 // Quieter background music

        for ((freq, durationMs) in melody) {
            val numSamples = (sampleRate * durationMs / 1000.0).toInt()
            for (i in 0 until numSamples) {
                if (offset + i >= totalSamples) break
                val t = i.toDouble() / sampleRate

                val raw = if (freq > 0) {
                    // Square wave with slight attack/release envelope
                    val envelope = when {
                        i < numSamples * 0.05 -> i.toDouble() / (numSamples * 0.05) // attack
                        i > numSamples * 0.8 -> (numSamples - i).toDouble() / (numSamples * 0.2) // release
                        else -> 1.0
                    }
                    val wave = sin(twoPi * freq * t)
                    val square = if (wave >= 0) 0.6 else -0.6
                    val sine = wave * 0.4
                    (square + sine) * envelope
                } else {
                    0.0 // rest
                }

                val value = (raw * Short.MAX_VALUE * volume).toInt()
                buffer[offset + i] = value.coerceIn(
                    Short.MIN_VALUE.toInt(),
                    Short.MAX_VALUE.toInt()
                ).toShort()
            }
            offset += numSamples
        }
        return buffer
    }
}
