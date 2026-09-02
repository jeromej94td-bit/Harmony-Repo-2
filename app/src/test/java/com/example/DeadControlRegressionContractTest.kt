package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeadControlRegressionContractTest {

    @Test
    fun `result cards can be rendered without click semantics`() {
        val common = source("app/src/main/java/com/example/ui/screens/FullscreenMechanicCommon.kt")
        val pairMechanics = source("app/src/main/java/com/example/ui/screens/FullscreenPairMechanics.kt")

        assertTrue(common.contains("onClick: (() -> Unit)? = null"))
        assertTrue(common.contains("onClick?.let"))
        assertFalse(pairMechanics.contains("onClick = {}"))
    }

    @Test
    fun `tot results header does not show a fake overflow control`() {
        val runner = source("app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt")

        assertFalse(runner.contains("text = \"•••\""))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
