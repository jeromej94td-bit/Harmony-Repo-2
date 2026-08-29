package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360RelationshipTopicCurationTest {

    private val packs by lazy { GeneratedHarmonyAdrenaline360.PACKS.associateBy { it.id } }

    @Test
    fun misplacedRelationshipPacksMoveToExistingVisibleTopics() {
        assertEquals("geld", requirePack("h500_394_lottogewinn_ranking").topic)
        assertEquals("kennen", requirePack("h500_398_kindheitstraum_memory").topic)
        assertEquals("reisen", requirePack("h500_396_einsame_insel_szenario").topic)
        assertEquals("aufwaermen", requirePack("h500_391_zeitreise_entweder_oder").topic)
        assertEquals("aufwaermen", requirePack("h500_392_superkraefte_wer_eher").topic)
        assertEquals("aufwaermen", requirePack("h500_425_gemeinsamer_sieg_prognose").topic)
    }

    @Test
    fun communicationAndStrongPairContentStayInRelationship() {
        assertEquals("beziehung", requirePack("h500_026_zuhoeren_szenario").topic)
        assertEquals("beziehung", requirePack("h500_251_streitkultur_entweder_oder").topic)
        assertEquals("beziehung", requirePack("h500_402_telepathie_wer_eher").topic)
        assertEquals("beziehung", requirePack("h500_410_unsere_traumwelt_offene_runde").topic)
        assertEquals("beziehung", requirePack("h500_411_zusammenhalt_entweder_oder").topic)
    }

    @Test
    fun lowValueOrDuplicateFillerIsArchivedInsteadOfCreatingNewTopics() {
        listOf(
            "h500_401_tierverwandlung_entweder_oder",
            "h500_406_alien_begegnung_szenario",
            "h500_397_paralleluniversum_geheime_wahl",
            "h500_351_selbstreflexion_entweder_oder",
            "h500_354_bindungsmuster_ranking",
            "h500_359_emotionale_sicherheit_prioritaet",
            "h500_364_vertrauen_ranking",
            "h500_365_vergebung_prognose"
        ).forEach { id -> assertFalse("$id should be archived", packs.containsKey(id)) }
    }

    @Test
    fun moralAndPersonalPacksUseExistingDestinations() {
        assertEquals("moral", requirePack("h500_271_werte_im_alltag_entweder_oder").topic)
        assertEquals("moral", requirePack("h500_284_moral_ranking").topic)
        assertEquals("kennen", requirePack("h500_272_charaktereigenschaften_wer_eher").topic)
        assertEquals("kennen", requirePack("h500_278_praegende_momente_memory").topic)
    }

    @Test
    fun survivingCuratedPacksDoNotExposeKnownBrokenQuartets() {
        val brokenSets = setOf(
            setOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
            setOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
            setOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer")
        )
        val auditedIds = setOf(
            "h500_051_unser_naechstes_jahr_entweder_oder",
            "h500_054_traumhaus_ranking",
            "h500_271_werte_im_alltag_entweder_oder",
            "h500_284_moral_ranking",
            "h500_313_glaube_skala",
            "h500_339_gesellschaftliche_werte_prioritaet",
            "h500_353_verletzlichkeit_skala",
            "h500_361_selbstwertgefuehl_entweder_oder"
        )

        auditedIds.forEach { id ->
            val pack = requirePack(id)
            assertTrue("$id should still contain useful content", pack.questions.isNotEmpty())
            pack.questions.forEach { question ->
                assertFalse(
                    "$id still exposes a known broken generic answer quartet: ${question.options}",
                    question.options.toSet() in brokenSets
                )
            }
        }
    }

    @Test
    fun noFinalHarmony360QuestionExposesTheWorstBrokenQuartet() {
        val broken = setOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort")

        GeneratedHarmonyAdrenaline360.PACKS.forEach { pack ->
            pack.questions.forEach { question ->
                assertFalse(
                    "${pack.id} still exposes the broken generic answer quartet",
                    question.options.toSet() == broken
                )
            }
        }
    }

    @Test
    fun onlyExistingVisibleTopicIdsAreUsedByHarmony360() {
        val visibleTopics = setOf(
            "aufwaermen", "beziehung", "sex", "moral", "geld", "kennen",
            "reisen", "familie", "hobbys", "filme_serien", "essen"
        )

        GeneratedHarmonyAdrenaline360.PACKS.forEach { pack ->
            assertTrue("${pack.id} uses unknown topic ${pack.topic}", pack.topic in visibleTopics)
        }
    }

    private fun requirePack(id: String): GenPack {
        val pack = packs[id]
        assertNotNull("Expected pack $id to remain available", pack)
        return requireNotNull(pack)
    }
}
