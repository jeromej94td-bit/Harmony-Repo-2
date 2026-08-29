package com.example.data

/**
 * Stage 05.2 curation boundary for food, travel, leisure and culture content.
 *
 * The generated section files stay untouched as raw source. Concrete Keep / Rewrite /
 * Archive decisions are added in later narrow 25.x packages; until then this transform
 * is intentionally value-preserving.
 */
object Harmony360FoodTravelLeisureCultureQualityRework {
    internal val sectionTags: Set<String> = setOf(
        "h360_section_04_reisen_abenteuer",
        "h360_section_05_essen_genuss",
        "h360_section_07_freizeit_hobbys",
        "h360_section_14_kultur_medien"
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map { pack ->
        if (pack.tags.any(sectionTags::contains)) pack else pack
    }
}
