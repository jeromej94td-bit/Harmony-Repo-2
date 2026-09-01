package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProductionSeedContractTest {
    @Test
    fun `production defaults are neutral and demo content is opt in`() {
        val models = source("app/src/main/java/com/example/data/model/Models.kt")
        val repository = source("app/src/main/java/com/example/data/repository/HarmonyRepository.kt")
        val viewModel = source("app/src/main/java/com/example/ui/HarmonyViewModel.kt")
        val main = source("app/src/main/java/com/example/MainActivity.kt")

        assertFalse(models.contains("val userName: String = \"Jerome\""))
        assertFalse(models.contains("val partnerName: String = \"Alex\""))
        assertFalse(models.contains("val visitedCities: Int = 7"))
        assertFalse(models.contains("val visitedCountries: Int = 3"))
        assertTrue(repository.contains("suspend fun ensureDemoData()"))
        assertTrue(viewModel.contains("fun ensureDemoData()"))
        assertTrue(main.contains("viewModel.ensureDemoData()"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
