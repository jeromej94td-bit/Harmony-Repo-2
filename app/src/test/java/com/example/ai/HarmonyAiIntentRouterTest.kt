package com.example.ai

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class HarmonyAiIntentRouterTest {
    @Test
    fun sushiNearbyUsesMapsAndLocation() {
        val route = HarmonyAiIntentRouter.route("Zeig mir Sushi in meiner Nähe")
        assertEquals(HarmonyAiIntent.FOOD, route.intent)
        assertEquals(HarmonyGroundingMode.GOOGLE_MAPS, route.grounding)
        assertTrue(route.needsUserLocation)
    }

    @Test
    fun explicitBerlinRestaurantUsesMapsWithoutGpsRequirement() {
        val route = HarmonyAiIntentRouter.route("Zeig mir Restaurants in Berlin Tiergarten")
        assertEquals(HarmonyGroundingMode.GOOGLE_MAPS, route.grounding)
        assertFalse(route.needsUserLocation)
    }

    @Test
    fun newAnimeUsesSearch() {
        val route = HarmonyAiIntentRouter.route("Welche neuen Anime könnten wir schauen?")
        assertEquals(HarmonyAiIntent.ENTERTAINMENT, route.intent)
        assertEquals(HarmonyGroundingMode.GOOGLE_SEARCH, route.grounding)
    }

    @Test
    fun timelessRelationshipQuestionDoesNotWasteGrounding() {
        val route = HarmonyAiIntentRouter.route("Warum streiten Paare manchmal über Kleinigkeiten?")
        assertEquals(HarmonyAiIntent.RELATIONSHIP, route.intent)
        assertEquals(HarmonyGroundingMode.NONE, route.grounding)
    }
}
