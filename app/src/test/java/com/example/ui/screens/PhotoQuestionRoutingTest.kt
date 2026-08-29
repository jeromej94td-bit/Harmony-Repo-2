package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PhotoQuestionRoutingTest {

    @Test
    fun `conversation favorite photo leaves standard quiz and enters photo mechanic`() {
        assertEquals(
            HarmonyImageChoiceKind.MEMORY_MATCH,
            harmonyImageChoiceKind("gespraechsanreger", 1)
        )
    }

    @Test
    fun `snapshot favorite photo leaves standard quiz and enters photo mechanic`() {
        assertEquals(
            HarmonyImageChoiceKind.MEMORY_MATCH,
            harmonyImageChoiceKind("schnapp", 1)
        )
    }

    @Test
    fun `non photo question in photo category keeps normal quiz behavior`() {
        assertNull(harmonyImageChoiceKind("schnapp", 0))
    }
}
