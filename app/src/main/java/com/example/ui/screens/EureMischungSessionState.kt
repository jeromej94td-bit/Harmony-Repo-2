package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import com.example.util.BlendGender
import com.example.util.BlendScenario
import com.example.util.BlendStyle
import com.example.util.GeneratedImageResult
import java.io.File
import java.io.Serializable

private data class EureMischungSavedResult(
    val localFilePath: String,
    val aiDescription: String,
    val promptSummary: String,
    val timestamp: Long
) : Serializable

private data class EureMischungPersistedState(
    val parent1CustomUriString: String?,
    val parent2CustomUriString: String?,
    val selectedScenarioName: String,
    val selectedStyleName: String,
    val selectedGenderName: String,
    val customNotes: String,
    val errorMessage: String?,
    val technicalErrorDetails: String?,
    val isTechDetailsExpanded: Boolean,
    val currentResult: EureMischungSavedResult?,
    val history: ArrayList<EureMischungSavedResult>,
    val isFullscreenImageOpen: Boolean,
    val isMomentSaved: Boolean
) : Serializable

/**
 * Saveable state holder for the user-visible Eure-Mischung session.
 *
 * The Gemini request itself is deliberately not persisted. If Android recreates the Activity
 * while a request is running, that composition-scoped request is cancelled and the screen comes
 * back idle with the selected photos/configuration/draft intact so the user can retry instead of
 * being trapped in a stale loading state.
 *
 * Generated images are already written to a local file by [GeneratedImageResult]. Only the small
 * file metadata is stored in SavedState; bitmaps are decoded again from disk after recreation.
 */
internal class EureMischungSessionState private constructor(
    parent1CustomUriString: String? = null,
    parent2CustomUriString: String? = null,
    selectedScenario: BlendScenario = BlendScenario.BABY,
    selectedStyle: BlendStyle = BlendStyle.ANIME,
    selectedGender: BlendGender = BlendGender.SURPRISE,
    customNotes: String = "",
    errorMessage: String? = null,
    technicalErrorDetails: String? = null,
    isTechDetailsExpanded: Boolean = false,
    currentResult: GeneratedImageResult? = null,
    history: List<GeneratedImageResult> = emptyList(),
    isFullscreenImageOpen: Boolean = false,
    isMomentSaved: Boolean = false
) {
    constructor() : this(parent1CustomUriString = null)

    val parent1CustomUriStringState: MutableState<String?> = mutableStateOf(parent1CustomUriString)
    val parent2CustomUriStringState: MutableState<String?> = mutableStateOf(parent2CustomUriString)
    val selectedScenarioState: MutableState<BlendScenario> = mutableStateOf(selectedScenario)
    val selectedStyleState: MutableState<BlendStyle> = mutableStateOf(selectedStyle)
    val selectedGenderState: MutableState<BlendGender> = mutableStateOf(selectedGender)
    val customNotesState: MutableState<String> = mutableStateOf(customNotes)

    val errorMessageState: MutableState<String?> = mutableStateOf(errorMessage)
    val technicalErrorDetailsState: MutableState<String?> = mutableStateOf(technicalErrorDetails)
    val isTechDetailsExpandedState: MutableState<Boolean> = mutableStateOf(isTechDetailsExpanded)
    val currentResultState: MutableState<GeneratedImageResult?> = mutableStateOf(currentResult)
    val isFullscreenImageOpenState: MutableState<Boolean> = mutableStateOf(isFullscreenImageOpen)
    val isMomentSavedState: MutableState<Boolean> = mutableStateOf(isMomentSaved)

    val historyList = mutableStateListOf<GeneratedImageResult>().apply { addAll(history) }

    companion object {
        val Saver: Saver<EureMischungSessionState, EureMischungPersistedState> = Saver(
            save = { state ->
                EureMischungPersistedState(
                    parent1CustomUriString = state.parent1CustomUriStringState.value,
                    parent2CustomUriString = state.parent2CustomUriStringState.value,
                    selectedScenarioName = state.selectedScenarioState.value.name,
                    selectedStyleName = state.selectedStyleState.value.name,
                    selectedGenderName = state.selectedGenderState.value.name,
                    customNotes = state.customNotesState.value,
                    errorMessage = state.errorMessageState.value,
                    technicalErrorDetails = state.technicalErrorDetailsState.value,
                    isTechDetailsExpanded = state.isTechDetailsExpandedState.value,
                    currentResult = state.currentResultState.value?.toSavedResult(),
                    history = ArrayList(state.historyList.map(GeneratedImageResult::toSavedResult)),
                    isFullscreenImageOpen = state.isFullscreenImageOpenState.value,
                    isMomentSaved = state.isMomentSavedState.value
                )
            },
            restore = { saved ->
                val restoredHistory = saved.history.mapNotNull(EureMischungSavedResult::restoreGeneratedImageResult)
                val restoredCurrent = saved.currentResult?.restoreGeneratedImageResult()
                    ?: saved.currentResult?.localFilePath?.let { path ->
                        restoredHistory.firstOrNull { it.localFilePath == path }
                    }

                EureMischungSessionState(
                    parent1CustomUriString = saved.parent1CustomUriString,
                    parent2CustomUriString = saved.parent2CustomUriString,
                    selectedScenario = enumValueOrDefault(saved.selectedScenarioName, BlendScenario.BABY),
                    selectedStyle = enumValueOrDefault(saved.selectedStyleName, BlendStyle.ANIME),
                    selectedGender = enumValueOrDefault(saved.selectedGenderName, BlendGender.SURPRISE),
                    customNotes = saved.customNotes,
                    errorMessage = saved.errorMessage,
                    technicalErrorDetails = saved.technicalErrorDetails,
                    isTechDetailsExpanded = saved.isTechDetailsExpanded,
                    currentResult = restoredCurrent,
                    history = restoredHistory,
                    isFullscreenImageOpen = saved.isFullscreenImageOpen && restoredCurrent != null,
                    isMomentSaved = saved.isMomentSaved && restoredCurrent != null
                )
            }
        )
    }
}

private fun GeneratedImageResult.toSavedResult(): EureMischungSavedResult = EureMischungSavedResult(
    localFilePath = localFilePath,
    aiDescription = aiDescription,
    promptSummary = promptSummary,
    timestamp = timestamp
)

private fun EureMischungSavedResult.restoreGeneratedImageResult(): GeneratedImageResult? {
    val file = File(localFilePath)
    if (!file.exists() || !file.isFile) return null
    val bitmap = BitmapFactory.decodeFile(localFilePath) ?: return null
    return GeneratedImageResult(
        bitmap = bitmap,
        localFilePath = localFilePath,
        aiDescription = aiDescription,
        promptSummary = promptSummary,
        timestamp = timestamp
    )
}

private inline fun <reified T : Enum<T>> enumValueOrDefault(name: String, fallback: T): T =
    runCatching { enumValueOf<T>(name) }.getOrDefault(fallback)
