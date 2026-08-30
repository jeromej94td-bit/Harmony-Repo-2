package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.model.HarmonyPacksData
import com.example.data.model.LoveBalanceQuestionPolicy
import com.example.data.model.Question
import com.example.ui.components.AmbientBackground
import com.example.ui.theme.HarmonyTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.LEGACY)
@Config(qualifiers = "w411dp-h1100dp-xxhdpi", sdk = [35])
class HappyCoupleNumberPickRegressionTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `happy couple exposes four numbered clickable cards and returns one through four`() {
        val question = HarmonyPacksData.DEFAULT_PACKS
            .first { it.id == "liebegleichgewicht" }
            .questions.first()
        var pickedAnswer by mutableStateOf<String?>(null)

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    HarmonyImageChoiceQuestion(
                        kind = HarmonyImageChoiceKind.HAPPY_COUPLE,
                        question = question.q,
                        options = question.options,
                        selectedAnswer = pickedAnswer,
                        onPick = { pickedAnswer = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        composeRule.mainClock.advanceTimeBy(3_000L)

        (1..4).forEach { number ->
            composeRule.onNodeWithTag("happy_couple_number_$number").assertExists()
        }

        (0..3).forEach { index ->
            composeRule.onNodeWithTag("happy_couple_option_$index").performClick()
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.waitForIdle()

            assertEquals((index + 1).toString(), pickedAnswer)
            composeRule.onNodeWithTag("happy_couple_option_${index}_selected").assertExists()
        }
    }

    @Test
    fun `love balance first slot keeps happy couple renderer when dynamic prompt changes`() {
        val embeddedPack = HarmonyPacksData.DEFAULT_PACKS.first {
            it.id == LoveBalanceQuestionPolicy.PACK_ID
        }
        val dynamicLikePack = embeddedPack.copy(
            questions = listOf(
                Question(
                    q = "Remote content may change this prompt",
                    options = listOf("A", "B", "C", "D")
                )
            )
        )

        assertEquals(
            HarmonyImageChoiceKind.HAPPY_COUPLE,
            harmonyImageChoiceKind(dynamicLikePack, 0)
        )
    }

    @Test
    fun `later love balance questions never reuse happy couple image renderer`() {
        val pack = HarmonyPacksData.DEFAULT_PACKS.first {
            it.id == LoveBalanceQuestionPolicy.PACK_ID
        }

        assertNotEquals(
            HarmonyImageChoiceKind.HAPPY_COUPLE,
            harmonyImageChoiceKind(pack, 1)
        )
    }
}
