package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RankingOneScreenContractTest {

    @Test
    fun `ranking uses a stable two row choice pool so four options stay visible`() {
        val source = source("app/src/main/java/com/example/ui/screens/RankingSlotBoard.kt")

        assertTrue(source.contains("val poolCells = options.map"))
        assertTrue(source.contains("poolCells.chunked(2)"))
        assertTrue(source.contains("ranking_pool_row_"))
    }

    @Test
    fun `ranking continue action only appears after every slot is filled`() {
        val source = source("app/src/main/java/com/example/ui/screens/RankingSlotBoard.kt")
        val normalized = source.replace(Regex("\\s+"), " ")

        assertTrue(normalized.contains("if (complete) { PrimaryMechanicButton("))
        assertFalse(source.contains("Belege alle Rangplätze"))
    }

    @Test
    fun `global skip does not cover ranking controls`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(source.contains("val isRankingQuestion ="))
        assertTrue(source.contains("!isRankingQuestion &&"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("Expected source file to exist: $path")
    }
}
