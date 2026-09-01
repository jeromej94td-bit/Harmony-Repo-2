package com.example.data.model

import java.util.Locale

enum class GermanContentLanguageIssueKind {
    ENGLISH_PROMPT,
    ENGLISH_OPTION
}

data class GermanContentLanguageIssue(
    val packId: String,
    val questionIndex: Int,
    val kind: GermanContentLanguageIssueKind,
    val text: String,
    val optionIndex: Int? = null
) {
    override fun toString(): String {
        val location = buildString {
            append(packId)
            append('#')
            append(questionIndex + 1)
            optionIndex?.let { append(" option ").append(it + 1) }
        }
        return "$location: $kind: $text"
    }
}

/**
 * Stage 06.2 audit for clear English sentence residue in the German runtime catalogue.
 *
 * The detector is intentionally conservative. Harmony legitimately uses short English loanwords,
 * brands and established food/media terms (for example "Happy End", "Fine Dining", "Netflix"
 * or "Blue-Rare"). Those must not be treated as language defects. We only flag sentence-shaped
 * English prompts and answer options with high-confidence English grammar at the beginning.
 */
object GermanContentLanguageAudit {
    private val whitespace = Regex("\\s+")

    private val explicitGeneratorFragments = listOf(
        "what decides whether",
        "what matters most",
        "what would you",
        "what would your",
        "which option would",
        "which answer would",
        "how would you",
        "how likely",
        "rank these",
        "choose the",
        "who would be more likely"
    )

    private val englishQuestionStarter = Regex(
        pattern = "^(what|which|who|where|when|why|how)\\b.*\\b(is|are|was|were|do|does|did|would|could|should|can|will|makes?|matters?|feels?|means?|fits?|works?|prefer|choose|pick|relive)\\b.*[?!.]?$",
        option = RegexOption.IGNORE_CASE
    )

    private val englishAuxiliaryStarter = Regex(
        pattern = "^(would|could|should|do|does|did|can|will|are|is|have|has)\\s+(you|your|we|they|he|she|it)\\b.*[?!.]?$",
        option = RegexOption.IGNORE_CASE
    )

    private val englishImperativeStarter = Regex(
        pattern = "^(rank|choose|pick|select|describe|tell|name)\\s+(the|these|this|your|one|which|what)\\b.*[?!.]?$",
        option = RegexOption.IGNORE_CASE
    )

    private val englishAnswerStarter = Regex(
        pattern = "^(i|we|my|our)\\s+(would|will|want|prefer|choose|pick|need|feel|think|like|love|hate|can|could|should|am|are|have)\\b.+$",
        option = RegexOption.IGNORE_CASE
    )

    fun audit(packs: List<QuestionPack>): List<GermanContentLanguageIssue> = buildList {
        for (pack in packs) {
            for ((questionIndex, question) in pack.questions.withIndex()) {
                if (isClearEnglishPrompt(question.q)) {
                    add(
                        GermanContentLanguageIssue(
                            packId = pack.id,
                            questionIndex = questionIndex,
                            kind = GermanContentLanguageIssueKind.ENGLISH_PROMPT,
                            text = question.q
                        )
                    )
                }

                question.options.forEachIndexed { optionIndex, option ->
                    if (isClearEnglishOption(option)) {
                        add(
                            GermanContentLanguageIssue(
                                packId = pack.id,
                                questionIndex = questionIndex,
                                kind = GermanContentLanguageIssueKind.ENGLISH_OPTION,
                                text = option,
                                optionIndex = optionIndex
                            )
                        )
                    }
                }
            }
        }
    }

    private fun isClearEnglishPrompt(raw: String): Boolean {
        val text = normalize(raw)
        if (text.isBlank() || text.count(Char::isLetter) < 8) return false

        val lower = text.lowercase(Locale.ROOT)
        if (explicitGeneratorFragments.any(lower::contains)) return true

        return englishQuestionStarter.matches(text) ||
            englishAuxiliaryStarter.matches(text) ||
            englishImperativeStarter.matches(text)
    }

    private fun isClearEnglishOption(raw: String): Boolean {
        val text = normalize(raw)
        if (text.isBlank() || text.count(Char::isLetter) < 10) return false
        return englishAnswerStarter.matches(text)
    }

    private fun normalize(raw: String): String = raw.trim().replace(whitespace, " ")
}
