package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.data.model.HarmonyPacksData
import com.example.ui.components.AmbientBackground
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.captureRoboImage
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
class HarmonyImageChoiceUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `travel grid exposes twelve cards and stores the tapped answer`() {
        val question = HarmonyPacksData.DEFAULT_PACKS
            .first { it.id == "reisevor" }
            .questions[4]
        var pickedAnswer by mutableStateOf<String?>(null)

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    HarmonyImageChoiceQuestion(
                        kind = HarmonyImageChoiceKind.TRAVEL,
                        question = question.q,
                        options = question.options,
                        selectedAnswer = pickedAnswer,
                        onPick = { pickedAnswer = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        composeRule.mainClock.advanceTimeBy(2_200L)
        composeRule.onNodeWithText("Wie sieht deine Traumreise aus?").assertExists()
        composeRule.onNodeWithTag("harmony_image_choice_option_0").performClick()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("harmony_image_choice_option_11").assertExists()
        composeRule.onNodeWithTag("harmony_image_choice_option_0_selected").assertExists()

        assertEquals(question.options.first(), pickedAnswer)
        composeRule.onNodeWithTag("harmony_image_choice_question").captureRoboImage(
            filePath = "build/harmony-image-choice-preview/travel-question.png"
        )
    }

    @Test
    fun `happy couple question reveals four tappable image cards without helper footer`() {
        val question = HarmonyPacksData.PACKS
            .first { it.id == "liebegleichgewicht" }
            .questions.first()
        var pickedAnswer by mutableStateOf<String?>(null)

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    HarmonyImageChoiceQuestion(
                        kind = HarmonyImageChoiceKind.HAPPY_COUPLE,
                        question = question.q,
                        options = question.options,
                        selectedAnswer = pickedAnswer,
                        onPick = { pickedAnswer = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        composeRule.mainClock.advanceTimeBy(3_000L)
        composeRule.onNodeWithText("Welches Paar ist GLÜCKLICH?").assertExists()
        composeRule.onNodeWithText("Wähle das Paar, das für dich am glücklichsten wirkt.").assertExists()
        composeRule.onNodeWithText("Die Optionen erscheinen nacheinander – wie Karten, die sich nach und nach aufdecken.").assertDoesNotExist()
        composeRule.onNodeWithText("Bitte wähle ein Paar").assertDoesNotExist()

        (0 until 4).forEach { index ->
            composeRule.onNodeWithTag("happy_couple_option_$index").assertExists()
        }

        composeRule.onNodeWithTag("happy_couple_option_2").performClick()
        composeRule.mainClock.advanceTimeByFrame()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("happy_couple_option_2_selected").assertExists()
        assertEquals("3", pickedAnswer)

        composeRule.onNodeWithTag("harmony_happy_couple_question").captureRoboImage(
            filePath = "build/harmony-image-choice-preview/happy-couple-question.png"
        )
    }

    @Test
    fun `egg question renders all twelve source images`() {
        captureQuestion(
            packId = "essenreden",
            questionIndex = 3,
            kind = HarmonyImageChoiceKind.EGG,
            fileName = "egg-question.png"
        )
    }

    @Test
    fun `steak question renders all twelve source images`() {
        captureQuestion(
            packId = "essenreden",
            questionIndex = 4,
            kind = HarmonyImageChoiceKind.STEAK,
            fileName = "steak-question.png"
        )
    }

    private fun captureQuestion(
        packId: String,
        questionIndex: Int,
        kind: HarmonyImageChoiceKind,
        fileName: String
    ) {
        val question = HarmonyPacksData.DEFAULT_PACKS
            .first { it.id == packId }
            .questions[questionIndex]

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    HarmonyImageChoiceQuestion(
                        kind = kind,
                        question = question.q,
                        options = question.options,
                        selectedAnswer = null,
                        onPick = {},
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        composeRule.mainClock.advanceTimeBy(2_200L)
        composeRule.onNodeWithTag("harmony_image_choice_option_11").assertExists()
        composeRule.onNodeWithTag("harmony_image_choice_question").captureRoboImage(
            filePath = "build/harmony-image-choice-preview/$fileName"
        )
    }
}
