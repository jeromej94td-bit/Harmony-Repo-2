package com.example.data

/**
 * Final list-level curation layer for 360 Rework Stage 05.1.
 *
 * The large generated section files remain untouched. This layer is intentionally explicit:
 * only relationship-facing Sections 01, 02, 06 and 12 may be archived or manually overridden.
 * Later Stage-05.1 slices populate the rule collections with reviewed content decisions.
 */
object Harmony360RelationshipQualityRework {

    private val stage051SectionTags = setOf(
        "h360_section_01_beziehung_naehe",
        "h360_section_02_kommunikation",
        "h360_section_06_alltag_zuhause",
        "h360_section_12_kommunikation_konflikte"
    )

    private val archivedIds: Set<String> = emptySet()
    private val questionOverrides: Map<String, List<GenQuestion>> = emptyMap()

    internal fun isStage051(pack: GenPack): Boolean =
        pack.tags.any(stage051SectionTags::contains)

    fun apply(packs: List<GenPack>): List<GenPack> =
        applyRules(
            packs = packs,
            archivedIds = archivedIds,
            questionOverrides = questionOverrides
        )

    internal fun applyRules(
        packs: List<GenPack>,
        archivedIds: Set<String>,
        questionOverrides: Map<String, List<GenQuestion>>
    ): List<GenPack> = packs.mapNotNull { pack ->
        if (!isStage051(pack)) {
            pack
        } else if (pack.id in archivedIds) {
            null
        } else {
            questionOverrides[pack.id]?.let { questions ->
                pack.copy(questions = questions)
            } ?: pack
        }
    }
}
