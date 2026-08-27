package com.example.data.brain.model

import com.example.data.model.Question
import com.example.data.model.QuestionPack
import kotlinx.serialization.Serializable

@Serializable
data class GeneratedGameQuestion(
    val text: String,
    val options: List<String> = emptyList()
)

@Serializable
data class GeneratedGamePayload(
    val id: String,
    val title: String,
    val emoji: String,
    val questions: List<GeneratedGameQuestion>,
    val createdAt: Long
) {
    fun toQuestionPack() = QuestionPack(
        id = id,
        title = title,
        tags = listOf("Für dich", "KI"),
        cat = "fuer_dich",
        topic = "personalisiert",
        type = "quiz",
        questions = questions.map {
            Question(
                q = it.text,
                options = it.options.ifEmpty { listOf("Person A", "Person B", "Beide", "Keiner") }
            )
        },
        emoji = emoji
    )
}
