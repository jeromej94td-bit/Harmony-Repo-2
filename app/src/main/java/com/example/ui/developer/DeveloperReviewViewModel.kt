package com.example.ui.developer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.developer.DeveloperFeedbackDraft
import com.example.data.developer.DeveloperFeedbackItem
import com.example.data.developer.DeveloperFeedbackRepository
import com.example.data.developer.DeveloperFeedbackStatus
import com.example.data.developer.DeveloperReviewContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

 data class DeveloperReviewUiState(
    val accessChecked: Boolean = false,
    val isAdmin: Boolean = false,
    val isBusy: Boolean = false,
    val feedbackItems: List<DeveloperFeedbackItem> = emptyList(),
    val message: String? = null,
    val error: String? = null,
)

class DeveloperReviewViewModel(
    private val repository: DeveloperFeedbackRepository = DeveloperFeedbackRepository(),
) : ViewModel() {
    private val _state = MutableStateFlow(DeveloperReviewUiState())
    val state: StateFlow<DeveloperReviewUiState> = _state.asStateFlow()

    fun checkAccess(force: Boolean = false) {
        if (_state.value.isBusy || (_state.value.accessChecked && !force)) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, error = null, message = null)
            runCatching { repository.isCurrentUserAdmin() }
                .onSuccess { isAdmin ->
                    _state.value = _state.value.copy(
                        accessChecked = true,
                        isAdmin = isAdmin,
                        isBusy = false,
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        accessChecked = true,
                        isAdmin = false,
                        isBusy = false,
                        error = error.message ?: "Entwicklerzugang konnte nicht geprüft werden",
                    )
                }
        }
    }

    fun enroll(code: String) {
        if (_state.value.isBusy) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, error = null, message = null)
            runCatching { repository.enrollAdmin(code) }
                .onSuccess { isAdmin ->
                    _state.value = _state.value.copy(
                        accessChecked = true,
                        isAdmin = isAdmin,
                        isBusy = false,
                        message = if (isAdmin) "Developer Review aktiviert" else null,
                        error = if (isAdmin) null else "Aktivierung nicht möglich",
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isBusy = false,
                        error = error.message ?: "Aktivierung fehlgeschlagen",
                    )
                }
        }
    }

    fun submit(
        draft: DeveloperFeedbackDraft,
        context: DeveloperReviewContext,
        appVersion: String,
        buildNumber: String,
        gitCommit: String,
        device: Map<String, String>,
        onSaved: () -> Unit = {},
    ) {
        if (_state.value.isBusy || draft.note.isBlank()) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, error = null, message = null)
            runCatching {
                repository.submitFeedback(
                    draft = draft,
                    context = context,
                    appVersion = appVersion,
                    buildNumber = buildNumber,
                    gitCommit = gitCommit,
                    device = device,
                )
            }.onSuccess {
                _state.value = _state.value.copy(
                    isBusy = false,
                    message = "Notiz in Developer Inbox gespeichert",
                )
                onSaved()
            }.onFailure { error ->
                _state.value = _state.value.copy(
                    isBusy = false,
                    error = error.message ?: "Notiz konnte nicht gespeichert werden",
                )
            }
        }
    }

    fun refreshInbox() {
        if (_state.value.isBusy || !_state.value.isAdmin) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, error = null)
            runCatching { repository.loadFeedback() }
                .onSuccess { items ->
                    _state.value = _state.value.copy(isBusy = false, feedbackItems = items)
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isBusy = false,
                        error = error.message ?: "Inbox konnte nicht geladen werden",
                    )
                }
        }
    }

    fun updateStatus(id: String, status: DeveloperFeedbackStatus) {
        if (_state.value.isBusy || !_state.value.isAdmin) return
        viewModelScope.launch {
            _state.value = _state.value.copy(isBusy = true, error = null)
            runCatching { repository.updateStatus(id, status) }
                .onSuccess {
                    _state.value = _state.value.copy(
                        isBusy = false,
                        feedbackItems = _state.value.feedbackItems.map {
                            if (it.id == id) it.copy(status = status) else it
                        },
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        isBusy = false,
                        error = error.message ?: "Status konnte nicht geändert werden",
                    )
                }
        }
    }

    fun clearMessage() {
        _state.value = _state.value.copy(message = null, error = null)
    }
}
