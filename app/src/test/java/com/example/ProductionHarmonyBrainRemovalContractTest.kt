package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionHarmonyBrainRemovalContractTest {
    private fun mainJavaRoot(): File = listOf(
        File("src/main/java"),
        File("app/src/main/java")
    ).firstOrNull(File::exists) ?: error("Missing src/main/java")

    @Test
    fun productionSourceContainsNoHarmonyBrainFeatureCode() {
        val root = mainJavaRoot()
        val forbiddenPaths = listOf(
            "com/example/data/brain",
            "com/example/data/HarmonyBrainEngine.kt",
            "com/example/data/SupabaseBrainGateway.kt",
            "com/example/data/GeminiBrainGateway.kt",
            "com/example/util/GeminiGameGenerator.kt",
            "com/example/notifications/HarmonyGameNotifier.kt"
        )
        val existingForbiddenPaths = forbiddenPaths
            .map { File(root, it) }
            .filter(File::exists)
            .map { it.relativeTo(root).invariantSeparatorsPath }

        assertTrue(
            "Harmony Brain production paths still exist:\n${existingForbiddenPaths.joinToString("\n")}",
            existingForbiddenPaths.isEmpty()
        )

        val forbiddenMarkers = listOf(
            "HarmonyBrain",
            "Harmony Brain",
            "BrainRepository",
            "SupabaseBrain",
            "SupabaseHarmonyBrainGateway",
            "ForegroundGameGenerator",
            "GeminiGameGenerator",
            "HARMONY_BRAIN_ENABLED",
            "brainEnabled",
            "brainInterests",
            "brainSuggestions",
            "brainQuestions",
            "brainMessages",
            "isBrainChatMode",
            "isBrainGenerating",
            "brainRoomDao",
            "recordBrain",
            "BrainChatSuggestion",
            "BrainGeneratedContentEntity",
            "GeneratedGamePayload",
            "GeneratedGameCard",
            "generatedGames",
            "onStartGeneratedGame",
            "HarmonyGameNotifier",
            "generated_game_id",
            "harmony_generated_games",
            "com.example.data.brain"
        )

        val leaks = root.walkTopDown()
            .filter { it.isFile && it.extension == "kt" }
            .flatMap { file ->
                val body = file.readText()
                forbiddenMarkers.asSequence()
                    .filter(body::contains)
                    .map { marker -> "${file.relativeTo(root).invariantSeparatorsPath}: $marker" }
            }
            .toList()

        assertFalse(
            "Harmony Brain references remain in production source:\n${leaks.joinToString("\n")}",
            leaks.isNotEmpty()
        )
    }
}
