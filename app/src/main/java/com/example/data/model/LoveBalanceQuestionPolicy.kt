package com.example.data.model

import java.util.Locale

internal object LoveBalanceQuestionPolicy {
    const val PACK_ID = "liebegleichgewicht"
    const val QUESTION_TEXT = "Welches Paar ist GLÜCKLICH?"

    val happyCoupleQuestion = Question(
        q = QUESTION_TEXT,
        options = listOf("1", "2", "3", "4")
    )

    fun isHappyCoupleQuestionText(qText: String?): Boolean {
        val text = qText?.trim()?.lowercase(Locale.GERMAN) ?: return false
        if (text == "welches paar ist glücklich?" || text == "welches paar ist glücklich") return true
        if (text.contains("paar") && (text.contains("glücklich") || text.contains("glucklich") || text.contains("glücklichste") || text.contains("gluecklich"))) return true
        if (text.contains("couple") && (text.contains("happy") || text.contains("happiest"))) return true
        return false
    }

    fun ensureHappyCoupleFirst(pack: QuestionPack): QuestionPack {
        if (pack.id != PACK_ID || pack.type != "quiz") return pack

        val remainingQuestions = pack.questions.filterNot { isHappyCoupleQuestionText(it.q) }
        return pack.copy(questions = listOf(happyCoupleQuestion) + remainingQuestions)
    }
}
