package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.model.ChatMessageEntity
import com.example.ui.components.AuthenticatedAvatarImage
import com.example.ui.components.VoiceInputButton
import com.example.ui.components.VoiceMessageBubble
import com.example.ui.components.VoiceRecordingBar
import com.example.ui.components.formatTimeOnly
import com.example.ui.session.AppSessionViewModel
import com.example.ui.session.SessionPhase
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
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
    onSendVoiceMessage: (String, Int) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val sessionViewModel: AppSessionViewModel = viewModel()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val liveSession = sessionState.session
    val isDemoMode = sessionState.phase == SessionPhase.DEMO
    val livePartner = liveSession?.partner
    var isPartnerConnectionOpen by rememberSaveable { mutableStateOf(false) }

    if (!isDemoMode && livePartner == null) {
        Box(modifier = modifier.fillMaxSize()) {
            PartnerRequiredScreen(
                title = LanguageManager.tr("Euer privater Chat", appLanguage),
                description = LanguageManager.tr(
                    "Verbinde zuerst deinen Partner. Danach erscheint hier euer gemeinsamer Chat ohne simulierte Partnerdaten.",
                    appLanguage
                ),
                onConnectPartner = { isPartnerConnectionOpen = true }
            )

            if (isPartnerConnectionOpen && liveSession != null) {
                PartnerConnectionSheet(
                    session = liveSession,
                    activeInvite = sessionState.activeInvite,
                    actionInProgress = sessionState.actionInProgress,
                    errorMessage = sessionState.errorMessage,
                    onDismiss = {
                        isPartnerConnectionOpen = false
                        sessionViewModel.clearInvite()
                        sessionViewModel.clearError()
                    },
                    onCreateCode = { sessionViewModel.createPartnerInvite() },
                    onJoinCode = { code -> sessionViewModel.joinPartnerInvite(code) },
                    onClearInvite = { sessionViewModel.clearInvite() },
                    onDisconnect = { sessionViewModel.leaveCurrentCouple() }
                )
            }
        }
        return
    }

    val resolvedPartnerName = if (isDemoMode) partnerName else livePartner?.displayName.orEmpty()
    val context = LocalContext.current
    val recorderHelper = remember { AudioRecorderHelper(context) }
    var chatInputText by remember { mutableStateOf("") }
    var showReportDialog by remember { mutableStateOf(false) }
    var fullscreenImagePath by remember { mutableStateOf<String?>(null) }
    var isVoiceBarActive by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let(onSendImage)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    Column(modifier = modifier.fillMaxSize().padding(bottom = 80.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isDemoMode) {
                AvatarImage(path = partnerAvatarPath, fallback = resolvedPartnerName.take(1), size = 42)
            } else {
                AuthenticatedAvatarImage(
                    avatarRef = livePartner?.avatarUrl,
                    displayName = livePartner?.displayName.orEmpty(),
                    contentDescription = resolvedPartnerName,
                    modifier = Modifier.size(42.dp)
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(resolvedPartnerName, color = HarmonyText, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
                Text(
                    LanguageManager.tr("Privater Paar-Chat mit Sprachnachrichten", appLanguage),
                    color = HarmonyMuted,
                    fontSize = 11.sp
                )
            }
            IconButton(onClick = { showReportDialog = true }, modifier = Modifier.testTag("report_user_button")) {
                Icon(
                    Icons.Default.Flag,
                    contentDescription = LanguageManager.tr("Nutzer melden", appLanguage),
                    tint = HarmonyMuted
                )
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(top = 4.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { message ->
                ChatMessageBubble(
                    message = message,
                    appLanguage = appLanguage,
                    onImageClick = { fullscreenImagePath = it }
                )
            }
        }

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
                    onSendVoiceMessage(audioPath, durationSeconds)
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
                IconButton(
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(HarmonySurface)
                        .border(1.dp, HarmonyLine, CircleShape)
                        .testTag("add_chat_image_button")
                ) {
                    Icon(
                        Icons.Default.PhotoLibrary,
                        contentDescription = LanguageManager.tr("Bild hinzufügen", appLanguage),
                        tint = HarmonyPink
                    )
                }

                Spacer(Modifier.width(6.dp))

                IconButton(
                    onClick = { isVoiceBarActive = true },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(HarmonySurface)
                        .border(1.dp, HarmonyLine, CircleShape)
                        .testTag("voice_record_toggle_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = LanguageManager.tr("Sprachnachricht aufnehmen", appLanguage),
                        tint = HarmonyPink
                    )
                }

                Spacer(Modifier.width(6.dp))

                OutlinedTextField(
                    value = chatInputText,
                    onValueChange = { chatInputText = it },
                    placeholder = {
                        Text(
                            "${LanguageManager.tr("Nachricht an", appLanguage)} $resolvedPartnerName...",
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
                        focusedBorderColor = HarmonyPink,
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
                            onSendMessage(chatInputText)
                            chatInputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)))
                        .testTag("send_chat_button")
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = LanguageManager.tr("Senden", appLanguage),
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }

    if (showReportDialog) {
        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = { Text(LanguageManager.tr("Nutzer melden", appLanguage)) },
            text = {
                Text(
                    LanguageManager.tr(
                        "Möchtest du {partner} melden? Die Meldung wird erst nach deiner Bestätigung vorbereitet.",
                        appLanguage
                    ).replace("{partner}", resolvedPartnerName)
                )
            },
            confirmButton = {
                TextButton(onClick = { showReportDialog = false; onReportUser() }) {
                    Text(LanguageManager.tr("Meldung vorbereiten", appLanguage), color = HarmonyPink)
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text(LanguageManager.tr("Abbrechen", appLanguage))
                }
            }
        )
    }

    fullscreenImagePath?.let { path ->
        ChatImageFullscreen(path = path, appLanguage = appLanguage, onDismiss = { fullscreenImagePath = null })
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
                modifier = Modifier
                    .fillMaxWidth(0.78f)
                    .clip(bubbleShape)
                    .background(
                        if (isMe) Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))
                        else Brush.linearGradient(listOf(HarmonySurface2, HarmonySurface2))
                    )
                    .border(
                        if (isMe) 0.dp else 1.dp,
                        if (isMe) Color.Transparent else HarmonyLine,
                        bubbleShape
                    )
                    .padding(if (message.imagePath == null) 12.dp else 7.dp)
            ) {
                Column {
                    message.imagePath?.let { path ->
                        AsyncImage(
                            model = File(path),
                            contentDescription = LanguageManager.tr("Geteiltes Bild", appLanguage),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(196.dp)
                                .clip(RoundedCornerShape(15.dp))
                                .clickable { onImageClick(path) }
                                .testTag("chat_image_${message.id}")
                        )
                    }
                    if (message.text.isNotBlank()) {
                        Text(message.text, fontSize = 14.sp, color = Color.White, lineHeight = 19.sp)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        formatTimeOnly(message.timestamp),
                        fontSize = 9.5.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        modifier = Modifier.align(Alignment.End)
                    )
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
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xF20A0610))
                .clickable(onClick = onDismiss)
                .testTag("chat_image_fullscreen"),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = File(path),
                contentDescription = LanguageManager.tr("Geteiltes Bild im Vollbildmodus", appLanguage),
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp, vertical = 54.dp)
                    .clickable(enabled = false) {}
            )
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(18.dp)
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.56f))
                    .border(1.dp, Color.White.copy(alpha = 0.25f), CircleShape)
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
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple))),
        contentAlignment = Alignment.Center
    ) {
        if (path != null) {
            AsyncImage(
                model = File(path),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Text(fallback.uppercase(), color = Color.White, fontWeight = FontWeight.Black)
        }
    }
}
