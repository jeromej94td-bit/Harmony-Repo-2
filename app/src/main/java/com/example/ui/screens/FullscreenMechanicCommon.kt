package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InteractionPromptPolicy
import com.example.data.model.ProfileEntity
import com.example.ui.contentText
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
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
    val stageHeight = (configuration.screenHeightDp - 178).coerceAtLeast(560).dp
    val shape = RoundedCornerShape(30.dp)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(stageHeight)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.42f),
                        HarmonySurface2.copy(alpha = 0.98f),
                        HarmonyBg.copy(alpha = 0.99f)
                    )
                )
            )
            .border(1.2.dp, HarmonyPink.copy(alpha = 0.46f), shape)
            .padding(horizontal = 18.dp, vertical = 18.dp)
            .testTag("fullscreen_mechanic_stage"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = kicker,
            color = HarmonyPinkSoft,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = question,
            color = Color.White,
            fontSize = 27.sp,
            lineHeight = 33.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 5,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = instruction,
            color = HarmonyMuted,
            fontSize = 14.sp,
            lineHeight = 19.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
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
    badge: String? = null
) {
    val scale by animateFloatAsState(
        targetValue = if (selected) 1.035f else 1f,
        animationSpec = tween(220, easing = FastOutSlowInEasing),
        label = "mechanic_option_scale"
    )
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
                shadowElevation = if (selected) 18f else 5f
            }
            .clip(shape)
            .background(
                Brush.linearGradient(
                    if (selected) {
                        listOf(HarmonyPink.copy(alpha = 0.55f), HarmonyPurple.copy(alpha = 0.68f), HarmonySurface2)
                    } else {
                        listOf(HarmonySurface, HarmonyPurple.copy(alpha = 0.26f), HarmonySurface2)
                    }
                )
            )
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) HarmonyPinkSoft else Color.White.copy(alpha = 0.16f),
                shape
            )
            .clickable(onClick = onClick)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
            .padding(horizontal = 14.dp, vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (!badge.isNullOrBlank()) {
                Text(
                    text = badge,
                    color = HarmonyPinkSoft,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(6.dp))
            }
            Text(
                text = item.label,
                color = Color.White,
                fontSize = 17.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
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
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.chunked(2).forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                rowItems.forEachIndexed { columnIndex, item ->
                    val index = rowIndex * 2 + columnIndex
                    LargeOptionCard(
                        item = item,
                        selected = selectedRaw == item.raw,
                        onClick = { onSelect(item) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        testTag = "${tagPrefix}_$index"
                    )
                }
                if (rowItems.size == 1) Spacer(Modifier.weight(1f))
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
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(112.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
                .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.trim().take(1).uppercase().ifBlank { "?" },
                color = Color.White,
                fontSize = 42.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.height(20.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 24.sp,
            lineHeight = 30.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(28.dp))
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
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(58.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HarmonyPink,
            disabledContainerColor = Color.White.copy(alpha = 0.08f),
            disabledContentColor = HarmonyMuted
        )
    ) {
        Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}
