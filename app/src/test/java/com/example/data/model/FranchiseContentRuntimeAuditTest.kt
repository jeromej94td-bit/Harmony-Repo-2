package com.example.data.model

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertTrue
import org.junit.Test

class FranchiseContentRuntimeAuditTest {

    @Test
    fun `repository runtime catalog has no Stage 06_5 franchise residue`() {
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
            val issues = FranchiseContentAudit.audit(HarmonyPacksData.PACKS)
            if (issues.isNotEmpty()) {
                println("Stage 06.5 franchise findings:")
                issues.forEach { issue ->
                    println("${issue.matchedTerm}: ${issue.packId}: ${issue.text}")
                }
            }
            assertTrue(
                issues.joinToString(
                    prefix = "Stage 06.5 franchise findings:\n",
                    separator = "\n"
                ) { issue -> "${issue.matchedTerm}: ${issue.packId}: ${issue.text}" },
                issues.isEmpty()
            )
        } finally {
            HarmonyPacksData.setDynamicPacks(emptyList())
        }
    }
}
