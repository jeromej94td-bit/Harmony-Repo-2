package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DeadControlRegressionContractTest {

    @Test
    fun `large option cards can be rendered without click semantics`() {
        val common = source("app/src/main/java/com/example/ui/screens/FullscreenMechanicCommon.kt")

        assertTrue(common.contains("onClick: (() -> Unit)? = null"))
        assertTrue(common.contains("onClick?.let"))
    }

    @Test
    fun `result-only mechanic cards do not install empty click handlers`() {
        val pairMechanics = source("app/src/main/java/com/example/ui/screens/FullscreenPairMechanics.kt")
        val choiceMechanics = source("app/src/main/java/com/example/ui/screens/FullscreenChoiceMechanics.kt")

        assertFalse(pairMechanics.contains("onClick = {}"))
        assertFalse(choiceMechanics.contains("onClick = {}"))
        assertFalse(choiceMechanics.contains("true,\n                    {},"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
