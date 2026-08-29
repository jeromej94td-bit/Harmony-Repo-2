package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertDoesNotExist
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import com.example.data.model.ProposalLocationDuels
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class ProposalLocationDuelBoardTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `cards arrive before question and selection transitions exactly once`() {
        val round = ProposalLocationDuels.rounds.first()
        var pickedOptionId: String? = null
        var transitionCount = 0

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme {
                Box(Modifier.fillMaxSize()) {
                    AnimatedProposalLocationDuelBoard(
                        round = round,
                        selectedOptionId = null,
                        onPick = { pickedOptionId = it.id },
                        onTransitionFinished = { transitionCount += 1 }
                    )
                }
            }
        }

        composeRule.onNodeWithTag("proposal_location_${round.firstOption.id}").assertExists()
        composeRule.onNodeWithTag("proposal_location_${round.secondOption.id}").assertExists()
        composeRule.onNodeWithTag("proposal_location_question").assertDoesNotExist()

        composeRule.mainClock.advanceTimeBy(1_000)
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("proposal_location_question").assertExists()

        composeRule.onNodeWithTag("proposal_location_${round.firstOption.id}").performClick()
        composeRule.waitForIdle()
        assertEquals(round.firstOption.id, pickedOptionId)
        composeRule.onNodeWithTag("proposal_location_${round.secondOption.id}").assertIsNotEnabled()

        composeRule.mainClock.advanceTimeBy(1_300)
        composeRule.waitForIdle()
        assertEquals(1, transitionCount)
    }

    @Test
    fun `sequence advances through all three location duels and completes`() {
        val rounds = ProposalLocationDuels.rounds
        var completedSelections: Map<String, String>? = null

        composeRule.mainClock.autoAdvance = false
        composeRule.setContent {
            HarmonyTheme {
                ProposalLocationDuelSequence(
                    rounds = rounds,
                    onComplete = { completedSelections = it }
                )
            }
        }

        rounds.forEachIndexed { index, round ->
            composeRule.mainClock.advanceTimeBy(1_000)
            composeRule.waitForIdle()
            composeRule.onNodeWithTag("proposal_location_${round.firstOption.id}").performClick()
            composeRule.mainClock.advanceTimeBy(1_300)
            composeRule.waitForIdle()

            if (index < rounds.lastIndex) {
                val nextRound = rounds[index + 1]
                composeRule.onNodeWithTag("proposal_location_${nextRound.firstOption.id}").assertExists()
            }
        }

        val result = completedSelections
        assertNotNull(result)
        rounds.forEach { round ->
            assertEquals(round.firstOption.id, result?.get(round.id))
        }
    }

    @Test
    fun `duel exposes harmony choice numbers`() {
        val round = ProposalLocationDuels.rounds.first()

        composeRule.setContent {
            HarmonyTheme {
                ProposalLocationDuelBoard(
                    round = round,
                    selectedOptionId = null,
                    onPick = {}
                )
            }
        }

        composeRule.onNodeWithTag("proposal_location_choice_1").assertExists()
        composeRule.onNodeWithTag("proposal_location_choice_2").assertExists()
    }
}
