package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360Stage052CurationTest {
    @Test
    fun `stage 05 2 recognizes exactly food travel leisure and culture sections`() {
        assertEquals(
            setOf(
                "h360_section_04_reisen_abenteuer",
                "h360_section_05_essen_genuss",
                "h360_section_07_freizeit_hobbys",
                "h360_section_14_kultur_medien"
            ),
            Harmony360FoodTravelLeisureCultureQualityRework.sectionTags
        )
    }

    @Test
    fun `unrelated section stays value equivalent`() {
        val pack = GenPack(
            id = "outside_stage_05_2",
            title = "Outside",
            cat = "tot",
            topic = "arbeit",
            tags = listOf("harmony360", "h360_section_10_arbeit_karriere")
        )

        assertEquals(
            listOf(pack),
            Harmony360FoodTravelLeisureCultureQualityRework.apply(listOf(pack))
        )
    }

    @Test
    fun `scoped pack is not changed before explicit curation decisions exist`() {
        val pack = GenPack(
            id = "stage_05_2_fixture",
            title = "Fixture",
            cat = "tot",
            topic = "reisen",
            tags = listOf("harmony360", "h360_section_04_reisen_abenteuer")
        )

        assertEquals(
            listOf(pack),
            Harmony360FoodTravelLeisureCultureQualityRework.apply(listOf(pack))
        )
    }

    @Test
    fun `raw stage 05 2 inventory contains four complete sections with unique ids`() {
        val sections = listOf(
            "h360_section_04_reisen_abenteuer" to GeneratedHarmonyAdrenaline360Section04ReisenAbenteuer.PACKS,
            "h360_section_05_essen_genuss" to GeneratedHarmonyAdrenaline360Section05EssenGenuss.PACKS,
            "h360_section_07_freizeit_hobbys" to GeneratedHarmonyAdrenaline360Section07FreizeitHobbys.PACKS,
            "h360_section_14_kultur_medien" to GeneratedHarmonyAdrenaline360Section14KulturMedien.PACKS
        )

        assertEquals(4, sections.size)
        assertTrue(sections.all { (_, packs) -> packs.size == 18 })
        assertTrue(sections.all { (tag, packs) -> packs.all { tag in it.tags } })

        val packs = sections.flatMap { it.second }
        assertEquals(72, packs.size)
        assertEquals(packs.size, packs.map { it.id }.toSet().size)
    }
}
