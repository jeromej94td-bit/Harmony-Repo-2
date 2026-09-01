package com.example.data.model

import java.util.Locale

data class FranchiseContentIssue(
    val packId: String,
    val matchedTerm: String,
    val text: String,
    val questionIndex: Int? = null,
    val optionIndex: Int? = null
)

/**
 * Stage 06.5 audit for franchise-specific wording where Harmony can use an IP-neutral equivalent
 * without losing the gameplay idea. General media/product vocabulary is intentionally not blocked.
 */
object FranchiseContentAudit {
    private data class FranchiseTerm(val label: String, val regex: Regex)

    private val terms = listOf(
        FranchiseTerm("Hogwarts", Regex("\\bHogwarts\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Harry Potter", Regex("\\bHarry\\s+Potter\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Pokémon", Regex("\\bPok[eé]mon\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Star Wars", Regex("\\bStar\\s+Wars\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Marvel", Regex("\\bMarvel\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Avengers", Regex("\\bAvengers\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Game of Thrones", Regex("\\bGame\\s+of\\s+Thrones\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Der Herr der Ringe", Regex("\\b(?:Der\\s+)?Herr\\s+der\\s+Ringe\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Lord of the Rings", Regex("\\bLord\\s+of\\s+the\\s+Rings\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Super Mario", Regex("\\bSuper\\s+Mario\\b", RegexOption.IGNORE_CASE)),
        FranchiseTerm("Zelda", Regex("\\b(?:The\\s+Legend\\s+of\\s+)?Zelda\\b", RegexOption.IGNORE_CASE))
    )

    fun audit(packs: List<QuestionPack>): List<FranchiseContentIssue> = buildList {
        packs.forEach { pack ->
            scan(pack.id, pack.title, null, null)?.let(::add)
            pack.questions.forEachIndexed { questionIndex, question ->
                scan(pack.id, question.q, questionIndex, null)?.let(::add)
                question.options.forEachIndexed { optionIndex, option ->
                    scan(pack.id, option, questionIndex, optionIndex)?.let(::add)
                }
            }
        }
    }

    private fun scan(
        packId: String,
        text: String,
        questionIndex: Int?,
        optionIndex: Int?
    ): FranchiseContentIssue? {
        if (text.isBlank()) return null
        val normalized = text.trim().lowercase(Locale.ROOT)
        if (normalized.isBlank()) return null
        val match = terms.firstOrNull { it.regex.containsMatchIn(text) } ?: return null
        return FranchiseContentIssue(
            packId = packId,
            matchedTerm = match.label,
            text = text,
            questionIndex = questionIndex,
            optionIndex = optionIndex
        )
    }
}
