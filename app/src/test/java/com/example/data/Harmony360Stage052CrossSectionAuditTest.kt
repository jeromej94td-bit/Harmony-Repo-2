package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage052CrossSectionAuditTest {

    @Test
    fun `all 72 curated packs survive runtime with unique stable ids`() {
        val packs = Harmony360Stage052CrossSectionAudit.stagePacks(GeneratedHarmonyAdrenaline360.PACKS)

        assertEquals(72, packs.size)
        assertEquals(72, packs.map { it.id }.distinct().size)
        assertEquals(
            setOf(
                "h360_section_04_reisen_abenteuer",
                "h360_section_05_essen_genuss",
                "h360_section_07_freizeit_hobbys",
                "h360_section_14_kultur_medien"
            ),
            packs.flatMap { pack -> pack.tags.filter { it.startsWith("h360_section_") } }.toSet()
        )
    }

    @Test
    fun `curated stage has no banned generator residue`() {
        val violations = Harmony360Stage052CrossSectionAudit.audit(GeneratedHarmonyAdrenaline360.PACKS)

        assertTrue(
            "Stage 05.2 quality violations:\n${violations.joinToString("\n")}",
            violations.isEmpty()
        )
    }

    @Test
    fun `audit catches generic quartet english residue typo and excessive unrelated option reuse`() {
        val fixture = listOf(
            pack(
                id = "a",
                tag = "h360_section_04_reisen_abenteuer",
                question = GenQuestion(
                    "What decides whether this works?",
                    listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort")
                )
            ),
            pack(
                id = "b",
                tag = "h360_section_05_essen_genuss",
                question = GenQuestion("Was givst du?", listOf("A", "B", "C", "D"))
            ),
            pack(
                id = "c",
                tag = "h360_section_07_freizeit_hobbys",
                question = GenQuestion("Frage C", listOf("A", "B", "C", "D"))
            ),
            pack(
                id = "d",
                tag = "h360_section_14_kultur_medien",
                question = GenQuestion("Frage D", listOf("A", "B", "C", "D"))
            )
        )

        val violations = Harmony360Stage052CrossSectionAudit.audit(fixture)

        assertTrue(violations.any { it.contains("generic quartet") })
        assertTrue(violations.any { it.contains("English generator residue") })
        assertTrue(violations.any { it.contains("known typo") })
        assertTrue(violations.any { it.contains("reused 4-option set") })
    }

    @Test
    fun `intentional mechanic option sets are not treated as unrelated reuse`() {
        val fixture = listOf(
            pack(
                id = "who_a",
                tag = "h360_section_04_reisen_abenteuer",
                question = GenQuestion("Wer?", listOf("{user}", "{partner}", "Beide", "Niemand"))
            ),
            pack(
                id = "who_b",
                tag = "h360_section_05_essen_genuss",
                question = GenQuestion("Wer noch?", listOf("{user}", "{partner}", "Beide", "Niemand"))
            ),
            pack(
                id = "who_c",
                tag = "h360_section_14_kultur_medien",
                question = GenQuestion("Wer wieder?", listOf("{user}", "{partner}", "Beide", "Niemand"))
            )
        )

        assertTrue(Harmony360Stage052CrossSectionAudit.audit(fixture).isEmpty())
    }

    private fun pack(id: String, tag: String, question: GenQuestion): GenPack = GenPack(
        id = id,
        title = id,
        cat = "test",
        topic = "test",
        type = "quiz",
        tags = listOf("harmony360", tag),
        questions = listOf(question)
    )
}
