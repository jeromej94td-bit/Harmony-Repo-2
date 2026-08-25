package com.example.util

import com.example.data.model.AnswerEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProfileEntity
import com.example.data.model.QuestionPack

data class AnalyzedAnswer(
    val packId: String,
    val packTitle: String,
    val categoryIcon: String,
    val questionOrPair: String,
    val userChoice: String,
    val partnerChoice: String?,
    val isCustomText: Boolean,
    val isMatch: Boolean,
    val rawAnswer: String
)

object DateCoachContextBuilder {

    fun analyzeAnswers(
        answers: List<AnswerEntity>,
        profile: ProfileEntity,
        appLanguage: String = "de"
    ): List<AnalyzedAnswer> {
        val result = mutableListOf<AnalyzedAnswer>()

        for (answer in answers) {
            val rawPack = HarmonyPacksData.PACKS.firstOrNull { it.id == answer.packId }
            val pack = rawPack?.let { LanguageManager.translatePack(it, appLanguage) }
            val packTitle = pack?.title ?: answer.packId
            val categoryIcon = getCategoryIcon(rawPack, answer.packId)

            if (pack != null && pack.type == "tot") {
                val pair = pack.pairs.getOrNull(answer.questionIndex)
                val questionText = if (pair != null) "${pair.first}  ↔  ${pair.second}" else "Entweder-Oder #${answer.questionIndex + 1}"
                val coupleChoice = EitherOrAnswerCodec.decode(answer.answerText)
                if (coupleChoice != null) {
                    result.add(
                        AnalyzedAnswer(
                            packId = answer.packId,
                            packTitle = packTitle,
                            categoryIcon = categoryIcon,
                            questionOrPair = questionText,
                            userChoice = coupleChoice.userChoice,
                            partnerChoice = coupleChoice.partnerChoice,
                            isCustomText = false,
                            isMatch = coupleChoice.isMatch,
                            rawAnswer = answer.answerText
                        )
                    )
                } else {
                    result.add(
                        AnalyzedAnswer(
                            packId = answer.packId,
                            packTitle = packTitle,
                            categoryIcon = categoryIcon,
                            questionOrPair = questionText,
                            userChoice = answer.answerText,
                            partnerChoice = null,
                            isCustomText = false,
                            isMatch = false,
                            rawAnswer = answer.answerText
                        )
                    )
                }
            } else {
                val question = pack?.questions?.getOrNull(answer.questionIndex)
                val questionText = question?.q ?: "Frage #${answer.questionIndex + 1}"
                val standardOptions = question?.options?.map {
                    it.replace("{user}", profile.userName).replace("{partner}", profile.partnerName)
                } ?: emptyList()

                val isCustom = answer.answerText.isNotBlank() &&
                        standardOptions.isNotEmpty() &&
                        standardOptions.none { it.equals(answer.answerText.trim(), ignoreCase = true) }

                result.add(
                    AnalyzedAnswer(
                        packId = answer.packId,
                        packTitle = packTitle,
                        categoryIcon = categoryIcon,
                        questionOrPair = questionText,
                        userChoice = answer.answerText,
                        partnerChoice = null,
                        isCustomText = isCustom,
                        isMatch = false,
                        rawAnswer = answer.answerText
                    )
                )
            }
        }
        return result
    }

    fun buildSystemInstruction(profile: ProfileEntity): String {
        return """
            Du bist der persönliche "Harmony Date Coach" für das Paar ${profile.userName} und ${profile.partnerName}.
            Deine Hauptaufgabe: Entwickle hochgradig individuelle, maßgeschneiderte Date-Ideen, die DIREKT und KONKRET auf den echten Antworten, Vorlieben und insbesondere den selbst verfassten Freitext-Notizen des Paares aufbauen.

            WICHTIGE VERKNÜPFUNGS-REGELN:
            1. Direkte thematische Anknüpfung:
               - Wenn ${profile.userName} oder ${profile.partnerName} z. B. 'Kino' statt 'Couch & Decke' gewählt haben, schlage ein echtes Kino-Erlebnis vor (z. B. Programmkino, Open-Air-Kino, Autokino mit aktuellem Film-Tipp) und verknüpfe es mit ihren Lieblings-Snacks oder Food-Picks (z. B. Pizza, Sushi oder Kochen).
               - Wenn z. B. 'Wandern' oder 'Picknick' gewählt wurde, plane ein Outdoor-Erlebnis mit konkretem Ablauf.
            2. Besondere Priorität für EIGENE FREITEXT-ANTWORTEN:
               - Wenn das Paar eigene Freitext-Notizen (z. B. bei 'Wer würde eher', 'Diskussionsfragen' oder 'Eigene Antwort') eingegeben hat, gehe EINGEHEND auf diese individuellen Worte ein und baue sie direkt in das Date ein!
            3. Matches & Gegensätze:
               - Wenn beide dasselbe gewählt haben (Match), betone wie harmonisch das zu ihnen passt.
               - Bei gegensätzlichen Wünschen, finde einen charmanten Kompromiss oder eine Kombination beider Welten.
            4. Jede Date-Idee MUSS transparent im Feld 'inspiredBy' auflisten, welche konkreten Antworten & Notizen als Grundlage dienten (z. B. '🎬 Kino statt Couch', '✍️ Eigene Antwort bei Wer-würde-eher: ...', '🍕 Pizza-Vorliebe').
            5. Antworte immer im geforderten JSON-Format.
        """.trimIndent()
    }

    fun buildUserPrompt(
        profile: ProfileEntity,
        analyzedAnswers: List<AnalyzedAnswer>,
        moodFilter: String? = null,
        customWish: String? = null
    ): String {
        val sb = StringBuilder()
        sb.appendLine("Paar-Profil:")
        sb.appendLine("- Partner 1: ${profile.userName}")
        sb.appendLine("- Partner 2: ${profile.partnerName}")
        sb.appendLine()

        if (moodFilter != null && moodFilter.isNotBlank()) {
            sb.appendLine("Fokus / Vibe-Wunsch: $moodFilter")
        }
        if (customWish != null && customWish.isNotBlank()) {
            sb.appendLine("Zusätzlicher Wunsch der Nutzer: $customWish")
        }
        sb.appendLine()

        val customNotes = analyzedAnswers.filter { it.isCustomText }
        if (customNotes.isNotEmpty()) {
            sb.appendLine("--- BESONDERS WICHTIG: EIGENE FREITEXT-ANTWORTEN & NOTIZEN DES PAARES ---")
            for (note in customNotes) {
                sb.appendLine("• In '${note.packTitle}' zu '${note.questionOrPair}':")
                sb.appendLine("  ✍️ Eigene Notiz: \"${note.userChoice}\"")
            }
            sb.appendLine()
        }

        sb.appendLine("--- ALLE BEANTWORTETEN SPIEL-FRAGEN & PRÄFERENZEN (${analyzedAnswers.size} Antworten) ---")
        if (analyzedAnswers.isEmpty()) {
            sb.appendLine("(Noch keine Fragen beantwortet. Schlage 3 vielseitige, kreative Kennenlern- und Romantik-Dates vor.)")
        } else {
            for ((index, ans) in analyzedAnswers.withIndex()) {
                val matchStr = if (ans.isMatch) " [MATCH! Beide einig]" else ""
                val partnerStr = if (ans.partnerChoice != null) ", ${profile.partnerName}: ${ans.partnerChoice}" else ""
                val customTag = if (ans.isCustomText) " [EIGENE FREITEXT-NOTIZ]" else ""
                sb.appendLine("${index + 1}. [${ans.packTitle}] ${ans.questionOrPair}")
                sb.appendLine("   Antwort: ${profile.userName}: ${ans.userChoice}$partnerStr$matchStr$customTag")
            }
        }

        sb.appendLine()
        sb.appendLine("AUFTRAG:")
        sb.appendLine("Generiere genau 3 maßgeschneiderte Date-Ideen im folgenden JSON-Format:")
        sb.appendLine("""
            {
              "summary": "Kurzer, charmanter Einleitungssatz über die analysierten Antworten (z. B. 'Basierend auf eurer Liebe zu Kino, Pizza und eurer Notiz...')",
              "ideas": [
                {
                  "title": "Kreativer Date-Titel mit passendem Emoji",
                  "emoji": "🎬",
                  "vibe": "z. B. Gemütlich & Cineastisch / Abenteuerlich / Romantisch",
                  "inspiredBy": [
                    "Konkreter Bezug 1 (z. B. '🎬 Kino ↔ Couch & Decke: Kino gewählt')",
                    "Konkreter Bezug 2 (z. B. '✍️ Eigene Notiz: ...')"
                  ],
                  "description": "Lebendige, ansprechende Beschreibung der Date-Idee (2-3 Sätze).",
                  "steps": [
                    "Schritt 1: Konkreter Start/Vorbereitung",
                    "Schritt 2: Das Haupthighlight",
                    "Schritt 3: Schöner Ausklang"
                  ],
                  "conversationPrompt": "Eine persönliche Gesprächsfrage oder Mini-Challenge für das Date passend zu ihren Antworten",
                  "duration": "ca. 2-3 Stunden"
                }
              ]
            }
        """.trimIndent())

        return sb.toString()
    }

    private fun getCategoryIcon(pack: QuestionPack?, packId: String): String {
        if (pack != null && pack.emoji.isNotBlank()) return pack.emoji
        return when {
            packId.contains("panda") || packId.contains("tot") -> "🐼"
            packId.contains("wer") -> "🤔"
            packId.contains("essen") -> "🍽️"
            packId.contains("reise") -> "✈️"
            packId.contains("zuhause") || packId.contains("traumhaus") -> "🏡"
            packId.contains("aktivitaet") -> "🎯"
            packId.contains("film") || packId.contains("kino") -> "🎬"
            packId.contains("tief") || packId.contains("moral") -> "🌊"
            packId.contains("naehe") || packId.contains("intim") -> "🔥"
            else -> "✨"
        }
    }
}
