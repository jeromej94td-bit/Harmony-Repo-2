package com.example.ui

import com.example.data.model.PersonSide
import com.example.ui.screens.DropRect
import com.example.ui.screens.compactInteractionQuestion
import com.example.ui.screens.resolvePersonDrop
import org.junit.Assert.assertEquals
import org.junit.Test

class QuestionInteractionBehaviorTest {

    @Test
    fun dropUsesFingerPositionInsteadOfDraggedCardCenter() {
        val user = DropRect(left = 0f, top = 0f, right = 120f, bottom = 220f)
        val partner = DropRect(left = 260f, top = 0f, right = 380f, bottom = 220f)

        assertEquals(
            PersonSide.USER,
            resolvePersonDrop(
                pointerX = 110f,
                pointerY = 110f,
                userBounds = user,
                partnerBounds = partner,
                hitSlop = 18f
            )
        )
    }

    @Test
    fun dropAcceptsSmallEdgeMissInsideHitSlop() {
        val user = DropRect(left = 20f, top = 20f, right = 140f, bottom = 220f)

        assertEquals(
            PersonSide.USER,
            resolvePersonDrop(
                pointerX = 12f,
                pointerY = 100f,
                userBounds = user,
                partnerBounds = null,
                hitSlop = 12f
            )
        )
    }

    @Test
    fun rankingQuestionDoesNotRepeatVisibleAnswerCards() {
        val options = listOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort")

        assertEquals(
            "Deine persönliche Rangliste für „Arbeitsweg“",
            compactInteractionQuestion(
                "Deine persönliche Rangliste für „Arbeitsweg“: Sicherheit, Freiheit, Abenteuer, Komfort",
                options
            )
        )
    }

    @Test
    fun roleQuestionDropsRankSuffixWhenOptionsAreAlreadyShown() {
        val options = listOf("Visionär/Ideen", "Detailplaner", "Ausführer", "Qualitätsprüfer")

        assertEquals(
            "Wer übernimmt welche Rolle bei gemeinsamen Plänen?",
            compactInteractionQuestion(
                "Wer übernimmt welche Rolle bei gemeinsamen Plänen? Rank: Visionär/Ideen, Detailplaner, Ausführer, Qualitätsprüfer",
                options
            )
        )
    }
}
