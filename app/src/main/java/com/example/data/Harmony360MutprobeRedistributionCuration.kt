package com.example.data

/**
 * Re-homes useful ideas from the mixed-topic Mutprobe source pack before that source is merged
 * out of Section 20. Sorting in Harmony is pack-level, so a mixed pack must not be mislabeled as
 * one destination topic. Instead, its useful ideas are folded into real existing destination packs.
 */
object Harmony360MutprobeRedistributionCuration {
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")
    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")

    private fun q(text: String, vararg options: String) = GenQuestion(text, options.toList())
    private fun open(text: String) = GenQuestion(text)
    private fun whoQ(text: String) = GenQuestion(text, who)
    private fun scaleQ(text: String) = GenQuestion(text, scale)

    internal data class Redistribution(
        val sourceIdea: String,
        val destinationPackId: String,
        val replacedQuestion: String,
        val replacement: GenQuestion
    )

    internal val redistributions: List<Redistribution> = listOf(
        Redistribution(
            sourceIdea = "Bungee-Jumping oder Fallschirmspringen wagen",
            destinationPackId = "h500_085_abenteuerurlaub_prognose",
            replacedQuestion = "Welche Aktivität würde dein Partner wahrscheinlich zuerst wählen?",
            replacement = q(
                "Welche Reise-Mutprobe würde dein Partner am ehesten wagen?",
                "Bungee-Jumping", "Fallschirmspringen", "Rafting", "Klettersteig"
            )
        ),
        Redistribution(
            sourceIdea = "ins eiskalte Wasser springen",
            destinationPackId = "h500_085_abenteuerurlaub_prognose",
            replacedQuestion = "Wann würde dein Partner eher Nein sagen?",
            replacement = q(
                "Welche kleine Wasser-Mutprobe würde dein Partner auf einer Reise am ehesten wagen?",
                "Ins kalte Wasser springen", "Eine geführte Canyoning-Tour", "Nachts im sicheren Badebereich schwimmen", "Lieber keine davon"
            )
        ),
        Redistribution(
            sourceIdea = "falsches Essen im Restaurant ansprechen",
            destinationPackId = "h500_119_restaurantwahl_prioritaet",
            replacedQuestion = "Was wäre für dich der größte Grund, ein Restaurant nicht wieder zu besuchen?",
            replacement = q(
                "Wenn im Restaurant das falsche Essen kommt: Was ist dir am wichtigsten?",
                "Freundlich reklamieren", "Erst prüfen, ob es trotzdem passt", "Klar sagen, was bestellt war", "Nur bei echtem Problem reklamieren"
            )
        ),
        Redistribution(
            sourceIdea = "unbekannte Gerichte auf Reisen probieren",
            destinationPackId = "h500_103_streetfood_skala",
            replacedQuestion = "Wie mutig bist du bei unbekannten Gewürzen und Texturen?",
            replacement = scaleQ("Wie gern probierst du auf Reisen ein Gericht, das du vorher noch nie gesehen oder gegessen hast?")
        ),
        Redistribution(
            sourceIdea = "bei Ungerechtigkeit gegenüber Fremden einschreiten",
            destinationPackId = "h500_340_gerechtigkeit_offene_runde",
            replacedQuestion = "Bei welchem Thema reagierst du besonders sensibel auf Ungerechtigkeit?",
            replacement = open("Wenn du siehst, dass jemand vor dir unfair behandelt wird: Wann würdest du dich einmischen – und wovon hängt es ab?")
        ),
        Redistribution(
            sourceIdea = "spontan vor vielen Menschen sprechen",
            destinationPackId = "h500_272_charaktereigenschaften_wer_eher",
            replacedQuestion = "Wer ist bei neuen Menschen schneller offen?",
            replacement = whoQ("Wer würde eher spontan vor vielen Menschen etwas sagen oder eine kleine Rede halten?")
        ),
        Redistribution(
            sourceIdea = "im Alltag mutiger sein",
            destinationPackId = "h500_272_charaktereigenschaften_wer_eher",
            replacedQuestion = "Wer hält länger an einem einmal gefassten Plan fest?",
            replacement = whoQ("Wer traut sich im Alltag eher, etwas Neues zu machen, obwohl es Überwindung kostet?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> {
        val byDestination = redistributions.groupBy { it.destinationPackId }
        return packs.map { pack ->
            val moves = byDestination[pack.id].orEmpty()
            if (moves.isEmpty()) return@map pack

            val updated = pack.questions.toMutableList()
            moves.forEach { move ->
                val index = updated.indexOfFirst { it.q == move.replacedQuestion }
                if (index >= 0) updated[index] = move.replacement
            }
            pack.copy(questions = updated)
        }
    }
}
