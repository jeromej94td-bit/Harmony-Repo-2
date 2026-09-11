package com.example.ui

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.example.data.db.HarmonyDatabase
import com.example.data.repository.deleteAnswerForSkip
import kotlinx.coroutines.launch

/**
 * Advances the current runner without inventing an answer. Keeping this separate from
 * nextStep() preserves skip semantics without any removed personalization side-channel.
 */
@Suppress("UNCHECKED_CAST")
fun HarmonyViewModel.skipCurrentQuestion() {
    val run = uiState.value.activeRun ?: return
    if (run.isFinished) return

    val total = if (run.pack.type == "tot") run.pack.pairs.size else run.pack.questions.size
    if (run.currentIndex !in 0 until total) return

    val packId = run.pack.id
    val questionIndex = run.currentIndex
    val app = getApplication<Application>()

    // A user can answer a question, navigate back to it and then decide to skip it.
    // Remove that stale answer from the active run before nextStep() copies the run;
    // otherwise the old value could reappear in results or be persisted again on finish.
    (run.currentAnswers as? MutableMap<Int, String>)?.remove(questionIndex)

    val db = HarmonyDatabase.getInstance(app)
    viewModelScope.launch {
        db.deleteAnswerForSkip(packId, questionIndex)
    }

    nextStep()
}
