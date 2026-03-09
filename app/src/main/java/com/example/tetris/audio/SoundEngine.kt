package com.example.tetris.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import java.util.concurrent.Executors
import kotlin.math.pow
import kotlin.math.sin

class SoundEngine {
    private val sampleRate = 22050
    private val executor = Executors.newSingleThreadExecutor()

    private val soundBuffers: Map<SoundType, ShortArray> = SoundType.entries.associateWith {
        generateSound(it)
    }

    fun play(type: SoundType) {
        executor.execute {
            try {
                val buffer = soundBuffers[type] ?: return@execute
                val bufferSize = buffer.size * 2

                val track = AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        AudioFormat.Builder()
                            .setSampleRate(sampleRate)
                            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                            .build()
                    )
                    .setBufferSizeInBytes(bufferSize)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, buffer.size)
                track.setNotificationMarkerPosition(buffer.size)
                track.setPlaybackPositionUpdateListener(object :
                    AudioTrack.OnPlaybackPositionUpdateListener {
                    override fun onMarkerReached(t: AudioTrack) {
                        t.release()
                    }
                    override fun onPeriodicNotification(t: AudioTrack) {}
                })
                track.play()
            } catch (_: Exception) {
                // Silently ignore audio errors
            }
        }
    }

    private fun generateSound(type: SoundType): ShortArray {
        val numSamples = (sampleRate * type.durationMs / 1000.0).toInt()
        val samples = ShortArray(numSamples)
        val twoPi = 2.0 * Math.PI

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = i.toDouble() / numSamples
            val envelope = (1.0 - progress).pow(0.5)

            val freq = if (type.endFrequency > 0) {
                type.frequency + (type.endFrequency - type.frequency) * progress
            } else {
                type.frequency
            }

            val raw = when (type.waveform) {
                Waveform.SINE -> sin(twoPi * freq * t)
                Waveform.SQUARE -> if (sin(twoPi * freq * t) >= 0) 1.0 else -1.0
                Waveform.NOISE -> Math.random() * 2.0 - 1.0
            }

            val value = (raw * envelope * Short.MAX_VALUE * type.volume).toInt()
            samples[i] = value.coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
        return samples
    }

    fun release() {
        executor.shutdown()
    }
}
