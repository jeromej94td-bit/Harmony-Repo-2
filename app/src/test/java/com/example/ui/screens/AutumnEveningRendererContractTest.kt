package com.example.ui.screens

import com.example.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AutumnEveningRendererContractTest {

    @Test
    fun `every autumn round maps four images in answer order`() {
        val expected = mapOf(
            HarmonyImageChoiceKind.AUTUMN_STORY to listOf(
                R.drawable.autumn_story_01, R.drawable.autumn_story_02,
                R.drawable.autumn_story_03, R.drawable.autumn_story_04
            ),
            HarmonyImageChoiceKind.AUTUMN_DRINK to listOf(
                R.drawable.autumn_drink_01, R.drawable.autumn_drink_02,
                R.drawable.autumn_drink_03, R.drawable.autumn_drink_04
            ),
            HarmonyImageChoiceKind.AUTUMN_SNACK to listOf(
                R.drawable.autumn_snack_01, R.drawable.autumn_snack_02,
                R.drawable.autumn_snack_03, R.drawable.autumn_snack_04
            ),
            HarmonyImageChoiceKind.AUTUMN_NOOK to listOf(
                R.drawable.autumn_nook_01, R.drawable.autumn_nook_02,
                R.drawable.autumn_nook_03, R.drawable.autumn_nook_04
            ),
            HarmonyImageChoiceKind.AUTUMN_SOUND to listOf(
                R.drawable.autumn_sound_01, R.drawable.autumn_sound_02,
                R.drawable.autumn_sound_03, R.drawable.autumn_sound_04
            ),
            HarmonyImageChoiceKind.AUTUMN_SCENT to listOf(
                R.drawable.autumn_scent_01, R.drawable.autumn_scent_02,
                R.drawable.autumn_scent_03, R.drawable.autumn_scent_04
            )
        )

        assertEquals(expected.keys.toList(), AUTUMN_EVENING_KINDS)
        expected.forEach { (kind, images) ->
            assertEquals(images, autumnEveningVisuals(kind).images)
            assertEquals(4, autumnEveningVisuals(kind).images.distinct().size)
        }
    }

    @Test
    fun `every autumn round has a distinct short subtitle`() {
        val subtitles = AUTUMN_EVENING_KINDS.map { autumnEveningVisuals(it).subtitle }
        assertEquals(AUTUMN_EVENING_KINDS.size, subtitles.distinct().size)
        assertTrue(subtitles.all { it.isNotBlank() && it.length <= 52 })
    }
}
