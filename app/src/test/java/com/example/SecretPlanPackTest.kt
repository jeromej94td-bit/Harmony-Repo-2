package com.example

import com.example.data.model.HarmonyExpansionPacks
import com.example.data.model.SecretPlanCatalog
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SecretPlanPackTest {
    @Test
    fun `secret plan pack has the three approved chapters and custom card`() {
        val pack = HarmonyExpansionPacks.PACKS.single { it.id == SecretPlanCatalog.PACK_ID }

        assertEquals(3, pack.questions.size)
        assertEquals("Wofür nehmt ihr euch spontan einen freien Tag?", pack.questions[0].q)
        assertEquals("Eigene Idee …", pack.questions[0].options.last())
        assertEquals("Welche gemeinsame Idee sollte endlich passieren?", pack.questions[2].q)
        assertTrue(pack.tags.contains("mechanik_geheime_wahl"))
    }
}
