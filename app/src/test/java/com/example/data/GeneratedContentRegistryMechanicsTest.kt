package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedContentRegistryMechanicsTest {

    @Test
    fun `generated deep talk bypasses the legacy discussion runner`() {
        val pack = GeneratedContentRegistry.PACKS.first {
            it.id == "h500_030_direkte_worte_offene_runde"
        }

        assertTrue("mechanik_deep_talk" in pack.tags)
        assertEquals("quiz", pack.type)
    }

    @Test
    fun `fullscreen ranking prompt does not repeat its draggable cards`() {
        val pack = GeneratedContentRegistry.PACKS.first {
            it.id == "h500_004_koerpernaehe_ranking"
        }
        val question = pack.questions.first()

        question.options.forEach { option ->
            assertFalse(
                "Prompt should not repeat option: $option",
                question.q.contains(option, ignoreCase = true)
            )
        }
    }

    @Test
    fun `scenario packs keep the eight decision journey contract`() {
        val scenarioPacks = GeneratedContentRegistry.PACKS.filter {
            "mechanik_szenario" in it.tags || it.cat == "h360_szenario"
        }

        assertTrue("Expected generated scenario packs", scenarioPacks.isNotEmpty())
        scenarioPacks.forEach { pack ->
            assertEquals("Scenario pack ${pack.id} must contain eight decisions", 8, pack.questions.size)
        }
    }
}
