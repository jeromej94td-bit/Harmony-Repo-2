package com.example.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyTheme
import org.junit.Rule
import org.junit.Test

class WhoWouldPandaSceneTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `bedtime question shows panda children and both user profiles`() {
        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                WhoWouldConfirmBoard(
                    question = "Wer erzwingt am ehesten eine Schlafenszeit-Routine?",
                    options = listOf("{user}", "{partner}", "Beide", "Keiner"),
                    selectedAnswer = null,
                    profile = ProfileEntity(
                        userName = "Jerome",
                        partnerName = "Alex",
                        userAvatarPath = "https://example.com/jerome.jpg",
                        partnerAvatarPath = "https://example.com/alex.jpg"
                    ),
                    onPick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("panda_question_scene_bedtime_routine").assertExists()
        composeTestRule.onNodeWithTag("who_user").assertExists()
        composeTestRule.onNodeWithTag("who_partner").assertExists()
        composeTestRule.onNodeWithText("Jerome").assertExists()
        composeTestRule.onNodeWithText("Alex").assertExists()
    }
}
