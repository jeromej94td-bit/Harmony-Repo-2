package com.example

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class CoupleReplayAttemptIsolationContractTest {

    @Test
    fun `fresh paired replay starts shared server attempt before local reset`() {
        val repository = File("src/main/java/com/example/data/couple/CoupleQuestionRepository.kt").readText()
        val main = File("src/main/java/com/example/MainActivity.kt").readText()

        assertTrue(repository.contains("suspend fun startPackAttempt("))
        assertTrue(repository.contains("\"start_pack_attempt\""))

        val freshRunBlock = main
            .substringAfter("freshRun -> {")
            .substringBefore("else -> viewModel.startPack(packId)")

        val serverStart = freshRunBlock.indexOf("coupleQuestionRepository.startPackAttempt(packId)")
        val localDelete = freshRunBlock.indexOf("appDb.answerDao().deleteAnswersForPack(packId)")
        assertTrue("fresh replay must start the server attempt", serverStart >= 0)
        assertTrue("server attempt must start before local answers are deleted", serverStart < localDelete)
    }

    @Test
    fun `migration scopes rounds results and completion to current attempt`() {
        val migrations = File("../supabase/migrations")
            .listFiles()
            .orEmpty()
            .filter { it.name.contains("harmony_pack_attempt_isolation") && it.extension == "sql" }
        assertTrue("attempt isolation migration is missing", migrations.isNotEmpty())

        val sql = migrations.maxBy { it.name }.readText().lowercase()
        assertTrue(sql.contains("create table if not exists public.harmony_pack_attempts"))
        assertTrue(sql.contains("attempt_id"))
        assertTrue(sql.contains("unique (attempt_id, question_index)"))
        assertTrue(sql.contains("create or replace function public.start_pack_attempt"))
        assertTrue(sql.contains("create or replace function public.submit_question_answer"))
        assertTrue(sql.contains("create or replace function public.get_pack_question_results"))
        assertTrue(sql.contains("create or replace function public.complete_partner_pack"))
        assertTrue(sql.contains("create or replace function public.get_partner_pack_results"))
        assertTrue(sql.contains("delete from public.harmony_pack_completions"))
    }
}
