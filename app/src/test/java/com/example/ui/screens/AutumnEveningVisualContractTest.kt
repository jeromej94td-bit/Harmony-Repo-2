package com.example.ui.screens

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.example.ui.components.AmbientBackground
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.LEGACY)
@Config(qualifiers = "de-rDE-w411dp-h1100dp-xxhdpi", sdk = [35])
class AutumnEveningVisualContractTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `drink round renders all cards and copper selection state`() {
        val selected = mutableStateOf<String?>(null)
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    AutumnEveningQuestion(
                        kind = HarmonyImageChoiceKind.AUTUMN_DRINK,
                        question = "Was wärmt deinen Abend?",
                        options = listOf(
                            "Chai Latte",
                            "Heiße Schokolade",
                            "Apfel-Zimt-Tee",
                            "Pumpkin Spice"
                        ),
                        selectedAnswer = selected.value,
                        onPick = { selected.value = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        composeRule.mainClock.advanceTimeBy(2_000L)
        listOf("Chai Latte", "Heiße Schokolade", "Apfel-Zimt-Tee", "Pumpkin Spice")
            .forEach { composeRule.onNodeWithText(it).assertExists() }
        composeRule.onNodeWithText("Chai Latte").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("autumn_evening_option_0_selected").assertExists()
        composeRule.onNodeWithTag("autumn_evening_question").captureRoboImage(
            filePath = "build/autumn-evening-preview/drink.png"
        )
    }

    @Test
    fun `window nook round renders readable rain choice and selection state`() {
        val selected = mutableStateOf<String?>(null)
        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                AmbientBackground {
                    AutumnEveningQuestion(
                        kind = HarmonyImageChoiceKind.AUTUMN_NOOK,
                        question = "Wo wird es richtig gemütlich?",
                        options = listOf(
                            "Fensternest",
                            "Kaminsofa",
                            "Deckenhöhle",
                            "Bibliotheksecke"
                        ),
                        selectedAnswer = selected.value,
                        onPick = { selected.value = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        composeRule.mainClock.advanceTimeBy(2_000L)
        listOf("Fensternest", "Kaminsofa", "Deckenhöhle", "Bibliotheksecke")
            .forEach { composeRule.onNodeWithText(it).assertExists() }
        composeRule.onNodeWithText("Fensternest").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("autumn_evening_option_0_selected").assertExists()
        composeRule.onNodeWithTag("autumn_evening_question").captureRoboImage(
            filePath = "build/autumn-evening-preview/nook.png"
        )
    }
}
