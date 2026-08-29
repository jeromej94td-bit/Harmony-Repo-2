package com.example.ui.screens

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.model.ExperienceEitherOrRound
import com.example.ui.theme.HarmonyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ExperienceEitherOrBoardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `board renders reusable round and returns picked raw choice`() {
        var picked: String? = null
        val round = ExperienceEitherOrRound(
            id = "tone",
            prompt = "Welcher Ton passt besser?",
            firstChoice = "Leicht",
            secondChoice = "Tief"
        )

        composeRule.setContent {
            HarmonyTheme {
                ExperienceEitherOrBoard(
                    round = round,
                    selectedChoice = null,
                    onPick = { picked = it }
                )
            }
        }

        composeRule.onNodeWithTag("experience_either_or_board").assertExists()
        composeRule.onNodeWithText(round.prompt).assertExists()
        composeRule.onNodeWithTag("experience_either_or_first").assertExists().performClick()
        composeRule.waitForIdle()

        assertEquals("Leicht", picked)
    }

    @Test
    fun `board exposes selected choice without owning selection state`() {
        val round = ExperienceEitherOrRound(
            id = "privacy",
            prompt = "Wie möchtet ihr den Moment teilen?",
            firstChoice = "Nur wir zwei",
            secondChoice = "Mit Lieblingsmenschen"
        )

        composeRule.setContent {
            HarmonyTheme {
                ExperienceEitherOrBoard(
                    round = round,
                    selectedChoice = round.secondChoice,
                    onPick = {}
                )
            }
        }

        composeRule.onNodeWithTag("experience_either_or_second").assertIsSelected()
    }
}
