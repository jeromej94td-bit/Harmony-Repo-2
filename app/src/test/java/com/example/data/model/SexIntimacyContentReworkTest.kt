package com.example.data.model

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SexIntimacyContentReworkTest {

    @Test
    fun sexAndIntimacyPacks_haveDeepAndEverydayRelevantQuestions() {
        val intimacy = GeneratedContentRegistry.PACKS.firstOrNull { it.id == "naehe" }
        val sex = GeneratedContentRegistry.PACKS.firstOrNull { it.id == "intimleben" }

        assertNotNull("Nähe & Intimität rework pack must exist", intimacy)
        assertNotNull("Unser Intimleben rework pack must exist", sex)
        intimacy!!
        sex!!

        assertEquals("sex", intimacy.topic)
        assertEquals("sex", sex.topic)
        assertTrue("Intimacy pack should be substantially expanded", intimacy.questions.size >= 12)
        assertTrue("Sex pack should contain the richer everyday progression", sex.questions.size >= 18)

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
        assertTrue(sex.questions.any { it.q.contains("ideal", ignoreCase = true) && it.q.contains("wie oft", ignoreCase = true) })
        assertTrue(sex.questions.any { it.q.contains("Vorspiel", ignoreCase = true) })
        assertTrue(sex.questions.any { it.q.contains("Lust am schnellsten verschwinden", ignoreCase = true) })
        assertTrue(sex.questions.any { it.q.contains("keine Lust", ignoreCase = true) && it.q.contains("leichter", ignoreCase = true) })
        assertTrue(sex.questions.any { it.q.contains("spontan entsteht", ignoreCase = true) && it.q.contains("bewusst Zeit", ignoreCase = true) })
        assertTrue(sex.questions.any { it.q.contains("merken", ignoreCase = true) && it.q.contains("Lust auf dich", ignoreCase = true) })
        assertTrue(sex.questions.any { it.q.contains("Thema rund um Sex", ignoreCase = true) && it.q.contains("öfter sprechen", ignoreCase = true) })
        assertTrue(
            sex.questions.any { it.q.contains("neue", ignoreCase = true) && it.q.contains("Sex", ignoreCase = true) }
        )
        assertTrue(
            sex.questions.any { it.q.contains("Dirty Talk", ignoreCase = true) || it.q.contains("erotischen Worten", ignoreCase = true) }
        )
        assertTrue(sex.questions.any { it.q.contains("erotische Inhalte", ignoreCase = true) })

        val texts = sex.questions.map { it.q }
        val frequencyIndex = texts.indexOfFirst { it.contains("wie oft", ignoreCase = true) && it.contains("ideal", ignoreCase = true) }
        val experimentationIndex = texts.indexOfFirst { it.contains("neue Dinge", ignoreCase = true) }
        val communicationIndex = texts.indexOfFirst { it.contains("keine Lust", ignoreCase = true) && it.contains("leichter", ignoreCase = true) }
        val dirtyTalkIndex = texts.indexOfFirst { it.contains("Dirty Talk", ignoreCase = true) }
        assertTrue("Everyday desire should come before experimentation", frequencyIndex in 0 until experimentationIndex)
        assertTrue("Communication should come before more explicit experimentation", communicationIndex in 0 until dirtyTalkIndex)

        assertFalse(sex.questions.any { it.q == "Beschreibe unser Sexleben mit einem Emoji." })
        assertFalse(sex.questions.any { it.q == "Sind wir beim Sex eher laut oder leise?" })
        assertFalse(sex.questions.any { it.q == "Wie wichtig ist dir Spontanität bei Sex?" })
    }
}
