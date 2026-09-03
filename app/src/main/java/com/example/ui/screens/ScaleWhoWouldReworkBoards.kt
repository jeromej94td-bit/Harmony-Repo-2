package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
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
import com.example.data.model.PairedChoiceAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyBg
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

private fun neutralScaleValue(itemCount: Int): Float = when {
    itemCount <= 1 -> 1f
    else -> ((itemCount + 1) / 2).toFloat()
}

@Composable
internal fun ScaleMatchRevealBoard(
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
    val neutralValue = neutralScaleValue(items.size)
    var first by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.first) }
    var second by rememberSaveable(question, selectedAnswer) { mutableStateOf(restored?.second) }
    var phase by rememberSaveable(question, selectedAnswer) { mutableStateOf(if (restored != null) 4 else 0) }
    var sliderValue by rememberSaveable(question, selectedAnswer) { mutableStateOf(neutralValue) }
    val max = items.size.coerceAtLeast(2)
    val configuration = LocalConfiguration.current
    val revealMetrics = PairMechanicRevealLayoutPolicy.metrics(
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )

    FullscreenMechanicShell(
        kicker = tr("🎚️ SKALEN-MATCH", "🎚️ SCALE MATCH"),
        question = prompt,
        instruction = when (phase) {
            0 -> tr(
                "Setze deinen Marker. Dein Wert bleibt für ${profile.partnerName} unsichtbar.",
                "Place your marker. Your value stays hidden from ${profile.partnerName}."
            )
            1 -> tr("Dein Marker ist sicher verdeckt.", "Your marker is safely hidden.")
            2 -> tr(
                "Jetzt setzt ${profile.partnerName} den eigenen Marker – deinen sieht man nicht.",
                "Now ${profile.partnerName} places their marker – yours stays hidden."
            )
            3 -> tr("Beide Marker sind gesetzt. Deckt die Skala gemeinsam auf.", "Both markers are locked. Reveal the scale together.")
            else -> tr("Jetzt liegen eure beiden Marker auf derselben Skala.", "Now both markers share the same scale.")
        },
        modifier = modifier.testTag("scale_match_board")
    ) {
        when (phase) {
            0, 2 -> {
                val selectedIndex = sliderValue
                    .roundToInt()
                    .coerceIn(1, items.size.coerceAtLeast(1)) - 1
                val selected = items.getOrNull(selectedIndex)

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(revealMetrics.scaleInputHeightDp.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        HarmonyPink.copy(alpha = 0.34f),
                                        HarmonyPurple.copy(alpha = 0.50f),
                                        HarmonySurface2
                                    )
                                )
                            )
                            .border(1.5.dp, HarmonyPinkSoft.copy(alpha = 0.62f), RoundedCornerShape(28.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (phase == 0) profile.userName else profile.partnerName,
                                color = HarmonyPinkSoft,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(Modifier.height(5.dp))
                            Text(
                                text = selected?.label ?: sliderValue.roundToInt().toString(),
                                color = Color.White,
                                fontSize = revealMetrics.scaleInputTextSizeSp.sp,
                                lineHeight = (revealMetrics.scaleInputTextSizeSp + 5).sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .padding(horizontal = 14.dp)
                                    .testTag("scale_selected_value")
                            )
                        }
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(HarmonyPurple.copy(alpha = 0.18f))
                            .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(24.dp))
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Slider(
                            value = sliderValue,
                            onValueChange = { sliderValue = it },
                            valueRange = 1f..max.toFloat(),
                            steps = (max - 2).coerceAtLeast(0),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("scale_slider")
                        )
                        Spacer(Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            repeat(items.size.coerceAtLeast(1)) { marker ->
                                val active = marker == selectedIndex
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(if (active) 12.dp else 6.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (active) HarmonyPinkSoft
                                            else Color.White.copy(alpha = 0.16f)
                                        )
                                )
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = items.firstOrNull()?.label ?: "1",
                                color = HarmonyMuted,
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = items.lastOrNull()?.label ?: max.toString(),
                                color = HarmonyMuted,
                                fontSize = 13.sp,
                                lineHeight = 17.sp,
                                textAlign = TextAlign.End,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    PrimaryMechanicButton(
                        text = if (phase == 0) {
                            tr("Meinen Marker verdecken", "Hide my marker")
                        } else {
                            tr("Marker festlegen", "Lock marker")
                        },
                        enabled = selected != null,
                        onClick = {
                            val raw = selected?.raw ?: return@PrimaryMechanicButton
                            triggerMiniVibration(context, 42L)
                            if (phase == 0) {
                                first = raw
                                phase = 1
                            } else {
                                second = raw
                                phase = 3
                            }
                        },
                        testTag = if (phase == 0) "scale_first_lock" else "scale_second_lock"
                    )
                }
            }

            1 -> ScalePrivateHandoffPane(
                partnerName = profile.partnerName,
                onReady = {
                    sliderValue = neutralValue
                    phase = 2
                }
            )

            3 -> ScaleRevealReadyPane(
                userName = profile.userName,
                partnerName = profile.partnerName,
                onReveal = {
                    triggerMiniVibration(context, 58L)
                    phase = 4
                }
            )

            else -> {
                val firstIndex = items.indexOfFirst { it.raw == first }.coerceAtLeast(0)
                val secondIndex = items.indexOfFirst { it.raw == second }.coerceAtLeast(0)
                val gap = abs(firstIndex - secondIndex)
                val maxGap = (items.size - 1).coerceAtLeast(1)
                val match = (100f - gap.toFloat() * (100f / maxGap.toFloat()))
                    .roundToInt()
                    .coerceIn(0, 100)
                val reveal = remember(question, first, second) { Animatable(0f) }

                LaunchedEffect(question, first, second) {
                    reveal.snapTo(0f)
                    reveal.animateTo(1f, tween(620, easing = FastOutSlowInEasing))
                }

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = tr("$match % auf einer Wellenlänge", "$match% on the same wavelength"),
                        color = Color.White,
                        fontSize = revealMetrics.scaleTitleSizeSp.sp,
                        lineHeight = (revealMetrics.scaleTitleSizeSp + 6).sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.graphicsLayer {
                            alpha = 0.35f + reveal.value * 0.65f
                            scaleX = 0.94f + reveal.value * 0.06f
                            scaleY = 0.94f + reveal.value * 0.06f
                        }
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(revealMetrics.scaleRevealHeightDp.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        HarmonyPurple.copy(alpha = 0.42f),
                                        HarmonySurface2,
                                        HarmonyPink.copy(alpha = 0.20f)
                                    )
                                )
                            )
                            .border(1.4.dp, HarmonyPink.copy(alpha = 0.52f), RoundedCornerShape(28.dp))
                            .padding(revealMetrics.scaleCardPaddingDp.dp)
                            .testTag("scale_result_card")
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ScaleMatchRevealLine(
                                name = profile.userName,
                                label = items.getOrNull(firstIndex)?.label ?: "–",
                                index = firstIndex,
                                size = items.size,
                                accent = HarmonyPinkSoft
                            )
                            ScaleMatchRevealLine(
                                name = profile.partnerName,
                                label = items.getOrNull(secondIndex)?.label ?: "–",
                                index = secondIndex,
                                size = items.size,
                                accent = HarmonyPurpleLight
                            )
                        }
                    }

                    PrimaryMechanicButton(
                        text = tr("Match speichern & weiter", "Save match & continue"),
                        onClick = {
                            val a = first
                            val b = second
                            if (a != null && b != null) {
                                onPick(PairedChoiceAnswerCodec.encode(a, b))
                            }
                        },
                        testTag = "scale_match_continue"
                    )
                }
            }
        }
    }
}

@Composable
private fun ScalePrivateHandoffPane(
    partnerName: String,
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
                .size(if (compact) 92.dp else 116.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
                .border(2.dp, Color.White.copy(alpha = 0.52f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✦",
                color = Color.White,
                fontSize = if (compact) 36.sp else 46.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.height(if (compact) 14.dp else 20.dp))
        Text(
            text = tr("Marker verdeckt", "Marker hidden"),
            color = Color.White,
            fontSize = if (compact) 22.sp else 27.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(7.dp))
        Text(
            text = tr(
                "Übergib das Handy an $partnerName. Dein Wert bleibt unsichtbar.",
                "Pass the phone to $partnerName. Your value stays hidden."
            ),
            color = HarmonyMuted,
            fontSize = if (compact) 14.sp else 16.sp,
            lineHeight = if (compact) 19.sp else 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(if (compact) 20.dp else 28.dp))
        PrimaryMechanicButton(
            text = tr("$partnerName ist bereit", "$partnerName is ready"),
            onClick = onReady,
            testTag = "scale_handoff_ready"
        )
    }
}

@Composable
private fun ScaleRevealReadyPane(
    userName: String,
    partnerName: String,
    onReveal: () -> Unit
) {
    val compact = LocalConfiguration.current.screenHeightDp < 700
    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("scale_reveal_ready"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = tr("Bereit zum Aufdecken?", "Ready to reveal?"),
                color = Color.White,
                fontSize = if (compact) 22.sp else 27.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = tr("Beide Marker sind festgelegt und noch verborgen.", "Both markers are locked and still hidden."),
                color = HarmonyMuted,
                fontSize = if (compact) 13.sp else 15.sp,
                textAlign = TextAlign.Center
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HiddenScaleMarkerCard(userName, Modifier.weight(1f))
            HiddenScaleMarkerCard(partnerName, Modifier.weight(1f))
        }

        PrimaryMechanicButton(
            text = tr("Gemeinsam aufdecken", "Reveal together"),
            onClick = onReveal,
            testTag = "scale_reveal_button"
        )
    }
}

@Composable
private fun HiddenScaleMarkerCard(
    name: String,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .heightIn(min = 130.dp)
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.80f),
                        HarmonySurface2,
                        HarmonyPink.copy(alpha = 0.30f)
                    )
                )
            )
            .border(1.5.dp, HarmonyPink.copy(alpha = 0.54f), shape)
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("●", color = HarmonyPinkSoft, fontSize = 34.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = tr("Marker verborgen", "Marker hidden"),
                color = HarmonyMuted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ScaleMatchRevealLine(
    name: String,
    label: String,
    index: Int,
    size: Int,
    accent: Color
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = name,
                color = accent,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp,
                modifier = Modifier.weight(0.42f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = label,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(0.58f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(Modifier.height(9.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(size.coerceAtLeast(1)) { marker ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(if (marker == index) 13.dp else 6.dp)
                        .clip(CircleShape)
                        .background(
                            if (marker == index) accent
                            else Color.White.copy(alpha = 0.16f)
                        )
                )
                if (marker < size - 1) Spacer(Modifier.width(5.dp))
            }
        }
    }
}

@Composable
internal fun WhoWouldConfirmBoard(
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
    val bothItem = items.firstOrNull {
        it.label.equals("Beide", true) || it.label.equals("Both", true)
    } ?: items.getOrNull(2)
    val nobodyItem = items.firstOrNull {
        it.label.equals("Niemand", true) ||
            it.label.equals("Nobody", true) ||
            it.label.equals("Neither", true)
    } ?: items.getOrNull(3)
    var pendingSelection by rememberSaveable(question, selectedAnswer) {
        mutableStateOf(selectedAnswer?.takeIf { answer -> items.any { it.raw == answer } })
    }
    val configuration = LocalConfiguration.current
    val layoutMetrics = WhoWouldLayoutPolicy.metrics(
        screenWidthDp = configuration.screenWidthDp,
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )

    fun select(raw: String) {
        triggerMiniVibration(context, 38L)
        pendingSelection = raw
    }

    FullscreenMechanicShell(
        kicker = tr("👤 PAARLABOR", "👤 COUPLE LAB"),
        question = prompt,
        instruction = tr(
            "Tippe auf die Person, die eher passt. Erst mit Bestätigen geht es weiter.",
            "Tap the person who fits best. Continue only after confirming."
        ),
        modifier = modifier.testTag("who_would_board")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(layoutMetrics.rowGapDp.dp)
            ) {
                WhoWouldPersonCard(
                    name = profile.userName,
                    avatarPath = profile.userAvatarPath,
                    selected = pendingSelection == userItem?.raw,
                    onClick = { userItem?.raw?.let(::select) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    tag = "who_user",
                    layoutMetrics = layoutMetrics,
                    accent = HarmonyPink
                )
                WhoWouldPersonCard(
                    name = profile.partnerName,
                    avatarPath = profile.partnerAvatarPath,
                    selected = pendingSelection == partnerItem?.raw,
                    onClick = { partnerItem?.raw?.let(::select) },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    tag = "who_partner",
                    layoutMetrics = layoutMetrics,
                    accent = HarmonyPurpleLight
                )
            }

            Spacer(Modifier.height(layoutMetrics.rowGapDp.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(layoutMetrics.bottomRowHeightDp.dp),
                horizontalArrangement = Arrangement.spacedBy(layoutMetrics.rowGapDp.dp)
            ) {
                bothItem?.let { item ->
                    LargeOptionCard(
                        item = item,
                        selected = pendingSelection == item.raw,
                        onClick = { select(item.raw) },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        testTag = "who_both",
                        badge = "◉  ◉",
                        accent = HarmonyPink
                    )
                }
                nobodyItem?.let { item ->
                    LargeOptionCard(
                        item = item,
                        selected = pendingSelection == item.raw,
                        onClick = { select(item.raw) },
                        modifier = Modifier
                            .weight(0.70f)
                            .fillMaxSize(),
                        testTag = "who_nobody",
                        accent = HarmonyPurpleLight
                    )
                }
            }

            Spacer(Modifier.height(layoutMetrics.rowGapDp.dp))

            PrimaryMechanicButton(
                text = if (pendingSelection == null) {
                    tr("Wähle zuerst eine Antwort", "Choose an answer first")
                } else {
                    tr("Auswahl bestätigen", "Confirm choice")
                },
                enabled = pendingSelection != null,
                onClick = { pendingSelection?.let(onPick) },
                testTag = "who_confirm"
            )
        }
    }
}

@Composable
private fun WhoWouldPersonCard(
    name: String,
    avatarPath: String?,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier,
    tag: String,
    layoutMetrics: WhoWouldLayoutMetrics,
    accent: Color
) {
    val shape = RoundedCornerShape(30.dp)
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = if (selected) 1.018f else 1f
                scaleY = if (selected) 1.018f else 1f
                shadowElevation = if (selected) 24f else 8f
            }
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = if (selected) 0.58f else 0.30f),
                        HarmonySurface2,
                        HarmonyBg
                    )
                )
            )
            .border(
                width = if (selected) 2.2.dp else 1.2.dp,
                color = if (selected) Color.White.copy(alpha = 0.76f) else accent.copy(alpha = 0.52f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(layoutMetrics.cardPaddingDp.dp)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(layoutMetrics.avatarSizeDp.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(accent, HarmonyPurple)))
                    .border(
                        width = if (selected) 3.dp else 2.dp,
                        color = if (selected) Color.White else Color.White.copy(alpha = 0.48f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!avatarPath.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarPath,
                        contentDescription = name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                    )
                } else {
                    Text(
                        text = name.trim().take(1).uppercase().ifBlank { "?" },
                        color = Color.White,
                        fontSize = (layoutMetrics.nameSizeSp * 2).coerceAtMost(48).sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
            Spacer(Modifier.height(layoutMetrics.avatarNameGapDp.dp))
            Text(
                text = name,
                color = Color.White,
                fontSize = layoutMetrics.nameSizeSp.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (selected) {
                Spacer(Modifier.height(5.dp))
                Text(
                    text = tr("Ausgewählt", "Selected"),
                    color = HarmonyPinkSoft,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
    }
}
