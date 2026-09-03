package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class DeepTalkStateRestorationContractTest {

    @Test
    fun `unfinished private Deep Talk state survives activity recreation`() {
        val source = File("src/main/java/com/example/ui/screens/FullscreenDeepTalk.kt").readText()

        assertTrue(source.contains("var firstAnswer by rememberSaveable(question, selectedAnswer)"))
        assertTrue(source.contains("var secondAnswer by rememberSaveable(question, selectedAnswer)"))
        assertTrue(source.contains("var phase by rememberSaveable(question, selectedAnswer)"))
        assertTrue(source.contains("var discussionOpen by rememberSaveable(question)"))
        assertTrue(source.contains("var memorySaved by rememberSaveable(question)"))
    }
}
