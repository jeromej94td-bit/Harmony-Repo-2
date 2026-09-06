package com.example.ui.screens

import java.util.Locale

internal data class ScenarioAdventureChapterRef(
    val prompt: String,
    val options: List<String>
)

internal data class ScenarioAdventurePackRef(
    val packId: String,
    val chapters: List<ScenarioAdventureChapterRef>
)

internal data class ScenarioAdventureLocation(
    val pack: ScenarioAdventurePackRef,
    val chapterIndex: Int
) {
    val chapterCount: Int
        get() = pack.chapters.size

    val isFinalChapter: Boolean
        get() = chapterIndex == pack.chapters.lastIndex
}

private fun scenarioNormalize(value: String): String = value
    .trim()
    .replace(Regex("\\s+"), " ")
    .lowercase(Locale.ROOT)

private fun scenarioChapterMatches(
    chapter: ScenarioAdventureChapterRef,
    question: String,
    options: List<String>
): Boolean {
    if (scenarioNormalize(chapter.prompt) != scenarioNormalize(question)) return false
    if (chapter.options.size != options.size) return false
    return chapter.options.indices.all { index ->
        scenarioNormalize(chapter.options[index]) == scenarioNormalize(options[index])
    }
}

internal fun resolveScenarioAdventureLocation(
    packs: List<ScenarioAdventurePackRef>,
    question: String,
    options: List<String>
): ScenarioAdventureLocation? {
    val matches = buildList {
        packs.forEach { pack ->
            pack.chapters.forEachIndexed { index, chapter ->
                if (scenarioChapterMatches(chapter, question, options)) {
                    add(ScenarioAdventureLocation(pack = pack, chapterIndex = index))
                }
            }
        }
    }
    return matches.singleOrNull()
}

internal fun scenarioChoiceIndex(
    options: List<String>,
    answer: String?
): Int? {
    val normalizedAnswer = answer?.let(::scenarioNormalize) ?: return null
    val matches = options.mapIndexedNotNull { index, option ->
        index.takeIf { scenarioNormalize(option) == normalizedAnswer }
    }
    return matches.singleOrNull()
}

internal fun scenarioRouteIndexes(
    chapters: List<ScenarioAdventureChapterRef>,
    answers: Map<Int, String>
): List<Int> = chapters.indices.mapNotNull { chapterIndex ->
    answers[chapterIndex]?.let { answer ->
        scenarioChoiceIndex(chapters[chapterIndex].options, answer)
    }
}

internal fun scenarioDominantStyle(
    routeIndexes: List<Int>,
    styleCount: Int = 4
): Int? {
    if (styleCount <= 0) return null
    val validChoices = routeIndexes.filter { it in 0 until styleCount }
    if (validChoices.isEmpty()) return null

    var bestStyle = 0
    var bestCount = -1
    for (style in 0 until styleCount) {
        val count = validChoices.count { it == style }
        if (count > bestCount) {
            bestStyle = style
            bestCount = count
        }
    }
    return bestStyle
}
