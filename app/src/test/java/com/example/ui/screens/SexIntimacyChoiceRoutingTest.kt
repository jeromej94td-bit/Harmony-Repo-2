package com.example.ui.screens

import com.example.data.GeneratedContentRegistry
import com.example.data.model.SexIntimacyRevealPolicy
import org.junit.Assert.assertEquals
import org.junit.Test

class SexIntimacyChoiceRoutingTest {

    @Test
    fun intimacyPacks_routeEveryQuestionAwayFromScrollableGenericQuiz() {
        val intimacyPacks = GeneratedContentRegistry.PACKS.filter { it.id == "naehe" || it.id == "intimleben" }
        assertEquals(2, intimacyPacks.size)

        intimacyPacks.forEach { pack ->
            pack.questions.forEachIndexed { index, question ->
                val expected = if (
                    SexIntimacyRevealPolicy.usesPrivateCoupleReveal(pack.id, pack.topic, question.q)
                ) {
                    HarmonyImageChoiceKind.INTIMACY_PRIVATE_REVEAL
                } else {
                    HarmonyImageChoiceKind.INTIMACY_COMPACT
                }
                assertEquals("${pack.id} question $index", expected, harmonyImageChoiceKind(pack.id, index))
            }
        }
    }
}
