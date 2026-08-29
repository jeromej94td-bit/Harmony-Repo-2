package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class GeneratedMomentWiringContractTest {

    @Test
    fun `both mixture flows pass their generated image path into moment persistence`() {
        val legacy = source("app/src/main/java/com/example/ui/screens/EureMischungScreen.kt")
            .replace(Regex("\\s+"), " ")
        val kid = source("app/src/main/java/com/example/ui/screens/KidGeneratorScreen.kt")
            .replace(Regex("\\s+"), " ")
        val root = source("app/src/main/java/com/example/MainActivity.kt")
            .replace(Regex("\\s+"), " ")

        assertTrue(legacy.contains("onAddMoment: (title: String, content: String, emoji: String, imagePath: String) -> Unit"))
        assertTrue(legacy.contains("res.aiDescription, \"👶\", res.localFilePath"))

        assertTrue(kid.contains("onAddMoment: (title: String, content: String, emoji: String, imagePath: String) -> Unit"))
        assertTrue(kid.contains("\"👶\", localPath"))

        assertTrue(root.contains("onAddMoment = { title, content, emoji, imagePath -> viewModel.addGeneratedMoment(title, content, emoji, imagePath) }"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from ${File(".").absolutePath}")
    }
}
