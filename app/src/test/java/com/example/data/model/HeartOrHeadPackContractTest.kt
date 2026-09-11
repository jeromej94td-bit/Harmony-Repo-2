package com.example.data.model

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HeartOrHeadPackContractTest {

    @Test
    fun `herz oder kopf pack has six four-option rounds`() {
        val pack = GeneratedContentRegistry.PACKS.firstOrNull { it.id == "herz_oder_kopf" }
        assertNotNull("herz_oder_kopf pack must exist", pack)
        pack!!

        assertEquals("Herz oder Kopf", pack.title)
        assertEquals("beziehung", pack.topic)
        assertEquals("lieber", pack.cat)
        assertEquals(6, pack.questions.size)
        pack.questions.forEachIndexed { index, question ->
            assertEquals("round $index must have exactly four options", 4, question.options.size)
        }

        assertEquals(
            listOf(
                "Welcher Abend fühlt sich am meisten nach dir an?",
                "Was bedeutet dir bei einem Geschenk am meisten?",
                "Wie gehst du eher mit Spannung um?",
                "Was gibt dir in eurer Zukunft am meisten Sicherheit?",
                "Was zeigt Liebe für dich im Alltag am stärksten?",
                "Wenn du lieben müsstest – worauf vertraust du zuerst?"
            ),
            pack.questions.map { it.q }
        )
    }
}
