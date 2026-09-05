package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.example.R

/**
 * Visual treatment for exactly one pilot series: Fine Dining ranking.
 *
 * The raw option strings remain untouched because they are part of the persisted ranking answer.
 * Only the prompt shown to the player, the short card label and the artwork are presentation data.
 */
internal const val FINE_DINING_VISUAL_RANKING_PACK_ID = "h500_104_fine_dining_ranking"

internal data class FineDiningVisualCard(
    val row: Int,
    val column: Int,
    val displayLabel: String
)

internal fun fineDiningVisualPrompt(question: String): String? = when (question) {
    "Was macht Fine Dining für dich zuerst besonders? Ordne." ->
        "Euer besonderer Abend beginnt – was muss zuerst stimmen?"
    "Wofür würdest du bei einem besonderen Dinner am ehesten mehr bezahlen? Ordne." ->
        "Hier darf der Abend Luxus sein – wo ist er dir das Geld wert?"
    "Was entscheidet, ob sich ein langes Menü lohnt? Ordne." ->
        "Ihr sitzt drei Stunden hier – was macht daraus einen richtig guten Abend?"
    "Was wäre dir bei der Auswahl wichtiger? Ordne." ->
        "Der Küchenchef lässt euch die Richtung bestimmen."
    "Was sollte ein Fine-Dining-Abend am wenigsten sein? Ordne vom größten Störfaktor." ->
        "Der Abend kippt – was nervt dich am schnellsten?"
    "Was würdest du vom Abend am liebsten mit nach Hause nehmen? Ordne." ->
        "Morgen früh – was soll dir davon noch im Kopf sein?"
    else -> null
}

internal fun fineDiningRankingImageRes(question: String, option: String): Int? =
    fineDiningRankingCard(question, option)?.let { R.drawable.fine_dining_ranking_atlas }

internal fun fineDiningRankingCard(question: String, option: String): FineDiningVisualCard? {
    val row = when (question) {
        "Was macht Fine Dining für dich zuerst besonders? Ordne." -> 0
        "Wofür würdest du bei einem besonderen Dinner am ehesten mehr bezahlen? Ordne." -> 1
        "Was entscheidet, ob sich ein langes Menü lohnt? Ordne." -> 2
        "Was wäre dir bei der Auswahl wichtiger? Ordne." -> 3
        "Was sollte ein Fine-Dining-Abend am wenigsten sein? Ordne vom größten Störfaktor." -> 4
        "Was würdest du vom Abend am liebsten mit nach Hause nehmen? Ordne." -> 5
        else -> return null
    }

    val options = when (row) {
        0 -> listOf("Geschmack", "Menüfolge", "Service", "Atmosphäre")
        1 -> listOf("Außergewöhnliche Zutaten", "Kreative Zubereitung", "Perfekter Service", "Besondere Location")
        2 -> listOf("Überraschende Gänge", "Passende Portionsgröße", "Gutes Tempo", "Zeit zum Reden")
        3 -> listOf("Regional", "Saisonal", "Experimentell", "Klassisch perfektioniert")
        4 -> listOf("Steif", "Zu laut", "Zu langsam", "Mehr Show als Geschmack")
        5 -> listOf("Ein neues Lieblingsgericht", "Eine überraschende Kombination", "Eine schöne Erinnerung", "Eine neue Geschmacksidee")
        else -> return null
    }
    val column = options.indexOf(option)
    if (column < 0) return null

    val shortLabel = when (option) {
        "Außergewöhnliche Zutaten" -> "Zutaten"
        "Kreative Zubereitung" -> "Kreativität"
        "Perfekter Service" -> "Service"
        "Besondere Location" -> "Location"
        "Überraschende Gänge" -> "Überraschungen"
        "Passende Portionsgröße" -> "Portionen"
        "Gutes Tempo" -> "Tempo"
        "Zeit zum Reden" -> "Zeit zu zweit"
        "Klassisch perfektioniert" -> "Klassisch perfekt"
        "Mehr Show als Geschmack" -> "Zu viel Show"
        "Ein neues Lieblingsgericht" -> "Lieblingsgericht"
        "Eine überraschende Kombination" -> "Neue Kombination"
        "Eine schöne Erinnerung" -> "Gemeinsamer Moment"
        "Eine neue Geschmacksidee" -> "Neue Inspiration"
        else -> option
    }
    return FineDiningVisualCard(row = row, column = column, displayLabel = shortLabel)
}

/** One atlas keeps the 24 card artworks compact in the APK while each card is rendered separately. */
@Composable
internal fun FineDiningRankingThumbnail(
    card: FineDiningVisualCard,
    modifier: Modifier = Modifier
) {
    val atlas = ImageBitmap.imageResource(R.drawable.fine_dining_ranking_atlas)
    val cellWidth = atlas.width / 4
    val cellHeight = atlas.height / 6
    val srcOffset = IntOffset(card.column * cellWidth, card.row * cellHeight)
    val srcSize = IntSize(cellWidth, cellHeight)

    Image(
        painter = BitmapPainter(
            image = atlas,
            srcOffset = srcOffset,
            srcSize = srcSize
        ),
        contentDescription = card.displayLabel,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxHeight()
            .widthIn(min = 54.dp, max = 82.dp)
            .aspectRatio(0.82f)
            .clip(RoundedCornerShape(12.dp))
    )
}
