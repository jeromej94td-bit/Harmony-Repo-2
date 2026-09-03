package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PairedChoiceAnswerCodec
import com.example.data.model.PredictionAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface2
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration
import kotlin.math.abs
import kotlin.math.roundToInt

internal data class PartnerPredictionProgress(
    val hits: Int,
    val completed: Int,
    val questionCount: Int
)

internal data class PairPredictionStoredResults(
    val questions: List<String>,
    val hits: BooleanArray
)

internal fun upsertPairPredictionResult(
    questions: List<String>,
    hits: BooleanArray,
    currentQuestion: String,
    currentHit: Boolean
): PairPredictionStoredResults {
    val existingCount = minOf(questions.size, hits.size)
    val updatedQuestions = questions.take(existingCount).toMutableList()
    val updatedHits = hits.take(existingCount).toMutableList()
    val existingIndex = updatedQuestions.indexOf(currentQuestion)

    if (existingIndex >= 0) {
        updatedHits[existingIndex] = currentHit
    } else {
        updatedQuestions += currentQuestion
        updatedHits += currentHit
    }

    return PairPredictionStoredResults(
        questions = updatedQuestions,
        hits = updatedHits.toBooleanArray()
    )
}

internal fun pairPredictionProgress(
    questions: List<String>,
    hits: BooleanArray,
    currentQuestion: String,
    currentHit: Boolean,
    questionCount: Int
): PartnerPredictionProgress {
    val updated = upsertPairPredictionResult(
        questions = questions,
        hits = hits,
        currentQuestion = currentQuestion,
        currentHit = currentHit
    )
    return PartnerPredictionProgress(
        hits = updated.hits.count { it },
        completed = updated.hits.size,
        questionCount = questionCount.coerceAtLeast(updated.hits.size)
    )
}

@Composable
internal fun PartnerPredictionRevealBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val restored = remember(selectedAnswer) { selectedAnswer?.let(PredictionAnswerCodec::decode) }
    var prediction by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.prediction) }
    var actual by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.actual) }
    var phase by rememberSaveable(question, selectedAnswer) { mutableStateOf(if (restored != null) 4 else 0) }
    var scoredQuestions by rememberSaveable { mutableStateOf(arrayListOf<String>()) }
    var scoredHits by rememberSaveable { mutableStateOf(BooleanArray(0)) }
    val configuration = LocalConfiguration.current
    val revealMetrics = PairMechanicRevealLayoutPolicy.metrics(
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )

    FullscreenMechanicShell(
        kicker = tr("🔮 PARTNER-PROGNOSE", "🔮 PARTNER PREDICTION"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr(
                "Tippe heimlich, was ${profile.partnerName} wählen wird.",
                "Secretly predict what ${profile.partnerName} will choose."
            )
            1 -> tr("Dein Tipp ist sicher verdeckt.", "Your prediction is safely hidden.")
            2 -> tr(
                "Jetzt entscheidet ${profile.partnerName} – dein Tipp bleibt unsichtbar.",
                "Now ${profile.partnerName} chooses – your prediction stays hidden."
            )
            3 -> tr("Beide Antworten sind gesperrt. Deckt sie gemeinsam auf.", "Both answers are locked. Reveal them together.")
            else -> tr("Das ist euer gemeinsamer Reveal.", "This is your shared reveal.")
        },
        modifier = modifier.testTag("partner_prediction_board")
    ) {
        when (phase) {
            0 -> LargeOptionGrid(
                items = items,
                selectedRaw = prediction,
                onSelect = { item ->
                    triggerMiniVibration(context, 36L)
                    prediction = item.raw
                    phase = 1
                },
                tagPrefix = "prediction_option"
            )

            1 -> PairPrivateHandoffPane(
                name = profile.partnerName,
                title = tr("Jetzt ist ${profile.partnerName} dran", "Now it's ${profile.partnerName}'s turn"),
                body = tr(
                    "Dein Tipp wurde verdeckt. Übergib das Handy, ohne zurückzugehen.",
                    "Your prediction is hidden. Pass the phone over without going back."
                ),
                buttonTag = "prediction_handoff_ready",
                onReady = { phase = 2 }
            )

            2 -> LargeOptionGrid(
                items = items,
                selectedRaw = actual,
                onSelect = { item ->
                    triggerMiniVibration(context, 42L)
                    actual = item.raw
                    phase = 3
                },
                tagPrefix = "prediction_actual_option"
            )

            3 -> PairRevealReadyPane(
                firstName = tr("Dein Tipp", "Your prediction"),
                secondName = profile.partnerName,
                title = tr("Bereit zum Aufdecken?", "Ready to reveal?"),
                body = tr(
                    "Beide Antworten sind festgelegt und noch verborgen.",
                    "Both answers are locked in and still hidden."
                ),
                rootTag = "prediction_reveal_ready",
                buttonTag = "prediction_reveal_button",
                onReveal = {
                    triggerMiniVibration(context, 55L)
                    phase = 4
                }
            )

            else -> {
                val predictedIndex = items.indexOfFirst { it.raw == prediction }
                val actualIndex = items.indexOfFirst { it.raw == actual }
                val distance = if (predictedIndex >= 0 && actualIndex >= 0) {
                    abs(predictedIndex - actualIndex)
                } else {
                    Int.MAX_VALUE
                }
                val hit = distance == 0
                val progress = pairPredictionProgress(
                    questions = scoredQuestions,
                    hits = scoredHits,
                    currentQuestion = question,
                    currentHit = hit,
                    questionCount = 8
                )
                val result = when (distance) {
                    0 -> tr("✨ Treffer", "✨ Perfect hit")
                    1 -> tr("💫 Knapp daneben", "💫 Very close")
                    else -> tr("😮 Überraschung", "😮 Surprise")
                }
                val finale = if (progress.completed >= progress.questionCount) {
                    val rate = if (progress.completed == 0) 0 else {
                        ((progress.hits.toFloat() / progress.completed.toFloat()) * 100f).roundToInt()
                    }
                    when {
                        rate >= 75 -> tr("Ihr lest euch fast die Gedanken.", "You almost read each other's minds.")
                        rate >= 50 -> tr(
                            "Ihr kennt euch stark – mit Raum für Überraschungen.",
                            "You know each other well – with room for surprises."
                        )
                        else -> tr(
                            "Ihr habt noch herrlich viel aneinander zu entdecken.",
                            "There is still wonderfully much to discover about each other."
                        )
                    }
                } else {
                    null
                }

                PairPredictionRevealResult(
                    result = result,
                    progress = progress,
                    finale = finale,
                    items = items,
                    prediction = prediction,
                    actual = actual,
                    partnerName = profile.partnerName,
                    hit = hit,
                    revealMetrics = revealMetrics,
                    onContinue = {
                        val predicted = prediction
                        val chosen = actual
                        if (predicted != null && chosen != null) {
                            val updated = upsertPairPredictionResult(
                                questions = scoredQuestions,
                                hits = scoredHits,
                                currentQuestion = question,
                                currentHit = hit
                            )
                            scoredQuestions = ArrayList(updated.questions)
                            scoredHits = updated.hits
                            onPick(PredictionAnswerCodec.encode(predicted, chosen))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun PairPredictionRevealResult(
    result: String,
    progress: PartnerPredictionProgress,
    finale: String?,
    items: List<MechanicOption>,
    prediction: String?,
    actual: String?,
    partnerName: String,
    hit: Boolean,
    revealMetrics: PairMechanicRevealMetrics,
    onContinue: () -> Unit
) {
    val flip = remember(result, prediction, actual) { Animatable(0f) }
    LaunchedEffect(result, prediction, actual) {
        flip.snapTo(0f)
        flip.animateTo(1f, tween(560, easing = FastOutSlowInEasing))
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = result,
                color = Color.White,
                fontSize = revealMetrics.predictionTitleSizeSp.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = tr(
                    "${progress.hits}/${progress.completed} Treffer in dieser Runde",
                    "${progress.hits}/${progress.completed} hits this round"
                ),
                color = HarmonyPinkSoft,
                fontSize = revealMetrics.predictionCountSizeSp.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LargeOptionCard(
                item = items.firstOrNull { it.raw == prediction } ?: MechanicOption("", "–"),
                selected = hit,
                onClick = {},
                badge = tr("Dein Tipp", "Your prediction"),
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = revealMetrics.predictionCardMinHeightDp.dp)
                    .graphicsLayer {
                        rotationY = (1f - flip.value) * -86f
                        scaleX = 0.90f + flip.value * 0.10f
                        alpha = 0.20f + flip.value * 0.80f
                        cameraDistance = 32f * density
                    },
                testTag = "prediction_result_guess"
            )
            LargeOptionCard(
                item = items.firstOrNull { it.raw == actual } ?: MechanicOption("", "–"),
                selected = true,
                onClick = {},
                badge = partnerName,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = revealMetrics.predictionCardMinHeightDp.dp)
                    .graphicsLayer {
                        rotationY = (1f - flip.value) * 86f
                        scaleX = 0.90f + flip.value * 0.10f
                        alpha = 0.20f + flip.value * 0.80f
                        cameraDistance = 32f * density
                    },
                testTag = "prediction_result_actual"
            )
        }

        if (finale != null) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(HarmonyPurple.copy(alpha = 0.25f))
                    .border(1.dp, HarmonyPink.copy(alpha = 0.35f), RoundedCornerShape(20.dp))
                    .padding(revealMetrics.finalePaddingDp.dp)
            ) {
                Text(
                    text = tr("Wie gut kennt ihr euch?", "How well do you know each other?"),
                    color = Color.White,
                    fontSize = revealMetrics.finaleTitleSizeSp.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = finale,
                    color = HarmonyMuted,
                    fontSize = revealMetrics.finaleBodySizeSp.sp,
                    lineHeight = revealMetrics.finaleBodyLineHeightSp.sp,
                    textAlign = TextAlign.Center
                )
            }
        }

        PrimaryMechanicButton(
            text = tr("Reveal speichern & weiter", "Save reveal & continue"),
            onClick = onContinue,
            testTag = "prediction_continue"
        )
    }
}

@Composable
internal fun SecretChoiceRevealBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val restored = remember(selectedAnswer) { selectedAnswer?.let(PairedChoiceAnswerCodec::decode) }
    var first by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.first) }
    var second by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.second) }
    var phase by rememberSaveable(question, selectedAnswer) { mutableStateOf(if (restored != null) 4 else 0) }
    val configuration = LocalConfiguration.current
    val revealMetrics = PairMechanicRevealLayoutPolicy.metrics(
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )

    FullscreenMechanicShell(
        kicker = tr("🤫 GEHEIME WAHL", "🤫 SECRET CHOICE"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr("Wähle deine Karte. Sie wird sofort verdeckt.", "Choose your card. It will be hidden immediately.")
            1 -> tr("Deine Antwort ist sicher verborgen.", "Your answer is safely hidden.")
            2 -> tr(
                "${profile.partnerName} wählt jetzt ebenfalls geheim.",
                "${profile.partnerName} now chooses in secret too."
            )
            3 -> tr("Beide Karten sind gesperrt. Deckt sie gemeinsam auf.", "Both cards are locked. Reveal them together.")
            else -> tr("Jetzt seht ihr beide Antworten gleichzeitig.", "Now you see both answers at the same time.")
        },
        modifier = modifier.testTag("secret_choice_board")
    ) {
        when (phase) {
            0 -> LargeOptionGrid(
                items = items,
                selectedRaw = first,
                onSelect = { item ->
                    triggerMiniVibration(context, 36L)
                    first = item.raw
                    phase = 1
                },
                tagPrefix = "secret_first_option"
            )

            1 -> PairPrivateHandoffPane(
                name = profile.partnerName,
                title = tr("Übergib das Handy an ${profile.partnerName}", "Pass the phone to ${profile.partnerName}"),
                body = tr(
                    "Deine Karte ist verdeckt. Die nächste Person kann sie nicht sehen.",
                    "Your card is face down. The next person cannot see it."
                ),
                buttonTag = "secret_handoff_ready",
                onReady = { phase = 2 }
            )

            2 -> LargeOptionGrid(
                items = items,
                selectedRaw = second,
                onSelect = { item ->
                    triggerMiniVibration(context, 42L)
                    second = item.raw
                    phase = 3
                },
                tagPrefix = "secret_second_option"
            )

            3 -> PairRevealReadyPane(
                firstName = profile.userName,
                secondName = profile.partnerName,
                title = tr("Bereit für euren Reveal?", "Ready for your reveal?"),
                body = tr(
                    "Keine Antwort ist sichtbar, bis ihr gemeinsam aufdeckt.",
                    "No answer is visible until you reveal together."
                ),
                rootTag = "secret_reveal_ready",
                buttonTag = "secret_reveal_button",
                onReveal = {
                    triggerMiniVibration(context, 55L)
                    phase = 4
                }
            )

            else -> {
                val same = first != null && first == second
                val flip = remember(question, first, second) { Animatable(0f) }
                LaunchedEffect(question, first, second) {
                    flip.snapTo(0f)
                    flip.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (same) {
                            tr("🧲 Gleiche Wellenlänge", "🧲 Same wavelength")
                        } else {
                            tr("↔ Zwei Perspektiven", "↔ Two perspectives")
                        },
                        color = Color.White,
                        fontSize = revealMetrics.secretTitleSizeSp.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(if (same) 7.dp else 18.dp)
                    ) {
                        LargeOptionCard(
                            item = items.firstOrNull { it.raw == first } ?: MechanicOption("", "–"),
                            selected = same,
                            onClick = {},
                            badge = profile.userName,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = revealMetrics.secretCardMinHeightDp.dp)
                                .graphicsLayer {
                                    rotationY = (1f - flip.value) * -88f
                                    translationX = if (same) 20f * flip.value else -14f * flip.value
                                    alpha = 0.18f + flip.value * 0.82f
                                    cameraDistance = 32f * density
                                },
                            testTag = "secret_result_first"
                        )
                        LargeOptionCard(
                            item = items.firstOrNull { it.raw == second } ?: MechanicOption("", "–"),
                            selected = same,
                            onClick = {},
                            badge = profile.partnerName,
                            modifier = Modifier
                                .weight(1f)
                                .heightIn(min = revealMetrics.secretCardMinHeightDp.dp)
                                .graphicsLayer {
                                    rotationY = (1f - flip.value) * 88f
                                    translationX = if (same) -20f * flip.value else 14f * flip.value
                                    alpha = 0.18f + flip.value * 0.82f
                                    cameraDistance = 32f * density
                                },
                            testTag = "secret_result_second"
                        )
                    }

                    PrimaryMechanicButton(
                        text = tr("Antworten festhalten & weiter", "Keep answers & continue"),
                        onClick = {
                            val a = first
                            val b = second
                            if (a != null && b != null) {
                                onPick(PairedChoiceAnswerCodec.encode(a, b))
                            }
                        },
                        testTag = "secret_choice_continue"
                    )
                }
            }
        }
    }
}

@Composable
private fun PairPrivateHandoffPane(
    name: String,
    title: String,
    body: String,
    buttonTag: String,
    onReady: () -> Unit
) {
    val compact = LocalConfiguration.current.screenHeightDp < 700
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 86.dp else 108.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
                .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.trim().take(1).uppercase().ifBlank { "?" },
                color = Color.White,
                fontSize = if (compact) 34.sp else 42.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.height(if (compact) 14.dp else 20.dp))
        Text(
            text = title,
            color = Color.White,
            fontSize = if (compact) 20.sp else 24.sp,
            lineHeight = if (compact) 25.sp else 30.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = body,
            color = HarmonyMuted,
            fontSize = if (compact) 13.sp else 15.sp,
            lineHeight = if (compact) 17.sp else 20.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(if (compact) 18.dp else 26.dp))
        PrimaryMechanicButton(
            text = tr("Ich bin bereit", "I'm ready"),
            onClick = onReady,
            testTag = buttonTag
        )
    }
}

@Composable
private fun PairRevealReadyPane(
    firstName: String,
    secondName: String,
    title: String,
    body: String,
    rootTag: String,
    buttonTag: String,
    onReveal: () -> Unit
) {
    val compact = LocalConfiguration.current.screenHeightDp < 700
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag(rootTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = title,
                color = Color.White,
                fontSize = if (compact) 21.sp else 26.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = body,
                color = HarmonyMuted,
                fontSize = if (compact) 13.sp else 15.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FaceDownHarmonyCard(firstName, Modifier.weight(1f))
            FaceDownHarmonyCard(secondName, Modifier.weight(1f))
        }

        PrimaryMechanicButton(
            text = tr("Gemeinsam aufdecken", "Reveal together"),
            onClick = onReveal,
            testTag = buttonTag
        )
    }
}

@Composable
private fun FaceDownHarmonyCard(
    name: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .heightIn(min = 132.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.82f),
                        HarmonySurface2,
                        HarmonyPink.copy(alpha = 0.34f)
                    )
                )
            )
            .border(1.5.dp, HarmonyPink.copy(alpha = 0.58f), shape)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.09f))
                    .border(1.dp, HarmonyLine, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("✦", color = HarmonyPinkSoft, fontSize = 24.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = tr("Verdeckt", "Hidden"),
                color = HarmonyMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
