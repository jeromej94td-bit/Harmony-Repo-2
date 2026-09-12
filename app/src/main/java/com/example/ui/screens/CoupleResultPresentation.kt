package com.example.ui.screens

import androidx.annotation.DrawableRes
import com.example.data.couple.normalizeCoupleAnswerText
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.MemoryMatchAnswerCodec
import com.example.data.model.PairedChoiceAnswerCodec
import com.example.data.model.PersonAssignmentCodec
import com.example.data.model.PersonSide
import com.example.data.model.PredictionAnswerCodec
import com.example.data.model.QuestionPack
import com.example.data.model.RankingAnswerCodec
import com.example.data.model.SecretPlanAnswerCodec
import com.example.data.model.WeekendEchoAnswerCodec
import com.example.data.model.WeekendEchoMotif

/**
 * One shared presentation contract for all paired result cards.
 *
 * Gameplay mechanics are free to persist compact/encoded answers. The result screen must never
 * expose those storage strings to the couple. This resolver translates every known answer codec
 * into a readable presentation and reuses the same visual asset as gameplay whenever possible.
 */
internal data class CoupleResultPresentation(
    val displayText: String,
    val detailLines: List<String> = emptyList(),
    @param:DrawableRes val imageRes: Int? = null,
    val localImagePath: String? = null,
    val structured: Boolean = false
)

internal fun coupleResultPresentation(
    pack: QuestionPack,
    questionIndex: Int,
    answer: String
): CoupleResultPresentation {
    val normalized = normalizeCoupleAnswerText(answer)
    if (normalized.isBlank()) return CoupleResultPresentation(displayText = "")

    val question = pack.questions.getOrNull(questionIndex)
    val options = question?.options.orEmpty()
    val resultImageRes = coupleResultImageRes(pack, questionIndex, normalized)

    SecretPlanAnswerCodec.decode(normalized)?.let { pair ->
        return CoupleResultPresentation(
            displayText = pair.first.text,
            detailLines = listOf("Zweite Wahl: ${pair.second.text}"),
            imageRes = resultImageRes,
            structured = true
        )
    }

    WeekendEchoAnswerCodec.decode(normalized)?.let { round ->
        val first = WeekendEchoMotif.fromKey(round.firstMotifKey)?.displayText ?: round.firstMotifKey
        val second = WeekendEchoMotif.fromKey(round.secondMotifKey)?.displayText ?: round.secondMotifKey
        return CoupleResultPresentation(
            displayText = first,
            detailLines = listOf("Zweite Auswahl: $second"),
            imageRes = resultImageRes,
            structured = true
        )
    }

    EitherOrAnswerCodec.decode(normalized)?.let { choice ->
        return CoupleResultPresentation(
            displayText = choice.userChoice,
            detailLines = listOf(
                "Partner: ${choice.partnerChoice}",
                if (choice.isMatch) "Gleiche Wahl ✓" else "Unterschiedliche Wahl"
            ),
            imageRes = resultImageRes,
            structured = true
        )
    }

    PredictionAnswerCodec.decode(normalized)?.let { prediction ->
        return CoupleResultPresentation(
            displayText = "Vermutung: ${prediction.prediction}",
            detailLines = listOf(
                "Tatsächlich: ${prediction.actual}",
                if (prediction.isHit) "Treffer ✓" else "Anders eingeschätzt"
            ),
            imageRes = resultImageRes,
            structured = true
        )
    }

    if (options.isNotEmpty()) {
        RankingAnswerCodec.decode(normalized, options)?.let { order ->
            return CoupleResultPresentation(
                displayText = "Ranking",
                detailLines = order.mapIndexed { index, value -> "${index + 1}. $value" },
                imageRes = resultImageRes,
                structured = true
            )
        }

        PersonAssignmentCodec.decode(normalized, options)?.let { assignments ->
            return CoupleResultPresentation(
                displayText = "Rollenverteilung",
                detailLines = options.map { option ->
                    val side = assignments[option]
                    val owner = if (side == PersonSide.USER) "Ich" else "Partner"
                    "$option → $owner"
                },
                imageRes = resultImageRes,
                structured = true
            )
        }
    }

    MemoryMatchAnswerCodec.decode(normalized)?.let { memory ->
        val details = buildList {
            if (memory.partnerText.isNotBlank()) add("Zweite Erinnerung: ${memory.partnerText}")
        }
        return CoupleResultPresentation(
            displayText = memory.text.ifBlank { "Erinnerung gespeichert" },
            detailLines = details,
            imageRes = resultImageRes,
            localImagePath = memory.imagePath,
            structured = true
        )
    }

    PairedChoiceAnswerCodec.decode(normalized)?.let { pair ->
        return CoupleResultPresentation(
            displayText = pair.first,
            detailLines = listOf("Zweite Antwort: ${pair.second}"),
            imageRes = resultImageRes,
            structured = true
        )
    }

    if (normalized == "DRAWING_COMPLETED") {
        return CoupleResultPresentation(
            displayText = "Zeichnung abgeschlossen",
            imageRes = resultImageRes,
            structured = true
        )
    }

    return CoupleResultPresentation(
        displayText = normalized,
        imageRes = resultImageRes
    )
}

@DrawableRes
internal fun coupleResultImageRes(
    pack: QuestionPack,
    questionIndex: Int,
    answer: String
): Int? {
    val kind = harmonyImageChoiceKind(pack, questionIndex) ?: return null
    if (kind == HarmonyImageChoiceKind.HAPPY_COUPLE) {
        return happyCoupleImageResForAnswer(answer)
    }

    val options = pack.questions.getOrNull(questionIndex)?.options.orEmpty()
    val optionIndex = options.indexOf(answer)
    if (optionIndex < 0) return null

    if (kind in AUTUMN_EVENING_KINDS) {
        return autumnEveningVisuals(kind).images.getOrNull(optionIndex)
    }

    return legacyImageChoiceResultImageRes(kind, optionIndex)
}
