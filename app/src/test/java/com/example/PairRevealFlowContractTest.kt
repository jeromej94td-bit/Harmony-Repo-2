package com.example

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.model.ProfileEntity
import com.example.ui.screens.PartnerPredictionRevealBoard
import com.example.ui.screens.SecretChoiceRevealBoard
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
class PairRevealFlowContractTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val profile = ProfileEntity(userName = "Jerome", partnerName = "Alex")
    private val options = listOf("A", "B", "C", "D")

    @Test
    fun partnerPredictionRequiresExplicitRevealAfterPartnerChoice() {
        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                PartnerPredictionRevealBoard(
                    question = "Was wählt Alex?",
                    options = options,
                    selectedAnswer = null,
                    profile = profile,
                    onPick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("prediction_option_0").performClick()
        composeTestRule.onNodeWithTag("prediction_handoff_ready").performClick()
        composeTestRule.onNodeWithTag("prediction_actual_option_1").performClick()

        composeTestRule.onNodeWithTag("prediction_reveal_ready").assertExists()
        composeTestRule.onNodeWithTag("prediction_reveal_button").assertExists().performClick()
        composeTestRule.onNodeWithTag("prediction_result_guess").assertExists()
        composeTestRule.onNodeWithTag("prediction_result_actual").assertExists()
    }

    @Test
    fun secretChoiceRequiresExplicitRevealAfterBothHiddenChoices() {
        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                SecretChoiceRevealBoard(
                    question = "Was würdet ihr wählen?",
                    options = options,
                    selectedAnswer = null,
                    profile = profile,
                    onPick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("secret_first_option_0").performClick()
        composeTestRule.onNodeWithTag("secret_handoff_ready").performClick()
        composeTestRule.onNodeWithTag("secret_second_option_1").performClick()

        composeTestRule.onNodeWithTag("secret_reveal_ready").assertExists()
        composeTestRule.onNodeWithTag("secret_reveal_button").assertExists().performClick()
        composeTestRule.onNodeWithTag("secret_result_first").assertExists()
        composeTestRule.onNodeWithTag("secret_result_second").assertExists()
    }
}
