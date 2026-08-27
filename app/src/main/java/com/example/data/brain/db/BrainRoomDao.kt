package com.example.data.brain.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BrainRoomDao {

    // --- ANSWER HISTORY (Append-Only) ---
    @Query("SELECT * FROM brain_answer_history ORDER BY createdAt DESC")
    fun getAllAnswerHistoryFlow(): Flow<List<BrainAnswerHistoryEntity>>

    @Query("SELECT * FROM brain_answer_history ORDER BY createdAt DESC")
    suspend fun getAllAnswerHistory(): List<BrainAnswerHistoryEntity>

    @Query("SELECT COUNT(*) FROM brain_answer_history")
    suspend fun getAnswerCount(): Int

    @Query("SELECT COUNT(*) FROM brain_answer_history")
    fun getAnswerCountFlow(): Flow<Int>

    @Query("SELECT * FROM brain_answer_history WHERE questionId = :questionId ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestAnswerForQuestion(questionId: String): BrainAnswerHistoryEntity?

    @Query("SELECT * FROM brain_answer_history WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getAnswerHistoryByCategory(category: String): List<BrainAnswerHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerHistory(item: BrainAnswerHistoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswerHistories(items: List<BrainAnswerHistoryEntity>)

    @Update
    suspend fun updateAnswerHistory(item: BrainAnswerHistoryEntity)

    // --- PREFERENCES ---
    @Query("SELECT * FROM brain_preferences WHERE scope = :scope ORDER BY score DESC, confidence DESC")
    suspend fun getPreferencesForScope(scope: String): List<BrainPreferenceEntity>

    @Query("SELECT * FROM brain_preferences WHERE scope = :scope ORDER BY score DESC, confidence DESC")
    fun getPreferencesForScopeFlow(scope: String): Flow<List<BrainPreferenceEntity>>

    @Query("SELECT * FROM brain_preferences ORDER BY score DESC, confidence DESC")
    suspend fun getAllPreferences(): List<BrainPreferenceEntity>

    @Query("SELECT * FROM brain_preferences ORDER BY score DESC, confidence DESC")
    fun getAllPreferencesFlow(): Flow<List<BrainPreferenceEntity>>

    @Query("SELECT * FROM brain_preferences WHERE scope = :scope ORDER BY score DESC, confidence DESC LIMIT :limit")
    suspend fun getTopPreferences(scope: String, limit: Int): List<BrainPreferenceEntity>

    @Query("SELECT * FROM brain_preferences ORDER BY score ASC, confidence DESC LIMIT :limit")
    suspend fun getLowestPreferences(limit: Int): List<BrainPreferenceEntity>

    @Query("SELECT * FROM brain_preferences WHERE scope = :scope AND tag = :tag LIMIT 1")
    suspend fun getPreference(scope: String, tag: String): BrainPreferenceEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreference(pref: BrainPreferenceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdatePreferences(prefs: List<BrainPreferenceEntity>)

    // --- INTERACTIONS ---
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInteraction(interaction: BrainInteractionEntity)

    @Query("SELECT COUNT(*) FROM brain_interactions")
    suspend fun getInteractionCount(): Int

    @Query("SELECT COUNT(*) FROM brain_interactions")
    fun getInteractionCountFlow(): Flow<Int>

    @Query("SELECT * FROM brain_interactions ORDER BY createdAt DESC")
    suspend fun getAllInteractions(): List<BrainInteractionEntity>

    @Query("SELECT * FROM brain_interactions WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getInteractionsByCategory(category: String): List<BrainInteractionEntity>

    // --- MEMORY FACTS ---
    @Query("SELECT * FROM brain_memory_facts ORDER BY importance DESC, confidence DESC")
    suspend fun getAllMemoryFacts(): List<BrainMemoryFactEntity>

    @Query("SELECT * FROM brain_memory_facts ORDER BY importance DESC, confidence DESC")
    fun getMemoryFactsFlow(): Flow<List<BrainMemoryFactEntity>>

    @Query("SELECT COUNT(*) FROM brain_memory_facts")
    suspend fun getMemoryFactsCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoryFact(fact: BrainMemoryFactEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMemoryFacts(facts: List<BrainMemoryFactEntity>)

    @Update
    suspend fun updateMemoryFact(fact: BrainMemoryFactEntity)

    @Query("DELETE FROM brain_memory_facts WHERE id = :id")
    suspend fun deleteMemoryFact(id: String)

    // --- GENERATED CONTENT ---
    @Query("SELECT * FROM brain_generated_content WHERE status = :status ORDER BY createdAt DESC")
    suspend fun getGeneratedContentByStatus(status: String): List<BrainGeneratedContentEntity>

    @Query("SELECT * FROM brain_generated_content ORDER BY createdAt DESC")
    suspend fun getAllGeneratedContent(): List<BrainGeneratedContentEntity>

    @Query("SELECT * FROM brain_generated_content ORDER BY createdAt DESC")
    fun getAllGeneratedContentFlow(): Flow<List<BrainGeneratedContentEntity>>

    @Query("SELECT COUNT(*) FROM brain_generated_content")
    suspend fun getGeneratedContentCount(): Int

    @Query("SELECT * FROM brain_generated_content WHERE normalizedText = :normalizedText LIMIT 1")
    suspend fun findGeneratedByNormalizedText(normalizedText: String): BrainGeneratedContentEntity?

    @Query("UPDATE brain_generated_content SET firstShownAt = COALESCE(firstShownAt, :now), lastShownAt = :now, playedCount = playedCount + 1 WHERE id = :id")
    suspend fun markGeneratedGameOpened(id: String, now: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGeneratedContent(content: BrainGeneratedContentEntity)

    @Update
    suspend fun updateGeneratedContent(content: BrainGeneratedContentEntity)

    // --- PENDING GENERATIONS ---
    @Query("SELECT * FROM brain_pending_generation WHERE status = :status ORDER BY createdAt ASC")
    suspend fun getPendingGenerations(status: String = "WAITING"): List<BrainPendingGenerationEntity>

    @Query("SELECT * FROM brain_pending_generation ORDER BY createdAt DESC")
    fun getPendingGenerationsFlow(): Flow<List<BrainPendingGenerationEntity>>

    @Query("SELECT COUNT(*) FROM brain_pending_generation WHERE status = 'WAITING'")
    suspend fun getPendingGenerationCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPendingGeneration(item: BrainPendingGenerationEntity)

    @Update
    suspend fun updatePendingGeneration(item: BrainPendingGenerationEntity)

    @Query("DELETE FROM brain_pending_generation WHERE id = :id")
    suspend fun deletePendingGeneration(id: String)
}
