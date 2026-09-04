package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.BrainInterestEntity
import com.example.data.model.BrainSuggestionEntity
import com.example.data.model.BrainQuestionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BrainDao {
    // --- Interests ---
    @Query("SELECT * FROM brain_interests ORDER BY timestamp DESC")
    fun getAllInterestsFlow(): Flow<List<BrainInterestEntity>>

    @Query("SELECT * FROM brain_interests")
    suspend fun getAllInterests(): List<BrainInterestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterest(interest: BrainInterestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInterests(interests: List<BrainInterestEntity>)

    @Query("DELETE FROM brain_interests")
    suspend fun clearInterests()

    // --- Suggestions ---
    @Query("SELECT * FROM brain_suggestions ORDER BY timestamp DESC")
    fun getAllSuggestionsFlow(): Flow<List<BrainSuggestionEntity>>

    @Query("SELECT * FROM brain_suggestions")
    suspend fun getAllSuggestions(): List<BrainSuggestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestion(suggestion: BrainSuggestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSuggestions(suggestions: List<BrainSuggestionEntity>)

    @Update
    suspend fun updateSuggestion(suggestion: BrainSuggestionEntity)

    @Query("DELETE FROM brain_suggestions")
    suspend fun clearSuggestions()

    // --- Questions ---
    @Query("SELECT * FROM brain_questions ORDER BY timestamp DESC")
    fun getAllQuestionsFlow(): Flow<List<BrainQuestionEntity>>

    @Query("SELECT * FROM brain_questions")
    suspend fun getAllQuestions(): List<BrainQuestionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(question: BrainQuestionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<BrainQuestionEntity>)

    @Update
    suspend fun updateQuestion(question: BrainQuestionEntity)

    @Query("DELETE FROM brain_questions")
    suspend fun clearQuestions()
}
