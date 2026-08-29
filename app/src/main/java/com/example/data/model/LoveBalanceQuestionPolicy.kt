package com.example.data.model

internal object LoveBalanceQuestionPolicy {
    const val PACK_ID = "liebegleichgewicht"
    const val QUESTION_TEXT = "Welches Paar ist GLÜCKLICH?"

    val happyCoupleQuestion = Question(
        q = QUESTION_TEXT,
        options = listOf("1", "2", "3", "4")
    )

    fun ensureHappyCoupleFirst(pack: QuestionPack): QuestionPack {
        if (pack.id != PACK_ID || pack.type != "quiz") return pack

        val remainingQuestions = pack.questions.filterNot { it.q == QUESTION_TEXT }
        return pack.copy(questions = listOf(happyCoupleQuestion) + remainingQuestions)
    }
}
