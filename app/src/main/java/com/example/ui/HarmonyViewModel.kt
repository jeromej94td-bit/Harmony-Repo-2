package com.example.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.DeveloperDataManager
import com.example.data.DriveTotAssetInstaller
import com.example.data.db.HarmonyDatabase
import com.example.data.model.AnswerEntity
import com.example.data.model.ChatMessageEntity
import com.example.data.model.CoupleStatsEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.HarmonyPacksData
import com.example.data.model.MomentEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.QuestionPack
import com.example.data.model.SharedPicEntity
import com.example.data.repository.HarmonyRepository
import com.example.ui.components.TotImageProvider
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Random

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
    val isBrainGenerating: Boolean = false
)

class HarmonyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = HarmonyDatabase.getInstance(application)
    private val repository = HarmonyRepository(db, application)

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
        _isBrainGenerating
    ) { arrayOfValues ->
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
            isBrainGenerating = arrayOfValues[25] as Boolean
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
            runCatching { com.example.data.SupabaseSync.fetchAndSync() }
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

                    val existingQuestions = repository.getAllQuestions()
                    if (existingQuestions.isEmpty()) {
                        val questions = com.example.data.HarmonyBrainEngine.generateQuestions()
                        repository.saveQuestions(questions)
                    }
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
            com.example.data.SupabaseSync.fetchAndSync()
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
        val pack = HarmonyPacksData.PACKS.find { it.id == packId } ?: return
        val currentAnswers = uiState.value.answers.filter { it.packId == packId }
            .associate { it.questionIndex to it.answerText }
        _activeRun.value = ActivePackRun(
            pack = pack,
            currentIndex = 0,
            currentAnswers = currentAnswers,
            isFinished = false
        )
    }

    fun pickAnswer(optionText: String) {
        val run = _activeRun.value ?: return
        val updatedAnswers = run.currentAnswers.toMutableMap()
        updatedAnswers[run.currentIndex] = optionText
        _activeRun.value = run.copy(currentAnswers = updatedAnswers)
        nextStep()
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
        if (run != null) {
            viewModelScope.launch {
                run.currentAnswers.forEach { (index, answerText) ->
                    repository.saveAnswer(run.pack.id, index, answerText)
                }
                _activeRun.value = null
                _isExitConfirmOpen.value = false
            }
        } else {
            _activeRun.value = null
            _isExitConfirmOpen.value = false
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

    fun saveOwnAnswer(answerText: String) {
        val run = _activeRun.value ?: return
        val idx = _ownAnswerTargetIndex.value ?: run.currentIndex
        val updatedAnswers = run.currentAnswers.toMutableMap()
        updatedAnswers[idx] = answerText
        _activeRun.value = run.copy(currentAnswers = updatedAnswers)
        closeOwnAnswerDialog()
        nextStep()
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
                val reply = SIM_REPLIES[Random().nextInt(SIM_REPLIES.size)]
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

    fun addMoment(title: String, content: String) {
        if (title.isBlank() || content.isBlank()) return
        viewModelScope.launch {
            repository.addMoment(title, content)
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

    fun sendBrainMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = com.example.data.model.BrainMessage(text = text, sender = "user")
        _brainMessages.value = _brainMessages.value + userMsg

        _isBrainGenerating.value = true
        val thinkingId = java.util.UUID.randomUUID().toString()
        val thinkingMsg = com.example.data.model.BrainMessage(id = thinkingId, text = "...", sender = "brain", isSearching = true)
        _brainMessages.value = _brainMessages.value + thinkingMsg

        viewModelScope.launch {
            try {
                val gateway = com.example.data.SupabaseBrainGateway.getInstance()
                val interests = repository.getAllInterests()
                val profile = uiState.value.profile
                val localContextText = buildString {
                    append("Paar-Kontext (lokal):\n")
                    append("- Name: ${profile.userName}, Partner: ${profile.partnerName}\n")
                    append("- Gemeinsame Interessen:\n")
                    interests.filter { it.confidence == "sicher" }.forEach {
                        append("  * ${it.name} (Sicher, weil: ${it.reason})\n")
                    }
                    interests.filter { it.confidence == "wahrscheinlich" }.forEach {
                        append("  * ${it.name} (Wahrscheinlich)\n")
                    }
                }

                val isFoodQuery = text.lowercase().let { q ->
                    q.contains("essen") || q.contains("restaurant") || q.contains("hunger") || 
                    q.contains("gericht") || q.contains("speise") || q.contains("sushi") || 
                    q.contains("pizza") || q.contains("café") || q.contains("cafe") || 
                    q.contains("küche") || q.contains("kuche") || q.contains("lecker") || 
                    q.contains("dinner") || q.contains("frühstück") || q.contains("lunch") || 
                    q.contains("bistro") || q.contains("bar") || q.contains("kneipe")
                }
                
                val foodInstructionText = if (isFoodQuery) {
                    "\n\n[WICHTIGE SYSTEMINSTRUKTION FÜR ESSEN/RESTAURANTS: Halte deine Antwort extrem kompakt und übersichtlich. Nenne den Namen des Restaurants/Ortes oder des Gerichts zwingend in Fettschrift (z. B. **Restaurant Name**). Gib immer eine konkrete Adresse an. Füge zwingend einen Google Maps Suchlink hinzu (Format: [Auf Google Maps anzeigen](https://www.google.com/maps/search/?api=1&query=NAME_UND_ADRESSE_URL_ENCODED)).]"
                } else ""

                val enrichedQuery = "$localContextText\nNutzer-Anfrage:\n$text$foodInstructionText"
                val response = gateway.search(enrichedQuery)

                _brainMessages.value = _brainMessages.value.filter { it.id != thinkingId }

                if (response.ok && response.answer != null) {
                    val replyMsg = com.example.data.model.BrainMessage(
                        text = response.answer,
                        sender = "brain",
                        sources = response.sources,
                        searchQueries = response.searchQueries
                    )
                    _brainMessages.value = _brainMessages.value + replyMsg
                } else {
                    val fallbackText = generateOfflineBrainReply(text, interests)
                    val replyMsg = com.example.data.model.BrainMessage(
                        text = "⚠️ [Offline-Modus] ${response.errorMessage ?: "Konnte das Web-Brain nicht erreichen."}\n\n$fallbackText",
                        sender = "brain",
                        errorType = response.errorType
                    )
                    _brainMessages.value = _brainMessages.value + replyMsg
                }
            } catch (e: Exception) {
                _brainMessages.value = _brainMessages.value.filter { it.id != thinkingId }
                val replyMsg = com.example.data.model.BrainMessage(
                    text = "⚠️ [Offline-Modus] Ein unerwarteter Fehler ist aufgetreten: ${e.localizedMessage}",
                    sender = "brain",
                    errorType = "exception"
                )
                _brainMessages.value = _brainMessages.value + replyMsg
            } finally {
                _isBrainGenerating.value = false
            }
        }
    }

    private fun generateOfflineBrainReply(text: String, interests: List<com.example.data.model.BrainInterestEntity>): String {
        val queryLower = text.lowercase()
        return when {
            queryLower.contains("essen") || queryLower.contains("restaurant") || queryLower.contains("hunger") || queryLower.contains("sushi") || queryLower.contains("pizza") || queryLower.contains("café") || queryLower.contains("cafe") -> {
                "Hier ist eine schnelle Empfehlung im Offline-Modus:\n\n" +
                "🍕 **Trattoria Bella Vista**\n" +
                "Adresse: Hauptstraße 12, 10115 Berlin\n" +
                "[Auf Google Maps anzeigen](https://www.google.com/maps/search/?api=1&query=Trattoria+Bella+Vista+Hauptstrasse+12+Berlin)\n\n" +
                "*(Hinweis: Für tagesaktuelle, personalisierte Restaurant-Empfehlungen in deiner Nähe, aktiviere bitte die Internetverbindung!)*"
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
                "Hier ist eine gemütliche Date-Idee basierend auf euren Interessen:\n\n" +
                "🎬 **Klassischer Filmabend & Deckenburg**\n" +
                "Macht es euch auf der Couch gemütlich, schaltet das Licht aus und baut eine kuschelige Festung aus Decken. Popcorn ist Pflicht!\n\n" +
                "*(Tipp: Für personalisierte, tagesaktuelle Events in eurer Nähe, aktiviere bitte die Internetverbindung.)*"
            }
            queryLower.contains("reise") || queryLower.contains("urlaub") -> {
                "Für euren nächsten Urlaub solltet ihr euch folgendes überlegen:\n\n" +
                "🏕️ **Natur-Ausflug oder gemütlicher Strandtag**\n" +
                "Basierend auf eurer Entweder-Oder-Wahl scheint ihr die perfekte Balance aus Entspannung und frischer Luft zu lieben. Wie wäre es mit einem gemütlichen See in eurer Nähe?"
            }
            else -> {
                "Ich habe eure Anfrage empfangen! Im Offline-Modus kann ich euch eure gemeinsamen Interessen aufzeigen (tippe 'Interessen') oder Date-Ideen vorschlagen (tippe 'Date-Ideen'). Für freie Fragen und Websuche ist eine Internetverbindung erforderlich."
            }
        }
    }

    fun resetBrainChat() {
        _brainMessages.value = listOf(
            com.example.data.model.BrainMessage(
                text = "Hallo! Ich bin euer Harmony Brain 🧠. Ich analysiere eure gemeinsamen Antworten und helfe euch dabei, noch tiefere Gemeinsamkeiten und neue Interessen zu entdecken.\n\nFragt mich gerne nach Date-Ideen, euren gemeinsamen Interessen oder wie ihr eure Unterschiede feiern könnt!",
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
