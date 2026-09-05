package com.example.developer

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeveloperReviewTypeButtonPreservationTest {
    @Test
    fun `developer review wiring preserves live change type selector`() {
        val support = File("src/main/java/com/example/ui/screens/LiveChangeTypeButton.kt").readText()
        assertTrue(support.contains("internal fun TypeButton("))
        assertTrue(support.contains("HarmonySurface"))
        assertTrue(support.contains("HarmonyPurple"))
    }
}
