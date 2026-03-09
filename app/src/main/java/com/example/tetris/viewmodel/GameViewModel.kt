package com.example.tetris.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tetris.audio.MusicPlayer
import com.example.tetris.audio.SoundEngine
import com.example.tetris.audio.SoundType
import com.example.tetris.game.GameAction
import com.example.tetris.game.GameEngine
import com.example.tetris.game.SpeedConfig
import com.example.tetris.model.GamePhase
import com.example.tetris.model.GameState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val engine = GameEngine()
    private val soundEngine = SoundEngine()
    private val musicPlayer = MusicPlayer()

    private val _gameState = MutableStateFlow(GameState())
    val gameState: StateFlow<GameState> = _gameState.asStateFlow()

    private var gameLoopJob: Job? = null
    private var autoPauseJob: Job? = null
    private var currentLevel: Int = 1
    private var tickCounter: Long = 0
    private var musicEnabled: Boolean = true

    fun onAction(action: GameAction) {
        when (action) {
            is GameAction.StartGame -> startGame(action.level)
            is GameAction.MoveLeft -> {
                if (_gameState.value.phase == GamePhase.PLAYING) {
                    if (engine.tryMoveLeft()) {
                        soundEngine.play(SoundType.MOVE)
                    }
                    resetAutoPauseTimer()
                    emitState()
                }
            }
            is GameAction.MoveRight -> {
                if (_gameState.value.phase == GamePhase.PLAYING) {
                    if (engine.tryMoveRight()) {
                        soundEngine.play(SoundType.MOVE)
                    }
                    resetAutoPauseTimer()
                    emitState()
                }
            }
            is GameAction.RotateCW -> {
                if (_gameState.value.phase == GamePhase.PLAYING) {
                    if (engine.tryRotateCW()) {
                        soundEngine.play(SoundType.ROTATE)
                    }
                    resetAutoPauseTimer()
                    emitState()
                }
            }
            is GameAction.SoftDrop -> {
                if (_gameState.value.phase == GamePhase.PLAYING) {
                    if (engine.tryMoveDown()) {
                        soundEngine.play(SoundType.MOVE)
                    }
                    resetAutoPauseTimer()
                    emitState()
                }
            }
            is GameAction.HardDrop -> {
                if (_gameState.value.phase == GamePhase.PLAYING) {
                    engine.hardDrop()
                    soundEngine.play(SoundType.DROP)
                    lockAndSpawn()
                    resetAutoPauseTimer()
                    emitState()
                }
            }
            is GameAction.Pause -> pauseGame()
            is GameAction.Resume -> resumeGame()
            is GameAction.Tick -> processTick()
            is GameAction.ToggleMusic -> toggleMusic()
        }
    }

    private fun startGame(level: Int) {
        gameLoopJob?.cancel()
        autoPauseJob?.cancel()
        musicPlayer.stop()

        if (level <= 0) {
            // Quit to menu
            _gameState.value = GameState()
            return
        }

        currentLevel = level
        engine.start()
        _gameState.value = buildState(GamePhase.PLAYING)
        startGameLoop()
        resetAutoPauseTimer()
        if (musicEnabled) {
            musicPlayer.start()
        }
    }

    private fun startGameLoop() {
        gameLoopJob?.cancel()
        gameLoopJob = viewModelScope.launch {
            val interval = SpeedConfig.tickIntervalMs(currentLevel)
            while (isActive) {
                delay(interval)
                if (_gameState.value.phase == GamePhase.PLAYING) {
                    processTick()
                }
            }
        }
    }

    private fun processTick() {
        if (_gameState.value.phase != GamePhase.PLAYING) return

        if (!engine.tryMoveDown()) {
            lockAndSpawn()
        }
        emitState()
    }

    private fun lockAndSpawn() {
        val result = engine.lockPiece()

        if (result.linesCleared > 0) {
            if (result.linesCleared >= 4) {
                soundEngine.play(SoundType.TETRIS_CLEAR)
            } else {
                soundEngine.play(SoundType.LINE_CLEAR)
            }
        } else {
            soundEngine.play(SoundType.DROP)
        }

        engine.spawnNextPiece()
    }

    private fun pauseGame() {
        gameLoopJob?.cancel()
        autoPauseJob?.cancel()
        musicPlayer.pause()
        _gameState.value = buildState(GamePhase.PAUSED)
    }

    private fun resumeGame() {
        _gameState.value = buildState(GamePhase.PLAYING)
        startGameLoop()
        resetAutoPauseTimer()
        if (musicEnabled) {
            musicPlayer.resume()
        }
    }

    private fun toggleMusic() {
        musicEnabled = !musicEnabled
        if (musicEnabled) {
            if (_gameState.value.phase == GamePhase.PLAYING) {
                musicPlayer.resume()
            }
        } else {
            musicPlayer.pause()
        }
        emitState()
    }

    private fun resetAutoPauseTimer() {
        autoPauseJob?.cancel()
        autoPauseJob = viewModelScope.launch {
            delay(AUTO_PAUSE_DELAY_MS)
            if (_gameState.value.phase == GamePhase.PLAYING) {
                pauseGame()
            }
        }
    }

    private fun emitState() {
        _gameState.value = buildState(_gameState.value.phase)
    }

    private fun buildState(phase: GamePhase): GameState {
        return GameState(
            currentPiece = engine.currentPiece,
            nextPiece = engine.nextType,
            ghostY = engine.calculateGhostY(),
            cameraTopRow = engine.cameraTopRow,
            score = engine.score,
            linesCleared = engine.linesCleared,
            level = currentLevel,
            phase = phase,
            boardSnapshot = engine.getVisibleBoardSnapshot(),
            hiddenRowsBelow = engine.getHiddenRowsBelow(),
            musicEnabled = musicEnabled,
            tick = ++tickCounter
        )
    }

    fun onPauseLifecycle() {
        if (_gameState.value.phase == GamePhase.PLAYING) {
            pauseGame()
        }
    }

    override fun onCleared() {
        super.onCleared()
        gameLoopJob?.cancel()
        autoPauseJob?.cancel()
        soundEngine.release()
        musicPlayer.release()
    }

    companion object {
        const val AUTO_PAUSE_DELAY_MS = 60_000L
    }
}
