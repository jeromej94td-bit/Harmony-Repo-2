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
import androidx.compose.runtime.saveable.rememberSaveable
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
    val configuration = context.resources.configuration
    val layoutMetrics = WhoWouldLayoutPolicy.metrics(
        screenWidthDp = configuration.screenWidthDp,
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )

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
                horizontalArrangement = Arrangement.spacedBy(layoutMetrics.rowGapDp.dp)
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
                    tag = "who_user",
                    layoutMetrics = layoutMetrics
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
                    tag = "who_partner",
                    layoutMetrics = layoutMetrics
                )
            }
            Spacer(Modifier.height(layoutMetrics.rowGapDp.dp))
            Row(
                modifier = Modifier.fillMaxWidth().height(layoutMetrics.bottomRowHeightDp.dp),
                horizontalArrangement = Arrangement.spacedBy(layoutMetrics.rowGapDp.dp)
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
    tag: String,
    layoutMetrics: WhoWouldLayoutMetrics
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
            .padding(layoutMetrics.cardPaddingDp.dp)
            .testTag(tag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(layoutMetrics.avatarSizeDp.dp)
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
                    fontSize = (layoutMetrics.nameSizeSp * 2).coerceAtMost(44).sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
        Spacer(Modifier.height(layoutMetrics.avatarNameGapDp.dp))
        Text(
            name,
            color = Color.White,
            fontSize = layoutMetrics.nameSizeSp.sp,
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
    var selected by rememberSaveable(question, selectedAnswer) {
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
    var championIndex by rememberSaveable(question, selectedAnswer) { mutableStateOf(existingIndex.coerceAtLeast(0)) }
    var challengerIndex by rememberSaveable(question, selectedAnswer) { mutableStateOf(1) }
    var finished by rememberSaveable(question, selectedAnswer) { mutableStateOf(existingIndex >= 0) }
    val configuration = context.resources.configuration
    val resultMetrics = MatchTournamentResultLayoutPolicy.metrics(
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )

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
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "🏆",
                        fontSize = resultMetrics.trophySizeSp.sp,
                        modifier = Modifier.testTag("tournament_trophy")
                    )
                    Spacer(Modifier.height(resultMetrics.gapDp.dp))
                    LargeOptionCard(
                        item = winner,
                        selected = true,
                        onClick = {},
                        badge = tr("Dein Sieger", "Your winner"),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .heightIn(min = resultMetrics.winnerMinHeightDp.dp),
                        testTag = "tournament_winner"
                    )
                    Spacer(Modifier.height(resultMetrics.gapDp.dp))
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
    var selected by rememberSaveable(question, selectedAnswer) {
        mutableStateOf(selectedAnswer?.takeIf { answer -> items.any { it.raw == answer } })
    }
    var journeyChoices by rememberSaveable { mutableStateOf(IntArray(0)) }
    var showJourneyResult by rememberSaveable(question) { mutableStateOf(false) }
    val configuration = context.resources.configuration
    val resultMetrics = ScenarioResultLayoutPolicy.metrics(
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )
    val playMetrics = ScenarioPlayLayoutPolicy.metrics(
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )

    val sceneEmoji = when {
        journeyChoices.size >= 7 -> "🏝️"
        journeyChoices.size >= 6 -> "🌅"
        journeyChoices.size >= 5 -> "🗺️"
        prompt.contains("insel", true) -> "🌴"
        prompt.contains("reise", true) || prompt.contains("unterwegs", true) -> "🗺️"
        prompt.contains("streit", true) -> "🌩️"
        prompt.contains("zukunft", true) -> "🔭"
        prompt.contains("nacht", true) -> "🌙"
        else -> "🧭"
    }

    val finalChoiceIndex = selected?.let { raw -> items.indexOfFirst { it.raw == raw }.coerceAtLeast(0) }
    val projectedJourney = if (finalChoiceIndex != null) journeyChoices + finalChoiceIndex.coerceIn(0, 3) else journeyChoices
    val dominantStyle = (0..3).maxByOrNull { style -> projectedJourney.count { it == style } } ?: 0
    val resultTitle = when (dominantStyle) {
        0 -> tr("Die Überlebenskünstler", "The Survivors")
        1 -> tr("Die Strategen", "The Strategists")
        2 -> tr("Die Genießer", "The Enjoyers")
        else -> tr("Das Chaos-Duo", "The Chaos Duo")
    }
    val resultText = when (dominantStyle) {
        0 -> tr("Ihr entscheidet direkt, praktisch und mit Blick auf das, was jetzt wirklich zählt.", "You decide directly, practically, and focus on what matters right now.")
        1 -> tr("Ihr denkt voraus, wägt ab und baut euch Schritt für Schritt einen gemeinsamen Plan.", "You think ahead, weigh options, and build a shared plan step by step.")
        2 -> tr("Ihr verliert auch im Abenteuer nicht aus den Augen, dass das Leben gemeinsam Spaß machen soll.", "Even in an adventure, you never forget that life together should be enjoyed.")
        else -> tr("Ihr seid spontan, überraschend und vermutlich genau deshalb schwer aus der Ruhe zu bringen.", "You are spontaneous, surprising, and probably hard to shake for exactly that reason.")
    }

    FullscreenMechanicShell(
        kicker = tr("🎭 SZENARIO", "🎭 SCENARIO"),
        question = if (showJourneyResult) tr("Eure Geschichte ist geschrieben", "Your story is written") else prompt,
        instruction = if (showJourneyResult) {
            tr("Acht Entscheidungen ergeben euren Spieltyp.", "Eight decisions reveal your play style.")
        } else {
            tr(
                "Entscheidet euch – die Geschichte geht mit eurer Wahl weiter.",
                "Choose – the story continues with your decision."
            )
        },
        modifier = modifier.testTag("scenario_board")
    ) {
        if (showJourneyResult) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(resultMetrics.trophyContainerDp.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    HarmonyPink.copy(alpha = 0.42f),
                                    HarmonyPurple.copy(alpha = 0.50f),
                                    HarmonySurface2
                                )
                            )
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.30f), CircleShape)
                        .testTag("scenario_result_trophy"),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🏆", fontSize = resultMetrics.trophySizeSp.sp)
                }
                Spacer(Modifier.height(resultMetrics.gapDp.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(26.dp))
                        .background(HarmonyPurple.copy(alpha = 0.25f))
                        .border(1.dp, HarmonyPink.copy(alpha = 0.36f), RoundedCornerShape(26.dp))
                        .padding(resultMetrics.cardPaddingDp.dp)
                        .testTag("scenario_result_card"),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = resultTitle,
                        color = Color.White,
                        fontSize = resultMetrics.titleSizeSp.sp,
                        lineHeight = resultMetrics.titleLineHeightSp.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(resultMetrics.gapDp.dp))
                    Text(
                        text = resultText,
                        color = Color.White.copy(alpha = 0.78f),
                        fontSize = resultMetrics.bodySizeSp.sp,
                        lineHeight = resultMetrics.bodyLineHeightSp.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(resultMetrics.gapDp.dp))
                    Text(
                        text = tr("8 Entscheidungen · 1 gemeinsamer Weg", "8 decisions · 1 shared path"),
                        color = HarmonyPinkSoft,
                        fontSize = resultMetrics.metaSizeSp.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(resultMetrics.gapDp.dp))
                PrimaryMechanicButton(
                    text = tr("Geschichte speichern & weiter", "Save story & continue"),
                    onClick = {
                        val answer = selected
                        val index = finalChoiceIndex
                        if (answer != null && index != null) {
                            journeyChoices = journeyChoices + index.coerceIn(0, 3)
                            onPick(answer)
                        }
                    },
                    testTag = "scenario_finale_submit"
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                if (playMetrics.showScene) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(playMetrics.sceneHeightDp.dp)
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
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(sceneEmoji, fontSize = playMetrics.sceneEmojiSp.sp)
                            if (playMetrics.showChapterLabel && journeyChoices.isNotEmpty()) {
                                Text(
                                    text = tr("Kapitel ${journeyChoices.size + 1}", "Chapter ${journeyChoices.size + 1}"),
                                    color = Color.White.copy(alpha = 0.74f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(playMetrics.gapDp.dp))
                }
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
                Spacer(Modifier.height(playMetrics.gapDp.dp))
                PrimaryMechanicButton(
                    text = if (projectedJourney.size >= 8) tr("Finale aufdecken", "Reveal finale") else tr("Entscheidung treffen", "Make decision"),
                    enabled = selected != null,
                    onClick = {
                        val answer = selected
                        val index = finalChoiceIndex
                        if (answer != null && index != null) {
                            triggerMiniVibration(context, 42L)
                            if (projectedJourney.size >= 8) {
                                showJourneyResult = true
                            } else {
                                journeyChoices = journeyChoices + index.coerceIn(0, 3)
                                onPick(answer)
                            }
                        }
                    },
                    testTag = "scenario_submit"
                )
            }
        }
    }
}
