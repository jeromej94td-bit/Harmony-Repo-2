package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.model.BrainChatSuggestionItem
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.ui.util.triggerMiniVibration
import com.example.util.AudioPlaybackState
import com.example.util.AudioPlayerHelper
import com.example.util.AudioRecorderHelper
import com.example.util.GeminiAudioTranscriber
import com.example.util.LanguageManager
import kotlinx.coroutines.launch
import java.io.File

/**
 * Interactive Audio Recording Bar displayed when the user is recording speech or voice message.
 */
@Composable
fun VoiceRecordingBar(
    recorderHelper: AudioRecorderHelper,
    appLanguage: String = "de",
    onSendVoiceMessage: (String, Int) -> Unit,
    onTranscribeToText: (String) -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val recordingState by recorderHelper.recordingState.collectAsState()
    var isTranscribing by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "recording_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xFF2C1438), Color(0xFF1B1226))))
            .border(1.dp, HarmonyPink.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pulsing Mic Icon
        Box(
            modifier = Modifier
                .size(38.dp)
                .scale(if (recordingState.isRecording) pulseScale else 1f)
                .clip(CircleShape)
                .background(HarmonyPink),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "Aufnahme aktiv",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        // Timer & Dynamic Waveform
        Column(modifier = Modifier.weight(1f)) {
            val minutes = recordingState.durationSeconds / 60
            val seconds = recordingState.durationSeconds % 60
            val timeFormatted = String.format("%02d:%02d", minutes, seconds)

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = timeFormatted,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (isTranscribing) "Transkribiere mit Gemini 3.5 Flash..." else LanguageManager.tr("Sprachaufnahme...", appLanguage),
                    color = HarmonyMuted,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(Modifier.height(4.dp))

            // Animated amplitude bars
            Row(
                modifier = Modifier.fillMaxWidth().height(14.dp),
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val amp = recordingState.currentAmplitude
                for (i in 0..15) {
                    val barHeight = (4 + (amp * 10 * ((i % 5) + 1) / 3f)).coerceIn(3f, 14f).dp
                    Box(
                        modifier = Modifier
                            .width(3.dp)
                            .height(barHeight)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(HarmonyPink, HarmonyPurpleLight)
                                )
                            )
                    )
                }
            }
        }

        Spacer(Modifier.width(8.dp))

        if (isTranscribing) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                color = HarmonyPink,
                strokeWidth = 2.5.dp
            )
        } else {
            // Action 1: Transcribe with Gemini 3.5 Flash
            IconButton(
                onClick = {
                    triggerMiniVibration(context, 35L)
                    val (file, _) = recorderHelper.stopRecording()
                    if (file != null && file.exists()) {
                        isTranscribing = true
                        scope.launch {
                            val result = GeminiAudioTranscriber.transcribeAudioFile(file, appLanguage)
                            isTranscribing = false
                            result.onSuccess { transcribedText ->
                                onTranscribeToText(transcribedText)
                            }.onFailure { err ->
                                onTranscribeToText("⚠️ Transkription: ${err.localizedMessage ?: "Fehler"}")
                            }
                        }
                    } else {
                        onCancel()
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.12f))
                    .testTag("transcribe_recording_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "In Text umwandeln",
                    tint = HarmonyPurpleLight,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(Modifier.width(6.dp))

            // Action 2: Send as Voice Message
            IconButton(
                onClick = {
                    triggerMiniVibration(context, 40L)
                    val (file, durationSec) = recorderHelper.stopRecording()
                    if (file != null && file.exists()) {
                        onSendVoiceMessage(file.absolutePath, durationSec.coerceAtLeast(1))
                    } else {
                        onCancel()
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(HarmonyPink)
                    .testTag("send_voice_message_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Sprachnachricht senden",
                    tint = Color.White,
                    modifier = Modifier.size(17.dp)
                )
            }

            Spacer(Modifier.width(6.dp))

            // Action 3: Cancel / Delete
            IconButton(
                onClick = {
                    triggerMiniVibration(context, 30L)
                    recorderHelper.cancelRecording()
                    onCancel()
                },
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.08f))
                    .testTag("cancel_recording_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Abbrechen",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * Audio Player Bubble for Voice Messages in Chat.
 */
@Composable
fun VoiceMessageBubble(
    audioPath: String,
    durationSeconds: Int,
    isMe: Boolean,
    timestamp: Long,
    appLanguage: String = "de",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val playbackState by AudioPlayerHelper.playbackState.collectAsState()
    val isCurrentAudio = playbackState.activeFilePath == audioPath
    val isPlaying = isCurrentAudio && playbackState.isPlaying

    val currentSeconds = if (isCurrentAudio) {
        playbackState.currentPositionMs / 1000
    } else {
        0
    }

    val displaySeconds = if (isPlaying || (isCurrentAudio && currentSeconds > 0)) {
        currentSeconds
    } else {
        durationSeconds
    }

    val formattedTime = String.format("%d:%02d", displaySeconds / 60, displaySeconds % 60)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Play / Pause Button
        IconButton(
            onClick = {
                triggerMiniVibration(context, 35L)
                AudioPlayerHelper.togglePlay(audioPath, durationSeconds)
            },
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isMe) Color.White.copy(alpha = 0.25f) else HarmonyPink)
                .testTag("play_voice_message_button")
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Abspielen",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            // Waveform Progress Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .clickable {
                        // Toggle play/pause or restart
                        AudioPlayerHelper.togglePlay(audioPath, durationSeconds)
                    },
                horizontalArrangement = Arrangement.spacedBy(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val totalBars = 20
                val progress = if (isCurrentAudio) playbackState.progress else 0f
                val activeBarCount = (progress * totalBars).toInt()

                for (i in 0 until totalBars) {
                    val isPast = i <= activeBarCount && (isPlaying || currentSeconds > 0)
                    val barHeight = ((i * 7 % 14) + 6).dp

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(barHeight)
                            .clip(RoundedCornerShape(2.dp))
                            .background(
                                if (isPast) {
                                    if (isMe) Color.White else HarmonyPink
                                } else {
                                    if (isMe) Color.White.copy(alpha = 0.35f) else HarmonyLine
                                }
                            )
                    )
                }
            }

            Spacer(Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "🎙️ $formattedTime",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isMe) Color.White.copy(alpha = 0.9f) else HarmonyPurpleLight
                    )
                }

                Text(
                    text = formatTimeOnly(timestamp),
                    fontSize = 9.5.sp,
                    color = if (isMe) Color.White.copy(alpha = 0.65f) else HarmonyMuted
                )
            }
        }
    }
}

/**
 * Compact Voice Input Button for text fields and question answer dialogs.
 * Enables users to speak their answers, transcribing them directly with Gemini 3.5 Flash / SpeechRecognizer!
 */
@Composable
fun VoiceInputButton(
    appLanguage: String = "de",
    onTextTranscribed: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val recorderHelper = remember { AudioRecorderHelper(context) }
    val recordingState by recorderHelper.recordingState.collectAsState()
    var isTranscribing by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            recorderHelper.cancelRecording()
        }
    }

    if (recordingState.isRecording || isTranscribing) {
        Row(
            modifier = modifier
                .clip(RoundedCornerShape(18.dp))
                .background(Brush.horizontalGradient(listOf(Color(0xFF331644), Color(0xFF1E112B))))
                .border(1.dp, HarmonyPink, RoundedCornerShape(18.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isTranscribing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    color = HarmonyPink,
                    strokeWidth = 2.dp
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Transkribiere...",
                    fontSize = 12.sp,
                    color = HarmonyPurpleLight,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(HarmonyPink)
                )
                Spacer(Modifier.width(8.dp))
                val sec = recordingState.durationSeconds
                Text(
                    text = String.format("0:%02d", sec),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                IconButton(
                    onClick = {
                        triggerMiniVibration(context, 35L)
                        val (file, _) = recorderHelper.stopRecording()
                        if (file != null && file.exists()) {
                            isTranscribing = true
                            scope.launch {
                                val result = GeminiAudioTranscriber.transcribeAudioFile(file, appLanguage)
                                isTranscribing = false
                                result.onSuccess { text ->
                                    onTextTranscribed(text)
                                }.onFailure { err ->
                                    onTextTranscribed(err.localizedMessage ?: "Fehler")
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Fertig",
                        tint = HarmonyPink,
                        modifier = Modifier.size(18.dp)
                    )
                }
                IconButton(
                    onClick = {
                        triggerMiniVibration(context, 30L)
                        recorderHelper.cancelRecording()
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Abbrechen",
                        tint = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    } else {
        IconButton(
            onClick = {
                triggerMiniVibration(context, 35L)
                recorderHelper.startRecording()
            },
            modifier = modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.08f))
                .border(1.dp, HarmonyLine, CircleShape)
                .testTag("voice_input_button")
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = LanguageManager.tr("Spracheingabe mit KI-Transkription", appLanguage),
                tint = HarmonyPink,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/**
 * Rich Suggestion Card with Image URL, Title, Description, and Actions
 * (Save to Notes / Bucket List, Open in Google Maps, Like).
 */
@Composable
fun BrainSuggestionCard(
    suggestion: BrainChatSuggestionItem,
    appLanguage: String = "de",
    onSaveToNotes: (BrainChatSuggestionItem) -> Unit = {},
    onOpenMaps: (String) -> Unit = {},
    onLike: (BrainChatSuggestionItem) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSaved by remember(suggestion.isSavedToNotes) { mutableStateOf(suggestion.isSavedToNotes) }
    var isLiked by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(HarmonySurface)
            .border(1.dp, HarmonyLine, RoundedCornerShape(18.dp))
    ) {
        Column {
            // Visual Image Preview (using AsyncImage from Coil)
            if (!suggestion.imageUrl.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(suggestion.imageUrl)
                            .crossfade(true)
                            .build(),
                        contentDescription = suggestion.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
                    )

                    // Gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.3f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Category Pill on Image
                    if (!suggestion.category.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(10.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.Black.copy(alpha = 0.65f))
                                .border(1.dp, HarmonyPink.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = when (suggestion.category.lowercase()) {
                                    "essen" -> "🍕 Essen & Genuss"
                                    "date" -> "🍷 Romantisches Date"
                                    "aktivität" -> "🧗 Aktivität & Spaß"
                                    "ausflug" -> "🗺️ Ausflugsziel"
                                    "wellness" -> "🧖 Wellness & Erholung"
                                    "kultur" -> "🎭 Kultur & Events"
                                    "reisen" -> "✈️ Urlaub & Reise"
                                    else -> "✨ ${suggestion.category}"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Card Text Details
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = suggestion.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    lineHeight = 20.sp
                )

                if (suggestion.description.isNotBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = suggestion.description,
                        fontSize = 13.sp,
                        color = HarmonyMuted,
                        lineHeight = 18.sp
                    )
                }

                Spacer(Modifier.height(12.dp))

                // Interactive Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Action 1: Save as Note / Bucket List
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSaved) Color(0xFF1B382B) else HarmonySurface2)
                            .border(1.dp, if (isSaved) Color(0xFF4ED69A) else HarmonyLine, RoundedCornerShape(12.dp))
                            .clickable {
                                triggerMiniVibration(context, 40L)
                                isSaved = true
                                onSaveToNotes(suggestion)
                            }
                            .padding(vertical = 8.dp, horizontal = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isSaved) Icons.Default.Check else Icons.Default.PushPin,
                                contentDescription = "Als Notiz speichern",
                                tint = if (isSaved) Color(0xFF4ED69A) else HarmonyPink,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                text = if (isSaved) "Gespeichert 📌" else "Als Notiz 📌",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSaved) Color(0xFF4ED69A) else Color.White
                            )
                        }
                    }

                    // Action 2: Open in Google Maps
                    if (!suggestion.linkUrl.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(HarmonySurface2)
                                .border(1.dp, HarmonyLine, RoundedCornerShape(12.dp))
                                .clickable {
                                    triggerMiniVibration(context, 35L)
                                    onOpenMaps(suggestion.linkUrl)
                                }
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Map,
                                    contentDescription = "Auf Karte öffnen",
                                    tint = HarmonyPurpleLight,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(5.dp))
                                Text(
                                    text = "Google Maps 🗺️",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Action 3: Like / Heart
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isLiked) HarmonyPink else HarmonySurface2)
                            .border(1.dp, if (isLiked) HarmonyPink else HarmonyLine, CircleShape)
                            .clickable {
                                triggerMiniVibration(context, 35L)
                                isLiked = !isLiked
                                onLike(suggestion)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "Gefällt uns",
                            tint = if (isLiked) Color.White else HarmonyPink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
