package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PrivateAvatarUiContractTest {
    @Test
    fun `real profile avatar picker uploads through authenticated session`() {
        val profile = source("app/src/main/java/com/example/ui/screens/ProfileSheetCoupleAdapter.kt")

        assertTrue(profile.contains("sessionViewModel.updateProfileAvatar(it)"))
        assertTrue(profile.contains("collectAsStateWithLifecycle"))
        assertTrue(profile.contains("AuthenticatedAvatarImage("))
        assertTrue(profile.contains("isDemoMode"))
    }

    @Test
    fun `couple reveal never sends private avatar reference directly to coil`() {
        val reveal = source("app/src/main/java/com/example/ui/screens/CouplePackRevealScreen.kt")

        assertTrue(reveal.contains("AuthenticatedAvatarImage("))
        assertFalse(reveal.contains("model = profile.avatarUrl"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
