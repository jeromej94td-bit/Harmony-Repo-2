package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class PartnerPackRevealIntegrationContractTest {
    @Test
    fun `pilot migration enforces full pack completion before partner answers can be returned`() {
        val migration = source("supabase/migrations/20260908181500_partner_pack_reveal_pilot.sql")
        assertTrue(migration.contains("'aufwaermen1', 'Einander kennenlernen', 10"))
        assertTrue(migration.contains("create or replace function public.complete_partner_pack"))
        assertTrue(migration.contains("create or replace function public.get_partner_pack_results"))
        assertTrue(migration.contains("partner_pack_already_completed"))
        assertTrue(migration.contains("when v_ready_to_reveal then theirs.answer_text"))
        assertTrue(migration.contains("create or replace function public.get_partner_notifications"))
        assertTrue(migration.contains("create or replace function public.mark_partner_notification_read"))
    }

    @Test
    fun `android repository uses whole pack rpc only for the opt in pilot`() {
        val repository = source("app/src/main/java/com/example/data/couple/CoupleQuestionRepository.kt")
        assertTrue(repository.contains("PartnerPackRevealPolicy.isWholePackRevealEnabled(packId)"))
        assertTrue(repository.contains("completePartnerPack"))
        assertTrue(repository.contains("get_partner_pack_results"))
        assertTrue(repository.contains("getPartnerNotifications"))
    }

    @Test
    fun `pilot reveal is one shared reveal and notifications deep link into the pack`() {
        val reveal = source("app/src/main/java/com/example/ui/screens/CouplePackRevealScreen.kt")
        val notifier = source("app/src/main/java/com/example/data/couple/PartnerCompletionNotifier.kt")
        val launcher = source("app/src/main/java/com/example/HarmonyEntryActivity.kt")
        assertTrue(reveal.contains("Antworten enthüllen"))
        assertTrue(reveal.contains("wholePackReveal"))
        assertTrue(notifier.contains("open_partner_pack_id"))
        assertTrue(launcher.contains("getPartnerNotifications"))
        assertTrue(launcher.contains("PartnerCompletionNotifier.show"))
        assertTrue(launcher.contains("EXTRA_OPEN_PARTNER_PACK_ID"))
        assertTrue(launcher.contains("hasCompletePackResults"))
        assertTrue(launcher.contains("viewModel.finishPack()"))
    }

    private fun source(path: String): String {
        val start = File(requireNotNull(System.getProperty("user.dir"))).absoluteFile
        val file = generateSequence(start) { it.parentFile }
            .map { File(it, path) }
            .firstOrNull(File::exists)
            ?: error("$path not found from ${start.path}")
        return file.readText()
    }
}
