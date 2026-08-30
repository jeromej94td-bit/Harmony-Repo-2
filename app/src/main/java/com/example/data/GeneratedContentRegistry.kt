package com.example.data

import com.example.data.model.InteractionPromptPolicy
import com.example.data.model.LoveBalanceQuestionPolicy

/**
 * Vereinigt den bisherigen generierten Harmony-Content mit zusätzlichen
 * Dev-Studio-Imports, ohne GeneratedHarmonyContent.kt destruktiv zu ersetzen.
 *
 * Ältere GeneratedHarmonyContent-Dateien besitzen noch keine ASSETS-Metadaten.
 * Deshalb stammen die Asset-Metadaten ausschließlich aus den neuen additiven
 * Imports; bestehende PACKS/IMAGES bleiben vollständig erhalten. Bereits auf
 * main verwaltete Standard-Assets bleiben bewusst bei ihren aktuellen Installern.
 */
object GeneratedContentRegistry {
    val VERSION: Long =
        (GeneratedHarmonyContent.VERSION * 31L) xor
            GeneratedHarmonyNewPicGame.VERSION xor
            GeneratedHarmonyAdrenaline360.VERSION xor
            GeneratedHarmonyHappyCouple.VERSION xor
            GeneratedHarmonySexIntimacyRework.VERSION

    val CATEGORIES: List<GenCategory> by lazy {
        (GeneratedHarmonyContent.CATEGORIES + GeneratedHarmonyNewPicGame.CATEGORIES + GeneratedHarmonyAdrenaline360.CATEGORIES)
            .distinctBy { it.id }
    }

    private fun runtimePack(pack: GenPack): GenPack {
        var result = Harmony360SectionTopicSorting.apply(
            GeneratedContentRepairPolicy.repair(pack)
        )

        // Deep Talk owns a dedicated full-screen, two-person reveal flow. The legacy
        // "disc" runner renders every question in one long discussion list and would
        // otherwise swallow this mechanic before the fullscreen router can see it.
        result = if (result.type == "disc" && "mechanik_deep_talk" in result.tags) {
            result.copy(type = "quiz")
        } else {
            result
        }

        // Fullscreen mechanics own their answer presentation. Some generated questions
        // repeat the same choices in the sentence (for example "Ordne A, B, C, D") and
        // then show A-D again as cards. Strip only a detected repeated option tail.
        if (result.tags.any { it.startsWith("mechanik_") }) {
            result = result.copy(
                questions = result.questions.map { question ->
                    question.copy(
                        q = InteractionPromptPolicy.displayPrompt(question.q, question.options)
                    )
                }
            )
        }
        return result
    }

    /**
     * Old Dev-Studio exports can still contain stale copies of generated packs. CUSTOM
     * intentionally wins over GENERATED later in DeveloperDataManager, so repair those
     * loaded custom copies before the merge instead of allowing old metadata/content to
     * hide the curated runtime versions again.
     */
    private fun normalizeLoadedCustomPacks() {
        val customPacks = DeveloperDataManager._customPacks
        for (index in customPacks.indices) {
            val retopiced = Harmony360SectionTopicSorting.apply(customPacks[index])
            if (retopiced != customPacks[index]) {
                customPacks[index] = retopiced
            }

            val pack = customPacks[index]
            if (
                pack.id == LoveBalanceQuestionPolicy.PACK_ID &&
                pack.type == "quiz" &&
                (
                    !LoveBalanceQuestionPolicy.isHappyCoupleQuestionText(pack.questions.firstOrNull()?.q) ||
                        pack.questions.count { LoveBalanceQuestionPolicy.isHappyCoupleQuestionText(it.q) } != 1
                    )
            ) {
                customPacks[index] = LoveBalanceQuestionPolicy.ensureHappyCoupleFirst(pack)
            }
        }
    }

    private val cachedPacks: List<GenPack> by lazy {
        val byId = LinkedHashMap<String, GenPack>()
        GeneratedHarmonyContent.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        GeneratedHarmonyNewPicGame.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        GeneratedHarmonyAdrenaline360.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        GeneratedHarmonyHappyCouple.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        // Curated reworks come last so their stable pack IDs intentionally override
        // older generated/default variants at runtime without touching Models.kt.
        GeneratedHarmonySexIntimacyRework.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        byId.values.toList()
    }

    val PACKS: List<GenPack>
        get() {
            normalizeLoadedCustomPacks()
            return cachedPacks
        }

    val LINK_PACKS: List<GenLinkPack> by lazy {
        (GeneratedHarmonyContent.LINK_PACKS + GeneratedHarmonyNewPicGame.LINK_PACKS)
            .distinctBy { it.id }
    }

    val ASSETS: List<GenAssetMeta> by lazy {
        GeneratedHarmonyNewPicGame.ASSETS.distinctBy { it.optionKey }
    }

    val IMAGES: Map<String, String> by lazy {
        LinkedHashMap<String, String>().apply {
            putAll(GeneratedHarmonyContent.IMAGES)
            putAll(GeneratedHarmonyNewPicGame.IMAGES)
        }
    }
}
