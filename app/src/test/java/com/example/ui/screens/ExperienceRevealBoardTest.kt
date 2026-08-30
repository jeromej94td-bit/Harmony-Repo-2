package com.example.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.model.ExperienceRevealResult
import com.example.data.model.ExperienceRevealSection
import com.example.ui.theme.HarmonyTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ExperienceRevealBoardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `board renders reusable result and forwards close`() {
        var closed = false
        val reveal = ExperienceRevealResult(
            title = "Unser Ergebnis",
            subtitle = "Was euch verbindet",
            sections = listOf(
                ExperienceRevealSection(
                    id = "values",
                    title = "Was zählt",
                    values = listOf("Nähe", "Vertrauen")
                )
            ),
            closing = "Ganz euer Moment"
        )

        composeRule.setContent {
            HarmonyTheme {
                ExperienceRevealBoard(
                    reveal = reveal,
                    onClose = { closed = true },
                    closeButtonTestTag = "demo_reveal_close"
                )
            }
        }

        composeRule.onNodeWithTag("experience_reveal_board").assertExists()
        composeRule.onNodeWithText("Unser Ergebnis").assertExists()
        composeRule.onNodeWithText("Was euch verbindet").assertExists()
        composeRule.onNodeWithText("Was zählt").assertExists()
        composeRule.onNodeWithText("• Nähe").assertExists()
        composeRule.onNodeWithText("Ganz euer Moment").assertExists()
        composeRule.onNodeWithTag("demo_reveal_close").performClick()
        assertTrue(closed)
    }
}
