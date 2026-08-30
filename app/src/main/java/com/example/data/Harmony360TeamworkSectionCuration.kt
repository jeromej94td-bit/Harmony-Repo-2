package com.example.data

/** Explicit Stage 05.5 curation for Harmony-360 Section 20 — Teamwork & Challenge. */
object Harmony360TeamworkSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private const val SECTION = "h360_section_20_teamwork_challenge"
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun open(text: String): GenQuestion = GenQuestion(q = text)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_411_zusammenhalt_entweder_oder" to CurationDecision.REWRITE,
        "h500_412_krisenmodus_wer_eher" to CurationDecision.REWRITE,
        "h500_413_teamgeist_skala" to CurationDecision.REWRITE,
        "h500_414_rollenverteilung_ranking" to CurationDecision.REWRITE,
        "h500_415_blindes_vertrauen_prognose" to CurationDecision.ARCHIVE,
        "h500_416_escape_room_szenario" to CurationDecision.REWRITE,
        "h500_417_geheimes_ziel_geheime_wahl" to CurationDecision.REWRITE,
        "h500_418_groesster_triumph_memory" to CurationDecision.REWRITE,
        "h500_419_paarchallenge_prioritaet" to CurationDecision.REWRITE,
        "h500_420_unschlagbar_offene_runde" to CurationDecision.ARCHIVE,
        "h500_421_wettbewerb_entweder_oder" to CurationDecision.REWRITE,
        "h500_422_mutprobe_wer_eher" to CurationDecision.MERGE,
        "h500_423_durchhaltevermoegen_skala" to CurationDecision.ARCHIVE,
        "h500_424_staerken_ranking" to CurationDecision.ARCHIVE,
        "h500_425_gemeinsamer_sieg_prognose" to CurationDecision.REWRITE,
        "h500_426_notfallplan_szenario" to CurationDecision.REWRITE,
        "h500_427_mutiger_traum_geheime_wahl" to CurationDecision.ARCHIVE,
        "h500_430_team_zukunft_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_411_zusammenhalt_entweder_oder" to listOf(
            q("Wenn dein Partner eine Entscheidung trifft, die du kritisch siehst: Was zeigt mehr Zusammenhalt?", "Klar ehrlich sein", "Erst Rückhalt geben und später reden"),
            q("Was stärkt euer Wir-Gefühl eher?", "Gemeinsame Ziele verfolgen", "Im Alltag zuverlässig füreinander da sein"),
            q("Wenn Druck von außen kommt: Was ist wichtiger?", "Als Paar geschlossen auftreten", "Jeder darf seine eigene Sicht behalten"),
            q("Was fühlt sich für dich mehr nach Loyalität an?", "Privat ehrlich widersprechen", "Vor anderen nicht gegeneinander arbeiten"),
            q("Nach einem Streit: Was bringt euch eher wieder ins Team?", "Eine konkrete Lösung finden", "Erst emotionale Nähe herstellen"),
            q("Was sollte Zusammenhalt niemals bedeuten?", "Alles gutheißen müssen", "Eigene Grenzen aufgeben")
        ),
        "h500_412_krisenmodus_wer_eher" to listOf(
            whoQ("Wer behält bei unerwartetem Stress eher zuerst den Überblick?"),
            whoQ("Wer beginnt eher sofort mit einer praktischen Lösung?"),
            whoQ("Wer merkt eher, dass der andere gerade erst Beruhigung statt Lösung braucht?"),
            whoQ("Wer kann eher Hilfe von außen organisieren, wenn ihr allein nicht weiterkommt?"),
            whoQ("Wer schafft es eher, Schuldfragen bis nach der akuten Situation zu verschieben?"),
            whoQ("Wer erinnert eher daran, nach einer Krise auch wieder aufeinander zu achten?")
        ),
        "h500_413_teamgeist_skala" to listOf(
            q("Wie gut könnt ihr bei einem anstrengenden gemeinsamen Projekt Aufgaben verteilen?", "1 – kaum", "2", "3", "4", "5 – sehr gut"),
            q("Wie sicher fühlst du dich darauf, dass dein Partner Zusagen im Team einhält?", "1 – unsicher", "2", "3", "4", "5 – sehr sicher"),
            q("Wie leicht kannst du deinem Partner bei einer gemeinsamen Aufgabe Verantwortung überlassen?", "1 – sehr schwer", "2", "3", "4", "5 – sehr leicht"),
            q("Wie gut könnt ihr unterschiedliche Arbeitsweisen nutzen, statt euch daran zu reiben?", "1 – kaum", "2", "3", "4", "5 – sehr gut"),
            q("Wie fair fühlt sich eure Aufgabenteilung in stressigen Phasen an?", "1 – unfair", "2", "3", "4", "5 – sehr fair"),
            q("Wie gut ermutigt ihr euch, eine selbstgewählte Komfortzone zu verlassen, ohne Druck aufzubauen?", "1 – gar nicht", "2", "3", "4", "5 – sehr gut")
        ),
        "h500_414_rollenverteilung_ranking" to listOf(
            q("Welche Rolle ist bei gemeinsamen Projekten für euch am wichtigsten? Ordne.", "Ideen entwickeln", "Plan strukturieren", "Umsetzung vorantreiben", "Ergebnis prüfen"),
            q("Was sollte bei guter Rollenverteilung zuerst zählen? Ordne.", "Stärken nutzen", "Arbeitslast fair halten", "Klare Verantwortung", "Flexibel tauschen können"),
            q("Welche Fähigkeit hilft euch bei Zeitdruck am meisten? Ordne.", "Priorisieren", "Entscheiden", "Improvisieren", "Ruhig kommunizieren"),
            q("Was verhindert am ehesten Streit über Zuständigkeiten? Ordne.", "Vorher absprechen", "Zwischendurch nachjustieren", "Hilfe aktiv anbieten", "Probleme früh ansprechen"),
            q("Was sollte passieren, wenn einer dauerhaft mehr übernimmt? Ordne.", "Belastung sichtbar machen", "Aufgaben neu verteilen", "Standards vereinfachen", "Nach Unterstützung suchen"),
            q("Was macht Rollen als Paar langfristig gesund? Ordne.", "Keine Rolle ist festgeschrieben", "Beide werden gesehen", "Verantwortung bleibt gemeinsam", "Jeder darf Nein sagen")
        ),
        "h500_416_escape_room_szenario" to listOf(
            q("Ihr betretet einen Escape Room und der Raum ist voller Hinweise. Wie startet ihr?", "Raum systematisch aufteilen", "Gemeinsam zuerst alles ansehen", "Gefundene Hinweise laut sammeln", "Direkt das auffälligste Rätsel lösen"),
            q("Einer entdeckt ein Rätsel, das genau seiner Stärke entspricht. Was macht ihr?", "Er übernimmt und erklärt", "Beide knobeln trotzdem zusammen", "Der andere sucht parallel weiter", "Nach kurzer Zeit Rollen tauschen"),
            q("Ihr hängt seit mehreren Minuten an demselben Rätsel fest. Was ist der beste nächste Schritt?", "Perspektive wechseln", "Hinweise neu sortieren", "Partner übernimmt", "Einen offiziellen Tipp nutzen"),
            q("Die Zeit wird knapp und ihr habt zwei offene Rätsel. Wie entscheidet ihr?", "Einfacheres zuerst", "Wichtigere Verbindung verfolgen", "Aufteilen", "Kurz gemeinsam priorisieren"),
            q("Einer ist sicher, den richtigen Code zu haben, der andere zweifelt. Was tut ihr?", "Einmal logisch prüfen", "Code testen, wenn kein Nachteil entsteht", "Alternative kurz vergleichen", "Der sicherere Hinweis entscheidet"),
            q("Ein Fehler kostet euch mehrere Minuten. Wie reagiert ihr?", "Ohne Vorwurf weitermachen", "Kurz benennen, was gelernt wurde", "Aufgaben neu verteilen", "Mit Humor Fokus zurückholen"),
            q("Ihr löst das letzte Rätsel in letzter Minute. Was war wahrscheinlich eure größte Stärke?", "Gute Kommunikation", "Arbeitsteilung", "Hartnäckigkeit", "Unterschiedliche Denkweisen"),
            q("Nach dem Spiel wollt ihr besser werden. Was besprecht ihr zuerst?", "Wo ihr Zeit verloren habt", "Welche Rollen gut funktioniert haben", "Wann ihr zu wenig zugehört habt", "Was besonders Spaß gemacht hat")
        ),
        "h500_417_geheimes_ziel_geheime_wahl" to listOf(
            q("Welches gemeinsames Ziel würdest du heimlich am liebsten als Nächstes anstoßen?", "Eine besondere Reise", "Ein gemeinsames Projekt", "Eine neue Fähigkeit lernen", "Unseren Alltag spürbar verbessern"),
            q("Welche gemeinsame Fähigkeit würdest du mit deinem Partner gern entwickeln?", "Besser planen", "Gelassener improvisieren", "Konsequenter Ziele verfolgen", "Noch klarer kommunizieren"),
            q("Wo wünschst du dir insgeheim mehr echtes Teamwork?", "Bei Alltagsorganisation", "Bei großen Entscheidungen", "Bei persönlichen Zielen", "Bei gemeinsamer Freizeit"),
            q("Welches Projekt würde euch als Team wahrscheinlich besonders wachsen lassen?", "Etwas gemeinsam bauen oder gestalten", "Eine längere Reise planen", "Ein neues Hobby meistern", "Für ein gemeinsames Ziel sparen"),
            q("Was würdest du gern einmal gemeinsam schaffen, nur um später stolz darauf zurückzuschauen?", "Eine schwierige Challenge", "Ein Herzensprojekt", "Eine große Veränderung", "Eine gemeinsame Tradition aufbauen"),
            q("Was sollte bei einem gemeinsamen Ziel immer erhalten bleiben?", "Freiwilligkeit", "Eigene Wünsche", "Humor", "Faire Verantwortung")
        ),
        "h500_418_groesster_triumph_memory" to listOf(
            open("Welche schwierige Situation habt ihr gemeinsam besser gemeistert, als du vorher erwartet hattest?"),
            open("Auf welches gemeinsame Projekt oder Ziel bist du als Paar besonders stolz?"),
            open("Wann hat dich dein Partner durch seinen Einsatz für euch besonders beeindruckt?"),
            open("Welche Panne habt ihr zusammen so gut gelöst, dass sie heute fast wie ein Erfolg wirkt?"),
            open("Wann habt ihr euch nach einer gemeinsamen Anstrengung richtig als Team gefühlt?"),
            open("Welchen nächsten gemeinsamen Erfolg würdest du besonders gern mit deinem Partner feiern?")
        ),
        "h500_419_paarchallenge_prioritaet" to listOf(
            q("Welche Paar-Challenge hätte für euch gerade den größten echten Nutzen?", "Mehr ungestörte Zeit", "Ein gemeinsames Projekt", "Mehr Bewegung zusammen", "Etwas Neues lernen"),
            q("Was macht eine gemeinsame Challenge für dich fair?", "Beide wollen sie wirklich", "Ziel passt zu beiden", "Pausen sind erlaubt", "Niemand kontrolliert den anderen"),
            q("Was sollte passieren, wenn einer an einem Tag keine Energie dafür hat?", "Pausieren ohne schlechtes Gewissen", "Challenge kleiner machen", "Nur der andere macht weiter", "Gemeinsam neu entscheiden"),
            q("Was motiviert dich bei einer gemeinsamen Challenge am meisten?", "Fortschritt sehen", "Gemeinsam lachen", "Ein konkretes Ziel", "Der feste Termin miteinander"),
            q("Wann sollte eine Challenge beendet oder verändert werden?", "Wenn sie nur noch Druck macht", "Wenn das Ziel nicht mehr passt", "Wenn einer klar aussteigen will", "Wenn eine bessere Idee entsteht"),
            q("Was wäre nach einer gelungenen Challenge die beste Belohnung?", "Gemeinsam etwas unternehmen", "Den Erfolg bewusst feiern", "Direkt etwas Neues ausprobieren", "Einfach stolz darauf sein")
        ),
        "h500_421_wettbewerb_entweder_oder" to listOf(
            q("Bei Spielen gegeneinander: Was macht dir mehr Spaß?", "Locker spielen", "Mit gesundem Ehrgeiz spielen"),
            q("Wie spielt ihr lieber?", "Im selben Team", "Direkt gegeneinander"),
            q("Nach einer knappen Niederlage?", "Sofort Revanche", "Ergebnis abhaken und weiter"),
            q("Was ist beim Wettbewerb wichtiger?", "Fair bleiben", "Alles geben"),
            q("Wenn einer deutlich besser ist?", "Vorsprung ohne Schonung ausspielen", "Spiel so wählen, dass es spannend bleibt"),
            q("Was darf ein Wettbewerb zwischen euch niemals kaputtmachen?", "Gute Stimmung", "Respekt füreinander")
        ),
        "h500_425_gemeinsamer_sieg_prognose" to listOf(
            q("Wenn ihr bei einer Fernseh-Quizshow mitmacht: Wie weit kommt ihr vermutlich?", "Bis ganz nach vorne", "Sehr weit", "Solides Mittelfeld", "Hauptsache gemeinsam Spaß"),
            q("Welche Art Aufgabe würde dein Partner euch als stärkste Disziplin zutrauen?", "Wissen", "Logik", "Geschicklichkeit", "Menschenkenntnis"),
            q("Was wäre laut deinem Partner eure größte Stärke in einer TV-Challenge?", "Ruhig bleiben", "Schnell absprechen", "Unterschiedliche Fähigkeiten", "Nicht aufgeben"),
            q("Woran würdet ihr laut deinem Partner am ehesten Punkte verlieren?", "Zu viel diskutieren", "Zu schnell entscheiden", "Ehrgeiz", "Konzentration unter Zeitdruck"),
            q("Wie würde dein Partner einen gemeinsamen Sieg am liebsten feiern?", "Richtig ausgiebig", "Mit einem besonderen Essen", "Nur zu zweit genießen", "Direkt die nächste Challenge suchen"),
            q("Was würde dein Partner nach einer Niederlage wahrscheinlich zuerst sagen?", "Wir haben alles gegeben", "Das war trotzdem lustig", "Nächstes Mal besser", "Lass uns kurz analysieren, warum")
        ),
        "h500_426_notfallplan_szenario" to listOf(
            q("Kurz vor einer Reise fällt euer wichtigstes Verkehrsmittel aus. Was macht ihr zuerst?", "Alternativen prüfen", "Kostenlimit festlegen", "Zeitplan neu bewerten", "Ruhe bewahren und gemeinsam priorisieren"),
            q("Unterwegs habt ihr eine Panne und müsst länger warten. Wie teilt ihr euch auf?", "Einer organisiert Hilfe, einer sichert den Ort", "Alles gemeinsam erledigen", "Einer telefoniert, einer kümmert sich um Gepäck", "Je nach Stärke spontan verteilen"),
            q("Euer Koffer kommt am Ziel nicht an. Was ist der beste erste Schritt?", "Verlust direkt melden", "Nur das Nötigste besorgen", "Unterkunft informieren", "Urlaubsplan erst einmal vereinfachen"),
            q("Ein gebuchtes Event wird kurzfristig abgesagt. Wie rettet ihr den Tag?", "Spontane Alternative suchen", "Bewusst einen ruhigen Tag machen", "Lokale Empfehlungen prüfen", "Jeder nennt einen Ersatzwunsch"),
            q("Zuhause fällt für viele Stunden der Strom aus. Was priorisiert ihr?", "Wärme und Licht", "Informationen und Akkus", "Lebensmittel schützen", "Eine ruhige gemeinsame Beschäftigung"),
            q("Ein unerwartetes Problem wird teuer. Wie entscheidet ihr unter Druck?", "Nur notwendige Kosten freigeben", "Optionen kurz vergleichen", "Einer recherchiert, einer rechnet", "Größere Entscheidung vertagen, wenn möglich"),
            q("Einer gerät in einer chaotischen Lage in Stress. Was sollte der andere zuerst tun?", "Eine konkrete Aufgabe übernehmen", "Kurz beruhigen", "Informationen sortieren", "Fragen, was gerade gebraucht wird"),
            q("Nach dem Notfall ist alles gelöst. Was macht euch beim nächsten Mal besser?", "Kurz auswerten", "Kontakte und Dokumente ordnen", "Kleine Notfallreserve vorbereiten", "Absprechen, wer welche Rolle übernimmt")
        ),
        "h500_430_team_zukunft_offene_runde" to listOf(
            open("Welche Art Herausforderung möchtet ihr in den nächsten Jahren unbedingt einmal gemeinsam meistern?"),
            open("Welche Fähigkeit müsst ihr als Team noch entwickeln, damit große gemeinsame Pläne leichter werden?"),
            open("Wo sollen persönliche Ziele auch in einer starken Partnerschaft bewusst getrennt bleiben dürfen?"),
            open("Welche gemeinsame Entscheidung wird in Zukunft wahrscheinlich besonders gutes Teamwork von euch verlangen?"),
            open("Wie könnt ihr verhindern, dass bei großen Zielen immer dieselbe Person Planung oder Verantwortung trägt?"),
            open("Woran möchtest du in einigen Jahren erkennen, dass ihr nicht nur ein Paar, sondern auch ein gutes Team geblieben seid?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        if (SECTION !in pack.tags) return@mapNotNull pack

        when (decisions[pack.id]) {
            CurationDecision.ARCHIVE, CurationDecision.MERGE -> null
            CurationDecision.REWRITE -> pack.copy(questions = overrides.getValue(pack.id))
            CurationDecision.KEEP -> pack
            null -> null
        }
    }
}
