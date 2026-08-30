package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExperienceRevealTest {

    @Test
    fun `generic reveal validates stable reusable result content`() {
        val reveal = ExperienceRevealResult(
            title = "Unser Ergebnis",
            subtitle = "Was euch verbindet",
            sections = listOf(
                ExperienceRevealSection(
                    id = "values",
                    title = "Was zählt",
                    values = listOf("Nähe", "Vertrauen")
                )
            ),
            closing = "Ganz euer Moment"
        )

        assertEquals("Unser Ergebnis", reveal.title)
        assertEquals("values", reveal.sections.single().id)
        assertEquals(listOf("Nähe", "Vertrauen"), reveal.sections.single().values)

        var rejectedBlankId = false
        try {
            ExperienceRevealSection(
                id = "",
                title = "Ungültig",
                values = listOf("Wert")
            )
        } catch (_: IllegalArgumentException) {
            rejectedBlankId = true
        }
        assertTrue(rejectedBlankId)
    }

    @Test
    fun `proposal reveal adapter preserves legacy output exactly`() {
        val legacy = ProposalRevealResult(
            title = "Ein persönlicher Antrag",
            subtitle = "Als Kulisse zieht es euch ans Meer.",
            sections = listOf(
                ProposalRevealSection(
                    id = "location",
                    title = "Eure Kulisse",
                    values = listOf("Meer", "Abendlicht")
                ),
                ProposalRevealSection(
                    id = "priorities",
                    title = "Was wirklich zählt",
                    values = listOf("Nähe")
                )
            ),
            closing = "Ganz euer Moment."
        )

        val generic = legacy.toExperienceRevealResult()

        assertEquals(legacy.title, generic.title)
        assertEquals(legacy.subtitle, generic.subtitle)
        assertEquals(legacy.closing, generic.closing)
        assertEquals(
            legacy.sections.map { it.id to (it.title to it.values) },
            generic.sections.map { it.id to (it.title to it.values) }
        )
    }
}
