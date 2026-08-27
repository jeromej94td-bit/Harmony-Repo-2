package com.example.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
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
import java.io.IOException

data class AudioRecordingState(
    val isRecording: Boolean = false,
    val durationSeconds: Int = 0,
    val currentAmplitude: Float = 0f,
    val currentFilePath: String? = null,
    val error: String? = null
)

class AudioRecorderHelper(private val context: Context) {

    private val tag = "AudioRecorderHelper"
    private var mediaRecorder: MediaRecorder? = null
    private var activeRecordingFile: File? = null

    private val _recordingState = MutableStateFlow(AudioRecordingState())
    val recordingState: StateFlow<AudioRecordingState> = _recordingState.asStateFlow()

    private var amplitudeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Main + Job())

    fun startRecording(): Boolean {
        if (_recordingState.value.isRecording) return false

        try {
            val outputDir = File(context.cacheDir, "voice_notes").apply { mkdirs() }
            val outputFile = File(outputDir, "voice_${System.currentTimeMillis()}.m4a")
            activeRecordingFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(96000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            _recordingState.value = AudioRecordingState(
                isRecording = true,
                durationSeconds = 0,
                currentFilePath = outputFile.absolutePath
            )

            startMonitoring()
            return true
        } catch (e: Exception) {
            Log.e(tag, "Failed to start recording: ${e.message}", e)
            cleanup()
            _recordingState.value = AudioRecordingState(
                isRecording = false,
                error = e.localizedMessage ?: "Mikrofonaufnahme fehlgeschlagen"
            )
            return false
        }
    }

    private fun startMonitoring() {
        amplitudeJob?.cancel()
        amplitudeJob = scope.launch {
            var seconds = 0
            var tickCount = 0
            while (isActive && _recordingState.value.isRecording) {
                delay(100)
                tickCount++
                if (tickCount % 10 == 0) {
                    seconds++
                }

                val maxAmp = try {
                    mediaRecorder?.maxAmplitude ?: 0
                } catch (_: Exception) {
                    0
                }

                val normalizedAmp = (maxAmp / 32767f).coerceIn(0.05f, 1f)

                _recordingState.value = _recordingState.value.copy(
                    durationSeconds = seconds,
                    currentAmplitude = normalizedAmp
                )
            }
        }
    }

    /**
     * Stops recording and returns the path to the recorded audio file and its duration.
     */
    fun stopRecording(): Pair<File?, Int> {
        amplitudeJob?.cancel()
        val duration = _recordingState.value.durationSeconds
        val recordedFile = activeRecordingFile

        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error stopping recorder: ${e.message}", e)
        } finally {
            mediaRecorder = null
            _recordingState.value = AudioRecordingState(isRecording = false)
        }

        return Pair(recordedFile?.takeIf { it.exists() && it.length() > 0 }, duration)
    }

    fun cancelRecording() {
        amplitudeJob?.cancel()
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
        } catch (_: Exception) {
        } finally {
            mediaRecorder = null
            activeRecordingFile?.delete()
            activeRecordingFile = null
            _recordingState.value = AudioRecordingState(isRecording = false)
        }
    }

    private fun cleanup() {
        amplitudeJob?.cancel()
        try {
            mediaRecorder?.release()
        } catch (_: Exception) {
        }
        mediaRecorder = null
        activeRecordingFile?.delete()
        activeRecordingFile = null
    }
}
