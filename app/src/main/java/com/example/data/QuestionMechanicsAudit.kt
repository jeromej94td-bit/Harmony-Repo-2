package com.example.data

import com.example.data.model.FullscreenGameMechanicKind
import com.example.data.model.QuestionInteractionPolicy
import com.example.data.model.QuestionPack
import com.example.data.model.QuestionResponseKind

enum class QuestionAuditKind {
    PHOTO_SEMANTICS_CANDIDATE,
    ORDERING_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC,
    PREDICTION_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC,
    EMPTY_OPTIONS_WITHOUT_OPEN_TEXT,
    DUPLICATE_OPTIONS,
    GENERIC_FALLBACK_IN_SOURCE_OPTIONS,
    UNSTABLE_INDEX_SPECIAL_CASE
}

data class QuestionAuditFinding(
    val packId: String,
    val questionIndex: Int,
    val question: String,
    val responseKind: QuestionResponseKind,
    val fullscreenMechanic: FullscreenGameMechanicKind?,
    val kind: QuestionAuditKind,
    val reason: String
)

/**
 * Read-only quality scanner for Harmony question content.
 *
 * Text heuristics live only in this audit. A finding is a review candidate and never changes
 * runtime routing. Runtime mechanics continue to come exclusively from explicit policies/curation.
 */
object QuestionMechanicsAudit {
    private val directPhotoSelection = Regex(
        pattern = "(?i)(\\bwelches\\b.{0,60}\\b(foto|bild)\\b|\\b(foto|bild)\\b.{0,60}\\b(auswählen|auswaehlen|wählen|waehlen|zeigen|hochladen|aussuchen)\\b)"
    )
    private val predictionPrompt = Regex(
        pattern = "(?i)(was glaubst du.{0,90}(partner|partnerin)|was würde.{0,90}(partner|partnerin)|wie würde.{0,90}(partner|partnerin)|tippe.{0,90}(partner|partnerin))"
    )
    private val orderingTokens = listOf(
        "ordne",
        "ordnet",
        "sortiere",
        "sortiert",
        "rangliste",
        "ranking",
        "reihenfolge",
        "priorisiere",
        "priorisiert"
    )
    private val genericFallbackOptions = setOf(
        "schreibe deine eigene antwort",
        "deine eigene antwort",
        "eigene antwort",
        "write your own answer",
        "your own answer"
    )

    fun scan(packs: List<QuestionPack>): List<QuestionAuditFinding> = buildList {
        packs.forEach { pack ->
            pack.questions.forEachIndexed { index, question ->
                val spec = QuestionInteractionPolicy.resolveSpec(pack, index, question)
                val normalizedOptions = question.options.map { it.trim().lowercase() }
                val rawQuestionLower = question.q.lowercase()

                fun addFinding(kind: QuestionAuditKind, reason: String) {
                    add(
                        QuestionAuditFinding(
                            packId = pack.id,
                            questionIndex = index,
                            question = question.q,
                            responseKind = spec.responseKind,
                            fullscreenMechanic = spec.fullscreenMechanic,
                            kind = kind,
                            reason = reason
                        )
                    )
                }

                if (normalizedOptions.size != normalizedOptions.distinct().size) {
                    addFinding(
                        QuestionAuditKind.DUPLICATE_OPTIONS,
                        "Mindestens zwei Antwortoptionen sind nach Trim/Normalisierung identisch."
                    )
                }

                if (normalizedOptions.any { it in genericFallbackOptions }) {
                    addFinding(
                        QuestionAuditKind.GENERIC_FALLBACK_IN_SOURCE_OPTIONS,
                        "Eine generische Freitext-Antwort steckt bereits im Quellinhalt und sollte explizit modelliert werden."
                    )
                }

                if (
                    spec.responseKind !in setOf(
                        QuestionResponseKind.PHOTO_ONLY,
                        QuestionResponseKind.CHOICE_WITH_OPTIONAL_PHOTO
                    ) && directPhotoSelection.containsMatchIn(question.q)
                ) {
                    addFinding(
                        QuestionAuditKind.PHOTO_SEMANTICS_CANDIDATE,
                        "Die Formulierung verlangt wahrscheinlich die Auswahl eines konkreten Fotos/Bildes, ist aber nicht als Foto-Interaktion klassifiziert."
                    )
                }

                val hasOrderingLanguage = orderingTokens.any { token -> rawQuestionLower.contains(token) }
                val hasOrderingMechanic = spec.fullscreenMechanic in setOf(
                    FullscreenGameMechanicKind.RANK_ORDER,
                    FullscreenGameMechanicKind.PRIORITY_POKER
                )
                if (hasOrderingLanguage && !hasOrderingMechanic) {
                    addFinding(
                        QuestionAuditKind.ORDERING_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC,
                        "Die Frage spricht von Sortieren/Rangfolge, hat aber keine explizite Ordnungsmechanik."
                    )
                }

                if (
                    predictionPrompt.containsMatchIn(question.q) &&
                    spec.fullscreenMechanic != FullscreenGameMechanicKind.PARTNER_PREDICTION
                ) {
                    addFinding(
                        QuestionAuditKind.PREDICTION_SEMANTICS_WITHOUT_EXPLICIT_MECHANIC,
                        "Die Frage fordert eine Partner-Prognose, ist aber nicht als Partner-Prognose geroutet."
                    )
                }

                if (
                    pack.type == "quiz" &&
                    question.options.isEmpty() &&
                    spec.responseKind != QuestionResponseKind.OPEN_TEXT &&
                    spec.fullscreenMechanic == null
                ) {
                    addFinding(
                        QuestionAuditKind.EMPTY_OPTIONS_WITHOUT_OPEN_TEXT,
                        "Quizfrage hat keine Optionen und ist zugleich nicht als Freitext oder Spezialmechanik aufgelöst."
                    )
                }

                val indexTag = pack.tags.any {
                    it == "interaction_person_assignment_$index" || it == "interaction_rank_order_$index"
                }
                if (indexTag) {
                    addFinding(
                        QuestionAuditKind.UNSTABLE_INDEX_SPECIAL_CASE,
                        "Diese Mechanik hängt noch an einer veränderlichen Fragenposition und sollte beim nächsten Touch auf einen stabilen Schlüssel migriert werden."
                    )
                }
            }
        }
    }
}
