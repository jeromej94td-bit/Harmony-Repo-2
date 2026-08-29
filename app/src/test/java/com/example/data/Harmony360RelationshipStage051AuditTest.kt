package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360RelationshipStage051AuditTest {

    private val archivedIds = setOf(
        "h500_022_zaertlichkeit_wer_eher",
        "h500_023_verbundenheit_skala",
        "h500_027_missverstaendnisse_geheime_wahl",
        "h500_148_wohnzimmer_memory",
        "h500_149_balkon_prioritaet",
        "h500_259_gespraechsthemen_prioritaet"
    )

    private fun targetRawPacks(): List<GenPack> = buildList {
        addAll(GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS)
        addAll(GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS)
    }

    private fun finalTargetPacks(): List<GenPack> =
        NormensLoeschungen.apply(
            Harmony360RelationshipStage051Pipeline.apply(targetRawPacks())
        )

    @Test
    fun `all four target sections have explicit decisions and expected curated sizes`() {
        val section01Raw = GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS
        val section02Raw = GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS
        val section06Raw = GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS
        val section12Raw = GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS

        assertEquals(18, section01Raw.size)
        assertEquals(18, section02Raw.size)
        assertEquals(18, section06Raw.size)
        assertEquals(18, section12Raw.size)
        assertEquals(section01Raw.map { it.id }.toSet(), Harmony360RelationshipQualityRework.section01Decisions.keys)
        assertEquals(section02Raw.map { it.id }.toSet(), Harmony360RelationshipQualityRework.section02Decisions.keys)
        assertEquals(section06Raw.map { it.id }.toSet(), Harmony360RelationshipSection06Curation.decisions.keys)
        assertEquals(section12Raw.map { it.id }.toSet(), Harmony360RelationshipSection12Curation.decisions.keys)

        assertEquals(16, Harmony360RelationshipQualityRework.apply(section01Raw).size)
        assertEquals(17, Harmony360RelationshipQualityRework.apply(section02Raw).size)
        assertEquals(16, Harmony360RelationshipSection06Curation.apply(section06Raw).size)
        assertEquals(17, Harmony360RelationshipSection12Curation.apply(section12Raw).size)
    }

    @Test
    fun `final target list has expected size no duplicate ids and no archived packs`() {
        val finalPacks = finalTargetPacks()
        val ids = finalPacks.map { it.id }

        assertEquals(67, finalPacks.size)
        assertEquals(ids.size, ids.toSet().size)
        archivedIds.forEach { archivedId -> assertFalse(archivedId in ids) }
        assertEquals(1, ids.count { it == "h360_need_now_quick" })
    }

    @Test
    fun `canonical relationship communication everyday and repair packs remain visible`() {
        val ids = finalTargetPacks().map { it.id }.toSet()
        val canonicalIds = setOf(
            "h500_004_koerpernaehe_ranking",
            "h500_010_vertrauen_offene_runde",
            "h500_026_zuhoeren_szenario",
            "h500_030_direkte_worte_offene_runde",
            "h500_126_morgenroutine_szenario",
            "h500_128_haushalt_memory",
            "h500_134_schlafen_ranking",
            "h500_256_missverstaendnisse_szenario",
            "h500_270_versoehnung_offene_runde",
            "h360_need_now_quick"
        )

        assertTrue(ids.containsAll(canonicalIds))
    }

    @Test
    fun `quick game remains exactly ten valid two-choice questions`() {
        val pack = finalTargetPacks().single { it.id == "h360_need_now_quick" }

        assertEquals(10, pack.questions.size)
        assertTrue(pack.questions.all { it.q.isNotBlank() })
        assertTrue(pack.questions.all { it.options.size == 2 })
        assertTrue(pack.questions.flatMap { it.options }.all { it.isNotBlank() })
    }

    @Test
    fun `curated conflict output has no known accidental english ranking template`() {
        val curated = Harmony360RelationshipSection12Curation.apply(
            GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS
        )

        assertFalse(
            curated.flatMap { it.questions }
                .any { it.q.contains("What decides whether", ignoreCase = true) }
        )
    }

    @Test
    fun `stage 05 1 pipeline preserves unrelated packs and only appends the quick game`() {
        val unrelated = GenPack(
            id = "outside_stage_05_1",
            title = "Outside",
            cat = "tot",
            topic = "arbeit",
            type = "quiz",
            tags = listOf("harmony360", "h360_section_10_arbeit_karriere"),
            questions = listOf(GenQuestion("Unverändert", listOf("A", "B")))
        )

        val result = Harmony360RelationshipStage051Pipeline.apply(listOf(unrelated))

        assertEquals(2, result.size)
        assertEquals(unrelated, result.first())
        assertEquals("h360_need_now_quick", result.last().id)
    }
}
