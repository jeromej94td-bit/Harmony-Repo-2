package com.example.ui.screens

import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.click
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureScreenRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [35])
@OptIn(ExperimentalRoborazziApi::class)
class SecretPlanVisualContractTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `magic book keeps its scene while the selected page turns`() {
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                SecretPlanBoard(
                    question = "Wofür nehmt ihr euch spontan einen freien Tag?",
                    options = listOf(
                        "Kleiner Roadtrip",
                        "Zeit nur für uns",
                        "Etwas Neues erleben",
                        "Ein Herzensprojekt starten",
                        "Eigene Idee …"
                    ),
                    questionIndex = 0,
                    historicalAnswers = emptyList(),
                    selectedAnswer = null,
                    profile = ProfileEntity(userName = "Jerome", partnerName = "Alex"),
                    onPick = {}
                )
            }
        }

        composeRule.mainClock.advanceTimeBy(1_000L)
        composeRule.onNodeWithTag("secret_plan_magic_book").assertExists()
        composeRule.onNodeWithTag("secret_plan_pandas").assertExists()
        composeRule.onNodeWithTag("secret_plan_heart_seal").assertExists()
        captureScreenRoboImage("build/secret-plan-preview/01-book-choice.png")

        composeRule.onNodeWithTag("secret_plan_first_option_0").performTouchInput { click() }
        composeRule.mainClock.advanceTimeBy(900L)

        composeRule.onNodeWithTag("secret_plan_first_option_0").assertIsNotEnabled()
        composeRule.onNodeWithTag("secret_plan_first_option_1").assertIsNotEnabled()
        captureScreenRoboImage("build/secret-plan-preview/02-page-turn.png")
    }
}
