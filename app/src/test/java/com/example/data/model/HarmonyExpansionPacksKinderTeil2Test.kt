package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class HarmonyExpansionPacksKinderTeil2Test {

    @Test
    fun kinderTeil2_isPresentWithExpectedMetadataAndAnswerScheme() {
        val pack = HarmonyExpansionPacks.PACKS.firstOrNull { it.id == "kinder_teil_2" }

        assertNotNull("Kinder Teil 2 pack must exist", pack)
        pack!!
        assertEquals("Kinder Teil 2", pack.title)
        assertEquals("wer", pack.cat)
        assertEquals("familie", pack.topic)
        assertEquals("quiz", pack.type)
        assertEquals(18, pack.questions.size)
        assertTrue(pack.tags.contains("kinder"))
        assertTrue(pack.tags.contains("wer-wuerde-eher"))

        val expectedOptions = listOf("{user}", "{partner}", "Beide", "Keiner")
        pack.questions.forEach { question ->
            assertEquals(expectedOptions, question.options)
        }
    }
}
