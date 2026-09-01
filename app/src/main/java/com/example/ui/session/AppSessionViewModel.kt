package com.example.ui.session

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SupabaseConfig
import com.example.data.session.AccountCacheBoundary
import com.example.data.session.AppSession
import com.example.data.session.AppSessionRepository
import com.example.data.session.PartnerInvite
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SessionPhase {
    LOADING,
    SIGNED_OUT,
    READY,
    ERROR
}

data class AppSessionUiState(
    val phase: SessionPhase = SessionPhase.LOADING,
    val session: AppSession? = null,
    val activeInvite: PartnerInvite? = null,
    val actionInProgress: Boolean = false,
    val errorMessage: String? = null
)

class AppSessionViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppSessionRepository()
    private val cacheBoundary = AccountCacheBoundary.forApplication(application)

    private val _uiState = MutableStateFlow(AppSessionUiState())
    val uiState: StateFlow<AppSessionUiState> = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                phase = SessionPhase.LOADING,
                actionInProgress = true,
                errorMessage = null
            )

            runCatching {
                val authSession = SupabaseConfig.client.auth.currentSessionOrNull()
                    ?: return@runCatching null
                val userId = authSession.user?.id
                    ?: error("Authenticated Supabase session has no user id")
                cacheBoundary.ensureOwner(userId)
                repository.refresh().also { appSession ->
                    check(appSession.userId == userId) {
                        "App session does not belong to the authenticated user"
                    }
                }
            }.onSuccess { session ->
                _uiState.value = if (session == null) {
                    AppSessionUiState(phase = SessionPhase.SIGNED_OUT)
                } else {
                    AppSessionUiState(phase = SessionPhase.READY, session = session)
                }
            }.onFailure { error ->
                _uiState.value = AppSessionUiState(
                    phase = SessionPhase.ERROR,
                    errorMessage = error.message ?: "Session konnte nicht geladen werden"
                )
            }
        }
    }

    fun createPartnerInvite() {
        runReadyAction { current ->
            val invite = repository.createPartnerInvite()
            current.copy(activeInvite = invite)
        }
    }

    fun joinPartnerInvite(code: String) {
        runReadyAction { current ->
            val session = repository.joinPartnerInvite(code)
            current.copy(session = session, activeInvite = null)
        }
    }

    fun leaveCurrentCouple() {
        runReadyAction { current ->
            val session = repository.leaveCurrentCouple()
            current.copy(session = session, activeInvite = null)
        }
    }

    fun resetHarmony() {
        runReadyAction { current ->
            val userId = requireNotNull(current.session).userId
            val session = repository.resetHarmony()
            cacheBoundary.clearForReset(userId)
            current.copy(session = session, activeInvite = null)
        }
    }

    fun clearInvite() {
        _uiState.value = _uiState.value.copy(activeInvite = null)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(actionInProgress = true, errorMessage = null)
            runCatching { SupabaseConfig.client.auth.signOut() }
                .onSuccess { _uiState.value = AppSessionUiState(phase = SessionPhase.SIGNED_OUT) }
                .onFailure { error ->
                    _uiState.value = _uiState.value.copy(
                        actionInProgress = false,
                        errorMessage = error.message ?: "Abmelden fehlgeschlagen"
                    )
                }
        }
    }

    fun onAccountDeleted() {
        val userId = _uiState.value.session?.userId
        viewModelScope.launch {
            if (userId != null) {
                runCatching { cacheBoundary.clearForReset(userId) }
            }
            runCatching { SupabaseConfig.client.auth.signOut() }
            _uiState.value = AppSessionUiState(phase = SessionPhase.SIGNED_OUT)
        }
    }

    private fun runReadyAction(
        action: suspend (AppSessionUiState) -> AppSessionUiState
    ) {
        val current = _uiState.value
        if (current.phase != SessionPhase.READY || current.session == null || current.actionInProgress) return

        viewModelScope.launch {
            _uiState.value = current.copy(actionInProgress = true, errorMessage = null)
            runCatching { action(current) }
                .onSuccess { result ->
                    _uiState.value = result.copy(
                        phase = SessionPhase.READY,
                        actionInProgress = false,
                        errorMessage = null
                    )
                }
                .onFailure { error ->
                    _uiState.value = current.copy(
                        actionInProgress = false,
                        errorMessage = error.message ?: "Aktion fehlgeschlagen"
                    )
                }
        }
    }
}
