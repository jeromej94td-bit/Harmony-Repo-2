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

/**
 * Proposal text-choice renderer. These text-only cards deliberately get their own luminous,
 * tactile treatment; image-duel and other game renderers are not affected.
 */
@Composable
internal fun ExperienceEitherOrBoard(
    round: ExperienceEitherOrRound,
    selectedChoice: String?,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
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
            tiltDirection = -1f,
            onClick = { onPick(round.firstChoice) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "experience_either_or_first"
        )
        Spacer(Modifier.height(14.dp))
        ExperienceEitherOrChoiceCard(
            text = round.secondChoice,
            selected = selectedChoice == round.secondChoice,
            tiltDirection = 1f,
            onClick = { onPick(round.secondChoice) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "experience_either_or_second"
        )
    }
}

@Composable
private fun ExperienceEitherOrChoiceCard(
    text: String,
    selected: Boolean,
    tiltDirection: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val shape = RoundedCornerShape(24.dp)
    val tilt = remember { Animatable(0f) }
    val density = LocalDensity.current.density
    val scope = rememberCoroutineScope()
    var animating by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .graphicsLayer {
                rotationZ = tilt.value
                rotationX = tilt.value * 0.65f
                scaleX = 1f - kotlin.math.abs(tilt.value) * 0.004f
                scaleY = 1f - kotlin.math.abs(tilt.value) * 0.004f
                cameraDistance = 18f * density
                shadowElevation = 18f * density
            }
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        HarmonyPink.copy(alpha = if (selected) 0.78f else 0.48f),
                        HarmonyPurple.copy(alpha = 0.78f),
                        HarmonyPinkSoft.copy(alpha = if (selected) 0.58f else 0.34f)
                    )
                )
            )
            .border(
                width = if (selected) 2.5.dp else 1.6.dp,
                color = if (selected) HarmonyPinkSoft else HarmonyPinkSoft.copy(alpha = 0.58f),
                shape = shape
            )
            .padding(2.dp)
            .testTag("${testTag}_glow"),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            if (selected) HarmonyPink.copy(alpha = 0.42f)
                            else HarmonyPurple.copy(alpha = 0.42f),
                            HarmonySurface2,
                            HarmonyPurple.copy(alpha = 0.30f)
                        )
                    )
                )
                .selectable(
                    selected = selected,
                    enabled = !animating,
                    onClick = {
                        if (animating) return@selectable
                        animating = true
                        scope.launch {
                            tilt.animateTo(tiltDirection * 4.5f, tween(95, easing = FastOutSlowInEasing))
                            tilt.animateTo(tiltDirection * -1.2f, tween(85, easing = FastOutSlowInEasing))
                            tilt.animateTo(0f, tween(105, easing = FastOutSlowInEasing))
                            animating = false
                            onClick()
                        }
                    },
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
}
