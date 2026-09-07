package com.example

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import com.example.data.model.AnswerEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.data.model.WeekendEchoAnswerCodec
import com.example.data.model.WeekendEchoSelector
import com.example.ui.screens.WeekendEchoBoard
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
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
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [35])
class WeekendEchoBoardTest {
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
    fun `background and pandas never become answer controls`() {
        setBoard()

        composeRule.onNodeWithTag("weekend_echo_background").assertHasNoClickAction()
        composeRule.onNodeWithTag("weekend_echo_pandas").assertHasNoClickAction()
        composeRule.onNodeWithTag("weekend_echo_left_seal").assertHasClickAction()
        composeRule.onNodeWithTag("weekend_echo_right_seal").assertHasClickAction()
        composeRule.onAllNodes(hasClickAction()).assertCountEquals(2)
    }

    @Test
    fun `missing personal history returns to the standard scenario adventure`() {
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                WeekendEchoBoard(
                    question = "Dauerregen zerstört euren Plan. Was macht ihr?",
                    options = listOf("Wir improvisieren", "Wir bleiben drin"),
                    questionIndex = 3,
                    historicalAnswers = emptyList(),
                    selectedAnswer = null,
                    profile = ProfileEntity(userName = "Jerome", partnerName = "Alex"),
                    onPick = {}
                )
            }
        }

        composeRule.onNodeWithTag("scenario_adventure_intro").assertExists()
    }

    @Test
    fun `both private choices stay hidden until explicit reveal`() {
        setBoard()

        assertMotifsHidden()
        composeRule.onNodeWithTag("weekend_echo_left_seal").performClick()
        assertMotifsHidden()
        composeRule.onNodeWithTag("weekend_echo_handoff").performClick()
        composeRule.onNodeWithTag("weekend_echo_right_seal").performClick()
        assertMotifsHidden()
        composeRule.onNodeWithTag("weekend_echo_reveal").performClick()

        composeRule.onAllNodesWithText("Kino", substring = true).assertCountEquals(1)
        composeRule.onAllNodesWithText("Couch & Decke", substring = true).assertCountEquals(1)
        composeRule.onAllNodesWithText("Damals gewählt", substring = true).assertCountEquals(0)
        composeRule.onAllNodesWithText("Weiter zum nächsten Kapitel", substring = true).assertCountEquals(0)
    }

    @Test
    fun `opened seal submits both choices once`() {
        var saved: String? = null
        setBoard(onPick = { saved = it })

        composeRule.onNodeWithTag("weekend_echo_left_seal").performClick()
        composeRule.onNodeWithTag("weekend_echo_handoff").performClick()
        composeRule.onNodeWithTag("weekend_echo_right_seal").performClick()
        composeRule.onNodeWithTag("weekend_echo_reveal").performClick()
        assertNull(saved)
        composeRule.onNodeWithTag("weekend_echo_left_seal").performClick()

        assertEquals("couch_blanket", WeekendEchoAnswerCodec.decode(saved.orEmpty())?.firstMotifKey)
        assertEquals("cinema", WeekendEchoAnswerCodec.decode(saved.orEmpty())?.secondMotifKey)
    }

    @Test
    fun `revealed split scene keeps the approved visual hierarchy`() {
        setBoard()
        composeRule.onNodeWithTag("weekend_echo_left_seal").performClick()
        composeRule.onNodeWithTag("weekend_echo_handoff").performClick()
        composeRule.onNodeWithTag("weekend_echo_right_seal").performClick()
        composeRule.onNodeWithTag("weekend_echo_reveal").performClick()

        composeRule.onNodeWithTag("weekend_echo_background").assertHasNoClickAction()
        composeRule.onNodeWithTag("weekend_echo_pandas").assertHasNoClickAction()
        composeRule.onRoot().captureRoboImage(
            filePath = "build/weekend-echo-preview/revealed.png"
        )
    }

    private fun assertMotifsHidden() {
        composeRule.onAllNodesWithText("Kino", substring = true).assertCountEquals(0)
        composeRule.onAllNodesWithText("Couch & Decke", substring = true).assertCountEquals(0)
    }

    private fun setBoard(onPick: (String) -> Unit = {}) {
        composeRule.setContent {
            HarmonyTheme(darkTheme = true) {
                WeekendEchoBoard(
                    question = "Dauerregen zerstört euren Plan. Was macht ihr?",
                    options = listOf("Wir improvisieren", "Wir bleiben drin"),
                    questionIndex = 3,
                    historicalAnswers = history,
                    selectedAnswer = null,
                    profile = ProfileEntity(userName = "Jerome", partnerName = "Alex"),
                    onPick = onPick
                )
            }
        }
    }
}
