package com.example.data

/**
 * Prevents the later relationship/topic passes from silently deleting whole source sections.
 *
 * The Stage 05.2 travel/food/leisure/culture quality curation remains authoritative and may
 * still archive its own low-value packs. We only restore sections that were previously being
 * lost as a side effect of relationship/topic normalization.
 */
object Harmony360CurationPackPreservation {
    private val protectedSectionTags = setOf(
        "h360_section_03_zukunft_lebensplanung",
        "h360_section_10_arbeit_karriere",
        "h360_section_11_gesundheit_fitness",
        "h360_section_13_persoenlichkeit_werte",
        "h360_section_15_glaube_religion",
        "h360_section_16_politik_gesellschaft",
        "h360_section_17_psychologie_gefuehle",
        "h360_section_19_fantasie_was_waere_wenn"
    )

    fun apply(baseline: List<GenPack>, curated: List<GenPack>): List<GenPack> {
        val curatedById = curated.associateBy { it.id }
        val baselineIds = baseline.mapTo(linkedSetOf()) { it.id }

        return buildList {
            baseline.forEach { pack ->
                val curatedPack = curatedById[pack.id]
                when {
                    curatedPack != null -> add(curatedPack)
                    shouldPreserve(pack) -> add(pack)
                }
            }
            curated.filterTo(this) { it.id !in baselineIds }
        }
    }

    internal fun shouldPreserve(pack: GenPack): Boolean =
        pack.tags.any { it in protectedSectionTags }
}
