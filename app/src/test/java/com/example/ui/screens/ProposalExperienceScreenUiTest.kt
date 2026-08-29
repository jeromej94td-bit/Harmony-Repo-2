package com.example.ui.screens

import android.content.Context
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.ProfileEntity
import com.example.data.model.ProposalEitherOrRounds
import com.example.data.model.ProposalLocationDuels
import com.example.data.model.ProposalRingImageDuels
import com.example.ui.theme.HarmonyTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class ProposalExperienceScreenUiTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `intro starts dedicated proposal experience and either-or rounds reach location duels`() {
        composeRule.setContent {
            HarmonyTheme {
                ProposalExperienceScreen(
                    profile = ProfileEntity(userName = "Du", partnerName = "Partner"),
                    onClose = {}
                )
            }
        }

        composeRule.onNodeWithTag("proposal_experience_screen").assertExists()
        composeRule.onNodeWithTag("proposal_start").assertExists().performClick()
        composeRule.waitForIdle()

        listOf("proposal_mood", "proposal_details").forEach { stepId ->
            ProposalEitherOrRounds.roundsFor(stepId).forEach { round ->
                composeRule.onNodeWithText(round.prompt).assertExists()
                composeRule.onNodeWithText(round.firstChoice).assertExists().performClick()
                composeRule.waitForIdle()
            }
        }

        val firstLocation = ProposalLocationDuels.rounds.first()
        composeRule.onNodeWithTag("proposal_location_${firstLocation.firstOption.id}").assertExists()
        composeRule.onNodeWithTag("proposal_location_${firstLocation.secondOption.id}").assertExists()
    }

    @Test
    fun `all proposal ring duel asset keys resolve to packaged drawables`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        ProposalRingImageDuels.rounds
            .flatMap { round -> listOf(round.firstAssetKey, round.secondAssetKey) }
            .forEach { assetKey ->
                val resourceId = context.resources.getIdentifier(assetKey, "drawable", context.packageName)
                assertNotEquals("Missing proposal ring drawable: $assetKey", 0, resourceId)
            }
    }
}
