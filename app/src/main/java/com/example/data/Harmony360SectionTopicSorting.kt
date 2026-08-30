package com.example.data

import com.example.data.model.QuestionPack

/**
 * Non-destructive top-level topic routing, grouped by the real Harmony source sections.
 * This is intentionally a pure retagging pass: stable ids, questions, mechanics and order stay intact.
 */
object Harmony360SectionTopicSorting {
    private val section03Future = mapOf(
        "h500_054_traumhaus_ranking" to "kennen",
        "h500_056_auswandern_szenario" to "reisen",
        "h500_058_finanzielle_ziele_memory" to "geld",
        "h500_060_hochzeit_offene_runde" to "familie",
        "h500_061_familienplanung_entweder_oder" to "familie",
        "h500_064_abenteuerliste_ranking" to "reisen",
        "h500_066_wohnort_szenario" to "kennen",
        "h500_070_sicherheit_oder_freiheit_offene_runde" to "moral",
        "h500_075_das_leben_mit_60_prognose" to "kennen"
    )

    private val section06EverydayHome = mapOf(
        "h500_126_morgenroutine_szenario" to "kennen",
        "h500_127_abendroutine_geheime_wahl" to "kennen",
        "h500_128_haushalt_memory" to "kennen",
        "h500_129_ordnung_prioritaet" to "kennen",
        "h500_132_einkaufen_wer_eher" to "kennen",
        "h500_133_kochen_im_alltag_skala" to "essen",
        "h500_134_schlafen_ranking" to "kennen",
        "h500_135_homeoffice_prognose" to "kennen",
        "h500_136_dekoration_szenario" to "kennen",
        "h500_138_haustiere_memory" to "familie",
        "h500_141_sonntage_entweder_oder" to "hobbys",
        "h500_142_feierabend_wer_eher" to "hobbys",
        "h500_144_technik_zuhause_ranking" to "hobbys"
    )

    private val section10Work = mapOf(
        "h500_211_arbeitszeiten_entweder_oder" to "kennen",
        "h500_214_selbststaendigkeit_ranking" to "kennen",
        "h500_215_berufliche_veraenderung_prognose" to "kennen",
        "h500_216_work_life_balance_szenario" to "kennen",
        "h500_217_geheimnis_arbeitsplatz_geheime_wahl" to "kennen",
        "h500_219_berufliche_ziele_prioritaet" to "kennen",
        "h500_221_nebenjob_entweder_oder" to "kennen",
        "h500_224_arbeitsweg_ranking" to "kennen",
        "h500_225_ruhestand_prognose" to "geld",
        "h500_226_kuendigung_szenario" to "kennen",
        "h500_227_kollegen_geheime_wahl" to "kennen",
        "h500_230_beruflicher_erfolg_offene_runde" to "kennen"
    )

    private val section11Health = mapOf(
        "h500_231_ernaehrung_entweder_oder" to "essen",
        "h500_236_sportliche_ziele_szenario" to "hobbys",
        "h500_238_krank_sein_memory" to "kennen",
        "h500_239_gesunder_lebensstil_prioritaet" to "kennen",
        "h500_241_biorhythmus_entweder_oder" to "kennen",
        "h500_242_gesundes_kochen_wer_eher" to "essen",
        "h500_247_sportarten_geheime_wahl" to "hobbys",
        "h500_250_gemeinsame_gesundheit_offene_runde" to "kennen"
    )

    private val section12Conflict = mapOf(
        "h500_254_kompromisse_ranking" to "moral",
        "h500_257_geheimnisse_geheime_wahl" to "moral",
        "h500_260_ehrlichkeit_offene_runde" to "moral"
    )

    private val section17Psychology = mapOf(
        "h500_352_einfuehlungsvermoegen_wer_eher" to "kennen",
        "h500_353_verletzlichkeit_skala" to "kennen",
        "h500_357_wuensche_und_beduerfnisse_geheime_wahl" to "kennen",
        "h500_358_kindheitstraumata_memory" to "kennen",
        "h500_361_selbstwertgefuehl_entweder_oder" to "kennen",
        "h500_362_troesten_wer_eher" to "kennen",
        "h500_366_stressreaktionen_szenario" to "kennen",
        "h500_367_sehnsuechte_geheime_wahl" to "kennen"
    )

    private val legacyWeddingPacks = mapOf(
        "antrag" to "familie",
        "ringe" to "familie",
        "straeusse" to "familie",
        "traumhochzeit" to "familie"
    )

    private val topicByPackId = buildMap {
        putAll(section03Future)
        putAll(section06EverydayHome)
        putAll(section10Work)
        putAll(section11Health)
        putAll(section12Conflict)
        putAll(section17Psychology)
        putAll(legacyWeddingPacks)
    }

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map(::apply)

    fun apply(pack: GenPack): GenPack {
        val topic = topicByPackId[pack.id] ?: return pack
        return if (topic == pack.topic) pack else pack.copy(topic = topic)
    }

    fun apply(pack: QuestionPack): QuestionPack {
        val topic = topicByPackId[pack.id] ?: return pack
        return if (topic == pack.topic) pack else pack.copy(topic = topic)
    }
}
