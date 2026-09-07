package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AppSessionRpcMigrationContractTest {
    @Test
    fun `get app session migration avoids plpgsql name conflicts and exact return type mismatch`() {
        val migration = source("supabase/migrations/20260907222425_fix_get_app_session_email_type.sql")
        assertTrue(migration.contains("create or replace function public.get_app_session()"))
        assertTrue(migration.contains("on conflict on constraint harmony_profiles_pkey do update"))
        assertTrue(migration.contains("u.email::text"))
        assertFalse(migration.contains("on conflict(user_id)"))
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
