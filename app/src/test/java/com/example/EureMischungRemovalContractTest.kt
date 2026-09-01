package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EureMischungRemovalContractTest {

    @Test
    fun `productive navigation contains neither eure mischung nor kid generator`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertFalse(main.contains("EureMischungScreen"))
        assertFalse(main.contains("isEureMischungOpen"))
        assertFalse(main.contains("KidGenerator"))
        assertFalse(main.contains("isKidGeneratorOpen"))
        assertFalse(main.contains("catId == \"mischung\""))
    }

    @Test
    fun `catalog removes eure mischung and applies the removal policy`() {
        val models = source("app/src/main/java/com/example/data/model/Models.kt")

        assertFalse(models.contains("Eure Mischung"))
        assertFalse(models.contains("Category(\"mischung\""))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsCategoryId"))
        assertTrue(models.contains("RemovedGameCatalogPolicy.allowsPackCategoryId"))
    }

    @Test
    fun `kid generator production sources are removed while shared image service remains`() {
        val kidGeneratorSources = listOf(
            "app/src/main/java/com/example/ui/screens/KidGeneratorScreen.kt",
            "app/src/main/java/com/example/ui/viewmodel/KidGeneratorViewModel.kt",
            "app/src/main/java/com/example/data/repository/KidGeneratorRepository.kt",
            "app/src/main/java/com/example/data/SupabaseKidGeneratorGateway.kt",
            "app/src/main/java/com/example/data/model/KidGeneratorModels.kt"
        )

        kidGeneratorSources.forEach { path ->
            assertFalse("$path must be removed", sourceExists(path))
        }
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
