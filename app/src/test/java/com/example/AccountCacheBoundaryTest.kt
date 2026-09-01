package com.example

import com.example.data.session.AccountCacheBoundary
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class AccountCacheBoundaryTest {
    @Test
    fun `first authenticated owner is recorded without clearing cache`() = runTest {
        var owner: String? = null
        var clearCount = 0
        val boundary = AccountCacheBoundary(
            readOwner = { owner },
            writeOwner = { owner = it },
            clearLocalData = { clearCount++ }
        )

        boundary.ensureOwner("user-a")

        assertEquals("user-a", owner)
        assertEquals(0, clearCount)
    }

    @Test
    fun `switching authenticated owner clears previous local data exactly once`() = runTest {
        var owner: String? = "user-a"
        var clearCount = 0
        val boundary = AccountCacheBoundary(
            readOwner = { owner },
            writeOwner = { owner = it },
            clearLocalData = { clearCount++ }
        )

        boundary.ensureOwner("user-b")
        boundary.ensureOwner("user-b")

        assertEquals("user-b", owner)
        assertEquals(1, clearCount)
    }

    @Test
    fun `harmony reset clears local data but keeps current cache owner`() = runTest {
        var owner: String? = "user-a"
        var clearCount = 0
        val boundary = AccountCacheBoundary(
            readOwner = { owner },
            writeOwner = { owner = it },
            clearLocalData = { clearCount++ }
        )

        boundary.clearForReset("user-a")

        assertEquals("user-a", owner)
        assertEquals(1, clearCount)
    }
}
