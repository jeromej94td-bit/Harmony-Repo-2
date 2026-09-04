package com.example.data.brain

import android.content.SharedPreferences
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

class ForegroundGameGenerator(
    private val scope: CoroutineScope,
    private val generateOne: suspend () -> Boolean,
    private val onCreated: suspend () -> Unit,
    private val prefs: SharedPreferences
) : DefaultLifecycleObserver {
    private var job: Job? = null

    override fun onStart(owner: LifecycleOwner) {
        if (job?.isActive == true) return
        job = scope.launch {
            resetDayIfNeeded()

            // One visible result can be created as soon as the app becomes active.
            // After that, generation is strictly throttled to one attempt per minute.
            createIfAllowed()
            while (isActive) {
                delay(AutoGenerationPolicy.INTERVAL_MS)
                createIfAllowed()
            }
        }
    }

    override fun onStop(owner: LifecycleOwner) {
        job?.cancel()
        job = null
    }

    private suspend fun createIfAllowed() {
        resetDayIfNeeded()
        val count = prefs.getInt("generated_today", 0)
        if (!AutoGenerationPolicy.canGenerate(count)) return

        if (generateOne()) {
            prefs.edit()
                .putInt("generated_today", (count + 1).coerceAtMost(AutoGenerationPolicy.DAILY_LIMIT))
                .putLong("last_generation_at", System.currentTimeMillis())
                .apply()
            onCreated()
        }
    }

    private fun resetDayIfNeeded() {
        val today = LocalDate.now().toString()
        if (prefs.getString("day_key", null) == today) return

        prefs.edit()
            .putString("day_key", today)
            .putInt("generated_today", 0)
            .remove("game_day")
            .remove("game_count")
            .remove("startup_batch_done")
            .apply()
    }
}
