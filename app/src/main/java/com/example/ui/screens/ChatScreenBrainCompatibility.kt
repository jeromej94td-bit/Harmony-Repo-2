package com.example.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.BrainChatSuggestionItem
import com.example.data.model.BrainMessage
import com.example.data.model.ChatMessageEntity
import com.example.ui.HarmonyViewModel
import com.example.ui.session.isConnectedPartnerName

/**
 * Source-compatibility bridge while the legacy MainActivity signature is being
 * reduced. Harmony Brain inputs are deliberately ignored and can never reach
 * network or UI code from here.
 */
@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    partnerName: String,
    partnerAvatarPath: String?,
    appLanguage: String = "de",
    onSendMessage: (String) -> Unit,
    onSendImage: (Uri) -> Unit,
    onReportUser: () -> Unit,
    isBrainChatMode: Boolean = false,
    isBrainGenerating: Boolean = false,
    brainMessages: List<BrainMessage> = emptyList(),
    onToggleBrainChatMode: (Boolean) -> Unit = {},
    onSendBrainMessage: (String) -> Unit = {},
    onResetBrainChat: () -> Unit = {},
    onSendVoiceMessage: (String, Int) -> Unit = { _, _ -> },
    onSendVoiceBrainMessage: (String, Int) -> Unit = { _, _ -> },
    onSaveSuggestionToNotes: (BrainChatSuggestionItem) -> Unit = {},
    onSuggestionFeedback: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    if (!isConnectedPartnerName(partnerName)) {
        val harmonyViewModel: HarmonyViewModel = viewModel()
        PartnerRequiredScreen(
            title = "Chat wird zu zweit freigeschaltet",
            description = "Verbinde zuerst dein Harmony-Konto mit deinem Partner. Spiele und Kategorien kannst du bis dahin weiterhin alleine entdecken.",
            onConnectPartner = { harmonyViewModel.openProfileSheet() },
            modifier = modifier
        )
        return
    }

    ChatScreen(
        messages = messages,
        partnerName = partnerName,
        partnerAvatarPath = partnerAvatarPath,
        appLanguage = appLanguage,
        onSendMessage = onSendMessage,
        onSendImage = onSendImage,
        onReportUser = onReportUser,
        onSendVoiceMessage = onSendVoiceMessage,
        modifier = modifier
    )
}
