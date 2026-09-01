package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InteractionPromptPolicy
import com.example.data.model.ProfileEntity
import com.example.ui.contentText
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyBlue
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyTeal
import com.example.ui.tr

internal data class MechanicOption(
    val raw: String,
    val label: String
)

internal fun resolveProfileTokens(text: String, profile: ProfileEntity): String = text
    .replace("{user}", profile.userName)
    .replace("{partner}", profile.partnerName)

@Composable
internal fun mechanicOptions(options: List<String>, profile: ProfileEntity): List<MechanicOption> {
    val result = ArrayList<MechanicOption>(options.size)
    for (raw in options) {
        result += MechanicOption(
            raw = raw,
            label = resolveProfileTokens(contentText(raw), profile)
        )
    }
    return result
}

@Composable
internal fun mechanicPrompt(
    question: String,
    items: List<MechanicOption>,
    profile: ProfileEntity
): String {
    val localized = resolveProfileTokens(contentText(question), profile)
    return InteractionPromptPolicy.displayPrompt(localized, items.map { it.label })
}

@Composable
internal fun FullscreenMechanicShell(
    kicker: String,
    question: String,
    instruction: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val compact = screenHeight < 700
    val chromeMetrics = FullscreenMechanicChromeLayoutPolicy.metrics(
        screenHeightDp = screenHeight,
        fontScale = configuration.fontScale
    )
    // Keep the stage inside the usable viewport even on landscape/short displays.
    val stageHeight = FullscreenMechanicStageHeightPolicy.stageHeightDp(screenHeight).dp
    val shape = RoundedCornerShape(if (compact) 26.dp else 30.dp)
    val horizontalPadding = if (compact) 14.dp else 18.dp
    val questionSizeSp = FullscreenMechanicChromeLayoutPolicy.questionSizeSp(
        screenHeightDp = screenHeight,
        questionLength = question.length,
        fontScale = configuration.fontScale
    )
    val questionSize = questionSizeSp.sp
    val questionLineHeight = FullscreenMechanicChromeLayoutPolicy
        .questionLineHeightSp(screenHeight, questionSizeSp)
        .sp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(stageHeight)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.46f),
                        HarmonySurface2.copy(alpha = 0.98f),
                        HarmonyBg.copy(alpha = 0.99f)
                    )
                )
            )
            .border(1.2.dp, HarmonyPink.copy(alpha = 0.54f), shape)
            .padding(
                horizontal = horizontalPadding,
                vertical = chromeMetrics.verticalPaddingDp.dp
            )
            .testTag("fullscreen_mechanic_stage"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = kicker,
            color = HarmonyPinkSoft,
            fontSize = chromeMetrics.kickerSizeSp.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(chromeMetrics.headerGapDp.dp))
        Text(
            text = question,
            color = Color.White,
            fontSize = questionSize,
            lineHeight = questionLineHeight,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        if (chromeMetrics.showInstruction) {
            Spacer(Modifier.height(chromeMetrics.headerGapDp.dp))
            Text(
                text = instruction,
                color = HarmonyMuted,
                fontSize = chromeMetrics.instructionSizeSp.sp,
                lineHeight = chromeMetrics.instructionLineHeightSp.sp,
                textAlign = TextAlign.Center
            )
        }
        Spacer(Modifier.height(chromeMetrics.contentGapDp.dp))
        Box(modifier = Modifier.fillMaxSize()) { content() }
    }
}

@Composable
internal fun LargeOptionCard(
    item: MechanicOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String? = null,
    badge: String? = null,
    accent: Color = HarmonyPink
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.018f else 1f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "mechanic_option_scale"
    )
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (selected) 24f else 8f
            }
            .clip(shape)
            .background(
                Brush.linearGradient(
                    if (selected) {
                        listOf(
                            accent.copy(alpha = 0.70f),
                            HarmonyPurple.copy(alpha = 0.62f),
                            HarmonySurface2
                        )
                    } else {
                        listOf(
                            accent.copy(alpha = 0.24f),
                            HarmonySurface.copy(alpha = 0.96f),
                            HarmonyPurple.copy(alpha = 0.25f),
                            HarmonySurface2
                        )
                    }
                )
            )
            .border(
                if (selected) 2.dp else 1.2.dp,
                if (selected) Color.White.copy(alpha = 0.72f) else accent.copy(alpha = 0.58f),
                shape
            )
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .fillMaxWidth()
                .height(if (selected) 4.dp else 3.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color.Transparent,
                            accent.copy(alpha = if (selected) 0.95f else 0.70f),
                            Color.White.copy(alpha = if (selected) 0.62f else 0.20f),
                            Color.Transparent
                        )
                    )
                )
        )

        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            val compactCard = maxHeight < 145.dp
            val cardMetrics = LargeOptionCardLayoutPolicy.metrics(maxHeight.value.toInt())
            val labelSize = when {
                compactCard && item.label.length > 78 -> 12.sp
                compactCard && item.label.length > 55 -> 13.sp
                compactCard && item.label.length > 36 -> 14.sp
                compactCard -> 15.sp
                item.label.length > 78 -> 14.sp
                item.label.length > 55 -> 15.sp
                else -> 17.sp
            }
            val labelLineHeight = (labelSize.value + 4f).sp

            Column(
                modifier = Modifier.padding(
                    horizontal = cardMetrics.horizontalPaddingDp.dp,
                    vertical = cardMetrics.verticalPaddingDp.dp
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!badge.isNullOrBlank()) {
                    Text(
                        text = badge,
                        color = accent,
                        fontSize = if (compactCard) 10.sp else 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(if (compactCard) 4.dp else 6.dp))
                }
                Text(
                    text = item.label,
                    color = Color.White,
                    fontSize = labelSize,
                    lineHeight = labelLineHeight,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
internal fun LargeOptionGrid(
    items: List<MechanicOption>,
    selectedRaw: String?,
    onSelect: (MechanicOption) -> Unit,
    modifier: Modifier = Modifier,
    tagPrefix: String = "mechanic_option"
) {
    val configuration = LocalConfiguration.current
    val gap = if (configuration.screenHeightDp < 700) 8.dp else 12.dp
    val rowItems = items.chunked(2)
    val accents = listOf(HarmonyPink, HarmonyBlue, HarmonyTeal, HarmonyGold, HarmonyPurpleLight)

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val gridMetrics = LargeOptionGridLayoutPolicy.metrics(
            availableHeightDp = maxHeight.value.toInt(),
            rowCount = rowItems.size,
            gapDp = gap.value.toInt(),
            fontScale = configuration.fontScale
        )

        if (gridMetrics.useScroll) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .testTag("${tagPrefix}_scroll"),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                rowItems.forEachIndexed { rowIndex, itemsInRow ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(gridMetrics.scrollRowHeightDp.dp),
                        horizontalArrangement = Arrangement.spacedBy(gap)
                    ) {
                        itemsInRow.forEachIndexed { columnIndex, item ->
                            val index = rowIndex * 2 + columnIndex
                            LargeOptionCard(
                                item = item,
                                selected = selectedRaw == item.raw,
                                onClick = { onSelect(item) },
                                modifier = Modifier.weight(1f).fillMaxSize(),
                                testTag = "${tagPrefix}_$index",
                                accent = accents[index % accents.size]
                            )
                        }
                        if (itemsInRow.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(gap)
            ) {
                rowItems.forEachIndexed { rowIndex, itemsInRow ->
                    Row(
                        modifier = Modifier.fillMaxWidth().weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(gap)
                    ) {
                        itemsInRow.forEachIndexed { columnIndex, item ->
                            val index = rowIndex * 2 + columnIndex
                            LargeOptionCard(
                                item = item,
                                selected = selectedRaw == item.raw,
                                onClick = { onSelect(item) },
                                modifier = Modifier.weight(1f).fillMaxSize(),
                                testTag = "${tagPrefix}_$index",
                                accent = accents[index % accents.size]
                            )
                        }
                        if (itemsInRow.size == 1) Spacer(Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

@Composable
internal fun HandoffPane(
    name: String,
    text: String,
    onReady: () -> Unit,
    modifier: Modifier = Modifier
) {
    val compact = LocalConfiguration.current.screenHeightDp < 700
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (compact) 88.dp else 112.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
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
            text = text,
            color = Color.White,
            fontSize = if (compact) 20.sp else 24.sp,
            lineHeight = if (compact) 25.sp else 30.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(if (compact) 18.dp else 28.dp))
        PrimaryMechanicButton(text = tr("Ich bin bereit", "I'm ready"), onClick = onReady)
    }
}

@Composable
internal fun PrimaryMechanicButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    testTag: String? = null
) {
    val compact = LocalConfiguration.current.screenHeightDp < 700
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(if (compact) 52.dp else 58.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HarmonyPink,
            disabledContainerColor = Color.White.copy(alpha = 0.08f),
            disabledContentColor = HarmonyMuted
        )
    ) {
        Text(text = text, fontSize = if (compact) 15.sp else 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}
