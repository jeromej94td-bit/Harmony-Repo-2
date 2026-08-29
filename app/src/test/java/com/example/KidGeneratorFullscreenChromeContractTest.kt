package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class KidGeneratorFullscreenChromeContractTest {

    @Test
    fun `kid generator hides app top and bottom chrome like the other full screen flows`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")
        val topBarGuard = source.substringAfter("topBar = {")
            .substringBefore("bottomBar = {")
            .replace(Regex("\\s+"), " ")
        val bottomBarGuard = source.substringAfter("bottomBar = {")
            .substringBefore(") { innerPadding ->")
            .replace(Regex("\\s+"), " ")

        assertTrue(
            "KidGenerator must hide HarmonyTopBar while its full-screen flow is open.",
            topBarGuard.contains("&& !isKidGeneratorOpen")
        )
        assertTrue(
            "KidGenerator must hide HarmonyBottomNav while its full-screen flow is open.",
            bottomBarGuard.contains("&& !isKidGeneratorOpen")
        )
    }

    private fun source(path: String): String {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path)
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
