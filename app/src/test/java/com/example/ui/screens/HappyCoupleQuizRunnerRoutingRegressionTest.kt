package com.example.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProfileEntity
import com.example.ui.ActivePackRun
import com.example.ui.theme.HarmonyTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.LEGACY)
@Config(qualifiers = "w411dp-h1100dp-xxhdpi", sdk = [35])
class HappyCoupleQuizRunnerRoutingRegressionTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `love balance first question uses four image cards instead of generic answer buttons`() {
        val pack = HarmonyPacksData.PACKS.first { it.id == "liebegleichgewicht" }
        var pickedAnswer by mutableStateOf<String?>(null)

        renderRunner(pack = pack, onPickAnswer = { pickedAnswer = it })
        composeRule.mainClock.advanceTimeBy(3_000L)

        (1..4).forEach { number ->
            composeRule.onNodeWithTag("happy_couple_number_$number").assertExists()
        }
        (1..4).forEach { number ->
            composeRule.onNodeWithTag("quiz_option_$number").assertDoesNotExist()
        }

        (0..3).forEach { index ->
            composeRule.onNodeWithTag("happy_couple_option_$index").performClick()
            composeRule.mainClock.advanceTimeByFrame()
            composeRule.waitForIdle()
            assertEquals((index + 1).toString(), pickedAnswer)
        }
    }

    @Test
    fun `ordinary quiz question keeps generic answer buttons`() {
        val pack = HarmonyPacksData.PACKS.first {
            it.type == "quiz" && it.id != "liebegleichgewicht"
        }

        renderRunner(pack = pack, onPickAnswer = {})

        composeRule.onNodeWithTag("quiz_option_1").assertExists()
        composeRule.onNodeWithTag("happy_couple_option_0").assertDoesNotExist()
    }

    private fun renderRunner(
        pack: com.example.data.model.QuestionPack,
        onPickAnswer: (String) -> Unit
    ) {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                QuizRunnerScreen(
                    activeRun = ActivePackRun(pack = pack),
                    profile = ProfileEntity(userName = "Du", partnerName = "Partner"),
                    isExitConfirmOpen = false,
                    isOwnAnswerDialogOpen = false,
                    appLanguage = "de",
                    onPickAnswer = onPickAnswer,
                    onPickTot = {},
                    onNextStep = {},
                    onAskExit = {},
                    onCloseExitConfirm = {},
                    onCloseRunner = {},
                    onOpenOwnAnswerDialog = { _, _ -> },
                    onCloseOwnAnswerDialog = {},
                    onSaveOwnAnswer = {}
                )
            }
        }
    }
}
