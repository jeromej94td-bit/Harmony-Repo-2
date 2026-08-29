package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WhoWouldOptionPolicyTest {

    @Test
    fun `canonical both and nobody keep classic presentation`() {
        val layout = WhoWouldOptionPolicy.resolve(
            listOf("Ich", "Mein Partner", "Beide", "Niemand")
        )

        assertTrue(layout.useCanonicalInstruction)
        assertEquals(WhoWouldBottomRole.BOTH, layout.bottomOptions[0].role)
        assertEquals(WhoWouldBottomRole.NOBODY, layout.bottomOptions[1].role)
    }

    @Test
    fun `legacy alternative answers stay neutral instead of pretending to be both and nobody`() {
        val layout = WhoWouldOptionPolicy.resolve(
            listOf("Ich", "Mein Partner", "Kommt drauf an", "Wir treffen uns in der Mitte")
        )

        assertFalse(layout.useCanonicalInstruction)
        assertEquals(WhoWouldBottomRole.ALTERNATIVE, layout.bottomOptions[0].role)
        assertEquals(WhoWouldBottomRole.ALTERNATIVE, layout.bottomOptions[1].role)
    }

    @Test
    fun `common both and nobody wordings are recognized`() {
        val layout = WhoWouldOptionPolicy.resolve(
            listOf("Ich", "Mein Partner", "Wir beide", "Keiner von uns")
        )

        assertTrue(layout.useCanonicalInstruction)
        assertEquals(WhoWouldBottomRole.BOTH, layout.bottomOptions[0].role)
        assertEquals(WhoWouldBottomRole.NOBODY, layout.bottomOptions[1].role)
    }

    @Test
    fun `bottom options preserve their original positions and labels`() {
        val options = listOf("Ich", "Mein Partner", "Wir teilen", "Wir lassen ihn liegen")
        val layout = WhoWouldOptionPolicy.resolve(options)

        assertEquals("Wir teilen", layout.bottomOptions[0].label)
        assertEquals("Wir lassen ihn liegen", layout.bottomOptions[1].label)
    }
}
