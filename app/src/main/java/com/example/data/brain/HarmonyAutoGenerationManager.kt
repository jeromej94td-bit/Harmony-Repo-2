package com.example.data.brain

import android.content.Context
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.data.brain.engine.HarmonyContextBuilder
import com.example.data.brain.gateway.HarmonyBrainGateway
import com.example.data.brain.repository.BrainRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HarmonyAutoGenerationManager(
    context: Context,
    private val scope: CoroutineScope,
    private val repository: BrainRepository,
    private val contextBuilder: HarmonyContextBuilder = HarmonyContextBuilder,
    private val gateway: HarmonyBrainGateway,
    private val onGenerated: (count: Int, dailyCount: Int, dailyLimit: Int) -> Unit = { _, _, _ -> }
) : LifecycleEventObserver {

    private val appContext = context.applicationContext
    private val prefs = appContext.getSharedPreferences(
        "harmony_auto_generation",
        Context.MODE_PRIVATE
    )

    private var minuteJob: Job? = null
    private var generating = false
    private var foreground = false

    companion object {
        private const val ACTIVE_MINUTE_MS = AutoGenerationPolicy.INTERVAL_MS
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> startForegroundGeneration()
            Lifecycle.Event.ON_STOP -> stopForegroundGeneration()
            else -> Unit
        }
    }

    fun startForegroundGeneration() {
        if (foreground) return
        foreground = true
        prefs.edit().putBoolean("enabled", true).apply()

        minuteJob?.cancel()
        minuteJob = scope.launch {
            resetDayIfNeeded()

            generateOneIfAllowed()

            // Nur bei sichtbarer/aktiver App höchstens eine weitere Generierung pro Minute.
            while (isActive && foreground) {
                delay(ACTIVE_MINUTE_MS)
                if (foreground) generateOneIfAllowed()
            }
        }
    }

    fun stopForegroundGeneration() {
        foreground = false
        minuteJob?.cancel()
        minuteJob = null
    }

    private suspend fun generateOneIfAllowed() {
        if (!foreground || generating) return
        resetDayIfNeeded()

        val count = prefs.getInt("generated_today", 0)
        if (!AutoGenerationPolicy.canGenerate(count)) return

        generating = true
        try {
            val context = repository.buildBrainContext(task = "questions")
            val result = gateway.generateQuestions(
                query = "Erzeuge fünf neue Paarfragen. Nutze neue Blickwinkel, keine Wiederholung vorhandener Interessen oder Antworten.",
                context = context
            )

            if (!result.ok || result.questions.isEmpty()) return

            // Diese Methode muss nur wirklich neue, nicht doppelte Fragen speichern.
            val insertedCount = repository.storeGeneratedQuestions(
                questions = result.questions,
                category = "Harmony Brain"
            )

            if (insertedCount > 0) {
                val newCount = (count + 1).coerceAtMost(AutoGenerationPolicy.DAILY_LIMIT)
                prefs.edit()
                    .putInt("generated_today", newCount)
                    .putLong("last_generation_at", System.currentTimeMillis())
                    .apply()

                onGenerated(insertedCount, newCount, AutoGenerationPolicy.DAILY_LIMIT)
                HarmonyGenerationNotifier.notifyNewGameCreated(appContext, insertedCount)
            }
        } catch (_: Throwable) {
            // Error safety: failures do not increment daily limit
        } finally {
            generating = false
        }
    }

    private fun resetDayIfNeeded() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val storedDay = prefs.getString("day_key", null)
        if (storedDay != today) {
            prefs.edit()
                .putString("day_key", today)
                .putInt("generated_today", 0)
                .apply()
        }
    }

    fun getState(): AutoGenerationState {
        resetDayIfNeeded()
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        return AutoGenerationState(
            enabled = prefs.getBoolean("enabled", true),
            generatedToday = prefs.getInt("generated_today", 0),
            dailyLimit = AutoGenerationPolicy.DAILY_LIMIT,
            startupBatchRemaining = 0,
            isGenerating = generating,
            lastGenerationAt = if (prefs.contains("last_generation_at")) prefs.getLong("last_generation_at", 0L) else null,
            dayKey = today
        )
    }
}
