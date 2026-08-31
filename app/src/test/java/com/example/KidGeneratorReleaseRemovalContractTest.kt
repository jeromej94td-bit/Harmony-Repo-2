package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Test

class KidGeneratorReleaseRemovalContractTest {

    @Test
    fun `kid generator is absent from productive navigation`() {
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertFalse(main.contains("import com.example.ui.screens.KidGeneratorScreen"))
        assertFalse(main.contains("isKidGeneratorOpen"))
        assertFalse(main.contains("KidGeneratorScreen("))
        assertFalse(main.contains("catId == \"mischung\""))
    }

    @Test
    fun `eure mischung category is absent from release catalog`() {
        val models = source("app/src/main/java/com/example/data/model/Models.kt")
        assertFalse(models.contains("Category(\"mischung\", \"Eure Mischung\""))
    }

    @Test
    fun `kid generator implementation is removed from productive source set`() {
        listOf(
            "app/src/main/java/com/example/ui/screens/KidGeneratorScreen.kt",
            "app/src/main/java/com/example/ui/viewmodel/KidGeneratorViewModel.kt",
            "app/src/main/java/com/example/data/repository/KidGeneratorRepository.kt",
            "app/src/main/java/com/example/data/SupabaseKidGeneratorGateway.kt",
            "app/src/main/java/com/example/data/model/KidGeneratorModels.kt"
        ).forEach { path -> assertFalse("Expected $path to be parked outside release source", sourceExists(path)) }
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }

    private fun sourceExists(path: String): Boolean =
        listOf(File(path.removePrefix("app/")), File(path)).any(File::exists)
}
