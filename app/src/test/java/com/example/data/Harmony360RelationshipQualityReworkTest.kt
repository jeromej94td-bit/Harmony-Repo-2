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

        assertEquals(
            listOf(original),
            Harmony360RelationshipQualityRework.apply(listOf(original))
        )
    }

    @Test
    fun `stage 05 1 pack stays unchanged when no explicit rule exists`() {
        val original = pack("keep_me", "h360_section_01_beziehung_naehe")

        assertEquals(
            listOf(original),
            Harmony360RelationshipQualityRework.apply(listOf(original))
        )
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
}
