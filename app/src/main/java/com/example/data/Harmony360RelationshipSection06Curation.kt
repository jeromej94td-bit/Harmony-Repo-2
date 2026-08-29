package com.example.data

/** Stage 05.1d: hand-curated Alltag & Zuhause content. */
object Harmony360RelationshipSection06Curation {

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String) = GenQuestion(text, options.toList())
    private fun scaleQ(text: String) = GenQuestion(text, scale)
    private fun whoQ(text: String) = GenQuestion(text, who)

    internal val decisions = linkedMapOf(
        "h500_126_morgenroutine_szenario" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_127_abendroutine_geheime_wahl" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_128_haushalt_memory" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_129_ordnung_prioritaet" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_132_einkaufen_wer_eher" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_133_kochen_im_alltag_skala" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_134_schlafen_ranking" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_135_homeoffice_prognose" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_136_dekoration_szenario" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_138_haustiere_memory" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_139_besuch_bekommen_prioritaet" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_141_sonntage_entweder_oder" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_142_feierabend_wer_eher" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_143_gemeinsame_to_do_liste_skala" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_144_technik_zuhause_ranking" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_148_wohnzimmer_memory" to Harmony360RelationshipQualityRework.CurationDecision.ARCHIVE,
        "h500_149_balkon_prioritaet" to Harmony360RelationshipQualityRework.CurationDecision.ARCHIVE,
        "h500_150_unser_gemuetlichster_abend_offene_runde" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE
    )

    internal val archivedIds: Set<String> = decisions
        .filterValues { it == Harmony360RelationshipQualityRework.CurationDecision.ARCHIVE }
        .keys

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_126_morgenroutine_szenario" to listOf(
            q("Einer ist morgens sofort gesprächig, der andere braucht Ruhe. Was wäre für dich die beste Lösung?", "Erst Ruhe, später reden", "Kurzer Guten-Morgen-Moment", "Kaffee zusammen, dann jeder für sich", "Je nach Tag spontan"),
            q("Ihr müsst gleichzeitig los und das Bad wird knapp. Was funktioniert für dich am besten?", "Feste Reihenfolge", "Wer früher losmuss zuerst", "Abwechseln", "Einer macht sich woanders fertig"),
            q("Dein Partner verschläft und ist gestresst. Was hilft eher?", "Praktisch mithelfen", "Ruhe lassen", "Frühstück einpacken", "Kurz motivieren"),
            q("Was sollte morgens möglichst nicht zur Paarpflicht werden?", "Sofort reden", "Gemeinsam frühstücken", "Gute Laune spielen", "Alles perfekt planen"),
            q("Was würde eure Morgen entspannter machen?", "Vorabend vorbereiten", "Klare Aufgaben", "10 Minuten Puffer", "Weniger Handy"),
            q("Wenn einer Frühaufsteher und einer Morgenmuffel ist: Was zählt mehr?", "Ruhe respektieren", "Gemeinsames Mini-Ritual", "Getrennte Abläufe", "Am Wochenende ausgleichen")
        ),
        "h500_127_abendroutine_geheime_wahl" to listOf(
            q("Was brauchst du am Abend heimlich öfter?", "Noch reden", "Einfach Ruhe", "Kuscheln", "Zeit allein"),
            q("Was wäre dein perfekter Übergang vom Alltag in den Abend?", "Gemeinsam essen", "Duschen und runterkommen", "Spaziergang", "Sofa ohne Plan"),
            q("Was sollte die letzte halbe Stunde vor dem Schlafen eher sein?", "Handyfrei", "Serie zusammen", "Reden", "Jeder macht seins"),
            q("Was nervt dich abends schneller, als du zugibst?", "Noch To-dos beginnen", "Lautes Handy", "Zu spät essen", "„Nur noch kurz“ arbeiten"),
            q("Wenn nur einer noch Energie hat: Was wäre fair?", "Der Aktive macht seins", "Kurzer gemeinsamer Moment zuerst", "Heute Ruhe, morgen Aktion", "Spontan entscheiden"),
            q("Welches Abendritual würdest du am liebsten fest behalten?", "Gute-Nacht-Kuss", "Kurzes Tages-Update", "Gemeinsam ins Bett", "Noch etwas zusammen trinken")
        ),
        "h500_128_haushalt_memory" to listOf(
            GenQuestion("Welche Aufgabe im Haushalt bemerkst du meistens schon, bevor dein Partner sie überhaupt sieht?"),
            GenQuestion("Wann hast du dich zuletzt wirklich entlastet gefühlt, weil dein Partner etwas von selbst übernommen hat?"),
            GenQuestion("Bei welcher Haushaltsaufgabe spürst du Mental Load am stärksten – also das Planen, Erinnern und Mitdenken dahinter?"),
            GenQuestion("Welche Aufgabe wird bei euch eher diskutiert, obwohl sie eigentlich in fünf Minuten erledigt wäre?"),
            GenQuestion("Was sollte dein Partner über deinen persönlichen Sauberkeitsstandard unbedingt wissen?"),
            GenQuestion("Welche Haushaltsregel würde euren Alltag sofort friedlicher machen?")
        ),
        "h500_129_ordnung_prioritaet" to listOf(
            q("Was ist dir bei Ordnung zuhause am wichtigsten?", "Saubere Küche", "Freie Flächen", "Aufgeräumtes Bad", "Kein Chaos im Schlafzimmer"),
            q("Welche Unordnung kannst du am ehesten ignorieren?", "Kleidung herumliegen", "Geschirr stehen lassen", "Papierkram", "Unaufgeräumte Schränke"),
            q("Wenn eure Sauberkeitsstandards verschieden sind: Was sollte zuerst zählen?", "Mindeststandard vereinbaren", "Aufgaben aufteilen", "Wer mehr will, macht mehr", "Bestimmte Zonen festlegen"),
            q("Was stresst dich stärker?", "Dreck", "Unordnung", "Sachen nicht wiederfinden", "Ständig erinnert werden"),
            q("Wo brauchst du am meisten deine eigene Ordnung?", "Schreibtisch", "Kleiderschrank", "Küche", "Bad"),
            q("Was wäre der beste Kompromiss für unterschiedliche Ordnungstypen?", "Gemeinsamer 15-Minuten-Reset", "Feste Zuständigkeiten", "Eigene Chaos-Zone", "Wöchentlicher Großputz")
        ),
        "h500_132_einkaufen_wer_eher" to listOf(
            whoQ("Wer merkt eher, dass etwas Wichtiges zuhause fast leer ist?"),
            whoQ("Wer hält sich eher wirklich an die Einkaufsliste?"),
            whoQ("Wer packt eher noch drei spontane Snacks in den Wagen?"),
            whoQ("Wer achtet eher auf das gemeinsame Budget beim Einkaufen?"),
            whoQ("Wer vergleicht eher Preise, bevor etwas Größeres gekauft wird?"),
            whoQ("Wer schreibt eher „Brauchst du noch was?“, wenn er sowieso im Laden ist?"),
            whoQ("Wer kommt eher mit genau dem einen Ding nach Hause, das gar nicht auf der Liste stand?")
        ),
        "h500_133_kochen_im_alltag_skala" to listOf(
            scaleQ("Wie wichtig ist dir gemeinsame Essensplanung für die Woche?"),
            scaleQ("Wie sehr nervt es dich, wenn die Frage „Was essen wir?“ jeden Abend neu beginnt?"),
            scaleQ("Wie gern kochst du gemeinsam statt alleine?"),
            scaleQ("Wie wichtig ist dir eine faire Aufteilung zwischen Kochen und Abwasch?"),
            scaleQ("Wie offen bist du dafür, dass einer komplett entscheidet, was gekocht wird?"),
            scaleQ("Wie wichtig ist dir, dass Lieblingsgerichte des anderen regelmäßig mitgedacht werden?")
        ),
        "h500_134_schlafen_ranking" to listOf(
            q("Was ist dir für guten gemeinsamen Schlaf am wichtigsten? Ordne.", "Ruhe", "Dunkelheit", "Passende Temperatur", "Genug Platz"),
            q("Welche Nähe beim Einschlafen passt am besten zu dir? Ordne.", "Nähe beim Einschlafen", "Kuscheln und später lösen", "Nur kurz berühren", "Mehr Freiraum im Bett"),
            q("Was stört deinen Schlaf am stärksten? Ordne.", "Schnarchen", "Handylicht", "Unterschiedliche Bettzeiten", "Zu warme oder kalte Luft"),
            q("Wie würdest du unterschiedliche Schlafrhythmen lösen? Ordne.", "Jeder eigene Bettzeit", "Meist zusammen ins Bett", "Kurzes Ritual, dann getrennt", "Am Wochenende angleichen"),
            q("Was sollte morgens im Schlafzimmer am meisten respektiert werden? Ordne.", "Weckerlautstärke", "Licht", "Redebedarf", "Noch schlafen dürfen"),
            q("Was wäre dir bei Schlafproblemen als Paar am wichtigsten? Ordne.", "Offen ansprechen", "Praktische Lösung testen", "Nicht persönlich nehmen", "Schlaf beider ernst nehmen")
        ),
        "h500_135_homeoffice_prognose" to listOf(
            q("Was braucht dein Partner im Homeoffice vermutlich am meisten?", "Ruhe", "Klare Arbeitszeiten", "Eigenen Platz", "Kurze Pausen zusammen"),
            q("Was würde deinen Partner im Homeoffice wahrscheinlich am meisten stören?", "Ungefragtes Reinplatzen", "Haushalt nebenbei erwarten", "Laute Calls", "Kein richtiger Feierabend"),
            q("Welche Grenze wäre deinem Partner vermutlich wichtig?", "Geschlossene Tür heißt nicht stören", "Pausen sind keine Haushaltszeit", "Nach Feierabend Laptop zu", "Calls vorher ankündigen"),
            q("Was würde Homeoffice für euren Alltag vermutlich verbessern?", "Mehr gemeinsame Mahlzeiten", "Weniger Pendeln", "Mehr Flexibilität", "Mehr Zeit am Abend"),
            q("Wo könnte Homeoffice bei euch eher Reibung erzeugen?", "Raumaufteilung", "Lautstärke", "Aufgaben zuhause", "Trennung Arbeit/Privat"),
            q("Welche kleine Unterstützung würde dein Partner wahrscheinlich schätzen?", "Kaffee bringen", "Ruhe schützen", "Mittag zusammen", "Feierabend bewusst starten")
        ),
        "h500_136_dekoration_szenario" to listOf(
            q("Ihr liebt zwei völlig verschiedene Einrichtungsstile. Wie entscheidet ihr?", "Jeder bekommt Bereiche", "Gemeinsame dritte Variante", "Abwechselnd auswählen", "Veto für beide"),
            q("Einer liebt ein Möbelstück, der andere findet es furchtbar. Was ist fair?", "Gemeinsames Veto", "Probeweise aufstellen", "In eigenen Bereich", "Alternative zusammen suchen"),
            q("Dekoration wird teurer als geplant. Was kürzt ihr zuerst?", "Deko statt Möbel", "Alles langsamer kaufen", "Secondhand suchen", "Nur Lieblingsstück behalten"),
            q("Wer sollte bei gemeinsam genutzten Räumen mehr bestimmen?", "Beide gleich", "Wer ihn mehr nutzt", "Jeder bei eigenen Dingen", "Immer gemeinsam entscheiden"),
            q("Ein Geschenk passt überhaupt nicht zu eurer Wohnung. Was macht ihr?", "Trotzdem sichtbar", "Ehrlich umtauschen", "Nur gelegentlich nutzen", "Anderen Platz finden"),
            q("Was macht ein Zuhause für dich persönlicher?", "Fotos und Erinnerungen", "Farben", "Souvenirs", "Gemeinsam ausgesuchte Dinge")
        ),
        "h500_138_haustiere_memory" to listOf(
            GenQuestion("Welche Erfahrung mit einem Tier hat deine heutige Haltung zu Haustieren am stärksten geprägt?"),
            GenQuestion("Welche Tier-Eigenheit würdest du zuhause liebenswert finden – und welche würde dich wahnsinnig machen?"),
            GenQuestion("Was müsste vor einem gemeinsamen Haustier unbedingt geklärt sein: Zeit, Kosten, Reisen oder Aufgaben? Warum?"),
            GenQuestion("Welche Verantwortung rund um ein Haustier würdest du freiwillig übernehmen – und welche ungern?"),
            GenQuestion("Dürfte ein Haustier bei dir ins Bett oder aufs Sofa? Was steckt hinter deiner Antwort?"),
            GenQuestion("Welche gemeinsame Erinnerung mit einem Haustier würdest du irgendwann gern haben?")
        ),
        "h500_139_besuch_bekommen_prioritaet" to listOf(
            q("Was ist dir bei Besuch zuhause am wichtigsten?", "Vorher Bescheid wissen", "Wohnung halbwegs ordentlich", "Genug Rückzug danach", "Gast soll sich wohlfühlen"),
            q("Wie spontan darf Besuch für dich sein?", "Jederzeit", "Ein paar Stunden vorher", "Am Vortag", "Nur geplant"),
            q("Was sollte vor Übernachtungsbesuch zuerst geklärt werden?", "Dauer", "Schlafplatz", "Gemeinsame Pläne", "Privatsphäre"),
            q("Wenn einer keine Energie für Besuch hat: Was zählt mehr?", "Absagen", "Besuch verkürzen", "Nur einer empfängt", "Heute durchziehen, danach Ruhe"),
            q("Was stresst dich beim Gastgebersein am meisten?", "Aufräumen", "Essen organisieren", "Lange bleiben", "Keine Zeit zu zweit"),
            q("Was wäre nach Besuch am fairsten?", "Gemeinsam aufräumen", "Wer eingeladen hat räumt", "Am nächsten Tag", "Nur das Nötigste sofort")
        ),
        "h500_141_sonntage_entweder_oder" to listOf(
            q("Wie sieht dein perfekter Sonntag eher aus?", "Langsam zuhause", "Früh raus und etwas erleben"),
            q("Sonntagmorgen?", "Langes Frühstück", "Ausschlafen und später starten"),
            q("Was fühlt sich sonntags besser an?", "Nichts vorhaben", "Einen schönen Plan haben"),
            q("Haushalt am Sonntag?", "Kurz gemeinsam erledigen", "Konsequent frei lassen"),
            q("Familie oder Freunde sonntags?", "Gern regelmäßig", "Lieber viel Zeit nur zu zweit"),
            q("Sonntagabend?", "Woche kurz planen", "Bloß noch nicht an Montag denken")
        ),
        "h500_142_feierabend_wer_eher" to listOf(
            whoQ("Wer braucht nach der Arbeit eher erst zehn Minuten Ruhe, bevor geredet wird?"),
            whoQ("Wer erzählt eher sofort den kompletten Arbeitstag?"),
            whoQ("Wer sagt eher: „Heute bestellen wir einfach“?"),
            whoQ("Wer schlägt eher spontan einen Feierabend-Spaziergang vor?"),
            whoQ("Wer schafft es eher, Arbeitsnachrichten nach Feierabend wirklich liegen zu lassen?"),
            whoQ("Wer merkt eher, dass der andere heute keinen großen Plan mehr braucht?"),
            whoQ("Wer macht aus einem normalen Feierabend eher noch einen kleinen besonderen Moment?")
        ),
        "h500_143_gemeinsame_to_do_liste_skala" to listOf(
            scaleQ("Wie hilfreich wäre euch eine wirklich gemeinsame To-do-Liste?"),
            scaleQ("Wie sehr belastet dich der Mental Load, an Aufgaben erinnern zu müssen?"),
            scaleQ("Wie wichtig ist dir, dass eine übernommene Aufgabe komplett selbstständig erledigt wird?"),
            scaleQ("Wie sehr nerven dich Erinnerungen an Aufgaben, die du sowieso machen wolltest?"),
            scaleQ("Wie wichtig ist dir eine klare Zuständigkeit statt ständigem Nachfragen?"),
            scaleQ("Wie gut funktioniert eure aktuelle Aufteilung von Planen, Erinnern und Erledigen?")
        ),
        "h500_144_technik_zuhause_ranking" to listOf(
            q("Was sollte bei Technik zuhause am meisten geschützt werden? Ordne.", "Handyfreie Essenszeit", "Schlaf ohne Bildschirm", "Gemeinsame Serienzeit", "Zeit für eigene Geräte"),
            q("Welche Technik-Regel wäre dir am wichtigsten? Ordne.", "Keine Handys im Bett", "Benachrichtigungen leise", "Nicht beim Gespräch scrollen", "Arbeitsgeräte nach Feierabend weg"),
            q("Wo nervt Bildschirmzeit dich am ehesten? Ordne.", "Beim Essen", "Im Bett", "Beim gemeinsamen Film", "Während eines Gesprächs"),
            q("Welche gemeinsame Technik ist dir am wichtigsten? Ordne.", "Guter Fernseher", "Musik", "Smart-Home-Komfort", "Gemeinsame Fotos"),
            q("Wofür würdest du bewusst weniger Bildschirmzeit wollen? Ordne.", "Mehr Gespräche", "Besser schlafen", "Mehr Bewegung", "Mehr echte gemeinsame Zeit"),
            q("Was wäre der beste Handy-Kompromiss zuhause? Ordne.", "Feste handyfreie Zeiten", "Nur bei wichtigen Gesprächen weg", "Jeder entscheidet selbst", "Bestimmte Räume handyfrei")
        ),
        "h500_150_unser_gemuetlichster_abend_offene_runde" to listOf(
            GenQuestion("Was gehört für dich zu einem wirklich gemütlichen Abend zu Hause – und was ganz bewusst nicht?"),
            GenQuestion("Was wäre für dich gemütlicher: gemeinsam kochen oder Lieblingsessen bestellen – und warum?"),
            GenQuestion("Welche Mischung aus Gespräch, Serie, Musik, Spiel oder einfach Ruhe fühlt sich für dich perfekt an?"),
            GenQuestion("Wie viel Handy darf bei einem richtig guten Abend zu zweit noch vorkommen?"),
            GenQuestion("Welche kleine Sache macht einen normalen Abend für dich plötzlich besonders?"),
            GenQuestion("An welchen gemeinsamen Abend zu Hause erinnerst du dich besonders gern – was war daran so gut?"),
            GenQuestion("Wenn wir morgen einen komplett freien Abend hätten: Wie würdest du ihn gestalten?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        if ("h360_section_06_alltag_zuhause" !in pack.tags) {
            pack
        } else if (pack.id in archivedIds) {
            null
        } else {
            overrides[pack.id]?.let { pack.copy(questions = it) } ?: pack
        }
    }
}
