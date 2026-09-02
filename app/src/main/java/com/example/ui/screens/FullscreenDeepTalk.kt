package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.HarmonyDatabase
import com.example.data.model.MemoryDefaults
import com.example.data.model.MemoryEntryEntity
import com.example.data.model.MemoryEntryKind
import com.example.data.model.PairedChoiceAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.data.repository.RoomMemoryRepository
import com.example.ui.LocalAppLanguage
import com.example.ui.components.VoiceInputButton
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration
import java.util.UUID
import kotlinx.coroutines.launch

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

    var firstAnswer by remember(question, selectedAnswer) { mutableStateOf(restored?.first.orEmpty()) }
    var secondAnswer by remember(question, selectedAnswer) { mutableStateOf(restored?.second.orEmpty()) }
    var phase by remember(question, selectedAnswer) { mutableStateOf(if (restored != null) 3 else 0) }
    var discussionOpen by remember(question) { mutableStateOf(false) }
    var memorySaved by remember(question) { mutableStateOf(false) }
    var memorySaving by remember(question) { mutableStateOf(false) }

    val memoryRepository = remember(context.applicationContext) {
        RoomMemoryRepository(HarmonyDatabase.getInstance(context.applicationContext))
    }

    FullscreenMechanicShell(
        kicker = tr("💬 DEEP TALK", "💬 DEEP TALK"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr(
                "Antworte erst für dich. Deine Antwort bleibt bis zum Reveal verdeckt.",
                "Answer for yourself first. Your answer stays hidden until the reveal."
            )
            1 -> tr(
                "Deine Antwort ist sicher verdeckt.",
                "Your answer is safely hidden."
            )
            2 -> tr(
                "Jetzt antwortet ${profile.partnerName} unabhängig.",
                "Now ${profile.partnerName} answers independently."
            )
            else -> tr(
                "Jetzt seht ihr beide Antworten gleichzeitig.",
                "Now you can see both answers at the same time."
            )
        },
        modifier = modifier.testTag("deep_talk_board")
    ) {
        when (phase) {
            0 -> DeepTalkAnswerPane(
                name = profile.userName,
                value = firstAnswer,
                onValueChange = { firstAnswer = it },
                appLanguage = appLanguage,
                buttonText = tr("Antwort verdecken", "Hide answer"),
                onSubmit = {
                    if (firstAnswer.isNotBlank()) {
                        triggerMiniVibration(context, 38L)
                        phase = 1
                    }
                },
                testTag = "deep_talk_first"
            )

            1 -> HandoffPane(
                name = profile.partnerName,
                text = tr(
                    "Jetzt ist ${profile.partnerName} dran",
                    "Now it's ${profile.partnerName}'s turn"
                ),
                onReady = { phase = 2 }
            )

            2 -> DeepTalkAnswerPane(
                name = profile.partnerName,
                value = secondAnswer,
                onValueChange = { secondAnswer = it },
                appLanguage = appLanguage,
                buttonText = tr("Gemeinsam aufdecken", "Reveal together"),
                onSubmit = {
                    if (secondAnswer.isNotBlank()) {
                        triggerMiniVibration(context, 44L)
                        phase = 3
                    }
                },
                testTag = "deep_talk_second"
            )

            else -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    DeepTalkRevealCard(
                        name = profile.userName,
                        answer = firstAnswer,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                    DeepTalkRevealCard(
                        name = profile.partnerName,
                        answer = secondAnswer,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                    )
                }

                if (discussionOpen) {
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = tr(
                            "Nehmt euch einen Moment: Was überrascht euch an der Antwort des anderen?",
                            "Take a moment: what surprises you about each other's answer?"
                        ),
                        color = Color.White,
                        fontSize = 16.sp,
                        lineHeight = 21.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(HarmonyPurple.copy(alpha = 0.24f))
                            .border(1.dp, HarmonyLine, RoundedCornerShape(18.dp))
                            .padding(13.dp)
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    PrimaryMechanicButton(
                        text = tr("💬 Darüber reden", "💬 Talk about it"),
                        onClick = {
                            triggerMiniVibration(context, 28L)
                            discussionOpen = !discussionOpen
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "deep_talk_discuss"
                    )
                    PrimaryMechanicButton(
                        text = when {
                            memorySaved -> tr("📌 Gespeichert", "📌 Saved")
                            memorySaving -> tr("Speichere …", "Saving …")
                            else -> tr("📌 Als Memory", "📌 Save memory")
                        },
                        enabled = !memorySaving && !memorySaved,
                        onClick = {
                            memorySaving = true
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
                                                body = buildString {
                                                    append(profile.userName)
                                                    append(": ")
                                                    append(firstAnswer.trim())
                                                    append("\n\n")
                                                    append(profile.partnerName)
                                                    append(": ")
                                                    append(secondAnswer.trim())
                                                },
                                                createdAt = now,
                                                updatedAt = now
                                            )
                                        )
                                    )
                                }.onSuccess {
                                    memorySaved = true
                                    triggerMiniVibration(context, 42L)
                                }
                                memorySaving = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        testTag = "deep_talk_memory"
                    )
                }

                Spacer(Modifier.height(10.dp))
                PrimaryMechanicButton(
                    text = tr("❤️ Behalten & weiter", "❤️ Keep & continue"),
                    onClick = {
                        if (firstAnswer.isNotBlank() && secondAnswer.isNotBlank()) {
                            onPick(
                                PairedChoiceAnswerCodec.encode(
                                    firstAnswer.trim(),
                                    secondAnswer.trim()
                                )
                            )
                        }
                    },
                    testTag = "deep_talk_continue"
                )
            }
        }
    }
}

@Composable
private fun DeepTalkAnswerPane(
    name: String,
    value: String,
    onValueChange: (String) -> Unit,
    appLanguage: String,
    buttonText: String,
    onSubmit: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = name,
            color = HarmonyPink,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black
        )
        Spacer(Modifier.height(12.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = {
                    Text(
                        tr("Sag, was du wirklich denkst …", "Say what you really think …"),
                        color = HarmonyMuted
                    )
                },
                minLines = 7,
                maxLines = 10,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .testTag("${testTag}_input")
            )
            Spacer(Modifier.width(8.dp))
            VoiceInputButton(
                appLanguage = appLanguage,
                onTextTranscribed = { spoken ->
                    onValueChange(if (value.isBlank()) spoken else "$value $spoken")
                }
            )
        }
        Spacer(Modifier.height(14.dp))
        PrimaryMechanicButton(
            text = buttonText,
            enabled = value.isNotBlank(),
            onClick = onSubmit,
            testTag = "${testTag}_submit"
        )
    }
}

@Composable
private fun DeepTalkRevealCard(
    name: String,
    answer: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.34f),
                        HarmonySurface2.copy(alpha = 0.96f)
                    )
                )
            )
            .border(1.dp, HarmonyPink.copy(alpha = 0.30f), RoundedCornerShape(24.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = name,
            color = HarmonyPink,
            fontSize = 17.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = answer.ifBlank { "–" },
            color = Color.White,
            fontSize = 17.sp,
            lineHeight = 23.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}
