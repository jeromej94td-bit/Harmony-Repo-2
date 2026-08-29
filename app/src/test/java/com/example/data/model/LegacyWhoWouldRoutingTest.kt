package com.example.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LegacyWhoWouldRoutingTest {

    private fun pack(id: String, tags: List<String>, question: Question) = QuestionPack(
        id = id,
        title = id,
        tags = tags,
        cat = "wer",
        topic = "familie",
        type = "quiz",
        questions = listOf(question)
    )

    @Test
    fun `legacy explicit wer-wuerde-eher tag uses who-would board`() {
        val source = pack(
            "kinder_teil_2",
            listOf("kinder", "familie", "wer-wuerde-eher"),
            Question(
                "Wer ist wohl eher das Kitzelmonster?",
                listOf("{user}", "{partner}", "Beide", "Keiner")
            )
        )

        assertEquals(
            FullscreenGameMechanicKind.WHO_WOULD,
            FullscreenGameMechanicPolicy.resolve(source, 0)
        )
    }

    @Test
    fun `generic wer category without mechanic tag stays standard`() {
        val source = pack(
            "filmabendentscheidung",
            listOf("filme", "alltag", "spaß"),
            Question(
                "Was entscheidet bei euch zuerst über einen Film?",
                listOf("Genre", "Trailer", "Bewertung", "Wer mitspielt")
            )
        )

        assertNull(FullscreenGameMechanicPolicy.resolve(source, 0))
    }
}
