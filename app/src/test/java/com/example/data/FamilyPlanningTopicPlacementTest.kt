package com.example.data

import com.example.data.model.Question
import com.example.data.model.QuestionPack
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test

class FamilyPlanningTopicPlacementTest {

    @After
    fun resetCustomPacks() {
        DeveloperDataManager._customPacks.clear()
    }

    @Test
    fun `stale custom Familienplanung pack is moved from Beziehung into Familie before runtime merge`() {
        val questions = listOf(
            Question(
                q = "Welche Seite gewinnt?",
                options = listOf("A", "B")
            )
        )
        val stalePack = QuestionPack(
            id = "h500_061_familienplanung_entweder_oder",
            title = "Familienplanung – Matchcheck",
            tags = listOf("harmony360", "mechanik_entweder_oder"),
            cat = "tot",
            topic = "beziehung",
            type = "quiz",
            questions = questions
        )

        DeveloperDataManager._customPacks.clear()
        DeveloperDataManager._customPacks.add(stalePack)

        GeneratedContentRegistry.PACKS

        val normalized = DeveloperDataManager._customPacks.single {
            it.id == "h500_061_familienplanung_entweder_oder"
        }
        assertEquals("familie", normalized.topic)
        assertEquals(stalePack.title, normalized.title)
        assertEquals(stalePack.cat, normalized.cat)
        assertEquals(questions, normalized.questions)
    }
}
