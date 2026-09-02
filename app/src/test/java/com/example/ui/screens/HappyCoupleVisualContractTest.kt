package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.ui.components.AmbientBackground
import com.example.ui.theme.HarmonyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.LEGACY)
@Config(qualifiers = "w411dp-h1100dp-xxhdpi", sdk = [35])
class HappyCoupleVisualContractTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `happy couple visibly shows question one of eleven and numbered image cards`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    HarmonyImageChoiceQuestion(
                        kind = HarmonyImageChoiceKind.HAPPY_COUPLE,
                        question = "Welches Paar ist GLÜCKLICH?",
                        options = listOf("1", "2", "3", "4"),
                        selectedAnswer = null,
                        onPick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        composeRule.mainClock.advanceTimeBy(3_000L)

        composeRule.onNodeWithTag("happy_couple_question_pill")
            .assertTextContains("Frage 1 von 11")

        (1..4).forEach { number ->
            composeRule.onNodeWithTag("happy_couple_number_$number")
                .assertTextEquals(number.toString())
        }
    }
}
