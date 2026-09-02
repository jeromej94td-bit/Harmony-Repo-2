package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.ui.components.AmbientBackground
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.captureRoboImage
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
    fun `happy couple visibly keeps canonical one of eleven contract despite stale override`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    HarmonyImageChoiceQuestion(
                        kind = HarmonyImageChoiceKind.HAPPY_COUPLE,
                        question = "Remote content changed this prompt",
                        options = listOf("A", "B", "C", "D"),
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
        composeRule.onNodeWithText("Welches Paar ist GLÜCKLICH?").assertExists()
        composeRule.onNodeWithText("Remote content changed this prompt").assertDoesNotExist()

        (1..4).forEach { number ->
            composeRule.onNodeWithTag("happy_couple_number_$number")
                .assertTextEquals(number.toString())
        }

        composeRule.onNodeWithTag("harmony_happy_couple_question").captureRoboImage(
            filePath = "build/happy-couple-preview/happy-couple-question.png"
        )
    }
}
