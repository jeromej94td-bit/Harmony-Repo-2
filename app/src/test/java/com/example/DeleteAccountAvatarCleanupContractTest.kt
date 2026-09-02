package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class DeleteAccountAvatarCleanupContractTest {
    @Test
    fun `account deletion removes private avatar before auth user`() {
        val source = source("supabase/functions/delete-account/delete-account-core.mjs")
        val avatarIndex = source.indexOf("/storage/v1/object/harmony-avatars/")
        val authDeleteIndex = source.indexOf("/auth/v1/admin/users/")

        assertTrue(avatarIndex >= 0)
        assertTrue(authDeleteIndex > avatarIndex)
        assertTrue(source.contains("avatar_cleanup_failed"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
