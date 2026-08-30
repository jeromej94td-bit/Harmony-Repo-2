package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Test

class Harmony360FinalScenarioContractAfterCurationTest {
    @Test
    fun `late content curations cannot shrink scenario packs below eight decisions`() {
        val scenarioIds = listOf(
            "h500_056_auswandern_szenario",
            "h500_236_sportliche_ziele_szenario",
            "h500_366_stressreaktionen_szenario"
        )

        scenarioIds.forEach { id ->
            val pack = GeneratedHarmonyAdrenaline360.PACKS.single { it.id == id }
            assertEquals("Scenario pack $id must contain eight decisions after all curations", 8, pack.questions.size)
        }
    }
}
