package com.example.data.model

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertTrue
import org.junit.Test

class GermanContentRuntimeLanguageAuditTest {

    @Test
    fun `runtime German catalogue has no clear English sentence residue`() {
        val generatedPacks = GeneratedContentRegistry.PACKS.map { pack ->
            QuestionPack(
                id = pack.id,
                title = pack.title,
                tags = pack.tags,
                cat = pack.cat,
                topic = pack.topic,
                type = pack.type,
                questions = pack.questions.map { question ->
                    Question(
                        q = question.q,
                        options = question.options,
                        defaultMine = question.defaultMine
                    )
                },
                pairs = pack.pairs,
                emoji = pack.emoji
            )
        }

        HarmonyPacksData.setDynamicPacks(generatedPacks)
        try {
            val issues = GermanContentLanguageAudit.audit(HarmonyPacksData.PACKS)
            assertTrue(
                "Stage 06.2 language mismatches:\n${issues.joinToString("\n")}",
                issues.isEmpty()
            )
        } finally {
            HarmonyPacksData.setDynamicPacks(emptyList())
        }
    }
}
