package com.example.data.couple

import com.example.data.model.MemoryMatchAnswerCodec
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class CoupleAnswerTextTest {
    @Test
    fun `structured answer keeps trailing codec separators intact`() {
        val encoded = MemoryMatchAnswerCodec.encode(
            text = "Unser erster Urlaub",
            imagePath = null,
            partnerText = "Der Zug war lustig",
            partnerImagePath = null
        )

        val normalized = normalizeCoupleAnswerText(encoded)
        val decoded = MemoryMatchAnswerCodec.decode(normalized)

        assertEquals(encoded, normalized)
        assertNotNull(decoded)
        assertEquals("Unser erster Urlaub", decoded?.text)
        assertEquals("Der Zug war lustig", decoded?.partnerText)
    }

    @Test
    fun `plain human answer is still trimmed`() {
        assertEquals("Hallo", normalizeCoupleAnswerText("  Hallo  "))
    }
}
