package com.example.data

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
        (GeneratedHarmonyContent.VERSION * 31L) xor GeneratedHarmonyNewPicGame.VERSION xor GeneratedHarmonyAdrenaline360.VERSION

    val CATEGORIES: List<GenCategory> by lazy {
        (GeneratedHarmonyContent.CATEGORIES + GeneratedHarmonyNewPicGame.CATEGORIES + GeneratedHarmonyAdrenaline360.CATEGORIES)
            .distinctBy { it.id }
    }

    private fun runtimePack(pack: GenPack): GenPack {
        // Deep Talk owns a dedicated full-screen, two-person reveal flow. The legacy
        // "disc" runner renders every question in one long discussion list and would
        // otherwise swallow this mechanic before the fullscreen router can see it.
        return if (pack.type == "disc" && "mechanik_deep_talk" in pack.tags) {
            pack.copy(type = "quiz")
        } else {
            pack
        }
    }

    val PACKS: List<GenPack> by lazy {
        val byId = LinkedHashMap<String, GenPack>()
        GeneratedHarmonyContent.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        GeneratedHarmonyNewPicGame.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        GeneratedHarmonyAdrenaline360.PACKS.forEach { pack -> runtimePack(pack).also { byId[it.id] = it } }
        byId.values.toList()
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
