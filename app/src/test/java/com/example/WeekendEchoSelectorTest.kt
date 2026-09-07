package com.example

import com.example.data.model.AnswerEntity
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.WeekendEchoAnswerCodec
import com.example.data.model.WeekendEchoMotif
import com.example.data.model.WeekendEchoPerson
import com.example.data.model.WeekendEchoRoundAnswer
import com.example.data.model.WeekendEchoSelector
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class WeekendEchoSelectorTest {
    @Test
    fun `catalog recognizes exactly the eight approved stored choices`() {
        val stored = listOf(
            "Couch & Decke 🛋️" to WeekendEchoMotif.COUCH_BLANKET,
            "Kino 🎬" to WeekendEchoMotif.CINEMA,
            "Stadt 🌆" to WeekendEchoMotif.CITY,
            "Land 🌳" to WeekendEchoMotif.COUNTRY,
            "Roadtrip 🚗" to WeekendEchoMotif.ROADTRIP,
            "Zug 🚆" to WeekendEchoMotif.TRAIN,
            "Camping ⛺" to WeekendEchoMotif.CAMPING,
            "5-Sterne-Hotel 🏨" to WeekendEchoMotif.HOTEL
        )

        assertEquals(8, WeekendEchoMotif.entries.size)
        stored.forEach { (raw, expected) ->
            assertEquals(expected, WeekendEchoMotif.fromStored(raw))
        }
        assertNull(WeekendEchoMotif.fromStored("Flugzeug ✈️"))
    }

    @Test
    fun `selector keeps both peoples genuine panda choices`() {
        val history = listOf(
            AnswerEntity(
                packId = WeekendEchoSelector.SOURCE_PACK_ID,
                questionIndex = 8,
                answerText = EitherOrAnswerCodec.encode("Kino 🎬", "Couch & Decke 🛋️"),
                timestamp = 100L
            )
        )

        val candidates = WeekendEchoSelector.candidates(history)

        assertEquals(2, candidates.size)
        assertEquals(setOf(WeekendEchoPerson.PERSON_A), candidates.first { it.motif == WeekendEchoMotif.CINEMA }.people)
        assertEquals(setOf(WeekendEchoPerson.PERSON_B), candidates.first { it.motif == WeekendEchoMotif.COUCH_BLANKET }.people)
    }

    @Test
    fun `selector excludes plain text current pack and malformed choices`() {
        val history = listOf(
            AnswerEntity("some_open_pack", 0, "Couch & Decke", 100L),
            AnswerEntity(WeekendEchoSelector.PACK_ID, 0, EitherOrAnswerCodec.encode("Kino 🎬", "Stadt 🌆"), 200L),
            AnswerEntity(WeekendEchoSelector.SOURCE_PACK_ID, 1, "not-encoded", 300L)
        )

        assertTrue(WeekendEchoSelector.candidates(history).isEmpty())
    }

    @Test
    fun `selector returns no duel with fewer than two distinct motifs`() {
        val history = listOf(
            AnswerEntity(
                WeekendEchoSelector.SOURCE_PACK_ID,
                8,
                EitherOrAnswerCodec.encode("Kino 🎬", "Kino 🎬"),
                100L
            )
        )

        assertNull(WeekendEchoSelector.select(history, questionIndex = 3))
    }

    @Test
    fun `selection is deterministic regardless of history order`() {
        val history = listOf(
            AnswerEntity(WeekendEchoSelector.SOURCE_PACK_ID, 8, EitherOrAnswerCodec.encode("Kino 🎬", "Couch & Decke 🛋️"), 100L),
            AnswerEntity(WeekendEchoSelector.SOURCE_PACK_ID, 9, EitherOrAnswerCodec.encode("Stadt 🌆", "Land 🌳"), 200L),
            AnswerEntity(WeekendEchoSelector.SOURCE_PACK_ID, 10, EitherOrAnswerCodec.encode("Roadtrip 🚗", "Zug 🚆"), 300L)
        )

        assertEquals(
            WeekendEchoSelector.select(history, questionIndex = 3),
            WeekendEchoSelector.select(history.reversed(), questionIndex = 3)
        )
    }

    @Test
    fun `round codec preserves both private selections and rejects invalid input`() {
        val encoded = WeekendEchoAnswerCodec.encode("cinema", "couch_blanket")

        assertEquals(
            WeekendEchoRoundAnswer("cinema", "couch_blanket"),
            WeekendEchoAnswerCodec.decode(encoded)
        )
        assertNull(WeekendEchoAnswerCodec.decode("cinema"))
        assertNull(WeekendEchoAnswerCodec.decode(WeekendEchoAnswerCodec.encode("cinema", "unknown")))
    }
}
