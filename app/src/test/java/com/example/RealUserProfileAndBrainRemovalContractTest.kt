package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RealUserProfileAndBrainRemovalContractTest {

    @Test
    fun `real account profile is driven by AppSession and exposes pairing directly`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")
        val bridge = source("app/src/main/java/com/example/ui/screens/ProfileSheetSessionBridge.kt")

        assertTrue(profile.contains("session: AppSession"))
        assertTrue(profile.contains("session.profile.displayName"))
        assertTrue(profile.contains("session.email"))
        assertTrue(profile.contains("partner_connection_button"))
        assertTrue(profile.contains("Code erstellen oder eingeben"))
        assertTrue(bridge.contains("session = realSession"))
        assertTrue(bridge.contains("sessionViewModel.updateProfileDisplayName(userName)"))

        assertFalse(profile.contains("currentSessionOrNull()?.user?.email"))
        assertFalse(profile.contains("val sessionViewModel: AppSessionViewModel = viewModel()"))
        assertFalse(profile.contains("onToggleSimulator"))
        assertFalse(profile.contains("Partner-Simulator"))
        assertFalse(profile.contains("simulator_toggle"))
    }

    @Test
    fun `unpaired real account never renders a fake partner identity in profile`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")

        assertTrue(profile.contains("session.partner"))
        assertTrue(profile.contains("Noch nicht verbunden"))
        assertTrue(profile.contains("else -> session.profile.displayName"))
        assertTrue(profile.contains("if (isDemoMode || isPaired)"))
        assertFalse(profile.contains("text = \"${'$'}{profile.userName} & ${'$'}{profile.partnerName}\""))
        assertFalse(profile.contains("Partner-Simulator"))
    }

    @Test
    fun `Harmony Brain has no productive UI wiring`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")
        val chat = source("app/src/main/java/com/example/ui/screens/ChatScreen.kt")
        val devStudio = source("app/src/main/java/com/example/ui/screens/DevStudioScreen.kt")

        listOf(
            "brainInterests =",
            "brainSuggestions =",
            "brainQuestions =",
            "isBrainChatMode =",
            "onSendBrainMessage =",
            "onSendVoiceBrainMessage =",
            "generatedGames =",
            "onStartGeneratedGame ="
        ).forEach { marker -> assertFalse(main.contains(marker)) }

        assertFalse(chat.contains("Harmony Brain"))
        assertFalse(chat.contains("BrainMessage"))
        assertFalse(devStudio.contains("🧠 Brain"))
        assertFalse(devStudio.contains("DevBrainTab("))
        assertFalse(fileExists("app/src/main/java/com/example/ui/screens/ChatScreenLegacyBridge.kt"))
        assertFalse(fileExists("app/src/main/java/com/example/ui/screens/DevBrainTab.kt"))
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
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }

    private fun fileExists(path: String): Boolean {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.any(File::exists)
    }
}
