package com.example.data

import com.example.data.model.QuestionPack

/** Central app-facing placement rules for packs that otherwise overload Beziehung. */
object TopLevelTopicPlacementPolicy {
    private val topicByPackId = mapOf(
        "antrag" to "familie",
        "ringe" to "familie",
        "straeusse" to "familie",
        "traumhochzeit" to "familie",
        "h500_054_traumhaus_ranking" to "kennen",
        "h500_060_hochzeit_offene_runde" to "familie",
        "h500_061_familienplanung_entweder_oder" to "familie",
        "h500_066_wohnort_szenario" to "kennen",
        "h500_070_sicherheit_oder_freiheit_offene_runde" to "moral",
        "h500_075_das_leben_mit_60_prognose" to "kennen",
        "h500_126_morgenroutine_szenario" to "kennen",
        "h500_127_abendroutine_geheime_wahl" to "kennen",
        "h500_128_haushalt_memory" to "kennen",
        "h500_129_ordnung_prioritaet" to "kennen",
        "h500_132_einkaufen_wer_eher" to "kennen",
        "h500_134_schlafen_ranking" to "kennen",
        "h500_135_homeoffice_prognose" to "kennen",
        "h500_136_dekoration_szenario" to "kennen",
        "h500_138_haustiere_memory" to "familie",
        "h500_141_sonntage_entweder_oder" to "hobbys",
        "h500_142_feierabend_wer_eher" to "hobbys",
        "h500_144_technik_zuhause_ranking" to "hobbys",
        "h500_216_work_life_balance_szenario" to "kennen",
        "h500_225_ruhestand_prognose" to "geld",
        "h500_226_kuendigung_szenario" to "kennen",
        "h500_238_krank_sein_memory" to "kennen",
        "h500_250_gemeinsame_gesundheit_offene_runde" to "kennen",
        "h500_254_kompromisse_ranking" to "moral",
        "h500_257_geheimnisse_geheime_wahl" to "moral",
        "h500_260_ehrlichkeit_offene_runde" to "moral",
        "h500_352_einfuehlungsvermoegen_wer_eher" to "kennen",
        "h500_353_verletzlichkeit_skala" to "kennen",
        "h500_357_wuensche_und_beduerfnisse_geheime_wahl" to "kennen",
        "h500_362_troesten_wer_eher" to "kennen",
        "h500_366_stressreaktionen_szenario" to "kennen",
        "h500_367_sehnsuechte_geheime_wahl" to "kennen"
    )

    private val defaultPackIds = setOf("antrag", "ringe", "straeusse", "traumhochzeit")

    fun topicFor(packId: String): String? = topicByPackId[packId]
    fun isDefaultPackOverride(packId: String): Boolean = packId in defaultPackIds

    fun apply(pack: GenPack): GenPack {
        val topic = topicFor(pack.id) ?: return pack
        return if (pack.topic == topic) pack else pack.copy(topic = topic)
    }

    fun apply(pack: QuestionPack): QuestionPack {
        val topic = topicFor(pack.id) ?: return pack
        return if (pack.topic == topic) pack else pack.copy(topic = topic)
    }
}
