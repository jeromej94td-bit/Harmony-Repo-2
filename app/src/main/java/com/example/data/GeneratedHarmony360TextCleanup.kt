package com.example.data

/**
 * Entfernt bekannte Generator-Resttexte und eindeutige Tippfehler, bevor Harmony-360-Packs
 * in den Runtime-Rework gehen. Die Regeln sind absichtlich eng gefasst.
 */
object GeneratedHarmony360TextCleanup {
    private val englishRankingTemplate = Regex(
        "^What decides whether „(.+)“ is special for you\\? Rank: (.+)$"
    )

    fun apply(pack: GenPack): GenPack {
        var changed = false
        val questions = pack.questions.map { question ->
            val cleanedPrompt = cleanPrompt(question.q)
            if (cleanedPrompt == question.q) {
                question
            } else {
                changed = true
                question.copy(q = cleanedPrompt)
            }
        }

        return if (changed) pack.copy(questions = questions) else pack
    }

    private fun cleanPrompt(prompt: String): String {
        englishRankingTemplate.matchEntire(prompt)?.let { match ->
            val subject = match.groupValues[1]
            val optionsText = match.groupValues[2]
            return "Was entscheidet, ob „$subject“ für dich besonders ist? Ordne: $optionsText"
        }

        return prompt
            .replace("Schlagwewohnheiten", "Schlafgewohnheiten")
            .replace(" is deinem Partner ", " ist deinem Partner ")
    }
}
