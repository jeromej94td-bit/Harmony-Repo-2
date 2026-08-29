package com.example.ui.screens

import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class RankingSlotBoardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val question = "Was wäre der erste Schritt nach dem Gewinn?"
    private val options = listOf(
        "Niemandem erzählen",
        "Familie einladen",
        "Champagner öffnen",
        "Finanzberater suchen"
    )

    @Test
    fun rankingBoard_usesCompactLanesWithoutSectionClutter() {
        composeTestRule.setContent {
            HarmonyTheme {
                RankingSlotBoard(
                    question = question,
                    options = options,
                    selectedAnswer = null,
                    profile = ProfileEntity(),
                    onPick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("ranking_slot_board").assertExists()
        composeTestRule.onNodeWithTag("ranking_slot_lane_0").assertExists()
        composeTestRule.onNodeWithText(question).assertIsDisplayed()

        composeTestRule.onNodeWithText("DEINE RANGLISTE").assertDoesNotExist()
        composeTestRule.onNodeWithText("KARTEN").assertDoesNotExist()
        composeTestRule.onNodeWithText("Platz 1 · Karte hier ablegen").assertDoesNotExist()

        options.forEach { option ->
            composeTestRule.onNodeWithText(option).assertIsDisplayed()
        }
    }
}
