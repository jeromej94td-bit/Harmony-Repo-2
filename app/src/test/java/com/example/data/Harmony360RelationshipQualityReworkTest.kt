package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360RelationshipQualityReworkTest {

    private fun pack(
        id: String,
        sectionTag: String,
        question: String = "Original"
    ) = GenPack(
        id = id,
        title = "Test",
        cat = "tot",
        topic = "beziehung",
        type = "quiz",
        tags = listOf("harmony360", sectionTag),
        questions = listOf(GenQuestion(question, listOf("A", "B")))
    )

    @Test
    fun `only stage 05 1 section tags are in scope`() {
        val inScopeTags = listOf(
            "h360_section_01_beziehung_naehe",
            "h360_section_02_kommunikation",
            "h360_section_06_alltag_zuhause",
            "h360_section_12_kommunikation_konflikte"
        )

        inScopeTags.forEach { tag ->
            assertTrue(Harmony360RelationshipQualityRework.isStage051(pack("in_$tag", tag)))
        }

        assertFalse(
            Harmony360RelationshipQualityRework.isStage051(
                pack("outside", "h360_section_10_arbeit_karriere")
            )
        )
    }

    @Test
    fun `unrelated section stays value equivalent`() {
        val original = pack("outside", "h360_section_10_arbeit_karriere")
        assertEquals(listOf(original), Harmony360RelationshipQualityRework.apply(listOf(original)))
    }

    @Test
    fun `stage 05 1 pack stays unchanged when no explicit rule exists`() {
        val original = pack("keep_me", "h360_section_01_beziehung_naehe")
        assertEquals(listOf(original), Harmony360RelationshipQualityRework.apply(listOf(original)))
    }

    @Test
    fun `explicit rules archive and override only stage 05 1 packs`() {
        val archived = pack("archive_me", "h360_section_02_kommunikation")
        val overridden = pack("override_me", "h360_section_06_alltag_zuhause")
        val outsideWithArchiveId = pack("outside_archive", "h360_section_10_arbeit_karriere")
        val replacement = listOf(GenQuestion("Konkrete neue Frage", listOf("Ja", "Nein")))

        val result = Harmony360RelationshipQualityRework.applyRules(
            packs = listOf(archived, overridden, outsideWithArchiveId),
            archivedIds = setOf("archive_me", "outside_archive"),
            questionOverrides = mapOf("override_me" to replacement)
        )

        assertEquals(listOf("override_me", "outside_archive"), result.map { it.id })
        assertEquals(replacement, result.first().questions)
        assertEquals(outsideWithArchiveId, result.last())
    }

    @Test
    fun `section 01 has an explicit decision for every visible pack`() {
        val visibleIds = GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS.map { it.id }.toSet()
        assertEquals(visibleIds, Harmony360RelationshipQualityRework.section01Decisions.keys)
        assertEquals(18, visibleIds.size)
        assertEquals(
            setOf("h500_022_zaertlichkeit_wer_eher", "h500_023_verbundenheit_skala"),
            Harmony360RelationshipQualityRework.section01Decisions
                .filterValues { it == Harmony360RelationshipQualityRework.CurationDecision.ARCHIVE }
                .keys
        )
        assertEquals(Harmony360RelationshipQualityRework.CurationDecision.KEEP, Harmony360RelationshipQualityRework.section01Decisions["h500_010_vertrauen_offene_runde"])
        assertEquals(Harmony360RelationshipQualityRework.CurationDecision.KEEP, Harmony360RelationshipQualityRework.section01Decisions["h500_018_ueberraschungen_memory"])
    }

    @Test
    fun `section 01 archives overlap and keeps canonical themes`() {
        val curated = Harmony360RelationshipQualityRework.apply(GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS)
        val ids = curated.map { it.id }
        assertEquals(16, curated.size)
        assertFalse("h500_022_zaertlichkeit_wer_eher" in ids)
        assertFalse("h500_023_verbundenheit_skala" in ids)
        assertTrue("h500_004_koerpernaehe_ranking" in ids)
        assertTrue("h500_012_emotionale_sicherheit_wer_eher" in ids)
    }

    @Test
    fun `body closeness ranking is touch specific instead of generic filler`() {
        val curated = Harmony360RelationshipQualityRework.apply(GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS)
        val body = curated.single { it.id == "h500_004_koerpernaehe_ranking" }
        val allOptions = body.questions.flatMap { it.options }
        val generic = listOf("Sofort ansprechen", "Erst fühlen", "Nähe suchen", "Raum geben")
        assertTrue(body.questions.size in 6..8)
        assertFalse(body.questions.any { it.options == generic })
        assertTrue("Hand halten" in allOptions)
        assertTrue("Umarmung" in allOptions)
        assertTrue("Kuscheln" in allOptions)
        assertTrue("Nähe beim Einschlafen" in allOptions)
    }

    @Test
    fun `section 01 rewrites use concrete relationship language`() {
        val curated = Harmony360RelationshipQualityRework.apply(GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS).associateBy { it.id }
        assertTrue(curated.getValue("h500_002_quality_time_wer_eher").questions.any { "Handy" in it.q })
        assertTrue(curated.getValue("h500_003_kleine_gesten_skala").questions.any { "Lieblingssnack" in it.q })
        assertTrue(curated.getValue("h500_005_komplimente_prognose").questions.any { "Kompliment" in it.q && "Partner" in it.q })
        assertTrue(curated.getValue("h500_006_vermissen_szenario").questions.any { "Tage" in it.q || "Nachricht" in it.q })
        assertTrue(curated.getValue("h500_014_gemeinsame_rituale_ranking").questions.any { "Gute-Nacht" in it.q || "Morgen" in it.q || it.options.any { o -> "Gute-Nacht" in o || "Morgen" in o } })
        assertTrue(curated.getValue("h500_017_flirten_in_der_beziehung_geheime_wahl").questions.any { "flirten" in it.q.lowercase() })
    }

    @Test
    fun `section 01 keep decisions preserve the strong source questions`() {
        val raw = GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS.associateBy { it.id }
        val curated = Harmony360RelationshipQualityRework.apply(GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS).associateBy { it.id }
        assertEquals(raw.getValue("h500_010_vertrauen_offene_runde").questions, curated.getValue("h500_010_vertrauen_offene_runde").questions)
        assertEquals(raw.getValue("h500_018_ueberraschungen_memory").questions, curated.getValue("h500_018_ueberraschungen_memory").questions)
    }

    @Test
    fun `section 02 has an explicit decision for every visible pack`() {
        val visibleIds = GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS.map { it.id }.toSet()
        assertEquals(18, visibleIds.size)
        assertEquals(visibleIds, Harmony360RelationshipQualityRework.section02Decisions.keys)
        assertEquals(
            setOf("h500_027_missverstaendnisse_geheime_wahl"),
            Harmony360RelationshipQualityRework.section02Decisions
                .filterValues { it == Harmony360RelationshipQualityRework.CurationDecision.ARCHIVE }
                .keys
        )
        assertEquals(Harmony360RelationshipQualityRework.CurationDecision.KEEP, Harmony360RelationshipQualityRework.section02Decisions["h500_030_direkte_worte_offene_runde"])
        assertEquals(Harmony360RelationshipQualityRework.CurationDecision.KEEP, Harmony360RelationshipQualityRework.section02Decisions["h500_040_schweigen_offene_runde"])
    }

    @Test
    fun `section 02 archives absurd misunderstanding secret choice but keeps communication coverage`() {
        val curated = Harmony360RelationshipQualityRework.apply(GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS)
        val ids = curated.map { it.id }
        assertEquals(17, curated.size)
        assertFalse("h500_027_missverstaendnisse_geheime_wahl" in ids)
        assertTrue("h500_026_zuhoeren_szenario" in ids)
        assertTrue("h500_029_ehrlichkeit_prioritaet" in ids)
        assertTrue("h500_030_direkte_worte_offene_runde" in ids)
    }

    @Test
    fun `communication rewrites are concrete instead of noun substitution templates`() {
        val curated = Harmony360RelationshipQualityRework.apply(GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS).associateBy { it.id }
        val listeningOptions = curated.getValue("h500_026_zuhoeren_szenario").questions.flatMap { it.options }
        assertTrue("Nur zuhören" in listeningOptions)
        assertTrue("Fragen stellen" in listeningOptions)
        assertTrue("Gemeinsam Lösung suchen" in listeningOptions)
        assertTrue("Erst kurz Ruhe" in listeningOptions)
        assertTrue(curated.getValue("h500_029_ehrlichkeit_prioritaet").questions.any { "Zeitpunkt" in it.q || "Wahrheit" in it.q })
        assertTrue(curated.getValue("h500_032_textnachrichten_wer_eher").questions.any { "alles gut" in it.q.lowercase() || "Handy" in it.q })
        assertTrue(curated.getValue("h500_038_grenzen_erklaeren_memory").questions.any { "Grenze" in it.q && ("Nein" in it.q || "respektiert" in it.q) })
        assertTrue(curated.getValue("h500_039_humor_im_gespraech_prioritaet").questions.any { "Humor" in it.q && ("hilft" in it.q || "unpassend" in it.q) })
        assertTrue(curated.getValue("h500_044_gefuehle_benennen_ranking").questions.any { "überfordert" in it.q.lowercase() || "Gefühl" in it.q })
        assertTrue(curated.getValue("h500_049_tabuthemen_prioritaet").questions.any { "Geld" in it.options || "Eifersucht" in it.options || "Zukunft" in it.options })
    }

    @Test
    fun `section 02 keep decisions preserve strong open rounds`() {
        val raw = GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS.associateBy { it.id }
        val curated = Harmony360RelationshipQualityRework.apply(GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS).associateBy { it.id }
        assertEquals(raw.getValue("h500_030_direkte_worte_offene_runde").questions, curated.getValue("h500_030_direkte_worte_offene_runde").questions)
        assertEquals(raw.getValue("h500_040_schweigen_offene_runde").questions, curated.getValue("h500_040_schweigen_offene_runde").questions)
    }
}
