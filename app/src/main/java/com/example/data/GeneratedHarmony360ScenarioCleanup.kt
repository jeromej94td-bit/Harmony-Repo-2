package com.example.data

object GeneratedHarmony360ScenarioCleanup {
    private data class Replacement(
        val prompt: String,
        val options: List<String>
    )

    private val replacements = mapOf(
        "h500_126_morgenroutine_szenario" to Replacement(
            prompt = "Ihr müsst morgen zur gleichen Zeit los: Einer braucht morgens Ruhe, der andere will sofort reden und planen. Was probiert ihr zuerst?",
            options = listOf(
                "10 Minuten Ruhe, dann gemeinsam planen",
                "Aufgaben am Vorabend verteilen",
                "Jeder macht seine Routine, Treffpunkt beim Frühstück",
                "Eine Woche lang zwei Varianten testen"
            )
        ),
        "h500_236_sportliche_ziele_szenario" to Replacement(
            prompt = "Einer will für einen Halbmarathon trainieren, der andere möchte Bewegung ohne Leistungsdruck. Wie findet ihr einen gemeinsamen Rhythmus?",
            options = listOf(
                "Ein gemeinsamer lockerer Termin pro Woche",
                "Getrennte Ziele, gegenseitig anfeuern",
                "Ein gemeinsames Mini-Ziel festlegen",
                "Abwechselnd die Aktivität bestimmen"
            )
        ),
        "h500_296_buecher_szenario" to Replacement(
            prompt = "Einer möchte abends gemeinsam lesen, der andere lieber eine Serie schauen. Wie macht ihr daraus etwas, das für beide funktioniert?",
            options = listOf(
                "20 Minuten lesen, dann Serie",
                "Getrennt genießen und danach austauschen",
                "Abwechselnde Themenabende",
                "Ein Hörbuch als gemeinsamen Mittelweg testen"
            )
        )
    )

    fun apply(pack: GenPack): GenPack {
        val replacement = replacements[pack.id] ?: return pack
        if (pack.questions.isEmpty()) return pack

        return pack.copy(
            questions = pack.questions.mapIndexed { index, question ->
                if (index == 0) {
                    question.copy(q = replacement.prompt, options = replacement.options)
                } else {
                    question
                }
            }
        )
    }
}
