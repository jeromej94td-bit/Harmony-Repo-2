package com.example.ui.screens

import com.example.data.model.AnswerEntity

internal fun answerCountsByPack(answers: List<AnswerEntity>): Map<String, Int> =
    answers.groupingBy { it.packId }.eachCount()
