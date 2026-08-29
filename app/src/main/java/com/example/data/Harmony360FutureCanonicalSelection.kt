package com.example.data

/**
 * Canonical visibility contract for Stage 05.3a / Section 03.
 *
 * `Harmony360FutureSectionCuration` may author strong replacement content for every raw source
 * pack, but the previously approved relationship/topic cleanup intentionally exposes only ten
 * distinct future packs at runtime. Keeping that visibility decision separate prevents a future
 * rewrite pass from accidentally resurrecting the eight archived overlaps.
 */
object Harmony360FutureCanonicalSelection {
    internal enum class VisibilityDecision { KEEP, ARCHIVE }

    internal val decisions: Map<String, VisibilityDecision> = linkedMapOf(
        "h500_051_unser_naechstes_jahr_entweder_oder" to VisibilityDecision.KEEP,
        "h500_052_in_fuenf_jahren_wer_eher" to VisibilityDecision.ARCHIVE,
        "h500_053_traumwohnung_skala" to VisibilityDecision.ARCHIVE,
        "h500_054_traumhaus_ranking" to VisibilityDecision.KEEP,
        "h500_055_stadt_oder_land_prognose" to VisibilityDecision.ARCHIVE,
        "h500_056_auswandern_szenario" to VisibilityDecision.KEEP,
        "h500_057_karriereplaene_geheime_wahl" to VisibilityDecision.ARCHIVE,
        "h500_058_finanzielle_ziele_memory" to VisibilityDecision.KEEP,
        "h500_060_hochzeit_offene_runde" to VisibilityDecision.KEEP,
        "h500_061_familienplanung_entweder_oder" to VisibilityDecision.KEEP,
        "h500_062_lebensstil_wer_eher" to VisibilityDecision.ARCHIVE,
        "h500_064_abenteuerliste_ranking" to VisibilityDecision.KEEP,
        "h500_065_bucket_list_prognose" to VisibilityDecision.ARCHIVE,
        "h500_066_wohnort_szenario" to VisibilityDecision.KEEP,
        "h500_067_prioritaeten_geheime_wahl" to VisibilityDecision.ARCHIVE,
        "h500_069_selbststaendigkeit_prioritaet" to VisibilityDecision.ARCHIVE,
        "h500_070_sicherheit_oder_freiheit_offene_runde" to VisibilityDecision.KEEP,
        "h500_075_das_leben_mit_60_prognose" to VisibilityDecision.KEEP
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.filter { pack ->
        decisions[pack.id] != VisibilityDecision.ARCHIVE
    }
}
