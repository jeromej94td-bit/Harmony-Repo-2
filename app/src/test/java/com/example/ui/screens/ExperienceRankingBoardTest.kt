package com.example.ui.screens

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.data.model.ExperienceRankingItem
import com.example.data.model.ExperienceRankingRound
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36])
class ExperienceRankingBoardTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val round = ExperienceRankingRound(
        id = "demo_ranking",
        prompt = "Was kommt zuerst?",
        items = listOf(
            ExperienceRankingItem("a", "Nähe"),
            ExperienceRankingItem("b", "Ort"),
            ExperienceRankingItem("c", "Überraschung")
        )
    )

    @Test
    fun `board reuses ranking slots and restores stable id order`() {
        composeRule.setContent {
            HarmonyTheme {
                ExperienceRankingBoard(
                    round = round,
                    selectedItemIds = listOf("c", "a", "b"),
                    profile = ProfileEntity(),
                    onPick = {},
                )
            }
        }

        composeRule.onNodeWithTag("experience_ranking_board").assertExists()
        composeRule.onNodeWithTag("ranking_slot_0").assertExists()
        composeRule.onNodeWithText("Überraschung").assertExists()
        composeRule.onNodeWithText("Nähe").assertExists()
        composeRule.onNodeWithText("Ort").assertExists()
    }
}
