package com.example.data

/** Small aggregator; content is split into 20 section objects to avoid JVM <clinit> size limits. */
object GeneratedHarmonyAdrenaline360 {
    const val VERSION: Long = 1787868780002L

    val CATEGORIES: List<GenCategory> = listOf(
        GenCategory(id="h360_skala", name="Skalen-Match", emoji="🎚️", color=0xFF9DB2FF),
        GenCategory(id="h360_ranking", name="Ranking-Duell", emoji="🏆", color=0xFFFFC46B),
        GenCategory(id="h360_prognose", name="Partner-Prognose", emoji="🔮", color=0xFFC89BE0),
        GenCategory(id="h360_szenario", name="Was würdest du tun?", emoji="🎭", color=0xFF7BD8CB),
        GenCategory(id="h360_geheim", name="Geheime Wahl", emoji="🤫", color=0xFFFF70A6),
        GenCategory(id="h360_memory", name="Erinnerungs-Match", emoji="🧠", color=0xFF9D4EDD),
        GenCategory(id="h360_prioritaet", name="Prioritäten-Poker", emoji="🃏", color=0xFFFF6B8F),
    )

    val PACKS: List<GenPack> by lazy {
        val raw = buildList<GenPack> {
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
        Harmony360RelationshipTopicCuration.apply(
            Harmony360TopicNormalizationCuration.apply(
                NormensLoeschungen.apply(
                    Harmony360RelationshipStage051Pipeline.apply(
                        raw
                            .map(GeneratedHarmony360ScenarioCleanup::apply)
                            .map(GeneratedHarmony360TextCleanup::apply)
                            .map(Harmony360ContentRework::apply)
                    )
                )
            )
        )
    }
}
