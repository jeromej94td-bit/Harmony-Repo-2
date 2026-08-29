package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.ProposalImageDuelOption
import com.example.data.model.ProposalImageDuelRound
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2

private val proposalLocationImages = mapOf(
    "location_home" to R.drawable.proposal_location_home,
    "location_lake" to R.drawable.proposal_location_lake,
    "location_garden" to R.drawable.proposal_location_garden,
    "location_view" to R.drawable.proposal_location_view,
    "location_city" to R.drawable.proposal_location_city,
    "location_coast" to R.drawable.proposal_location_coast
)

/**
 * Aurora-Glass presentation for one Stage 02.3 proposal-location duel.
 *
 * The board is deliberately not registered in navigation. Stage 02.11 will connect the
 * deterministic proposal runner to this component after the remaining mechanics are ready.
 */
@Composable
internal fun ProposalLocationDuelBoard(
    round: ProposalImageDuelRound,
    selectedOptionId: String?,
    onPick: (ProposalImageDuelOption) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.48f),
                        HarmonySurface2.copy(alpha = 0.96f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.18f), RoundedCornerShape(30.dp))
            .padding(16.dp)
            .testTag("proposal_location_duel"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Euer Ort",
            color = HarmonyPink,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = round.prompt,
            color = Color.White,
            fontSize = 22.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth().height(292.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ProposalLocationOptionCard(
                option = round.firstOption,
                selected = selectedOptionId == round.firstOption.id,
                onClick = { onPick(round.firstOption) },
                modifier = Modifier.weight(1f)
            )
            ProposalLocationOptionCard(
                option = round.secondOption,
                selected = selectedOptionId == round.secondOption.id,
                onClick = { onPick(round.secondOption) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ProposalLocationOptionCard(
    option: ProposalImageDuelOption,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val imageResId = proposalLocationImages.getValue(option.id)
    val shape = RoundedCornerShape(24.dp)

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(shape)
            .border(
                width = if (selected) 3.dp else 1.dp,
                color = if (selected) HarmonyPink else Color.White.copy(alpha = 0.26f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .testTag("proposal_location_${option.id}")
    ) {
        ProposalLocationImage(
            imageResId = imageResId,
            contentDescription = option.label
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0f to Color.Black.copy(alpha = 0.06f),
                        0.55f to Color.Transparent,
                        1f to Color.Black.copy(alpha = 0.78f)
                    )
                )
        )
        if (selected) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(HarmonyPink),
                contentAlignment = Alignment.Center
            ) {
                Text("✓", color = Color.White, fontWeight = FontWeight.Black)
            }
        }
        Text(
            text = option.label,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 19.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(14.dp)
        )
    }
}

@Composable
private fun ProposalLocationImage(
    @DrawableRes imageResId: Int,
    contentDescription: String
) {
    Image(
        painter = painterResource(imageResId),
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
    )
}
