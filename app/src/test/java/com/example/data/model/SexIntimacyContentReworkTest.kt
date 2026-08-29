package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SexIntimacyContentReworkTest {

    @Test
    fun sexAndIntimacyPacks_haveDeepAndEverydayRelevantQuestions() {
        val intimacy = HarmonyPacksData.DEFAULT_PACKS.firstOrNull { it.id == "naehe" }
        val sex = HarmonyPacksData.DEFAULT_PACKS.firstOrNull { it.id == "intimleben" }

        assertNotNull("Nähe & Intimität pack must exist", intimacy)
        assertNotNull("Unser Intimleben pack must exist", sex)
        intimacy!!
        sex!!

        assertEquals("sex", intimacy.topic)
        assertEquals("sex", sex.topic)
        assertTrue("Intimacy pack should be substantially expanded", intimacy.questions.size >= 12)
        assertTrue("Sex pack should be substantially expanded", sex.questions.size >= 12)

        assertTrue(
            intimacy.questions.any { it.q.contains("geliebt", ignoreCase = true) && it.q.contains("begehrt", ignoreCase = true) }
        )
        assertTrue(
            intimacy.questions.any { it.q.contains("Grenze", ignoreCase = true) || it.q.contains("Wunsch", ignoreCase = true) }
        )
        assertTrue(
            sex.questions.any { it.q.contains("Tageszeit", ignoreCase = true) && it.q.contains("Sex", ignoreCase = true) }
        )
        assertTrue(
            sex.questions.any { it.q.contains("initiieren", ignoreCase = true) || it.q.contains("Initiative", ignoreCase = true) }
        )
        assertTrue(
            sex.questions.any { it.q.contains("Vorspiel", ignoreCase = true) }
        )
        assertTrue(
            sex.questions.any { it.q.contains("neue", ignoreCase = true) && it.q.contains("Sex", ignoreCase = true) }
        )
        assertTrue(
            sex.questions.any { it.q.contains("Dirty Talk", ignoreCase = true) || it.q.contains("erotischen Worten", ignoreCase = true) }
        )
        assertTrue(
            sex.questions.any { it.q.contains("erotische Inhalte", ignoreCase = true) }
        )

        assertFalse(sex.questions.any { it.q == "Beschreibe unser Sexleben mit einem Emoji." })
        assertFalse(sex.questions.any { it.q == "Sind wir beim Sex eher laut oder leise?" })
    }
}
