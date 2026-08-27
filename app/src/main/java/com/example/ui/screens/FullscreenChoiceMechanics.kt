package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface2
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration

@Composable
internal fun WhoWouldBoard(
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
    val userItem = items.firstOrNull { it.raw.contains("{user}") } ?: items.getOrNull(0)
    val partnerItem = items.firstOrNull { it.raw.contains("{partner}") } ?: items.getOrNull(1)
    val bothItem = items.firstOrNull { it.label.equals("Beide", true) || it.label.equals("Both", true) } ?: items.getOrNull(2)
    val nobodyItem = items.firstOrNull {
        it.label.equals("Niemand", true) || it.label.equals("Nobody", true) || it.label.equals("Neither", true)
    } ?: items.getOrNull(3)

    FullscreenMechanicShell(
        kicker = tr("👤 PAARLABOR", "👤 COUPLE LAB"),
        question = prompt,
        instruction = tr(
            "Tippe direkt auf die Person – oder auf euch beide in der Mitte.",
            "Tap the person directly – or both of you in the middle."
        ),
        modifier = modifier.testTag("who_would_board")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PersonChoiceCard(
                    name = profile.userName,
                    avatarPath = profile.userAvatarPath,
                    selected = selectedAnswer == userItem?.raw,
                    onClick = {
                        userItem?.let {
                            triggerMiniVibration(context, 38L)
                            onPick(it.raw)
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    tag = "who_user"
                )
                PersonChoiceCard(
                    name = profile.partnerName,
                    avatarPath = profile.partnerAvatarPath,
                    selected = selectedAnswer == partnerItem?.raw,
                    onClick = {
                        partnerItem?.let {
                            triggerMiniVibration(context, 38L)
                            onPick(it.raw)
                        }
                    },
                    modifier = Modifier.weight(1f).fillMaxSize(),
                    tag = "who_partner"
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(94.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                bothItem?.let { item ->
                    LargeOptionCard(
                        item = item,
                        selected = selectedAnswer == item.raw,
                        onClick = {
                            triggerMiniVibration(context, 38L)
                            onPick(item.raw)
                        },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        testTag = "who_both",
                        badge = "◉ ◉"
                    )
                }
                nobodyItem?.let { item ->
                    LargeOptionCard(
                        item = item,
                        selected = selectedAnswer == item.raw,
                        onClick = {
                            triggerMiniVibration(context, 38L)
                            onPick(item.raw)
                        },
                        modifier = Modifier.weight(0.72f).fillMaxSize(),
                        testTag = "who_nobody"
                    )
                }
            }
        }
    }
}

@Composable
private fun PersonChoiceCard(
    name: String,
    avatarPath: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    tag: String
) {
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (selected) HarmonyPink.copy(alpha = 0.52f) else HarmonyPurple.copy(alpha = 0.30f),
                        HarmonySurface2,
                        HarmonyBg
                    )
                )
            )
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) HarmonyPinkSoft else Color.White.copy(alpha = 0.18f),
                shape
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(122.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
                .border(2.dp, Color.White.copy(alpha = 0.55f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!avatarPath.isNullOrBlank()) {
                AsyncImage(
                    model = avatarPath,
                    contentDescription = name,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
                )
            } else {
                Text(
                    name.trim().take(1).uppercase().ifBlank { "?" },
                    color = Color.White,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(Modifier.height(16.dp))
        Text(
            name,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
internal fun PriorityPokerBoard(
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
    var selected by remember(question, selectedAnswer) {
        mutableStateOf(selectedAnswer?.takeIf { answer -> items.any { it.raw == answer } })
    }

    FullscreenMechanicShell(
        kicker = tr("🃏 PRIORITÄTEN-POKER", "🃏 PRIORITY POKER"),
        question = prompt,
        instruction = tr(
            "Zieh genau eine Karte nach vorn. Die anderen werden abgeworfen.",
            "Pull exactly one card forward. The others are discarded."
        ),
        modifier = modifier.testTag("priority_poker_board")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            LargeOptionGrid(
                items = items,
                selectedRaw = selected,
                onSelect = { item ->
                    triggerMiniVibration(context, 34L)
                    selected = item.raw
                },
                modifier = Modifier.weight(1f),
                tagPrefix = "poker_card"
            )
            Spacer(Modifier.height(14.dp))
            PrimaryMechanicButton(
                text = tr("Diese Karte spiele ich", "Play this card"),
                enabled = selected != null,
                onClick = { selected?.let(onPick) },
                testTag = "poker_submit"
            )
        }
    }
}

@Composable
internal fun MatchTournamentBoard(
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
    val existingIndex = items.indexOfFirst { it.raw == selectedAnswer }
    var championIndex by remember(question, selectedAnswer) { mutableStateOf(existingIndex.coerceAtLeast(0)) }
    var challengerIndex by remember(question, selectedAnswer) { mutableStateOf(1) }
    var finished by remember(question, selectedAnswer) { mutableStateOf(existingIndex >= 0) }

    FullscreenMechanicShell(
        kicker = tr("⚔️ MATCHCHECK", "⚔️ MATCH CHECK"),
        question = prompt,
        instruction = if (finished) {
            tr("Dein Sieger steht fest.", "Your winner is set.")
        } else {
            tr(
                "Zwei Optionen treten gegeneinander an. Der Sieger bleibt im Turnier.",
                "Two options face off. The winner stays in the tournament."
            )
        },
        modifier = modifier.testTag("match_tournament_board")
    ) {
        when {
            items.isEmpty() -> Unit
            items.size == 1 -> Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LargeOptionCard(
                    items.first(),
                    true,
                    {},
                    modifier = Modifier.fillMaxWidth().weight(1f)
                )
                Spacer(Modifier.height(14.dp))
                PrimaryMechanicButton(
                    tr("Sieger übernehmen", "Use winner"),
                    onClick = { onPick(items.first().raw) }
                )
            }
            finished -> {
                val winner = items.getOrNull(championIndex) ?: items.first()
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🏆", fontSize = 64.sp)
                    LargeOptionCard(
                        item = winner,
                        selected = true,
                        onClick = {},
                        badge = tr("Dein Sieger", "Your winner"),
                        modifier = Modifier.fillMaxWidth().heightIn(min = 190.dp)
                    )
                    PrimaryMechanicButton(
                        tr("Sieger speichern & weiter", "Save winner & continue"),
                        onClick = { onPick(winner.raw) },
                        testTag = "tournament_submit"
                    )
                }
            }
            else -> {
                val safeChampion = championIndex.coerceIn(items.indices)
                val safeChallenger = challengerIndex.coerceIn(1, items.lastIndex)
                val champion = items[safeChampion]
                val challenger = items[safeChallenger]
                Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.SpaceBetween) {
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        LargeOptionCard(
                            item = champion,
                            selected = false,
                            onClick = {
                                triggerMiniVibration(context, 42L)
                                if (challengerIndex >= items.lastIndex) finished = true
                                else challengerIndex += 1
                            },
                            badge = tr("Portal A", "Portal A"),
                            modifier = Modifier.weight(1f).fillMaxSize(),
                            testTag = "tournament_left"
                        )
                        LargeOptionCard(
                            item = challenger,
                            selected = false,
                            onClick = {
                                triggerMiniVibration(context, 42L)
                                championIndex = safeChallenger
                                if (challengerIndex >= items.lastIndex) finished = true
                                else challengerIndex += 1
                            },
                            badge = tr("Portal B", "Portal B"),
                            modifier = Modifier.weight(1f).fillMaxSize(),
                            testTag = "tournament_right"
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text(
                        text = tr(
                            "Duell ${safeChallenger}/${items.lastIndex}",
                            "Duel ${safeChallenger}/${items.lastIndex}"
                        ),
                        color = Color.White.copy(alpha = 0.68f),
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
internal fun ScenarioBoard(
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
    var selected by remember(question, selectedAnswer) {
        mutableStateOf(selectedAnswer?.takeIf { answer -> items.any { it.raw == answer } })
    }
    val sceneEmoji = when {
        prompt.contains("insel", true) -> "🌴"
        prompt.contains("reise", true) || prompt.contains("unterwegs", true) -> "🗺️"
        prompt.contains("streit", true) -> "🌩️"
        prompt.contains("zukunft", true) -> "🔭"
        prompt.contains("nacht", true) -> "🌙"
        else -> "🧭"
    }

    FullscreenMechanicShell(
        kicker = tr("🎭 SZENARIO", "🎭 SCENARIO"),
        question = prompt,
        instruction = tr(
            "Entscheidet euch – die Geschichte geht mit eurer Wahl weiter.",
            "Choose – the story continues with your decision."
        ),
        modifier = modifier.testTag("scenario_board")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(28.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                HarmonyPurple.copy(alpha = 0.52f),
                                HarmonyPink.copy(alpha = 0.24f),
                                HarmonySurface2
                            )
                        )
                    )
                    .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(sceneEmoji, fontSize = 60.sp)
            }
            Spacer(Modifier.height(12.dp))
            LargeOptionGrid(
                items = items,
                selectedRaw = selected,
                onSelect = { item ->
                    triggerMiniVibration(context, 34L)
                    selected = item.raw
                },
                modifier = Modifier.weight(1f),
                tagPrefix = "scenario_option"
            )
            Spacer(Modifier.height(12.dp))
            PrimaryMechanicButton(
                text = tr("Entscheidung treffen", "Make decision"),
                enabled = selected != null,
                onClick = { selected?.let(onPick) },
                testTag = "scenario_submit"
            )
        }
    }
}
