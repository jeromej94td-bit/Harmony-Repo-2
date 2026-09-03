package com.example.ui

import android.app.Application
import android.util.Log
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.data.DeveloperDataManager
import com.example.data.DriveTotAssetInstaller
import com.example.data.LinkPreviewResolver
import com.example.data.LinkPreviewResult
import com.example.data.OkHttpLinkPreviewResolver
import com.example.data.db.HarmonyDatabase
import com.example.data.model.AnswerEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CoupleStatsEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.HarmonyPacksData
import com.example.data.model.MemoryDefaults
import com.example.data.model.MemoryEntryEntity
import com.example.data.model.MemoryEntryKind
import com.example.data.model.MomentEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.QuestionPack
import com.example.data.model.SharedPicEntity
import com.example.data.repository.HarmonyRepository
import com.example.data.repository.MemoryRepository
import com.example.data.repository.RoomMemoryRepository
import com.example.ui.components.TotImageProvider
import com.example.util.GeminiAudioTranscriber
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URLEncoder
import java.util.UUID

data class ActivePackRun(
    val pack: QuestionPack,
    val currentIndex: Int = 0,
    val currentAnswers: Map<Int, String> = emptyMap(),
    val isFinished: Boolean = false
)

data class HarmonyUiState(
    val selectedTab: Int = 0,
    val profile: ProfileEntity = ProfileEntity(),
    val answers: List<AnswerEntity> = emptyList(),
    val messages: List<ChatMessageEntity> = emptyList(),
    val sharedPics: List<SharedPicEntity> = emptyList(),
    val moments: List<MomentEntity> = emptyList(),
    val stats: CoupleStatsEntity = CoupleStatsEntity(),
    val packFilter: String = "all", // "all", "open", "done"
    val selectedTopicId: String? = null,
    val selectedCategoryId: String? = null,
    val activeRun: ActivePackRun? = null,
    val isExitConfirmOpen: Boolean = false,
    val isOwnAnswerDialogOpen: Boolean = false,
    val ownAnswerTargetIndex: Int? = null,
    val ownAnswerMode: String? = null, // null or "disc"
    val isProfileSheetOpen: Boolean = false,
    val isEditProfileOpen: Boolean = false,
    val isAddMomentOpen: Boolean = false,
    val toastMessage: String? = null,
    val isRefreshing: Boolean = false,
    val isDarkMode: Boolean = true,
    val appLanguage: String = "de",
)

class HarmonyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HarmonyDatabase.getInstance(application)
    private val repository = HarmonyRepository(db, application)
    private val memoryRepository: MemoryRepository = RoomMemoryRepository(db)
    private val linkPreviewResolver: LinkPreviewResolver = OkHttpLinkPreviewResolver()
    private val generatedJson = Json { ignoreUnknownKeys = true }
    private val settingsPrefs = application.getSharedPreferences("harmony_settings_prefs", android.content.Context.MODE_PRIVATE)
    private val _isDarkMode = MutableStateFlow(settingsPrefs.getBoolean("is_dark_mode", true))
    private val _appLanguage = MutableStateFlow(settingsPrefs.getString("app_language", "de") ?: "de")

    private val _selectedTab = MutableStateFlow(0)
    private val _packFilter = MutableStateFlow("all")
    private val _selectedTopicId = MutableStateFlow<String?>(null)
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    private val _activeRun = MutableStateFlow<ActivePackRun?>(null)

    private val _isExitConfirmOpen = MutableStateFlow(false)
    private val _isOwnAnswerDialogOpen = MutableStateFlow(false)
    private val _ownAnswerTargetIndex = MutableStateFlow<Int?>(null)
    private val _ownAnswerMode = MutableStateFlow<String?>(null)

    private val _isProfileSheetOpen = MutableStateFlow(false)
    private val _isEditProfileOpen = MutableStateFlow(false)
    private val _isAddMomentOpen = MutableStateFlow(false)

    private val _toastMessage = MutableStateFlow<String?>(null)

    private val _isRefreshing = MutableStateFlow(false)

    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<HarmonyUiState> = combine(
        _selectedTab,
        repository.profileFlow,
        repository.answersFlow,
        repository.chatMessagesFlow,
        repository.sharedPicsFlow,
        repository.momentsFlow,
        repository.statsFlow,
        _packFilter,
        _selectedTopicId,
        _selectedCategoryId,
        _activeRun,
        _isExitConfirmOpen,
        _isOwnAnswerDialogOpen,
        _isProfileSheetOpen,
        _isEditProfileOpen,
        _isAddMomentOpen,
        _toastMessage,
        _isRefreshing,
        _isDarkMode,
        _appLanguage,
    ) { arrayOfValues ->
        val publishedGames = rawGenerated
            .filter { it.contentType == "GAME" && it.status == "PUBLISHED" }
            .sortedByDescending { it.createdAt }

        HarmonyUiState(
            selectedTab = arrayOfValues[0] as Int,
            profile = (arrayOfValues[1] as? ProfileEntity) ?: ProfileEntity(),
            answers = (arrayOfValues[2] as? List<AnswerEntity>) ?: emptyList(),
            messages = (arrayOfValues[3] as? List<ChatMessageEntity>) ?: emptyList(),
            sharedPics = (arrayOfValues[4] as? List<SharedPicEntity>) ?: emptyList(),
            moments = (arrayOfValues[5] as? List<MomentEntity>) ?: emptyList(),
            stats = (arrayOfValues[6] as? CoupleStatsEntity) ?: CoupleStatsEntity(),
            packFilter = arrayOfValues[7] as String,
            selectedTopicId = arrayOfValues[8] as? String,
            selectedCategoryId = arrayOfValues[9] as? String,
            activeRun = arrayOfValues[10] as? ActivePackRun,
            isExitConfirmOpen = arrayOfValues[11] as Boolean,
            isOwnAnswerDialogOpen = arrayOfValues[12] as Boolean,
            ownAnswerTargetIndex = _ownAnswerTargetIndex.value,
            ownAnswerMode = _ownAnswerMode.value,
            isProfileSheetOpen = arrayOfValues[13] as Boolean,
            isEditProfileOpen = arrayOfValues[14] as Boolean,
            isAddMomentOpen = arrayOfValues[15] as Boolean,
            toastMessage = arrayOfValues[16] as? String,
            isRefreshing = arrayOfValues[17] as Boolean,
            isDarkMode = arrayOfValues[18] as Boolean,
            appLanguage = arrayOfValues[19] as String
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HarmonyUiState()
    )

    private fun installDriveTotImages(application: Application) {
        runCatching {
            DriveTotAssetInstaller.install(application).forEach { (name, path) ->
                TotImageProvider.setGeneratedImage(name, path)
            }
        }
    }

    init {
        runCatching { DeveloperDataManager.init(application) }
        installDriveTotImages(application)
        
        // Supabase initialisieren
        runCatching {
            com.example.data.SupabaseClientProvider.init(
                projectId = "rspgnonlpkxdudbjxnrl",
                anonKey = "sb_publishable_qNtemRRaLIW0nbFb52uKLw_rWwlgUo1"
            )
        }
        
        viewModelScope.launch {
            runCatching { repository.ensureInitialData() }
            runCatching { com.example.data.SupabaseSync.fetchAndSync(application) }
            // SupabaseSync baut die Bildregistrierung neu auf. Drive-Bilder danach
            // erneut setzen, damit die lokalen GitHub-Assets die Web-Fallbacks schlagen.
            installDriveTotImages(application)
        }


    }

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    fun setPackFilter(filter: String) {
        _packFilter.value = filter
    }

    fun openTopic(topicId: String) {
        _selectedTopicId.value = topicId
        _selectedCategoryId.value = null
        _selectedTab.value = 6 // Pack list screen
    }

    fun openCategory(catId: String) {
        _selectedCategoryId.value = catId
        _selectedTopicId.value = null
        _selectedTab.value = 6 // Pack list screen
    }

    fun refreshData() {
        viewModelScope.launch {
            _isRefreshing.value = true
            com.example.data.SupabaseSync.fetchAndSync(getApplication())
            installDriveTotImages(getApplication())
            _isRefreshing.value = false
            showToast("Daten aktualisiert 🔄")
        }
    }

    fun ensureProductionData() {
        viewModelScope.launch {
            runCatching { repository.ensureInitialData() }
        }
    }

    fun ensureDemoData() {
        viewModelScope.launch {
            runCatching { repository.ensureDemoData() }
        }
    }

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2400)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }        )
        foregroundGameGenerator = generator
        lifecycleOwner.lifecycle.addObserver(generator)
    }
    fun clearToast() {
        _toastMessage.value = null
    }

    fun saveEitherOrAnswer(questionIndex: Int, userChoice: String, partnerChoice: String) {
        viewModelScope.launch {
            repository.saveAnswer(
                packId = "entweder_oder_panda",
                questionIndex = questionIndex,
                answerText = EitherOrAnswerCodec.encode(userChoice, partnerChoice)
            )
        }
    }

    // --- QUIZ RUNNER ---

    fun startPack(packId: String) {
        val pack = HarmonyPacksData.PACKS.find { it.id == packId }
            ?.let { com.example.data.model.LoveBalanceQuestionPolicy.ensureHappyCoupleFirst(it) }
            ?: return
        val currentAnswers = uiState.value.answers.filter { it.packId == packId }
            .associate { it.questionIndex to it.answerText }
        val total = if (pack.type == "tot") pack.pairs.size else pack.questions.size
        val resumeIndex = if (packId == com.example.data.model.LoveBalanceQuestionPolicy.PACK_ID) {
            0
        } else {
            RunnerProgressPolicy.firstUnanswered(total, currentAnswers.keys) ?: 0
        }
        _activeRun.value = ActivePackRun(
            pack = pack,
            currentIndex = resumeIndex,
            currentAnswers = currentAnswers,
            isFinished = false
        )
    }

    fun startPackForTest(pack: QuestionPack, currentIndex: Int = 0) {
        _activeRun.value = ActivePackRun(
            pack = pack,
            currentIndex = currentIndex,
            currentAnswers = emptyMap(),
            isFinished = false
        )
    }

    fun pickAnswer(optionText: String) {
        val run = _activeRun.value ?: return
        if (run.isFinished) return
        val total = if (run.pack.type == "tot") run.pack.pairs.size else run.pack.questions.size
        if (run.currentIndex !in 0 until total) return

        val answers = run.currentAnswers.toMutableMap()
        answers[run.currentIndex] = optionText

        if (RunnerProgressPolicy.deferAutomaticAdvance(run.pack.type, optionText)) {
            _activeRun.value = run.copy(currentAnswers = answers)
            viewModelScope.launch {
                repository.saveAnswer(run.pack.id, run.currentIndex, optionText)
            }
            return
        }

        if (run.currentIndex >= total - 1) {
            val completedRun = run.copy(currentAnswers = answers, isFinished = true)
            _activeRun.value = completedRun
            viewModelScope.launch {
                answers.forEach { (index, answer) -> repository.saveAnswer(run.pack.id, index, answer) }
            }
        } else {
            _activeRun.value = run.copy(currentAnswers = answers, currentIndex = run.currentIndex + 1)
            viewModelScope.launch {
                repository.saveAnswer(run.pack.id, run.currentIndex, optionText)
            }
        }
    }

    fun nextStep() {
        val run = _activeRun.value ?: return
        val totalLen = if (run.pack.type == "tot") run.pack.pairs.size else run.pack.questions.size
        val nextIndex = run.currentIndex + 1
        if (nextIndex >= totalLen) {
            finishPack()
        } else {
            _activeRun.value = run.copy(currentIndex = nextIndex)
        }
    }

    fun previousStep() {
        val run = _activeRun.value ?: return
        val previousIndex = when {
            run.isFinished -> {
                val totalLen = if (run.pack.type == "tot") run.pack.pairs.size else run.pack.questions.size
                (totalLen - 1).coerceAtLeast(0)
            }
            run.currentIndex > 0 -> run.currentIndex - 1
            else -> 0
        }
        _activeRun.value = run.copy(currentIndex = previousIndex, isFinished = false)
        _isExitConfirmOpen.value = false
        closeOwnAnswerDialog()
    }

    fun finishPack() {
        val run = _activeRun.value ?: return
        val total = if (run.pack.type == "tot") run.pack.pairs.size else run.pack.questions.size
        if (total <= 0) {
            _activeRun.value = null
            return
        }
        viewModelScope.launch {
            run.currentAnswers.forEach { (index, answerText) ->
                repository.saveAnswer(run.pack.id, index, answerText)
            }
            _activeRun.value = run.copy(isFinished = true)
        }
    }

    fun askExitRun() {
        if (_activeRun.value != null) {
            _isExitConfirmOpen.value = true
        }
    }

    fun closeExitConfirm() {
        _isExitConfirmOpen.value = false
    }

    fun closeRunner() {
        val run = _activeRun.value
        // UI sofort freigeben; Daten speichern darf den Nutzer nicht im Spiel festhalten.
        _activeRun.value = null
        _isExitConfirmOpen.value = false
        closeOwnAnswerDialog()

        if (run == null) return
        viewModelScope.launch {
            runCatching {
                run.currentAnswers.forEach { (index, answerText) ->
                    repository.saveAnswer(run.pack.id, index, answerText)
                }
            }.onFailure {
                Log.e("HarmonyRunner", "Antworten konnten beim Verlassen nicht gespeichert werden", it)
                showToast("Spiel geschlossen – einzelne Antworten konnten nicht gespeichert werden.")
            }
        }
    }

    fun openOwnAnswerDialog(index: Int? = null, mode: String? = null) {
        _ownAnswerTargetIndex.value = index
        _ownAnswerMode.value = mode
        _isOwnAnswerDialogOpen.value = true
    }

    fun closeOwnAnswerDialog() {
        _isOwnAnswerDialogOpen.value = false
        _ownAnswerTargetIndex.value = null
        _ownAnswerMode.value = null
    }

    fun submitOwnAnswer(answerText: String) {
        val run = _activeRun.value ?: return
        val targetIndex = _ownAnswerTargetIndex.value ?: run.currentIndex
        if (answerText.isBlank()) return

        val trimmed = answerText.trim()
        val answers = run.currentAnswers.toMutableMap().apply {
            put(targetIndex, trimmed)
        }
        closeOwnAnswerDialog()

        val total = if (run.pack.type == "tot") run.pack.pairs.size else run.pack.questions.size
        if (run.pack.type == "disc") {
            val nextUnanswered = RunnerProgressPolicy.nextUnanswered(
                total = total,
                answeredIndexes = answers.keys,
                afterIndex = targetIndex
            )
            if (nextUnanswered == null) {
                _activeRun.value = run.copy(
                    currentIndex = targetIndex.coerceIn(0, (total - 1).coerceAtLeast(0)),
                    currentAnswers = answers,
                    isFinished = true
                )
                viewModelScope.launch {
                    answers.forEach { (i, ans) -> repository.saveAnswer(run.pack.id, i, ans) }
                }
            } else {
                _activeRun.value = run.copy(
                    currentIndex = nextUnanswered,
                    currentAnswers = answers,
                    isFinished = false
                )
                viewModelScope.launch {
                    repository.saveAnswer(run.pack.id, targetIndex, trimmed)
                }
            }
            return
        }

        if (targetIndex >= total - 1) {
            _activeRun.value = run.copy(currentIndex = targetIndex, currentAnswers = answers, isFinished = true)
            viewModelScope.launch {
                answers.forEach { (i, ans) -> repository.saveAnswer(run.pack.id, i, ans) }
            }
        } else {
            _activeRun.value = run.copy(currentIndex = targetIndex + 1, currentAnswers = answers)
            viewModelScope.launch {
                repository.saveAnswer(run.pack.id, targetIndex, trimmed)
            }
        }
    }

    fun saveOwnAnswer(answerText: String) {
        submitOwnAnswer(answerText)
    }

    // --- CHAT ---

    private val SIM_REPLIES = listOf(
        "Das klingt schön 🥰",
        "Ich vermiss dich gerade richtig",
        "Erzähl mir mehr davon 💕",
        "Haha du bringst mich immer zum Lachen 😄",
        "Können wir heute Abend telefonieren?",
        "Ich denk so oft an dich ❤️"
    )

    fun sendChatMessage(text: String) {
        if (text.isBlank()) return
        viewModelScope.launch {
            repository.sendChatMessage(text, sender = "me")
            val profile = uiState.value.profile
            if (profile.simulatorEnabled) {
                delay(1200)
                val reply = SIM_REPLIES.random()
                repository.sendChatMessage(reply, sender = "them")
            }
        }
    }

    fun sendVoiceChatMessage(audioPath: String, durationSeconds: Int) {
        if (audioPath.isBlank()) return
        viewModelScope.launch {
            repository.sendChatVoiceMessage(audioPath, durationSeconds, sender = "me")
            val profile = uiState.value.profile
            if (profile.simulatorEnabled) {
                delay(1500)
                val reply = SIM_REPLIES.random()
                repository.sendChatMessage(reply, sender = "them")
            }
        }
    }

    fun sendChatImage(uri: Uri) {
        viewModelScope.launch {
            repository.sendChatImage(uri)
            showToast("Bild gesendet 📷")
        }
    }

    fun reportPartner() {
        showToast("Meldung vorbereitet – vor dem Senden kannst du Details prüfen")
    }

    // --- MOMENTS ---

    fun openAddMomentDialog() {
        _isAddMomentOpen.value = true
    }

    fun closeAddMomentDialog() {
        _isAddMomentOpen.value = false
    }

    fun addMoment(title: String, content: String, imageUris: List<Uri> = emptyList()) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            repository.addMoment(title, content, imageUris)
            _isAddMomentOpen.value = false
            showToast("Moment gespeichert 💞")
        }
    }

    // --- PROFILE ---

    fun openProfileSheet() {
        _isProfileSheetOpen.value = true
    }

    fun closeProfileSheet() {
        _isProfileSheetOpen.value = false
    }

    fun toggleDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
        settingsPrefs.edit().putBoolean("is_dark_mode", enabled).apply()
    }

    fun setLanguage(lang: String) {
        _appLanguage.value = lang
        settingsPrefs.edit().putString("app_language", lang).apply()
    }

    fun toggleSimulator() {
        val profile = uiState.value.profile
        viewModelScope.launch {
            repository.setSimulatorEnabled(!profile.simulatorEnabled)
        }
    }

    fun openEditProfile() {
        _isEditProfileOpen.value = true
    }

    fun closeEditProfile() {
        _isEditProfileOpen.value = false
    }

    fun saveEditProfile(userName: String, partnerName: String, startDate: Long) {
        viewModelScope.launch {
            repository.updateProfile(userName, partnerName, startDate)
            _isEditProfileOpen.value = false
            showToast("Profil gespeichert")
        }
    }

    fun updateProfileAvatar(uri: Uri, isUser: Boolean) {
        viewModelScope.launch {
            repository.updateProfileAvatar(uri, isUser)
            showToast(if (isUser) "Dein Profilbild wurde aktualisiert" else "Partnerbild wurde aktualisiert")
        }
    }

    fun addSharedPictures(uris: List<Uri>, addedBy: String = "me") {
        if (uris.isEmpty()) return
        viewModelScope.launch {
            repository.addSharedPictures(uris, addedBy)
            showToast("${uris.size} Bild${if (uris.size == 1) "" else "er"} für PicShare gespeichert")
        }
    }

    fun updateSharedPicture(pic: SharedPicEntity) {
        viewModelScope.launch { repository.updateSharedPicture(pic) }
    }

    // --- HARMONY BRAIN ---

    }
