package com.example.data.brain.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "brain_answer_history",
    indices = [
        Index("questionId"),
        Index("category"),
        Index("topic"),
        Index("createdAt")
    ]
)
data class BrainAnswerHistoryEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val packId: String? = null,
    val questionId: String,
    val questionIndex: Int? = null,
    val questionText: String,
    val category: String,
    val topic: String? = null,
    val contentType: String,
    val answerPersonA: String? = null,
    val answerPersonB: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val liked: Boolean = false,
    val disliked: Boolean = false,
    val skipped: Boolean = false,
    val source: String = "STATIC", // STATIC | GENERATED
    val generatedContentId: String? = null,
    val metadataJson: String? = null
)

@Entity(
    tableName = "brain_preferences",
    primaryKeys = ["scope", "tag"],
    indices = [
        Index(value = ["scope", "score"])
    ]
)
data class BrainPreferenceEntity(
    val scope: String, // PERSON_A | PERSON_B | COUPLE
    val tag: String,
    val score: Double, // 0.0 .. 1.0
    val confidence: Double, // 0.0 .. 1.0
    val engagement: Double = 0.0, // 0.0 .. 1.0
    val positiveSignals: Int = 0,
    val negativeSignals: Int = 0,
    val saturation: Double = 0.0, // 0.0 .. 1.0
    val lastSeenAt: Long = System.currentTimeMillis(),
    val lastUsedForContentAt: Long? = null,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "brain_interactions",
    indices = [
        Index("contentId"),
        Index("category"),
        Index("createdAt")
    ]
)
data class BrainInteractionEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val contentId: String,
    val contentType: String,
    val action: String, // PLAYED | ANSWERED | SKIPPED | LIKED | DISLIKED | OPENED_CATEGORY | FINISHED_PACK | GENERATED_SHOWN | GENERATED_PLAYED
    val category: String? = null,
    val topic: String? = null,
    val personScope: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "brain_memory_facts",
    indices = [
        Index("category"),
        Index("personScope")
    ]
)
data class BrainMemoryFactEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val factText: String,
    val category: String? = null,
    val personScope: String, // PERSON_A | PERSON_B | COUPLE
    val confidence: Double,
    val importance: Double,
    val sourceAnswerIdsJson: String = "[]",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastUsedAt: Long? = null
)

@Entity(
    tableName = "brain_generated_content",
    indices = [
        Index("contentType"),
        Index("normalizedText"),
        Index("status")
    ]
)
data class BrainGeneratedContentEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val contentType: String, // QUESTION | CATEGORY | DUEL | RECOMMENDATION | PERSONAL_ANSWER
    val category: String? = null,
    val topic: String? = null,
    val title: String? = null,
    val normalizedText: String,
    val payloadJson: String,
    val sourceModel: String? = null,
    val promptVersion: String? = null,
    val status: String = "DRAFT", // DRAFT | QUEUED | PUBLISHED | ARCHIVED | REJECTED
    val createdAt: Long = System.currentTimeMillis(),
    val firstShownAt: Long? = null,
    val lastShownAt: Long? = null,
    val playedCount: Int = 0,
    val likeCount: Int = 0,
    val dislikeCount: Int = 0
)

@Entity(
    tableName = "brain_pending_generation",
    indices = [
        Index("status")
    ]
)
data class BrainPendingGenerationEntity(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val mode: String,
    val query: String,
    val contextJson: String,
    val status: String = "WAITING", // WAITING | IN_FLIGHT | DONE | FAILED
    val createdAt: Long = System.currentTimeMillis(),
    val lastAttemptAt: Long? = null,
    val retryCount: Int = 0,
    val lastError: String? = null
)
