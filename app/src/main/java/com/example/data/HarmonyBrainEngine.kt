package com.example.data

import com.example.data.model.AnswerEntity
import com.example.data.model.BrainInterestEntity
import com.example.data.model.BrainSuggestionEntity
import com.example.data.model.BrainQuestionEntity
import com.example.data.model.EitherOrAnswerCodec
import java.util.UUID

object HarmonyBrainEngine {

    /**
     * Analysiert alle bisherigen Antworten des Paares und leitet lokale Interessen ab.
     */
    fun analyzeAnswers(answers: List<AnswerEntity>): List<BrainInterestEntity> {
        val interests = mutableListOf<BrainInterestEntity>()

        // 1. Suche nach "Entweder oder" Antworten (panda_either_or)
        val pandaAnswers = answers.filter { it.packId == "entweder_oder_panda" }
        
        // Zähler für bestimmte Vorlieben
        var strandCountMe = 0
        var strandCountPartner = 0
        var bergCountMe = 0
        var bergCountPartner = 0

        var pizzaCountMe = 0
        var pizzaCountPartner = 0
        var sushiCountMe = 0
        var sushiCountPartner = 0

        var couchCountMe = 0
        var couchCountPartner = 0
        var kinoCountMe = 0
        var kinoCountPartner = 0

        var kaffeeCountMe = 0
        var kaffeeCountPartner = 0
        var teeCountMe = 0
        var teeCountPartner = 0

        var boardCountMe = 0
        var boardCountPartner = 0
        var videoCountMe = 0
        var videoCountPartner = 0

        var campingCountMe = 0
        var campingCountPartner = 0
        var hotelCountMe = 0
        var hotelCountPartner = 0

        var kochenCountMe = 0
        var kochenCountPartner = 0
        var bestellenCountMe = 0
        var bestellenCountPartner = 0

        for (ans in pandaAnswers) {
            val decoded = EitherOrAnswerCodec.decode(ans.answerText) ?: continue
            val leftSelected = decoded.userChoice
            val rightSelected = decoded.partnerChoice

            // Index 1: "Strandurlaub 🏖️" to "Bergabenteuer 🏔️"
            if (ans.questionIndex == 1) {
                if (leftSelected.contains("Strand")) strandCountMe++ else bergCountMe++
                if (rightSelected.contains("Strand")) strandCountPartner++ else bergCountPartner++
            }
            // Index 3: Pizza to Sushi
            if (ans.questionIndex == 3) {
                if (leftSelected.contains("Pizza")) pizzaCountMe++ else sushiCountMe++
                if (rightSelected.contains("Pizza")) pizzaCountPartner++ else sushiCountPartner++
            }
            // Index 6: Kaffee to Tee
            if (ans.questionIndex == 6) {
                if (leftSelected.contains("Kaffee")) kaffeeCountMe++ else teeCountMe++
                if (rightSelected.contains("Kaffee")) kaffeeCountPartner++ else teeCountPartner++
            }
            // Index 8: Kino to Couch
            if (ans.questionIndex == 8) {
                if (leftSelected.contains("Kino")) kinoCountMe++ else couchCountMe++
                if (rightSelected.contains("Kino")) kinoCountPartner++ else couchCountPartner++
            }
            // Index 12: Camping to 5-Sterne-Hotel
            if (ans.questionIndex == 12) {
                if (leftSelected.contains("Camping")) campingCountMe++ else hotelCountMe++
                if (rightSelected.contains("Camping")) campingCountPartner++ else hotelCountPartner++
            }
            // Index 17: Kochen to Bestellen
            if (ans.questionIndex == 17) {
                if (leftSelected.contains("Kochen")) kochenCountMe++ else bestellenCountMe++
                if (rightSelected.contains("Kochen")) kochenCountPartner++ else bestellenCountPartner++
            }
            // Index 26: Brettspiel to Videospiel
            if (ans.questionIndex == 26) {
                if (leftSelected.contains("Brettspiel")) boardCountMe++ else videoCountMe++
                if (rightSelected.contains("Brettspiel")) boardCountPartner++ else videoCountPartner++
            }
        }

        // --- Auswertung und Generierung von Interessen ---

        // Reisen (Strand vs Berge)
        if (strandCountMe > 0 && strandCountPartner > 0) {
            interests.add(BrainInterestEntity("Strand & Meerurlaub", "Reisen", "sicher", "Beide bevorzugen Entspannung am Strand 🏖️."))
        } else if (bergCountMe > 0 && bergCountPartner > 0) {
            interests.add(BrainInterestEntity("Bergabenteuer & Natur", "Reisen", "sicher", "Beide bevorzugen Bergwandern und Natur 🏔️."))
        } else if (strandCountMe > 0 || strandCountPartner > 0 || bergCountMe > 0 || bergCountPartner > 0) {
            val strandPref = strandCountMe + strandCountPartner > bergCountMe + bergCountPartner
            val name = if (strandPref) "Strand & Meerurlaub" else "Bergabenteuer & Natur"
            interests.add(BrainInterestEntity(name, "Reisen", "wahrscheinlich", "Abgeleitet aus euren Entscheidungen bei Entweder-Oder."))
        }

        // Essen (Pizza vs Sushi)
        if (sushiCountMe > 0 && sushiCountPartner > 0) {
            interests.add(BrainInterestEntity("Sushi & asiatische Küche", "Essen", "sicher", "Beide lieben Sushi statt Pizza 🍣."))
        } else if (pizzaCountMe > 0 && pizzaCountPartner > 0) {
            interests.add(BrainInterestEntity("Pizza & italienisches Essen", "Essen", "sicher", "Beide bevorzugen traditionelle italienische Pizza 🍕."))
        } else if (sushiCountMe > 0 || sushiCountPartner > 0 || pizzaCountMe > 0 || pizzaCountPartner > 0) {
            val sushiPref = sushiCountMe + sushiCountPartner > pizzaCountMe + pizzaCountPartner
            val name = if (sushiPref) "Asiatische Küche" else "Italienische Küche"
            interests.add(BrainInterestEntity(name, "Essen", "wahrscheinlich", "Abgeleitet aus euren Antworten im Essens-Vergleich."))
        }

        // Freizeit (Couch vs Kino)
        if (couchCountMe > 0 && couchCountPartner > 0) {
            interests.add(BrainInterestEntity("Ruhige Couch-Abende", "Freizeit", "sicher", "Beide lieben die Gemütlichkeit zu Hause 🛋️."))
        } else if (kinoCountMe > 0 && kinoCountPartner > 0) {
            interests.add(BrainInterestEntity("Kino & Film-Abende", "Freizeit", "sicher", "Beide gehen gerne ins Kino für Filme 🎬."))
        } else if (couchCountMe > 0 || couchCountPartner > 0 || kinoCountMe > 0 || kinoCountPartner > 0) {
            val couchPref = couchCountMe + couchCountPartner > kinoCountMe + kinoCountPartner
            val name = if (couchPref) "Zuhause entspannen" else "Kino & Events"
            interests.add(BrainInterestEntity(name, "Freizeit", "wahrscheinlich", "Tendenz zu gemütlichen Abenden versus Ausgehen."))
        }

        // Getränke (Kaffee vs Tee)
        if (kaffeeCountMe > 0 && kaffeeCountPartner > 0) {
            interests.add(BrainInterestEntity("Kaffeegenuss", "Essen", "sicher", "Ihr seid beide passionierte Kaffeetrinker ☕."))
        } else if (teeCountMe > 0 && teeCountPartner > 0) {
            interests.add(BrainInterestEntity("Teezeremonien", "Essen", "sicher", "Ihr bevorzugt beide eine heiße Tasse Tee 🍵."))
        }

        // Spiele (Brettspiel vs Videospiel)
        if (boardCountMe > 0 && boardCountPartner > 0) {
            interests.add(BrainInterestEntity("Brettspiele", "Freizeit", "sicher", "Ihr liebt beide klassische Spieleabende 🎲."))
        } else if (videoCountMe > 0 && videoCountPartner > 0) {
            interests.add(BrainInterestEntity("Gaming / Videospiele", "Freizeit", "sicher", "Ihr zockt beide gerne Konsolen- oder PC-Spiele 🎮."))
        } else if (boardCountMe > 0 || videoCountMe > 0 || boardCountPartner > 0 || videoCountPartner > 0) {
            val boardPref = boardCountMe + boardCountPartner > videoCountMe + videoCountPartner
            val name = if (boardPref) "Gesellschaftsspiele" else "Gaming-Kultur"
            interests.add(BrainInterestEntity(name, "Freizeit", "wahrscheinlich", "Interesse an gemeinsamen spielerischen Aktivitäten."))
        }

        // Camping vs Hotel
        if (campingCountMe > 0 && campingCountPartner > 0) {
            interests.add(BrainInterestEntity("Camping & Outdoor", "Reisen", "sicher", "Ihr liebt beide das Abenteuer unter freiem Himmel ⛺."))
        } else if (hotelCountMe > 0 && hotelCountPartner > 0) {
            interests.add(BrainInterestEntity("Wellness & Luxushotels", "Reisen", "sicher", "Ihr genießt beide den Komfort eines feinen Hotels 🏨."))
        }

        // Kochen vs Bestellen
        if (kochenCountMe > 0 && kochenCountPartner > 0) {
            interests.add(BrainInterestEntity("Gemeinsam kochen", "Essen", "sicher", "Ihr steht beide gerne selbst in der Küche 👩‍🍳."))
        } else if (bestellenCountMe > 0 && bestellenCountPartner > 0) {
            interests.add(BrainInterestEntity("Essen bestellen & liefern", "Essen", "sicher", "Ihr lasst euch gerne mit geliefertem Essen verwöhnen 🛵."))
        }

        // 2. Vermutungen und Brücken (Cross-Interests)
        // Wenn einer klassische Musik mag (z.B. aus anderen Fragen) und einer Anime:
        // Wir setzen das standardmäßig als spannende Brücke rein
        interests.add(
            BrainInterestEntity(
                "Orchester- & Anime-Soundtracks",
                "Musik",
                "vermutung",
                "Spannende Kombination! Verbindung aus Anime-Liebe & orchestraler Musik."
            )
        )

        // Fallback: Falls noch gar keine Antworten vorliegen, geben wir Standard-Starter-Interessen als Vermutung aus
        if (interests.size <= 1) {
            interests.add(BrainInterestEntity("Gemeinsame Genussmomente", "Essen", "vermutung", "Erste Vermutung basierend auf eurem Profil."))
            interests.add(BrainInterestEntity("Entdeckungsreisen", "Reisen", "vermutung", "Ihr wollt sicher bald die Welt gemeinsam erkunden."))
            interests.add(BrainInterestEntity("Neue Filme & Serien", "Freizeit", "vermutung", "Ihr habt bestimmt Lust auf einen Filmabend."))
        }

        return interests
    }

    /**
     * Erzeugt eine Liste personalisierter Vorschläge basierend auf den erkannten Interessen.
     */
    fun generateSuggestions(interests: List<BrainInterestEntity>): List<BrainSuggestionEntity> {
        val list = mutableListOf<BrainSuggestionEntity>()

        // Finde bestimmte Interessen
        val hasSushi = interests.any { it.name.contains("Sushi") || it.name.contains("Asiatische") }
        val hasPizza = interests.any { it.name.contains("Pizza") || it.name.contains("Italienische") }
        val hasStrand = interests.any { it.name.contains("Strand") || it.name.contains("Meer") }
        val hasBerge = interests.any { it.name.contains("Berg") || it.name.contains("Natur") }
        val hasCouch = interests.any { it.name.contains("Couch") || it.name.contains("Zuhause") }
        val hasKino = interests.any { it.name.contains("Kino") }
        val hasGames = interests.any { it.name.contains("Spiele") || it.name.contains("Gaming") }
        val hasKochen = interests.any { it.name.contains("kochen") }
        val hasCamping = interests.any { it.name.contains("Camping") }

        // 1. Sushi-Date
        if (hasSushi) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_sushi_date",
                    title = "Sushi-Date & Stäbchen-Challenge",
                    description = "Besucht euer liebstes Sushi-Restaurant oder rollt euer Sushi gemeinsam zu Hause. Wer rollt die schönste Maki-Rolle? Perfekt für ein leckeres, interaktives Date.",
                    category = "Essen",
                    matchReason = "Passt zu euch, weil ihr beide eine Vorliebe für Sushi & asiatisches Essen teilt."
                )
            )
        }

        // 2. Pizza-Abend
        if (hasPizza) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_pizza_night",
                    title = "Selbstgemachte neapolitanische Pizza",
                    description = "Knetet gemeinsam einen luftigen Pizzateig, lasst ihn gehen und belegt ihn mit euren Lieblingszutaten. Backt die Pizza super heiß und genießt sie bei Kerzenschein und italienischer Musik.",
                    category = "Essen",
                    matchReason = "Passt perfekt zu eurem gemeinsamen Interesse an italienischer Pizza 🍕."
                )
            )
        }

        // 3. Strand / See-Ausflug
        if (hasStrand) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_beach_day",
                    title = "Romantischer Tag am Wasser",
                    description = "Packt eure Badesachen, Sonnencreme und ein Picknick ein. Fahrt an den nächsten See oder Strand und lauscht dem Rauschen des Wassers. Schaut euch gemeinsam den Sonnenuntergang an.",
                    category = "Ausflug",
                    matchReason = "Basiert auf eurer gemeinsamen Liebe zu Strand, Meer und Entspannung 🌊."
                )
            )
        }

        // 4. Berge / Natur-Ausflug
        if (hasBerge) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_mountain_hike",
                    title = "Gipfelstürmer-Wanderung & Picknick",
                    description = "Sucht euch eine malerische Wanderroute in den Bergen oder der Natur heraus. Packt einen Rucksack mit deftigen Snacks und genießt euer Picknick bei einem atemberaubenden Panoramablick auf dem Berggipfel.",
                    category = "Ausflug",
                    matchReason = "Passt zu eurem Sinn für Naturerlebnisse und Wanderungen 🏔️."
                )
            )
        }

        // 5. Couch-Tag
        if (hasCouch) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_couch_marathon",
                    title = "Kuscheliger Serienmarathon",
                    description = "Macht es euch auf dem Sofa so richtig bequem mit einer Festung aus Decken und Kissen. Holt euch Popcorn und eure Lieblingsgetränke und schaut eine neue Serie, die ihr beide noch nicht kennt.",
                    category = "Aktivität",
                    matchReason = "Entspricht eurem Wunsch nach ruhigen, gemütlichen Abenden zu zweit zu Hause 🛋️."
                )
            )
        }

        // 6. Kino-Date
        if (hasKino) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_cinema_date",
                    title = "Klassischer Kino-Abend mit XXL-Popcorn",
                    description = "Besucht ein gemütliches Programmkino oder ein großes Lichtspielhaus. Holt euch eine riesige Portion Popcorn zum Teilen und taucht gemeinsam in eine andere Welt ein. Diskutiert den Film danach bei einem Drink.",
                    category = "Date",
                    matchReason = "Passt zu eurer Vorliebe für Filme und echte Kino-Atmosphäre 🎬."
                )
            )
        }

        // 7. Spieleabend
        if (hasGames) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_games_night",
                    title = "Spieleduell: Brettspiele & Snacks",
                    description = "Bereitet Fingerfood vor und tretet in euren Lieblingsspielen gegeneinander an. Egal ob Kniffel, Monopoly oder ein schnelles Koop-Videospiel auf der Couch – der Verlierer muss das nächste Frühstück machen!",
                    category = "Aktivität",
                    matchReason = "Basiert auf eurer Spielfreude bei Brett- und Videospielen 🎲."
                )
            )
        }

        // 8. Koch-Challenge
        if (hasKochen) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_cooking_challenge",
                    title = "Das 3-Zutaten-Blindkochen",
                    description = "Jeder wählt heimlich 3 Zutaten aus. Gemeinsam müsst ihr nun aus diesen 6 Zutaten ein leckeres Gericht zaubern. Kreativität und viel Gelächter in der Küche sind garantiert!",
                    category = "Essen",
                    matchReason = "Nutzt eure Freude am gemeinsamen Kochen für ein spielerisches Küchendate 👩‍🍳."
                )
            )
        }

        // 9. Camping-Ausflug
        if (hasCamping) {
            list.add(
                BrainSuggestionEntity(
                    id = "sug_camping_adventure",
                    title = "Nacht unter den Sternen",
                    description = "Schlagt euer Zelt im Garten, auf einem Campingplatz oder im Wohnzimmer auf! Macht ein kleines Lagerfeuer (oder stellt Kerzen auf), röstet Marshmallows und schaut in den Nachthimmel.",
                    category = "Reisen",
                    matchReason = "Passt perfekt, da ihr beide das Abenteuer und Camping liebt ⛺."
                )
            )
        }

        // 10. Wildcard-Vorschlag (Anime-Orchester-Abend)
        list.add(
            BrainSuggestionEntity(
                id = "sug_anime_symphony",
                title = "Anime-Soundtracks & Kerzenlicht",
                description = "Macht das Licht aus, zündet Kerzen an und hört euch orchestrale Versionen bekannter Anime- und Filmsoundtracks an. Eine faszinierende Verbindung aus klassischer Musik und modernen Geschichten.",
                category = "Aktivität",
                matchReason = "Eine kreative Kombination aus unterschiedlichen Welten: Klassische Musik trifft epische Abenteuer."
            )
        )

        // 11. Zusätzlicher Ausflug (Rooftop-Bar)
        list.add(
            BrainSuggestionEntity(
                id = "sug_rooftop_drink",
                title = "Cocktails über den Dächern",
                description = "Zieht euch schick an und besucht eine Rooftop-Bar in eurer Stadt. Genießt den weiten Ausblick und stoßt auf eure gemeinsame Geschichte an.",
                category = "Date",
                matchReason = "Ein toller Vorschlag für einen unvergesslichen romantischen Moment zu zweit."
            )
        )

        // 12. Spaziergang im Regen (Gemütliche Natur)
        list.add(
            BrainSuggestionEntity(
                id = "sug_rainy_walk",
                title = "Regenspaziergang & heißer Kakao",
                description = "Zieht euch warm an, schnappt euch einen großen Regenschirm und macht einen Spaziergang im Wald oder Park, während der Regen prasselt. Wärmt euch danach zu Hause mit einem heißen Kakao mit Marshmallows auf.",
                category = "Ausflug",
                matchReason = "Passt zu eurer Vorliebe für gemütliche, naturverbundene Momente."
            )
        )

        return list
    }

    /**
     * Erzeugt eine Liste von klugen, ansteigenden Fragen, die Harmony Brain stellen kann,
     * um fehlende Bereiche im Profil auszufüllen.
     */
    fun generateQuestions(): List<BrainQuestionEntity> {
        return listOf(
            BrainQuestionEntity(
                id = "bq_travel_style",
                text = "Welches Land oder welche Kultur fasziniert euch beide am meisten und steht ganz oben auf eurer gemeinsamen Reiseliste?",
                category = "Reisen",
                difficulty = "easy"
            ),
            BrainQuestionEntity(
                id = "bq_weekend_style",
                text = "Wie sieht für euch beide ein absolut perfektes, stressfreies Wochenende aus, an dem ihr mal gar nichts erledigen müsst?",
                category = "Freizeit",
                difficulty = "easy"
            ),
            BrainQuestionEntity(
                id = "bq_cooking_preference",
                text = "Gibt es ein bestimmtes Gericht oder eine Länderküche (z.B. Mexikanisch, Indisch), die ihr beide noch nie ausprobiert habt, aber gerne testen wollt?",
                category = "Essen",
                difficulty = "medium"
            ),
            BrainQuestionEntity(
                id = "bq_future_dreams",
                text = "Wenn ihr euch euer Leben in genau fünf Jahren vorstellt – was ist der größte Traum, den ihr bis dahin gemeinsam verwirklicht haben wollt?",
                category = "Beziehung",
                difficulty = "deep"
            ),
            BrainQuestionEntity(
                id = "bq_communication_needs",
                text = "In welchen Momenten wünschst du dir im Alltag noch mehr Aufmerksamkeit oder Unterstützung von deinem Partner?",
                category = "Kommunikation",
                difficulty = "deep"
            )
        )
    }
}
