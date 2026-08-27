package com.example.util

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit

object GeminiAudioTranscriber {
    private const val TAG = "GeminiAudioTranscriber"
    private const val MODEL_NAME = "gemini-3.5-flash"

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(45, TimeUnit.SECONDS)
        .readTimeout(45, TimeUnit.SECONDS)
        .writeTimeout(45, TimeUnit.SECONDS)
        .build()

    private val mediaTypeJson = "application/json; charset=utf-8".toMediaType()

    /**
     * Transcribes an audio file using the Gemini 3.5 Flash model.
     */
    suspend fun transcribeAudioFile(
        audioFile: File,
        appLanguage: String = "de"
    ): Result<String> = withContext(Dispatchers.IO) {
        runCatching {
            if (!audioFile.exists() || audioFile.length() == 0L) {
                throw IllegalArgumentException("Audio-Datei existiert nicht oder ist leer.")
            }

            val apiKey = GeminiImageService.resolveGeminiApiKey()
            if (apiKey.isBlank()) {
                throw IllegalStateException("Kein Gemini API-Schlüssel konfiguriert.")
            }

            val bytes = audioFile.readBytes()
            val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)

            val mimeType = when {
                audioFile.name.endsWith(".m4a", ignoreCase = true) -> "audio/mp4"
                audioFile.name.endsWith(".mp4", ignoreCase = true) -> "audio/mp4"
                audioFile.name.endsWith(".aac", ignoreCase = true) -> "audio/aac"
                audioFile.name.endsWith(".wav", ignoreCase = true) -> "audio/wav"
                audioFile.name.endsWith(".3gp", ignoreCase = true) -> "audio/3gpp"
                else -> "audio/mp4"
            }

            val instruction = if (appLanguage == "en") {
                "Accurately transcribe the provided audio in English. Output only the transcribed text with proper punctuation, without quotation marks, markdown wrappers, or additional commentary."
            } else {
                "Transkribiere diese Audionachricht präzise auf Deutsch. Gib ausschließlich den transkribierten Text mit korrekter Zeichensetzung zurück, ohne Anführungszeichen, Markdown-Codeblöcke oder zusätzliche Kommentare."
            }

            val requestJson = JSONObject().apply {
                val contentsArray = JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", instruction)
                            })
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", mimeType)
                                    put("data", base64Data)
                                })
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.2)
                    put("maxOutputTokens", 1024)
                })
            }

            val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
            val request = Request.Builder()
                .url(url)
                .post(requestJson.toString().toRequestBody(mediaTypeJson))
                .build()

            httpClient.newCall(request).execute().use { response ->
                val bodyString = response.body?.string().orEmpty()
                if (!response.isSuccessful) {
                    Log.e(TAG, "Gemini Transcription API failed (${response.code}): $bodyString")
                    throw IllegalStateException("API-Fehler ${response.code}: $bodyString")
                }

                val resJson = JSONObject(bodyString)
                val candidates = resJson.optJSONArray("candidates")
                if (candidates == null || candidates.length() == 0) {
                    throw IllegalStateException("Keine Transkriptions-Ergebnisse von Gemini erhalten.")
                }

                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val transcription = parts?.optJSONObject(0)?.optString("text")?.trim() ?: ""

                if (transcription.isBlank()) {
                    throw IllegalStateException("Leeres Transkript erhalten.")
                }

                Log.d(TAG, "Transcription success: $transcription")
                transcription
            }
        }
    }

    /**
     * Provides a Flow for live on-device speech recognition via Android SpeechRecognizer.
     */
    fun startLiveSpeechRecognition(
        context: Context,
        language: String = "de-DE"
    ): Flow<SpeechRecognitionResult> = callbackFlow {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            trySend(SpeechRecognitionResult.Error("Spracherkennung auf diesem Gerät nicht verfügbar."))
            close()
            return@callbackFlow
        }

        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, language)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }

        val listener = object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                trySend(SpeechRecognitionResult.Ready)
            }

            override fun onBeginningOfSpeech() {
                trySend(SpeechRecognitionResult.Started)
            }

            override fun onRmsChanged(rmsdB: Float) {
                trySend(SpeechRecognitionResult.RmsChanged(rmsdB))
            }

            override fun onBufferReceived(buffer: ByteArray?) {}

            override fun onEndOfSpeech() {
                trySend(SpeechRecognitionResult.EndOfSpeech)
            }

            override fun onError(error: Int) {
                val errorMsg = when (error) {
                    SpeechRecognizer.ERROR_NO_MATCH -> "Keine Sprache erkannt"
                    SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Keine Spracheingabe empfangen"
                    SpeechRecognizer.ERROR_AUDIO -> "Audio-Aufnahmefehler"
                    SpeechRecognizer.ERROR_NETWORK -> "Netzwerkfehler bei Spracherkennung"
                    else -> "Spracherkennungsfehler ($error)"
                }
                trySend(SpeechRecognitionResult.Error(errorMsg))
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                trySend(SpeechRecognitionResult.FinalResult(text))
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.firstOrNull() ?: ""
                if (text.isNotBlank()) {
                    trySend(SpeechRecognitionResult.PartialResult(text))
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        }

        speechRecognizer.setRecognitionListener(listener)
        speechRecognizer.startListening(intent)

        awaitClose {
            try {
                speechRecognizer.stopListening()
                speechRecognizer.destroy()
            } catch (_: Exception) {}
        }
    }
}

sealed class SpeechRecognitionResult {
    data object Ready : SpeechRecognitionResult()
    data object Started : SpeechRecognitionResult()
    data class RmsChanged(val rms: Float) : SpeechRecognitionResult()
    data object EndOfSpeech : SpeechRecognitionResult()
    data class PartialResult(val partialText: String) : SpeechRecognitionResult()
    data class FinalResult(val text: String) : SpeechRecognitionResult()
    data class Error(val message: String) : SpeechRecognitionResult()
}
