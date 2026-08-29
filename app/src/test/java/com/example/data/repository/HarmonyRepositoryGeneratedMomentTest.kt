package com.example.data.repository

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.data.db.HarmonyDatabase
import java.io.File
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class HarmonyRepositoryGeneratedMomentTest {

    @Test
    fun `generated image is copied into moments storage and keeps emoji`() = runTest {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val db = HarmonyDatabase.getInstance(app)
        val repository = HarmonyRepository(db, app)
        val title = "generated_moment_${UUID.randomUUID()}"
        val sourceDir = File(app.filesDir, "generated-moment-test").apply { mkdirs() }
        val source = File(sourceDir, "${UUID.randomUUID()}.jpg")
        val originalBytes = byteArrayOf(1, 2, 3, 4, 5, 6)
        source.writeBytes(originalBytes)

        repository.addGeneratedMoment(
            title = title,
            content = "Generated future image",
            imagePath = source.absolutePath,
            emoji = "👶"
        )

        val moment = withTimeout(2_000) {
            db.momentDao().getAllMoments().first { moments ->
                moments.any { it.title == title }
            }.first { it.title == title }
        }

        assertEquals("👶", moment.emoji)
        val storedPath = Regex("\\\"([^\\\"]+)\\\"")
            .find(moment.imagePathsJson)
            ?.groupValues
            ?.getOrNull(1)
        assertNotNull(storedPath)
        assertFalse(source.absolutePath == storedPath)

        val storedFile = File(requireNotNull(storedPath))
        assertTrue(storedFile.exists())
        assertTrue(storedFile.parentFile?.name == "moments")
        assertArrayEquals(originalBytes, storedFile.readBytes())
    }
}
