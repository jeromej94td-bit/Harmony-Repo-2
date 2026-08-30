package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IntrospectionStateRestorationContractTest {

    @Test
    fun `introspection overlay remains open across activity recreation`() {
        val source = source("app/src/main/java/com/example/MainActivity.kt")

        assertTrue(source.contains("var isIntrospectionOpen by rememberSaveable { mutableStateOf(false) }"))
        assertFalse(source.contains("var isIntrospectionOpen by remember { mutableStateOf(false) }"))
    }

    @Test
    fun `introspection screen and draft state survive recreation`() {
        val source = source("app/src/main/java/com/example/ui/screens/IntrospectionGameScreen.kt")

        assertTrue(source.contains("import androidx.compose.runtime.saveable.rememberSaveable"))
        assertTrue(source.contains("var screenStateName by rememberSaveable"))
        assertTrue(source.contains("var showIntroVideo by rememberSaveable"))
        assertTrue(source.contains("var inputMode by rememberSaveable"))
        assertTrue(source.contains("var currentTextAnswer by rememberSaveable"))
        assertTrue(source.contains("var currentRecordedFilePath by rememberSaveable"))
        assertTrue(source.contains("var draftStageName by rememberSaveable"))
    }

    @Test
    fun `restored draft is not overwritten by durable answer preload`() {
        val source = source("app/src/main/java/com/example/ui/screens/IntrospectionGameScreen.kt")

        assertTrue(source.contains("val hasDraftForCurrentStage = draftStageName == progress.stage.name"))
        assertTrue(source.contains("if (!hasDraftForCurrentStage)"))
        assertTrue(source.contains("draftStageName = progress.stage.name"))
    }

    @Test
    fun `exit and resume dialogs survive recreation`() {
        val source = source("app/src/main/java/com/example/ui/screens/IntrospectionGameScreen.kt")

        assertTrue(source.contains("var showContinueDialog by rememberSaveable"))
        assertTrue(source.contains("var showLeaveDialog by rememberSaveable"))
        assertTrue(source.contains("var showPermissionSettingsDialog by rememberSaveable"))
    }

    private fun source(path: String): String {
        val candidates = listOf(
            File(path.removePrefix("app/")),
            File(path)
        )
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
