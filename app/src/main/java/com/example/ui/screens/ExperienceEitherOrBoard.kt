package com.example.ui.screens

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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

/**
 * Stateless renderer for an experience two-choice round.
 * Selection ownership deliberately stays with the calling experience runner.
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
            onClick = { onPick(round.firstChoice) },
            modifier = Modifier.fillMaxWidth(),
            testTag = "experience_either_or_first"
        )
        Spacer(Modifier.height(14.dp))
        ExperienceEitherOrChoiceCard(
            text = round.secondChoice,
            selected = selectedChoice == round.secondChoice,
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
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val shape = RoundedCornerShape(24.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        if (selected) HarmonyPink.copy(alpha = 0.55f)
                        else HarmonyPurple.copy(alpha = 0.34f),
                        HarmonySurface2
                    )
                )
            )
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) HarmonyPinkSoft else Color.White.copy(alpha = 0.15f),
                shape
            )
            .selectable(
                selected = selected,
                onClick = onClick,
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
