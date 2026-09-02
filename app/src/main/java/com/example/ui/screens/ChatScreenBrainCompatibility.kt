package com.example.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.BrainChatSuggestionItem
import com.example.data.model.BrainMessage
import com.example.data.model.ChatMessageEntity

/**
 * Temporary source-compatibility bridge while the legacy MainActivity call site
 * is removed. All Harmony Brain parameters are deliberately ignored.
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
