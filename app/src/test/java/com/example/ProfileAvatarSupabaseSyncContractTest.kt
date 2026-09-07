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
        val profileSheet = source("app/src/main/java/com/example/ui/screens/ProfileSheet.kt")
        val presentation = source("app/src/main/java/com/example/ui/session/SessionProfilePresentation.kt")
        val whoWould = source("app/src/main/java/com/example/ui/screens/ScaleWhoWouldReworkBoards.kt")

        assertTrue(repository.contains("storage/v1/object/harmony-avatars"))
        assertTrue(repository.contains("storage/v1/object/sign/harmony-avatars"))
        assertTrue(repository.contains("update_harmony_avatar"))
        assertTrue(repository.contains("harmony-avatar:"))
        assertTrue(repository.contains("x-upsert"))
        assertTrue(sessionViewModel.contains("fun updateProfileAvatar(uri: Uri)"))
        assertTrue(mainActivity.contains("sessionViewModel.updateProfileAvatar(uri)"))

        // Production must use the cloud-linked avatars; local Room image paths are demo fallback only.
        assertTrue(profileSheet.contains("userAvatarModel: Any? = if (isDemoMode)"))
        assertTrue(presentation.contains("useLocalAvatarFallback: Boolean = true"))
        assertTrue(mainActivity.contains("questionDisplayProfile(uiState.profile, useLocalAvatarFallback = isDemoMode)"))

        // The existing person-choice mechanic consumes the display profile only.
        // Raw persisted answer values therefore stay independent from avatar URLs.
        assertTrue(whoWould.contains("avatarPath = profile.userAvatarPath"))
        assertTrue(whoWould.contains("avatarPath = profile.partnerAvatarPath"))
    }

    private fun source(path: String): String {
        val candidates = listOf(File(path.removePrefix("app/")), File(path))
        return candidates.firstOrNull(File::exists)?.readText()
            ?: error("$path not found from test working directory ${File(".").absolutePath}")
    }
}
