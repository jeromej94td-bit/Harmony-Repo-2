package com.example

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.couple.CoupleQuestionRepository
import com.example.data.couple.PartnerCompletionNotifier
import com.example.ui.AppLanguage
import com.example.ui.HarmonyViewModel
import com.example.ui.LocalAppLanguage
import com.example.ui.screens.AuthScreenV2
import com.example.ui.session.AppSessionViewModel
import com.example.ui.session.SessionPhase
import com.example.ui.theme.HarmonyTheme
import com.example.widget.MemoryWidgetDatabaseObserver
import com.example.widget.MemoryWidgetOpenRequest
import com.example.widget.parseMemoryWidgetOpenRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext

/**
 * Canonical Harmony launcher.
 *
 * Signed-out users are intentionally routed through AuthScreenV2 instead of
 * the legacy AuthScreen. Once a real or demo session exists, the established
 * HarmonyApp composable is used unchanged.
 */
class HarmonyEntryActivity : ComponentActivity() {

    private val viewModel: HarmonyViewModel by viewModels()
    private val sessionViewModel: AppSessionViewModel by viewModels()
    private var memoryWidgetOpenRequest by mutableStateOf<MemoryWidgetOpenRequest?>(null)
    private var partnerPackOpenRequest by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        memoryWidgetOpenRequest = parseMemoryWidgetOpenRequest(intent)
        partnerPackOpenRequest = intent.getStringExtra(PartnerCompletionNotifier.EXTRA_OPEN_PARTNER_PACK_ID)
        if (intent.getIntExtra("open_tab", -1) == 1) {
            viewModel.selectTab(1)
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU &&
            androidx.core.content.ContextCompat.checkSelfPermission(
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
            val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
            val currentLanguage = AppLanguage.fromCode(uiState.appLanguage)
            val coupleQuestionRepository = remember { CoupleQuestionRepository() }

            LaunchedEffect(sessionState.phase, sessionState.session?.userId, sessionState.session?.coupleId) {
                val session = sessionState.session
                if (sessionState.phase != SessionPhase.READY || session?.isPaired != true) return@LaunchedEffect

                while (coroutineContext.isActive) {
                    runCatching { coupleQuestionRepository.getPartnerNotifications() }
                        .getOrDefault(emptyList())
                        .forEach { notification ->
                            val shown = PartnerCompletionNotifier.show(applicationContext, notification)
                            if (shown) {
                                runCatching {
                                    coupleQuestionRepository.markPartnerNotificationRead(notification.notificationId)
                                }
                            }
                        }
                    delay(15_000)
                }
            }

            LaunchedEffect(sessionState.phase, partnerPackOpenRequest) {
                val packId = partnerPackOpenRequest ?: return@LaunchedEffect
                if (sessionState.phase != SessionPhase.READY) return@LaunchedEffect
                viewModel.selectTab(1)
                viewModel.startPack(packId)
                partnerPackOpenRequest = null
            }

            CompositionLocalProvider(
                LocalAppLanguage provides currentLanguage,
                androidx.compose.ui.platform.LocalLayoutDirection provides
                    if (currentLanguage.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr
            ) {
                HarmonyTheme(darkTheme = uiState.isDarkMode) {
                    when (sessionState.phase) {
                        SessionPhase.LOADING -> {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                        SessionPhase.SIGNED_OUT -> {
                            AuthScreenV2(
                                onAuthSuccess = { sessionViewModel.refresh() },
                                onDemoRequested = { sessionViewModel.enterDemo() }
                            )
                        }
                        else -> {
                            HarmonyApp(
                                viewModel = viewModel,
                                sessionViewModel = sessionViewModel,
                                memoryWidgetOpenRequest = memoryWidgetOpenRequest,
                                onMemoryWidgetRequestConsumed = { memoryWidgetOpenRequest = null }
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        memoryWidgetOpenRequest = parseMemoryWidgetOpenRequest(intent)
        partnerPackOpenRequest = intent.getStringExtra(PartnerCompletionNotifier.EXTRA_OPEN_PARTNER_PACK_ID)
        if (intent.getIntExtra("open_tab", -1) == 1) {
            viewModel.selectTab(1)
        }
    }
}
