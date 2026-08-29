package com.example.data.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SexIntimacyRevealPolicyTest {

    @Test
    fun sexIntimacyPacks_allowSkipAndCompactDenseChoices() {
        assertTrue(SexIntimacyRevealPolicy.isSexIntimacyPack("naehe", "sex"))
        assertTrue(SexIntimacyRevealPolicy.isSexIntimacyPack("intimleben", "sex"))
        assertFalse(SexIntimacyRevealPolicy.isSexIntimacyPack("essenreden", "essen"))

        assertTrue(SexIntimacyRevealPolicy.allowsSkip("naehe", "sex"))
        assertTrue(SexIntimacyRevealPolicy.allowsSkip("intimleben", "sex"))
        assertTrue(SexIntimacyRevealPolicy.useCompactAnswerLayout("intimleben", "sex", 5))
        assertTrue(SexIntimacyRevealPolicy.useCompactAnswerLayout("naehe", "sex", 4))
        assertFalse(SexIntimacyRevealPolicy.useCompactAnswerLayout("essenreden", "essen", 5))
    }

    @Test
    fun selectedEverydaySexQuestions_usePrivateCoupleReveal() {
        val revealQuestions = listOf(
            "Zu welcher Tageszeit hast du am liebsten Sex?",
            "Wie oft würdest du dir Sex idealerweise wünschen – unabhängig davon, wie oft wir aktuell Sex haben?",
            "Welche Rolle spielt Vorspiel für dich bei gutem Sex?",
            "Was beeinflusst deine Lust im Alltag am stärksten?",
            "Was lässt deine Lust am schnellsten verschwinden?",
            "Magst du es lieber, wenn Sex spontan entsteht oder wenn wir uns bewusst Zeit dafür nehmen?",
            "Wie möchtest du am liebsten merken, dass ich gerade Lust auf dich habe?"
        )

        revealQuestions.forEach { question ->
            assertTrue(
                "Expected private couple reveal for: $question",
                SexIntimacyRevealPolicy.usesPrivateCoupleReveal("intimleben", "sex", question)
            )
        }

        assertFalse(
            SexIntimacyRevealPolicy.usesPrivateCoupleReveal(
                "intimleben",
                "sex",
                "Wie wohl fühlst du dich mit Dirty Talk oder erotischen Worten beim Sex?"
            )
        )
        assertFalse(
            SexIntimacyRevealPolicy.usesPrivateCoupleReveal(
                "essenreden",
                "essen",
                "Zu welcher Tageszeit hast du am liebsten Sex?"
            )
        )
    }
}
