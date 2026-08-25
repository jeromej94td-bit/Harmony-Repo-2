package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.CoachLocation
import com.example.ai.CoachMessage
import com.example.ai.CoachRole
import com.example.ai.HarmonyAiCoachRepository
import com.example.ai.HarmonyCoachUiState
import com.example.data.db.HarmonyDatabase
import com.example.data.repository.HarmonyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicLong

class HarmonyCoachViewModel(application: Application) : AndroidViewModel(application) {
    private val harmonyRepository = HarmonyRepository(HarmonyDatabase.getInstance(application), application)
    private val coachRepository = HarmonyAiCoachRepository()
    private val ids = AtomicLong(System.currentTimeMillis())

    private val _uiState = MutableStateFlow(HarmonyCoachUiState())
    val uiState: StateFlow<HarmonyCoachUiState> = _uiState.asStateFlow()

    fun ask(query: String, languageCode: String, location: CoachLocation?) {
        val clean = query.trim()
        if (clean.isEmpty() || _uiState.value.isLoading) return

        val previous = _uiState.value.messages
        val userMessage = CoachMessage(
            id = ids.incrementAndGet(),
            role = CoachRole.USER,
            text = clean
        )
        _uiState.value = _uiState.value.copy(
            messages = previous + userMessage,
            isLoading = true,
            errorMessage = null
        )

        viewModelScope.launch {
            runCatching {
                val profile = harmonyRepository.profileFlow.first()
                val answers = harmonyRepository.answersFlow.first()
                coachRepository.ask(
                    query = clean,
                    languageCode = languageCode,
                    profile = profile,
                    answers = answers,
                    history = previous,
                    location = location
                )
            }.onSuccess { response ->
                val assistant = CoachMessage(
                    id = ids.incrementAndGet(),
                    role = CoachRole.ASSISTANT,
                    text = response.text,
                    sources = response.sources,
                    groundedBySearch = response.groundedBySearch,
                    groundedByMaps = response.groundedByMaps
                )
                _uiState.value = _uiState.value.copy(
                    messages = _uiState.value.messages + assistant,
                    isLoading = false,
                    errorMessage = null
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = friendlyError(error, languageCode)
                )
            }
        }
    }

    private fun friendlyError(error: Throwable, languageCode: String): String {
        val raw = error.message.orEmpty()
        if (raw.contains("GEMINI_API_KEY", ignoreCase = true)) return raw
        return when (languageCode.lowercase()) {
            "de" -> "Harmony konnte gerade keine sichere Antwort laden. Prüfe Internet/API-Key und versuche es erneut."
            "it" -> "Harmony non è riuscita a caricare una risposta affidabile. Controlla connessione/API key e riprova."
            "pl" -> "Harmony nie mogła teraz pobrać wiarygodnej odpowiedzi. Sprawdź internet/klucz API i spróbuj ponownie."
            "es" -> "Harmony no pudo cargar una respuesta fiable. Revisa Internet/la clave API e inténtalo de nuevo."
            "fr" -> "Harmony n’a pas pu charger une réponse fiable. Vérifie Internet/la clé API puis réessaie."
            else -> "Harmony could not load a reliable answer right now. Check your internet/API key and try again."
        }
    }
}
