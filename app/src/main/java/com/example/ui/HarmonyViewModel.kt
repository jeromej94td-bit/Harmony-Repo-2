package com.example.ui

import android.app.Application
import android.util.Log
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.data.DeveloperDataManager
import com.example.data.DriveTotAssetInstaller
import com.example.data.GeminiBrainGateway
import com.example.data.LinkPreviewResolver
import com.example.data.LinkPreviewResult
import com.example.data.OkHttpLinkPreviewResolver
import com.example.data.brain.ForegroundGameGenerator
import com.example.data.brain.gateway.SupabaseHarmonyBrainGateway
import com.example.data.brain.repository.BrainRepository
import com.example.data.db.HarmonyDatabase
import com.example.data.model.AnswerEntity
import com.example.data.model.BrainChatSuggestionItem
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
    val brainInterests: List<com.example.data.model.BrainInterestEntity> = emptyList(),
    val brainSuggestions: List<com.example.data.model.BrainSuggestionEntity> = emptyList(),
    val brainQuestions: List<com.example.data.model.BrainQuestionEntity> = emptyList(),
    val brainMessages: List<com.example.data.model.BrainMessage> = emptyList(),
    val isBrainChatMode: Boolean = false,
    val isBrainGenerating: Boolean = false,
    val generatedGames: List<com.example.data.brain.db.BrainGeneratedContentEntity> = emptyList()
)

class HarmonyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HarmonyDatabase.getInstance(application)
    private val repository = HarmonyRepository(db, application)
    private val memoryRepository: MemoryRepository = RoomMemoryRepository(db)
    private val brainRepository = com.example.data.brain.repository.BrainRepository(db.brainRoomDao(), application)
    private val brainGateway = SupabaseHarmonyBrainGateway.getInstance()
    private val linkPreviewResolver: LinkPreviewResolver = OkHttpLinkPreviewResolver()
    private val generatedJson = Json { ignoreUnknownKeys = true }
    private var foregroundGameGenerator: ForegroundGameGenerator? = null

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

    private val _brainMessages = MutableStateFlow<List<com.example.data.model.BrainMessage>>(emptyList())
    private val _isBrainChatMode = MutableStateFlow(false)
    private val _isBrainGenerating = MutableStateFlow(false)

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
        repository.brainInterestsFlow,
        repository.brainSuggestionsFlow,
        repository.brainQuestionsFlow,
        _brainMessages,
        _isBrainChatMode,
        _isBrainGenerating,
        brainRepository.generatedContentFlow
    ) { arrayOfValues ->
        val rawGenerated = (arrayOfValues[26] as? List<com.example.data.brain.db.BrainGeneratedContentEntity>) ?: emptyList()
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
            appLanguage = arrayOfValues[19] as String,
            brainInterests = (arrayOfValues[20] as? List<com.example.data.model.BrainInterestEntity>) ?: emptyList(),
            brainSuggestions = (arrayOfValues[21] as? List<com.example.data.model.BrainSuggestionEntity>) ?: emptyList(),
            brainQuestions = (arrayOfValues[22] as? List<com.example.data.model.BrainQuestionEntity>) ?: emptyList(),
            brainMessages = (arrayOfValues[23] as? List<com.example.data.model.BrainMessage>) ?: emptyList(),
            isBrainChatMode = arrayOfValues[24] as Boolean,
            isBrainGenerating = arrayOfValues[25] as Boolean,
            generatedGames = publishedGames
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

        // --- HARMONY BRAIN ANALYZER ---
        _brainMessages.value = listOf(
            com.example.data.model.BrainMessage(
                text = "Hallo! Ich bin euer Harmony Brain 🧠. Ich analysiere eure gemeinsamen Antworten und helfe euch dabei, noch tiefere Gemeinsamkeiten und neue Interessen zu entdecken.\n\nFragt mich gerne nach Date-Ideen, euren gemeinsamen Interessen oder wie ihr eure Unterschiede feiern könnt!",
                sender = "brain"
            )
        )

        viewModelScope.launch {
            repository.answersFlow.collect { answers ->
                runCatching {
                    val interests = com.example.data.HarmonyBrainEngine.analyzeAnswers(answers)
                    repository.saveInterests(interests)

                    val suggestions = com.example.data.HarmonyBrainEngine.generateSuggestions(interests)
                    val existingSuggestions = repository.getAllSuggestions()
                    val suggestionsToInsert = suggestions.map { sug ->
                        val existing = existingSuggestions.find { it.id == sug.id }
                        if (existing != null) {
                            sug.copy(feedback = existing.feedback)
                        } else {
                            sug
                        }
                    }
                    repository.saveSuggestions(suggestionsToInsert)

                    // No default static offline questions generated on startup
                }
            }
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

    fun showToast(msg: String) {
        _toastMessage.value = msg
        viewModelScope.launch {
            delay(2400)
            if (_toastMessage.value == msg) {
                _toastMessage.value = null
            }
        }
    }

    fun attachAutoGeneration(lifecycleOwner: LifecycleOwner) {
        if (foregroundGameGenerator != null) return

        val prefs = getApplication<Application>().getSharedPreferences("harmony_auto_generation", android.content.Context.MODE_PRIVATE)
        var lastCreatedGame: com.example.data.brain.db.BrainGeneratedContentEntity? = null

        val generator = ForegroundGameGenerator(
            scope = viewModelScope,
            prefs = prefs,
            generateOne = {
                try {
                    val prompt = "Erzeuge ein kurzes personalisiertes Paarspiel mit 7 abwechslungsreichen Fragen. Jede Frage muss mit Person A, Person B, Beide oder Keiner beantwortbar sein. Keine Wiederholungen. Gib zusätzlich einen kurzen Titel und genau ein Emoji aus der erlaubten Liste zurück."
                    val context = brainRepository.buildBrainContext(
                        task = "questions",
                        query = prompt,
                        userName = uiState.value.profile.userName,
                        partnerName = uiState.value.profile.partnerName
                    )
                    val result = brainGateway.generateQuestions(
                        query = prompt,
                        context = context
                    )
                    if (!result.ok || result.questions.isEmpty()) return@ForegroundGameGenerator false

                    val emoji = result.questions.firstOrNull()?.topic?.let { t ->
                        BrainRepository.ALLOWED_GAME_EMOJIS.find { it == t }
                    } ?: BrainRepository.ALLOWED_GAME_EMOJIS.random()

                    val title = result.questions.firstOrNull()?.category?.takeIf { it.isNotBlank() && it != "Gemischt" }
                        ?: "Wer kennt wen besser?"

                    val saved = brainRepository.storeGeneratedGame(
                        title = title,
                        emoji = emoji,
                        questions = result.questions,
                        sourceModel = result.model ?: "supabase-gemini"
                    )
                    if (saved != null) {
                        lastCreatedGame = saved
                        true
                    } else {
                        false
                    }
                } catch (e: Exception) {
                    false
                }
            },
            onCreated = {
                val game = lastCreatedGame
                if (game != null) {
                    val payload = runCatching {
                        generatedJson.decodeFromString(com.example.data.brain.model.GeneratedGamePayload.serializer(), game.payloadJson)
                    }.getOrNull()
                    val emoji = payload?.emoji ?: "✨"
                    val title = payload?.title ?: game.title ?: "Neues Spiel für euch"
                    com.example.notifications.HarmonyGameNotifier.notifyNewGeneratedGame(
                        context = getApplication(),
                        gameId = game.id,
                        title = title,
                        emoji = emoji
                    )
                    showToast("🧠 Harmony hat ein neues persönliches Spiel erstellt!")
                }
            }
        )
        foregroundGameGenerator = generator
        lifecycleOwner.lifecycle.addObserver(generator)
    }

    fun startGeneratedGame(gameId: String) {
        viewModelScope.launch {
            val entity = brainRepository.getAllGeneratedContent()
                .firstOrNull { it.id == gameId && it.contentType == "GAME" }
                ?: return@launch
            val payload = runCatching {
                generatedJson.decodeFromString(
                    com.example.data.brain.model.GeneratedGamePayload.serializer(), entity.payloadJson
                )
            }.getOrNull() ?: return@launch
            _activeRun.value = ActivePackRun(pack = payload.toQuestionPack())
            brainRepository.markGeneratedGameOpened(entity.id)
            _selectedTab.value = 1
        }
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
        _activeRun.value = ActivePackRun(
            pack = pack,
            currentIndex = 0,
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

        if (run.currentIndex >= total - 1) {
            val completedRun = run.copy(currentAnswers = answers, isFinished = true)
            _activeRun.value = completedRun
            viewModelScope.launch {
                answers.forEach { (index, answer) -> repository.saveAnswer(run.pack.id, index, answer) }
                repository.recordBrainPackFinished(run.pack.id)
            }
        } else {
            _activeRun.value = run.copy(currentAnswers = answers, currentIndex = run.currentIndex + 1)
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
            repository.recordBrainPackFinished(run.pack.id)
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
        if (targetIndex >= total - 1) {
            _activeRun.value = run.copy(currentIndex = targetIndex, currentAnswers = answers, isFinished = true)
            viewModelScope.launch {
                answers.forEach { (i, ans) -> repository.saveAnswer(run.pack.id, i, ans) }
                repository.recordBrainPackFinished(run.pack.id)
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

    fun setBrainChatMode(enabled: Boolean) {
        _isBrainChatMode.value = enabled
    }

    fun setSuggestionFeedback(suggestionId: String, feedback: String) {
        viewModelScope.launch {
            val suggestions = repository.getAllSuggestions()
            val suggestion = suggestions.find { it.id == suggestionId }
            if (suggestion != null) {
                repository.updateSuggestion(suggestion.copy(feedback = feedback))
                if (feedback == "liked") {
                    showToast("Gefällt mir markiert ❤️")
                } else if (feedback == "disliked") {
                    showToast("Ausgeblendet 🚫")
                } else if (feedback == "done") {
                    showToast("Als erledigt markiert 🎉")
                }
            }
        }
    }

    fun saveSuggestionToNotes(suggestion: BrainChatSuggestionItem) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            val categories = memoryRepository.categories.firstOrNull().orEmpty()
            val targetCategoryId = categories.find {
                it.systemKey?.contains("Idee", ignoreCase = true) == true ||
                it.customName?.contains("Idee", ignoreCase = true) == true
            }?.id ?: MemoryDefaults.IDEAS_ID

            val entry = MemoryEntryEntity(
                id = java.util.UUID.randomUUID().toString(),
                categoryId = targetCategoryId,
                kind = MemoryEntryKind.NOTE,
                title = suggestion.title,
                body = suggestion.description,
                previewImageUrl = suggestion.imageUrl,
                previewTitle = suggestion.title,
                previewDescription = suggestion.description,
                url = suggestion.linkUrl,
                createdAt = now,
                updatedAt = now
            )

            memoryRepository.insertEntries(listOf(entry))

            // Update suggestion state in brain messages
            _brainMessages.value = _brainMessages.value.map { msg ->
                val updatedSuggestions = msg.suggestions.map { s ->
                    if (s.id == suggestion.id) s.copy(isSavedToNotes = true) else s
                }
                msg.copy(suggestions = updatedSuggestions)
            }

            showToast("In Notizen & Bucket List gespeichert! 📌")
        }
    }

    fun sendVoiceBrainMessage(audioPath: String, durationSeconds: Int) {
        if (audioPath.isBlank()) return
        val userMsg = com.example.data.model.BrainMessage(
            text = "🎙️ Sprachnachricht ($durationSeconds Sek.)",
            sender = "user",
            audioPath = audioPath,
            audioDurationSeconds = durationSeconds
        )
        _brainMessages.value = _brainMessages.value + userMsg

        _isBrainGenerating.value = true
        val thinkingId = UUID.randomUUID().toString()
        val thinkingMsg = com.example.data.model.BrainMessage(
            id = thinkingId,
            text = "...",
            sender = "brain",
            isSearching = true
        )
        _brainMessages.value = _brainMessages.value + thinkingMsg

        viewModelScope.launch {
            val audioFile = File(audioPath)
            val transcriptionResult = GeminiAudioTranscriber.transcribeAudioFile(audioFile, _appLanguage.value)
            val queryText = transcriptionResult.getOrDefault("")

            if (queryText.isNotBlank()) {
                // Execute brain query with the transcribed speech
                executeBrainQuery(queryText, thinkingId)
            } else {
                _brainMessages.value = _brainMessages.value.filter { it.id != thinkingId }
                val replyMsg = com.example.data.model.BrainMessage(
                    text = "Ich habe deine Sprachnachricht empfangen. Wie kann ich dir und deinem Schatz heute helfen? Frage mich gerne nach Date-Ideen, Restaurants oder gemeinsamen Aktivitäten!",
                    sender = "brain"
                )
                _brainMessages.value = _brainMessages.value + replyMsg
                _isBrainGenerating.value = false
            }
        }
    }

    fun sendBrainMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = com.example.data.model.BrainMessage(text = text, sender = "user")
        _brainMessages.value = _brainMessages.value + userMsg

        _isBrainGenerating.value = true
        val thinkingId = UUID.randomUUID().toString()
        val thinkingMsg = com.example.data.model.BrainMessage(id = thinkingId, text = "...", sender = "brain", isSearching = true)
        _brainMessages.value = _brainMessages.value + thinkingMsg

        viewModelScope.launch {
            executeBrainQuery(text, thinkingId)
        }
    }

    private fun needsBrainWebSearch(query: String): Boolean {
        val q = query.lowercase(java.util.Locale.getDefault())
        val localIntent = listOf(
            "restaurant", "restaurants", "café", "cafe", "bar", "brunch", "essen",
            "in berlin", "wedding", "tiergarten", "nürnberg", "in der nähe", "nähe",
            "adresse", "öffnungszeiten", "heute", "jetzt", "maps", "google maps",
            "hotel", "kino", "veranstaltung", "wo können wir"
        )
        return localIntent.any(q::contains)
    }

    private suspend fun executeBrainQuery(queryText: String, thinkingId: String) {
        val isSearch = needsBrainWebSearch(queryText)
        try {
            val interests = repository.getAllInterests()
            val profile = uiState.value.profile

            if (isSearch) {
                // 1. Build compact, privacy-safe local Room context via HarmonyContextBuilder for search
                val brainContext = brainRepository.buildBrainContext(
                    task = "search",
                    query = queryText,
                    userName = profile.userName,
                    partnerName = profile.partnerName
                )

                // 2. Call Supabase Edge Function in mode = "search"
                val edgeResult = SupabaseHarmonyBrainGateway.getInstance().search(
                    query = queryText,
                    context = brainContext
                )

                _brainMessages.value = _brainMessages.value.filter { it.id != thinkingId }

                if (edgeResult.ok && !edgeResult.answer.isNullOrBlank()) {
                    val replyMsg = com.example.data.model.BrainMessage(
                        text = edgeResult.answer,
                        sender = "brain",
                        sources = emptyList(),
                        searchQueries = edgeResult.searchQueries,
                        suggestions = emptyList(),
                        animateOnArrival = true
                    )
                    _brainMessages.value = _brainMessages.value + replyMsg
                } else {
                    _brainMessages.value = _brainMessages.value + com.example.data.model.BrainMessage(
                        text = "Die Suche ist gerade kurz nicht erreichbar. Bitte versuche es gleich noch einmal.",
                        sender = "brain",
                        errorType = "search_error",
                        animateOnArrival = true
                    )
                }
            } else {
                // Regular Chat Mode via Supabase Edge Function
                val brainContext = brainRepository.buildBrainContext(
                    task = "chat",
                    query = queryText,
                    userName = profile.userName,
                    partnerName = profile.partnerName
                )

                val edgeResult = SupabaseHarmonyBrainGateway.getInstance().chat(
                    query = queryText,
                    context = brainContext,
                    useCurrentInfo = false
                )

                _brainMessages.value = _brainMessages.value.filter { it.id != thinkingId }

                if (edgeResult.ok && !edgeResult.answer.isNullOrBlank()) {
                    val answer = edgeResult.answer
                    val replyMsg = com.example.data.model.BrainMessage(
                        text = answer,
                        sender = "brain",
                        animateOnArrival = true
                    )
                    _brainMessages.value = _brainMessages.value + replyMsg
                } else {
                    // Fallback to Gemini with Grounding or offline relationship engine
                    val recentEntries = memoryRepository.entries.firstOrNull().orEmpty()
                    val recentNotesStrings = recentEntries.take(5).map { "${it.title}: ${it.body}" }

                    val geminiResult = GeminiBrainGateway.queryBrain(
                        userQuery = queryText,
                        profile = profile,
                        interests = interests,
                        recentNotes = recentNotesStrings,
                        appLanguage = _appLanguage.value
                    )

                    if (geminiResult.ok && geminiResult.answer.isNotBlank()) {
                        val replyMsg = com.example.data.model.BrainMessage(
                            text = geminiResult.answer,
                            sender = "brain",
                            sources = geminiResult.sources,
                            searchQueries = geminiResult.searchQueries,
                            animateOnArrival = true
                        )
                        _brainMessages.value = _brainMessages.value + replyMsg
                    } else {
                        val fallbackText = generateOfflineBrainReply(queryText, interests)
                        val replyMsg = com.example.data.model.BrainMessage(
                            text = fallbackText,
                            sender = "brain",
                            errorType = "offline",
                            animateOnArrival = true
                        )
                        _brainMessages.value = _brainMessages.value + replyMsg
                    }
                }
            }
        } catch (e: Exception) {
            _brainMessages.value = _brainMessages.value.filter { it.id != thinkingId }
            if (isSearch) {
                val replyMsg = com.example.data.model.BrainMessage(
                    text = "Ich konnte gerade keinen verlässlichen Treffer in deiner Nähe finden.",
                    sender = "brain",
                    errorType = "search_exception",
                    animateOnArrival = true
                )
                _brainMessages.value = _brainMessages.value + replyMsg
            } else {
                val interests = repository.getAllInterests()
                val fallbackText = generateOfflineBrainReply(queryText, interests)
                val replyMsg = com.example.data.model.BrainMessage(
                    text = fallbackText,
                    sender = "brain",
                    errorType = "exception",
                    animateOnArrival = true
                )
                _brainMessages.value = _brainMessages.value + replyMsg
            }
        } finally {
            _isBrainGenerating.value = false
        }
    }

    private fun generateOfflineBrainReply(text: String, interests: List<com.example.data.model.BrainInterestEntity>): String {
        val queryLower = text.lowercase()
        return when {
            queryLower.contains("essen") || queryLower.contains("restaurant") || queryLower.contains("hunger") || queryLower.contains("sushi") || queryLower.contains("pizza") || queryLower.contains("café") || queryLower.contains("cafe") -> {
                "Hier sind leckere Restaurant- und Genuss-Ideen für euch zwei:\n\n" +
                "1. **Trattoria Bella Vista 🍝**\n" +
                "Kuscheliges italienisches Restaurant mit hausgemachter Pasta, Steinofenpizza und erlesenen Weinen.\n" +
                "[Auf Google Maps suchen](https://www.google.com/maps/search/?api=1&query=Italienisches+Restaurant)\n\n" +
                "2. **Café Am Schlosspark ☕**\n" +
                "Perfekt für einen gemütlichen Sonntagsbrunch mit frischem Gebäck und herrlicher Aussicht ins Grüne.\n" +
                "[Auf Google Maps suchen](https://www.google.com/maps/search/?api=1&query=Cafe+Brunch)"
            }
            queryLower.contains("interesse") || queryLower.contains("gemeinsam") -> {
                buildString {
                    append("Eure erkannten gemeinsamen Interessen:\n\n")
                    interests.forEach {
                        append("🧩 **${it.name}** (${it.confidence})\n")
                        append("  _${it.reason}_\n\n")
                    }
                }
            }
            queryLower.contains("date") || queryLower.contains("idee") || queryLower.contains("vorschlag") -> {
                "Hier sind wundervolle Date-Ideen basierend auf euren Vorlieben:\n\n" +
                "1. **Romantisches Sonnenuntergangs-Picknick 🧺**\n" +
                "Packt eure Lieblingssnacks, eine kuschelige Decke und genießt den Sonnenuntergang bei guter Musik.\n\n" +
                "2. **Klassischer Filmabend & Deckenburg 🎬**\n" +
                "Macht es euch auf der Couch gemütlich, schaltet das Licht aus und baut eine kuschelige Festung aus Decken. Popcorn ist Pflicht!"
            }
            queryLower.contains("reise") || queryLower.contains("urlaub") || queryLower.contains("ausflug") -> {
                "Hier sind inspirierende Ausflugs- und Reiseziele:\n\n" +
                "1. **Ausflug an den See mit Bootsfahrt 🛶**\n" +
                "Frische Luft, Sonnenschein und ein entspanntes Picknick am Ufer.\n\n" +
                "2. **Städtetrip mit Altstadt-Spaziergang 🏛️**\n" +
                "Neue Gassen entdecken, Kunstgalerien besuchen und leckeres Eis schlemmen."
            }
            else -> {
                "Ich habe eure Anfrage empfangen! Ich kann euch Date-Ideen mit Fotos vorschlagen, Restaurants heraussuchen oder eure gemeinsamen Interessen analysieren. Probiert doch einen der Schnellvorschläge unten aus!"
            }
        }
    }

    fun resetBrainChat() {
        _brainMessages.value = listOf(
            com.example.data.model.BrainMessage(
                text = "Hallo! Ich bin euer Harmony Brain 🧠. Ich plane mit euch die schönsten Dates, finde leckere Restaurants und analysiere eure gemeinsamen Interessen mit Bildvorschlägen und Google Maps Verknüpfung.\n\nIhr könnt mir auch gerne eine **Sprachnachricht 🎙️** schicken!",
                sender = "brain"
            )
        )
    }

    fun answerBrainQuestion(questionId: String, answerText: String) {
        viewModelScope.launch {
            val questions = repository.getAllQuestions()
            val q = questions.find { it.id == questionId }
            if (q != null) {
                repository.updateQuestion(q.copy(answered = true, answerText = answerText))
                showToast("Antwort im Paarprofil gespeichert! 🧠")
            }
        }
    }
}
