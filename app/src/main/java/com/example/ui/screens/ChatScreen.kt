package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.model.BrainChatSuggestionItem
import com.example.data.model.BrainMessage
import com.example.data.model.ChatMessageEntity
import com.example.ui.components.BrainSuggestionCard
import com.example.ui.components.VoiceInputButton
import com.example.ui.components.VoiceMessageBubble
import com.example.ui.components.VoiceRecordingBar
import com.example.ui.components.formatTimeOnly
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.util.AudioRecorderHelper
import com.example.util.LanguageManager
import java.io.File

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
    val context = LocalContext.current
    val recorderHelper = remember { AudioRecorderHelper(context) }
    var chatInputText by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }
    var fullscreenImagePath by remember { mutableStateOf<String?>(null) }
    var isVoiceBarActive by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri -> uri?.let(onSendImage) }

    LaunchedEffect(messages.size, brainMessages.size, isBrainChatMode) {
        val currentSize = if (isBrainChatMode) brainMessages.size else messages.size
        if (currentSize > 0) {
            listState.animateScrollToItem(currentSize - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(bottom = 80.dp)) {
        // --- CHAT MODE TOGGLE ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(HarmonySurface2)
                .border(1.dp, HarmonyLine, RoundedCornerShape(12.dp))
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (!isBrainChatMode) {
                            Modifier.background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)))
                        } else {
                            Modifier
                        }
                    )
                    .clickable { onToggleBrainChatMode(false) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = LanguageManager.tr("Normaler Paar-Chat", appLanguage),
                    color = if (!isBrainChatMode) Color.White else HarmonyMuted,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isBrainChatMode) HarmonyPurple else Color.Transparent)
                    .clickable { onToggleBrainChatMode(true) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🧠 " + LanguageManager.tr("Harmony Brain", appLanguage),
                        color = if (isBrainChatMode) Color.White else HarmonyMuted,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // --- HEADER BAR ---
        if (isBrainChatMode) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .background(HarmonyPurple.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧠", fontSize = 16.sp)
                }
                Spacer(Modifier.width(7.dp))
                Text(
                    text = "Harmony Brain",
                    color = HarmonyText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = onResetBrainChat,
                    modifier = Modifier.size(34.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = LanguageManager.tr("Chat zurücksetzen", appLanguage),
                        tint = HarmonyPurpleLight,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AvatarImage(path = partnerAvatarPath, fallback = partnerName.take(1), size = 42)
                Spacer(Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(partnerName, color = HarmonyText, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                    Text(LanguageManager.tr("Privater Paar-Chat mit Sprachnachrichten", appLanguage), color = HarmonyMuted, fontSize = 11.sp)
                }
                IconButton(onClick = { showReportDialog = true }, modifier = Modifier.testTag("report_user_button")) {
                    Icon(Icons.Default.Flag, contentDescription = LanguageManager.tr("Nutzer melden", appLanguage), tint = HarmonyMuted)
                }
            }
        }

        // --- MESSAGES LIST ---
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                top = 4.dp,
                bottom = 12.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isBrainChatMode) {
                items(brainMessages, key = { it.id }) { message ->
                    BrainMessageBubble(
                        message = message,
                        appLanguage = appLanguage,
                        isGenerating = isBrainGenerating,
                        onSaveSuggestionToNotes = onSaveSuggestionToNotes,
                        onSuggestionFeedback = onSuggestionFeedback,
                        onOpenUrl = { url ->
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    )
                }
            } else {
                items(messages, key = { it.id }) { message ->
                    ChatMessageBubble(
                        message = message,
                        appLanguage = appLanguage,
                        onImageClick = { fullscreenImagePath = it }
                    )
                }
            }
        }

        // --- VOICE RECORDING BAR OR TEXT INPUT BAR ---
        AnimatedVisibility(
            visible = isVoiceBarActive,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            VoiceRecordingBar(
                recorderHelper = recorderHelper,
                appLanguage = appLanguage,
                onCancel = { isVoiceBarActive = false },
                onSendVoiceMessage = { audioPath, durationSeconds ->
                    isVoiceBarActive = false
                    if (isBrainChatMode) {
                        onSendVoiceBrainMessage(audioPath, durationSeconds)
                    } else {
                        onSendVoiceMessage(audioPath, durationSeconds)
                    }
                },
                onTranscribeToText = { transcribed ->
                    isVoiceBarActive = false
                    chatInputText = if (chatInputText.isBlank()) transcribed else "$chatInputText $transcribed"
                },
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp)
            )
        }

        if (!isVoiceBarActive) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isBrainChatMode) {
                    IconButton(
                        onClick = { imagePicker.launch("image/*") },
                        modifier = Modifier.size(44.dp).clip(CircleShape).background(HarmonySurface)
                            .border(1.dp, HarmonyLine, CircleShape).testTag("add_chat_image_button")
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = LanguageManager.tr("Bild hinzufügen", appLanguage), tint = HarmonyPink)
                    }
                    Spacer(Modifier.width(6.dp))
                }

                // Voice Recording Trigger Button
                IconButton(
                    onClick = { isVoiceBarActive = true },
                    modifier = Modifier.size(44.dp).clip(CircleShape).background(HarmonySurface)
                        .border(1.dp, HarmonyLine, CircleShape).testTag("voice_record_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = LanguageManager.tr("Sprachnachricht aufnehmen", appLanguage),
                        tint = if (isBrainChatMode) HarmonyPurpleLight else HarmonyPink
                    )
                }

                Spacer(Modifier.width(6.dp))

                OutlinedTextField(
                    value = chatInputText,
                    onValueChange = { chatInputText = it },
                    placeholder = {
                        Text(
                            if (isBrainChatMode) {
                                LanguageManager.tr("Frage an Harmony Brain …", appLanguage)
                            } else {
                                "${LanguageManager.tr("Nachricht an", appLanguage)} $partnerName..."
                            },
                            color = HarmonyMuted
                        )
                    },
                    trailingIcon = {
                        VoiceInputButton(
                            appLanguage = appLanguage,
                            onTextTranscribed = { transcribed ->
                                chatInputText = if (chatInputText.isBlank()) transcribed else "$chatInputText $transcribed"
                            }
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 52.dp, max = 112.dp)
                        .testTag("chat_input_field"),
                    singleLine = false,
                    minLines = 1,
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (isBrainChatMode) HarmonyPurple else HarmonyPink,
                        unfocusedBorderColor = HarmonyLine,
                        focusedTextColor = HarmonyText,
                        unfocusedTextColor = HarmonyText,
                        focusedContainerColor = HarmonySurface,
                        unfocusedContainerColor = HarmonySurface
                    ),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(Modifier.width(6.dp))

                IconButton(
                    onClick = {
                        if (chatInputText.isNotBlank()) {
                            if (isBrainChatMode) {
                                onSendBrainMessage(chatInputText)
                            } else {
                                onSendMessage(chatInputText)
                            }
                            chatInputText = ""
                        }
                    },
                    modifier = Modifier.size(44.dp).clip(CircleShape)
                        .background(
                            if (isBrainChatMode) {
                                Brush.linearGradient(listOf(HarmonyPurple, HarmonyPurpleLight))
                            } else {
                                Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))
                            }
                        ).testTag("send_chat_button")
                ) {
                    Icon(Icons.AutoMirrored.Filled.Send, contentDescription = LanguageManager.tr("Senden", appLanguage), tint = Color.White, modifier = Modifier.size(18.dp))
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text(LanguageManager.tr("Nutzer melden", appLanguage)) },
            text = {
                Text(LanguageManager.tr("Möchtest du {partner} melden? Die Meldung wird erst nach deiner Bestätigung vorbereitet.", appLanguage).replace("{partner}", partnerName))
            },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false; onReportUser() }) {
                    Text(LanguageManager.tr("Meldung vorbereiten", appLanguage), color = HarmonyPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) { Text(LanguageManager.tr("Abbrechen", appLanguage)) }
            }
        )
    }

    fullscreenImagePath?.let { path ->
        ChatImageFullscreen(path = path, appLanguage = appLanguage, onDismiss = { fullscreenImagePath = null })
    }
}

@Composable
private fun HarmonyWordRevealText(
    messageId: String,
    fullText: String,
    animate: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    var visibleWords by rememberSaveable(messageId) {
        mutableIntStateOf(if (animate) 0 else Int.MAX_VALUE)
    }
    val words = remember(fullText) { fullText.split(Regex("(?<=\\s)|(?=\\s)")) }

    LaunchedEffect(messageId, fullText, animate) {
        if (!animate || visibleWords == Int.MAX_VALUE) return@LaunchedEffect
        while (visibleWords < words.size) {
            visibleWords = (visibleWords + 2).coerceAtMost(words.size)
            delay(24L)
        }
    }

    val shown = if (visibleWords == Int.MAX_VALUE) fullText
    else words.take(visibleWords).joinToString("")

    Text(
        text = shown,
        modifier = modifier.animateContentSize(),
        color = color,
        fontSize = 15.sp,
        lineHeight = 22.sp
    )
}

@Composable
fun BrainMessageBubble(
    message: BrainMessage,
    appLanguage: String = "de",
    isGenerating: Boolean = false,
    onSaveSuggestionToNotes: (BrainChatSuggestionItem) -> Unit = {},
    onSuggestionFeedback: (String, String) -> Unit = { _, _ -> },
    onOpenUrl: (String) -> Unit = {}
) {
    val isUser = message.sender == "user"
    val bubbleShape = RoundedCornerShape(
        topStart = 19.dp,
        topEnd = 19.dp,
        bottomStart = if (isUser) 19.dp else 4.dp,
        bottomEnd = if (isUser) 4.dp else 19.dp
    )

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(HarmonyPurple.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("🧠", fontSize = 16.sp)
            }
            Spacer(Modifier.width(8.dp))
        }

        Column(modifier = Modifier.fillMaxWidth(if (isUser) 0.88f else 0.98f)) {
            // Audio Voice message bubble if present
            if (message.audioPath != null) {
                VoiceMessageBubble(
                    audioPath = message.audioPath,
                    durationSeconds = message.audioDurationSeconds,
                    isMe = isUser,
                    timestamp = message.timestamp,
                    appLanguage = appLanguage,
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(bubbleShape)
                        .background(
                            if (isUser) {
                                Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))
                            } else {
                                Brush.linearGradient(listOf(HarmonySurface2, HarmonySurface2))
                            }
                        )
                        .border(
                            width = 1.dp,
                            color = if (isUser) Color.Transparent else HarmonyLine,
                            shape = bubbleShape
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Column {
                        if (message.text == "..." && message.isSearching) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    LanguageManager.tr("Harmony Brain sucht & analysiert...", appLanguage),
                                    fontSize = 12.5.sp,
                                    color = HarmonyPurpleLight,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        } else {
                            // Direct Single Image Preview if present
                            message.imageUrl?.let { imgUrl ->
                                AsyncImage(
                                    model = imgUrl,
                                    contentDescription = "Empfohlenes Bild",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                )
                                Spacer(Modifier.height(8.dp))
                            }

                            if (message.text.isNotBlank()) {
                                HarmonyWordRevealText(
                                    messageId = message.id,
                                    fullText = message.text,
                                    animate = !isUser && message.animateOnArrival,
                                    color = if (isUser) Color.White else HarmonyText,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            if (message.sources.isNotEmpty()) {
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = "🔍 Grounding-Quellen:",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HarmonyPurpleLight
                                )
                                Spacer(Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    message.sources.forEach { source ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(HarmonyPurple.copy(alpha = 0.15f))
                                                .clickable { onOpenUrl(source.url) }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = source.title,
                                                fontSize = 10.sp,
                                                color = HarmonyPurpleLight,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = formatTimeOnly(message.timestamp),
                            fontSize = 9.5.sp,
                            color = if (isUser) Color.White.copy(alpha = 0.65f) else HarmonyMuted,
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }
            }

            // --- SUGGESTIONS CARDS (Images & Actions) ---
            if (message.suggestions.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(message.suggestions, key = { it.id }) { item ->
                        BrainSuggestionCard(
                            suggestion = item,
                            appLanguage = appLanguage,
                            onSaveToNotes = { onSaveSuggestionToNotes(item) },
                            onOpenMaps = { url -> onOpenUrl(url) },
                            onLike = { onSuggestionFeedback(item.id, "liked") },
                            modifier = Modifier.width(280.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessageEntity,
    appLanguage: String = "de",
    onImageClick: (String) -> Unit = {}
) {
    val isMe = message.sender == "me"
    val bubbleShape = RoundedCornerShape(
        topStart = 19.dp,
        topEnd = 19.dp,
        bottomStart = if (isMe) 19.dp else 4.dp,
        bottomEnd = if (isMe) 4.dp else 19.dp
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
    ) {
        if (message.audioPath != null) {
            VoiceMessageBubble(
                audioPath = message.audioPath,
                durationSeconds = message.audioDurationSeconds,
                isMe = isMe,
                timestamp = message.timestamp,
                appLanguage = appLanguage,
                modifier = Modifier.fillMaxWidth(0.82f)
            )
        } else {
            Box(
                modifier = Modifier.fillMaxWidth(0.78f).clip(bubbleShape)
                    .background(if (isMe) Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)) else Brush.linearGradient(listOf(HarmonySurface2, HarmonySurface2)))
                    .border(if (isMe) 0.dp else 1.dp, if (isMe) Color.Transparent else HarmonyLine, bubbleShape)
                    .padding(if (message.imagePath == null) 12.dp else 7.dp)
            ) {
                Column {
                    message.imagePath?.let { path ->
                        AsyncImage(
                            model = File(path),
                            contentDescription = LanguageManager.tr("Geteiltes Bild", appLanguage),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(196.dp).clip(RoundedCornerShape(15.dp))
                                .clickable { onImageClick(path) }.testTag("chat_image_${message.id}")
                        )
                    }
                    if (message.text.isNotBlank()) Text(message.text, fontSize = 14.sp, color = Color.White, lineHeight = 19.sp)
                    Spacer(Modifier.height(4.dp))
                    Text(formatTimeOnly(message.timestamp), fontSize = 9.5.sp, color = Color.White.copy(alpha = 0.65f), modifier = Modifier.align(Alignment.End))
                }
            }
        }
    }
}

@Composable
private fun ChatImageFullscreen(path: String, appLanguage: String, onDismiss: () -> Unit) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color(0xF20A0610)).clickable(onClick = onDismiss).testTag("chat_image_fullscreen"),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = File(path),
                contentDescription = LanguageManager.tr("Geteiltes Bild im Vollbildmodus", appLanguage),
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp, vertical = 54.dp).clickable(enabled = false) {}
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd).padding(18.dp).size(44.dp).clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.56f)).border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
                    .testTag("close_chat_image_fullscreen")
            ) {
                Icon(Icons.Default.Close, contentDescription = "Vollbild schließen", tint = Color.White)
            }
        }
    }
}

@Composable
private fun AvatarImage(path: String?, fallback: String, size: Int) {
    Box(
        modifier = Modifier.size(size.dp).clip(CircleShape).background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))),
        contentAlignment = Alignment.Center
    ) {
        if (path != null) {
            AsyncImage(model = File(path), contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
        } else {
            Text(fallback.uppercase(), color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}
