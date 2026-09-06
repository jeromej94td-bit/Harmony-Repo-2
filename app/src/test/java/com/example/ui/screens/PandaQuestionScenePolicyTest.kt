package com.example.ui.screens

import org.junit.Assert.assertEquals
import org.junit.Test

class PandaQuestionScenePolicyTest {

    @Test
    fun `recognizes the siblings peace question`() {
        assertEquals(
            PandaQuestionScene.PEACE_BETWEEN_SIBLINGS,
            PandaQuestionScenePolicy.forQuestion(
                "Wer ist eher in der Lage, Frieden zwischen rivalisierenden Geschwistern auszuhandeln?"
            )
        )
    }

    @Test
    fun `recognizes the bedtime routine question`() {
        assertEquals(
            PandaQuestionScene.BEDTIME_ROUTINE,
            PandaQuestionScenePolicy.forQuestion("Wer erzwingt am ehesten eine Schlafenszeit-Routine?")
        )
    }

    @Test
    fun `uses the warm children scene for other questions`() {
        assertEquals(
            PandaQuestionScene.WARM_CHILDREN_MOMENT,
            PandaQuestionScenePolicy.forQuestion("Wer sorgt am ehesten für gute Laune?")
        )
    }
}
