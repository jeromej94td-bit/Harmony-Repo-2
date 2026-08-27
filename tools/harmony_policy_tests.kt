import com.example.data.GameRunPolicy
import com.example.data.brain.AutoGenerationPolicy
import com.example.data.brain.HarmonyBrainIntentPolicy

private fun assertTrue(value: Boolean, message: String) {
    if (!value) error(message)
}

private fun assertFalse(value: Boolean, message: String) {
    if (value) error(message)
}

private fun <T> assertEquals(expected: T, actual: T, message: String) {
    if (expected != actual) error("$message expected=$expected actual=$actual")
}

fun main() {
    val liveQueries = listOf(
        "Zeige mir Aktivitäten in der Nähe",
        "Was kann man heute in Berlin Tiergarten machen?",
        "Nenne mir Museen in Florenz",
        "Sushi Restaurants in meiner Nähe",
        "Welche Hotels gibt es in Siena?",
        "Sehenswürdigkeiten in Bologna",
        "Öffnungszeiten vom Kino in Wedding",
        "cafes near me",
        "things to do in Rome tonight"
    )
    liveQueries.forEach { query ->
        assertTrue(HarmonyBrainIntentPolicy.needsLiveSearch(query), "Should route live: $query")
    }

    val localQueries = listOf(
        "Wie können wir besser über Streit reden?",
        "Was weißt du über unsere gemeinsamen Interessen?",
        "Gib uns eine Frage über Vertrauen"
    )
    localQueries.forEach { query ->
        assertFalse(HarmonyBrainIntentPolicy.needsLiveSearch(query), "Should stay relationship chat: $query")
    }

    val complete = GameRunPolicy.initialState(
        total = 3,
        answers = mapOf(0 to "A", 1 to "B", 2 to "C")
    )
    assertTrue(complete.isFinished, "Complete game must open results")
    assertEquals(2, complete.currentIndex, "Results should point at final answered item")

    val partial = GameRunPolicy.initialState(
        total = 4,
        answers = mapOf(0 to "A", 2 to "C")
    )
    assertFalse(partial.isFinished, "Partial game must remain playable")
    assertEquals(1, partial.currentIndex, "Partial game must resume first unanswered question")

    val empty = GameRunPolicy.initialState(total = 5, answers = emptyMap())
    assertFalse(empty.isFinished, "Empty game is not finished")
    assertEquals(0, empty.currentIndex, "Empty game starts at zero")

    assertEquals(20, AutoGenerationPolicy.DAILY_LIMIT, "Test-mode daily limit")
    assertEquals(60_000L, AutoGenerationPolicy.INTERVAL_MS, "Test-mode interval")
    assertTrue(AutoGenerationPolicy.canGenerate(19), "19 should allow one more")
    assertFalse(AutoGenerationPolicy.canGenerate(20), "20 must stop generation")

    println("Harmony policy tests passed")
}
