package com.example.data

/**
 * Kleine, explizite Reparaturen für bereits generierten Content.
 *
 * Die generierten Quelldateien bleiben dadurch unangetastet; bekannte fehlerhafte
 * Einträge werden erst beim Aufbau der Runtime-Registry korrigiert.
 */
object GeneratedContentRepairPolicy {
    private const val HOGWARTS_PACK_ID = "cj_hogwarts_quiz"
    private const val HOGWARTS_MALFORMED_PROMPT =
        "Ich habe noch nie behauptet, Slytherin sei eigentlich gar nicht so böse."

    private val neverHaveIEverOptions = listOf("Habe ich", "Habe ich noch nie")

    fun repair(pack: GenPack): GenPack {
        if (pack.id != HOGWARTS_PACK_ID) return pack

        return pack.copy(
            questions = pack.questions.map { question ->
                if (question.q == HOGWARTS_MALFORMED_PROMPT && question.options.isEmpty()) {
                    question.copy(options = neverHaveIEverOptions)
                } else {
                    question
                }
            }
        )
    }
}
