package com.example.data

/**
 * Keeps later Harmony-360 curation passes from silently deleting packs that already survived
 * the explicit Stage 05.1 and Normens deletion layers.
 *
 * Later curation may still rewrite questions, mechanics or topics. If one of those passes
 * omits a pack, the last known baseline version is restored under the same stable id.
 */
object Harmony360CurationPackPreservation {
    fun apply(baseline: List<GenPack>, curated: List<GenPack>): List<GenPack> {
        val curatedById = curated.associateBy { it.id }
        val baselineIds = baseline.mapTo(linkedSetOf()) { it.id }

        return buildList {
            baseline.forEach { pack -> add(curatedById[pack.id] ?: pack) }
            curated.filterTo(this) { it.id !in baselineIds }
        }
    }
}
