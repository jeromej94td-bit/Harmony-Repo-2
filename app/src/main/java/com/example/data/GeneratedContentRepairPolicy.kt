package com.example.data

/**
 * Kleine, explizite Reparaturen für bereits generierten Content.
 *
 * Die generierten Quelldateien bleiben dadurch unangetastet; bekannte strukturelle
 * Defekte werden erst beim Aufbau der Runtime-Registry korrigiert.
 */
object GeneratedContentRepairPolicy {
    private val neverHaveIEverOptions = listOf("Habe ich", "Habe ich noch nie")

    fun repair(pack: GenPack): GenPack {
        if ("ichhabenochnie" !in pack.tags) return pack

        return pack.copy(
            questions = pack.questions.map { question ->
                if (question.options.isEmpty()) {
                    question.copy(options = neverHaveIEverOptions)
                } else {
                    question
                }
            }
        )
    }
}
