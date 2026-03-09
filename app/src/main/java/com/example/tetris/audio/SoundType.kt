package com.example.tetris.audio

enum class Waveform { SINE, SQUARE, NOISE }

enum class SoundType(
    val frequency: Double,
    val endFrequency: Double = 0.0,
    val durationMs: Int,
    val waveform: Waveform = Waveform.SQUARE,
    val volume: Double = 0.7
) {
    MOVE(220.0, durationMs = 30),
    ROTATE(330.0, durationMs = 50),
    DROP(110.0, durationMs = 80, waveform = Waveform.NOISE),
    LINE_CLEAR(440.0, endFrequency = 880.0, durationMs = 200),
    TETRIS_CLEAR(660.0, endFrequency = 1320.0, durationMs = 400, volume = 0.9);
}
