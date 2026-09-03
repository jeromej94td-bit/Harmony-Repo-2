package com.example.ui.screens

import com.example.data.model.HarmonyPacksData
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class AutumnEveningImageChoicePolicyTest {

    @Test
    fun `autumn pack routes every round by stable id and index`() {
        val pack = HarmonyPacksData.DEFAULT_PACKS.single { it.id == "herbstabend" }
        val expected = listOf(
            HarmonyImageChoiceKind.AUTUMN_STORY,
            HarmonyImageChoiceKind.AUTUMN_DRINK,
            HarmonyImageChoiceKind.AUTUMN_SNACK,
            HarmonyImageChoiceKind.AUTUMN_NOOK,
            HarmonyImageChoiceKind.AUTUMN_SOUND,
            HarmonyImageChoiceKind.AUTUMN_SCENT
        )

        assertEquals(expected, pack.questions.indices.map { harmonyImageChoiceKind(pack, it) })
    }

    @Test
    fun `autumn routing rejects out of range rounds and copied wording`() {
        val autumnPack = HarmonyPacksData.DEFAULT_PACKS.single { it.id == "herbstabend" }
        val copiedPack = QuestionPack(
            id = "copied-autumn-wording",
            title = "Not the autumn game",
            tags = emptyList(),
            cat = "lieber",
            topic = "hobbys",
            type = "quiz",
            questions = listOf(
                Question(
                    "Welche Geschichte zieht dich in den Herbst?",
                    listOf("Mystery", "Thriller", "Dark Academia", "Cozy Fantasy")
                )
            )
        )

        assertNull(harmonyImageChoiceKind(autumnPack, -1))
        assertNull(harmonyImageChoiceKind(autumnPack, 6))
        assertNull(harmonyImageChoiceKind(copiedPack, 0))
    }

    @Test
    fun `autumn cards reveal in row major order`() {
        assertEquals(listOf(0L, 110L, 420L, 530L), (0..3).map(::autumnEveningRevealDelayMillis))
    }
}
