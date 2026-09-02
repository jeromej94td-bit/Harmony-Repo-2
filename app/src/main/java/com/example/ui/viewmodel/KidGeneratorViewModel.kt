package com.example.ui.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.KidScenario
import com.example.data.model.KidStyle
import com.example.data.model.KidGender
import com.example.data.model.KidGeneratorResponse
import com.example.data.model.ProfileEntity
import com.example.data.repository.KidGeneratorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

data class KidGeneratorUiState(
    val userMainUri: Uri? = null,
    val partnerMainUri: Uri? = null,
    val additionalUserUris: List<Uri> = emptyList(),
    val additionalPartnerUris: List<Uri> = emptyList(),
    val selectedScenario: KidScenario = KidScenario.BABY,
    val selectedStyle: KidStyle = KidStyle.ANIME_ROMANTIC,
    val selectedGender: KidGender = KidGender.SURPRISE,
    val wishes: String = "",
    val isGenerating: Boolean = false,
    val errorMessage: String? = null,
    val result: KidGeneratorResponse? = null,
    val generatedLocalPath: String? = null
)

class KidGeneratorViewModel(context: Context) : ViewModel() {
    private val repository = KidGeneratorRepository(context)

    private val _uiState = MutableStateFlow(KidGeneratorUiState())
    val uiState: StateFlow<KidGeneratorUiState> = _uiState.asStateFlow()

    fun selectUserMainPhoto(uri: Uri) {
        _uiState.value = _uiState.value.copy(userMainUri = uri)
    }

    fun selectPartnerMainPhoto(uri: Uri) {
        _uiState.value = _uiState.value.copy(partnerMainUri = uri)
    }

    fun addAdditionalUserPhoto(uri: Uri) {
        val currentList = _uiState.value.additionalUserUris.toMutableList()
        if (currentList.size < 3) {
            currentList.add(uri)
            _uiState.value = _uiState.value.copy(additionalUserUris = currentList)
        }
    }

    fun removeAdditionalUserPhoto(uri: Uri) {
        val currentList = _uiState.value.additionalUserUris.toMutableList()
        currentList.remove(uri)
        _uiState.value = _uiState.value.copy(additionalUserUris = currentList)
    }

    fun addAdditionalPartnerPhoto(uri: Uri) {
        val currentList = _uiState.value.additionalPartnerUris.toMutableList()
        if (currentList.size < 3) {
            currentList.add(uri)
            _uiState.value = _uiState.value.copy(additionalPartnerUris = currentList)
        }
    }

    fun removeAdditionalPartnerPhoto(uri: Uri) {
        val currentList = _uiState.value.additionalPartnerUris.toMutableList()
        currentList.remove(uri)
        _uiState.value = _uiState.value.copy(additionalPartnerUris = currentList)
    }

    fun setScenario(scenario: KidScenario) {
        _uiState.value = _uiState.value.copy(selectedScenario = scenario)
    }

    fun setStyle(style: KidStyle) {
        _uiState.value = _uiState.value.copy(selectedStyle = style)
    }

    fun setGender(gender: KidGender) {
        _uiState.value = _uiState.value.copy(selectedGender = gender)
    }

    fun setWishes(wishes: String) {
        _uiState.value = _uiState.value.copy(wishes = wishes)
    }

    fun clearResult() {
        _uiState.value = _uiState.value.copy(result = null, generatedLocalPath = null, errorMessage = null)
    }

    fun generate(context: Context, profile: ProfileEntity) {
        val state = _uiState.value
        if (state.isGenerating) return

        val userPhoto = state.userMainUri?.toString() ?: profile.userAvatarPath
        val partnerPhoto = state.partnerMainUri?.toString() ?: profile.partnerAvatarPath

        if (userPhoto.isNullOrBlank()) {
            _uiState.value = state.copy(errorMessage = "Bitte füge für dich ein Hauptfoto hinzu.")
            return
        }
        if (partnerPhoto.isNullOrBlank()) {
            _uiState.value = state.copy(errorMessage = "Bitte füge für deinen Partner ein Hauptfoto hinzu.")
            return
        }

        _uiState.value = state.copy(isGenerating = true, errorMessage = null, result = null, generatedLocalPath = null)

        viewModelScope.launch {
            repository.generateKid(
                userName = profile.userName.ifBlank { "Ich" },
                partnerName = profile.partnerName.ifBlank { "Partner" },
                userMainSource = userPhoto,
                partnerMainSource = partnerPhoto,
                additionalUserSources = state.additionalUserUris.map { it.toString() },
                additionalPartnerSources = state.additionalPartnerUris.map { it.toString() },
                scenario = state.selectedScenario,
                style = state.selectedStyle,
                gender = state.selectedGender,
                wishes = state.wishes
            ).onSuccess { response ->
                if (response.ok && response.imageBase64 != null) {
                    val savedFile = saveBase64Image(context, response.imageBase64)
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        result = response,
                        generatedLocalPath = savedFile?.absolutePath
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isGenerating = false,
                        errorMessage = response.error ?: "Ein unbekannter Fehler ist aufgetreten."
                    )
                }
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isGenerating = false,
                    errorMessage = "Fehler bei der Anfrage: ${error.localizedMessage ?: "Keine Antwort erhalten."}"
                )
            }
        }
    }

    private fun saveBase64Image(context: Context, base64Str: String): File? {
        return runCatching {
            val imageBytes = Base64.decode(base64Str, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: return null
            val dir = File(context.filesDir, "ai_images").apply { mkdirs() }
            val file = File(dir, "kid_blend_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            file
        }.getOrNull()
    }
}
