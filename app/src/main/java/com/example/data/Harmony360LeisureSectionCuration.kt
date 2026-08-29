package com.example.data

/** Explicit Stage 05.2 curation for Harmony-360 Section 07 — Freizeit & Hobbys. */
object Harmony360LeisureSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_151_serien_und_filme_entweder_oder" to CurationDecision.REWRITE,
        "h500_152_buecher_wer_eher" to CurationDecision.REWRITE,
        "h500_153_gaming_skala" to CurationDecision.REWRITE,
        "h500_154_sport_ranking" to CurationDecision.REWRITE,
        "h500_155_konzerte_prognose" to CurationDecision.REWRITE,
        "h500_156_museen_szenario" to CurationDecision.REWRITE,
        "h500_157_festivals_geheime_wahl" to CurationDecision.REWRITE,
        "h500_158_flohmaerkte_memory" to CurationDecision.REWRITE,
        "h500_159_wandern_prioritaet" to CurationDecision.REWRITE,
        "h500_160_wellness_offene_runde" to CurationDecision.REWRITE,
        "h500_161_fotografie_entweder_oder" to CurationDecision.REWRITE,
        "h500_162_gaertnern_wer_eher" to CurationDecision.REWRITE,
        "h500_163_diy_projekte_skala" to CurationDecision.REWRITE,
        "h500_164_brettspiele_ranking" to CurationDecision.REWRITE,
        "h500_165_sprachen_lernen_prognose" to CurationDecision.REWRITE,
        "h500_166_instrumente_szenario" to CurationDecision.REWRITE,
        "h500_167_backen_geheime_wahl" to CurationDecision.REWRITE,
        "h500_170_ehrenamt_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_151_serien_und_filme_entweder_oder" to listOf(
            q("Was passt heute eher zu dir?", "Eine Folge Serie", "Ein kompletter Film"),
            q("Was zieht dich eher rein?", "Spannung", "Humor"),
            q("Wie schaust du lieber?", "Mehrere Folgen am Stück", "Eine Folge und Schluss"),
            q("Was gewinnt eher?", "Etwas Neues anfangen", "Einen Favoriten noch einmal sehen"),
            q("Was ist dir lieber?", "Originalton mit Untertiteln", "Synchronisiert schauen"),
            q("Wenn ihr verschieden wählen würdet?", "Abwechselnd entscheiden", "Etwas suchen, das beide mögen")
        ),
        "h500_152_buecher_wer_eher" to listOf(
            whoQ("Wer nimmt eher ein Buch mit, obwohl die Tasche eigentlich schon voll ist?"),
            whoQ("Wer liest eher noch ein Kapitel, obwohl es längst zu spät ist?"),
            whoQ("Wer würde eher ein Buch nur wegen eines starken Covers in die Hand nehmen?"),
            whoQ("Wer empfiehlt dem anderen eher ein Buch und fragt danach ständig, wie weit er ist?"),
            whoQ("Wer kann eher stundenlang in einer Buchhandlung verschwinden?"),
            whoQ("Wer würde eher zuerst das Buch lesen, bevor ihr die Verfilmung schaut?")
        ),
        "h500_153_gaming_skala" to listOf(
            scaleQ("Wie gern verbringst du einen freien Abend mit Gaming?"),
            scaleQ("Wie sehr reizt dich ein Koop-Spiel, das ihr nur gemeinsam lösen könnt?"),
            scaleQ("Wie ehrgeizig wirst du, wenn ihr gegeneinander spielt?"),
            scaleQ("Wie gern probierst du ein Spiel aus einem Genre, das sonst nicht deins ist?"),
            scaleQ("Wie wichtig ist dir eine starke Geschichte im Vergleich zu reiner Spielmechanik?"),
            scaleQ("Wie leicht kannst du ein Spiel nach einer Niederlage einfach lachend beenden?")
        ),
        "h500_154_sport_ranking" to listOf(
            q("Was motiviert dich bei Sport am stärksten? Ordne.", "Spaß", "Fortschritt", "Gesundheit", "Gemeinsame Zeit"),
            q("Welche Sportart würde dich am ehesten für einen gemeinsamen Start reizen? Ordne.", "Schwimmen", "Radfahren", "Fitness", "Tanzen"),
            q("Was ist dir bei einer Sport-Routine am wichtigsten? Ordne.", "Regelmäßigkeit", "Abwechslung", "Messbare Ziele", "Kein Leistungsdruck"),
            q("Was bremst deine Motivation am stärksten? Ordne.", "Zeitmangel", "Müdigkeit", "Monotonie", "Zu hoher Anspruch"),
            q("Welche Art Bewegung fühlt sich für dich am besten an? Ordne.", "Ausdauer", "Kraft", "Beweglichkeit", "Geschicklichkeit"),
            q("Was macht gemeinsamen Sport langfristig gut? Ordne.", "Ähnliches Tempo", "Gegenseitige Motivation", "Humor", "Eigene Ziele behalten")
        ),
        "h500_155_konzerte_prognose" to listOf(
            q("Welchen Platz würde dein Partner bei einem Konzert vermutlich wählen?", "Stehplatz mitten drin", "Sitzplatz mit guter Sicht", "Weiter hinten mit mehr Raum", "Je nach Künstler unterschiedlich"),
            q("Was wäre deinem Partner wahrscheinlich wichtiger?", "Lieblingssongs live hören", "Stimmung im Publikum", "Guter Sound", "Der gemeinsame Abend"),
            q("Wie früh würde dein Partner vermutlich dort sein wollen?", "Sehr früh", "Rechtzeitig zum Einlass", "Kurz vor Beginn", "Vorband reicht als Puffer"),
            q("Welche Konzertgröße passt vermutlich besser zu deinem Partner?", "Kleiner Club", "Mittlere Halle", "Große Arena", "Open-Air"),
            q("Was würde deinen Partner am ehesten nerven?", "Zu viele Handys", "Gedränge", "Schlechter Sound", "Lange Wartezeiten"),
            q("Was würde dein Partner nach dem letzten Song am liebsten machen?", "Noch bleiben und Stimmung genießen", "Direkt etwas trinken gehen", "Über das Konzert reden", "Zügig nach Hause")
        ),
        "h500_156_museen_szenario" to listOf(
            q("Ihr habt nur zwei Stunden in einem riesigen Museum. Wie geht ihr vor?", "Vorher Highlights wählen", "Einfach treiben lassen", "Je einen Wunsch auswählen", "Nur eine Abteilung richtig anschauen"),
            q("Einer liest jede Infotafel, der andere möchte schneller weiter. Was macht ihr?", "Tempo angleichen", "Kurz getrennt schauen", "Nur besondere Texte lesen", "Abwechselnd Tempo bestimmen"),
            q("Eine Sonderausstellung kostet deutlich extra. Wie entscheidet ihr?", "Wenn beide interessiert sind", "Einer entscheidet diesmal", "Bewertungen kurz prüfen", "Beim normalen Eintritt bleiben"),
            q("Ihr merkt nach 30 Minuten, dass einer gar keinen Zugang zur Ausstellung findet. Was nun?", "Andere Abteilung suchen", "Kurze Pause machen", "Früher gehen", "Der andere zeigt seine Lieblingsstücke"),
            q("Im Museum darf nicht fotografiert werden. Wie fühlt sich das für euch an?", "Angenehm konzentriert", "Ein bisschen schade", "Völlig egal", "Erinnerung bewusst ohne Handy genießen"),
            q("Ihr könnt nur noch einen Raum sehen. Wer entscheidet?", "Gemeinsam den spannendsten wählen", "Der bisher weniger Begeisterte", "Der größere Kunstfan", "Münzwurf")
        ),
        "h500_157_festivals_geheime_wahl" to listOf(
            q("Welches Festival-Setup würdest du heimlich am liebsten wählen?", "Tagesfestival ohne Camping", "Ganzes Wochenende mit Camping", "Kleines Boutique-Festival", "Großes Festival mit vielen Bühnen"),
            q("Was ist dir heimlich wichtiger als du vielleicht zugibst?", "Gutes Line-up", "Saubere Unterkunft", "Gutes Essen", "Entspannte Leute"),
            q("Wie würdest du einen Festival-Tag am liebsten planen?", "Feste Must-sees", "Nur ein paar Highlights", "Komplett spontan", "Partner wählt die Hälfte"),
            q("Wofür würdest du am ehesten extra bezahlen?", "Bessere Unterkunft", "Fast Lane", "Sehr gutes Essen", "Bessere Anreise"),
            q("Was wäre dein heimlicher Festival-Luxus?", "Dusche ohne Schlange", "Richtiges Bett", "Ruhiger Rückzugsort", "Frühstück statt Dosenessen"),
            q("Welche spontane Idee würdest du dort am ehesten mitmachen?", "Unbekannte Band anschauen", "Bis Sonnenaufgang bleiben", "Mit neuen Leuten feiern", "Eine völlig andere Bühne wählen")
        ),
        "h500_158_flohmaerkte_memory" to listOf(
            GenQuestion("Welcher Flohmarkt-Fund aus deinem Leben ist dir bis heute im Kopf geblieben?"),
            GenQuestion("Hast du schon einmal etwas auf einem Flohmarkt gekauft, das sich später als richtig guter Glücksgriff herausgestellt hat?"),
            GenQuestion("Welche alte Sache aus deiner Kindheit würdest du auf einem Flohmarkt sofort wieder kaufen?"),
            GenQuestion("Wann hast du zuletzt auf einem Flohmarkt etwas völlig Unerwartetes entdeckt?"),
            GenQuestion("Gab es einen Fund, bei dem du heute noch bereust, ihn stehen gelassen zu haben?"),
            GenQuestion("Welche Art gemeinsamer Flohmarkt-Fund würde bei euch wahrscheinlich jahrelang eine Geschichte bleiben?")
        ),
        "h500_159_wandern_prioritaet" to listOf(
            q("Was muss bei einer guten Wanderung zuerst stimmen?", "Aussicht", "Strecke", "Wetter", "Passendes Tempo"),
            q("Was ist wichtiger, wenn eure Kondition unterschiedlich ist?", "Gemeinsames Tempo", "Kürzere Route", "Mehr Pausen", "Getrennte Teilstrecken vermeiden"),
            q("Wofür würdest du auf einer Tour am ehesten einen Umweg machen?", "Aussichtspunkt", "Berghütte", "See oder Wasserfall", "Ruhiger Weg ohne Menschen"),
            q("Was darf im Rucksack am wenigsten fehlen?", "Genug Wasser", "Gute Snacks", "Regenzeug", "Erste-Hilfe-Set"),
            q("Was entscheidet bei einer spontanen Tour am stärksten?", "Wetter", "Verfügbare Zeit", "Schwierigkeit", "Anfahrt"),
            q("Was macht eine Wanderung für dich besonders?", "Ziel erreichen", "Gespräche unterwegs", "Natur bewusst wahrnehmen", "Danach gemeinsam einkehren")
        ),
        "h500_160_wellness_offene_runde" to listOf(
            GenQuestion("Was bedeutet echte Erholung für dich: völlige Ruhe oder bewusst etwas Angenehmes tun?"),
            GenQuestion("Welche Wellness-Sache entspannt dich wirklich – und welche kannst du problemlos weglassen?"),
            GenQuestion("Wie viel gemeinsame Zeit möchtest du bei einem Wellness-Tag und wie viel Zeit lieber für dich?"),
            GenQuestion("Was müsste passieren, damit du bei Wellness tatsächlich das Handy vergisst?"),
            GenQuestion("Welche kleine Form von Erholung funktioniert bei dir auch ohne Hotel oder Spa?"),
            GenQuestion("Woran würdest du am Abend merken, dass ein gemeinsamer Erholungstag wirklich gut war?")
        ),
        "h500_161_fotografie_entweder_oder" to listOf(
            q("Was fotografierst du eher gern?", "Menschen fotografieren", "Landschaften fotografieren"),
            q("Was ist dir wichtiger?", "Perfekter Moment", "Perfekte Bildgestaltung"),
            q("Was passt eher zu dir?", "Viele spontane Fotos", "Wenige bewusst geplante Fotos"),
            q("Was reizt dich mehr?", "Handy-Fotografie", "Kamera mit mehr Kontrolle"),
            q("Was würdest du eher behalten?", "Ungestelltes Erinnerungsfoto", "Technisch starkes Bild"),
            q("Nach einer Reise: Was machst du eher?", "Fotos direkt aussortieren", "Erst viel später wieder anschauen")
        ),
        "h500_162_gaertnern_wer_eher" to listOf(
            whoQ("Wer würde eher neue Pflanzen kaufen, obwohl eigentlich kein Platz mehr ist?"),
            whoQ("Wer denkt eher daran, regelmäßig zu gießen?"),
            whoQ("Wer würde eher Gemüse oder Kräuter selbst ziehen wollen?"),
            whoQ("Wer liest eher nach, warum eine Pflanze plötzlich traurig aussieht?"),
            whoQ("Wer hätte eher Geduld, monatelang auf die erste Blüte oder Ernte zu warten?"),
            whoQ("Wer würde eher aus einem kleinen Balkon ein halbes Gewächshaus machen?")
        ),
        "h500_163_diy_projekte_skala" to listOf(
            scaleQ("Wie gern baust, reparierst oder gestaltest du Dinge selbst?"),
            scaleQ("Wie viel Geduld hast du, wenn ein DIY-Projekt beim ersten Versuch nicht funktioniert?"),
            scaleQ("Wie gern lernst du für ein Projekt ein Werkzeug oder eine Technik neu?"),
            scaleQ("Wie wichtig ist dir ein perfektes Ergebnis im Vergleich zum Spaß beim Machen?"),
            scaleQ("Wie gut könntest du bei einem gemeinsamen Projekt akzeptieren, dass dein Partner anders arbeitet als du?"),
            scaleQ("Wie groß wäre deine Lust auf ein gemeinsames Projekt, das mehrere Wochen dauert?")
        ),
        "h500_164_brettspiele_ranking" to listOf(
            q("Welche Art Brettspiel macht dir am meisten Spaß? Ordne.", "Kooperativ", "Strategie", "Quiz & Wissen", "Partyspiel"),
            q("Was ist dir beim Spieleabend am wichtigsten? Ordne.", "Spaß", "Spannung", "Taktik", "Gemeinsame Zeit"),
            q("Was sollte ein gutes Spiel für dich haben? Ordne.", "Einfache Regeln", "Viele Entscheidungen", "Kurze Runden", "Hoher Wiederspielwert"),
            q("Was nervt dich am ehesten? Ordne vom schlimmsten.", "Zu lange Erklärung", "Viel Zufall", "Endlose Spielzeit", "Zu viel Konkurrenz"),
            q("Mit wem spielst du am liebsten? Ordne.", "Nur zu zweit", "Kleine Gruppe", "Große Runde", "Wechselnde Mitspieler"),
            q("Was macht eine knappe Partie gut? Ordne.", "Fair verlieren können", "Guter Wettkampf", "Lustige Momente", "Revanche möglich")
        ),
        "h500_165_sprachen_lernen_prognose" to listOf(
            q("Wie würde dein Partner eine neue Sprache vermutlich am liebsten lernen?", "Im Alltag sprechen", "App und kurze Übungen", "Kurs mit Struktur", "Filme, Musik und Podcasts"),
            q("Was wäre deinem Partner wahrscheinlich wichtiger?", "Schnell sprechen können", "Gute Aussprache", "Grammatik verstehen", "Viele Wörter kennen"),
            q("Wodurch würde dein Partner vermutlich am meisten dranbleiben?", "Reiseziel", "Gemeinsames Lernen", "Fester Kurs", "Tägliche kleine Serie"),
            q("Was würde deinen Partner beim Lernen am ehesten bremsen?", "Angst vor Fehlern", "Zu wenig Zeit", "Trockene Übungen", "Kein konkretes Ziel"),
            q("Welche Übung würde dein Partner eher mögen?", "Gespräch führen", "Vokabelspiel", "Serie mit Untertiteln", "Kurze Schreibaufgabe"),
            q("Wann würde dein Partner wahrscheinlich merken: Jetzt kann ich wirklich etwas?", "Erstes echtes Gespräch", "Film teilweise verstehen", "Speisekarte problemlos lesen", "Jemanden spontan verstehen")
        ),
        "h500_166_instrumente_szenario" to listOf(
            q("Ihr findet zuhause ein altes Instrument und wollt es testen. Wie startet ihr?", "Gemeinsam ausprobieren", "Tutorial anschauen", "Einer spielt zuerst", "Erst stimmen und Grundlagen klären"),
            q("Einer lernt deutlich schneller als der andere. Was wäre die beste Reaktion?", "Zusammen einfache Stücke spielen", "Jeder übt im eigenen Tempo", "Der Schnellere hilft", "Unterschied akzeptieren und Spaß behalten"),
            q("Nach zehn Minuten klingt alles schief. Was macht ihr?", "Weiterprobieren und lachen", "Einfachere Übung suchen", "Kurze Pause", "Für heute aufhören"),
            q("Ihr könnt nur ein Instrument für einen gemeinsamen Kurs wählen. Wie entscheidet ihr?", "Beide testen vorher", "Abwechselnd Wünsche erfüllen", "Das leichter zugängliche", "Das, auf das beide neugierig sind"),
            q("Üben wäre zuhause ziemlich laut. Was ist fair?", "Feste Zeiten absprechen", "Kopfhörer-Instrument wählen", "Übungsraum suchen", "Kürzere Sessions"),
            q("Ihr könnt nach einem Monat ein erstes kleines Stück spielen. Was macht ihr?", "Für euch aufnehmen", "Freunden vorspielen", "Nächstes Stück starten", "Einfach den Fortschritt feiern")
        ),
        "h500_167_backen_geheime_wahl" to listOf(
            q("Was würdest du heimlich am liebsten zusammen backen?", "Brot", "Kuchen", "Cookies", "Etwas, das ihr noch nie gemacht habt"),
            q("Welche Rolle würdest du beim Backen am liebsten übernehmen?", "Teig machen", "Füllung oder Creme", "Dekorieren", "Ofen und Timing im Blick behalten"),
            q("Wie genau würdest du ein Rezept befolgen?", "Grammgenau", "Mit kleinen Änderungen", "Nur als Orientierung", "Lieber improvisieren"),
            q("Was wäre für dich der beste Teil?", "Naschen beim Backen", "Der Duft aus dem Ofen", "Dekorieren", "Gemeinsam probieren"),
            q("Wobei dürftest du gern überrascht werden?", "Geschmack", "Form", "Füllung", "Dekoration"),
            q("Was würdest du lieber riskieren?", "Kompliziertes Rezept", "Ungewöhnliche Zutaten", "Aufwendige Dekoration", "Etwas komplett Eigenes erfinden")
        ),
        "h500_170_ehrenamt_offene_runde" to listOf(
            GenQuestion("Für welches Thema würdest du freiwillig regelmäßig Zeit geben, auch wenn niemand davon erfährt?"),
            GenQuestion("Was wäre dir bei einem Ehrenamt wichtiger: direkt Menschen helfen, Tiere unterstützen, Umwelt schützen oder Wissen weitergeben – und warum?"),
            GenQuestion("Wie viel feste Verpflichtung wäre für dich bei freiwilligem Engagement noch angenehm?"),
            GenQuestion("Welche Fähigkeit von dir könnte in einem Ehrenamt besonders nützlich sein?"),
            GenQuestion("Würdest du lieber gemeinsam mit deinem Partner helfen oder bewusst ein eigenes Engagement haben?"),
            GenQuestion("Was müsste eine Organisation tun, damit du ihr deine Zeit langfristig anvertrauen würdest?")
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
