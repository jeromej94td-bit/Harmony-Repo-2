package com.example.ui.screens

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.model.ExperienceImageDuelOption
import com.example.data.model.ExperienceImageDuelRound
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
class ExperienceImageDuelBoardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val round = ExperienceImageDuelRound(
        id = "visual",
        prompt = "Welches Bild passt besser?",
        firstOption = ExperienceImageDuelOption("first", "Erste Wahl", "asset_first"),
        secondOption = ExperienceImageDuelOption("second", "Zweite Wahl", "asset_second")
    )

    @Test
    fun `board renders both image choices and returns picked option`() {
        var picked: ExperienceImageDuelOption? = null

        composeRule.setContent {
            HarmonyTheme {
                ExperienceImageDuelBoard(
                    round = round,
                    selectedOptionId = null,
                    imageResFor = { android.R.drawable.ic_menu_gallery },
                    onPick = { picked = it },
                    eyebrow = "BILD-DUELL"
                )
            }
        }

        composeRule.onNodeWithTag("experience_image_duel_board").assertExists()
        composeRule.onNodeWithText(round.prompt).assertExists()
        composeRule.onNodeWithText(round.firstOption.label).assertExists()
        composeRule.onNodeWithText(round.secondOption.label).assertExists()
        composeRule.onNodeWithTag("experience_image_duel_first").performClick()
        composeRule.waitForIdle()

        assertEquals(round.firstOption, picked)
    }

    @Test
    fun `board exposes caller owned selected option`() {
        composeRule.setContent {
            HarmonyTheme {
                ExperienceImageDuelBoard(
                    round = round,
                    selectedOptionId = round.secondOption.id,
                    imageResFor = { android.R.drawable.ic_menu_gallery },
                    onPick = {}
                )
            }
        }

        composeRule.onNodeWithTag("experience_image_duel_second").assertIsSelected()
    }
}
