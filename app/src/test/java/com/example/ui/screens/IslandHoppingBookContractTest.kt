package com.example.ui.screens

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class IslandHoppingBookContractTest {

    private fun source(pathFromRepoRoot: String): String {
        val candidates = listOf(
            File(pathFromRepoRoot),
            File("../$pathFromRepoRoot")
        )
        val file = candidates.firstOrNull { it.exists() }
        assertTrue("Expected source file to exist: $pathFromRepoRoot", file != null)
        return file!!.readText()
    }

    @Test
    fun `island hopping has exact book policy and special priority poker routing`() {
        val policy = source("app/src/main/java/com/example/ui/screens/IslandHoppingBookPolicy.kt")
        val router = source("app/src/main/java/com/example/ui/screens/FullscreenQuestionMechanicBoard.kt")

        assertTrue(policy.contains("h500_089_inselhopping_prioritaet"))
        assertTrue(policy.contains("fun isEnabled(packId: String)"))
        assertTrue(router.contains("IslandHoppingBookPolicy.isEnabled(packId)"))
        assertTrue(router.contains("FullscreenGameMechanicKind.PRIORITY_POKER"))
        assertTrue(router.contains("IslandHoppingBookBoard("))
        assertTrue(router.contains("DirectPriorityPokerBoard("))
    }

    @Test
    fun `island hopping curated six questions stay canonical`() {
        val curation = source("app/src/main/java/com/example/data/Harmony360FoodTravelLeisureCultureQualityRework.kt")
        listOf(
            "Was ist beim Inselhopping wichtiger?",
            "Was muss auf den Inseln unbedingt passieren?",
            "Worauf würdest du bei engem Budget am wenigsten verzichten?",
            "Was nervt dich beim Inselwechsel am ehesten?",
            "Was soll bei der Route den Ausschlag geben?",
            "Was wäre dein perfekter letzter Inselabend?"
        ).forEach { question ->
            assertTrue("Missing curated island-hopping question: $question", curation.contains(question))
        }
    }

    @Test
    fun `island hopping plays one time magical book intro before questions`() {
        val intro = source("app/src/main/java/com/example/ui/screens/IslandHoppingBookIntro.kt")
        val runner = source("app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt")

        assertTrue(intro.contains("FairyBookIntroOverlay("))
        assertTrue(runner.contains("IslandHoppingBookPolicy.isEnabled(pack.id)"))
        assertTrue(runner.contains("remember(pack.id) { mutableStateOf(!isIslandHoppingBook) }"))
        assertTrue(runner.contains("IslandHoppingBookIntro("))
        assertTrue(runner.contains("!isIslandHoppingBook || islandBookIntroFinished"))
    }
}
