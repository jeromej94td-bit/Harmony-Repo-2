package com.example.ui

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.data.model.ProfileEntity
import com.example.data.model.QuestionInteractionKind
import com.example.ui.screens.QuestionInteractionBoard
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
class QuestionInteractionBoardTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val profile = ProfileEntity(
        userName = "Jerome",
        partnerName = "Alex",
        userAvatarPath = null,
        partnerAvatarPath = null
    )

    @Test
    fun personAssignmentShowsTwoProfileTargetsAndAllRoles() {
        val roles = listOf("Visionär/Ideen", "Detailplaner", "Ausführer", "Qualitätsprüfer")

        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                QuestionInteractionBoard(
                    kind = QuestionInteractionKind.PERSON_ASSIGNMENT,
                    question = "Wer übernimmt welche Rolle?",
                    options = roles,
                    selectedAnswer = null,
                    profile = profile,
                    onPick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("person_assignment_board").assertExists()
        composeTestRule.onNodeWithTag("assignment_target_user").assertExists()
        composeTestRule.onNodeWithTag("assignment_target_partner").assertExists()
        roles.indices.forEach { index ->
            composeTestRule.onNodeWithTag("assignment_role_$index", useUnmergedTree = true).assertExists()
        }
        composeTestRule.onNodeWithTag("assignment_submit").assertIsNotEnabled()
    }

    @Test
    fun rankingShowsEveryOptionInAReorderableList() {
        val options = listOf("Vertrauen", "Kommunikation", "Humor", "Leidenschaft")

        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                QuestionInteractionBoard(
                    kind = QuestionInteractionKind.RANK_ORDER,
                    question = "Ordne nach Wichtigkeit",
                    options = options,
                    selectedAnswer = null,
                    profile = profile,
                    onPick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("ranking_drag_board").assertExists()
        options.indices.forEach { index ->
            composeTestRule.onNodeWithTag("ranking_item_$index", useUnmergedTree = true).assertExists()
        }
        composeTestRule.onNodeWithTag("ranking_submit").assertIsEnabled()
    }
}
