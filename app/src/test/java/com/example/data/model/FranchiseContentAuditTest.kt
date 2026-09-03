package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FranchiseContentAuditTest {

    @Test
    fun `flags franchise specific world references that have neutral Harmony alternatives`() {
        val packs = listOf(
            pack(
                id = "magic",
                prompt = "Würdet ihr lieber einen Tag in Hogwarts verbringen?",
                options = listOf("Ja", "Nein")
            ),
            pack(
                id = "creatures",
                prompt = "Welche Pokémon-Welt würdet ihr gemeinsam erkunden?",
                options = listOf("Wald", "Meer")
            )
        )

        val issues = FranchiseContentAudit.audit(packs)

        assertEquals(2, issues.size)
        assertTrue(issues.any { it.matchedTerm.equals("Hogwarts", ignoreCase = true) })
        assertTrue(issues.any { it.matchedTerm.startsWith("Pok", ignoreCase = true) })
    }

    @Test
    fun `ip neutral fantasy wording remains allowed`() {
        val packs = listOf(
            pack(
                id = "neutral",
                prompt = "Welche magische Zauberschule würdet ihr gemeinsam besuchen?",
                options = listOf("Im Gebirge", "Am Meer", "Im Wald", "In einer Wolkenstadt")
            )
        )

        assertTrue(FranchiseContentAudit.audit(packs).isEmpty())
    }

    @Test
    fun `ordinary media and product vocabulary is not treated as franchise residue`() {
        val packs = listOf(
            pack(
                id = "media",
                prompt = "Was schaut ihr lieber beim Streaming-Abend?",
                options = listOf("Science-Fiction", "Anime", "Komödie", "Dokumentation")
            )
        )

        assertTrue(FranchiseContentAudit.audit(packs).isEmpty())
    }

    private fun pack(id: String, prompt: String, options: List<String>) = QuestionPack(
        id = id,
        title = id,
        tags = emptyList(),
        cat = "test",
        topic = "filme_serien",
        type = "quiz",
        questions = listOf(Question(q = prompt, options = options)),
        pairs = emptyList(),
        emoji = ""
    )
}
