package com.example

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.model.ExperiencePartnerPredictionRound
import com.example.data.model.ExperiencePartnerPredictionSelection
import com.example.data.model.FullscreenGameMechanicKind
import com.example.data.model.ProfileEntity
import com.example.ui.screens.ExperiencePartnerPredictionBoard
import com.example.ui.screens.FullscreenQuestionMechanicBoard
import com.example.ui.screens.PartnerPredictionRevealBoard
import com.example.ui.screens.SecretChoiceRevealBoard
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
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

        composeTestRule.onNodeWithTag("prediction_reveal_ready").fetchSemanticsNode()
        composeTestRule.onNodeWithTag("prediction_reveal_button").performClick()
        composeTestRule.onNodeWithTag("prediction_result_guess").fetchSemanticsNode()
        composeTestRule.onNodeWithTag("prediction_result_actual").fetchSemanticsNode()
    }

    @Test
    fun harmony360PartnerPredictionStoresBothChoicesAndContinuesWithoutQuestionReveal() {
        var picked: ExperiencePartnerPredictionSelection? = null
        val round = ExperiencePartnerPredictionRound(
            id = "feedback",
            prompt = "Wie hört Alex kritisches Feedback am liebsten?",
            options = options
        )

        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                ExperiencePartnerPredictionBoard(
                    round = round,
                    selectedSelection = null,
                    profile = profile,
                    onPick = { picked = it }
                )
            }
        }

        composeTestRule.onNodeWithTag("prediction_option_0").performClick()
        composeTestRule.onNodeWithTag("prediction_handoff_ready").performClick()
        composeTestRule.onNodeWithTag("prediction_actual_option_1").performClick()

        assertEquals(
            ExperiencePartnerPredictionSelection(prediction = "A", actual = "B"),
            picked
        )
        composeTestRule.onAllNodesWithTag("prediction_reveal_ready").assertCountEquals(0)
        composeTestRule.onAllNodesWithTag("prediction_result_guess").assertCountEquals(0)
        composeTestRule.onAllNodesWithTag("prediction_result_actual").assertCountEquals(0)
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

        composeTestRule.onNodeWithTag("secret_reveal_ready").fetchSemanticsNode()
        composeTestRule.onNodeWithTag("secret_reveal_button").performClick()
        composeTestRule.onNodeWithTag("secret_result_first").fetchSemanticsNode()
        composeTestRule.onNodeWithTag("secret_result_second").fetchSemanticsNode()
    }

    @Test
    fun scaleMatchStartsNeutralAndRequiresExplicitSharedReveal() {
        val scaleOptions = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")

        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                FullscreenQuestionMechanicBoard(
                    kind = FullscreenGameMechanicKind.SCALE_MATCH,
                    question = "Wie spontan seid ihr?",
                    options = scaleOptions,
                    selectedAnswer = null,
                    profile = profile,
                    onPick = {}
                )
            }
        }

        composeTestRule.onNodeWithTag("scale_selected_value").assertTextContains("3")
        composeTestRule.onNodeWithTag("scale_first_lock").performClick()
        composeTestRule.onNodeWithTag("scale_handoff_ready").performClick()
        composeTestRule.onNodeWithTag("scale_second_lock").performClick()

        composeTestRule.onNodeWithTag("scale_reveal_ready").fetchSemanticsNode()
        composeTestRule.onNodeWithTag("scale_reveal_button").performClick()
        composeTestRule.onNodeWithTag("scale_result_card").fetchSemanticsNode()
    }

    @Test
    fun whoWouldSelectionDoesNotSubmitUntilConfirmed() {
        var picked: String? = null
        val whoOptions = listOf("{user}", "{partner}", "Beide", "Niemand")

        composeTestRule.setContent {
            HarmonyTheme(darkTheme = true) {
                FullscreenQuestionMechanicBoard(
                    kind = FullscreenGameMechanicKind.WHO_WOULD,
                    question = "Wer plant eher spontan einen Ausflug?",
                    options = whoOptions,
                    selectedAnswer = null,
                    profile = profile,
                    onPick = { picked = it }
                )
            }
        }

        composeTestRule.onNodeWithTag("who_user").performClick()
        assertNull(picked)
        composeTestRule.onNodeWithTag("who_confirm").performClick()
        assertEquals("{user}", picked)
    }
}
