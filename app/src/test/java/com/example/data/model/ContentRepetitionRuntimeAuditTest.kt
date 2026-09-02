package com.example.data.model

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertTrue
import org.junit.Test

class ContentRepetitionRuntimeAuditTest {

    @Test
    fun `repository runtime catalog has no Stage 06_4 repetition defects`() {
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
            val issues = ContentRepetitionAudit.audit(HarmonyPacksData.PACKS)
            if (issues.isNotEmpty()) {
                println("Stage 06.4 repetition defects found:")
                issues.forEach { issue ->
                    println(
                        "${issue.kind}: ${issue.signature} | " +
                            "packs=${issue.packIds.sorted().joinToString()} | " +
                            "occurrences=${issue.occurrences}"
                    )
                }
            }

            assertTrue(
                issues.joinToString(
                    prefix = "Stage 06.4 repetition defects found:\n",
                    separator = "\n"
                ) { issue ->
                    "${issue.kind}: ${issue.signature} | " +
                        "packs=${issue.packIds.sorted().joinToString()} | " +
                        "occurrences=${issue.occurrences}"
                },
                issues.isEmpty()
            )
        } finally {
            HarmonyPacksData.setDynamicPacks(emptyList())
        }
    }
}
