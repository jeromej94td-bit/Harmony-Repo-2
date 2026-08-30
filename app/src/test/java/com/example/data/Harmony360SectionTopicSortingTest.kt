package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class Harmony360SectionTopicSortingTest {

    @Test
    fun `curation preservation never drops a protected baseline pack`() {
        val baseline = GeneratedHarmonyAdrenaline360Section03ZukunftLebensplanung.PACKS.take(4)
        val curated = baseline.drop(1)

        val restored = Harmony360CurationPackPreservation.apply(baseline, curated)

        assertEquals(baseline.map { it.id }, restored.map { it.id })
    }

    @Test
    fun `future topics are sorted by their own section rules`() {
        val sorted = Harmony360SectionTopicSorting.apply(
            GeneratedHarmonyAdrenaline360Section03ZukunftLebensplanung.PACKS
        ).associateBy { it.id }

        assertEquals("kennen", sorted.getValue("h500_054_traumhaus_ranking").topic)
        assertEquals("reisen", sorted.getValue("h500_056_auswandern_szenario").topic)
        assertEquals("geld", sorted.getValue("h500_058_finanzielle_ziele_memory").topic)
        assertEquals("familie", sorted.getValue("h500_060_hochzeit_offene_runde").topic)
        assertEquals("familie", sorted.getValue("h500_061_familienplanung_entweder_oder").topic)
        assertEquals("reisen", sorted.getValue("h500_064_abenteuerliste_ranking").topic)
        assertEquals("kennen", sorted.getValue("h500_066_wohnort_szenario").topic)
        assertEquals("moral", sorted.getValue("h500_070_sicherheit_oder_freiheit_offene_runde").topic)
        assertEquals("kennen", sorted.getValue("h500_075_das_leben_mit_60_prognose").topic)
    }

    @Test
    fun `alltag und zuhause is routed from its real section ids`() {
        val sorted = Harmony360SectionTopicSorting.apply(
            GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS
        ).associateBy { it.id }

        listOf(
            "h500_126_morgenroutine_szenario",
            "h500_127_abendroutine_geheime_wahl",
            "h500_128_haushalt_memory",
            "h500_129_ordnung_prioritaet",
            "h500_132_einkaufen_wer_eher",
            "h500_134_schlafen_ranking",
            "h500_135_homeoffice_prognose",
            "h500_136_dekoration_szenario",
            "h500_138_haustiere_memory",
            "h500_139_besuch_bekommen_prioritaet",
            "h500_143_gemeinsame_to_do_liste_skala",
            "h500_144_technik_zuhause_ranking",
            "h500_148_wohnzimmer_memory",
            "h500_149_balkon_prioritaet"
        ).forEach { id -> assertEquals("kennen", sorted.getValue(id).topic) }
        assertEquals("essen", sorted.getValue("h500_133_kochen_im_alltag_skala").topic)
        assertEquals("hobbys", sorted.getValue("h500_141_sonntage_entweder_oder").topic)
        assertEquals("hobbys", sorted.getValue("h500_142_feierabend_wer_eher").topic)
        assertEquals("beziehung", sorted.getValue("h500_150_unser_gemuetlichster_abend_offene_runde").topic)
    }

    @Test
    fun `work health conflict and psychology topics are placed outside Beziehung where appropriate`() {
        val input =
            GeneratedHarmonyAdrenaline360Section10ArbeitKarriere.PACKS +
                GeneratedHarmonyAdrenaline360Section11GesundheitFitness.PACKS +
                GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS +
                GeneratedHarmonyAdrenaline360Section17PsychologieGefuehle.PACKS
        val sorted = Harmony360SectionTopicSorting.apply(input).associateBy { it.id }

        assertEquals("kennen", sorted.getValue("h500_216_work_life_balance_szenario").topic)
        assertEquals("geld", sorted.getValue("h500_225_ruhestand_prognose").topic)
        assertEquals("kennen", sorted.getValue("h500_226_kuendigung_szenario").topic)
        assertEquals("kennen", sorted.getValue("h500_238_krank_sein_memory").topic)
        assertEquals("kennen", sorted.getValue("h500_250_gemeinsame_gesundheit_offene_runde").topic)

        listOf(
            "h500_254_kompromisse_ranking",
            "h500_257_geheimnisse_geheime_wahl",
            "h500_260_ehrlichkeit_offene_runde"
        ).forEach { id -> assertEquals("moral", sorted.getValue(id).topic) }

        listOf(
            "h500_352_einfuehlungsvermoegen_wer_eher",
            "h500_353_verletzlichkeit_skala",
            "h500_357_wuensche_und_beduerfnisse_geheime_wahl",
            "h500_358_kindheitstraumata_memory",
            "h500_361_selbstwertgefuehl_entweder_oder",
            "h500_362_troesten_wer_eher",
            "h500_366_stressreaktionen_szenario",
            "h500_367_sehnsuechte_geheime_wahl"
        ).forEach { id -> assertEquals("kennen", sorted.getValue(id).topic) }
    }

    @Test
    fun `final Harmony 360 pipeline keeps every protected pack that survived explicit Stage051 and Normens deletions`() {
        val raw = buildList {
            addAll(GeneratedHarmonyAdrenaline360Section01BeziehungNaehe.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section02Kommunikation.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section03ZukunftLebensplanung.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section04ReisenAbenteuer.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section05EssenGenuss.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section06AlltagZuhause.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section07FreizeitHobbys.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section08FreundeFamilie.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section09GeldFinanzen.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section10ArbeitKarriere.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section11GesundheitFitness.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section12KommunikationKonflikte.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section13PersoenlichkeitWerte.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section14KulturMedien.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section15GlaubeReligion.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section16PolitikGesellschaft.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section17PsychologieGefuehle.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section18HumorLachen.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section19FantasieWasWaereWenn.PACKS)
            addAll(GeneratedHarmonyAdrenaline360Section20TeamworkChallenge.PACKS)
        }
        val baseline = NormensLoeschungen.apply(
            Harmony360RelationshipStage051Pipeline.apply(
                raw
                    .map(GeneratedHarmony360ScenarioCleanup::apply)
                    .map(GeneratedHarmony360TextCleanup::apply)
                    .map(Harmony360ContentRework::apply)
            )
        )
        val protectedBaselineIds = baseline
            .filter(Harmony360CurationPackPreservation::shouldPreserve)
            .map { it.id }
            .toSet()
        val finalIds = GeneratedHarmonyAdrenaline360.PACKS.map { it.id }.toSet()

        assertTrue(finalIds.containsAll(protectedBaselineIds))
        assertTrue(finalIds.isNotEmpty())
    }
}
