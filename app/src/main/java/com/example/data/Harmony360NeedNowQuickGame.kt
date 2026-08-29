package com.example.data

/** Stage 05.1f: short two-choice relationship game using the existing choice runner. */
object Harmony360NeedNowQuickGame {

    val PACK = GenPack(
        id = "h360_need_now_quick",
        title = "Was brauchst du gerade?",
        cat = "tot",
        topic = "beziehung",
        type = "quiz",
        tags = listOf(
            "harmony360",
            "h360_stage_05_1",
            "quick_game",
            "mechanik_entweder_oder",
            "intensitaet_leicht"
        ),
        emoji = "💞",
        questions = listOf(
            GenQuestion(
                q = "Du kommst nach einem richtig miesen Tag nach Hause. Was hilft dir eher?",
                options = listOf("Umarmung ohne Fragen", "Erst mal 20 Minuten Ruhe")
            ),
            GenQuestion(
                q = "Du erzählst mir ein Problem. Was willst du zuerst?",
                options = listOf("Einfach zuhören", "Mit mir eine Lösung suchen")
            ),
            GenQuestion(
                q = "Wir hatten einen kleinen Streit. Was bringt dich eher wieder zurück?",
                options = listOf("Nähe", "Erst einmal Abstand")
            ),
            GenQuestion(
                q = "Du fühlst dich gerade übersehen. Was würde heute mehr bedeuten?",
                options = listOf("Zeit nur für uns", "Eine kleine persönliche Geste")
            ),
            GenQuestion(
                q = "Du bist stiller als sonst. Was soll ich eher tun?",
                options = listOf("Direkt liebevoll nachfragen", "Warten, bis du selbst anfängst")
            ),
            GenQuestion(
                q = "Du bist gestresst und wir haben etwas geplant. Was wäre liebevoller?",
                options = listOf("Den Plan vereinfachen", "Dich entscheiden lassen")
            ),
            GenQuestion(
                q = "Du zweifelst gerade an dir. Was hilft dir eher?",
                options = listOf("Konkrete Bestärkung", "Einfach bei dir sein")
            ),
            GenQuestion(
                q = "Du brauchst Nähe, aber ich merke es nicht. Was wäre dir lieber?",
                options = listOf("Ich frage aktiv nach", "Du sagst es direkt")
            ),
            GenQuestion(
                q = "Du willst heute nichts mehr entscheiden. Was entlastet dich eher?",
                options = listOf("Ich übernehme eine kleine Entscheidung", "Wir lassen alles offen")
            ),
            GenQuestion(
                q = "Du wirkst plötzlich genervt. Was ist bei dir wahrscheinlicher?",
                options = listOf("Ich bin wirklich sauer", "Ich bin eigentlich nur hungrig")
            )
        )
    )

    fun appendTo(packs: List<GenPack>): List<GenPack> =
        packs.filterNot { it.id == PACK.id } + PACK
}
