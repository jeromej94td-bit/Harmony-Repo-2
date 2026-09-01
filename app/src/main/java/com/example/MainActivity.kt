package com.example

import android.content.Intent
import android.os.Bundle
import android.graphics.Color as AndroidColor
import androidx.activity.compose.BackHandler
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel as composeViewModel
import com.example.data.DeveloperDataManager
import com.example.data.DevExporter
import com.example.data.OkHttpLinkPreviewResolver
import com.example.data.db.HarmonyDatabase
import com.example.data.model.HarmonyPacksData
import com.example.data.model.MemoryEntryKind
import com.example.data.model.ProposalExperienceEntryPolicy
import com.example.data.repository.RoomMemoryRepository
import com.example.ui.AppLanguage
import com.example.ui.HarmonyViewModel
import com.example.ui.LocalAppLanguage
import com.example.ui.nextStep
import com.example.ui.pickAnswer
import com.example.ui.skipCurrentQuestion
import com.example.ui.memory.MemoryEditorMode
import com.example.ui.memory.MemoryTab
import com.example.ui.memory.MemoryViewModel
import com.example.ui.memory.MemoryViewModelFactory
import com.example.ui.components.AmbientBackground
import com.example.ui.components.HarmonyBottomNav
import com.example.ui.components.HarmonyToast
import com.example.ui.components.HarmonyTopBar
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.DevStudioScreen
import com.example.ui.screens.GamesScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.IntrospectionExperienceScreen
import com.example.ui.screens.LiveChangeEditor
import com.example.ui.screens.LiveChangeHud
import com.example.ui.screens.LiveChangeLauncher
import com.example.ui.screens.MomentsScreen
import com.example.ui.screens.MemoryEditorSheet
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.PackListScreen
import com.example.ui.screens.PackResultsScreen
import com.example.ui.screens.PANDA_EITHER_OR_PACK_ID
import com.example.ui.screens.PandaEitherOrScreen
import com.example.ui.screens.ProfileSheet
import com.example.ui.screens.ProposalExperienceScreen
import com.example.ui.screens.QuizRunnerScreen
import com.example.ui.screens.RunnerSkipButton
import com.example.ui.screens.hasCompletePackResults
import com.example.ui.screens.liveChangeLongPressObserver
import com.example.ui.theme.HarmonyTheme
import com.example.widget.MemoryWidgetDatabaseObserver
import com.example.widget.MemoryWidgetOpenRequest
import com.example.widget.PicShareWidgetProvider
import com.example.widget.parseMemoryWidgetOpenRequest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: HarmonyViewModel by viewModels()
    private var memoryWidgetOpenRequest by mutableStateOf<MemoryWidgetOpenRequest?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryWidgetOpenRequest = parseMemoryWidgetOpenRequest(intent)
        if (intent.getIntExtra("open_tab", -1) == 1) {
            viewModel.selectTab(1)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (androidx.core.content.ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) {
                androidx.core.app.ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1001
                )
            }
        }
        MemoryWidgetDatabaseObserver.install(applicationContext)
        enableEdgeToEdge()
        window.navigationBarColor = AndroidColor.TRANSPARENT
        window.statusBarColor = AndroidColor.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).apply {
            isAppearanceLightNavigationBars = false
            isAppearanceLightStatusBars = false
        }
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            val currentLanguage = AppLanguage.fromCode(uiState.appLanguage)
            CompositionLocalProvider(
                LocalAppLanguage provides currentLanguage,
                LocalLayoutDirection provides if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                HarmonyTheme(darkTheme = uiState.isDarkMode) {
                    HarmonyApp(
                        viewModel = viewModel,
                        memoryWidgetOpenRequest = memoryWidgetOpenRequest,
                        onMemoryWidgetRequestConsumed = { memoryWidgetOpenRequest = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        memoryWidgetOpenRequest = parseMemoryWidgetOpenRequest(intent)
        if (intent.getIntExtra("open_tab", -1) == 1) {
            viewModel.selectTab(1)
        }
    }
}

@OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
fun HarmonyApp(
    viewModel: HarmonyViewModel,
    memoryWidgetOpenRequest: MemoryWidgetOpenRequest? = null,
    onMemoryWidgetRequestConsumed: () -> Unit = {}
) {
    var isAuthenticated by androidx.compose.runtime.saveable.rememberSaveable { androidx.compose.runtime.mutableStateOf(false) }

    if (!isAuthenticated) {
        com.example.ui.screens.AuthScreen(
            onAuthSuccess = { isAuthenticated = true }
        )
        return
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val isImeVisible = WindowInsets.isImeVisible
    val runnerScope = rememberCoroutineScope()
    val appDb = remember(context.applicationContext) {
        HarmonyDatabase.getInstance(context.applicationContext)
    }
    val memoryRepository = remember(context.applicationContext) {
        RoomMemoryRepository(appDb)
    }
    val memoryFactory = remember(memoryRepository) {
        MemoryViewModelFactory(
            repository = memoryRepository,
            linkPreviewResolver = OkHttpLinkPreviewResolver()
        )
    }
    val memoryViewModel: MemoryViewModel = composeViewModel(factory = memoryFactory)
    val memoryState by memoryViewModel.uiState.collectAsStateWithLifecycle()
    var isIntrospectionOpen by rememberSaveable { mutableStateOf(false) }
    var isPandaEitherOrOpen by rememberSaveable { mutableStateOf(false) }
    var isPandaExitConfirmOpen by rememberSaveable { mutableStateOf(false) }
    var isSpecialFlowExitConfirmOpen by remember { mutableStateOf(false) }
    var isProposalExperienceOpen by rememberSaveable { mutableStateOf(false) }
    var isLiveChangeMode by remember { mutableStateOf(false) }
    var isLiveChangeEditorOpen by remember { mutableStateOf(false) }
    var isLiveChangeLauncherVisible by remember { mutableStateOf(true) }
    var liveChangeCount by remember { mutableStateOf(0) }
    var resultsPackId by rememberSaveable { mutableStateOf<String?>(null) }

    LaunchedEffect(memoryWidgetOpenRequest) {
        val request = memoryWidgetOpenRequest ?: return@LaunchedEffect
        viewModel.selectTab(4)
        memoryViewModel.selectTab(MemoryTab.CURRENT)
        memoryViewModel.setCategoryFilter(null)
        memoryViewModel.setQuery("")

        val entryId = request.entryId
        if (entryId == null) {
            memoryViewModel.closeEditor()
        } else {
            val entry = memoryRepository.getEntry(entryId)
            if (entry != null && entry.completedAt == null) {
                val mode = if (entry.kind == MemoryEntryKind.LINK) {
                    MemoryEditorMode.LINK
                } else {
                    MemoryEditorMode.NOTE
                }
                memoryViewModel.openEditor(mode, entry.id)
            } else {
                memoryViewModel.closeEditor()
            }
        }
        onMemoryWidgetRequestConsumed()
    }

    fun openPackForPlay(packId: String, freshRun: Boolean = false) {
        resultsPackId = null
        when {
            packId == PANDA_EITHER_OR_PACK_ID -> {
                isPandaEitherOrOpen = true
                isPandaExitConfirmOpen = false
            }
            ProposalExperienceEntryPolicy.handlesPack(packId) -> {
                isProposalExperienceOpen = true
            }
            freshRun -> {
                val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == packId }
                    ?.let { com.example.data.model.LoveBalanceQuestionPolicy.ensureHappyCoupleFirst(it) }
                    ?: return
                runnerScope.launch {
                    appDb.answerDao().deleteAnswersForPack(packId)
                    appDb.brainRoomDao().clearFinishedPack(packId)
                    viewModel.startPackForTest(pack, currentIndex = 0)
                }
            }
            else -> viewModel.startPack(packId)
        }
    }

    fun openPack(packId: String) {
        if (packId == PANDA_EITHER_OR_PACK_ID || ProposalExperienceEntryPolicy.handlesPack(packId)) {
            openPackForPlay(packId)
            return
        }
        val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == packId } ?: return
        if (hasCompletePackResults(pack, uiState.answers)) {
            resultsPackId = packId
            return
        }
        runnerScope.launch {
            if (appDb.brainRoomDao().hasFinishedPack(packId)) {
                resultsPackId = packId
            } else {
                openPackForPlay(packId)
            }
        }
    }

    val isQuizActive = uiState.activeRun != null
    val isResultsOpen = resultsPackId != null
    val isMemoryOverlayActive = memoryState.editorMode != null ||
        memoryState.pendingDeleteEntryIds.isNotEmpty() || memoryState.selectionMode
    val isSheetOrDialogActive = uiState.isProfileSheetOpen || uiState.isAddMomentOpen || isMemoryOverlayActive
    val isNotHomeTab = uiState.selectedTab != 0

    val canHandleBack = isResultsOpen || isIntrospectionOpen || isPandaEitherOrOpen || isProposalExperienceOpen || isQuizActive || isSheetOrDialogActive || isNotHomeTab

    BackHandler(enabled = canHandleBack || isLiveChangeEditorOpen) {
        when {
            isLiveChangeEditorOpen -> {
                isLiveChangeEditorOpen = false
            }
            isResultsOpen -> {
                resultsPackId = null
            }
            isIntrospectionOpen -> {
                // IntrospectionExperienceScreen handles back so it can show its own leave dialog.
            }
            isPandaEitherOrOpen -> {
                isPandaExitConfirmOpen = true
            }
            isProposalExperienceOpen -> {
                isSpecialFlowExitConfirmOpen = true
            }
            isQuizActive -> {
                if (uiState.isExitConfirmOpen) {
                    viewModel.closeExitConfirm()
                } else if (uiState.isOwnAnswerDialogOpen) {
                    viewModel.closeOwnAnswerDialog()
                } else {
                    viewModel.askExitRun()
                }
            }
            uiState.isProfileSheetOpen -> {
                viewModel.closeProfileSheet()
            }
            uiState.isAddMomentOpen -> {
                viewModel.closeAddMomentDialog()
            }
            memoryState.pendingDeleteEntryIds.isNotEmpty() -> {
                memoryViewModel.dismissPermanentDelete()
            }
            memoryState.editorMode != null && isImeVisible -> {
                keyboardController?.hide()
            }
            memoryState.editorMode != null -> {
                memoryViewModel.closeEditor()
            }
            memoryState.selectionMode -> {
                memoryViewModel.clearSelection()
            }
            uiState.selectedTab == 6 -> { // PackListScreen
                viewModel.selectTab(1) // Back to GamesScreen
            }
            uiState.selectedTab != 0 -> {
                viewModel.selectTab(0) // Back to HomeScreen
            }
        }
    }

    AmbientBackground {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .liveChangeLongPressObserver(
                    enabled = isLiveChangeMode &&
                        isQuizActive &&
                        !uiState.isExitConfirmOpen &&
                        !uiState.isOwnAnswerDialogOpen &&
                        !isLiveChangeEditorOpen,
                    onLongPress = { isLiveChangeEditorOpen = true }
                ),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            topBar = {
                if (!isResultsOpen && !isQuizActive && !isIntrospectionOpen && !isPandaEitherOrOpen && !isProposalExperienceOpen) {
                    HarmonyTopBar(
                        userName = uiState.profile.userName,
                        partnerName = uiState.profile.partnerName,
                        userAvatarPath = uiState.profile.userAvatarPath,
                        partnerAvatarPath = uiState.profile.partnerAvatarPath,
                        onProfileClick = { viewModel.openProfileSheet() },
                        onRefresh = { viewModel.refreshData() },
                        showMemoryMark = uiState.selectedTab == 4
                    )
                }
            },
            bottomBar = {
                if (!isResultsOpen && !isQuizActive && !isIntrospectionOpen && !isPandaEitherOrOpen && !isProposalExperienceOpen) {
                    val navSelectedTab = when (uiState.selectedTab) {
                        6 -> 1 // When inside PackListScreen, highlight Spiele tab
                        else -> uiState.selectedTab
                    }
                    HarmonyBottomNav(
                        selectedTab = navSelectedTab,
                        onTabSelected = { tab -> viewModel.selectTab(tab) },
                        appLanguage = uiState.appLanguage
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Main Content depending on selected Tab
                when (uiState.selectedTab) {
                    0 -> HomeScreen(
                        profile = uiState.profile,
                        answers = uiState.answers,
                        sharedPics = uiState.sharedPics,
                        stats = uiState.stats,
                        appLanguage = uiState.appLanguage,
                        onStartPack = { packId -> openPack(packId) },
                        onAddSharedPictures = { uris, addedBy -> viewModel.addSharedPictures(uris, addedBy) },
                        onUpdateSharedPicture = { pic -> viewModel.updateSharedPicture(pic) },
                        onPinWidget = {
                            val requested = PicShareWidgetProvider.requestPin(context)
                            viewModel.showToast(if (requested) "Widget-Auswahl geöffnet" else "Widget bitte über den Startbildschirm hinzufügen")
                        },
                        brainInterests = uiState.brainInterests,
                        brainSuggestions = uiState.brainSuggestions,
                        brainQuestions = uiState.brainQuestions,
                        onSuggestionFeedback = { id, feedback -> viewModel.setSuggestionFeedback(id, feedback) },
                        onAnswerBrainQuestion = { id, text -> viewModel.answerBrainQuestion(id, text) },
                        onOpenBrainChat = {
                            viewModel.setBrainChatMode(true)
                            viewModel.selectTab(2)
                        }
                    )

                    1 -> GamesScreen(
                        answers = uiState.answers,
                        packFilter = uiState.packFilter,
                        generatedGames = uiState.generatedGames,
                        appLanguage = uiState.appLanguage,
                        onSetFilter = { filter -> viewModel.setPackFilter(filter) },
                        onCategoryClick = { catId ->
                            if (catId == "unterbewusstsein") {
                                isIntrospectionOpen = true
                            } else {
                                viewModel.openCategory(catId)
                            }
                        },
                        onTopicClick = { topicId -> viewModel.openTopic(topicId) },
                        onStartPack = { packId -> openPack(packId) },
                        onStartGeneratedGame = { gameId -> viewModel.startGeneratedGame(gameId) }
                    )

                    2 -> ChatScreen(
                        messages = uiState.messages,
                        partnerName = uiState.profile.partnerName,
                        partnerAvatarPath = uiState.profile.partnerAvatarPath,
                        appLanguage = uiState.appLanguage,
                        onSendMessage = { text -> viewModel.sendChatMessage(text) },
                        onSendImage = { uri -> viewModel.sendChatImage(uri) },
                        onReportUser = { viewModel.reportPartner() },
                        isBrainChatMode = uiState.isBrainChatMode,
                        isBrainGenerating = uiState.isBrainGenerating,
                        brainMessages = uiState.brainMessages,
                        onToggleBrainChatMode = { enabled -> viewModel.setBrainChatMode(enabled) },
                        onSendBrainMessage = { text -> viewModel.sendBrainMessage(text) },
                        onResetBrainChat = { viewModel.resetBrainChat() },
                        onSendVoiceMessage = { path, duration -> viewModel.sendVoiceChatMessage(path, duration) },
                        onSendVoiceBrainMessage = { path, duration -> viewModel.sendVoiceBrainMessage(path, duration) },
                        onSaveSuggestionToNotes = { suggestion -> viewModel.saveSuggestionToNotes(suggestion) },
                        onSuggestionFeedback = { id, feedback -> viewModel.setSuggestionFeedback(id, feedback) }
                    )

                    3 -> MomentsScreen(
                        moments = uiState.moments,
                        profile = uiState.profile,
                        isAddMomentOpen = uiState.isAddMomentOpen,
                        appLanguage = uiState.appLanguage,
                        onOpenAddMoment = { viewModel.openAddMomentDialog() },
                        onCloseAddMoment = { viewModel.closeAddMomentDialog() },
                        onAddMoment = { title, content, imageUris -> viewModel.addMoment(title, content, imageUris) }
                    )

                    4 -> {
                        MemoryScreen(
                            state = memoryState,
                            appLanguage = uiState.appLanguage,
                            userName = uiState.profile.userName,
                            partnerName = uiState.profile.partnerName,
                            userAvatarPath = uiState.profile.userAvatarPath,
                            partnerAvatarPath = uiState.profile.partnerAvatarPath,
                            onSelectTab = memoryViewModel::selectTab,
                            onQueryChange = memoryViewModel::setQuery,
                            onCategoryFilter = memoryViewModel::setCategoryFilter,
                            onOpenEditor = memoryViewModel::openEditor,
                            onStartSelection = memoryViewModel::startSelection,
                            onToggleEntrySelection = memoryViewModel::toggleEntrySelection,
                            onSelectAllEntries = memoryViewModel::selectAllVisibleEntries,
                            onClearSelection = memoryViewModel::clearSelection,
                            onDeleteSelectedRequest = memoryViewModel::requestSelectedDelete,
                            onComplete = memoryViewModel::complete,
                            onRestore = memoryViewModel::restore,
                            onRetryPreview = memoryViewModel::retryPreview,
                            onDeleteRequest = memoryViewModel::requestPermanentDelete,
                            onDeleteConfirm = memoryViewModel::confirmPermanentDelete,
                            onDeleteDismiss = memoryViewModel::dismissPermanentDelete,
                            onCreateCategory = memoryViewModel::createCategory,
                            onUpdateCategory = memoryViewModel::updateCategory,
                            onDeleteCategory = memoryViewModel::deleteCategory
                        )

                        memoryState.editorMode?.let { editorMode ->
                            MemoryEditorSheet(
                                mode = editorMode,
                                categories = memoryState.categories,
                                appLanguage = uiState.appLanguage,
                                onModeChange = { mode ->
                                    memoryViewModel.openEditor(mode, memoryState.editorEntryId)
                                },
                                onDismiss = memoryViewModel::closeEditor,
                                onSaveNote = { entryId, categoryId, title, body ->
                                    memoryViewModel.saveNote(entryId, categoryId, title, body)
                                    memoryViewModel.closeEditor()
                                },
                                onSaveList = { entryId, categoryId, title, items ->
                                    memoryViewModel.saveList(entryId, categoryId, title, items)
                                    memoryViewModel.closeEditor()
                                },
                                onSaveLink = { entryId, categoryId, url, note ->
                                    memoryViewModel.saveLink(entryId, categoryId, url, note)
                                    memoryViewModel.closeEditor()
                                },
                                initialEntry = memoryState.visibleEntries
                                    .firstOrNull { it.entity.id == memoryState.editorEntryId }
                                    ?.entity
                            )
                        }
                    }

                    5 -> DevStudioScreen(
                        answers = uiState.answers,
                        profile = uiState.profile,
                        onStartPack = { packId -> openPackForPlay(packId) },
                        onShowToast = { msg -> viewModel.showToast(msg) }
                    )

                    6 -> PackListScreen(
                        answers = uiState.answers,
                        selectedTopicId = uiState.selectedTopicId,
                        selectedCategoryId = uiState.selectedCategoryId,
                        packFilter = uiState.packFilter,
                        appLanguage = uiState.appLanguage,
                        onSetFilter = { filter -> viewModel.setPackFilter(filter) },
                        onStartPack = { packId -> openPack(packId) },
                        onClose = { viewModel.selectTab(1) }
                    )
                }

                if (uiState.selectedTab == 5 && !isQuizActive && !isLiveChangeMode && isLiveChangeLauncherVisible) {
                    LiveChangeLauncher(
                        onStart = {
                            isLiveChangeMode = true
                            isLiveChangeEditorOpen = false
                            viewModel.closeProfileSheet()
                            viewModel.selectTab(1)
                            viewModel.showToast("Live Change aktiv · Spiel öffnen und Frage gedrückt halten")
                        },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                    )
                }

                // Toast Notification Overlay
                HarmonyToast(
                    message = uiState.toastMessage,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                // Profile Sheet
                if (uiState.isProfileSheetOpen) {
                    val currentLanguage = AppLanguage.fromCode(uiState.appLanguage)
                    ProfileSheet(
                        profile = uiState.profile,
                        isEditProfileOpen = uiState.isEditProfileOpen,
                        isDarkMode = uiState.isDarkMode,
                        onToggleDarkMode = { enabled -> viewModel.toggleDarkMode(enabled) },
                        language = currentLanguage,
                        onLanguageChange = { lang -> viewModel.setLanguage(lang.code) },
                        onDismiss = { viewModel.closeProfileSheet() },
                        onToggleSimulator = { viewModel.toggleSimulator() },
                        onOpenEditProfile = { viewModel.openEditProfile() },
                        onCloseEditProfile = { viewModel.closeEditProfile() },
                        onSaveEditProfile = { u, p, s -> viewModel.saveEditProfile(u, p, s) },
                        onUpdateAvatar = { uri, isUser -> viewModel.updateProfileAvatar(uri, isUser) },
                        onOpenDevStudio = { viewModel.selectTab(5) },
                        onLogout = { isAuthenticated = false }
                    )
                }

                // Full-Screen Quiz Runner Overlay
                uiState.activeRun?.let { activeRun ->
                    QuizRunnerScreen(
                        activeRun = activeRun,
                        profile = uiState.profile,
                        isExitConfirmOpen = uiState.isExitConfirmOpen,
                        isOwnAnswerDialogOpen = uiState.isOwnAnswerDialogOpen,
                        appLanguage = uiState.appLanguage,
                        onPickAnswer = { optText ->
                            val skipLabel = com.example.util.LanguageManager.tr("Überspringen", uiState.appLanguage)
                            if (optText == skipLabel) {
                                viewModel.skipCurrentQuestion(expectedIndex = activeRun.currentIndex)
                            } else {
                                viewModel.pickAnswer(optText, expectedIndex = activeRun.currentIndex)
                            }
                        },
                        onPickTot = { optionText ->
                            viewModel.pickAnswer(optionText, expectedIndex = activeRun.currentIndex)
                        },
                        onNextStep = {
                            viewModel.nextStep(expectedIndex = activeRun.currentIndex)
                        },
                        onAskExit = { viewModel.previousStep() },
                        onCloseExitConfirm = { viewModel.closeExitConfirm() },
                        onCloseRunner = { viewModel.closeRunner() },
                        onOpenOwnAnswerDialog = { idx, mode ->
                            if (activeRun.pack.cat == "nie" && mode == null) {
                                viewModel.skipCurrentQuestion(expectedIndex = activeRun.currentIndex)
                            } else {
                                viewModel.openOwnAnswerDialog(idx, mode)
                            }
                        },
                        onCloseOwnAnswerDialog = { viewModel.closeOwnAnswerDialog() },
                        onSaveOwnAnswer = { ansText -> viewModel.saveOwnAnswer(ansText) }
                    )

                    val isHappyCoupleQuestion = activeRun.pack.id == com.example.data.model.LoveBalanceQuestionPolicy.PACK_ID && activeRun.currentIndex == 0
                    if (
                        !activeRun.isFinished &&
                        activeRun.pack.type != "disc" &&
                        activeRun.pack.cat != "nie" &&
                        !isHappyCoupleQuestion &&
                        !uiState.isExitConfirmOpen &&
                        !uiState.isOwnAnswerDialogOpen
                    ) {
                        RunnerSkipButton(
                            appLanguage = uiState.appLanguage,
                            onSkip = {
                                viewModel.skipCurrentQuestion(expectedIndex = activeRun.currentIndex)
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp, bottom = 14.dp)
                        )
                    }

                    if (
                        activeRun.pack.type == "tot" &&
                        !activeRun.isFinished &&
                        !uiState.isExitConfirmOpen &&
                        !uiState.isOwnAnswerDialogOpen
                    ) {
                        val totalQuestions = activeRun.pack.pairs.size.coerceAtLeast(1)
                        val currentQuestion = (activeRun.currentIndex + 1).coerceIn(1, totalQuestions)
                        androidx.compose.material3.Text(
                            text = "$currentQuestion/$totalQuestions",
                            fontSize = 12.sp,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                            color = androidx.compose.ui.graphics.Color.White.copy(alpha = 0.92f),
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .statusBarsPadding()
                                .padding(top = 19.dp, end = 18.dp)
                        )
                    }
                }

                resultsPackId?.let { packId ->
                    HarmonyPacksData.PACKS.firstOrNull { it.id == packId }?.let { resultPack ->
                        PackResultsScreen(
                            pack = resultPack,
                            answers = uiState.answers.filter { it.packId == packId },
                            profile = uiState.profile,
                            appLanguage = uiState.appLanguage,
                            onReplay = {
                                resultsPackId = null
                                openPackForPlay(packId, freshRun = true)
                            },
                            onClose = { resultsPackId = null }
                        )
                    }
                }

                if (isLiveChangeMode) {
                    LiveChangeHud(
                        changeCount = liveChangeCount,
                        hasActiveItem = uiState.activeRun != null,
                        onEditCurrent = {
                            if (uiState.activeRun != null) isLiveChangeEditorOpen = true
                        },
                        onExportTxt = {
                            DevExporter.exportLiveChangesTxt(context)
                            viewModel.showToast("📄 Live-Change TXT exportiert!")
                        },
                        onStop = {
                            isLiveChangeEditorOpen = false
                            isLiveChangeMode = false
                            viewModel.showToast("Live Change beendet · Änderungen bleiben gespeichert")
                        },
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .statusBarsPadding()
                            .padding(top = 7.dp)
                    )
                }

                if (isLiveChangeMode && isLiveChangeEditorOpen) {
                    uiState.activeRun?.let { activeRun ->
                        LiveChangeEditor(
                            pack = activeRun.pack,
                            currentIndex = activeRun.currentIndex,
                            onDismiss = { isLiveChangeEditorOpen = false },
                            onSave = { updatedPack, targetIndex, message ->
                                DeveloperDataManager.savePack(context, updatedPack)
                                liveChangeCount++
                                com.example.data.LiveChangeHistory.record(
                                    packId = updatedPack.id,
                                    packTitle = updatedPack.title,
                                    index = targetIndex,
                                    actionType = message,
                                    details = "Pack \"${updatedPack.title}\" aktualisiert"
                                )
                                isLiveChangeEditorOpen = false
                                isLiveChangeMode = false

                                val totalItems = if (updatedPack.type == "tot") {
                                    updatedPack.pairs.size
                                } else {
                                    updatedPack.questions.size
                                }
                                viewModel.startPack(updatedPack.id)
                                if (totalItems > 0) {
                                    val boundedTarget = targetIndex.coerceIn(0, totalItems - 1)
                                    repeat(boundedTarget) { viewModel.nextStep() }
                                }
                                viewModel.showToast("$message · Gespeichert & Live Change beendet")
                            }
                        )
                    }
                }

                if (isIntrospectionOpen) {
                    IntrospectionExperienceScreen(
                        appLanguage = uiState.appLanguage,
                        onExit = { isIntrospectionOpen = false }
                    )
                }

                if (isPandaEitherOrOpen) {
                    PandaEitherOrScreen(
                        profile = uiState.profile,
                        answers = uiState.answers,
                        appLanguage = uiState.appLanguage,
                        onSaveAnswer = { questionIndex, userChoice, partnerChoice ->
                            viewModel.saveEitherOrAnswer(questionIndex, userChoice, partnerChoice)
                        },
                        onExit = {
                            isPandaExitConfirmOpen = false
                            isPandaEitherOrOpen = false
                        }
                    )
                }

                if (isProposalExperienceOpen) {
                    ProposalExperienceScreen(
                        profile = uiState.profile,
                        onClose = { isProposalExperienceOpen = false }
                    )
                }

                if (isPandaExitConfirmOpen) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { isPandaExitConfirmOpen = false },
                        title = {
                            androidx.compose.material3.Text(
                                com.example.util.LanguageManager.tr("Spiel verlassen?", uiState.appLanguage)
                            )
                        },
                        text = {
                            androidx.compose.material3.Text(
                                com.example.util.LanguageManager.tr(
                                    "Möchtet ihr das Spiel wirklich verlassen?",
                                    uiState.appLanguage
                                )
                            )
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    isPandaExitConfirmOpen = false
                                    isPandaEitherOrOpen = false
                                }
                            ) {
                                androidx.compose.material3.Text(
                                    com.example.util.LanguageManager.tr("Verlassen", uiState.appLanguage)
                                )
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(
                                onClick = { isPandaExitConfirmOpen = false }
                            ) {
                                androidx.compose.material3.Text(
                                    com.example.util.LanguageManager.tr("Weiterspielen", uiState.appLanguage),
                                    color = androidx.compose.ui.graphics.Color.Unspecified
                                )
                            }
                        }
                    )
                }

                if (isSpecialFlowExitConfirmOpen) {
                    androidx.compose.material3.AlertDialog(
                        onDismissRequest = { isSpecialFlowExitConfirmOpen = false },
                        title = {
                            androidx.compose.material3.Text(
                                com.example.util.LanguageManager.tr("Spiel verlassen?", uiState.appLanguage)
                            )
                        },
                        text = {
                            androidx.compose.material3.Text(
                                com.example.util.LanguageManager.tr(
                                    "Möchtet ihr das Spiel wirklich verlassen? Nicht gespeicherter Fortschritt geht verloren.",
                                    uiState.appLanguage
                                )
                            )
                        },
                        confirmButton = {
                            androidx.compose.material3.TextButton(
                                onClick = {
                                    isSpecialFlowExitConfirmOpen = false
                                    isProposalExperienceOpen = false
                                }
                            ) {
                                androidx.compose.material3.Text(
                                    com.example.util.LanguageManager.tr("Verlassen", uiState.appLanguage)
                                )
                            }
                        },
                        dismissButton = {
                            androidx.compose.material3.TextButton(
                                onClick = { isSpecialFlowExitConfirmOpen = false }
                            ) {
                                androidx.compose.material3.Text(
                                    com.example.util.LanguageManager.tr("Weiterspielen", uiState.appLanguage),
                                    color = androidx.compose.ui.graphics.Color.Unspecified
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
