package com.example.ui.session

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SupabaseConfig
import com.example.data.session.AccountCacheBoundary
import com.example.data.session.AppSession
import com.example.data.session.AppSessionRepository
import com.example.data.session.PartnerInvite
import com.example.data.session.ProfileAvatarRepository
import com.example.data.session.UserProfile
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.functions.functions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SessionPhase {
    LOADING,
    SIGNED_OUT,
    READY,
    DEMO,
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
    private val avatarRepository = ProfileAvatarRepository(application.applicationContext)
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
                    errorMessage = sessionErrorCopy(error.message)
                )
            }
        }
    }

    fun enterDemo() {
        viewModelScope.launch {
            runCatching {
                cacheBoundary.ensureOwner(DEMO_USER_ID)
                AppSession(
                    userId = DEMO_USER_ID,
                    email = null,
                    profile = UserProfile(
                        userId = DEMO_USER_ID,
                        displayName = "Jerome",
                        avatarUrl = null
                    ),
                    coupleId = DEMO_COUPLE_ID,
                    partner = UserProfile(
                        userId = DEMO_PARTNER_ID,
                        displayName = "Alex",
                        avatarUrl = null
                    )
                )
            }.onSuccess { demoSession ->
                _uiState.value = AppSessionUiState(
                    phase = SessionPhase.DEMO,
                    session = demoSession
                )
            }.onFailure {
                _uiState.value = AppSessionUiState(
                    phase = SessionPhase.ERROR,
                    errorMessage = "Demo-Modus konnte nicht gestartet werden."
                )
            }
        }
    }

    fun updateProfileDisplayName(rawDisplayName: String) {
        val displayName = normalizeHarmonyDisplayName(rawDisplayName)
        if (displayName == null) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Bitte gib einen Namen mit maximal 60 Zeichen ein."
            )
            return
        }

        runReadyAction { current ->
            val session = repository.updateProfile(displayName)
            current.copy(session = session)
        }
    }

    fun updateProfileAvatar(uri: Uri) {
        runReadyAction { current ->
            val avatarRef = avatarRepository.uploadOwnAvatar(uri)
            val session = repository.updateAvatar(avatarRef)
            current.copy(session = session)
        }
    }

    fun refreshSilently() {
        val current = _uiState.value
        if (current.phase != SessionPhase.READY || current.session == null || current.actionInProgress) return

        viewModelScope.launch {
            runCatching { repository.refresh() }
                .onSuccess { session ->
                    val latest = _uiState.value
                    if (latest.phase != SessionPhase.READY || latest.actionInProgress) return@onSuccess
                    _uiState.value = latest.copy(
                        session = session,
                        activeInvite = if (session.isPaired) null else current.activeInvite,
                        errorMessage = null
                    )
                }
            // Polling is best-effort. A transient network error must not replace the invite screen.
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
        val current = _uiState.value
        if (current.phase == SessionPhase.DEMO) {
            _uiState.value = AppSessionUiState(phase = SessionPhase.SIGNED_OUT)
            return
        }

        viewModelScope.launch {
            _uiState.value = current.copy(actionInProgress = true, errorMessage = null)
            runCatching { SupabaseConfig.client.auth.signOut() }
                .onSuccess { _uiState.value = AppSessionUiState(phase = SessionPhase.SIGNED_OUT) }
                .onFailure {
                    _uiState.value = current.copy(
                        actionInProgress = false,
                        errorMessage = "Abmelden fehlgeschlagen. Bitte versuche es erneut."
                    )
                }
        }
    }

    fun deleteAccount() {
        val current = _uiState.value
        val userId = current.session?.userId ?: return
        if (current.phase != SessionPhase.READY || current.actionInProgress) return

        viewModelScope.launch {
            _uiState.value = current.copy(actionInProgress = true, errorMessage = null)
            runCatching {
                SupabaseConfig.client.functions.invoke("delete-account")
                cacheBoundary.clearForReset(userId)
                runCatching { SupabaseConfig.client.auth.signOut() }
            }.onSuccess {
                _uiState.value = AppSessionUiState(phase = SessionPhase.SIGNED_OUT)
            }.onFailure { error ->
                _uiState.value = current.copy(
                    actionInProgress = false,
                    errorMessage = sessionErrorCopy(error.message)
                )
            }
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
                        errorMessage = sessionErrorCopy(error.message)
                    )
                }
        }
    }

    private companion object {
        const val DEMO_USER_ID = "local-demo-session"
        const val DEMO_PARTNER_ID = "local-demo-partner"
        const val DEMO_COUPLE_ID = "local-demo-couple"
    }
}
