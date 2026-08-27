package com.example.data.brain.repository

import android.app.Application
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.brain.db.BrainRoomDao
import com.example.data.db.HarmonyDatabase
import com.example.data.brain.model.GeneratedBrainQuestion
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class BrainRepositoryTest {

    private lateinit var database: HarmonyDatabase
    private lateinit var repository: BrainRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            HarmonyDatabase::class.java
        ).allowMainThreadQueries().build()
        
        repository = BrainRepository(database.brainRoomDao(), context as Application)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `generated duplicate question is stored only once`() = runTest {
        val first = GeneratedBrainQuestion("Worauf freut ihr euch diesen Monat gemeinsam?", "Harmony Brain")
        val sameWords = GeneratedBrainQuestion("Worauf freut ihr Euch diesen Monat gemeinsam?", "Harmony Brain")
        assertEquals(1, repository.storeGeneratedQuestions(listOf(first, sameWords)))
    }
}
