package com.example.ai

import com.example.data.model.AnswerEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProfileEntity

object HarmonyPersonalizationContextBuilder {
    private const val MAX_EVIDENCE = 36

    fun build(
        profile: ProfileEntity,
        answers: List<AnswerEntity>,
        route: HarmonyAiRoute,
        query: String
    ): String {
        val allowSensitive = HarmonyAiIntentRouter.allowsSensitiveContext(query)
        val allowedTopics = topicsFor(route.intent)

        val evidence = answers
            .asSequence()
            .sortedByDescending { it.timestamp }
            .mapNotNull { answer -> evidenceFor(answer, allowSensitive, allowedTopics) }
            .take(MAX_EVIDENCE)
            .toList()

        val userCounts = linkedMapOf<String, Int>()
        val partnerCounts = linkedMapOf<String, Int>()
        val sharedCounts = linkedMapOf<String, Int>()

        evidence.forEach { item ->
            item.userChoice?.takeIf { it.length <= 60 }?.let { value ->
                userCounts[value] = (userCounts[value] ?: 0) + 1
            }
            item.partnerChoice?.takeIf { it.length <= 60 }?.let { value ->
                partnerCounts[value] = (partnerCounts[value] ?: 0) + 1
            }
            if (item.userChoice != null && item.userChoice == item.partnerChoice) {
                sharedCounts[item.userChoice] = (sharedCounts[item.userChoice] ?: 0) + 1
            }
        }

        return buildString {
            appendLine("HARMONY PERSONALIZATION CONTEXT")
            appendLine("Person A: ${sanitize(profile.userName)}")
            appendLine("Person B: ${sanitize(profile.partnerName)}")
            appendLine("Profile confidence: ${confidenceFor(evidence.size)}")
            appendLine("Current intent: ${route.intent}")
            appendLine()
            appendLine("Repeated Person A signals: ${topSignals(userCounts)}")
            appendLine("Repeated Person B signals: ${topSignals(partnerCounts)}")
            appendLine("Repeated shared matches: ${topSignals(sharedCounts)}")
            appendLine()
            appendLine("Relevant answer evidence (newest first):")
            if (evidence.isEmpty()) {
                appendLine("- No relevant stored answers yet. Do not pretend to know preferences that are not present.")
            } else {
                evidence.forEach { item ->
                    append("- [${item.topic}] ${item.question}: ")
                    when {
                        item.partnerChoice != null -> append("A=${item.userChoice}; B=${item.partnerChoice}")
                        else -> append("A=${item.userChoice ?: item.rawAnswer}")
                    }
                    appendLine()
                }
            }
            appendLine()
            appendLine("Rules for this context:")
            appendLine("- Treat repeated and explicit signals as stronger than a single choice.")
            appendLine("- Never state that both people like something unless the evidence supports both people.")
            appendLine("- The user's current request overrides stored preferences (for example 'no sushi today').")
            appendLine("- Do not mention database rows, timestamps, internal scores, pack IDs, or surveillance-like details.")
            appendLine("- Sensitive/intimate answers are excluded unless the current request is itself about intimacy.")
            appendLine("- Use personalization only when it improves the answer; do not force it into unrelated questions.")
        }
    }

    private fun evidenceFor(
        answer: AnswerEntity,
        allowSensitive: Boolean,
        allowedTopics: Set<String>?
    ): Evidence? {
        val pack = HarmonyPacksData.PACKS.firstOrNull { it.id == answer.packId } ?: return null
        if (!allowSensitive && pack.topic == "sex") return null
        if (allowedTopics != null && pack.topic !in allowedTopics) return null

        val question = if (pack.type == "tot") {
            pack.pairs.getOrNull(answer.questionIndex)?.let { "${it.first} / ${it.second}" }
        } else {
            pack.questions.getOrNull(answer.questionIndex)?.q
        } ?: pack.title

        val couple = EitherOrAnswerCodec.decode(answer.answerText)
        return if (couple != null) {
            Evidence(
                topic = pack.topic,
                question = sanitize(question),
                rawAnswer = "",
                userChoice = sanitize(couple.userChoice),
                partnerChoice = sanitize(couple.partnerChoice)
            )
        } else {
            Evidence(
                topic = pack.topic,
                question = sanitize(question),
                rawAnswer = sanitize(answer.answerText.take(240)),
                userChoice = sanitize(answer.answerText.take(240)),
                partnerChoice = null
            )
        }
    }

    private fun topicsFor(intent: HarmonyAiIntent): Set<String>? = when (intent) {
        HarmonyAiIntent.FOOD -> setOf("essen", "hobbys", "aufwaermen", "kennen")
        HarmonyAiIntent.ENTERTAINMENT -> setOf("filme_serien", "hobbys", "aufwaermen", "kennen")
        HarmonyAiIntent.LOCAL_DISCOVERY, HarmonyAiIntent.EVENT_SEARCH -> setOf("reisen", "essen", "hobbys", "filme_serien", "aufwaermen", "kennen")
        HarmonyAiIntent.TRAVEL -> setOf("reisen", "essen", "hobbys", "aufwaermen", "kennen")
        else -> null
    }

    private fun topSignals(counts: Map<String, Int>): String {
        if (counts.isEmpty()) return "none yet"
        return counts.entries
            .sortedByDescending { it.value }
            .take(8)
            .joinToString { (value, count) -> if (count > 1) "$value (x$count)" else value }
    }

    private fun confidenceFor(count: Int): String = when {
        count >= 20 -> "HIGH"
        count >= 7 -> "MEDIUM"
        else -> "LOW"
    }

    private fun sanitize(value: String): String = value
        .replace('\n', ' ')
        .replace('\r', ' ')
        .replace(Regex("\\s+"), " ")
        .trim()

    private data class Evidence(
        val topic: String,
        val question: String,
        val rawAnswer: String,
        val userChoice: String?,
        val partnerChoice: String?
    )
}
