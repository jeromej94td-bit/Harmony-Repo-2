package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExperienceEitherOrRound
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import kotlinx.coroutines.launch

internal enum class ExperienceEitherOrVisualStyle {
    STANDARD,
    PROPOSAL_GLOW_TILT
}

/** Stateless renderer for an experience two-choice round. */
@Composable
internal fun ExperienceEitherOrBoard(
    round: ExperienceEitherOrRound,
    selectedChoice: String?,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier,
    visualStyle: ExperienceEitherOrVisualStyle = ExperienceEitherOrVisualStyle.STANDARD
) {
    Column(
        modifier = modifier.testTag("experience_either_or_board"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "✦  ENTWEDER ODER",
            color = HarmonyPinkSoft,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 13.sp
        )
        Spacer(Modifier.height(12.dp))
        Text(
            text = round.prompt,
            color = Color.White,
            fontSize = 27.sp,
            lineHeight = 33.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(28.dp))
        ExperienceEitherOrChoiceCard(
            text = round.firstChoice,
            selected = selectedChoice == round.firstChoice,
            onClick = { onPick(round.firstChoice) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "experience_either_or_first",
            visualStyle = visualStyle,
            tiltDirection = 1f
        )
        Spacer(Modifier.height(14.dp))
        ExperienceEitherOrChoiceCard(
            text = round.secondChoice,
            selected = selectedChoice == round.secondChoice,
            onClick = { onPick(round.secondChoice) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "experience_either_or_second",
            visualStyle = visualStyle,
            tiltDirection = -1f
        )
    }
}

@Composable
private fun ExperienceEitherOrChoiceCard(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String,
    visualStyle: ExperienceEitherOrVisualStyle,
    tiltDirection: Float
) {
    val shape = RoundedCornerShape(24.dp)
    val enhanced = visualStyle == ExperienceEitherOrVisualStyle.PROPOSAL_GLOW_TILT
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()
    val rotation = remember { Animatable(0f) }
    val scale = remember { Animatable(1f) }
    val borderColor = if (selected) HarmonyPinkSoft else Color.White.copy(alpha = 0.15f)
    val borderBrush = if (enhanced) {
        Brush.horizontalGradient(
            listOf(
                HarmonyPinkSoft.copy(alpha = if (selected) 1f else 0.78f),
                HarmonyPink.copy(alpha = if (selected) 0.92f else 0.62f),
                HarmonyPurple.copy(alpha = 0.92f)
            )
        )
    } else {
        Brush.linearGradient(listOf(borderColor, borderColor))
    }

    fun handleClick() {
        if (!enhanced) {
            onClick()
            return
        }
        if (rotation.isRunning) return
        scope.launch {
            rotation.snapTo(0f)
            scale.snapTo(1f)
            scale.animateTo(1.025f, tween(90, easing = FastOutSlowInEasing))
            rotation.animateTo(tiltDirection * 10f, tween(105, easing = FastOutSlowInEasing))
            rotation.animateTo(-tiltDirection * 4f, tween(115, easing = FastOutSlowInEasing))
            rotation.animateTo(0f, tween(150, easing = FastOutSlowInEasing))
            scale.animateTo(1f, tween(120, easing = FastOutSlowInEasing))
            onClick()
        }
    }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationY = rotation.value
                scaleX = scale.value
                scaleY = scale.value
                cameraDistance = 28f * density
                shadowElevation = if (enhanced) 14f * density else 3f * density
                this.shape = shape
                clip = false
            }
            .clip(shape)
            .background(
                if (enhanced) {
                    Brush.horizontalGradient(
                        listOf(
                            HarmonyPurple.copy(alpha = if (selected) 0.72f else 0.56f),
                            HarmonyPink.copy(alpha = if (selected) 0.42f else 0.24f),
                            HarmonySurface2
                        )
                    )
                } else {
                    Brush.horizontalGradient(
                        listOf(
                            if (selected) HarmonyPink.copy(alpha = 0.55f)
                            else HarmonyPurple.copy(alpha = 0.34f),
                            HarmonySurface2
                        )
                    )
                }
            )
            .border(
                if (enhanced) {
                    if (selected) 2.6.dp else 1.8.dp
                } else {
                    if (selected) 2.dp else 1.dp
                },
                borderBrush,
                shape
            )
            .selectable(
                selected = selected,
                enabled = !rotation.isRunning,
                onClick = ::handleClick,
                role = Role.RadioButton
            )
            .padding(horizontal = 18.dp, vertical = 24.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}
