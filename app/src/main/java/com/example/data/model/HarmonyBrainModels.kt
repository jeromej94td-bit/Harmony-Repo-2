package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "brain_interests")
data class BrainInterestEntity(
    @PrimaryKey val name: String,
    val category: String, // e.g. "Essen", "Reisen", "Musik", "Filme", "Hobbys"
    val confidence: String, // "sicher", "wahrscheinlich", "vermutung"
    val reason: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "brain_suggestions")
data class BrainSuggestionEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val category: String, // "Date", "Aktivität", "Essen", "Reisen", "Ausflug"
    val matchReason: String,
    val feedback: String = "none", // "none", "liked", "disliked", "later", "hidden", "done"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "brain_questions")
data class BrainQuestionEntity(
    @PrimaryKey val id: String,
    val text: String,
    val category: String,
    val difficulty: String, // "easy", "medium", "deep"
    val answered: Boolean = false,
    val answerText: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

data class BrainMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val sender: String, // "user" or "brain"
    val timestamp: Long = System.currentTimeMillis(),
    val isSearching: Boolean = false,
    val sources: List<com.example.data.HarmonyBrainSource> = emptyList(),
    val searchQueries: List<String> = emptyList(),
    val errorType: String? = null
)
