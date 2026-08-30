package com.example.data

/** Explicit Stage 05.3 curation for Harmony-360 Section 10 — Arbeit & Karriere. */
object Harmony360WorkCareerSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun open(text: String): GenQuestion = GenQuestion(q = text)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_211_arbeitszeiten_entweder_oder" to CurationDecision.REWRITE,
        "h500_212_ueberstunden_wer_eher" to CurationDecision.ARCHIVE,
        "h500_213_karriere_skala" to CurationDecision.ARCHIVE,
        "h500_214_selbststaendigkeit_ranking" to CurationDecision.REWRITE,
        "h500_215_berufliche_veraenderung_prognose" to CurationDecision.REWRITE,
        "h500_216_work_life_balance_szenario" to CurationDecision.REWRITE,
        "h500_217_geheimnis_arbeitsplatz_geheime_wahl" to CurationDecision.REWRITE,
        "h500_218_erster_job_memory" to CurationDecision.ARCHIVE,
        "h500_219_berufliche_ziele_prioritaet" to CurationDecision.REWRITE,
        "h500_220_job_und_beziehung_offene_runde" to CurationDecision.REWRITE,
        "h500_221_nebenjob_entweder_oder" to CurationDecision.REWRITE,
        "h500_222_chef_sein_wer_eher" to CurationDecision.ARCHIVE,
        "h500_223_weiterbildung_skala" to CurationDecision.ARCHIVE,
        "h500_224_arbeitsweg_ranking" to CurationDecision.REWRITE,
        "h500_225_ruhestand_prognose" to CurationDecision.REWRITE,
        "h500_226_kuendigung_szenario" to CurationDecision.REWRITE,
        "h500_227_kollegen_geheime_wahl" to CurationDecision.REWRITE,
        "h500_230_beruflicher_erfolg_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_211_arbeitszeiten_entweder_oder" to listOf(
            q("Welche Arbeitszeit passt eher zu deinem Alltag?", "Feste Arbeitszeiten", "Flexible Arbeitszeiten"),
            q("Was wäre dir lieber?", "Früh anfangen und früh fertig", "Später starten und später fertig"),
            q("Wie würdest du zusätzliche Arbeitszeit eher ausgleichen?", "Freizeitausgleich", "Auszahlen lassen"),
            q("Was fühlt sich langfristig besser an?", "Vier längere Arbeitstage", "Fünf kürzere Arbeitstage"),
            q("Wenn ihr beide viel arbeitet?", "Gemeinsame Zeit fest einplanen", "Woche möglichst flexibel lassen"),
            q("Bei unterschiedlichen Arbeitsrhythmen?", "Rhythmen möglichst angleichen", "Jeder behält seinen eigenen Rhythmus")
        ),
        "h500_214_selbststaendigkeit_ranking" to listOf(
            q("Was wäre dir bei Selbstständigkeit am wichtigsten? Ordne.", "Selbstbestimmung", "Planbares Einkommen", "Zeitliche Freiheit", "Sinnvolle Arbeit"),
            q("Was müsste vor einer Selbstständigkeit zuerst stehen? Ordne.", "Finanzpuffer", "Realistischer Plan", "Erste Kunden oder Nachfrage", "Rückhalt im Alltag"),
            q("Welches Risiko würdest du am stärksten gewichten? Ordne.", "Schwankendes Einkommen", "Mehr Arbeitszeit", "Weniger Urlaub", "Verantwortung allein tragen"),
            q("Welche Unterstützung vom Partner wäre am wertvollsten? Ordne.", "Ehrliches Feedback", "Praktische Entlastung", "Emotionaler Rückhalt", "Klare finanzielle Grenzen"),
            q("Was sollte trotz Selbstständigkeit geschützt bleiben? Ordne.", "Paarzeit", "Erholung", "Eigene Hobbys", "Finanzielle Sicherheit"),
            q("Woran würdest du Erfolg zuerst messen? Ordne.", "Genug Einkommen", "Mehr Freiheit", "Zufriedene Kunden", "Freude an der Arbeit")
        ),
        "h500_215_berufliche_veraenderung_prognose" to listOf(
            q("Welche berufliche Veränderung würde dein Partner am ehesten wagen?", "Neue Firma", "Neue Branche", "Weniger Stunden", "Mehr Verantwortung"),
            q("Was müsste für deinen Partner vor einem Jobwechsel am ehesten stimmen?", "Aufgabe passt", "Gehalt passt", "Arbeitszeiten passen", "Team wirkt gut"),
            q("Was würde deinen Partner vermutlich stärker zum Wechsel bewegen?", "Dauerhafter Stress", "Keine Entwicklung", "Schlechtes Team", "Zu wenig Flexibilität"),
            q("Wie würde dein Partner eine gute neue Chance eher prüfen?", "Erst gründlich recherchieren", "Mit vertrauten Menschen sprechen", "Schnell ein Gespräch führen", "Vor- und Nachteile aufschreiben"),
            q("Welchen Preis würde dein Partner für einen Traumjob am ehesten akzeptieren?", "Längerer Arbeitsweg", "Weniger Sicherheit", "Vorübergehend mehr Arbeit", "Umzug"),
            q("Was wäre deinem Partner nach einer Veränderung am wichtigsten?", "Mehr Zufriedenheit", "Mehr Zeit", "Mehr Einkommen", "Mehr Entwicklung")
        ),
        "h500_216_work_life_balance_szenario" to listOf(
            q("Überstunden werden plötzlich jede Woche normal. Was sollte zuerst passieren?", "Belastung offen ansprechen", "Klare Grenze setzen", "Prioritäten im Job klären", "Gemeinsamen Alltag neu planen"),
            q("Einer kommt wochenlang erschöpft nach Hause. Wie reagiert ihr am sinnvollsten?", "Erholung zuerst schützen", "Aufgaben zuhause neu verteilen", "Arbeitsproblem konkret besprechen", "Feste Paarzeit klein halten, aber schützen"),
            q("Ein wichtiger Karriereschritt verlangt sechs harte Monate. Was macht ihn als Paar tragbar?", "Klares Enddatum", "Regelmäßige Check-ins", "Faire Entlastung zuhause", "Gemeinsames Ziel dahinter"),
            q("Beide haben gleichzeitig eine stressige Arbeitsphase. Was hilft am meisten?", "Ansprüche zuhause senken", "Termine reduzieren", "Aufgaben sehr klar teilen", "Bewusst kleine Ruhezeiten schaffen"),
            q("Arbeit unterbricht ständig eure gemeinsame Zeit. Was wäre eine faire Grenze?", "Handyzeiten begrenzen", "Nur echte Notfälle zulassen", "Bestimmte Abende arbeitsfrei", "Je nach Woche flexibel entscheiden"),
            q("Einer möchte Karriere beschleunigen, der andere mehr gemeinsame Zeit. Was ist der erste Schritt?", "Bedürfnisse konkret machen", "Zeitfenster vereinbaren", "Gemeinsame Prioritäten setzen", "Mehrere Modelle ausprobieren")
        ),
        "h500_217_geheimnis_arbeitsplatz_geheime_wahl" to listOf(
            q("Was würdest du vom Arbeitsplatz am ehesten privat für dich behalten?", "Kleine Peinlichkeit", "Konflikt mit Kollegen", "Eigene Unsicherheit", "Frust über Führung"),
            q("Was würdest du deinem Partner trotzdem immer erzählen?", "Etwas, das mich stark belastet", "Große berufliche Chance", "Ernster Konflikt", "Wichtige Entscheidung"),
            q("Welche Grenze bei Arbeitsgeschichten ist dir am wichtigsten?", "Vertrauliches bleibt vertraulich", "Keine Namen bei sensiblen Themen", "Partner darf alles hören", "Nur erzählen, was mich selbst betrifft"),
            q("Was verschweigst du eher, um zuhause Ruhe zu haben?", "Kleine Ärgernisse", "Büroklatsch", "Unwichtige Fehler", "Nichts bewusst"),
            q("Wann sollte ein Arbeitsthema zuhause unbedingt auf den Tisch?", "Wenn es Gesundheit belastet", "Wenn es gemeinsame Pläne betrifft", "Wenn Kündigung droht", "Wenn ich Unterstützung brauche"),
            q("Was wäre für dich problematischer?", "Zu viel aus dem Job erzählen", "Wichtige Belastungen komplett verschweigen", "Vertrauliches weitergeben", "Partner mit jedem Detail überladen")
        ),
        "h500_219_berufliche_ziele_prioritaet" to listOf(
            q("Was steht bei deinen beruflichen Zielen am höchsten?", "Gute Arbeit machen", "Genug verdienen", "Zeitliche Freiheit", "Weiter lernen"),
            q("Welches Ziel würdest du bei Zielkonflikten zuerst schützen?", "Gesundheit", "Beziehung und Familie", "Finanzielle Stabilität", "Berufliche Entwicklung"),
            q("Was wäre ein starker nächster Schritt?", "Neue Verantwortung", "Bessere Bedingungen", "Neue Fähigkeiten", "Klarer Wechsel"),
            q("Woran würdest du merken, dass ein Ziel nicht mehr zu dir passt?", "Motivation fehlt dauerhaft", "Preis für Privatleben ist zu hoch", "Werte passen nicht mehr", "Ziel kam nur von außen"),
            q("Was sollte ein Partner über deine beruflichen Ziele kennen?", "Wie wichtig sie mir sind", "Welche Opfer ich akzeptiere", "Welche Grenzen ich habe", "Wie viel Unterstützung ich brauche"),
            q("Was wäre langfristig wertvoller?", "Ein beeindruckender Titel", "Ein Arbeitsleben, das zu mir passt", "Maximales Einkommen", "Möglichst viel Sicherheit")
        ),
        "h500_220_job_und_beziehung_offene_runde" to listOf(
            open("Welche Arbeitsbelastung dürfte dauerhaft niemals wichtiger werden als eure Beziehung?"),
            open("Wann fühlst du dich vom Partner bei beruflichem Stress wirklich unterstützt – und wann eher zusätzlich unter Druck?"),
            open("Wie viel sollte ein Partner bei einem Jobwechsel mitentscheiden dürfen, wenn Wohnort oder gemeinsame Zeit betroffen sind?"),
            open("Welche berufliche Information würdest du in einer festen Beziehung früh teilen, auch wenn sie unangenehm ist?"),
            open("Wie möchtet ihr damit umgehen, wenn einer beruflich deutlich erfolgreicher oder finanziell stärker wird?"),
            open("Was wäre für dich ein gutes Zeichen dafür, dass Job und Beziehung sich gegenseitig stärken statt auffressen?")
        ),
        "h500_221_nebenjob_entweder_oder" to listOf(
            q("Warum wäre ein Nebenjob für dich eher sinnvoll?", "Zusätzliches Einkommen", "Etwas Eigenes ausprobieren"),
            q("Wann würdest du einen Nebenjob eher machen?", "Feste wenige Stunden", "Phasenweise mehr"),
            q("Was wäre dir wichtiger?", "Klare Trennung vom Hauptjob", "Nebenjob ergänzt meine Interessen"),
            q("Wenn Freizeit knapp wird?", "Nebenjob reduzieren", "Andere Freizeit vorübergehend reduzieren"),
            q("Was wäre als Paar fairer?", "Vorher feste Zeitgrenzen absprechen", "Nur bei Problemen neu verhandeln"),
            q("Wofür würdest du Extra-Einnahmen eher nutzen?", "Gemeinsames Ziel", "Persönliches Ziel")
        ),
        "h500_224_arbeitsweg_ranking" to listOf(
            q("Was zählt bei deinem Arbeitsweg am meisten? Ordne.", "Kurze Dauer", "Zuverlässigkeit", "Wenig Stress", "Geringe Kosten"),
            q("Welche Art Arbeitsweg wäre dir am liebsten? Ordne.", "Zu Fuß oder Rad", "ÖPNV", "Auto", "Homeoffice statt Weg"),
            q("Wofür würdest du einen längeren Arbeitsweg akzeptieren? Ordne.", "Besserer Job", "Mehr Gehalt", "Weniger Arbeitstage vor Ort", "Schönerer Wohnort"),
            q("Was nervt dich auf dem Arbeitsweg am meisten? Ordne.", "Stau", "Verspätungen", "Umsteigen", "Überfüllung"),
            q("Was wäre als Paar bei zwei langen Arbeitswegen am wichtigsten? Ordne.", "Wohnort fair wählen", "Homeoffice nutzen", "Gemeinsame Zeit schützen", "Kosten im Blick behalten"),
            q("Was wäre langfristig am wertvollsten? Ordne.", "Zeit sparen", "Flexibler sein", "Gesünder unterwegs sein", "Weniger abhängig vom Verkehr")
        ),
        "h500_225_ruhestand_prognose" to listOf(
            q("Wann würde dein Partner am liebsten mit klassischer Vollzeitarbeit aufhören?", "Früher aufhören, wenn es finanziell passt", "Bis zum normalen Rentenalter", "Schrittweise Stunden reduzieren", "So lange arbeiten, wie es Freude macht"),
            q("Wie würde dein Partner später am liebsten leben?", "Viel reisen", "Ruhiger Alltag", "Viele eigene Projekte", "Viel Zeit mit Familie und Freunden"),
            q("Was wäre deinem Partner für den Ruhestand vermutlich am wichtigsten?", "Finanzielle Sicherheit", "Gesundheit", "Frei verfügbare Zeit", "Ein klarer Alltag"),
            q("Wofür würde dein Partner im Ruhestand eher Geld ausgeben?", "Erlebnisse", "Komfort zuhause", "Hobbys", "Unterstützung für Familie"),
            q("Was würde dein Partner wahrscheinlich vermeiden wollen?", "Zu viel Leerlauf", "Finanzielle Abhängigkeit", "Zu wenig soziale Kontakte", "Zu viele Verpflichtungen"),
            q("Welche Vorbereitung würde deinem Partner vermutlich am meisten Sicherheit geben?", "Finanzieller Überblick", "Wohnsituation klären", "Gesundheit pflegen", "Ideen für die freie Zeit")
        ),
        "h500_226_kuendigung_szenario" to listOf(
            q("Einer von euch möchte kündigen, hat aber noch nichts Neues. Was klärt ihr zuerst?", "Finanzpuffer", "Gründe für die Kündigung", "Zeitrahmen", "Nächste Optionen"),
            q("Eine Kündigung kommt unerwartet vom Arbeitgeber. Was sollte zuhause zuerst passieren?", "Druck rausnehmen", "Finanzen grob prüfen", "Nächste Schritte sortieren", "Erst einmal emotional auffangen"),
            q("Der Job macht krank, aber finanziell wäre Kündigen schwierig. Was ist sinnvoll?", "Gesundheit ernst nehmen", "Übergangsplan bauen", "Professionelle Beratung nutzen", "Alternative intern prüfen"),
            q("Einer will sofort kündigen, der andere hat große Angst vor Unsicherheit. Wie entscheidet ihr?", "Risiken konkret machen", "Mindestpuffer festlegen", "Frist für Jobsuche setzen", "Gemeinsame Grenze definieren"),
            q("Nach der Kündigung dauert die Jobsuche länger als erwartet. Was passt ihr zuerst an?", "Budget", "Suchstrategie", "Tagesstruktur", "Erwartungen aneinander"),
            q("Was wäre in einer Kündigungsphase als Paar am wichtigsten?", "Keine Schuldzuweisungen", "Transparente Finanzen", "Regelmäßige Gespräche", "Eigenständigkeit des Betroffenen respektieren")
        ),
        "h500_227_kollegen_geheime_wahl" to listOf(
            q("Was wünschst du dir heimlich von guten Kollegen am meisten?", "Verlässlichkeit", "Humor", "Direktes Feedback", "Ruhe und Professionalität"),
            q("Welche Kollegensituation stresst dich am ehesten?", "Büroklatsch", "Unklare Zuständigkeiten", "Ständige Unterbrechungen", "Passive Konflikte"),
            q("Mit welchem Kollegen-Typ arbeitest du am liebsten?", "Sehr strukturiert", "Kreativ und spontan", "Ruhig und zuverlässig", "Kommunikativ und verbindend"),
            q("Was würdest du bei Kollegen am schwersten verzeihen?", "Vertrauensbruch", "Arbeit abschieben", "Respektlosigkeit", "Erfolge anderer kleinreden"),
            q("Wie viel Privates möchtest du am Arbeitsplatz teilen?", "Fast nichts", "Ein bisschen Alltag", "Mit vertrauten Kollegen einiges", "Sehr offen, wenn es passt"),
            q("Was wäre dir bei einem Teamwechsel am wichtigsten?", "Guter Umgang", "Klare Führung", "Faire Arbeitsteilung", "Gemeinsames Ziel")
        ),
        "h500_230_beruflicher_erfolg_offene_runde" to listOf(
            open("Was bedeutet beruflicher Erfolg für dich persönlich, wenn Titel und Außenwirkung keine Rolle spielen?"),
            open("Welche Form von Erfolg wäre dir zu teuer, wenn du dafür dauerhaft Zeit, Gesundheit oder Beziehung opfern müsstest?"),
            open("Woran würdest du merken, dass du beruflich genug erreicht hast und nicht ständig das nächste Ziel brauchst?"),
            open("Welche berufliche Leistung macht dich bisher am meisten stolz – und warum genau diese?"),
            open("Wie wichtig ist dir, dass dein Partner deinen beruflichen Erfolg versteht oder anerkennt?"),
            open("Welche Art Arbeitsleben würdest du in zehn Jahren rückblickend als wirklich gelungen bezeichnen?")
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
