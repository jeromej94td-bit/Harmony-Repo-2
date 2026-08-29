package com.example.data

/**
 * Normalizes legacy Harmony 360 topic ids to the visible app taxonomy and curates the
 * work/health sections that were historically dumped into `beziehung`.
 *
 * This pass runs after the generic generated-content rework so existing hand-written work
 * overrides are preserved. It removes low-value template packs rather than inventing new topics.
 */
object Harmony360TopicNormalizationCuration {

    private const val SECTION_07 = "h360_section_07_freizeit_hobbys"
    private const val SECTION_08 = "h360_section_08_freunde_familie"
    private const val SECTION_10 = "h360_section_10_arbeit_karriere"
    private const val SECTION_11 = "h360_section_11_gesundheit_fitness"
    private const val SECTION_14 = "h360_section_14_kultur_medien"
    private const val SECTION_18 = "h360_section_18_humor_lachen"

    private val keepWork = setOf(
        "h500_211_arbeitszeiten_entweder_oder",
        "h500_214_selbststaendigkeit_ranking",
        "h500_215_berufliche_veraenderung_prognose",
        "h500_216_work_life_balance_szenario",
        "h500_217_geheimnis_arbeitsplatz_geheime_wahl",
        "h500_219_berufliche_ziele_prioritaet",
        "h500_220_job_und_beziehung_offene_runde",
        "h500_221_nebenjob_entweder_oder",
        "h500_224_arbeitsweg_ranking",
        "h500_225_ruhestand_prognose",
        "h500_226_kuendigung_szenario",
        "h500_227_kollegen_geheime_wahl",
        "h500_230_beruflicher_erfolg_offene_runde"
    )

    private val keepHealth = setOf(
        "h500_231_ernaehrung_entweder_oder",
        "h500_236_sportliche_ziele_szenario",
        "h500_238_krank_sein_memory",
        "h500_239_gesunder_lebensstil_prioritaet",
        "h500_241_biorhythmus_entweder_oder",
        "h500_242_gesundes_kochen_wer_eher",
        "h500_247_sportarten_geheime_wahl",
        "h500_250_gemeinsame_gesundheit_offene_runde"
    )

    private val topicOverrides = mapOf(
        // Arbeit & Karriere
        "h500_211_arbeitszeiten_entweder_oder" to "kennen",
        "h500_214_selbststaendigkeit_ranking" to "kennen",
        "h500_215_berufliche_veraenderung_prognose" to "kennen",
        "h500_216_work_life_balance_szenario" to "beziehung",
        "h500_217_geheimnis_arbeitsplatz_geheime_wahl" to "kennen",
        "h500_219_berufliche_ziele_prioritaet" to "kennen",
        "h500_220_job_und_beziehung_offene_runde" to "beziehung",
        "h500_221_nebenjob_entweder_oder" to "kennen",
        "h500_224_arbeitsweg_ranking" to "kennen",
        "h500_225_ruhestand_prognose" to "beziehung",
        "h500_226_kuendigung_szenario" to "beziehung",
        "h500_227_kollegen_geheime_wahl" to "kennen",
        "h500_230_beruflicher_erfolg_offene_runde" to "kennen",

        // Gesundheit & Fitness
        "h500_231_ernaehrung_entweder_oder" to "essen",
        "h500_236_sportliche_ziele_szenario" to "hobbys",
        "h500_238_krank_sein_memory" to "beziehung",
        "h500_239_gesunder_lebensstil_prioritaet" to "kennen",
        "h500_241_biorhythmus_entweder_oder" to "kennen",
        "h500_242_gesundes_kochen_wer_eher" to "essen",
        "h500_247_sportarten_geheime_wahl" to "hobbys",
        "h500_250_gemeinsame_gesundheit_offene_runde" to "beziehung"
    )

    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")
    private fun q(text: String, vararg options: String) = GenQuestion(text, options.toList())
    private fun open(text: String) = GenQuestion(text)
    private fun whoQ(text: String) = GenQuestion(text, who)

    private val questionOverrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_220_job_und_beziehung_offene_runde" to listOf(
            open("Wie viel Raum darf Arbeit in eurem gemeinsamen Alltag einnehmen, bevor es sich für dich nach zu viel anfühlt?"),
            open("Woran würdest du merken, dass der Job eines von euch gerade zu viel Energie aus der Beziehung zieht?"),
            open("Welche berufliche Chance würdest du auch dann unterstützen, wenn sie euren Alltag vorübergehend schwieriger macht?"),
            open("Welche Grenze zwischen Arbeit und Privatleben möchtest du als Paar unbedingt schützen?"),
            open("Wie soll dein Partner reagieren, wenn du wegen der Arbeit dauerhaft gestresst oder kaum ansprechbar bist?"),
            open("Welche Form von Unterstützung bei beruflichem Druck fühlt sich für dich hilfreich an – und welche eher kontrollierend?")
        ),
        "h500_230_beruflicher_erfolg_offene_runde" to listOf(
            open("Woran würdest du für dich persönlich merken, dass du beruflich erfolgreich bist?"),
            open("Was wäre dir im Beruf wichtiger als ein höheres Gehalt?"),
            open("Welche Art von beruflichem Erfolg würde dich stolz machen, auch wenn niemand davon erfährt?"),
            open("Was möchtest du für Karriere oder Status niemals dauerhaft opfern?"),
            open("Wie wichtig sind Anerkennung, Freiheit, Einkommen und Sinn jeweils für deine Vorstellung von Erfolg?"),
            open("Welche berufliche Entwicklung würdest du in den nächsten Jahren wirklich gern erleben?")
        ),
        "h500_231_ernaehrung_entweder_oder" to listOf(
            q("Was passt im Alltag eher zu dir?", "Frisch kochen", "Schnell und unkompliziert"),
            q("Bei Lebensmitteln achtest du eher auf was?", "Geschmack", "Nährwerte"),
            q("Was ist dir lieber?", "Feste Essenszeiten", "Essen nach Hunger"),
            q("Wenn du dich entscheiden musst?", "Bewährtes Lieblingsessen", "Etwas Neues probieren"),
            q("Was ist dir beim gemeinsamen Essen wichtiger?", "Gesund und ausgewogen", "Genuss ohne Regeln"),
            q("Was passt eher zu deinem Wochenende?", "Selbst ausgiebig kochen", "Restaurant oder bestellen")
        ),
        "h500_236_sportliche_ziele_szenario" to listOf(
            q("Ihr wollt gemeinsam fitter werden, habt aber völlig unterschiedliche Leistungsstände. Was wäre der beste Start?", "Gemeinsames realistisches Ziel", "Jeder trainiert auf eigenem Niveau", "Nur einzelne Einheiten zusammen", "Erst eine Sportart finden, die beiden Spaß macht"),
            q("Einer verliert nach zwei Wochen die Motivation. Wie sollte der andere reagieren?", "Motivieren ohne Druck", "Ziel kleiner machen", "Pause akzeptieren", "Neue Aktivität ausprobieren"),
            q("Einer trainiert sehr ehrgeizig, der andere eher zum Spaß. Was funktioniert am besten?", "Getrennte Ziele", "Gemeinsame lockere Einheiten", "Abwechselnd Tempo bestimmen", "Sport nicht zum Paarprojekt machen"),
            q("Ein sportliches Ziel kostet plötzlich viel gemeinsame Zeit. Was würdet ihr zuerst prüfen?", "Trainingsplan", "Gemeinsame Paarzeit", "Bedeutung des Ziels", "Ob es eine bessere Balance gibt"),
            q("Nach einem Rückschlag ist dein Partner frustriert. Was hilft eher?", "Zuhören", "Fortschritte erinnern", "Ziel anpassen", "Einfach gemeinsam etwas Lockeres machen"),
            q("Welche Regel wäre für gemeinsame Fitnessziele am fairsten?", "Kein gegenseitiger Druck", "Erfolge gemeinsam feiern", "Pausen sind erlaubt", "Jeder darf sein Ziel ändern")
        ),
        "h500_238_krank_sein_memory" to listOf(
            open("Wann hat dein Partner sich einmal besonders gut um dich gekümmert, als du krank oder völlig erschöpft warst?"),
            open("Was brauchst du normalerweise am meisten, wenn es dir körperlich nicht gut geht: Nähe, Ruhe, praktische Hilfe oder etwas anderes?"),
            open("Welche gut gemeinte Hilfe nervt dich eher, wenn du krank bist?"),
            open("Wie wurde bei dir zuhause früher damit umgegangen, wenn jemand krank war?"),
            open("Was sollte dein Partner über dich wissen, damit er dich in solchen Tagen gut unterstützen kann?"),
            open("Welche kleine Fürsorge bleibt dir besonders positiv im Gedächtnis?")
        ),
        "h500_239_gesunder_lebensstil_prioritaet" to listOf(
            q("Was trägt für dich am stärksten zu einem gesunden Alltag bei? Ordne.", "Guter Schlaf", "Bewegung", "Ausgewogene Ernährung", "Weniger Stress"),
            q("Welche Gewohnheit würdest du am liebsten verbessern? Ordne.", "Schlafrhythmus", "Regelmäßige Bewegung", "Kochen", "Pausen und Erholung"),
            q("Was hilft dir am ehesten, eine gesunde Gewohnheit wirklich beizubehalten? Ordne.", "Feste Routine", "Gemeinsam machen", "Messbares Ziel", "Flexibel bleiben"),
            q("Was sollte Gesundheit im Alltag für dich niemals werden? Ordne.", "Zwang", "Wettbewerb", "Dauernde Selbstkontrolle", "Quelle für Schuldgefühle"),
            q("Wofür würdest du im Alltag zuerst mehr Zeit schaffen? Ordne.", "Schlaf", "Sport", "Frisches Essen", "Mentale Erholung"),
            q("Was bedeutet ein gesunder Lebensstil für dich langfristig am meisten? Ordne.", "Energie", "Lebensqualität", "Belastbarkeit", "Sich wohlfühlen")
        ),
        "h500_241_biorhythmus_entweder_oder" to listOf(
            q("Wann hast du normalerweise mehr Energie?", "Morgens", "Abends"),
            q("Was fällt dir leichter?", "Früh aufstehen", "Lange wach bleiben"),
            q("An freien Tagen?", "Ähnlicher Rhythmus wie werktags", "Deutlich später schlafen und aufstehen"),
            q("Wann bist du für wichtige Gespräche eher aufnahmefähig?", "Früher am Tag", "Später am Abend"),
            q("Wenn ihr unterschiedliche Schlafrhythmen habt?", "Gemeinsames Ritual behalten", "Jeder folgt seinem Rhythmus"),
            q("Was wäre für dich schwieriger?", "Dauerhaft sehr früh starten", "Dauerhaft sehr spät ins Bett")
        ),
        "h500_242_gesundes_kochen_wer_eher" to listOf(
            whoQ("Wer schlägt eher ein gesundes Gericht vor, das trotzdem richtig gut schmeckt?"),
            whoQ("Wer achtet beim Einkauf eher auf frische Zutaten?"),
            whoQ("Wer probiert eher eine leichtere Version eines Lieblingsgerichts aus?"),
            whoQ("Wer plant eher Gemüse oder Salat ein, bevor nur Snacks im Wagen landen?"),
            whoQ("Wer improvisiert eher aus gesunden Resten noch ein gutes Essen?"),
            whoQ("Wer sagt eher: Heute genießen wir einfach und zählen gar nichts?"),
            whoQ("Wer entdeckt eher neue Rezepte, die beide wirklich mögen könnten?"),
            whoQ("Wer sorgt eher dafür, dass gesundes Kochen nicht in Stress ausartet?")
        ),
        "h500_247_sportarten_geheime_wahl" to listOf(
            q("Welche Sportart würdest du heimlich gern einmal ausprobieren?", "Klettern", "Tanzen", "Kampfsport", "Wassersport"),
            q("Welche Art Bewegung macht dir am ehesten wirklich Spaß?", "Teamsport", "Ausdauer", "Krafttraining", "Draußen unterwegs sein"),
            q("Was reizt dich mehr?", "Etwas mit Adrenalin", "Etwas mit Technik", "Etwas Entspanntes", "Etwas im Team"),
            q("Welche gemeinsame Aktivität könntest du dir regelmäßig vorstellen?", "Wandern", "Fitnessstudio", "Radfahren", "Tanzkurs"),
            q("Welche Sportart würdest du eher nur im Urlaub ausprobieren?", "Surfen", "Tauchen", "Skifahren", "Rafting"),
            q("Was müsste eine neue Sportart haben, damit du dranbleibst?", "Spaß", "Schnelle Fortschritte", "Gemeinschaft", "Abwechslung")
        ),
        "h500_250_gemeinsame_gesundheit_offene_runde" to listOf(
            open("Wie könnt ihr euch gegenseitig bei Gesundheit unterstützen, ohne dass es sich wie Kontrolle anfühlt?"),
            open("Welche gesunde Gewohnheit würde eurem gemeinsamen Alltag realistisch guttun?"),
            open("Wie möchtest du angesprochen werden, wenn dein Partner sich ernsthaft Sorgen um dein Wohlbefinden macht?"),
            open("Wo sollte jeder bei Gesundheit selbst entscheiden dürfen, auch wenn der andere es anders machen würde?"),
            open("Was hilft euch in stressigen Phasen dabei, Schlaf, Essen, Bewegung und Erholung nicht völlig zu vernachlässigen?"),
            open("Welche gemeinsame Veränderung wäre klein genug, dass ihr sie tatsächlich langfristig durchhalten könntet?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        when {
            SECTION_10 in pack.tags && pack.id !in keepWork -> return@mapNotNull null
            SECTION_11 in pack.tags && pack.id !in keepHealth -> return@mapNotNull null
        }

        val topic = topicOverrides[pack.id] ?: legacyVisibleTopic(pack)
        val questions = questionOverrides[pack.id] ?: pack.questions
        pack.copy(topic = topic, questions = questions)
    }

    private fun legacyVisibleTopic(pack: GenPack): String = when {
        SECTION_07 in pack.tags -> {
            if (pack.title.contains("Serie", ignoreCase = true) || pack.title.contains("Film", ignoreCase = true)) {
                "filme_serien"
            } else {
                "hobbys"
            }
        }
        SECTION_08 in pack.tags -> "familie"
        SECTION_14 in pack.tags -> {
            if (
                pack.title.contains("Streaming", ignoreCase = true) ||
                pack.title.contains("Film", ignoreCase = true) ||
                pack.title.contains("Serie", ignoreCase = true) ||
                pack.title.contains("Kino", ignoreCase = true)
            ) {
                "filme_serien"
            } else {
                "hobbys"
            }
        }
        SECTION_18 in pack.tags -> "aufwaermen"
        else -> pack.topic
    }
}
