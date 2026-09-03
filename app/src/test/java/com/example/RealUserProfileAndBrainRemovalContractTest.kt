package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RealUserProfileAndBrainRemovalContractTest {

    @Test
    fun `real account profile is driven by AppSession and exposes pairing directly`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(profile.contains("session: AppSession"))
        assertTrue(profile.contains("session.profile.displayName"))
        assertTrue(profile.contains("session.email"))
        assertTrue(profile.contains("partner_connection_button"))
        assertTrue(main.contains("session = appSession"))

        assertFalse(profile.contains("currentSessionOrNull()?.user?.email"))
        assertFalse(profile.contains("val sessionViewModel: AppSessionViewModel = viewModel()"))
        assertFalse(profile.contains("onToggleSimulator"))
        assertFalse(profile.contains("Partner-Simulator"))
        assertFalse(profile.contains("simulator_toggle"))
    }

    @Test
    fun `unpaired real account never renders a fake partner identity`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(profile.contains("session.partner"))
        assertTrue(profile.contains("Noch nicht verbunden"))
        assertFalse(profile.contains("text = \"${'$'}{profile.userName} & ${'$'}{profile.partnerName}\""))
        assertFalse(main.contains("partnerName = appSession.partner?.displayName ?: \"Partner\""))
    }

    @Test
    fun `Harmony Brain has no productive UI wiring`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
        val games = source("app/src/main/java/com/example/ui/screens/GamesScreen.kt")
        val chat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")
        val viewModel = source("app/src/main/java/com/example/ui/HarmonyViewModel.kt")

        listOf(main, home, games, chat).forEach { ui ->
            assertFalse(ui.contains("brainEnabled"))
            assertFalse(ui.contains("brainInterests"))
            assertFalse(ui.contains("brainSuggestions"))
            assertFalse(ui.contains("brainQuestions"))
            assertFalse(ui.contains("isBrainChatMode"))
            assertFalse(ui.contains("onSendBrainMessage"))
            assertFalse(ui.contains("generatedGames"))
        }

        assertFalse(viewModel.contains("HARMONY_BRAIN_ENABLED"))
        assertFalse(viewModel.contains("setBrainChatMode"))
        assertFalse(viewModel.contains("sendBrainMessage"))
        assertFalse(viewModel.contains("startGeneratedGame"))
        assertFalse(fileExists("app/src/main/java/com/example/ui/screens/ChatScreenLegacyBridge.kt"))
    }

    @Test
    fun `removed Mischung stays tombstoned`() {
        val policy = source("app/src/main/java/com/example/data/model/RemovedGameCatalogPolicy.kt")
        val models = source("app/src/main/java/com/example/data/model/Models.kt")

        assertTrue(policy.contains("MISCHUNG_CATEGORY_ID = \"mischung\""))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsCategoryId"))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsPackCategoryId"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${'$'}{File(".").absolutePath}")
    }

    private fun fileExists(path: String): Boolean {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.any(File::exists)
    }
}
