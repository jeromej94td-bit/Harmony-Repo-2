package com.example.data.model

import java.util.Locale

enum class GermanWordingDefectKind {
    KNOWN_TYPO,
    UNRESOLVED_TEMPLATE,
    PLACEHOLDER_TEXT,
    DUPLICATED_WORD
}

data class GermanWordingDefect(
    val packId: String,
    val kind: GermanWordingDefectKind,
    val text: String,
    val questionIndex: Int? = null,
    val optionIndex: Int? = null,
    val detail: String
)

/**
 * Stage 06.3 audit for objectively broken German wording in the runtime catalogue.
 *
 * This intentionally does not try to score style or rewrite natural language. It only flags
 * regression signatures that are unambiguously broken: known typo fragments, unresolved template
 * tokens, placeholder/debug copy and accidentally repeated functional words.
 */
object GermanWordingDefectAudit {
    private val unresolvedTemplate = Regex("""\{[A-Za-z_][^}\n]{0,40}}""")
    private val placeholderText = Regex(
        pattern = """\b(TODO|TBD|FIXME|LOREM\s+IPSUM|PLACEHOLDER)\b""",
        option = RegexOption.IGNORE_CASE
    )
    private val duplicatedFunctionalWord = Regex(
        pattern = """\b(und|oder|aber|denn|weil|dass|ist|sind|war|waren|du|dein|deine|deiner|deinem|deinen|euch|ihr|ihre|was|wie|welche|welcher|welches|lieber|eher|mehr|weniger)\s+\1\b""",
        option = RegexOption.IGNORE_CASE
    )

    private val knownTypos = listOf(
        Regex("""\bSchlagwewohnheiten\b""", RegexOption.IGNORE_CASE) to
            "Bekannter Tippfehler: Schlafgewohnheiten.",
        Regex("""\bis\s+deinem\s+Partner\b""", RegexOption.IGNORE_CASE) to
            "Bekannter Grammatikrest: ist deinem Partner."
    )

    fun audit(packs: List<QuestionPack>): List<GermanWordingDefect> = buildList {
        packs.forEach { pack ->
            inspect(
                packId = pack.id,
                text = pack.title,
                questionIndex = null,
                optionIndex = null
            )?.let(::add)

            pack.questions.forEachIndexed { questionIndex, question ->
                inspect(
                    packId = pack.id,
                    text = question.q,
                    questionIndex = questionIndex,
                    optionIndex = null
                )?.let(::add)

                question.options.forEachIndexed { optionIndex, option ->
                    inspect(
                        packId = pack.id,
                        text = option,
                        questionIndex = questionIndex,
                        optionIndex = optionIndex
                    )?.let(::add)
                }
            }
        }
    }

    private fun inspect(
        packId: String,
        text: String,
        questionIndex: Int?,
        optionIndex: Int?
    ): GermanWordingDefect? {
        knownTypos.firstOrNull { (pattern, _) -> pattern.containsMatchIn(text) }?.let { (_, detail) ->
            return defect(
                packId = packId,
                text = text,
                questionIndex = questionIndex,
                optionIndex = optionIndex,
                kind = GermanWordingDefectKind.KNOWN_TYPO,
                detail = detail
            )
        }

        if (unresolvedTemplate.containsMatchIn(text)) {
            return defect(
                packId = packId,
                text = text,
                questionIndex = questionIndex,
                optionIndex = optionIndex,
                kind = GermanWordingDefectKind.UNRESOLVED_TEMPLATE,
                detail = "Nicht aufgelöster Template-Platzhalter im sichtbaren Text."
            )
        }

        if (placeholderText.containsMatchIn(text)) {
            return defect(
                packId = packId,
                text = text,
                questionIndex = questionIndex,
                optionIndex = optionIndex,
                kind = GermanWordingDefectKind.PLACEHOLDER_TEXT,
                detail = "Interner Platzhalter-/Debugtext im sichtbaren Inhalt."
            )
        }

        if (duplicatedFunctionalWord.containsMatchIn(text.lowercase(Locale.ROOT))) {
            return defect(
                packId = packId,
                text = text,
                questionIndex = questionIndex,
                optionIndex = optionIndex,
                kind = GermanWordingDefectKind.DUPLICATED_WORD,
                detail = "Funktionswort wurde unmittelbar doppelt ausgegeben."
            )
        }

        return null
    }

    private fun defect(
        packId: String,
        text: String,
        questionIndex: Int?,
        optionIndex: Int?,
        kind: GermanWordingDefectKind,
        detail: String
    ) = GermanWordingDefect(
        packId = packId,
        kind = kind,
        text = text,
        questionIndex = questionIndex,
        optionIndex = optionIndex,
        detail = detail
    )
}
