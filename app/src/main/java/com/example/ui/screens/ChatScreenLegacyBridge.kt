package com.example.ui.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.BrainChatSuggestionItem
import com.example.data.model.BrainMessage
import com.example.data.model.ChatMessageEntity

/**
 * Compatibility bridge for the mixed main history.
 *
 * MainActivity still carried parameters from the archived Harmony Brain chat while the
 * production ChatScreen had already been reduced to private couple chat. This overload
 * intentionally ignores every archived Brain argument and delegates to the production
 * chat only, so old wiring can never surface Harmony Brain UI or trigger Brain actions.
 */
@Suppress("UNUSED_PARAMETER")
@Composable
fun ChatScreen(
    messages: List<ChatMessageEntity>,
    partnerName: String,
    partnerAvatarPath: String?,
    appLanguage: String = "de",
    onSendMessage: (String) -> Unit,
    onSendImage: (Uri) -> Unit,
    onReportUser: () -> Unit,
    isBrainChatMode: Boolean,
    isBrainGenerating: Boolean,
    brainMessages: List<BrainMessage>,
    onToggleBrainChatMode: (Boolean) -> Unit,
    onSendBrainMessage: (String) -> Unit,
    onResetBrainChat: () -> Unit,
    onSendVoiceMessage: (String, Int) -> Unit = { _, _ -> },
    onSendVoiceBrainMessage: (String, Int) -> Unit,
    onSaveSuggestionToNotes: (BrainChatSuggestionItem) -> Unit,
    onSuggestionFeedback: (String, String) -> Unit,
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
