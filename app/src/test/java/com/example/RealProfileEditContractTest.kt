package com.example

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class RealProfileEditContractTest {
    @Test
    fun `real account edits own cloud profile and never partner name`() {
        val source = source("app/src/main/java/com/example/ui/screens/ProfileSheetCoupleAdapter.kt")

        assertTrue(source.contains("sessionViewModel.updateProfileDisplayName(userName)"))
        assertTrue(source.contains("onSaveEditProfile(userName, profile.partnerName, startDate)"))
        assertTrue(source.contains("account_email"))
        assertTrue(source.contains("Partner verbinden"))
        assertFalse(source.contains("HRM-8731"))
        assertFalse(source.contains("functions.invoke(\"delete-account\")"))
    }

    private fun source(path: String): String =
        listOf(File(path.removePrefix("app/")), File(path)).first(File::exists).readText()
}
