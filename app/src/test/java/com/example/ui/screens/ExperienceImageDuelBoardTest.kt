package com.example.ui.screens

import androidx.compose.ui.test.assertExists
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

class ExperienceImageDuelBoardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `board renders prompt options and emits picked option`() {
        val round = ExperienceImageDuelRound(
            id = "home_or_lake",
            prompt = "Wo fühlt es sich richtig an?",
            firstOption = ExperienceImageDuelOption("home", "Zu Hause", "home_asset"),
            secondOption = ExperienceImageDuelOption("lake", "Am See", "lake_asset")
        )
        var picked: ExperienceImageDuelOption? = null

        composeRule.setContent {
            HarmonyTheme {
                ExperienceImageDuelBoard(
                    round = round,
                    selectedOptionId = null,
                    imageResolver = { android.R.drawable.ic_menu_gallery },
                    onPick = { picked = it }
                )
            }
        }

        composeRule.onNodeWithTag("experience_image_duel").assertExists()
        composeRule.onNodeWithText(round.prompt).assertExists()
        composeRule.onNodeWithText("Zu Hause").assertExists()
        composeRule.onNodeWithText("Am See").assertExists()
        composeRule.onNodeWithTag("experience_image_duel_home").assertExists().performClick()

        assertEquals("home", picked?.id)
    }

    @Test
    fun `selected option remains addressable with stable generic tags`() {
        val round = ExperienceImageDuelRound(
            id = "a_or_b",
            prompt = "A oder B?",
            firstOption = ExperienceImageDuelOption("a", "A", "asset_a"),
            secondOption = ExperienceImageDuelOption("b", "B", "asset_b")
        )

        composeRule.setContent {
            HarmonyTheme {
                ExperienceImageDuelBoard(
                    round = round,
                    selectedOptionId = "b",
                    imageResolver = { 0 },
                    onPick = {}
                )
            }
        }

        composeRule.onNodeWithTag("experience_image_duel_a").assertExists()
        composeRule.onNodeWithTag("experience_image_duel_b").assertExists()
        composeRule.onNodeWithTag("experience_image_duel_choice_1").assertExists()
        composeRule.onNodeWithTag("experience_image_duel_choice_2").assertExists()
    }
}
