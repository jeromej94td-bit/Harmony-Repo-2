package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360FinalScenarioContractAfterCurationTest {
    @Test
    fun `late content curations cannot shrink any scenario pack below eight decisions`() {
        val scenarios = GeneratedHarmonyAdrenaline360.PACKS.filter { pack ->
            pack.cat == "h360_szenario" || "mechanik_szenario" in pack.tags
        }

        assertTrue("Harmony 360 must expose scenario packs", scenarios.isNotEmpty())
        scenarios.forEach { pack ->
            assertEquals(
                "Scenario pack ${pack.id} must contain eight decisions after all curations",
                8,
                pack.questions.size
            )
        }
    }
}
