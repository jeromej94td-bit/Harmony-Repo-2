package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.HarmonyDatabase
import com.example.data.model.MemoryDefaults
import com.example.data.model.MemoryEntryEntity
import com.example.data.model.MemoryEntryKind
import com.example.data.model.MemoryMatchAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.data.repository.RoomMemoryRepository
import com.example.ui.LocalAppLanguage
import com.example.ui.components.VoiceInputButton
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val MEMORY_PHASE_FIRST = 0
private const val MEMORY_PHASE_HANDOFF = 1
private const val MEMORY_PHASE_SECOND = 2
private const val MEMORY_PHASE_REVEAL_READY = 3
private const val MEMORY_PHASE_REVEALED = 4

@Composable
internal fun MemoryMatchBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val photoMode = PhotoQuestionPolicy.modeForQuestion(question)
    if (photoMode != null) {
        PhotoQuestionBoard(
            mode = photoMode,
            rawQuestion = question,
            selectedAnswer = selectedAnswer,
            onPick = onPick,
            modifier = modifier
        )
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appLanguage = LocalAppLanguage.current.code
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val restored = remember(selectedAnswer) { selectedAnswer?.let(MemoryMatchAnswerCodec::decode) }

    var firstText by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.text.orEmpty()) }
    var firstImagePath by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.imagePath) }
    var partnerText by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.partnerText.orEmpty()) }
    var partnerImagePath by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.partnerImagePath) }
    var phase by rememberSaveable(question, selectedAnswer) {
        mutableStateOf(if (restored != null) MEMORY_PHASE_REVEALED else MEMORY_PHASE_FIRST)
    }
    var isSaving by rememberSaveable(question) { mutableStateOf(false) }

    val memoryRepository = remember(context.applicationContext) {
        RoomMemoryRepository(HarmonyDatabase.getInstance(context.applicationContext))
    }

    val firstImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                firstImagePath = withContext(Dispatchers.IO) { copyMemoryGameImage(context, uri) }
            }
        }
    }
    val partnerImagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                partnerImagePath = withContext(Dispatchers.IO) { copyMemoryGameImage(context, uri) }
            }
        }
    }

    FullscreenMechanicShell(
        kicker = tr("🧠 ERINNERUNGS-MATCH", "🧠 MEMORY MATCH"),
        question = prompt,
        instruction = when (phase) {
            MEMORY_PHASE_FIRST -> tr(
                "Halte deine Erinnerung fest. Sie bleibt verborgen, bis ihr beide fertig seid.",
                "Capture your memory. It stays hidden until both of you are ready."
            )
            MEMORY_PHASE_HANDOFF -> tr(
                "Deine Erinnerung ist verdeckt. Jetzt ist ${profile.partnerName} dran.",
                "Your memory is hidden. Now it's ${profile.partnerName}'s turn."
            )
            MEMORY_PHASE_SECOND -> tr(
                "${profile.partnerName} hält jetzt die eigene Erinnerung fest – ohne deine zu sehen.",
                "${profile.partnerName} now captures their memory without seeing yours."
            )
            MEMORY_PHASE_REVEAL_READY -> tr(
                "Beide Erinnerungen sind sicher. Deckt sie gemeinsam auf.",
                "Both memories are safe. Reveal them together."
            )
            else -> tr(
                "Zwei Perspektiven auf denselben Moment.",
                "Two perspectives on the same moment."
            )
        },
        modifier = modifier.testTag("memory_match_board")
    ) {
        when (phase) {
            MEMORY_PHASE_FIRST -> MemoryInputPane(
                name = profile.userName,
                text = firstText,
                imagePath = firstImagePath,
                appLanguage = appLanguage,
                textTag = "memory_match_text",
                photoTag = "memory_match_photo",
                lockTag = "memory_match_first_lock",
                onTextChange = { firstText = it },
                onVoiceText = { spoken -> firstText = appendSpokenText(firstText, spoken) },
                onPhotoPick = { firstImagePicker.launch("image/*") },
                onLock = {
                    triggerMiniVibration(context, 42L)
                    phase = MEMORY_PHASE_HANDOFF
                }
            )

            MEMORY_PHASE_HANDOFF -> MemoryHandoffPane(
                partnerName = profile.partnerName,
                onReady = {
                    triggerMiniVibration(context, 36L)
                    phase = MEMORY_PHASE_SECOND
                }
            )

            MEMORY_PHASE_SECOND -> MemoryInputPane(
                name = profile.partnerName,
                text = partnerText,
                imagePath = partnerImagePath,
                appLanguage = appLanguage,
                textTag = "memory_match_partner_text",
                photoTag = "memory_match_partner_photo",
                lockTag = "memory_match_partner_lock",
                onTextChange = { partnerText = it },
                onVoiceText = { spoken -> partnerText = appendSpokenText(partnerText, spoken) },
                onPhotoPick = { partnerImagePicker.launch("image/*") },
                onLock = {
                    triggerMiniVibration(context, 48L)
                    phase = MEMORY_PHASE_REVEAL_READY
                }
            )

            MEMORY_PHASE_REVEAL_READY -> MemoryRevealReadyPane(
                userName = profile.userName,
                partnerName = profile.partnerName,
                onReveal = {
                    triggerMiniVibration(context, 58L)
                    phase = MEMORY_PHASE_REVEALED
                }
            )

            else -> {
                val encodedAnswer = MemoryMatchAnswerCodec.encode(
                    firstText.trim(),
                    firstImagePath,
                    partnerText.trim(),
                    partnerImagePath
                )
                MemoryRevealPane(
                    firstName = profile.userName,
                    firstText = firstText,
                    firstImagePath = firstImagePath,
                    partnerName = profile.partnerName,
                    partnerText = partnerText,
                    partnerImagePath = partnerImagePath,
                    isSaving = isSaving,
                    restoredAnswer = selectedAnswer,
                    onKeep = {
                        if (isSaving) return@MemoryRevealPane
                        if (selectedAnswer != null) {
                            onPick(encodedAnswer)
                            return@MemoryRevealPane
                        }
                        isSaving = true
                        scope.launch {
                            val now = System.currentTimeMillis()
                            runCatching {
                                memoryRepository.ensureDefaultCategories(now)
                                memoryRepository.insertEntries(
                                    listOf(
                                        MemoryEntryEntity(
                                            id = UUID.randomUUID().toString(),
                                            categoryId = MemoryDefaults.OTHER_ID,
                                            kind = MemoryEntryKind.NOTE,
                                            title = prompt.take(80),
                                            body = buildSharedMemoryBody(
                                                profile.userName,
                                                firstText,
                                                firstImagePath,
                                                profile.partnerName,
                                                partnerText,
                                                partnerImagePath
                                            ),
                                            previewDescription = tr(
                                                "Gemeinsame Erinnerung von ${profile.userName} & ${profile.partnerName}",
                                                "Shared memory by ${profile.userName} & ${profile.partnerName}"
                                            ),
                                            previewImageUrl = (firstImagePath ?: partnerImagePath)?.let { "file://$it" },
                                            createdAt = now,
                                            updatedAt = now
                                        )
                                    )
                                )
                            }
                            isSaving = false
                            onPick(encodedAnswer)
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun MemoryInputPane(
    name: String,
    text: String,
    imagePath: String?,
    appLanguage: String,
    textTag: String,
    photoTag: String,
    lockTag: String,
    onTextChange: (String) -> Unit,
    onVoiceText: (String) -> Unit,
    onPhotoPick: () -> Unit,
    onLock: () -> Unit
) {
    val compact = LocalConfiguration.current.screenHeightDp < 700
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(28.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(
                            HarmonyPurple.copy(alpha = 0.30f),
                            HarmonySurface.copy(alpha = 0.84f),
                            HarmonyBg.copy(alpha = 0.88f)
                        )
                    )
                )
                .border(1.2.dp, HarmonyPink.copy(alpha = 0.34f), RoundedCornerShape(28.dp))
                .padding(if (compact) 12.dp else 16.dp)
        ) {
            Text(
                text = name,
                color = HarmonyPinkSoft,
                fontSize = if (compact) 15.sp else 17.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(if (compact) 7.dp else 10.dp))
            if (!imagePath.isNullOrBlank()) {
                AsyncImage(
                    model = File(imagePath),
                    contentDescription = tr("Ausgewählte Erinnerung", "Selected memory"),
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (compact) 108.dp else 142.dp)
                        .clip(RoundedCornerShape(22.dp))
                )
                Spacer(Modifier.height(9.dp))
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = text,
                    onValueChange = onTextChange,
                    placeholder = {
                        Text(tr("Welche Erinnerung öffnet sich?", "Which memory opens up?"))
                    },
                    minLines = if (compact) 3 else 4,
                    maxLines = if (compact) 5 else 7,
                    modifier = Modifier
                        .weight(1f)
                        .testTag(textTag)
                )
                Spacer(Modifier.width(8.dp))
                VoiceInputButton(
                    appLanguage = appLanguage,
                    onTextTranscribed = onVoiceText
                )
            }
        }
        Spacer(Modifier.height(if (compact) 9.dp else 12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PrimaryMechanicButton(
                text = if (imagePath == null) tr("📷 Foto", "📷 Photo") else tr("📷 Foto ändern", "📷 Change photo"),
                onClick = onPhotoPick,
                modifier = Modifier.weight(0.82f),
                testTag = photoTag
            )
            PrimaryMechanicButton(
                text = tr("Verdecken", "Hide memory"),
                enabled = text.isNotBlank() || !imagePath.isNullOrBlank(),
                onClick = onLock,
                modifier = Modifier.weight(1.18f),
                testTag = lockTag
            )
        }
    }
}

@Composable
private fun MemoryHandoffPane(
    partnerName: String,
    onReady: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
                .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("🔒", fontSize = 44.sp)
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = tr("Erinnerung verdeckt", "Memory hidden"),
            color = Color.White,
            fontSize = 27.sp,
            lineHeight = 32.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = tr(
                "Übergib das Handy an $partnerName. Deine Antwort bleibt unsichtbar.",
                "Pass the phone to $partnerName. Your answer stays hidden."
            ),
            color = HarmonyMuted,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        PrimaryMechanicButton(
            text = tr("$partnerName ist bereit", "$partnerName is ready"),
            onClick = onReady,
            testTag = "memory_match_handoff_ready"
        )
    }
}

@Composable
private fun MemoryRevealReadyPane(
    userName: String,
    partnerName: String,
    onReveal: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("memory_match_reveal_ready"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = tr("Bereit für eure zwei Erinnerungen?", "Ready for your two memories?"),
                color = Color.White,
                fontSize = 26.sp,
                lineHeight = 31.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(7.dp))
            Text(
                text = tr("Noch ist keine der beiden Antworten sichtbar.", "Neither answer is visible yet."),
                color = HarmonyMuted,
                fontSize = 15.sp,
                textAlign = TextAlign.Center
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HiddenMemoryCard(userName, Modifier.weight(1f))
            HiddenMemoryCard(partnerName, Modifier.weight(1f))
        }
        PrimaryMechanicButton(
            text = tr("Gemeinsam aufdecken", "Reveal together"),
            onClick = onReveal,
            testTag = "memory_match_reveal_button"
        )
    }
}

@Composable
private fun HiddenMemoryCard(name: String, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(26.dp)
    Box(
        modifier = modifier
            .heightIn(min = 150.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(HarmonyPurple.copy(alpha = 0.76f), HarmonySurface2, HarmonyBg)
                )
            )
            .border(1.5.dp, HarmonyPink.copy(alpha = 0.46f), shape)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("✦", color = HarmonyPinkSoft, fontSize = 36.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tr("verborgen", "hidden"),
                color = HarmonyMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MemoryRevealPane(
    firstName: String,
    firstText: String,
    firstImagePath: String?,
    partnerName: String,
    partnerText: String,
    partnerImagePath: String?,
    isSaving: Boolean,
    restoredAnswer: String?,
    onKeep: () -> Unit
) {
    val reveal = remember(firstText, partnerText, firstImagePath, partnerImagePath) { Animatable(0f) }
    LaunchedEffect(firstText, partnerText, firstImagePath, partnerImagePath) {
        reveal.snapTo(if (restoredAnswer != null) 1f else 0f)
        if (restoredAnswer == null) {
            reveal.animateTo(1f, tween(650, easing = FastOutSlowInEasing))
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = tr("✨ Derselbe Moment, zwei Blickwinkel", "✨ One moment, two perspectives"),
            color = Color.White,
            fontSize = 23.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.graphicsLayer {
                alpha = 0.35f + reveal.value * 0.65f
                scaleX = 0.95f + reveal.value * 0.05f
                scaleY = 0.95f + reveal.value * 0.05f
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            RevealedMemoryCard(
                name = firstName,
                text = firstText,
                imagePath = firstImagePath,
                accent = HarmonyPink,
                textTag = "memory_match_reveal_first",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = reveal.value
                        translationX = (1f - reveal.value) * -28f
                    }
            )
            RevealedMemoryCard(
                name = partnerName,
                text = partnerText,
                imagePath = partnerImagePath,
                accent = HarmonyPurpleLight,
                textTag = "memory_match_reveal_second",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = reveal.value
                        translationX = (1f - reveal.value) * 28f
                    }
            )
        }
        PrimaryMechanicButton(
            text = when {
                isSaving -> tr("Speichere …", "Saving …")
                restoredAnswer != null -> tr("Weiter", "Continue")
                else -> tr("Diese Erinnerung behalten", "Keep this memory")
            },
            enabled = !isSaving,
            onClick = onKeep,
            testTag = "memory_match_keep"
        )
    }
}

@Composable
private fun RevealedMemoryCard(
    name: String,
    text: String,
    imagePath: String?,
    accent: Color,
    textTag: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(accent.copy(alpha = 0.30f), HarmonySurface2, HarmonyBg)
                )
            )
            .border(1.5.dp, accent.copy(alpha = 0.58f), shape)
            .padding(13.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            color = accent,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
        if (!imagePath.isNullOrBlank()) {
            AsyncImage(
                model = File(imagePath),
                contentDescription = tr("Erinnerungsfoto von $name", "Memory photo by $name"),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(18.dp))
            )
            Spacer(Modifier.height(9.dp))
        }
        Text(
            text = text.ifBlank { tr("📷 Foto-Erinnerung", "📷 Photo memory") },
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag(textTag)
        )
    }
}

private fun appendSpokenText(current: String, spoken: String): String = when {
    spoken.isBlank() -> current
    current.isBlank() -> spoken.trim()
    else -> "${current.trim()} ${spoken.trim()}"
}

private fun buildSharedMemoryBody(
    firstName: String,
    firstText: String,
    firstImagePath: String?,
    partnerName: String,
    partnerText: String,
    partnerImagePath: String?
): String = buildString {
    append(firstName)
    append(":\n")
    append(firstText.trim().ifBlank { if (!firstImagePath.isNullOrBlank()) "📷 Foto-Erinnerung" else "–" })
    append("\n\n")
    append(partnerName)
    append(":\n")
    append(partnerText.trim().ifBlank { if (!partnerImagePath.isNullOrBlank()) "📷 Foto-Erinnerung" else "–" })
}

private fun copyMemoryGameImage(context: Context, uri: Uri): String? = runCatching {
    val directory = File(context.filesDir, "memory_game_images").apply { mkdirs() }
    val target = File(directory, "${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        target.outputStream().use { output -> input.copyTo(output) }
    } ?: return@runCatching null
    target.absolutePath
}.getOrNull()
