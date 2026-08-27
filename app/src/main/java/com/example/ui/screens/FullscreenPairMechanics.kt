package com.example.ui.screens

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PairedChoiceAnswerCodec
import com.example.data.model.PredictionAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
internal fun PartnerPredictionBoard(
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
    var prediction by remember(question, selectedAnswer) { mutableStateOf(restored?.prediction) }
    var actual by remember(question, selectedAnswer) { mutableStateOf(restored?.actual) }
    var phase by remember(question, selectedAnswer) { mutableStateOf(if (restored != null) 3 else 0) }

    FullscreenMechanicShell(
        kicker = tr("🔮 PARTNER-PROGNOSE", "🔮 PARTNER PREDICTION"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr("Tippe heimlich, was ${profile.partnerName} wählen wird.", "Secretly predict what ${profile.partnerName} will choose.")
            1 -> tr("Dein Tipp bleibt verborgen.", "Your prediction stays hidden.")
            2 -> tr("Jetzt entscheidet ${profile.partnerName} wirklich.", "Now ${profile.partnerName} chooses for real.")
            else -> tr("Zeit für den Reveal.", "Time for the reveal.")
        },
        modifier = modifier.testTag("partner_prediction_board")
    ) {
        when (phase) {
            0 -> LargeOptionGrid(items, prediction, onSelect = { item ->
                triggerMiniVibration(context, 36L)
                prediction = item.raw
                phase = 1
            }, tagPrefix = "prediction_option")

            1 -> HandoffPane(
                name = profile.partnerName,
                text = tr("Jetzt ist ${profile.partnerName} dran", "Now it's ${profile.partnerName}'s turn"),
                onReady = { phase = 2 }
            )

            2 -> LargeOptionGrid(items, actual, onSelect = { item ->
                triggerMiniVibration(context, 42L)
                actual = item.raw
                phase = 3
            }, tagPrefix = "prediction_actual_option")

            else -> {
                val predictedIndex = items.indexOfFirst { it.raw == prediction }
                val actualIndex = items.indexOfFirst { it.raw == actual }
                val distance = if (predictedIndex >= 0 && actualIndex >= 0) abs(predictedIndex - actualIndex) else Int.MAX_VALUE
                val result = when (distance) {
                    0 -> tr("✨ Treffer", "✨ Perfect hit")
                    1 -> tr("💫 Knapp daneben", "💫 Very close")
                    else -> tr("😮 Komplett überrascht", "😮 Total surprise")
                }
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(result, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Black, textAlign = TextAlign.Center)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        LargeOptionCard(
                            item = items.firstOrNull { it.raw == prediction } ?: MechanicOption("", "–"),
                            selected = distance == 0,
                            onClick = {},
                            badge = tr("Dein Tipp", "Your prediction"),
                            modifier = Modifier.weight(1f).heightIn(min = 155.dp)
                        )
                        LargeOptionCard(
                            item = items.firstOrNull { it.raw == actual } ?: MechanicOption("", "–"),
                            selected = true,
                            onClick = {},
                            badge = profile.partnerName,
                            modifier = Modifier.weight(1f).heightIn(min = 155.dp)
                        )
                    }
                    PrimaryMechanicButton(
                        text = tr("Reveal speichern & weiter", "Save reveal & continue"),
                        onClick = {
                            val predicted = prediction
                            val chosen = actual
                            if (predicted != null && chosen != null) {
                                onPick(PredictionAnswerCodec.encode(predicted, chosen))
                            }
                        },
                        testTag = "prediction_continue"
                    )
                }
            }
        }
    }
}

@Composable
internal fun SecretChoiceBoard(
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
    var first by remember(question, selectedAnswer) { mutableStateOf(restored?.first) }
    var second by remember(question, selectedAnswer) { mutableStateOf(restored?.second) }
    var phase by remember(question, selectedAnswer) { mutableStateOf(if (restored != null) 3 else 0) }

    FullscreenMechanicShell(
        kicker = tr("🤫 GEHEIME WAHL", "🤫 SECRET CHOICE"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr("Wähle. Deine Karte bleibt verdeckt.", "Choose. Your card stays face down.")
            1 -> tr("Keine Antwort ist sichtbar.", "No answer is visible.")
            2 -> tr("${profile.partnerName} wählt jetzt ebenfalls geheim.", "${profile.partnerName} now chooses in secret too.")
            else -> tr("Beide Karten werden gleichzeitig aufgedeckt.", "Both cards are revealed together.")
        },
        modifier = modifier.testTag("secret_choice_board")
    ) {
        when (phase) {
            0 -> LargeOptionGrid(items, first, onSelect = { item ->
                triggerMiniVibration(context, 36L)
                first = item.raw
                phase = 1
            }, tagPrefix = "secret_first_option")

            1 -> HandoffPane(
                name = profile.partnerName,
                text = tr("Übergib das Handy an ${profile.partnerName}", "Pass the phone to ${profile.partnerName}"),
                onReady = { phase = 2 }
            )

            2 -> LargeOptionGrid(items, second, onSelect = { item ->
                triggerMiniVibration(context, 42L)
                second = item.raw
                phase = 3
            }, tagPrefix = "secret_second_option")

            else -> {
                val same = first != null && first == second
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (same) tr("🧲 Gleiche Wellenlänge", "🧲 Same wavelength") else tr("↔ Zwei Perspektiven", "↔ Two perspectives"),
                        color = Color.White,
                        fontSize = 27.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(if (same) 5.dp else 18.dp)
                    ) {
                        LargeOptionCard(
                            item = items.firstOrNull { it.raw == first } ?: MechanicOption("", "–"),
                            selected = same,
                            onClick = {},
                            badge = profile.userName,
                            modifier = Modifier.weight(1f).heightIn(min = 160.dp)
                        )
                        LargeOptionCard(
                            item = items.firstOrNull { it.raw == second } ?: MechanicOption("", "–"),
                            selected = same,
                            onClick = {},
                            badge = profile.partnerName,
                            modifier = Modifier.weight(1f).heightIn(min = 160.dp)
                        )
                    }
                    PrimaryMechanicButton(
                        text = tr("Antworten festhalten & weiter", "Keep answers & continue"),
                        onClick = {
                            val a = first
                            val b = second
                            if (a != null && b != null) onPick(PairedChoiceAnswerCodec.encode(a, b))
                        },
                        testTag = "secret_choice_continue"
                    )
                }
            }
        }
    }
}

@Composable
internal fun ScaleMatchBoard(
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
    var first by remember(question, selectedAnswer) { mutableStateOf(restored?.first) }
    var second by remember(question, selectedAnswer) { mutableStateOf(restored?.second) }
    var phase by remember(question, selectedAnswer) { mutableStateOf(if (restored != null) 3 else 0) }
    var sliderValue by remember(question, selectedAnswer) { mutableStateOf(1f) }
    val max = items.size.coerceAtLeast(2)

    FullscreenMechanicShell(
        kicker = tr("🎚️ SKALEN-MATCH", "🎚️ SCALE MATCH"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr("Stelle deinen Wert ein. ${profile.partnerName} sieht ihn noch nicht.", "Set your value. ${profile.partnerName} won't see it yet.")
            1 -> tr("Dein Marker ist verborgen.", "Your marker is hidden.")
            2 -> tr("Jetzt setzt ${profile.partnerName} den eigenen Marker.", "Now ${profile.partnerName} sets their marker.")
            else -> tr("Eure Marker liegen jetzt auf derselben Skala.", "Your markers now share one scale.")
        },
        modifier = modifier.testTag("scale_match_board")
    ) {
        when (phase) {
            0, 2 -> {
                val selectedIndex = sliderValue.roundToInt().coerceIn(1, items.size.coerceAtLeast(1)) - 1
                val selected = items.getOrNull(selectedIndex)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .clip(RoundedCornerShape(26.dp))
                            .background(HarmonyPurple.copy(alpha = 0.22f))
                            .border(1.dp, HarmonyPink.copy(alpha = 0.35f), RoundedCornerShape(26.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = selected?.label ?: sliderValue.roundToInt().toString(),
                            color = Color.White,
                            fontSize = 27.sp,
                            lineHeight = 32.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(18.dp)
                        )
                    }
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 1f..max.toFloat(),
                            steps = (max - 2).coerceAtLeast(0),
                            modifier = Modifier.fillMaxWidth().testTag("scale_slider")
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(items.firstOrNull()?.label ?: "1", color = HarmonyMuted, fontSize = 12.sp, modifier = Modifier.weight(1f))
                            Text(items.lastOrNull()?.label ?: max.toString(), color = HarmonyMuted, fontSize = 12.sp, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                    }
                    PrimaryMechanicButton(
                        text = if (phase == 0) tr("Meinen Wert verdecken", "Hide my value") else tr("Wert festlegen", "Set value"),
                        onClick = {
                            val raw = selected?.raw
                            if (raw != null) {
                                triggerMiniVibration(context, 40L)
                                if (phase == 0) {
                                    first = raw
                                    phase = 1
                                } else {
                                    second = raw
                                    phase = 3
                                }
                            }
                        }
                    )
                }
            }

            1 -> HandoffPane(
                name = profile.partnerName,
                text = tr("Jetzt ist ${profile.partnerName} an der Skala", "Now ${profile.partnerName} takes the scale"),
                onReady = {
                    sliderValue = 1f
                    phase = 2
                }
            )

            else -> {
                val firstIndex = items.indexOfFirst { it.raw == first }.coerceAtLeast(0)
                val secondIndex = items.indexOfFirst { it.raw == second }.coerceAtLeast(0)
                val gap = abs(firstIndex - secondIndex)
                val match = (100 - gap * 22).coerceIn(0, 100)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tr("$match % auf einer Wellenlänge", "$match% on the same wavelength"),
                        color = Color.White,
                        fontSize = 28.sp,
                        lineHeight = 34.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(175.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(HarmonyPurple.copy(alpha = 0.22f))
                            .border(1.dp, HarmonyPink.copy(alpha = 0.4f), RoundedCornerShape(28.dp))
                            .padding(20.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceEvenly) {
                            ScaleRevealLine(profile.userName, items.getOrNull(firstIndex)?.label ?: "–", firstIndex, items.size)
                            ScaleRevealLine(profile.partnerName, items.getOrNull(secondIndex)?.label ?: "–", secondIndex, items.size)
                        }
                    }
                    PrimaryMechanicButton(
                        text = tr("Match speichern & weiter", "Save match & continue"),
                        onClick = {
                            val a = first
                            val b = second
                            if (a != null && b != null) onPick(PairedChoiceAnswerCodec.encode(a, b))
                        },
                        testTag = "scale_match_continue"
                    )
                }
            }
        }
    }
}

@Composable
private fun ScaleRevealLine(name: String, label: String, index: Int, size: Int) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(name, color = HarmonyPinkSoft, fontWeight = FontWeight.ExtraBold, fontSize = 13.sp)
            Text(label, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            repeat(size.coerceAtLeast(1)) { marker ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (marker == index) 12.dp else 5.dp)
                        .clip(CircleShape)
                        .background(if (marker == index) HarmonyPink else Color.White.copy(alpha = 0.16f))
                )
                if (marker < size - 1) Spacer(Modifier.width(4.dp))
            }
        }
    }
}
