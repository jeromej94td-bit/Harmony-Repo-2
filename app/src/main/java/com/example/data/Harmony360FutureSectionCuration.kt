package com.example.data

/** Explicit Stage 05.3 curation for Harmony-360 Section 03 — Zukunft & Lebensplanung. */
object Harmony360FutureSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_051_unser_naechstes_jahr_entweder_oder" to CurationDecision.REWRITE,
        "h500_052_in_fuenf_jahren_wer_eher" to CurationDecision.ARCHIVE,
        "h500_053_traumwohnung_skala" to CurationDecision.ARCHIVE,
        "h500_054_traumhaus_ranking" to CurationDecision.REWRITE,
        "h500_055_stadt_oder_land_prognose" to CurationDecision.ARCHIVE,
        "h500_056_auswandern_szenario" to CurationDecision.REWRITE,
        "h500_057_karriereplaene_geheime_wahl" to CurationDecision.ARCHIVE,
        "h500_058_finanzielle_ziele_memory" to CurationDecision.REWRITE,
        "h500_060_hochzeit_offene_runde" to CurationDecision.REWRITE,
        "h500_061_familienplanung_entweder_oder" to CurationDecision.REWRITE,
        "h500_062_lebensstil_wer_eher" to CurationDecision.ARCHIVE,
        "h500_064_abenteuerliste_ranking" to CurationDecision.REWRITE,
        "h500_065_bucket_list_prognose" to CurationDecision.ARCHIVE,
        "h500_066_wohnort_szenario" to CurationDecision.REWRITE,
        "h500_067_prioritaeten_geheime_wahl" to CurationDecision.ARCHIVE,
        "h500_069_selbststaendigkeit_prioritaet" to CurationDecision.ARCHIVE,
        "h500_070_sicherheit_oder_freiheit_offene_runde" to CurationDecision.REWRITE,
        "h500_075_das_leben_mit_60_prognose" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_051_unser_naechstes_jahr_entweder_oder" to listOf(
            q("Was wünschst du dir für euer nächstes Jahr eher?", "Mehr Stabilität", "Mehr Veränderung"),
            q("Was sollte eher passieren?", "Ein großer gemeinsamer Schritt", "Viele kleine Verbesserungen"),
            q("Wofür würdest du freie Zeit eher einsetzen?", "Mehr reisen", "Mehr Zuhause aufbauen"),
            q("Wie planst du das Jahr lieber?", "Früh wichtige Eckpunkte festlegen", "Bewusst viel Spielraum lassen"),
            q("Wohin sollte zusätzliches Geld eher fließen?", "Gemeinsame Erlebnisse", "Rücklagen und Sicherheit"),
            q("Wenn sich plötzlich eine große Chance ergibt?", "Mutig zugreifen", "Erst gemeinsam gründlich prüfen")
        ),
        "h500_054_traumhaus_ranking" to listOf(
            q("Welche Eigenschaften wären dir bei einem Traumhaus am wichtigsten? Ordne.", "Garten", "Große Küche", "Zusätzliches Zimmer", "Energieeffizienz"),
            q("Was entscheidet beim Standort am stärksten? Ordne.", "Kurzer Arbeitsweg", "Nähe zur Natur", "Gute Infrastruktur", "Nähe zu Familie und Freunden"),
            q("Welche Kostenfrage wiegt für dich am schwersten? Ordne.", "Kaufpreis", "Laufende Kosten", "Renovierungsbedarf", "Flexible Finanzierung"),
            q("Was macht ein Haus im Alltag am meisten lebenswert? Ordne.", "Viel Tageslicht", "Guter Grundriss", "Schöner Außenbereich", "Genug Stauraum"),
            q("Wo würdest du am ehesten Kompromisse machen? Ordne.", "Größe", "Lage", "Zustand", "Ausstattung"),
            q("Was wäre langfristig am wertvollsten? Ordne.", "Barrierearm nutzbar", "Homeoffice-tauglich", "Platz für Gäste", "Niedriger Energieverbrauch")
        ),
        "h500_056_auswandern_szenario" to listOf(
            q("Einer von euch bekommt ein starkes Jobangebot im Ausland. Was ist der erste sinnvolle Schritt?", "Bedingungen gemeinsam prüfen", "Land für einige Tage testen", "Finanzen durchrechnen", "Erst über persönliche Grenzen sprechen"),
            q("Nur einer spricht die Landessprache gut. Wie geht ihr damit um?", "Gemeinsam vorab lernen", "Vor Ort intensiv lernen", "Englisch als Übergang nutzen", "Umzug erst bei solider Sprachbasis"),
            q("Eure Familien reagieren sehr unterschiedlich auf den Plan. Was zählt bei der Entscheidung?", "Eure gemeinsame Perspektive", "Nähe zur Familie", "Regelmäßige Besuchsmöglichkeiten", "Erst eine Testphase"),
            q("Die Lebenshaltungskosten sind deutlich höher als erwartet. Was macht ihr?", "Budget neu rechnen", "Andere Stadt prüfen", "Umzug verschieben", "Weniger Wohnkomfort akzeptieren"),
            q("Ihr seid nach drei Monaten uneinig, ob ihr bleiben wollt. Was wäre fair?", "Festes Prüfdatum vereinbaren", "Noch drei Monate testen", "Rückkehr konkret planen", "Alternatives Land prüfen"),
            q("Aufenthaltsrecht und Bürokratie werden komplizierter als gedacht. Was tut ihr?", "Professionelle Beratung holen", "Aufgaben klar aufteilen", "Zeitplan verlängern", "Plan B für den Wohnort vorbereiten")
        ),
        "h500_058_finanzielle_ziele_memory" to listOf(
            GenQuestion("Wofür hast du zum ersten Mal bewusst länger Geld zurückgelegt?"),
            GenQuestion("Welche finanzielle Entscheidung aus deinem bisherigen Leben macht dich heute noch stolz?"),
            GenQuestion("Welche Haltung zu Geld hast du besonders stark aus deiner Familie oder Kindheit übernommen?"),
            GenQuestion("Welches größere finanzielle Ziel hast du schon einmal erreicht – und wie hat sich das angefühlt?"),
            GenQuestion("Bei welcher Ausgabe dachtest du später: Das hätte ich anders entscheiden sollen?"),
            GenQuestion("Gab es einen Moment, in dem Geld oder finanzielle Sicherheit einen größeren Zukunftsplan von dir verändert hat?")
        ),
        "h500_060_hochzeit_offene_runde" to listOf(
            GenQuestion("Welche persönliche Bedeutung hätte eine Hochzeit für dich – unabhängig von Feier, Fotos und Erwartungen anderer?"),
            GenQuestion("Wie würde sich eine Hochzeit anfühlen, die wirklich zu dir passt: eher klein und persönlich oder groß und festlich – und warum?"),
            GenQuestion("Welcher Teil einer Hochzeit müsste unbedingt persönlich sein, damit sie sich nicht austauschbar anfühlt?"),
            GenQuestion("Welche Tradition rund um Hochzeit würdest du gern behalten – und welche problemlos weglassen?"),
            GenQuestion("Was wäre dir wichtiger: die rechtliche Entscheidung, das Versprechen zwischen euch oder die gemeinsame Feier – und warum?"),
            GenQuestion("Welche Erwartung oder Grenze zum Thema Hochzeit sollte dein Partner über dich unbedingt kennen?")
        ),
        "h500_061_familienplanung_entweder_oder" to listOf(
            q("Wie möchtest du Familienplanung eher angehen?", "Früh klare Vorstellungen austauschen", "Schrittweise gemeinsam entwickeln"),
            q("Was ist dir bei langfristiger Lebensplanung eher wichtig?", "Konkrete Zeitfenster", "Flexibilität für Veränderungen"),
            q("Falls Kinder Teil eurer Zukunft sind: Was wäre dir eher wichtig?", "Nähe zu Familie", "Eigene Strukturen aufbauen"),
            q("Wie würdest du Betreuung eher denken?", "Möglichst gleichmäßig teilen", "Je nach Lebensphase flexibel aufteilen"),
            q("Wie sollten Karriere und Familienplanung eher zusammenspielen?", "Früh aufeinander abstimmen", "Erst anpassen, wenn es konkret wird"),
            q("Wenn ihr beim Thema noch nicht gleich denkt?", "Zeitnah Klarheit suchen", "Ohne Druck im Gespräch bleiben")
        ),
        "h500_064_abenteuerliste_ranking" to listOf(
            q("Welche Art Zukunftsabenteuer reizt dich am meisten? Ordne.", "Große Reise", "Neue Fähigkeit lernen", "Gemeinsames Herzensprojekt", "Körperliche Herausforderung"),
            q("Was macht einen Punkt auf eurer Abenteuerliste wirklich wichtig? Ordne.", "Emotionale Bedeutung", "Einmaligkeit", "Gemeinsamer Wunsch", "Realistische Machbarkeit"),
            q("Welche Zeitspanne passt dir für große Vorhaben am besten? Ordne.", "Dieses Jahr", "In zwei bis drei Jahren", "Später bewusst planen", "Spontan wenn die Chance kommt"),
            q("Wofür würdest du am ehesten viel Geld oder Zeit investieren? Ordne.", "Reise", "Weiterbildung", "Eigenes Projekt", "Besonderes Erlebnis"),
            q("Was dürfte eine Abenteuerliste am wenigsten werden? Ordne vom größten Störfaktor.", "Pflichtprogramm", "Zu teuer", "Nur einseitige Wünsche", "Endlos aufgeschoben"),
            q("Was sollte nach fünf Jahren am stärksten sichtbar sein? Ordne.", "Mutiger geworden", "Mehr gemeinsam erlebt", "Etwas aufgebaut", "Neue Seiten aneinander entdeckt")
        ),
        "h500_066_wohnort_szenario" to listOf(
            q("Einer bekommt einen Traumjob in einer anderen Stadt, der andere möchte bleiben. Wie startet ihr die Entscheidung?", "Beide Konsequenzen sammeln", "Probezeit mit Pendeln", "Umzug ernsthaft durchrechnen", "Alternative Jobs prüfen"),
            q("Eure Miete steigt stark. Was wäre der sinnvollste nächste Schritt?", "Neu verhandeln oder prüfen", "Andere Wohnung suchen", "Stadtteil wechseln", "Budget an anderer Stelle anpassen"),
            q("Einer will urban leben, der andere näher an die Natur. Was testet ihr?", "Ruhigen Stadtrand", "Kleinere Stadt", "Zwei Wunschorte besuchen", "Prioritäten gewichten"),
            q("Ein Familienmitglied braucht künftig regelmäßig Unterstützung. Wie beeinflusst das euren Wohnort?", "Nähe stärker gewichten", "Fahrtzeiten realistisch prüfen", "Aufgaben mit Familie teilen", "Wohnort trotzdem unabhängig wählen"),
            q("Nach Monaten findet ihr keine Wohnung, die alle Wünsche erfüllt. Was gebt ihr zuerst frei?", "Größe", "Lage", "Ausstattung", "Einzugszeitpunkt"),
            q("Ihr seid unsicher, ob ein neuer Ort wirklich passt. Was wäre ein guter Test?", "Mehrere Wochen dort leben", "Alltagswege ausprobieren", "Mit Menschen vor Ort sprechen", "Kosten und Pendeln simulieren")
        ),
        "h500_070_sicherheit_oder_freiheit_offene_runde" to listOf(
            GenQuestion("Was bedeutet Sicherheit für dich ganz konkret: Geld, Zuhause, Verlässlichkeit, Routine oder etwas anderes?"),
            GenQuestion("Bei welchem Lebensbereich ist dir Freiheit wichtiger als maximale Sicherheit?"),
            GenQuestion("Wie viel finanziellen Puffer brauchst du ungefähr, damit du dich bei einer größeren Entscheidung entspannt fühlst?"),
            GenQuestion("Was passt stärker zu deinem Zukunftsbild: ein fester Lebensmittelpunkt oder die Möglichkeit, öfter neu anzufangen – und warum?"),
            GenQuestion("Wie viel individuellen Freiraum brauchst du, damit sich eine verbindliche gemeinsame Zukunft trotzdem nach dir selbst anfühlt?"),
            GenQuestion("Wo würde Sicherheit für dich irgendwann in Begrenzung kippen?")
        ),
        "h500_075_das_leben_mit_60_prognose" to listOf(
            q("Wie würde dein Partner mit 60 vermutlich am liebsten leben?", "Viel reisen", "Fester ruhiger Lebensmittelpunkt", "Weiter aktiv arbeiten", "Mehr Zeit für eigene Projekte"),
            q("Welcher Wohnort passt wahrscheinlich am besten zu seinem Zukunftsbild?", "Lebendige Stadt", "Ruhiger Stadtrand", "Nähe zur Natur", "Teilweise im Ausland"),
            q("Wofür würde dein Partner dann wahrscheinlich am liebsten Geld ausgeben?", "Erlebnisse", "Komfort zuhause", "Familie und Freunde", "Hobbys und Projekte"),
            q("Wie sähe ein guter normaler Tag für deinen Partner vermutlich aus?", "Viel unterwegs", "Ruhig und selbstbestimmt", "Mit Menschen umgeben", "An eigenen Dingen arbeiten"),
            q("Was wäre deinem Partner im Rückblick wahrscheinlich besonders wichtig?", "Genug Zeit füreinander", "Mutige Entscheidungen", "Finanzielle Stabilität", "Viele unterschiedliche Erfahrungen"),
            q("Was würde dein Partner mit 60 wahrscheinlich am wenigsten bereuen wollen?", "Zu wenig gereist", "Zu viel gearbeitet", "Zu selten Neues gewagt", "Zu wenig Zeit mit wichtigen Menschen")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        when {
            decisions[pack.id] == CurationDecision.ARCHIVE -> null
            pack.id in overrides -> pack.copy(questions = overrides.getValue(pack.id))
            else -> pack
        }
    }
}
