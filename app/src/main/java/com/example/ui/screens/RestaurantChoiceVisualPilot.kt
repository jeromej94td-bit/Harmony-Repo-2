package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2

/**
 * First normal-question visual pilot.
 *
 * This intentionally matches one existing restaurant question only. The four raw answer strings stay
 * untouched because they are already persisted and used by the partner-comparison layer. Everything
 * here is presentation data: scene prompt, short labels and image coordinates.
 */
internal const val RESTAURANT_REVISIT_SOURCE_QUESTION =
    "Was wäre für dich der größte Grund, ein Restaurant nicht wieder zu besuchen?"

internal const val RESTAURANT_REVISIT_VISUAL_PROMPT =
    "Euer Date läuft – was würde dir den Abend am schnellsten verderben?"

internal data class RestaurantChoiceVisualCard(
    val row: Int,
    val column: Int,
    val displayLabel: String
)

internal data class RestaurantChoiceVisualSpec(
    val prompt: String,
    val cardsByRawAnswer: Map<String, RestaurantChoiceVisualCard>
)

internal fun restaurantChoiceVisualSpec(
    question: String,
    items: List<MechanicOption>
): RestaurantChoiceVisualSpec? {
    if (question != RESTAURANT_REVISIT_SOURCE_QUESTION) return null

    val expected = listOf(
        "Essen enttäuscht",
        "Schlechter Service",
        "Zu laut",
        "Preis passt nicht"
    )
    if (items.map { it.raw } != expected) return null

    return RestaurantChoiceVisualSpec(
        prompt = RESTAURANT_REVISIT_VISUAL_PROMPT,
        cardsByRawAnswer = linkedMapOf(
            "Essen enttäuscht" to RestaurantChoiceVisualCard(0, 0, "Essen"),
            "Schlechter Service" to RestaurantChoiceVisualCard(0, 1, "Service"),
            "Zu laut" to RestaurantChoiceVisualCard(1, 0, "Zu laut"),
            "Preis passt nicht" to RestaurantChoiceVisualCard(1, 1, "Preis")
        )
    )
}

@Composable
private fun rememberRestaurantRevisitAtlas(): ImageBitmap? {
    val context = LocalContext.current
    return remember(context) {
        runCatching {
            BitmapFactory.decodeResource(
                context.resources,
                R.drawable.restaurant_revisit_visual_atlas
            )?.asImageBitmap()
        }.getOrNull()
    }
}

@Composable
internal fun RestaurantChoiceVisualThumbnail(
    card: RestaurantChoiceVisualCard,
    modifier: Modifier = Modifier
) {
    val atlas = rememberRestaurantRevisitAtlas() ?: return
    val cellWidth = atlas.width / 2
    val cellHeight = atlas.height / 2
    if (cellWidth <= 0 || cellHeight <= 0) return

    Image(
        painter = BitmapPainter(
            image = atlas,
            srcOffset = IntOffset(card.column * cellWidth, card.row * cellHeight),
            srcSize = IntSize(cellWidth, cellHeight)
        ),
        contentDescription = card.displayLabel,
        contentScale = ContentScale.Crop,
        modifier = modifier.fillMaxSize()
    )
}

@Composable
internal fun RestaurantChoiceVisualGrid(
    items: List<MechanicOption>,
    spec: RestaurantChoiceVisualSpec,
    selectedRaw: String?,
    onSelect: (MechanicOption) -> Unit,
    modifier: Modifier = Modifier
) {
    val rows = items.chunked(2)
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEachIndexed { rowIndex, rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEachIndexed { columnIndex, item ->
                    val visual = spec.cardsByRawAnswer[item.raw] ?: return@forEachIndexed
                    RestaurantChoiceVisualTile(
                        item = item,
                        visual = visual,
                        selected = selectedRaw == item.raw,
                        onClick = { onSelect(item) },
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        testTag = "restaurant_visual_card_${rowIndex * 2 + columnIndex}"
                    )
                }
            }
        }
    }
}

@Composable
private fun RestaurantChoiceVisualTile(
    item: MechanicOption,
    visual: RestaurantChoiceVisualCard,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String
) {
    val shape = RoundedCornerShape(22.dp)
    val borderColor = if (selected) Color.White else HarmonyPink.copy(alpha = 0.56f)

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.30f),
                        HarmonySurface2.copy(alpha = 0.98f)
                    )
                )
            )
            .border(if (selected) 2.dp else 1.2.dp, borderColor, shape)
            .clickable(onClick = onClick)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
        ) {
            RestaurantChoiceVisualThumbnail(
                card = visual,
                modifier = Modifier.fillMaxSize()
            )
        }
        Text(
            text = visual.displayLabel,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 9.dp)
        )
    }
}
