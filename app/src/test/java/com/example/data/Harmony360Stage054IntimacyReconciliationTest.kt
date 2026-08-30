package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage054IntimacyReconciliationTest {
    @Test
    fun `intimacy rework keeps stable ids topic and curated question counts`() {
        val packs = GeneratedHarmonySexIntimacyRework.PACKS
        assertEquals(listOf("naehe", "intimleben"), packs.map { it.id })
        assertTrue(packs.all { it.topic == "sex" })
        assertEquals(12, packs.single { it.id == "naehe" }.questions.size)
        assertEquals(18, packs.single { it.id == "intimleben" }.questions.size)
    }

    @Test
    fun `intimacy rework contains concrete boundaries and no pressure language`() {
        val closeness = GeneratedHarmonySexIntimacyRework.PACKS.single { it.id == "naehe" }
        val intimacy = GeneratedHarmonySexIntimacyRework.PACKS.single { it.id == "intimleben" }

        assertTrue(closeness.questions.any { question ->
            question.q.contains("Grenze", ignoreCase = true) ||
                question.options.any { it.contains("Grenze", ignoreCase = true) }
        })
        assertTrue(intimacy.questions.any { question ->
            question.q.contains("keine Lust", ignoreCase = true)
        })
        assertTrue(intimacy.questions.flatMap { it.options }.any {
            it.contains("ohne Druck", ignoreCase = true) || it.contains("Nein", ignoreCase = true)
        })
    }

    @Test
    fun `intimacy rework has no known generic generator quartets`() {
        val forbidden = setOf(
            listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            listOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            listOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
            listOf("Mehr Mut", "Mehr Ehrlichkeit", "Mehr Spontanität", "Mehr Rücksicht")
        )
        assertFalse(GeneratedHarmonySexIntimacyRework.PACKS
            .flatMap { it.questions }
            .any { it.options in forbidden })
    }

    @Test
    fun `runtime registry exposes one final curated pack for each stable intimacy id`() {
        val runtime = GeneratedContentRegistry.PACKS
        val curated = GeneratedHarmonySexIntimacyRework.PACKS.associateBy { it.id }

        listOf("naehe", "intimleben").forEach { id ->
            val matches = runtime.filter { it.id == id }
            assertEquals(1, matches.size)
            assertEquals(curated.getValue(id).title, matches.single().title)
            assertEquals(curated.getValue(id).questions, matches.single().questions)
            assertEquals("sex", matches.single().topic)
        }
    }
}
