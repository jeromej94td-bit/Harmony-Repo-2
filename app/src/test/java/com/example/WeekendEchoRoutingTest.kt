package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.data.model.AnswerEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.FullscreenGameMechanicKind
import com.example.data.model.ProfileEntity
import com.example.data.model.WeekendEchoSelector
import com.example.ui.screens.FullscreenQuestionMechanicBoard
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
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [35])
class WeekendEchoRoutingTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val history = listOf(
        AnswerEntity(
            WeekendEchoSelector.SOURCE_PACK_ID,
            8,
            EitherOrAnswerCodec.encode("Kino 🎬", "Couch & Decke 🛋️"),
            100L
        )
    )

    @Test
    fun `fixed weekend pack id renders the personal echo board`() {
        setScenario(WeekendEchoSelector.PACK_ID)

        composeRule.onNodeWithTag("weekend_echo_background").assertExists()
    }

    @Test
    fun `another scenario pack keeps the established adventure intro`() {
        setScenario("another_scenario")

        composeRule.onNodeWithTag("scenario_adventure_intro").assertExists()
    }

    private fun setScenario(packId: String) {
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                FullscreenQuestionMechanicBoard(
                    kind = FullscreenGameMechanicKind.SCENARIO,
                    packId = packId,
                    questionIndex = 3,
                    historicalAnswers = history,
                    question = "Dauerregen zerstört euren Plan. Was macht ihr?",
                    options = listOf("A", "B"),
                    selectedAnswer = null,
                    profile = ProfileEntity(userName = "Jerome", partnerName = "Alex"),
                    onPick = {}
                )
            }
        }
    }
}
