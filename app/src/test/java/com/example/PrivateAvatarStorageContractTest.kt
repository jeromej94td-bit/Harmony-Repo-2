package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivateAvatarStorageContractTest {
    @Test
    fun `custom avatars use private owner partner storage and authenticated rendering`() {
        val migration = source("supabase/migrations/20260902_000004_private_harmony_avatar_storage.sql")
        val repository = source("app/src/main/java/com/example/data/session/ProfileAvatarRepository.kt")
        val image = source("app/src/main/java/com/example/ui/components/AuthenticatedAvatarImage.kt")
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheetCoupleAdapter.kt")
        val deletion = source("supabase/functions/delete-account/delete-account-core.mjs")

        assertTrue(migration.contains("'harmony-avatars'"))
        assertTrue(migration.contains("false,"))
        assertTrue(migration.contains("harmony_avatar_select_owner_or_partner"))
        assertTrue(migration.contains("name = auth.uid()::text || '/avatar'"))
        assertTrue(repository.contains("HARMONY_AVATAR_PREFIX"))
        assertTrue(repository.contains("x-upsert"))
        assertTrue(image.contains("Authorization"))
        assertTrue(image.contains("/storage/v1/object/authenticated/"))
        assertTrue(profile.contains("updateProfileAvatar"))
        assertTrue(deletion.contains("harmony-avatars"))
        assertFalse(repository.contains("/object/public/harmony-avatars"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
