package com.example.data

/** Explicit Stage 05.4 curation for Harmony-360 Section 11 — Gesundheit & Fitness. */
object Harmony360HealthFitnessSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun open(text: String): GenQuestion = GenQuestion(q = text)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_231_ernaehrung_entweder_oder" to CurationDecision.REWRITE,
        "h500_232_schlafgewohnheiten_wer_eher" to CurationDecision.ARCHIVE,
        "h500_233_mental_health_skala" to CurationDecision.ARCHIVE,
        "h500_234_arztbesuche_ranking" to CurationDecision.ARCHIVE,
        "h500_235_stressbewaeltigung_prognose" to CurationDecision.ARCHIVE,
        "h500_236_sportliche_ziele_szenario" to CurationDecision.REWRITE,
        "h500_237_wellness_und_spa_geheime_wahl" to CurationDecision.ARCHIVE,
        "h500_238_krank_sein_memory" to CurationDecision.REWRITE,
        "h500_239_gesunder_lebensstil_prioritaet" to CurationDecision.REWRITE,
        "h500_240_koerpergefuehl_offene_runde" to CurationDecision.ARCHIVE,
        "h500_241_biorhythmus_entweder_oder" to CurationDecision.REWRITE,
        "h500_242_gesundes_kochen_wer_eher" to CurationDecision.REWRITE,
        "h500_243_routinen_skala" to CurationDecision.ARCHIVE,
        "h500_244_vorsorge_ranking" to CurationDecision.ARCHIVE,
        "h500_245_suchtmittel_prognose" to CurationDecision.ARCHIVE,
        "h500_246_regeneration_szenario" to CurationDecision.ARCHIVE,
        "h500_247_sportarten_geheime_wahl" to CurationDecision.REWRITE,
        "h500_250_gemeinsame_gesundheit_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_231_ernaehrung_entweder_oder" to listOf(
            q("Was passt im Alltag eher zu dir?", "Frisch kochen", "Schnell und unkompliziert"),
            q("Worauf achtest du beim Essen eher?", "Geschmack", "Ausgewogenheit"),
            q("Was ist dir lieber?", "Feste Essenszeiten", "Essen nach Hunger"),
            q("Wenn du dich entscheiden musst?", "Bewährtes Lieblingsessen", "Etwas Neues probieren"),
            q("Was ist dir beim gemeinsamen Essen wichtiger?", "Bewusst und ausgewogen", "Genuss ohne starre Regeln"),
            q("Was passt eher zu deinem Wochenende?", "Selbst ausgiebig kochen", "Restaurant oder bestellen")
        ),
        "h500_236_sportliche_ziele_szenario" to listOf(
            q("Ihr möchtet gemeinsam aktiver werden, habt aber sehr unterschiedliche Leistungsstände. Was wäre ein guter Start ohne Druck?", "Gemeinsames realistisches Ziel", "Jeder auf eigenem Niveau", "Nur einzelne Einheiten zusammen", "Erst etwas finden, das beiden Spaß macht"),
            q("Einer verliert nach kurzer Zeit die Motivation. Wie reagiert der andere am besten?", "Motivieren ohne Druck", "Ziel kleiner machen", "Pause akzeptieren", "Andere Aktivität ausprobieren"),
            q("Einer trainiert ehrgeizig, der andere nur zum Spaß. Was funktioniert am ehesten?", "Getrennte Ziele", "Gemeinsame lockere Einheiten", "Abwechselnd Tempo bestimmen", "Sport nicht zum Paarprojekt machen"),
            q("Ein sportliches Ziel kostet plötzlich viel gemeinsame Zeit. Was prüft ihr zuerst?", "Trainingsplan", "Gemeinsame Paarzeit", "Bedeutung des Ziels", "Ob eine bessere Balance möglich ist"),
            q("Nach einem Rückschlag ist dein Partner frustriert. Was hilft eher?", "Zuhören", "An Fortschritte erinnern", "Ziel anpassen", "Gemeinsam etwas Lockeres machen"),
            q("Welche Regel wäre für gemeinsame Fitnessziele am fairsten?", "Kein gegenseitiger Druck", "Erfolge gemeinsam feiern", "Pausen sind erlaubt", "Jeder darf sein Ziel ändern")
        ),
        "h500_238_krank_sein_memory" to listOf(
            open("Wann hat dein Partner dich einmal besonders gut unterstützt, als du krank oder völlig erschöpft warst?"),
            open("Was brauchst du normalerweise am meisten, wenn es dir körperlich nicht gut geht: Ruhe, Nähe, praktische Hilfe oder etwas anderes?"),
            open("Welche gut gemeinte Hilfe nervt dich eher, wenn du krank bist?"),
            open("Wie wurde bei dir zuhause früher damit umgegangen, wenn jemand krank war?"),
            open("Was sollte dein Partner über dich wissen, um dich an solchen Tagen passend zu unterstützen?"),
            open("Welche kleine Form von Fürsorge ist dir besonders positiv im Gedächtnis geblieben?")
        ),
        "h500_239_gesunder_lebensstil_prioritaet" to listOf(
            q("Was trägt für dich am stärksten zu einem guten Alltag bei?", "Guter Schlaf", "Bewegung", "Ausgewogene Ernährung", "Weniger Stress"),
            q("Welche Gewohnheit würdest du am liebsten verbessern?", "Schlafrhythmus", "Regelmäßige Bewegung", "Kochen", "Pausen und Erholung"),
            q("Was hilft dir am ehesten, eine gute Gewohnheit beizubehalten?", "Feste Routine", "Gemeinsam machen", "Kleines realistisches Ziel", "Flexibel bleiben"),
            q("Was sollte ein gesunder Lebensstil für dich niemals werden?", "Zwang", "Wettbewerb", "Dauernde Selbstkontrolle", "Quelle für Schuldgefühle"),
            q("Wofür würdest du im Alltag zuerst mehr Zeit schaffen?", "Schlaf", "Bewegung", "Frisches Essen", "Mentale Erholung"),
            q("Was bedeutet Wohlbefinden für dich langfristig am meisten?", "Energie", "Lebensqualität", "Belastbarkeit", "Sich wohlfühlen")
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
            whoQ("Wer schlägt eher ein ausgewogenes Gericht vor, das trotzdem richtig gut schmeckt?"),
            whoQ("Wer achtet beim Einkauf eher auf frische Zutaten?"),
            whoQ("Wer probiert eher eine leichtere Variante eines Lieblingsgerichts aus?"),
            whoQ("Wer plant eher Gemüse oder Salat ein, bevor nur Snacks im Wagen landen?"),
            whoQ("Wer improvisiert eher aus frischen Resten noch ein gutes Essen?"),
            whoQ("Wer sorgt eher dafür, dass bewusstes Kochen nicht in Stress oder starre Regeln ausartet?")
        ),
        "h500_247_sportarten_geheime_wahl" to listOf(
            q("Welche Sportart würdest du heimlich gern einmal ausprobieren?", "Klettern", "Tanzen", "Kampfsport", "Wassersport"),
            q("Welche Art Bewegung macht dir am ehesten wirklich Spaß?", "Teamsport", "Ausdauer", "Krafttraining", "Draußen unterwegs sein"),
            q("Was reizt dich mehr?", "Etwas mit Adrenalin", "Etwas mit Technik", "Etwas Entspanntes", "Etwas im Team"),
            q("Welche gemeinsame Aktivität könntest du dir regelmäßig vorstellen?", "Wandern", "Fitnessstudio", "Radfahren", "Tanzkurs"),
            q("Welche Sportart würdest du eher nur im Urlaub ausprobieren?", "Surfen", "Tauchen", "Skifahren", "Rafting"),
            q("Was müsste eine neue Sportart haben, damit du dranbleibst?", "Spaß", "Spürbare Fortschritte", "Gemeinschaft", "Abwechslung")
        ),
        "h500_250_gemeinsame_gesundheit_offene_runde" to listOf(
            open("Wie könnt ihr euch gegenseitig beim Wohlbefinden unterstützen, ohne dass es sich wie Kontrolle anfühlt?"),
            open("Welche gute Gewohnheit würde eurem gemeinsamen Alltag realistisch guttun?"),
            open("Wie möchtest du angesprochen werden, wenn dein Partner sich ernsthaft Sorgen um dein Wohlbefinden macht?"),
            open("Wo sollte jeder bei persönlichen Gesundheitsentscheidungen selbst bestimmen dürfen, auch wenn der andere es anders machen würde?"),
            open("Was hilft euch in stressigen Phasen dabei, Schlaf, Essen, Bewegung und Erholung nicht völlig zu vernachlässigen?"),
            open("Welche gemeinsame Veränderung wäre klein genug, dass ihr sie tatsächlich langfristig durchhalten könntet?")
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
