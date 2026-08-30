package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360NeedNowQuickGameTest {

    @Test
    fun `quick game is one relationship pack with ten two-choice questions`() {
        val pack = Harmony360NeedNowQuickGame.PACK

        assertEquals("h360_need_now_quick", pack.id)
        assertEquals("beziehung", pack.topic)
        assertEquals("tot", pack.cat)
        assertEquals("quiz", pack.type)
        assertEquals(10, pack.questions.size)
        assertTrue(pack.questions.all { it.q.isNotBlank() })
        assertTrue(pack.questions.all { it.options.size == 2 })
        assertTrue(pack.questions.flatMap { it.options }.all { it.isNotBlank() })
    }

    @Test
    fun `quick game covers concrete current-needs situations`() {
        val questions = Harmony360NeedNowQuickGame.PACK.questions
        val text = questions.joinToString(" ") { it.q + " " + it.options.joinToString(" ") }

        assertTrue("miesen Tag" in text)
        assertTrue("Problem" in text)
        assertTrue("Streit" in text)
        assertTrue("übersehen" in text)
        assertTrue("stiller" in text)
        assertTrue("gestresst" in text)
        assertTrue("zweifelst" in text)
        assertTrue("Nähe" in text)
        assertTrue("entscheiden" in text)
        assertTrue("hungrig" in text)
    }

    @Test
    fun `append replaces an existing same-id pack instead of duplicating it`() {
        val stale = Harmony360NeedNowQuickGame.PACK.copy(
            title = "Alte Version",
            questions = listOf(GenQuestion("Alt", listOf("A", "B")))
        )
        val other = GenPack(
            id = "other",
            title = "Other",
            cat = "tot",
            topic = "beziehung",
            type = "quiz",
            questions = listOf(GenQuestion("Andere Frage", listOf("A", "B")))
        )

        val result = Harmony360NeedNowQuickGame.appendTo(listOf(other, stale))

        assertEquals(2, result.size)
        assertEquals(1, result.count { it.id == "h360_need_now_quick" })
        assertEquals("Other", result.first().title)
        assertFalse(result.single { it.id == "h360_need_now_quick" }.title == "Alte Version")
    }

    @Test
    fun `stage 05 1 pipeline registers quick game exactly once`() {
        val result = Harmony360RelationshipStage051Pipeline.apply(emptyList())

        assertEquals(1, result.count { it.id == "h360_need_now_quick" })
        assertEquals(Harmony360NeedNowQuickGame.PACK, result.single())
    }
}
