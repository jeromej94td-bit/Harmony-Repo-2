package com.example.ui.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.db.HarmonyDatabase
import com.example.data.model.MemoryDefaults
import com.example.data.model.MemoryEntryEntity
import com.example.data.model.MemoryEntryKind
import com.example.data.model.MemoryMatchAnswerCodec
import com.example.data.model.PairedChoiceAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.data.repository.RoomMemoryRepository
import com.example.ui.LocalAppLanguage
import com.example.ui.components.VoiceInputButton
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface
import com.example.ui.tr
import java.io.File
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
internal fun MemoryMatchBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appLanguage = LocalAppLanguage.current.code
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val restored = remember(selectedAnswer) { selectedAnswer?.let(MemoryMatchAnswerCodec::decode) }
    var text by remember(question, selectedAnswer) { mutableStateOf(restored?.text.orEmpty()) }
    var imagePath by remember(question, selectedAnswer) { mutableStateOf(restored?.imagePath) }
    var isSaving by remember(question) { mutableStateOf(false) }
    val photoFallback = tr("Foto-Erinnerung", "Photo memory")
    val memoryRepository = remember(context.applicationContext) {
        RoomMemoryRepository(HarmonyDatabase.getInstance(context.applicationContext))
    }

    val imagePicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            scope.launch {
                imagePath = withContext(Dispatchers.IO) { copyMemoryGameImage(context, uri) }
            }
        }
    }

    FullscreenMechanicShell(
        kicker = tr("🧠 ERINNERUNGS-MATCH", "🧠 MEMORY MATCH"),
        question = prompt,
        instruction = tr(
            "Halte den Moment als Text, Sprache oder Foto fest. Er landet direkt in Harmony Memory.",
            "Capture the moment with text, voice or a photo. It goes straight into Harmony Memory."
        ),
        modifier = modifier.testTag("memory_match_board")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(HarmonySurface.copy(alpha = 0.72f))
                    .border(1.dp, HarmonyLine, RoundedCornerShape(26.dp))
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    if (!imagePath.isNullOrBlank()) {
                        AsyncImage(
                            model = File(imagePath!!),
                            contentDescription = tr("Ausgewählte Erinnerung", "Selected memory"),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(155.dp)
                                .clip(RoundedCornerShape(20.dp))
                        )
                        Spacer(Modifier.height(10.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = text,
                            onValueChange = { text = it },
                            placeholder = {
                                Text(tr("Welche Erinnerung öffnet sich?", "Which memory opens up?"))
                            },
                            minLines = 4,
                            maxLines = 7,
                            modifier = Modifier.weight(1f).testTag("memory_match_text")
                        )
                        Spacer(Modifier.width(8.dp))
                        VoiceInputButton(
                            appLanguage = appLanguage,
                            onTextTranscribed = { spoken ->
                                text = if (text.isBlank()) spoken else "$text $spoken"
                            }
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PrimaryMechanicButton(
                    text = if (imagePath == null) tr("📷 Foto hinzufügen", "📷 Add photo") else tr("📷 Foto ändern", "📷 Change photo"),
                    onClick = { imagePicker.launch("image/*") },
                    modifier = Modifier.weight(1f)
                )
                PrimaryMechanicButton(
                    text = if (isSaving) tr("Speichere …", "Saving …") else tr("Memory speichern", "Save memory"),
                    enabled = !isSaving && (text.isNotBlank() || !imagePath.isNullOrBlank()),
                    onClick = {
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
                                            body = text.trim().ifBlank { photoFallback },
                                            previewImageUrl = imagePath?.let { "file://$it" },
                                            createdAt = now,
                                            updatedAt = now
                                        )
                                    )
                                )
                            }
                            isSaving = false
                            onPick(MemoryMatchAnswerCodec.encode(text.trim(), imagePath))
                        }
                    },
                    modifier = Modifier.weight(1f),
                    testTag = "memory_match_save"
                )
            }
        }
    }
}

private fun copyMemoryGameImage(context: Context, uri: Uri): String? = runCatching {
    val directory = File(context.filesDir, "memory_game_images").apply { mkdirs() }
    val target = File(directory, "${UUID.randomUUID()}.jpg")
    context.contentResolver.openInputStream(uri)?.use { input ->
        target.outputStream().use { output -> input.copyTo(output) }
    } ?: return@runCatching null
    target.absolutePath
}.getOrNull()

@Composable
internal fun DeepTalkBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val appLanguage = LocalAppLanguage.current.code
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val restored = remember(selectedAnswer) { selectedAnswer?.let(PairedChoiceAnswerCodec::decode) }
    var first by remember(question, selectedAnswer) { mutableStateOf(restored?.first.orEmpty()) }
    var second by remember(question, selectedAnswer) { mutableStateOf(restored?.second.orEmpty()) }
    var phase by remember(question, selectedAnswer) { mutableStateOf(if (restored != null) 3 else 0) }
    var isSavingMemory by remember(question) { mutableStateOf(false) }
    val memoryRepository = remember(context.applicationContext) {
        RoomMemoryRepository(HarmonyDatabase.getInstance(context.applicationContext))
    }
    val memoryBodyPrefix = tr("Unsere Antworten:", "Our answers:")

    FullscreenMechanicShell(
        kicker = tr("💬 DEEP TALK", "💬 DEEP TALK"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr("Antworte erst für dich. Deine Antwort bleibt verdeckt.", "Answer for yourself first. Your answer stays hidden.")
            1 -> tr("Antwort verdeckt gespeichert.", "Answer saved face down.")
            2 -> tr("Jetzt antwortet ${profile.partnerName} unabhängig.", "Now ${profile.partnerName} answers independently.")
            else -> tr("Bereit zum gemeinsamen Aufdecken.", "Ready for the shared reveal.")
        },
        modifier = modifier.testTag("deep_talk_board")
    ) {
        when (phase) {
            0, 2 -> {
                val value = if (phase == 0) first else second
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = value,
                            onValueChange = { if (phase == 0) first = it else second = it },
                            minLines = 7,
                            maxLines = 10,
                            placeholder = {
                                Text(tr("Schreib, was du wirklich denkst …", "Write what you really think …"))
                            },
                            modifier = Modifier.weight(1f).testTag("deep_talk_input")
                        )
                        Spacer(Modifier.width(8.dp))
                        VoiceInputButton(
                            appLanguage = appLanguage,
                            onTextTranscribed = { spoken ->
                                if (phase == 0) {
                                    first = if (first.isBlank()) spoken else "$first $spoken"
                                } else {
                                    second = if (second.isBlank()) spoken else "$second $spoken"
                                }
                            }
                        )
                    }
                    PrimaryMechanicButton(
                        text = if (phase == 0) {
                            tr("Antwort verdecken", "Hide answer")
                        } else {
                            tr("Antwort verdecken & aufdecken", "Hide answer & reveal")
                        },
                        enabled = value.isNotBlank(),
                        onClick = { phase = if (phase == 0) 1 else 3 }
                    )
                }
            }

            1 -> HandoffPane(
                name = profile.partnerName,
                text = tr("Jetzt ist ${profile.partnerName} dran", "Now it's ${profile.partnerName}'s turn"),
                onReady = { phase = 2 }
            )

            else -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                RevealTextCard(profile.userName, first)
                Spacer(Modifier.height(10.dp))
                RevealTextCard(profile.partnerName, second)
                Spacer(Modifier.height(12.dp))
                PrimaryMechanicButton(
                    text = tr("💬 Darüber reden & weiter", "💬 Talk about it & continue"),
                    onClick = {
                        onPick(PairedChoiceAnswerCodec.encode(first.trim(), second.trim()))
                    },
                    testTag = "deep_talk_continue"
                )
                Spacer(Modifier.height(8.dp))
                PrimaryMechanicButton(
                    text = if (isSavingMemory) tr("Speichere …", "Saving …") else tr("❤️ Als Memory behalten", "❤️ Keep as memory"),
                    enabled = !isSavingMemory,
                    onClick = {
                        isSavingMemory = true
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
                                            body = "$memoryBodyPrefix\n${profile.userName}: ${first.trim()}\n${profile.partnerName}: ${second.trim()}",
                                            createdAt = now,
                                            updatedAt = now
                                        )
                                    )
                                )
                            }
                            isSavingMemory = false
                            onPick(PairedChoiceAnswerCodec.encode(first.trim(), second.trim()))
                        }
                    },
                    testTag = "deep_talk_memory"
                )
            }
        }
    }
}

@Composable
private fun RevealTextCard(name: String, text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .background(HarmonyPurple.copy(alpha = 0.22f))
            .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(22.dp))
            .padding(15.dp)
    ) {
        Text(name, color = HarmonyPinkSoft, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(6.dp))
        Text(
            text,
            color = Color.White,
            fontSize = 16.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 6,
            overflow = TextOverflow.Ellipsis
        )
    }
}
