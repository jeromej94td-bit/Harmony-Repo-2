package com.example

import java.io.File
import org.junit.Assert.assertTrue
import org.junit.Test

class ProfileAvatarSupabaseSyncContractTest {

    @Test
    fun `real profile avatars are stored in supabase and reused by person choice cards`() {
        val repository = source("app/src/main/java/com/example/data/session/AppSessionRepository.kt")
        val sessionViewModel = source("app/src/main/java/com/example/ui/session/AppSessionViewModel.kt")
        val mainActivity = source("app/src/main/java/com/example/MainActivity.kt")
        val whoWould = source("app/src/main/java/com/example/ui/screens/ScaleWhoWouldReworkBoards.kt")

        assertTrue(repository.contains("storage/v1/object/harmony-avatars"))
        assertTrue(repository.contains("update_harmony_avatar"))
        assertTrue(repository.contains("harmony-avatar:"))
        assertTrue(repository.contains("x-upsert"))
        assertTrue(sessionViewModel.contains("fun updateProfileAvatar(uri: Uri)"))
        assertTrue(mainActivity.contains("sessionViewModel.updateProfileAvatar(uri)"))

        // The existing person-choice mechanic must keep using the linked real profiles,
        // never switch the persisted answer values to display-only avatar data.
        assertTrue(whoWould.contains("avatarPath = profile.userAvatarPath"))
        assertTrue(whoWould.contains("avatarPath = profile.partnerAvatarPath"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
