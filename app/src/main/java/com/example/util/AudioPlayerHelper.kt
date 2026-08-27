package com.example.util

import android.media.MediaPlayer
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.File

data class AudioPlaybackState(
    val isPlaying: Boolean = false,
    val activeFilePath: String? = null,
    val currentPositionMs: Int = 0,
    val totalDurationMs: Int = 0,
    val progress: Float = 0f
)

object AudioPlayerHelper {
    private const val TAG = "AudioPlayerHelper"
    private var mediaPlayer: MediaPlayer? = null
    private var progressJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    private val _playbackState = MutableStateFlow(AudioPlaybackState())
    val playbackState: StateFlow<AudioPlaybackState> = _playbackState.asStateFlow()

    fun togglePlay(filePath: String, totalSecondsHint: Int = 0) {
        val current = _playbackState.value
        if (current.isPlaying && current.activeFilePath == filePath) {
            pause()
        } else {
            play(filePath, totalSecondsHint)
        }
    }

    fun play(filePath: String, totalSecondsHint: Int = 0) {
        val file = File(filePath)
        if (!file.exists()) {
            Log.w(TAG, "Audio file does not exist: $filePath")
            return
        }

        val current = _playbackState.value
        if (current.activeFilePath == filePath && mediaPlayer != null) {
            try {
                mediaPlayer?.start()
                _playbackState.value = current.copy(isPlaying = true)
                startProgressTracker()
                return
            } catch (e: Exception) {
                Log.e(TAG, "Failed to resume playback: ${e.message}")
            }
        }

        stop()

        try {
            val player = MediaPlayer().apply {
                setDataSource(filePath)
                prepare()
                start()
            }

            val dur = if (player.duration > 0) player.duration else (totalSecondsHint * 1000)

            mediaPlayer = player
            _playbackState.value = AudioPlaybackState(
                isPlaying = true,
                activeFilePath = filePath,
                currentPositionMs = 0,
                totalDurationMs = dur,
                progress = 0f
            )

            player.setOnCompletionListener {
                _playbackState.value = AudioPlaybackState(
                    isPlaying = false,
                    activeFilePath = filePath,
                    currentPositionMs = dur,
                    totalDurationMs = dur,
                    progress = 1f
                )
                progressJob?.cancel()
            }

            player.setOnErrorListener { _, what, extra ->
                Log.e(TAG, "MediaPlayer error: what=$what, extra=$extra")
                stop()
                true
            }

            startProgressTracker()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to play audio: ${e.message}", e)
            stop()
        }
    }

    fun pause() {
        try {
            mediaPlayer?.pause()
            progressJob?.cancel()
            _playbackState.value = _playbackState.value.copy(isPlaying = false)
        } catch (e: Exception) {
            Log.e(TAG, "Error pausing: ${e.message}")
            stop()
        }
    }

    fun seekTo(progress: Float) {
        try {
            val dur = _playbackState.value.totalDurationMs
            if (dur > 0 && mediaPlayer != null) {
                val targetMs = (progress * dur).toInt().coerceIn(0, dur)
                mediaPlayer?.seekTo(targetMs)
                _playbackState.value = _playbackState.value.copy(
                    currentPositionMs = targetMs,
                    progress = progress.coerceIn(0f, 1f)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error seeking: ${e.message}")
        }
    }

    fun stop() {
        progressJob?.cancel()
        try {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        } catch (_: Exception) {
        } finally {
            mediaPlayer = null
            _playbackState.value = AudioPlaybackState()
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive && _playbackState.value.isPlaying) {
                delay(100)
                val player = mediaPlayer ?: break
                try {
                    if (player.isPlaying) {
                        val currentPos = player.currentPosition
                        val totalDur = if (player.duration > 0) player.duration else _playbackState.value.totalDurationMs
                        val prog = if (totalDur > 0) (currentPos.toFloat() / totalDur).coerceIn(0f, 1f) else 0f

                        _playbackState.value = _playbackState.value.copy(
                            currentPositionMs = currentPos,
                            totalDurationMs = totalDur,
                            progress = prog
                        )
                    }
                } catch (_: Exception) {
                    break
                }
            }
        }
    }
}
