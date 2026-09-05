package com.example.data

/** Curated six-round Harmony Panda image-choice game: Herz oder Kopf. */
object GeneratedHarmonyHeartOrHead {
    const val VERSION: Long = 1788590000000L

    val PACKS: List<GenPack> = listOf(
        GenPack(
            id = "herz_oder_kopf",
            title = "Herz oder Kopf",
            cat = "lieber",
            topic = "beziehung",
            type = "quiz",
            tags = listOf("beziehung", "bildauswahl", "harmony-panda", "herzoderkopf"),
            emoji = "💗",
            questions = listOf(
                GenQuestion(
                    q = "Welcher Abend fühlt sich am meisten nach dir an?",
                    options = listOf(
                        "Spontaner Nachtspaziergang",
                        "Geplantes Dinner",
                        "Picknick bei Sonnenuntergang",
                        "Gemütlicher Filmabend"
                    )
                ),
                GenQuestion(
                    q = "Was bedeutet dir bei einem Geschenk am meisten?",
                    options = listOf(
                        "Handgeschriebener Brief",
                        "Praktisches Geschenk",
                        "Kleine Überraschung",
                        "Gemeinsames Erlebnis"
                    )
                ),
                GenQuestion(
                    q = "Wie gehst du eher mit Spannung um?",
                    options = listOf(
                        "Sofort reden",
                        "Erst Ruhe, dann klären",
                        "Nähe suchen",
                        "Gedanken sortieren"
                    )
                ),
                GenQuestion(
                    q = "Was gibt dir in eurer Zukunft am meisten Sicherheit?",
                    options = listOf(
                        "Ein starkes Gefühl füreinander",
                        "Ein konkreter gemeinsamer Plan",
                        "Gemeinsame Erinnerungen",
                        "Klare Absprachen & Stabilität"
                    )
                ),
                GenQuestion(
                    q = "Was zeigt Liebe für dich im Alltag am stärksten?",
                    options = listOf(
                        "Ein tiefer Blick",
                        "Verlässliche Unterstützung",
                        "Spontane Zärtlichkeit",
                        "Mitdenken & Organisieren"
                    )
                ),
                GenQuestion(
                    q = "Wenn du lieben müsstest – worauf vertraust du zuerst?",
                    options = listOf(
                        "Herz",
                        "Kopf",
                        "Bauchgefühl",
                        "Balance"
                    )
                )
            )
        )
    )
}
