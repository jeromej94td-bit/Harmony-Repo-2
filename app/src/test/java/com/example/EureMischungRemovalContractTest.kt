package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EureMischungRemovalContractTest {

    @Test
    fun `eure mischung is absent from productive navigation`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertFalse(main.contains("import com.example.ui.screens.EureMischungScreen"))
        assertFalse(main.contains("isEureMischungOpen"))
        assertFalse(main.contains("onOpenEureMischung"))
        assertFalse(main.contains("EureMischungScreen("))
    }

    @Test
    fun `home no longer exposes eure mischung entry`() {
        val home = source("app/src/main/java/com/example/ui/screens/HomeScreen.kt")
        assertFalse(home.contains("onOpenEureMischung"))
    }

    @Test
    fun `eure mischung source is removed while kid generator and shared image service remain`() {
        assertFalse(sourceExists("app/src/main/java/com/example/ui/screens/EureMischungScreen.kt"))

        val main = source("app/src/main/java/com/example/MainActivity.kt")
        assertTrue(main.contains("KidGeneratorScreen"))
        assertTrue(main.contains("isKidGeneratorOpen"))
        assertTrue(sourceExists("app/src/main/java/com/example/ui/screens/KidGeneratorScreen.kt"))
        assertTrue(sourceExists("app/src/main/java/com/example/util/GeminiImageService.kt"))
    }

    private fun source(path: String): String {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path)
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }

    private fun sourceExists(path: String): Boolean =
        listOf(File(path.removePrefix("app/")), File(path)).any(File::exists)
}
