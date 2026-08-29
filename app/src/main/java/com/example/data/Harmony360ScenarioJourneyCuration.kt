package com.example.data

/**
 * Keeps the existing eight-decision contract for fullscreen Harmony 360 scenario games after
 * hand-curated packs replace older generated eight-question variants with shorter, better copy.
 */
object Harmony360ScenarioJourneyCuration {

    private fun q(text: String, vararg options: String) = GenQuestion(text, options.toList())

    private val additions: Map<String, List<GenQuestion>> = mapOf(
        "h500_006_vermissen_szenario" to listOf(
            q("Ihr habt beide einen schlechten Tag, aber kaum Zeit füreinander. Was hält trotzdem Verbindung?", "Kurzer ehrlicher Anruf", "Sprachnachricht später", "Nur ein liebevolles Zeichen", "Abends bewusst nachholen"),
            q("Ein geplanter Besuch muss kurzfristig ausfallen. Was hilft dir am ehesten?", "Neuen Termin direkt festlegen", "Kurz enttäuscht sein dürfen", "Gemeinsamen Videoabend planen", "Eine kleine Überraschung schicken")
        ),
        "h500_026_zuhoeren_szenario" to listOf(
            q("Du erzählst etwas Wichtiges und merkst, dass dein Partner dich falsch verstanden hat. Was hilft zuerst?", "Kurz neu formulieren", "Fragen, was angekommen ist", "Konkretes Beispiel nennen", "Gespräch kurz verlangsamen"),
            q("Dein Partner möchte reden, du hast aber gerade keinen Kopf dafür. Was ist am fairsten?", "Ehrlich sagen und Zeitpunkt nennen", "Zehn Minuten bewusst zuhören", "Kurz fragen, wie dringend es ist", "Ablenkung beenden und Raum schaffen")
        ),
        "h500_036_kritik_annehmen_szenario" to listOf(
            q("Du merkst erst später, dass an der Kritik deines Partners etwas dran war. Was machst du?", "Von selbst darauf zurückkommen", "Mich entschuldigen", "Konkrete Änderung vorschlagen", "Erst erklären, was ich verstanden habe"),
            q("Die Kritik ist berechtigt, aber im falschen Ton gesagt. Wie trennst du beides?", "Inhalt anerkennen, Ton ansprechen", "Erst über den Ton reden", "Pause und später beides klären", "Nach konkretem Wunsch fragen")
        ),
        "h500_056_auswandern_szenario" to listOf(
            q("Nach sechs Monaten im Ausland bekommt einer starkes Heimweh. Was wäre für euch ein fairer Umgang?", "Besuch zuhause planen", "Ursachen offen besprechen", "Rückkehr als echte Option zulassen", "Alltag vor Ort gezielt verbessern"),
            q("Die Auswanderung war ein gemeinsamer Traum, funktioniert aber anders als erwartet. Wann würdet ihr neu entscheiden?", "Nach einer festen Probezeit", "Wenn einer dauerhaft unglücklich ist", "Nach Klärung von Job und Finanzen", "Erst nach mehreren konkreten Verbesserungsversuchen")
        ),
        "h500_066_wohnort_szenario" to listOf(
            q("Ein Wohnort gefällt euch beiden, ist aber deutlich teurer als geplant. Was wäre dein sinnvollster Kompromiss?", "Kleinere Wohnung", "Andere Gegend", "Budget an anderer Stelle kürzen", "Weiter suchen"),
            q("Nach dem Umzug merkt einer, dass er sich dort nicht zuhause fühlt. Was macht ihr zuerst?", "Gründe konkret sammeln", "Mehr soziale Kontakte aufbauen", "Wohnsituation verändern", "Zeitpunkt für Neubewertung festlegen")
        ),
        "h500_126_morgenroutine_szenario" to listOf(
            q("Einer muss sehr früh los, der andere könnte länger schlafen. Wie schützt ihr beides?", "Abends vorbereiten", "Leise getrennte Abläufe", "Kurzer gemeinsamer Moment", "Nur am Wochenende gemeinsam starten"),
            q("Morgens entsteht immer wieder Streit wegen Kleinigkeiten. Was würdest du zuerst ändern?", "Weniger Entscheidungen morgens", "Mehr Zeitpuffer", "Aufgaben klar verteilen", "Schwierige Gespräche auf später legen")
        ),
        "h500_136_dekoration_szenario" to listOf(
            q("Ihr habt nur Budget für ein größeres Wohnungsprojekt. Wie entscheidet ihr?", "Meistgenutzten Raum zuerst", "Größten gemeinsamen Wunsch", "Dringendstes Problem lösen", "Budget auf zwei kleine Projekte teilen"),
            q("Eure Wohnung wirkt irgendwann mehr nach einer Person als nach euch beiden. Was wäre die beste Korrektur?", "Gemeinsam neu auswählen", "Jeder gestaltet einen Bereich", "Lieblingsstücke beider sichtbar machen", "Nur strittige Dinge austauschen")
        ),
        "h500_236_sportliche_ziele_szenario" to listOf(
            q("Einer erreicht sein Ziel viel schneller als der andere. Was hält das gemeinsame Projekt fair?", "Keine Vergleiche", "Individuelle Ziele", "Fortschritt beider feiern", "Training teilweise trennen"),
            q("Ihr merkt, dass das Ziel zwar gesund klingt, aber nur noch Stress macht. Was tut ihr?", "Ziel verkleinern", "Eine Woche Pause", "Sportart wechseln", "Spaß wieder vor Leistung stellen")
        ),
        "h500_256_missverstaendnisse_szenario" to listOf(
            q("Ihr habt beide das Gefühl, euch klar ausgedrückt zu haben – und meint trotzdem Verschiedenes. Was hilft am meisten?", "Jeder beschreibt sein Verständnis", "Konkrete Beispiele nennen", "Begriffe klären", "Gemeinsames Ziel zuerst benennen"),
            q("Ein Missverständnis ist geklärt, aber das verletzte Gefühl bleibt. Was braucht es danach?", "Wirkung anerkennen", "Entschuldigung", "Nähe anbieten", "Später noch einmal nachfragen")
        ),
        "h500_266_nachgeben_szenario" to listOf(
            q("Ihr merkt, dass ein Kompromiss niemanden wirklich zufrieden macht. Was wäre besser?", "Dritte Lösung suchen", "Entscheidung vertagen", "Prioritäten neu erklären", "Diesmal klar einer Person den Wunsch geben"),
            q("Dein Partner gibt nach, wirkt danach aber enttäuscht. Was machst du?", "Noch einmal nachfragen", "Entscheidung neu öffnen", "Bedeutung seines Wunsches verstehen", "Ausgleich konkret vereinbaren")
        ),
        "h500_366_stressreaktionen_szenario" to listOf(
            q("Ihr reagiert unter Stress genau gegensätzlich: einer will reden, einer Ruhe. Was hilft?", "Kurze Pause plus feste Gesprächszeit", "Erst Sicherheit geben", "Bedürfnisse klar benennen", "Heute nur das Dringende klären"),
            q("Nach einer stressigen Phase merkt ihr, dass ihr nur noch funktioniert habt. Was wäre euer erster Neustart?", "Ein freier Abend ohne Pflichten", "Aufgaben neu verteilen", "Offener Check-in", "Etwas Kleines gemeinsam unternehmen")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map { pack ->
        val extra = additions[pack.id] ?: return@map pack
        if (pack.cat != "h360_szenario" && "mechanik_szenario" !in pack.tags) return@map pack

        when {
            pack.questions.size == 8 -> pack
            pack.questions.size < 8 -> pack.copy(
                questions = (pack.questions + extra).take(8)
            )
            else -> pack.copy(questions = pack.questions.take(8))
        }
    }
}
