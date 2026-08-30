package com.example.data

/**
 * Final topic routing for Harmony 360, organized by the real source sections instead of one
 * relationship-only bucket. This pass is deliberately non-destructive: it only changes topic.
 */
object Harmony360SectionTopicSorting {
    private const val SECTION_03 = "h360_section_03_zukunft_lebensplanung"
    private const val SECTION_06 = "h360_section_06_alltag_zuhause"
    private const val SECTION_10 = "h360_section_10_arbeit_karriere"
    private const val SECTION_11 = "h360_section_11_gesundheit_fitness"
    private const val SECTION_12 = "h360_section_12_kommunikation_konflikte"
    private const val SECTION_13 = "h360_section_13_persoenlichkeit_werte"
    private const val SECTION_15 = "h360_section_15_glaube_religion"
    private const val SECTION_16 = "h360_section_16_politik_gesellschaft"
    private const val SECTION_17 = "h360_section_17_psychologie_gefuehle"
    private const val SECTION_19 = "h360_section_19_fantasie_was_waere_wenn"

    private val visibleTopics = setOf(
        "aufwaermen", "beziehung", "sex", "moral", "geld", "kennen",
        "reisen", "familie", "hobbys", "filme_serien", "essen"
    )

    private val exactTopicOverrides = mapOf(
        // Existing non-360 relationship packs that are actually wedding/family content.
        "antrag" to "familie",
        "ringe" to "familie",
        "straeusse" to "familie",
        "traumhochzeit" to "familie",

        // 03 · Zukunft & Lebensplanung
        "h500_051_unser_naechstes_jahr_entweder_oder" to "beziehung",
        "h500_054_traumhaus_ranking" to "kennen",
        "h500_056_auswandern_szenario" to "reisen",
        "h500_058_finanzielle_ziele_memory" to "geld",
        "h500_060_hochzeit_offene_runde" to "familie",
        "h500_061_familienplanung_entweder_oder" to "familie",
        "h500_064_abenteuerliste_ranking" to "reisen",
        "h500_066_wohnort_szenario" to "kennen",
        "h500_070_sicherheit_oder_freiheit_offene_runde" to "moral",
        "h500_075_das_leben_mit_60_prognose" to "kennen",

        // 06 · Alltag & Zuhause. Most packs here are preferences/routines and therefore
        // belong to Kennenlernen; only clear food/leisure/relationship exceptions differ.
        "h500_133_kochen_im_alltag_skala" to "essen",
        "h500_141_sonntage_entweder_oder" to "hobbys",
        "h500_142_feierabend_wer_eher" to "hobbys",
        "h500_150_unser_gemuetlichster_abend_offene_runde" to "beziehung",

        // 10 · Arbeit & Karriere
        "h500_216_work_life_balance_szenario" to "kennen",
        "h500_220_job_und_beziehung_offene_runde" to "beziehung",
        "h500_225_ruhestand_prognose" to "geld",
        "h500_226_kuendigung_szenario" to "kennen",

        // 11 · Gesundheit & Fitness
        "h500_231_ernaehrung_entweder_oder" to "essen",
        "h500_236_sportliche_ziele_szenario" to "hobbys",
        "h500_238_krank_sein_memory" to "kennen",
        "h500_242_gesundes_kochen_wer_eher" to "essen",
        "h500_247_sportarten_geheime_wahl" to "hobbys",
        "h500_250_gemeinsame_gesundheit_offene_runde" to "kennen",

        // 12 · Kommunikation & Konflikte — relationship stays the default; value questions move.
        "h500_254_kompromisse_ranking" to "moral",
        "h500_257_geheimnisse_geheime_wahl" to "moral",
        "h500_260_ehrlichkeit_offene_runde" to "moral",

        // 13 · Persönlichkeit & Werte
        "h500_271_werte_im_alltag_entweder_oder" to "moral",
        "h500_284_moral_ranking" to "moral",
        "h500_290_sinn_des_lebens_offene_runde" to "moral",

        // 15 · Glaube & Religion
        "h500_318_religioese_erziehung_memory" to "familie",
        "h500_330_gemeinsamer_glaube_offene_runde" to "beziehung",

        // 16 · Politik & Gesellschaft
        "h500_350_gemeinsames_weltbild_offene_runde" to "beziehung",

        // 17 · Psychologie & Gefühle. Eifersucht stays a direct relationship topic.
        "h500_352_einfuehlungsvermoegen_wer_eher" to "kennen",
        "h500_353_verletzlichkeit_skala" to "kennen",
        "h500_355_eifersucht_prognose" to "beziehung",
        "h500_357_wuensche_und_beduerfnisse_geheime_wahl" to "kennen",
        "h500_358_kindheitstraumata_memory" to "kennen",
        "h500_361_selbstwertgefuehl_entweder_oder" to "kennen",
        "h500_362_troesten_wer_eher" to "kennen",
        "h500_366_stressreaktionen_szenario" to "kennen",
        "h500_367_sehnsuechte_geheime_wahl" to "kennen",

        // 19 · Fantasie / Was wäre wenn
        "h500_394_lottogewinn_ranking" to "geld",
        "h500_396_einsame_insel_szenario" to "reisen",
        "h500_398_kindheitstraum_memory" to "kennen",
        "h500_402_telepathie_wer_eher" to "beziehung",
        "h500_403_zukunftsvision_skala" to "kennen",
        "h500_407_geheime_fantasie_geheime_wahl" to "kennen",
        "h500_410_unsere_traumwelt_offene_runde" to "beziehung"
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map(::apply)

    fun apply(pack: GenPack): GenPack {
        val topic = exactTopicOverrides[pack.id] ?: sectionDefault(pack) ?: return pack
        check(topic in visibleTopics) {
            "Harmony topic sorting produced unknown visible topic '$topic' for ${pack.id}"
        }
        return if (topic == pack.topic) pack else pack.copy(topic = topic)
    }

    private fun sectionDefault(pack: GenPack): String? = when {
        SECTION_03 in pack.tags -> "kennen"
        SECTION_06 in pack.tags -> "kennen"
        SECTION_10 in pack.tags -> "kennen"
        SECTION_11 in pack.tags -> "kennen"
        SECTION_12 in pack.tags -> "beziehung"
        SECTION_13 in pack.tags -> "kennen"
        SECTION_15 in pack.tags -> "moral"
        SECTION_16 in pack.tags -> "moral"
        SECTION_17 in pack.tags -> "kennen"
        SECTION_19 in pack.tags -> "aufwaermen"
        else -> null
    }
}
