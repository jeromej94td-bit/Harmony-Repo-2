package com.example.data

/**
 * Canonical runtime version of "Liebe im Gleichgewicht" with the visual
 * rainy-couple choice as question 1. Keeping this pack in the generated layer
 * lets the Dev-Studio/runtime merge replace the embedded legacy pack by id
 * without duplicating a second visible game.
 */
object GeneratedHarmonyHappyCouple {
    const val VERSION: Long = 2026082901L

    val PACKS: List<GenPack> = listOf(
        GenPack(
            id = "liebegleichgewicht",
            title = "Liebe im Gleichgewicht",
            tags = listOf("unterhaltung"),
            cat = "lieber",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(
                GenQuestion(
                    q = "Welches Paar ist GLÜCKLICH?",
                    options = listOf("1", "2", "3", "4")
                ),
                GenQuestion(
                    q = "Was fällt dir in unserer Beziehung leichter?",
                    options = listOf("Geben", "Nehmen", "Beides gleich", "Es ist situationsabhängig")
                ),
                GenQuestion(
                    q = "Wie triffst du am liebsten Entscheidungen für uns?",
                    options = listOf(
                        "Ich schlage vor, du entscheidest",
                        "Du schlägst vor, ich entscheide",
                        "Gemeinsam stundenlang diskutieren",
                        "Spontan abwechseln"
                    )
                ),
                GenQuestion(
                    q = "Wer investiert gefühlt mehr Zeit in die Beziehungsarbeit?",
                    options = listOf("{user}", "{partner}", "Absolut ausgeglichen", "Wir machen das unbewusst")
                ),
                GenQuestion(
                    q = "Wie wichtig ist dir persönlicher Freiraum?",
                    options = listOf(
                        "Extrem wichtig",
                        "Wichtig, aber zu zweit ist besser",
                        "Lieber fast alles zusammen machen",
                        "Ein gesundes Mittelmaß"
                    )
                ),
                GenQuestion(
                    q = "Wie gehen wir mit unterschiedlichen Meinungen um?",
                    options = listOf(
                        "Wir finden immer einen Kompromiss",
                        "Einer gibt meistens nach",
                        "Wir akzeptieren, dass wir uneinig sind",
                        "Wir diskutieren leidenschaftlich"
                    )
                ),
                GenQuestion(
                    q = "Fühlst du dich in deinen Bedürfnissen voll gesehen?",
                    options = listOf(
                        "Ja, immer",
                        "Meistens",
                        "Manchmal wünsche ich mir mehr Aufmerksamkeit",
                        "Wir arbeiten daran"
                    )
                ),
                GenQuestion(
                    q = "Wer von uns initiiert häufiger tiefe Gespräche?",
                    options = listOf("{user}", "{partner}", "Beide gleich", "Das ergibt sich von selbst")
                ),
                GenQuestion(
                    q = "Wie ausgeglichen ist unsere Aufgabenverteilung im Alltag?",
                    options = listOf(
                        "Sehr fair",
                        "Könnte besser sein",
                        "Einer macht fast alles",
                        "Wir haben keine feste Struktur"
                    )
                ),
                GenQuestion(
                    q = "Wie gehen wir mit Fehlern des anderen um?",
                    options = listOf(
                        "Schnell verzeihen",
                        "Darüber reden, bis alles geklärt ist",
                        "Erstmal schmollen",
                        "Wir lachen es oft weg"
                    )
                ),
                GenQuestion(
                    q = "Was stärkt das Gleichgewicht unserer Liebe am meisten?",
                    options = listOf(
                        "Regelmäßige Dates",
                        "Kleine Aufmerksamkeiten",
                        "Ehrliches Feedback",
                        "Gemeinsame Zukunftspläne"
                    )
                )
            )
        )
    )
}
