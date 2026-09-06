package com.example.data.model

import java.util.Locale

enum class WeekendEchoMotif(
    val key: String,
    val displayText: String,
    internal val chapterWeights: List<Int>
) {
    COUCH_BLANKET("couch_blanket", "Couch & Decke", listOf(1, 2, 1, 10, 8, 4, 9, 6)),
    CINEMA("cinema", "Kino", listOf(4, 4, 3, 9, 4, 7, 5, 5)),
    CITY("city", "Stadt", listOf(10, 7, 5, 6, 5, 7, 4, 6)),
    COUNTRY("country", "Land", listOf(10, 6, 6, 7, 6, 5, 6, 7)),
    ROADTRIP("roadtrip", "Roadtrip", listOf(8, 8, 5, 7, 7, 6, 8, 8)),
    TRAIN("train", "Zug", listOf(6, 5, 8, 6, 6, 5, 10, 5)),
    CAMPING("camping", "Camping", listOf(5, 8, 10, 6, 5, 4, 5, 9)),
    HOTEL("hotel", "5-Sterne-Hotel", listOf(6, 8, 10, 9, 8, 5, 8, 7));

    companion object {
        private val normalizedCatalog = entries.associateBy { normalize(it.displayText) }
        private val keyCatalog = entries.associateBy(WeekendEchoMotif::key)

        fun fromStored(raw: String): WeekendEchoMotif? = normalizedCatalog[normalize(raw)]

        fun fromKey(key: String): WeekendEchoMotif? = keyCatalog[key]

        private fun normalize(value: String): String = value
            .lowercase(Locale.ROOT)
            .replace(Regex("[^\\p{L}\\p{N}&-]+"), " ")
            .trim()
            .replace(Regex("\\s+"), " ")
    }
}

enum class WeekendEchoPerson { PERSON_A, PERSON_B }

data class WeekendEchoCandidate(
    val motif: WeekendEchoMotif,
    val people: Set<WeekendEchoPerson>,
    val sourceQuestionIndex: Int,
    val timestamp: Long
)

data class WeekendEchoDuel(
    val left: WeekendEchoCandidate,
    val right: WeekendEchoCandidate
)

data class WeekendEchoRoundAnswer(
    val firstMotifKey: String,
    val secondMotifKey: String
)

object WeekendEchoSelector {
    const val PACK_ID = "h500_076_wochenendtrip_szenario"
    const val SOURCE_PACK_ID = "entweder_oder_panda"

    fun candidates(history: List<AnswerEntity>): List<WeekendEchoCandidate> {
        val matches = history
            .asSequence()
            .filter { it.packId == SOURCE_PACK_ID }
            .mapNotNull { answer ->
                val pair = EitherOrAnswerCodec.decode(answer.answerText) ?: return@mapNotNull null
                listOfNotNull(
                    WeekendEchoMotif.fromStored(pair.userChoice)?.let {
                        MotifAnswer(it, WeekendEchoPerson.PERSON_A, answer)
                    },
                    WeekendEchoMotif.fromStored(pair.partnerChoice)?.let {
                        MotifAnswer(it, WeekendEchoPerson.PERSON_B, answer)
                    }
                )
            }
            .flatten()
            .toList()

        return matches
            .groupBy(MotifAnswer::motif)
            .map { (motif, motifMatches) ->
                val newest = motifMatches.maxWith(
                    compareBy<MotifAnswer> { it.answer.timestamp }
                        .thenBy { it.answer.questionIndex }
                )
                WeekendEchoCandidate(
                    motif = motif,
                    people = motifMatches.mapTo(linkedSetOf(), MotifAnswer::person),
                    sourceQuestionIndex = newest.answer.questionIndex,
                    timestamp = newest.answer.timestamp
                )
            }
            .sortedBy { it.motif.key }
    }

    fun select(history: List<AnswerEntity>, questionIndex: Int): WeekendEchoDuel? {
        val chapter = questionIndex.coerceIn(0, 7)
        val ranked = candidates(history).sortedWith(
            compareByDescending<WeekendEchoCandidate> { it.motif.chapterWeights[chapter] }
                .thenBy { stableTieBreak(it.motif.key, chapter) }
                .thenBy { it.motif.key }
        )
        return if (ranked.size >= 2) WeekendEchoDuel(ranked[0], ranked[1]) else null
    }

    private fun stableTieBreak(key: String, chapter: Int): Long =
        key.fold(17L + chapter) { value, character -> (value * 37L + character.code) and 0x7fffffffL }

    private data class MotifAnswer(
        val motif: WeekendEchoMotif,
        val person: WeekendEchoPerson,
        val answer: AnswerEntity
    )
}

object WeekendEchoAnswerCodec {
    private const val PREFIX = "weekend-echo-v1:"
    private const val SEPARATOR = "\u001F"

    fun encode(firstMotifKey: String, secondMotifKey: String): String =
        PREFIX + firstMotifKey.replace(SEPARATOR, " ") + SEPARATOR + secondMotifKey.replace(SEPARATOR, " ")

    fun decode(value: String): WeekendEchoRoundAnswer? {
        if (!value.startsWith(PREFIX)) return null
        val parts = value.removePrefix(PREFIX).split(SEPARATOR, limit = 2)
        if (parts.size != 2) return null
        if (WeekendEchoMotif.fromKey(parts[0]) == null || WeekendEchoMotif.fromKey(parts[1]) == null) return null
        return WeekendEchoRoundAnswer(parts[0], parts[1])
    }
}
