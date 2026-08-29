package com.example.data

/**
 * Entfernt bekannte Generator-Resttexte, bevor Harmony-360-Packs in den Runtime-Rework gehen.
 * Die Regel ist absichtlich eng gefasst und verändert keine bereits deutschen Prompts.
 */
object GeneratedHarmony360TextCleanup {
    private val englishRankingTemplate = Regex(
        "^What decides whether „(.+)“ is special for you\\? Rank: (.+)$"
    )

    fun apply(pack: GenPack): GenPack {
        var changed = false
        val questions = pack.questions.map { question ->
            val match = englishRankingTemplate.matchEntire(question.q)
            if (match == null) {
                question
            } else {
                changed = true
                val subject = match.groupValues[1]
                val optionsText = match.groupValues[2]
                question.copy(
                    q = "Was entscheidet, ob „$subject“ für dich besonders ist? Ordne: $optionsText"
                )
            }
        }

        return if (changed) pack.copy(questions = questions) else pack
    }
}
