package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExperienceImageDuelOption
import com.example.data.model.ExperienceImageDuelRound
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText

/**
 * Flexible product-card presentation for reusable image duels.
 *
 * Unlike the cinematic location board, the card row consumes the remaining vertical space. This
 * preserves the original proposal ring proportions across different screen heights.
 */
@Composable
internal fun ExperienceProductImageDuelBoard(
    round: ExperienceImageDuelRound,
    selectedOptionId: String?,
    imageResolver: (String) -> Int,
    onPick: (ExperienceImageDuelOption) -> Unit,
    modifier: Modifier = Modifier,
    kicker: String = "✦  BILD-DUELL",
    rootTestTag: String = "experience_product_image_duel",
    testTagPrefix: String = "experience_product_image_duel"
) {
    Column(
        modifier = modifier.testTag(rootTestTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(kicker, color = HarmonyPinkSoft, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(10.dp))
        Text(
            round.prompt,
            color = Color.White,
            fontSize = 25.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(18.dp))
        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ExperienceProductImageDuelCard(
                option = round.firstOption,
                imageRes = imageResolver(round.firstOption.imageKey),
                selected = selectedOptionId == round.firstOption.id,
                onClick = { onPick(round.firstOption) },
                modifier = Modifier.weight(1f).fillMaxSize(),
                testTag = "${testTagPrefix}_first"
            )
            ExperienceProductImageDuelCard(
                option = round.secondOption,
                imageRes = imageResolver(round.secondOption.imageKey),
                selected = selectedOptionId == round.secondOption.id,
                onClick = { onPick(round.secondOption) },
                modifier = Modifier.weight(1f).fillMaxSize(),
                testTag = "${testTagPrefix}_second"
            )
        }
    }
}

@Composable
private fun ExperienceProductImageDuelCard(
    option: ExperienceImageDuelOption,
    @DrawableRes imageRes: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val shape = RoundedCornerShape(26.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(HarmonySurface2)
            .border(
                if (selected) 2.dp else 1.dp,
                if (selected) HarmonyPink else Color.White.copy(alpha = 0.16f),
                shape
            )
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(10.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(20.dp))
                .background(HarmonyPurple.copy(alpha = 0.24f)),
            contentAlignment = Alignment.Center
        ) {
            if (imageRes != 0) {
                Image(
                    painter = painterResource(imageRes),
                    contentDescription = option.label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text("💍", fontSize = 64.sp)
            }
        }
        Spacer(Modifier.height(10.dp))
        Text(
            option.label,
            color = HarmonyText,
            fontSize = 15.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)
        )
    }
}
