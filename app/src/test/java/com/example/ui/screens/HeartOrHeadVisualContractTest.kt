package com.example.ui.screens

import androidx.activity.ComponentActivity
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35], qualifiers = "w390dp-h844dp-xhdpi")
class HeartOrHeadVisualContractTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `date round renders four Harmony Panda cards`() {
        val selected = mutableStateOf<String?>(null)
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                HeartOrHeadQuestion(
                    question = "Welcher Abend fühlt sich am meisten nach dir an?",
                    options = listOf(
                        "Spontaner Nachtspaziergang",
                        "Geplantes Dinner",
                        "Picknick bei Sonnenuntergang",
                        "Gemütlicher Filmabend"
                    ),
                    selectedAnswer = selected.value,
                    kind = HarmonyImageChoiceKind.HEART_HEAD_DATE,
                    onPick = { selected.value = it }
                )
            }
        }

        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.waitForIdle()
        (0 until 4).forEach { index ->
            composeRule.onNodeWithTag("heart_head_option_$index", useUnmergedTree = true).assertExists()
        }
        captureRoboImage(File("build/outputs/roborazzi/heart_or_head_date_preview.png"))
    }

    @Test
    fun `final round renders symbolic heart head instinct balance cards`() {
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                HeartOrHeadQuestion(
                    question = "Wenn du lieben müsstest – worauf vertraust du zuerst?",
                    options = listOf("Herz", "Kopf", "Bauchgefühl", "Balance"),
                    selectedAnswer = null,
                    kind = HarmonyImageChoiceKind.HEART_HEAD_FINAL,
                    onPick = {}
                )
            }
        }

        composeRule.mainClock.advanceTimeBy(2_000)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("heart_or_head_question", useUnmergedTree = true).assertExists()
        captureRoboImage(File("build/outputs/roborazzi/heart_or_head_final_preview.png"))
    }
}
