package com.example.data.brain

import com.example.data.brain.db.BrainAnswerHistoryEntity
import com.example.data.brain.db.BrainPreferenceEntity
import com.example.data.brain.engine.HarmonyContextBuilder
import com.example.data.brain.engine.HarmonyDuplicateDetector
import com.example.data.brain.engine.HarmonyLocalSignalExtractor
import com.example.data.brain.engine.HarmonyMemoryFactGenerator
import com.example.data.brain.engine.HarmonyPreferenceEngine
import com.example.data.brain.engine.HarmonyPrivacyFilter
import com.example.data.brain.model.BrainScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BrainCoreTest {

    @Test
    fun testSignalExtractor() {
        val signals = HarmonyLocalSignalExtractor.extractSignals(
            category = "Reisen",
            topic = "Urlaub",
            questionText = "Wo möchtest du am liebsten Urlaub machen?",
            answerPersonA = "Ich liebe Strand und Meer in Italien",
            answerPersonB = "Hauptsache Strand und Sonne!"
        )

        assertTrue(signals.isNotEmpty())
        val coupleTags = signals.filter { it.scope == BrainScope.COUPLE }.map { it.tag }
        assertTrue(coupleTags.contains("strand"))

        val personATags = signals.filter { it.scope == BrainScope.PERSON_A }.map { it.tag }
        assertTrue(personATags.contains("italien"))
    }

    @Test
    fun testPreferenceEngineBoundedMath() {
        val existing = BrainPreferenceEntity(
            scope = BrainScope.COUPLE.scopeKey,
            tag = "strand",
            score = 0.50,
            confidence = 0.20,
            engagement = 0.10,
            positiveSignals = 1,
            negativeSignals = 0,
            saturation = 0.0,
            lastSeenAt = 1000L,
            lastUsedForContentAt = null,
            updatedAt = 1000L
        )

        val updated = HarmonyPreferenceEngine.updatePreference(
            existing = existing,
            scope = BrainScope.COUPLE,
            tag = "strand",
            isPositive = true,
            weight = 0.15
        )

        assertTrue(updated.score > 0.50)
        assertTrue(updated.confidence > 0.20)
        assertTrue(updated.positiveSignals == 2)
        assertTrue(updated.score <= 1.0)
    }

    @Test
    fun testDuplicateDetector() {
        val existing = listOf(
            "Was ist dein absolutes Traumreiseziel?",
            "Welche Pizza magst du am liebsten?"
        )

        // Exact match
        assertTrue(HarmonyDuplicateDetector.isDuplicate("Was ist dein absolutes Traumreiseziel?", existing))

        // High similarity match
        assertTrue(HarmonyDuplicateDetector.isDuplicate("Was ist dein absolutes Traumreiseziel für den Urlaub?", existing))

        // Novel question
        assertFalse(HarmonyDuplicateDetector.isDuplicate("Welches Brettspiel wollen wir am Wochenende spielen?", existing))
    }

    @Test
    fun testPrivacyFilterSanitization() {
        val raw = "Jerome wohnt in der Musterstraße 12 und seine Mail ist jerome@example.com mit Tel +491701234567"
        val sanitized = HarmonyPrivacyFilter.sanitizeText(
            text = raw,
            userName = "Jerome",
            partnerName = "Alex"
        )

        assertFalse(sanitized.contains("jerome@example.com"))
        assertFalse(sanitized.contains("+491701234567"))
        assertFalse(sanitized.contains("Jerome"))
        assertTrue(sanitized.contains("Person A"))
    }

    @Test
    fun testMemoryFactGeneration() {
        val answers = listOf(
            BrainAnswerHistoryEntity(
                id = "1",
                questionId = "q1",
                questionText = "Wo machen wir am liebsten Urlaub?",
                category = "Reisen",
                contentType = "EITHER_OR",
                answerPersonA = "Strand",
                answerPersonB = "Strand"
            )
        )

        val prefs = listOf(
            BrainPreferenceEntity(
                scope = BrainScope.COUPLE.scopeKey,
                tag = "strand",
                score = 0.85,
                confidence = 0.60
            )
        )

        val facts = HarmonyMemoryFactGenerator.deriveFacts(answers, prefs)
        assertTrue(facts.isNotEmpty())
        assertEquals(BrainScope.COUPLE.scopeKey, facts.first().personScope)
        assertTrue(facts.first().factText.contains("Strand"))
    }

    @Test
    fun testContextBuilderBudgetSafety() {
        val answers = (1..50).map { idx ->
            BrainAnswerHistoryEntity(
                id = "$idx",
                questionId = "q$idx",
                questionText = "Dies ist eine sehr lange Frage Nummer $idx mit vielen detaillierten Beispielen?",
                category = "Reisen",
                contentType = "QUESTION",
                answerPersonA = "Ausführliche Antwort von Person A mit vielen interessanten Details über das Leben und Reisen",
                answerPersonB = "Ebenso ausführliche Antwort von Person B"
            )
        }

        val prefs = (1..30).map { idx ->
            BrainPreferenceEntity(
                scope = if (idx % 3 == 0) "COUPLE" else if (idx % 2 == 0) "PERSON_A" else "PERSON_B",
                tag = "thema_$idx",
                score = 0.75,
                confidence = 0.5
            )
        }

        val context = HarmonyContextBuilder.buildContext(
            allAnswers = answers,
            allPreferences = prefs,
            allMemoryFacts = emptyList(),
            totalInteractions = 100,
            task = "questions",
            category = "Reisen"
        )

        val jsonStr = HarmonyContextBuilder.serializeCompact(context)
        assertTrue("JSON payload length ${jsonStr.length} exceeds 5500 limit!", jsonStr.length <= 5500)
    }
}
