package com.example.data.model

import com.example.data.GeneratedContentRegistry
import org.junit.Assert.assertTrue
import org.junit.Test

class QuestionContentRuntimeCatalogAuditTest {

    @Test
    fun `repository runtime catalog has no Stage 06_1 question defects`() {
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
            val issues = QuestionContentDefectAudit.audit(HarmonyPacksData.PACKS)
            assertTrue(
                issues.joinToString(
                    prefix = "Stage 06.1 defects found:\n",
                    separator = "\n"
                ) { issue ->
                    "${issue.packId}[${issue.questionIndex}] ${issue.kind}: ${issue.question}"
                },
                issues.isEmpty()
            )
        } finally {
            HarmonyPacksData.setDynamicPacks(emptyList())
        }
    }
}
