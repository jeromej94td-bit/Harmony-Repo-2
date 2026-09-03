package com.example.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.ProfileEntity
import com.example.data.session.AppSession
import com.example.data.session.UserProfile
import com.example.ui.AppLanguage
import com.example.ui.session.AppSessionViewModel

/**
 * Compatibility entry point for the existing MainActivity call site.
 *
 * The visible profile is no longer driven by local simulator values. This bridge resolves the
 * Activity-scoped AppSessionViewModel and forwards the real Supabase-backed session into the new
 * ProfileSheet. The old simulator callback is intentionally ignored and no simulator UI exists.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun ProfileSheet(
    profile: ProfileEntity,
    isEditProfileOpen: Boolean,
    onDismiss: () -> Unit,
    onToggleSimulator: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onCloseEditProfile: () -> Unit,
    onSaveEditProfile: (String, String, Long) -> Unit,
    onUpdateAvatar: (Uri, Boolean) -> Unit,
    onOpenDevStudio: (() -> Unit)? = null,
    isDarkMode: Boolean = true,
    onToggleDarkMode: ((Boolean) -> Unit)? = null,
    language: AppLanguage = AppLanguage.GERMAN,
    onLanguageChange: (AppLanguage) -> Unit = {},
    isPaired: Boolean = false,
    isDemoMode: Boolean = false,
    partnerDisplayName: String? = null,
    onOpenPartnerConnection: () -> Unit = {},
    onOpenHarmonyReset: () -> Unit = {},
    onOpenDeleteAccount: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sessionViewModel: AppSessionViewModel = viewModel()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()

    val fallbackSession = AppSession(
        userId = if (isDemoMode) "local-demo-session" else "profile-session-loading",
        email = null,
        profile = UserProfile(
            userId = if (isDemoMode) "local-demo-session" else "profile-session-loading",
            displayName = profile.userName,
            avatarUrl = null
        ),
        coupleId = if (isPaired) "legacy-pair-loading" else null,
        partner = if (isPaired) {
            UserProfile(
                userId = "legacy-partner-loading",
                displayName = partnerDisplayName?.takeIf { it.isNotBlank() } ?: profile.partnerName,
                avatarUrl = null
            )
        } else null
    )
    val realSession = sessionState.session ?: fallbackSession

    ProfileSheet(
        profile = profile,
        session = realSession,
        isEditProfileOpen = isEditProfileOpen,
        onDismiss = onDismiss,
        onOpenEditProfile = onOpenEditProfile,
        onCloseEditProfile = onCloseEditProfile,
        onSaveEditProfile = { userName, savedPartnerName, startDate ->
            if (!isDemoMode) {
                sessionViewModel.updateProfileDisplayName(userName)
            }
            onSaveEditProfile(userName, savedPartnerName, startDate)
        },
        onUpdateAvatar = onUpdateAvatar,
        onOpenDevStudio = onOpenDevStudio,
        isDarkMode = isDarkMode,
        onToggleDarkMode = onToggleDarkMode,
        language = language,
        onLanguageChange = onLanguageChange,
        isDemoMode = isDemoMode,
        onOpenPartnerConnection = onOpenPartnerConnection,
        onOpenHarmonyReset = onOpenHarmonyReset,
        onOpenDeleteAccount = onOpenDeleteAccount,
        onLogout = onLogout,
        modifier = modifier
    )
}
