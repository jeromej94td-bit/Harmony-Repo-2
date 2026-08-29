package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ExperienceRankingRoundTest {

    private fun item(id: String, label: String) = ExperienceRankingItem(id, label)

    private fun round() = ExperienceRankingRound(
        id = "priorities",
        prompt = "Was ist euch am wichtigsten?",
        items = listOf(
            item("a", "Nähe"),
            item("b", "Überraschung"),
            item("c", "Ort")
        )
    )

    @Test
    fun `ranking round keeps immutable ordered items`() {
        val source = round().items.toMutableList()
        val copied = ExperienceRankingRound("copy", "Ordnet", source)
        source.clear()

        assertEquals(listOf("a", "b", "c"), copied.items.map(ExperienceRankingItem::id))
        assertEquals(3, copied.itemCount)
    }

    @Test
    fun `malformed ranking content fails fast`() {
        assertIllegalArgument { ExperienceRankingItem("", "A") }
        assertIllegalArgument { ExperienceRankingItem("a", "") }
        assertIllegalArgument { ExperienceRankingRound("", "Prompt", listOf(item("a", "A"), item("b", "B"))) }
        assertIllegalArgument { ExperienceRankingRound("round", "", listOf(item("a", "A"), item("b", "B"))) }
        assertIllegalArgument { ExperienceRankingRound("round", "Prompt", listOf(item("a", "A"))) }
        assertIllegalArgument {
            ExperienceRankingRound("round", "Prompt", listOf(item("same", "A"), item("same", "B")))
        }
        assertIllegalArgument {
            ExperienceRankingRound("round", "Prompt", listOf(item("a", "Same"), item("b", "Same")))
        }
    }

    @Test
    fun `stable id ranking selection round trips through existing answer format`() {
        val round = round()
        val ids = listOf("c", "a", "b")
        val encoded = ExperienceRankingSelectionCodec.encode(round, ids)

        assertEquals(ids, ExperienceRankingSelectionCodec.decode(round, encoded))
        assertNull(ExperienceRankingSelectionCodec.encodeOrNull(round, listOf("a", "b")))
        assertNull(ExperienceRankingSelectionCodec.decode(round, "invalid"))
    }

    @Test
    fun `proposal ranking adapts without changing ids labels prompt or item count`() {
        val generic = ProposalPriorityRanking.toExperienceRankingRound()

        assertEquals(ProposalPriorityRanking.STEP_ID, generic.id)
        assertEquals("Was muss für euren Antrag am meisten stimmen?", generic.prompt)
        assertEquals(5, generic.itemCount)
        assertEquals(ProposalPriorityRanking.priorities.map(ProposalPriority::id), generic.items.map(ExperienceRankingItem::id))
        assertEquals(ProposalPriorityRanking.priorities.map(ProposalPriority::label), generic.items.map(ExperienceRankingItem::label))
        assertEquals(1, ProposalExperienceRunnerPolicy.itemCount(ProposalPriorityRanking.STEP_ID))
    }

    private fun assertIllegalArgument(block: () -> Unit) {
        var failed = false
        try {
            block()
        } catch (_: IllegalArgumentException) {
            failed = true
        }
        assertTrue("Expected IllegalArgumentException", failed)
    }
}
