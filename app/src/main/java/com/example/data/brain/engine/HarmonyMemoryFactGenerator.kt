package com.example.data.brain.engine

import com.example.data.brain.db.BrainAnswerHistoryEntity
import com.example.data.brain.db.BrainMemoryFactEntity
import com.example.data.brain.db.BrainPreferenceEntity
import com.example.data.brain.model.BrainScope
import java.util.UUID

object HarmonyMemoryFactGenerator {

    /**
     * Generates condensed, verifiable memory facts based on answer history and high-confidence preferences.
     */
    fun deriveFacts(
        answers: List<BrainAnswerHistoryEntity>,
        preferences: List<BrainPreferenceEntity>
    ): List<BrainMemoryFactEntity> {
        val facts = mutableListOf<BrainMemoryFactEntity>()
        val answersByTag = mutableMapOf<String, MutableList<String>>()

        // Associate answers with detected tags
        for (ans in answers) {
            val tags = HarmonyLocalSignalExtractor.extractTagsFromText(
                "${ans.questionText} ${ans.answerPersonA.orEmpty()} ${ans.answerPersonB.orEmpty()}"
            )
            for (t in tags) {
                answersByTag.getOrPut(t) { mutableListOf() }.add(ans.id)
            }
        }

        // Group preferences by tag
        val prefsByTag = preferences.groupBy { it.tag }

        for ((tag, tagPrefs) in prefsByTag) {
            val prefCouple = tagPrefs.firstOrNull { it.scope == BrainScope.COUPLE.scopeKey }
            val prefA = tagPrefs.firstOrNull { it.scope == BrainScope.PERSON_A.scopeKey }
            val prefB = tagPrefs.firstOrNull { it.scope == BrainScope.PERSON_B.scopeKey }

            val sourceIds = answersByTag[tag] ?: emptyList()
            val sourceJson = "[" + sourceIds.take(5).joinToString(",") { "\"$it\"" } + "]"

            // 1. Couple Facts
            if (prefCouple != null && prefCouple.score >= 0.70 && prefCouple.confidence >= 0.35) {
                val factText = when (tag) {
                    "strand" -> "Beide genießen Urlaube am Strand und am Meer."
                    "berge" -> "Beide lieben die Natur, Berge und aktive Ausflüge."
                    "italien" -> "Beide haben eine starke Vorliebe für Reisen nach Italien."
                    "japan" -> "Beide möchten gemeinsam Japan und asiatische Kultur erleben."
                    "haus" -> "Beide wünschen sich für die Zukunft ein eigenes Haus."
                    "garten" -> "Ein Garten und Grünfläche ist beiden für ihr Zuhause wichtig."
                    "pizza" -> "Beide teilen die Begeisterung für gute italienische Pizza."
                    "sushi" -> "Beide essen leidenschaftlich gerne Sushi und asiatische Spezialitäten."
                    "kochen" -> "Beide kochen gerne gemeinsam in der eigenen Küche."
                    "couch_abend" -> "Beide schätzen gemütliche, ruhige Abende auf der Couch."
                    "kino_filme" -> "Beide lieben Kinoabende und Filme."
                    "brettspiele" -> "Beide haben großen Spaß an gemeinsamen Brettspielen."
                    "gaming" -> "Beide teilen ein Interesse an Videospielen und Gaming."
                    "wellness" -> "Beide entspannen gerne bei Wellness und Spa-Auszeiten."
                    "camping" -> "Beide mögen Outdoor-Abenteuer und Camping."
                    else -> "Beide teilen ein klares Interesse am Thema ${tag.replace("_", " ")}."
                }
                facts.add(
                    BrainMemoryFactEntity(
                        id = UUID.nameUUIDFromBytes("couple-$tag".toByteArray()).toString(),
                        factText = factText,
                        category = when {
                            tag in listOf("strand", "berge", "italien", "japan", "camping", "wellness", "roadtrip") -> "Reisen"
                            tag in listOf("pizza", "pasta", "sushi", "kochen", "bestellen", "kaffee") -> "Essen"
                            tag in listOf("haus", "wohnung", "garten", "familie_kinder") -> "Zukunft"
                            else -> "Freizeit"
                        },
                        personScope = BrainScope.COUPLE.scopeKey,
                        confidence = prefCouple.confidence,
                        importance = (prefCouple.score * 0.9).coerceIn(0.5, 1.0),
                        sourceAnswerIdsJson = sourceJson
                    )
                )
            }

            // 2. Person A Facts
            if (prefA != null && prefA.score >= 0.75 && prefA.confidence >= 0.35 && (prefB == null || prefB.score < 0.6)) {
                val factText = when (tag) {
                    "strand" -> "Person A bevorzugt Strandurlaub und Meer."
                    "berge" -> "Person A zieht Bergabenteuer und Wandern vor."
                    "pizza" -> "Person A bevorzugt klassische Pizza."
                    "sushi" -> "Person A wählt bevorzugt Sushi."
                    "kochen" -> "Person A steht gerne selbst am Herd."
                    "bestellen" -> "Person A bestellt gerne Essen nach Hause."
                    "kaffee" -> "Person A ist passionierte(r) Kaffeetrinker(in)."
                    "tee" -> "Person A bevorzugt Tee."
                    "gaming" -> "Person A begeistert sich für Gaming."
                    "brettspiele" -> "Person A liebt Brettspielabende."
                    else -> "Person A interessiert sich besonders für ${tag.replace("_", " ")}."
                }
                facts.add(
                    BrainMemoryFactEntity(
                        id = UUID.nameUUIDFromBytes("personA-$tag".toByteArray()).toString(),
                        factText = factText,
                        category = "Präferenz",
                        personScope = BrainScope.PERSON_A.scopeKey,
                        confidence = prefA.confidence,
                        importance = prefA.score * 0.8,
                        sourceAnswerIdsJson = sourceJson
                    )
                )
            }

            // 3. Person B Facts
            if (prefB != null && prefB.score >= 0.75 && prefB.confidence >= 0.35 && (prefA == null || prefA.score < 0.6)) {
                val factText = when (tag) {
                    "strand" -> "Person B bevorzugt Strandurlaub und Meer."
                    "berge" -> "Person B zieht Bergabenteuer und Natur vor."
                    "pizza" -> "Person B bevorzugt Pizza."
                    "sushi" -> "Person B wählt bevorzugt Sushi."
                    "kochen" -> "Person B kocht gerne selbst."
                    "bestellen" -> "Person B lässt sich gerne Essen liefern."
                    "kaffee" -> "Person B trinkt gerne Kaffee."
                    "tee" -> "Person B bevorzugt Tee."
                    "gaming" -> "Person B begeistert sich für Videospiele."
                    "brettspiele" -> "Person B mag gesellige Gesellschaftsspiele."
                    else -> "Person B interessiert sich besonders für ${tag.replace("_", " ")}."
                }
                facts.add(
                    BrainMemoryFactEntity(
                        id = UUID.nameUUIDFromBytes("personB-$tag".toByteArray()).toString(),
                        factText = factText,
                        category = "Präferenz",
                        personScope = BrainScope.PERSON_B.scopeKey,
                        confidence = prefB.confidence,
                        importance = prefB.score * 0.8,
                        sourceAnswerIdsJson = sourceJson
                    )
                )
            }
        }

        return facts
    }
}
