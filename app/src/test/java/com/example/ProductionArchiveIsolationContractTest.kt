package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionArchiveIsolationContractTest {

    @Test
    fun `production build has an archived feature isolation gate`() {
        val gradle = source("app/build.gradle.kts")
        assertTrue(gradle.contains("verifyProductionSourceIsolation"))
        assertTrue(gradle.contains("verifyProductionSourceIsolation\n  )"))
        assertTrue(gradle.contains("brainEnabled = true"))
        assertTrue(gradle.contains("HARMONY_BRAIN_ENABLED = true"))
        assertTrue(gradle.contains("id = \\\"mischung\\\""))
    }

    @Test
    fun `production chat cannot render Harmony Brain`() {
        val chat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")
        assertFalse(chat.contains("Harmony Brain"))
        assertFalse(chat.contains("isBrainChatMode"))
        assertFalse(chat.contains("onSendBrainMessage"))
    }

    @Test
    fun `legacy brain surfaces stay disabled unless deliberately reintroduced`() {
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
        val games = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val viewModel = source("app/src/main/java/com/example/ui/HarmonyViewModel.kt")

        assertTrue(home.contains("brainEnabled: Boolean = false"))
        assertTrue(games.contains("brainEnabled: Boolean = false"))
        assertTrue(viewModel.contains("HARMONY_BRAIN_ENABLED = false"))
        assertFalse(main.contains("brainEnabled = true"))
    }

    @Test
    fun `archived brain network gateway fails closed`() {
        val gateway = source("app/src/main/java/com/example/data/brain/gateway/SupabaseHarmonyBrainGateway.kt")
        assertTrue(gateway.contains("feature_removed"))
        assertTrue(gateway.contains("Harmony Brain wurde aus der aktiven App entfernt"))
        assertFalse(gateway.contains("OkHttpClient"))
        assertFalse(gateway.contains("executeEdgeCall"))
    }

    @Test
    fun `removed Mischung category stays tombstoned at final catalog boundary`() {
        val policy = source("app/src/main/java/com/example/data/model/RemovedGameCatalogPolicy.kt")
        val models = source("app/src/main/java/com/example/data/model/Models.kt")

        assertTrue(policy.contains("MISCHUNG_CATEGORY_ID = \"mischung\""))
        assertTrue(policy.contains("categoryId != MISCHUNG_CATEGORY_ID"))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsCategoryId(it.id)"))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsPackCategoryId(it.cat)"))
    }

    @Test
    fun `archive recovery instructions are kept outside production source`() {
        val archive = source("docs/ARCHIVED_FEATURES.md")
        assertTrue(archive.contains("archive/pre-production-isolation-2026-09-02"))
        assertTrue(archive.contains("niemals vollständig zurück nach `main` mergen"))
    }

    private fun source(path: String): String {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path),
            File("../$path")
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
