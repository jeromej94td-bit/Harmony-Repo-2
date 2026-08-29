package com.example.data

/**
 * Kleine, explizite Reparaturen für bereits generierten Content.
 *
 * Die generierten Quelldateien bleiben dadurch unangetastet; bekannte strukturelle
 * Defekte werden erst beim Aufbau der Runtime-Registry korrigiert.
 */
object GeneratedContentRepairPolicy {
    private val neverHaveIEverOptions = listOf("Habe ich", "Habe ich noch nie")

    fun repair(pack: GenPack): GenPack {
        var result = pack

        if ("ichhabenochnie" in result.tags) {
            result = result.copy(
                questions = result.questions.map { question ->
                    if (question.options.isEmpty()) {
                        question.copy(options = neverHaveIEverOptions)
                    } else {
                        question
                    }
                }
            )
        }

        if (result.id == "cj_hogwarts_quiz") {
            result = neutralizeMagicAcademyPack(result)
        }
        if (result.id == "cj_disney_quiz") {
            result = neutralizeAnimationPack(result)
        }
        if (result.id == "cj_entertainment_quiz") {
            result = neutralizeEntertainmentPack(result)
        }

        return result
    }

    private fun neutralizeMagicAcademyPack(pack: GenPack): GenPack = pack.copy(
        title = "Magische Akademie: Welches Haus passt zu dir?",
        tags = pack.tags.map { tag ->
            if (tag == "harrypotter") "fantasy_magie" else tag
        },
        questions = pack.questions.map { question ->
            question.copy(
                q = question.q
                    .replace("aus Hogwarts", "von einer magischen Akademie")
                    .replace("Hogwarts-Haus", "magisches Haus")
                    .replace("Harry Potter Bände", "Fantasy-Bände über Magie")
                    .replace("Quidditch zu spielen", "in einem magischen Besenturnier mitzuspielen")
                    .replace("als \"Muggel\"", "als \"Nichtmagier\"")
                    .replace("Harry-Potter-Film-Marathon", "Fantasy-Film-Marathon mit Zauberschulen")
                    .replace("Charakter aus Hogwarts", "Charakter aus einer magischen Akademiegeschichte")
                    .replace("Slytherin", "das vermeintlich düstere Haus")
            )
        }
    )

    private fun neutralizeAnimationPack(pack: GenPack): GenPack = pack.copy(
        title = "Animationswelten, Abenteuer und Feenstaub",
        tags = pack.tags.map { tag ->
            if (tag == "disney") "animation_fantasy" else tag
        },
        questions = pack.questions.map { question ->
            question.copy(
                q = question.q
                    .replace("Disney-Film", "Animationsfilm")
                    .replace("aus \"Der König der Löwen\"", "aus einem bekannten Animationsklassiker")
                    .replace("wie bei Disney", "wie in Animationsfilmen")
                    .replace("Schneewittchen", "einer Märchenfigur")
                    .replace("ins Disneyland", "in einen großen Themenpark")
                    .replace("Disney-Ohrwurm", "Animationsfilm-Ohrwurm")
                    .replace("Disney-Filme", "Animationsfilme")
                    .replace("Meister Yoda aus Star Wars", "einem weisen Weltraum-Meister")
                    .replace("Marvel-Film", "Superheldenfilm")
                    .replace("bei \"Oben\" (Up)", "bei einem besonders emotionalen Animationsfilm")
                    .replace("aus Toy Story", "aus einer Spielzeug-Abenteuergeschichte")
                    .replace("wie Elsa", "wie eine Eiszauberin")
                    .replace("Disney-Lied", "Animationsfilm-Lied")
            )
        }
    )

    private fun neutralizeEntertainmentPack(pack: GenPack): GenPack = pack.copy(
        questions = pack.questions.map { question ->
            question.copy(
                q = question.q
                    .replace("Netflix-Passwort", "Streaming-Passwort")
                    .replace("Oscar-Verleihung", "große Filmpreis-Verleihung")
            )
        }
    )
}
