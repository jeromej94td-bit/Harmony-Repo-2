package com.example.data

/**
 * Final hand-curated pass for Harmony 360 topic placement and content quality.
 *
 * This layer intentionally runs after the generic 360 cleanup/rework stages. It keeps the
 * visible app taxonomy small, removes low-value generated filler from relationship content,
 * and gives surviving template-heavy packs subject-specific questions and answer choices.
 */
object Harmony360RelationshipTopicCuration {

    private const val SECTION_03 = "h360_section_03_zukunft_lebensplanung"
    private const val SECTION_13 = "h360_section_13_persoenlichkeit_werte"
    private const val SECTION_15 = "h360_section_15_glaube_religion"
    private const val SECTION_16 = "h360_section_16_politik_gesellschaft"
    private const val SECTION_17 = "h360_section_17_psychologie_gefuehle"
    private const val SECTION_19 = "h360_section_19_fantasie_was_waere_wenn"

    private val visibleTopicIds = setOf(
        "aufwaermen", "beziehung", "sex", "moral", "geld", "kennen",
        "reisen", "familie", "hobbys", "filme_serien", "essen"
    )

    /**
     * Sections that were generated as broad idea dumps are reduced to the packs that add
     * distinct value to the app. Everything else in those sections is intentionally archived.
     */
    private val keepBySection = mapOf(
        SECTION_03 to setOf(
            "h500_051_unser_naechstes_jahr_entweder_oder",
            "h500_054_traumhaus_ranking",
            "h500_056_auswandern_szenario",
            "h500_058_finanzielle_ziele_memory",
            "h500_060_hochzeit_offene_runde",
            "h500_061_familienplanung_entweder_oder",
            "h500_064_abenteuerliste_ranking",
            "h500_066_wohnort_szenario",
            "h500_070_sicherheit_oder_freiheit_offene_runde",
            "h500_075_das_leben_mit_60_prognose"
        ),
        SECTION_13 to setOf(
            "h500_271_werte_im_alltag_entweder_oder",
            "h500_272_charaktereigenschaften_wer_eher",
            "h500_274_lebensmotto_ranking",
            "h500_275_staerken_und_schwaechen_prognose",
            "h500_278_praegende_momente_memory",
            "h500_284_moral_ranking",
            "h500_285_vorbilder_prognose",
            "h500_290_sinn_des_lebens_offene_runde"
        ),
        SECTION_15 to setOf(
            "h500_313_glaube_skala",
            "h500_318_religioese_erziehung_memory",
            "h500_320_tod_und_danach_offene_runde",
            "h500_330_gemeinsamer_glaube_offene_runde"
        ),
        SECTION_16 to setOf(
            "h500_333_nachhaltigkeit_skala",
            "h500_339_gesellschaftliche_werte_prioritaet",
            "h500_340_gerechtigkeit_offene_runde",
            "h500_350_gemeinsames_weltbild_offene_runde"
        ),
        SECTION_17 to setOf(
            "h500_352_einfuehlungsvermoegen_wer_eher",
            "h500_353_verletzlichkeit_skala",
            "h500_355_eifersucht_prognose",
            "h500_357_wuensche_und_beduerfnisse_geheime_wahl",
            "h500_358_kindheitstraumata_memory",
            "h500_361_selbstwertgefuehl_entweder_oder",
            "h500_362_troesten_wer_eher",
            "h500_366_stressreaktionen_szenario",
            "h500_367_sehnsuechte_geheime_wahl"
        ),
        SECTION_19 to setOf(
            "h500_391_zeitreise_entweder_oder",
            "h500_392_superkraefte_wer_eher",
            "h500_394_lottogewinn_ranking",
            "h500_395_unsichtbarkeit_prognose",
            "h500_396_einsame_insel_szenario",
            "h500_398_kindheitstraum_memory",
            "h500_399_drei_wuensche_prioritaet",
            "h500_400_ewige_jugend_offene_runde",
            "h500_402_telepathie_wer_eher",
            "h500_403_zukunftsvision_skala",
            "h500_405_koerpertausch_prognose",
            "h500_407_geheime_fantasie_geheime_wahl",
            "h500_410_unsere_traumwelt_offene_runde"
        )
    )

    private val topicOverrides = mapOf(
        // Zukunft & Lebensplanung
        "h500_056_auswandern_szenario" to "reisen",
        "h500_058_finanzielle_ziele_memory" to "geld",
        "h500_061_familienplanung_entweder_oder" to "familie",
        "h500_064_abenteuerliste_ranking" to "reisen",

        // Alltag & Zuhause
        "h500_133_kochen_im_alltag_skala" to "essen",

        // Persönlichkeit & Werte
        "h500_271_werte_im_alltag_entweder_oder" to "moral",
        "h500_272_charaktereigenschaften_wer_eher" to "kennen",
        "h500_274_lebensmotto_ranking" to "kennen",
        "h500_275_staerken_und_schwaechen_prognose" to "kennen",
        "h500_278_praegende_momente_memory" to "kennen",
        "h500_284_moral_ranking" to "moral",
        "h500_285_vorbilder_prognose" to "kennen",
        "h500_290_sinn_des_lebens_offene_runde" to "moral",

        // Glaube & Religion: only a few strong conversations survive.
        "h500_313_glaube_skala" to "moral",
        "h500_318_religioese_erziehung_memory" to "familie",
        "h500_320_tod_und_danach_offene_runde" to "moral",

        // Politik & Gesellschaft: values, not a new politics category.
        "h500_333_nachhaltigkeit_skala" to "moral",
        "h500_339_gesellschaftliche_werte_prioritaet" to "moral",
        "h500_340_gerechtigkeit_offene_runde" to "moral",

        // Psychologie & Gefühle
        "h500_358_kindheitstraumata_memory" to "kennen",
        "h500_361_selbstwertgefuehl_entweder_oder" to "kennen",

        // Fantasie / Was wäre wenn
        "h500_391_zeitreise_entweder_oder" to "aufwaermen",
        "h500_392_superkraefte_wer_eher" to "aufwaermen",
        "h500_394_lottogewinn_ranking" to "geld",
        "h500_395_unsichtbarkeit_prognose" to "aufwaermen",
        "h500_396_einsame_insel_szenario" to "reisen",
        "h500_398_kindheitstraum_memory" to "kennen",
        "h500_399_drei_wuensche_prioritaet" to "aufwaermen",
        "h500_400_ewige_jugend_offene_runde" to "aufwaermen",
        "h500_403_zukunftsvision_skala" to "kennen",
        "h500_405_koerpertausch_prognose" to "aufwaermen",
        "h500_407_geheime_fantasie_geheime_wahl" to "kennen",

        // Teamwork & Challenge
        "h500_416_escape_room_szenario" to "hobbys",
        "h500_421_wettbewerb_entweder_oder" to "hobbys",
        "h500_422_mutprobe_wer_eher" to "aufwaermen",
        "h500_425_gemeinsamer_sieg_prognose" to "aufwaermen"
    )

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – sehr stark")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String) = GenQuestion(text, options.toList())
    private fun open(text: String) = GenQuestion(text)
    private fun scaleQ(text: String) = GenQuestion(text, scale)
    private fun whoQ(text: String) = GenQuestion(text, who)

    private val questionOverrides: Map<String, List<GenQuestion>> = mapOf(
        // -----------------------------------------------------------------
        // 03 · Zukunft & Lebensplanung
        // -----------------------------------------------------------------
        "h500_051_unser_naechstes_jahr_entweder_oder" to listOf(
            q("Was soll in eurem nächsten Jahr mehr Raum bekommen?", "Mehr gemeinsame Erlebnisse", "Mehr Ruhe und Stabilität", "Ein großes gemeinsames Ziel", "Mehr persönliche Freiheit"),
            q("Wenn ihr nur eine Sache konkret planen könntet: welche?", "Eine große Reise", "Einen Wohnungswechsel", "Ein finanzielles Ziel", "Mehr feste Zeit zu zweit"),
            q("Was wäre für dich das beste Zeichen, dass euer nächstes Jahr gut läuft?", "Wir lachen mehr", "Wir streiten fairer", "Wir erleben Neues", "Wir fühlen uns sicherer miteinander"),
            q("Wie möchtest du große Entscheidungen im nächsten Jahr treffen?", "Früh gemeinsam planen", "Erst Optionen sammeln", "Viel spontan entscheiden", "Jeder bringt eigene Ziele ein"),
            q("Wofür würdest du im nächsten Jahr am ehesten bewusst Zeit freimachen?", "Beziehung", "Familie und Freunde", "Reisen und Erlebnisse", "Persönliche Entwicklung"),
            q("Was sollte trotz aller Pläne flexibel bleiben?", "Wohnort", "Urlaub", "Karriere", "Alltagsgestaltung")
        ),
        "h500_054_traumhaus_ranking" to listOf(
            q("Was wäre dir bei eurem gemeinsamen Zuhause am wichtigsten? Ordne.", "Lage", "Genug Platz", "Bezahlbarkeit", "Wohlfühlgefühl"),
            q("Welche Wohnqualität würdest du am stärksten priorisieren? Ordne.", "Ruhe", "Nähe zur Stadt", "Natur", "Kurze Alltagswege"),
            q("Was dürfte in deinem Traumzuhause am wenigsten fehlen? Ordne.", "Große Küche", "Balkon oder Garten", "Gemütliches Wohnzimmer", "Rückzugsraum für jeden"),
            q("Wofür würdest du beim Wohnen am ehesten mehr Geld ausgeben? Ordne.", "Bessere Lage", "Mehr Wohnfläche", "Hochwertige Ausstattung", "Außenbereich"),
            q("Was sollte bei unterschiedlichen Wohnwünschen zuerst zählen? Ordne.", "Gemeinsames Budget", "Alltagsbedürfnisse", "Langfristige Pläne", "Kompromiss für beide"),
            q("Was macht ein Zuhause für dich wirklich zu eurem Zuhause? Ordne.", "Gemeinsame Erinnerungen", "Eigener Stil", "Ruhe und Sicherheit", "Platz für gemeinsame Pläne")
        ),
        "h500_056_auswandern_szenario" to listOf(
            q("Ihr bekommt morgen die Chance, für zwei Jahre ins Ausland zu ziehen. Was klärt ihr zuerst?", "Finanzen und Jobs", "Land und Sprache", "Familie und Kontakte", "Ob beide es wirklich wollen"),
            q("Einer will unbedingt auswandern, der andere ist unsicher. Was wäre der beste nächste Schritt?", "Probezeit im Ausland", "Gründe offen vergleichen", "Noch ein Jahr warten", "Alternative Stadt im eigenen Land prüfen"),
            q("Im neuen Land fühlt sich einer schnell zuhause, der andere gar nicht. Was tut ihr?", "Feste Check-ins vereinbaren", "Mehr Anschluss suchen", "Rückkehrdatum offen besprechen", "Alltag gemeinsam neu gestalten"),
            q("Was müsste vor einer Auswanderung für dich unbedingt abgesichert sein?", "Finanzpolster", "Job oder Einkommen", "Wohnung", "Rückkehrmöglichkeit"),
            q("Welche Herausforderung würdest du am ehesten gemeinsam angehen?", "Neue Sprache", "Neue Freundschaften", "Bürokratie", "Komplett neuer Alltag"),
            q("Wann wäre Auswandern für dich ein klares Nein?", "Nur einer will es", "Finanzen sind ungeklärt", "Keine realistische Perspektive", "Wichtige Bindungen würden zu stark leiden")
        ),
        "h500_058_finanzielle_ziele_memory" to listOf(
            open("Welche Erfahrung mit Geld hat deine heutigen finanziellen Ziele am stärksten geprägt?"),
            open("Auf welches finanzielle Ziel warst du bisher besonders stolz, als du es erreicht hast?"),
            open("Welche größere Ausgabe hat dir im Nachhinein wirklich gezeigt, was dir wichtig ist?"),
            open("Gab es eine Geldentscheidung, die du heute deutlich anders treffen würdest?"),
            open("Welches finanzielle Ziel möchtest du als Paar einmal gemeinsam erreichen?"),
            open("Was sollte dein Partner über deinen Umgang mit Sparen, Sicherheit und Genuss unbedingt wissen?")
        ),
        "h500_060_hochzeit_offene_runde" to listOf(
            open("Welche Bedeutung hätte eine Hochzeit für dich persönlich – unabhängig von Feier und Tradition?"),
            open("Was wäre dir bei einer Hochzeit wichtiger als alles, was Gäste davon halten?"),
            open("Welche Tradition rund ums Heiraten würdest du gern übernehmen – und welche bewusst weglassen?"),
            open("Wie groß oder klein müsste eine Hochzeit sein, damit sie sich wirklich nach dir anfühlt?"),
            open("Welche finanziellen Grenzen wären dir bei einer Hochzeit wichtig?"),
            open("Was sollte sich zwischen euch durch eine Hochzeit auf keinen Fall verändern?")
        ),
        "h500_061_familienplanung_entweder_oder" to listOf(
            q("Wenn du an Familienplanung denkst: Was braucht zuerst Klarheit?", "Ob Kinder gewünscht sind", "Wann es passen könnte", "Wie Alltag geteilt wird", "Welche Unterstützung es gibt"),
            q("Was wäre dir bei der Aufgabenverteilung mit Familie am wichtigsten?", "Zeit möglichst fair teilen", "Nach Stärken aufteilen", "Flexibel nach Phase", "Früh feste Zuständigkeiten klären"),
            q("Welche Frage sollte ein Paar vor Familiengründung unbedingt besprechen?", "Finanzen", "Erziehungswerte", "Betreuung und Arbeit", "Nähe zu Familie"),
            q("Was wäre für dich bei Kinderfragen am schwierigsten?", "Unterschiedlicher Wunsch", "Unterschiedliches Timing", "Finanzielle Unsicherheit", "Druck aus dem Umfeld"),
            q("Welche Art Unterstützung wäre für eine Familie am wertvollsten?", "Verlässliche Betreuung", "Finanzielle Reserve", "Flexible Arbeitszeit", "Familie oder Freunde in der Nähe"),
            q("Was sollte bei Familienplanung niemals untergehen?", "Freiwilligkeit beider", "Offene Kommunikation", "Realistische Belastung", "Die Beziehung als Paar")
        ),
        "h500_064_abenteuerliste_ranking" to listOf(
            q("Welche Reiseerlebnisse stehen auf deiner persönlichen Wunschliste ganz oben? Ordne.", "Roadtrip", "Fernreise", "Natur-Abenteuer", "Städtereise"),
            q("Was würdest du lieber zuerst gemeinsam erleben? Ordne.", "Polarlichter sehen", "Inselhopping", "Mehrtagestour in den Bergen", "Große Metropole entdecken"),
            q("Was macht ein Bucket-List-Erlebnis für dich besonders? Ordne.", "Etwas völlig Neues", "Gemeinsame Erinnerung", "Nervenkitzel", "Ein lange gehegter Traum"),
            q("Wofür würdest du auf einer besonderen Reise am ehesten mehr Budget einplanen? Ordne.", "Außergewöhnliche Unterkunft", "Aktivitäten", "Essen", "Bequeme Anreise"),
            q("Welche Art Abenteuer passt am besten zu dir? Ordne.", "Spontan und offen", "Gut geplant", "Komfortabel mit Highlights", "Einfach und naturverbunden"),
            q("Welche gemeinsame Reise würdest du am liebsten wirklich in den nächsten zwei Jahren abhaken? Ordne.", "Große Fernreise", "Traumstadt", "Naturreise", "Ungeplanter Roadtrip")
        ),
        "h500_066_wohnort_szenario" to listOf(
            q("Ihr bekommt zwei gute Jobangebote in unterschiedlichen Städten. Wie entscheidet ihr?", "Beide Karrierechancen vergleichen", "Lebensqualität vergleichen", "Finanzen durchrechnen", "Langfristige Paarziele priorisieren"),
            q("Einer liebt Großstadt, der andere möchte Ruhe. Welcher Kompromiss wäre für dich am realistischsten?", "Stadtrand", "Kleinere Stadt", "Heute Stadt, später Land", "Getrennte Bedürfnisse anders ausgleichen"),
            q("Ein Wohnort wäre perfekt, aber weit weg von Familie und Freunden. Was wiegt für dich stärker?", "Nähe zu wichtigen Menschen", "Bessere Lebensqualität", "Berufliche Chancen", "Gemeinsames neues Kapitel"),
            q("Die Traumwohnung liegt in einer Gegend, die einem von euch nicht gefällt. Was tut ihr?", "Weitersuchen", "Probeweise wohnen", "Kriterien neu gewichten", "Der skeptischere Partner hat Veto"),
            q("Was müsste ein neuer Wohnort für euch beide unbedingt bieten?", "Bezahlbares Wohnen", "Gute Alltagswege", "Freizeitmöglichkeiten", "Perspektive für die nächsten Jahre"),
            q("Wann sollte bei einem Wohnortwechsel lieber noch gewartet werden?", "Einer ist nicht überzeugt", "Finanzen sind zu knapp", "Joblage ist unsicher", "Der Wechsel löst kein echtes Problem")
        ),
        "h500_070_sicherheit_oder_freiheit_offene_runde" to listOf(
            open("In welchem Lebensbereich brauchst du besonders viel Sicherheit – und wo bewusst Freiheit?"),
            open("Wann fühlt sich Verlässlichkeit für dich beruhigend an und wann eher einengend?"),
            open("Welche persönliche Freiheit möchtest du auch in einer sehr engen Beziehung unbedingt behalten?"),
            open("Bei welcher gemeinsamen Entscheidung würdest du eher Sicherheit wählen als die spannendere Chance?"),
            open("Wo könnten wir einander mehr Sicherheit geben, ohne den anderen stärker zu kontrollieren?"),
            open("Welche Mischung aus Stabilität und Abenteuer wünschst du dir für eure nächsten Jahre?")
        ),
        "h500_075_das_leben_mit_60_prognose" to listOf(
            q("Wie stellt sich dein Partner euer Leben mit 60 vermutlich am ehesten vor?", "Ruhiges Zuhause", "Viel unterwegs", "Nah bei Familie und Freunden", "Noch voller Projekte"),
            q("Was wäre deinem Partner im späteren Leben vermutlich besonders wichtig?", "Gesundheit", "Finanzielle Sicherheit", "Gemeinsame Zeit", "Unabhängigkeit"),
            q("Wo würde dein Partner mit 60 am liebsten wohnen?", "In derselben Region", "Am Meer", "Im Grünen", "Mitten in einer lebendigen Stadt"),
            q("Wie aktiv möchte dein Partner im Alter vermutlich bleiben?", "Sehr aktiv", "Regelmäßig unterwegs", "Ruhiger mit ausgewählten Aktivitäten", "Am liebsten entspannt"),
            q("Wofür würde dein Partner im Ruhestand wahrscheinlich am liebsten Geld ausgeben?", "Reisen", "Zuhause", "Familie", "Hobbys"),
            q("Was würde dein Partner sich für euch mit 60 am meisten wünschen?", "Noch viel miteinander lachen", "Gemeinsame Routinen", "Neue Abenteuer", "Ein stabiles Zuhause")
        ),

        // -----------------------------------------------------------------
        // 13 · Persönlichkeit & Werte
        // -----------------------------------------------------------------
        "h500_271_werte_im_alltag_entweder_oder" to listOf(
            q("Was zählt für dich im Alltag mehr, wenn beides kollidiert?", "Ehrlichkeit", "Harmonie"),
            q("Was beeindruckt dich an Menschen stärker?", "Zuverlässigkeit", "Mut"),
            q("Was sollte man eher tun?", "Ein Versprechen halten", "Einen Fehler ehrlich zugeben"),
            q("Was wiegt für dich schwerer?", "Loyalität", "Gerechtigkeit"),
            q("Welche Haltung ist dir näher?", "Erst helfen, dann urteilen", "Erst verstehen, dann handeln"),
            q("Was möchtest du im Alltag stärker leben?", "Großzügigkeit", "Bescheidenheit")
        ),
        "h500_272_charaktereigenschaften_wer_eher" to listOf(
            whoQ("Wer bleibt in unerwarteten Situationen eher ruhig?"),
            whoQ("Wer sagt unangenehme Wahrheiten eher direkt, aber fair?"),
            whoQ("Wer ist bei neuen Menschen schneller offen?"),
            whoQ("Wer hält länger an einem einmal gefassten Plan fest?"),
            whoQ("Wer kann eher über die eigenen Fehler lachen?"),
            whoQ("Wer merkt schneller, wenn jemand Unterstützung braucht?"),
            whoQ("Wer braucht eher Zeit allein, um wieder Energie zu bekommen?"),
            whoQ("Wer lässt sich leichter für eine spontane Idee begeistern?")
        ),
        "h500_274_lebensmotto_ranking" to listOf(
            q("Welche Lebenshaltung passt am stärksten zu dir? Ordne.", "Mutig ausprobieren", "Verlässlich aufbauen", "Neugierig bleiben", "Das Leben genießen"),
            q("Was soll dein Leben langfristig am meisten prägen? Ordne.", "Gute Beziehungen", "Freiheit", "Erlebnisse", "Etwas bewirken"),
            q("Was hilft dir bei schwierigen Entscheidungen am meisten? Ordne.", "Eigene Werte", "Erfahrung", "Bauchgefühl", "Rat vertrauter Menschen"),
            q("Welche Aussage fühlt sich am ehesten wie dein Motto an? Ordne.", "Lieber echt als perfekt", "Mut vor Bequemlichkeit", "Nicht alles muss sofort sein", "Menschen sind wichtiger als Dinge"),
            q("Worin möchtest du später am wenigsten Kompromisse gemacht haben? Ordne.", "Liebe", "Gesundheit", "Freiheit", "Eigene Überzeugungen"),
            q("Was soll dein Partner über deine Lebenshaltung unbedingt verstehen? Ordne.", "Was mich antreibt", "Was mir Sicherheit gibt", "Was ich niemals aufgeben will", "Wofür ich Risiken eingehe")
        ),
        "h500_275_staerken_und_schwaechen_prognose" to listOf(
            q("Welche Stärke würde dein Partner dir vermutlich als Erstes zuschreiben?", "Empathie", "Zuverlässigkeit", "Humor", "Durchhaltevermögen"),
            q("Welche deiner Seiten unterschätzt du laut deinem Partner wahrscheinlich?", "Mut", "Gelassenheit", "Kreativität", "Soziale Stärke"),
            q("Wobei glaubt dein Partner vermutlich, dass du dir selbst im Weg stehst?", "Zu viel nachdenken", "Zu hohe Ansprüche", "Zu wenig Nein sagen", "Zu schnell aufgeben"),
            q("In welcher Situation erlebt dein Partner dich vermutlich besonders stark?", "Unter Druck", "Wenn jemand Hilfe braucht", "Bei neuen Ideen", "Bei langfristigen Zielen"),
            q("Welche Schwäche würde dein Partner bei dir wahrscheinlich am liebevollsten beschreiben?", "Ungeduld", "Sturheit", "Chaos", "Übervorsicht"),
            q("Welche Stärke ergänzt euch als Paar vermutlich am besten?", "Kommunikation", "Organisation", "Optimismus", "Pragmatismus")
        ),
        "h500_278_praegende_momente_memory" to listOf(
            open("Welcher Moment hat deine Sicht auf Beziehungen besonders geprägt?"),
            open("Welche Entscheidung in deinem Leben hat dich stärker verändert, als du damals erwartet hast?"),
            open("Welche Person hat dir eine wichtige Haltung fürs Leben mitgegeben?"),
            open("Welche schwierige Phase hat dir eine Stärke gezeigt, die du vorher nicht an dir kanntest?"),
            open("Welche schöne Erinnerung erklärt besonders gut, was dir heute wichtig ist?"),
            open("Welchen prägenden Moment sollte dein Partner kennen, um dich noch besser zu verstehen?")
        ),
        "h500_284_moral_ranking" to listOf(
            q("Welche Werte sollten bei schwierigen Entscheidungen zuerst zählen? Ordne.", "Fairness", "Ehrlichkeit", "Mitgefühl", "Verantwortung"),
            q("Was ist dir im Umgang mit anderen am wichtigsten? Ordne.", "Respekt", "Hilfsbereitschaft", "Toleranz", "Verlässlichkeit"),
            q("Was wiegt für dich bei einem Fehler stärker? Ordne.", "Absicht", "Folgen", "Übernommene Verantwortung", "Wiedergutmachung"),
            q("Welche Haltung sollte eine Gesellschaft am stärksten schützen? Ordne.", "Freiheit", "Gerechtigkeit", "Solidarität", "Sicherheit"),
            q("Was sollte bei einem moralischen Dilemma am meisten zählen? Ordne.", "Eigene Werte", "Betroffene Menschen", "Regeln", "Langfristige Folgen"),
            q("Welche Eigenschaft möchtest du selbst in schwierigen Situationen am ehesten bewahren? Ordne.", "Anstand", "Mut", "Mitgefühl", "Aufrichtigkeit")
        ),
        "h500_285_vorbilder_prognose" to listOf(
            q("Welche Eigenschaft bewundert dein Partner vermutlich am meisten an einem Vorbild?", "Mut", "Menschlichkeit", "Erfolg", "Authentizität"),
            q("Welche Art Mensch würde dein Partner eher als Vorbild nennen?", "Jemand aus der Familie", "Eine bekannte Persönlichkeit", "Ein Lehrer oder Mentor", "Jemand aus dem eigenen Umfeld"),
            q("Was müsste ein Vorbild für deinen Partner unbedingt haben?", "Glaubwürdigkeit", "Leistung", "Haltung", "Wärme"),
            q("Welche Schwäche dürfte ein Vorbild laut deinem Partner ruhig zeigen?", "Fehler zugeben", "Unsicherheit", "Scheitern", "Emotionen"),
            q("Welche Art Erfolg beeindruckt deinen Partner vermutlich am stärksten?", "Etwas aufgebaut", "Anderen geholfen", "Sich selbst treu geblieben", "Eine Krise überwunden"),
            q("Was würde dein Partner von einem echten Vorbild niemals akzeptieren?", "Arroganz", "Unehrlichkeit", "Rücksichtslosigkeit", "Doppelmoral")
        ),
        "h500_290_sinn_des_lebens_offene_runde" to listOf(
            open("Was macht ein Leben für dich persönlich sinnvoll – ganz unabhängig davon, was andere erwarten?"),
            open("Welche Menschen oder Beziehungen geben deinem Leben besonders viel Bedeutung?"),
            open("Was möchtest du einmal getan oder aufgebaut haben, damit du wirklich zufrieden zurückblickst?"),
            open("Welche Rolle spielen Erfolg, Familie, Freiheit und Erlebnisse in deiner Vorstellung eines guten Lebens?"),
            open("Was verändert deine Sicht auf das Leben, wenn du an begrenzte Zeit denkst?"),
            open("Wobei möchtest du, dass dein Partner dich dabei unterstützt, dein eigenes sinnvolles Leben zu führen?")
        ),

        // -----------------------------------------------------------------
        // 15 · Glaube & Religion
        // -----------------------------------------------------------------
        "h500_313_glaube_skala" to listOf(
            scaleQ("Wie wichtig ist Glaube oder Spiritualität für deine persönliche Lebensgestaltung?"),
            scaleQ("Wie wichtig ist dir, dass dein Partner deine Überzeugungen respektiert, auch wenn er sie nicht teilt?"),
            scaleQ("Wie offen bist du für Gespräche über Religion, Zweifel oder Spiritualität?"),
            scaleQ("Wie stark prägen religiöse oder spirituelle Werte deine moralischen Entscheidungen?"),
            scaleQ("Wie wichtig sind dir Rituale oder Feiertage mit religiösem Hintergrund?"),
            scaleQ("Wie gut könntest du mit einem Partner leben, dessen Glauben deutlich von deinem abweicht?")
        ),
        "h500_318_religioese_erziehung_memory" to listOf(
            open("Welche Rolle spielte Religion oder Spiritualität in deiner Kindheit?"),
            open("Welche Tradition aus deiner Familie ist dir bis heute wichtig – religiös oder nicht?"),
            open("Gab es eine Überzeugung aus deiner Erziehung, die du später bewusst verändert hast?"),
            open("Welche Erfahrung hat deine heutige Haltung zu Glaube besonders geprägt?"),
            open("Welche Werte aus deiner Erziehung würdest du einer eigenen Familie gern weitergeben?"),
            open("Welche religiösen oder weltanschaulichen Entscheidungen sollten Kinder später selbst treffen dürfen?")
        ),
        "h500_320_tod_und_danach_offene_runde" to listOf(
            open("Was glaubst oder hoffst du persönlich, was nach dem Tod kommt?"),
            open("Macht der Gedanke an Endlichkeit dein Leben eher wertvoller, beängstigender oder beides?"),
            open("Welche Art Erinnerung möchtest du einmal bei anderen hinterlassen?"),
            open("Wie wichtig ist dir, über Wünsche rund um Krankheit, Abschied und Tod offen sprechen zu können?"),
            open("Welche Überzeugung über Tod oder Abschied wurde durch deine Familie oder Erfahrungen geprägt?"),
            open("Was würde dir helfen, wenn ihr bei diesem Thema völlig unterschiedliche Überzeugungen habt?")
        ),
        "h500_330_gemeinsamer_glaube_offene_runde" to listOf(
            open("Wie wichtig ist dir, dass ihr bei Glauben oder Weltanschauung ähnlich denkt?"),
            open("Welche Unterschiede in religiösen oder spirituellen Überzeugungen könntest du gut akzeptieren?"),
            open("Wo könnte ein Unterschied im Glauben im gemeinsamen Alltag tatsächlich schwierig werden?"),
            open("Welche Feiertage oder Rituale würdest du als Paar gern gemeinsam gestalten – unabhängig von Religion?"),
            open("Wie sollte ein Paar entscheiden, welche religiösen Traditionen in einer späteren Familie eine Rolle spielen?"),
            open("Was bedeutet gegenseitiger Respekt für dich, wenn einer glaubt und der andere nicht?")
        ),

        // -----------------------------------------------------------------
        // 16 · Politik & Gesellschaft – reduced to values conversations
        // -----------------------------------------------------------------
        "h500_333_nachhaltigkeit_skala" to listOf(
            scaleQ("Wie wichtig ist dir Nachhaltigkeit bei alltäglichen Kaufentscheidungen?"),
            scaleQ("Wie bereit bist du, für nachhaltigere Produkte mehr zu bezahlen?"),
            scaleQ("Wie wichtig ist dir, beim Reisen auf Umweltfolgen zu achten?"),
            scaleQ("Wie stark sollte Nachhaltigkeit beeinflussen, wie ihr zuhause lebt?"),
            scaleQ("Wie wichtig ist dir, weniger zu konsumieren statt nur anders zu konsumieren?"),
            scaleQ("Wie gut könntest du akzeptieren, wenn dein Partner Nachhaltigkeit deutlich anders priorisiert als du?")
        ),
        "h500_339_gesellschaftliche_werte_prioritaet" to listOf(
            q("Welche gesellschaftlichen Werte sind dir am wichtigsten? Ordne.", "Freiheit", "Gerechtigkeit", "Solidarität", "Sicherheit"),
            q("Was sollte eine Gesellschaft besonders schützen? Ordne.", "Menschenwürde", "Chancengleichheit", "Privatsphäre", "Meinungsfreiheit"),
            q("Wo sollte Verantwortung zuerst liegen? Ordne.", "Beim Einzelnen", "In Familien und Gemeinschaften", "Bei Unternehmen", "Beim Staat"),
            q("Was prägt eine faire Gesellschaft am stärksten? Ordne.", "Bildung", "Soziale Absicherung", "Gleiche Regeln", "Möglichkeiten aufzusteigen"),
            q("Welche Haltung ist dir bei unterschiedlichen Lebensentwürfen besonders wichtig? Ordne.", "Toleranz", "Neugier", "Respekt", "Klare Grenzen"),
            q("Was möchtest du in eurem gemeinsamen Weltbild am stärksten wiederfinden? Ordne.", "Mitgefühl", "Verantwortung", "Freiheit", "Fairness")
        ),
        "h500_340_gerechtigkeit_offene_runde" to listOf(
            open("Wann fühlt sich etwas für dich wirklich gerecht an – wenn alle dasselbe bekommen oder wenn Bedürfnisse berücksichtigt werden?"),
            open("Bei welchem Thema reagierst du besonders sensibel auf Ungerechtigkeit?"),
            open("Wie viel persönliche Verantwortung sollte jemand tragen, wenn die Ausgangsbedingungen sehr unterschiedlich sind?"),
            open("Wann ist es für dich richtig, eine Regel zu brechen, weil sie ungerecht wirkt?"),
            open("Welche Form von Ungleichheit findest du am schwersten zu akzeptieren – und warum?"),
            open("Wie gehst du damit um, wenn dein Partner eine Frage von Gerechtigkeit völlig anders bewertet als du?")
        ),
        "h500_350_gemeinsames_weltbild_offene_runde" to listOf(
            open("Bei welchen Grundwerten ist dir wichtig, dass ihr als Paar ähnlich denkt?"),
            open("Welche politische oder gesellschaftliche Meinungsverschiedenheit könntest du in einer Beziehung gut aushalten?"),
            open("Ab welchem Punkt würde ein Unterschied im Weltbild für dich zu einem echten Beziehungsthema?"),
            open("Wie möchtet ihr miteinander reden, wenn ein gesellschaftliches Thema euch emotional auf unterschiedliche Seiten bringt?"),
            open("Welche Werte sollten euer gemeinsames Handeln stärker prägen als einzelne Meinungen?"),
            open("Was möchtest du über das Weltbild deines Partners besser verstehen, ohne ihn davon überzeugen zu müssen?")
        ),

        // -----------------------------------------------------------------
        // 17 · Psychologie & Gefühle
        // -----------------------------------------------------------------
        "h500_352_einfuehlungsvermoegen_wer_eher" to listOf(
            whoQ("Wer merkt eher, dass der andere nur sagt „alles gut“, obwohl etwas nicht stimmt?"),
            whoQ("Wer kann sich leichter in die Sicht des anderen hineinversetzen, obwohl er anderer Meinung ist?"),
            whoQ("Wer erkennt eher, wann Zuhören wichtiger ist als eine Lösung?"),
            whoQ("Wer nimmt Stimmungen im Raum schneller wahr?"),
            whoQ("Wer fragt eher nach, bevor er eine Reaktion persönlich nimmt?"),
            whoQ("Wer findet leichter die richtigen Worte, wenn der andere verletzt ist?"),
            whoQ("Wer respektiert eher, dass derselbe Moment für zwei Menschen völlig verschieden wirken kann?"),
            whoQ("Wer merkt eher, wann der andere Nähe braucht und wann eher Ruhe?")
        ),
        "h500_353_verletzlichkeit_skala" to listOf(
            scaleQ("Wie leicht fällt es dir, deinem Partner zu sagen, wenn du Angst oder Unsicherheit fühlst?"),
            scaleQ("Wie sicher fühlst du dich dabei, vor deinem Partner zu weinen?"),
            scaleQ("Wie offen kannst du über Dinge sprechen, für die du dich schämst?"),
            scaleQ("Wie leicht kannst du sagen „Das hat mich verletzt“, ohne sofort in einen Vorwurf zu gehen?"),
            scaleQ("Wie gut kannst du Hilfe annehmen, wenn du sie wirklich brauchst?"),
            scaleQ("Wie sehr hast du das Gefühl, dass Verletzlichkeit zwischen euch Nähe schaffen darf?")
        ),
        "h500_355_eifersucht_prognose" to listOf(
            q("Was löst bei deinem Partner vermutlich am ehesten Eifersucht aus?", "Flirten mit anderen", "Viel Kontakt zu einer Ex-Person", "Sich ausgeschlossen fühlen", "Unklare Grenzen"),
            q("Wie zeigt dein Partner Eifersucht vermutlich zuerst?", "Spricht es direkt an", "Wird stiller", "Stellt mehr Fragen", "Macht einen Witz darüber"),
            q("Was würde deinem Partner bei Eifersucht am meisten Sicherheit geben?", "Klare Worte", "Transparenz", "Nähe", "Eine gemeinsam besprochene Grenze"),
            q("Was würde Eifersucht bei deinem Partner eher verschlimmern?", "Abwiegeln", "Geheimniskrämerei", "Gegenangriff", "Absichtlich provozieren"),
            q("Welche Grenze ist deinem Partner vermutlich besonders wichtig?", "Respektvoller Umgang mit Ex-Partnern", "Flirten", "Private Nachrichten", "Nähe zu neuen Bekanntschaften"),
            q("Wie möchte dein Partner vermutlich über Eifersucht reden?", "Direkt und ruhig", "Erst nach etwas Abstand", "Mit konkreten Situationen", "Nur wenn wirklich etwas belastet")
        ),
        "h500_357_wuensche_und_beduerfnisse_geheime_wahl" to listOf(
            q("Welches Bedürfnis wünschst du dir heimlich öfter erfüllt?", "Mehr Nähe", "Mehr Ruhe", "Mehr gemeinsame Zeit", "Mehr Unterstützung"),
            q("Was fällt dir am schwersten direkt zu verlangen?", "Zeit für mich", "Zärtlichkeit", "Praktische Hilfe", "Mehr Aufmerksamkeit"),
            q("Wovon hättest du im Alltag gern etwas weniger?", "Termindruck", "Handy und Ablenkung", "Pflichten", "Erwartungen anderer"),
            q("Welcher Wunsch würde eure Beziehung gerade am ehesten bereichern?", "Mehr Dates", "Mehr tiefe Gespräche", "Mehr Leichtigkeit", "Mehr gemeinsame Pläne"),
            q("Was hoffst du manchmal, dass dein Partner von selbst bemerkt?", "Dass ich Ruhe brauche", "Dass ich Nähe brauche", "Dass ich überfordert bin", "Dass ich Anerkennung brauche"),
            q("Welchen Wunsch möchtest du künftig lieber klar sagen statt darauf zu hoffen, dass er erraten wird?", "Mehr Hilfe", "Mehr Freiraum", "Mehr Zuneigung", "Mehr Verbindlichkeit")
        ),
        "h500_358_kindheitstraumata_memory" to listOf(
            open("Welche schöne Kindheitserinnerung sagt viel darüber aus, was dir heute Geborgenheit gibt?"),
            open("Welche Regel aus deiner Kindheit würdest du heute genauso übernehmen – und welche bewusst nicht?"),
            open("Wie wurde in deiner Familie mit Streit oder starken Gefühlen umgegangen?"),
            open("Welche Erfahrung aus deiner Kindheit erklärt eine heutige Gewohnheit oder Empfindlichkeit von dir?"),
            open("Wer hat dir als Kind besonders viel Sicherheit oder Selbstvertrauen gegeben?"),
            open("Was sollte dein Partner aus deiner Kindheit kennen, um eine Seite von dir besser zu verstehen?")
        ),
        "h500_361_selbstwertgefuehl_entweder_oder" to listOf(
            q("Was stärkt dein Selbstwertgefühl eher?", "Eigene Fortschritte sehen", "Anerkennung bekommen"),
            q("Was fällt dir leichter?", "Ein Kompliment annehmen", "Eine eigene Stärke nennen"),
            q("Nach einem Fehler hilft dir eher was?", "Mich selbst nicht fertig machen", "Mit jemandem darüber reden"),
            q("Was trifft dich stärker?", "Kritik von außen", "Eigene hohe Ansprüche"),
            q("Was möchtest du eher lernen?", "Klarer Nein sagen", "Mehr an mich glauben"),
            q("Was sollte ein Partner eher tun, wenn du an dir zweifelst?", "Mich an meine Stärken erinnern", "Einfach zuhören")
        ),
        "h500_362_troesten_wer_eher" to listOf(
            whoQ("Wer merkt eher, dass der andere Trost braucht, bevor er etwas sagt?"),
            whoQ("Wer bietet eher zuerst eine Umarmung an?"),
            whoQ("Wer kann besser einfach zuhören, ohne sofort Lösungen vorzuschlagen?"),
            whoQ("Wer findet eher die richtigen beruhigenden Worte?"),
            whoQ("Wer bringt den anderen eher vorsichtig wieder zum Lachen?"),
            whoQ("Wer erinnert sich eher daran, später noch einmal nachzufragen?"),
            whoQ("Wer weiß besser, wann Trost Nähe bedeutet und wann Raum?"),
            whoQ("Wer übernimmt eher praktisch etwas, wenn der andere völlig erschöpft ist?")
        ),
        "h500_366_stressreaktionen_szenario" to listOf(
            q("Dein Partner kommt völlig gestresst nach Hause und ist kurz angebunden. Was machst du zuerst?", "Kurz Ruhe geben", "Nachfragen, was los ist", "Etwas Praktisches abnehmen", "Nähe anbieten"),
            q("Ihr seid beide gestresst und eine Kleinigkeit droht zum Streit zu werden. Was hilft am ehesten?", "Pause vereinbaren", "Beim konkreten Thema bleiben", "Aufgaben verteilen", "Den Abend vereinfachen"),
            q("Dein Partner zieht sich bei Stress zurück. Wie gehst du damit um?", "Raum respektieren", "Kurz Sicherheit geben", "Zeitpunkt zum Reden vereinbaren", "Nicht persönlich nehmen"),
            q("Du wirst unter Stress kontrollierender als sonst. Was wäre ein guter Gegenschritt?", "Aufgaben abgeben", "Prioritäten kürzen", "Offen sagen, dass ich überfordert bin", "Kurz rausgehen"),
            q("Eine stressige Phase dauert mehrere Wochen. Was sollte als Paar geschützt werden?", "Schlaf und Erholung", "Kurze echte Paarzeit", "Faire Aufgabenverteilung", "Offene Check-ins"),
            q("Woran sollte dein Partner bei dir erkennen, dass Stress gerade zu viel wird?", "Ich werde still", "Ich werde gereizt", "Ich funktioniere nur noch", "Ich ziehe mich zurück")
        ),
        "h500_367_sehnsuechte_geheime_wahl" to listOf(
            q("Wonach sehnst du dich im gemeinsamen Alltag heimlich am meisten?", "Mehr Leichtigkeit", "Mehr Nähe", "Mehr Abenteuer", "Mehr Ruhe"),
            q("Welche gemeinsame Erfahrung fehlt dir noch?", "Eine große Reise", "Ein gemeinsames Projekt", "Ein richtig mutiges Abenteuer", "Eine längere Auszeit zu zweit"),
            q("Was würdest du gern öfter zwischen euch spüren?", "Begeisterung", "Geborgenheit", "Neugier", "Tiefe"),
            q("Welche Form von Zeit zu zweit vermisst du am ehesten?", "Lange Gespräche", "Spontane Dates", "Ruhige Abende", "Gemeinsames Entdecken"),
            q("Was würdest du gern einmal aussprechen, ohne sofort einen Plan daraus machen zu müssen?", "Einen großen Traum", "Eine Veränderung", "Eine Unsicherheit", "Einen verrückten Wunsch"),
            q("Welche Sehnsucht soll in den nächsten zwölf Monaten mehr Platz bekommen?", "Reisen", "Zuhause und Geborgenheit", "Persönliche Entwicklung", "Romantik")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        val sectionRule = keepBySection.entries.firstOrNull { (sectionTag, _) -> sectionTag in pack.tags }
        if (sectionRule != null && pack.id !in sectionRule.value) {
            return@mapNotNull null
        }

        val topic = topicOverrides[pack.id] ?: pack.topic
        check(topic in visibleTopicIds) {
            "Harmony 360 curation produced unknown visible topic '$topic' for ${pack.id}"
        }

        val questions = questionOverrides[pack.id] ?: pack.questions
        pack.copy(topic = topic, questions = questions)
    }
}
