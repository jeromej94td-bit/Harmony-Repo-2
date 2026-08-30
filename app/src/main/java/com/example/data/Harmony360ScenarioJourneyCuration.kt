package com.example.data

/**
 * Keeps the eight-decision contract for fullscreen Harmony 360 scenario games after
 * hand-curated packs replace older generated eight-question variants with shorter, better copy.
 *
 * Every shortened scenario gets explicit subject-specific additions; there is deliberately no
 * generic fallback template because generic filler is exactly what the curation pipeline removes.
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
        "h500_076_wochenendtrip_szenario" to listOf(
            q("Samstagnachmittag merkt ihr, dass euer Wochenendplan zu voll ist. Was streicht ihr zuerst?", "Das unwichtigste Must-see", "Den weitesten Weg", "Die feste Abendplanung", "Nichts – Tempo nur reduzieren"),
            q("Die Rückfahrt verzögert sich deutlich und Montag wird früh. Was ist euch wichtiger?", "Schnellste Alternative suchen", "Gelassen beim Plan bleiben", "Letzten Programmpunkt streichen", "Kosten und Erholung gemeinsam abwägen")
        ),
        "h500_086_kulinarische_reise_szenario" to listOf(
            q("Ihr entdeckt einen lokalen Markt, obwohl für mittags schon ein Restaurant geplant ist. Was macht ihr?", "Markt gemeinsam ausprobieren", "Nur Kleinigkeiten teilen", "Restaurant behalten", "Reservierung nach hinten schieben"),
            q("Am letzten Abend könnt ihr nur ein kulinarisches Erlebnis wiederholen. Wie entscheidet ihr?", "Gemeinsamen Favoriten", "Jeder nennt ein Argument", "Etwas Neues statt Wiederholung", "Der begeistertere Wunsch gewinnt")
        ),
        "h500_106_suesses_szenario" to listOf(
            q("Ihr wollt ein Dessert teilen, mögt aber völlig unterschiedliche Sachen. Was bestellt ihr?", "Zwei kleine Desserts", "Einen gemeinsamen Kompromiss", "Heute entscheidet einer", "Etwas völlig Neues für beide"),
            q("Ihr backt etwas Süßes für Gäste und das Rezept misslingt kurz vorher. Was tut ihr?", "Einfaches Ersatzdessert", "Improvisieren und retten", "Etwas Gutes kaufen", "Mit Humor genau so servieren")
        ),
        "h500_126_morgenroutine_szenario" to listOf(
            q("Einer muss sehr früh los, der andere könnte länger schlafen. Wie schützt ihr beides?", "Abends vorbereiten", "Leise getrennte Abläufe", "Kurzer gemeinsamer Moment", "Nur am Wochenende gemeinsam starten"),
            q("Morgens entsteht immer wieder Streit wegen Kleinigkeiten. Was würdest du zuerst ändern?", "Weniger Entscheidungen morgens", "Mehr Zeitpuffer", "Aufgaben klar verteilen", "Schwierige Gespräche auf später legen")
        ),
        "h500_136_dekoration_szenario" to listOf(
            q("Ihr habt nur Budget für ein größeres Wohnungsprojekt. Wie entscheidet ihr?", "Meistgenutzten Raum zuerst", "Größten gemeinsamen Wunsch", "Dringendstes Problem lösen", "Budget auf zwei kleine Projekte teilen"),
            q("Eure Wohnung wirkt irgendwann mehr nach einer Person als nach euch beiden. Was wäre die beste Korrektur?", "Gemeinsam neu auswählen", "Jeder gestaltet einen Bereich", "Lieblingsstücke beider sichtbar machen", "Nur strittige Dinge austauschen")
        ),
        "h500_156_museen_szenario" to listOf(
            q("Eine Sonderausstellung interessiert nur einen von euch, kostet aber viel zusätzliche Zeit. Was macht ihr?", "Gemeinsam kurz hinein", "Interessierter geht allein", "Anderes gemeinsames Highlight", "Beim nächsten Besuch priorisieren"),
            q("Einer möchte jedes Schild lesen, der andere nur die Highlights sehen. Wie bleibt der Besuch für beide gut?", "Treffpunkt und freie Zeit", "Abwechselnd Tempo bestimmen", "Audioguide nur für einen", "Gemeinsam nur Lieblingsräume")
        ),
        "h500_166_instrumente_szenario" to listOf(
            q("Ihr lernt gemeinsam ein Instrument, aber einer macht viel schneller Fortschritte. Was hält die Motivation fair?", "Nicht vergleichen", "Getrennte Übungsziele", "Gemeinsames leichtes Stück", "Abwechselnd voneinander lernen"),
            q("Üben passt zeitlich kaum in euren Alltag. Was wäre realistischer?", "Zwei kurze feste Termine", "Nur am Wochenende", "Abwechselnd allein üben", "Projekt bewusst pausieren")
        ),
        "h500_176_beste_freunde_szenario" to listOf(
            q("Dein bester Freund lädt nur dich zu einem wichtigen Wochenende ein. Wie gehst du als Paar damit um?", "Offen erklären, warum es wichtig ist", "Partner nach Gefühl fragen", "Zeit davor oder danach schützen", "Gemeinsam nach Alternative suchen"),
            q("Ein enger Freund erzählt dir wiederholt sehr private Dinge über eure Beziehung weiter. Was machst du?", "Klare Grenze setzen", "Weniger Privates erzählen", "Direkt über Vertrauen sprechen", "Kontakt vorerst stärker trennen")
        ),
        "h500_186_elternabende_szenario" to listOf(
            q("Falls Elternabende für euch relevant sind: Beide können bei einem wichtigen Termin nicht gleichzeitig. Wie entscheidet ihr?", "Wer das Thema besser kennt", "Abwechselnd teilnehmen", "Dringlichkeit entscheidet", "Andere Informationsmöglichkeit nutzen"),
            q("Falls ihr gemeinsam Verantwortung für ein Kind tragt: Nach einem Elternabend bewertet ihr dieselbe Rückmeldung völlig anders. Was hilft zuerst?", "Beobachtungen trennen", "Kind zuerst anhören", "Nachfragen statt deuten", "Später gemeinsam entscheiden")
        ),
        "h500_196_finanzielle_unabhaengigkeit_szenario" to listOf(
            q("Einer möchte eine größere persönliche Ausgabe komplett aus eigenem Geld bezahlen. Was sollte trotzdem gemeinsam geklärt sein?", "Auswirkung auf gemeinsame Ziele", "Nur wenn Rücklagen betroffen sind", "Zeitpunkt der Ausgabe", "Gar nichts, wenn gemeinsame Pflichten stehen")
            ,q("Eure Einkommen entwickeln sich sehr unterschiedlich. Was schützt Gleichwertigkeit am besten?", "Faire statt gleiche Beiträge", "Persönlichen Spielraum für beide", "Gemeinsame Ziele transparent halten", "Regeln regelmäßig neu prüfen")
        ),
        "h500_206_erben_szenario" to listOf(
            q("Nach einem Erbe möchte einer sofort etwas Großes verändern, der andere erst abwarten. Was ist fair?", "Bedenkzeit vereinbaren", "Nur kleinen Teil verplanen", "Ziele getrennt sammeln", "Externe Sachfragen zuerst klären"),
            q("Das Erbe weckt Erwartungen in der Familie, die ihr als Paar nicht teilen möchtet. Wie reagiert ihr?", "Gemeinsame Grenze formulieren", "Erbende Person spricht zuerst", "Keine Entscheidung unter Druck", "Nur konkrete Bitten einzeln prüfen")
        ),
        "h500_216_work_life_balance_szenario" to listOf(
            q("Ein freier Abend wird kurzfristig wieder von Arbeit gefressen. Wie verhindert ihr, dass das zur Normalität wird?", "Ersatzzeit direkt festlegen", "Dringlichkeit gemeinsam einordnen", "Klare arbeitsfreie Zeiten", "Nach mehreren Fällen Grundproblem ansprechen"),
            q("Einer hat beruflich gerade deutlich mehr Belastung. Wie verteilt ihr zuhause fair, ohne eine Dauerlösung daraus zu machen?", "Zeitlich begrenzte Entlastung", "Nur wichtigste Aufgaben", "Wöchentlich neu prüfen", "Externe Hilfe nutzen, wenn möglich")
        ),
        "h500_226_kuendigung_szenario" to listOf(
            q("Nach einer Kündigung kommt ein schnelles Jobangebot, das nicht wirklich passt. Was wäre ein guter Umgang?", "Finanzlage gegen Passung abwägen", "Bedenkzeit nutzen", "Parallel weitersuchen", "Nur als Übergang annehmen"),
            q("Die Jobsuche belastet plötzlich eure Beziehung. Was sollte zuerst getrennt werden?", "Unterstützung und Kontrolle", "Geldsorgen und persönliche Bewertung", "Jobsuche und Paarzeit", "Tagesstruktur und gemeinsame Erwartungen")
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
        "h500_296_buecher_szenario" to listOf(
            q("Ihr lest dasselbe Buch und einer verrät aus Versehen einen wichtigen Twist. Wie rettet ihr die gemeinsame Leseidee?", "Ohne weitere Spoiler weiterlesen", "Kurz getrennt weiterlesen", "Anderes Buch gemeinsam starten", "Mit Humor abhaken"),
            q("Nach dem Buch möchtet ihr darüber reden, bewertet es aber komplett gegensätzlich. Was macht die Diskussion gut?", "Lieblingsstellen vergleichen", "Erst Gründe statt Urteil", "Unterschiede stehen lassen", "Rezensionen erst danach lesen")
        ),
        "h500_306_dokumentationen_szenario" to listOf(
            q("Eine Doku behandelt ein Thema, das einen von euch stark beschäftigt und den anderen kaum. Wie schaut ihr sie?", "Gemeinsam und danach reden", "Interessierter schaut allein", "Nur eine Folge zusammen testen", "Anderes gemeinsames Thema wählen")
            ,q("Nach einer Doku seid ihr unsicher, wie ausgewogen sie war. Was macht ihr?", "Weitere Quelle ansehen", "Originalquellen suchen", "Unterschiedliche Einordnungen vergleichen", "Urteil erst einmal offenlassen")
        ),
        "h500_366_stressreaktionen_szenario" to listOf(
            q("Ihr reagiert unter Stress genau gegensätzlich: einer will reden, einer Ruhe. Was hilft?", "Kurze Pause plus feste Gesprächszeit", "Erst Sicherheit geben", "Bedürfnisse klar benennen", "Heute nur das Dringende klären"),
            q("Nach einer stressigen Phase merkt ihr, dass ihr nur noch funktioniert habt. Was wäre euer erster Neustart?", "Ein freier Abend ohne Pflichten", "Aufgaben neu verteilen", "Offener Check-in", "Etwas Kleines gemeinsam unternehmen")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map { pack ->
        if (pack.cat != "h360_szenario" && "mechanik_szenario" !in pack.tags) return@map pack
        val extra = additions[pack.id] ?: return@map pack

        when {
            pack.questions.size == 8 -> pack
            pack.questions.size < 8 -> pack.copy(questions = (pack.questions + extra).take(8))
            else -> pack.copy(questions = pack.questions.take(8))
        }
    }
}
