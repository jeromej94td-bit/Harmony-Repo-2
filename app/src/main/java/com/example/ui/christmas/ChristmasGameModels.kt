package com.example.ui.christmas

import androidx.compose.ui.graphics.Color

enum class ChristmasPlayMode { TOGETHER, ROUND_ROBIN }
enum class ChristmasRoundKind { VISUAL_GRID, TOURNAMENT, REVEAL, SPOTLIGHT }
enum class ChristmasSymbol { CANDLE, GIFT, FILM_MUSIC, STAR }

data class ChristmasOption(val label: String, val emoji: String)

data class ChristmasRound(
    val id: String,
    val title: String,
    val prompt: String,
    val kind: ChristmasRoundKind,
    val options: List<ChristmasOption>,
)

data class ChristmasPart(
    val number: Int,
    val title: String,
    val subtitle: String,
    val symbol: ChristmasSymbol,
    val accent: Color,
    val secondaryAccent: Color,
    val rounds: List<ChristmasRound>,
)

private fun option(label: String, emoji: String) = ChristmasOption(label, emoji)
private fun round(
    id: String,
    title: String,
    prompt: String,
    kind: ChristmasRoundKind = ChristmasRoundKind.VISUAL_GRID,
    vararg options: ChristmasOption,
) = ChristmasRound(id, title, prompt, kind, options.toList())

object ChristmasGameDefinition {
    val parts: List<ChristmasPart> = listOf(
        ChristmasPart(
            number = 1,
            title = "Lichter & Erinnerungen",
            subtitle = "Traditionen, Wärme und Wintermomente",
            symbol = ChristmasSymbol.CANDLE,
            accent = Color(0xFFFF67C7),
            secondaryAccent = Color(0xFFB95CFF),
            rounds = listOf(
                round("p1_01", "Euer Weihnachtsbaum", "Welcher Baum fühlt sich sofort nach eurem Weihnachten an?", options = arrayOf(option("Klassisch rot-gold", "🎄"), option("Winterweiß", "❄️"), option("Bunt & verspielt", "✨"), option("Natürlich & ruhig", "🌲"))),
                round("p1_02", "Der erste Weihnachtsmoment", "Womit beginnt Weihnachten für euch wirklich?", ChristmasRoundKind.SPOTLIGHT, option("Lichter einschalten", "🕯️"), option("Lieblingslied starten", "🎶"), option("Plätzchenduft", "🍪"), option("Alle kommen an", "🚪")),
                round("p1_03", "Lichterzauber", "Welche Beleuchtung soll den Raum verwandeln?", options = arrayOf(option("Warme Lichterkette", "💡"), option("Leuchtende Sterne", "⭐"), option("Kerzenmeer", "🕯️"), option("Sanftes Farbenspiel", "🌌"))),
                round("p1_04", "Duft der Weihnacht", "Welcher Duft gewinnt euren Winterabend?", ChristmasRoundKind.TOURNAMENT, option("Zimt & Orange", "🍊"), option("Tannengrün", "🌲"), option("Vanille", "🌼"), option("Bratapfel", "🍎")),
                round("p1_05", "Plätzchenteller", "Welches Gebäck verschwindet zuerst?", options = arrayOf(option("Zimtsterne", "⭐"), option("Vanillekipferl", "🌙"), option("Lebkuchen", "🍪"), option("Butterplätzchen", "🎀"))),
                round("p1_06", "Festliche Tafel", "Welcher Stil gehört auf euren Weihnachtstisch?", options = arrayOf(option("Gold & Kerzenschein", "🕯️"), option("Rot & Tannengrün", "🎄"), option("Weiß & Kristall", "❄️"), option("Bunt & familiär", "🎉"))),
                round("p1_07", "Winterort", "Wo wäre eure perfekte Weihnachtskulisse?", ChristmasRoundKind.REVEAL, option("Verschneite Hütte", "🏡"), option("Lichterstadt", "🌃"), option("Kaminzimmer", "🔥"), option("Wintergarten", "🌨️")),
                round("p1_08", "Adventsritual", "Welches Ritual würdet ihr jedes Jahr wiederholen?", options = arrayOf(option("Gemeinsam schmücken", "🎄"), option("Plätzchen backen", "🥣"), option("Weihnachtsmarkt", "🎠"), option("Filmabend", "🎬"))),
                round("p1_09", "Schneetag", "Was macht einen freien Schneetag perfekt?", options = arrayOf(option("Spaziergang", "🥾"), option("Schneefiguren", "⛄"), option("Deckenhöhle", "🛋️"), option("Heiße Getränke", "☕"))),
                round("p1_10", "Weihnachtsmorgen", "Welche Stimmung soll am Morgen im Raum liegen?", ChristmasRoundKind.SPOTLIGHT, option("Leise & magisch", "🌟"), option("Fröhlich & laut", "🔔"), option("Gemütlich & langsam", "🧦"), option("Neugierig & wild", "🎁")),
                round("p1_11", "Lieblingsschmuck", "Welches Ornament bekommt den Ehrenplatz?", options = arrayOf(option("Glasstern", "⭐"), option("Kleine Glocke", "🔔"), option("Holzfigur", "🪵"), option("Erinnerungsanhänger", "💝"))),
                round("p1_12", "Wintergetränk", "Was wärmt eure Hände beim Erzählen?", ChristmasRoundKind.TOURNAMENT, option("Kakao", "☕"), option("Kinderpunsch", "🍎"), option("Gewürztee", "🫖"), option("Glühwein", "🍷")),
                round("p1_13", "Zeitkapsel", "Welchen Moment würdet ihr für später bewahren?", ChristmasRoundKind.REVEAL, option("Das gemeinsame Essen", "🍽️"), option("Das größte Lachen", "😄"), option("Die Bescherung", "🎁"), option("Die stille Minute", "🕯️")),
                round("p1_14", "Weihnachtsfarben", "Welche Farbstimmung soll Teil 1 abschließen?", options = arrayOf(option("Rubin & Gold", "🔴"), option("Tanne & Kupfer", "🟢"), option("Eisblau & Silber", "🔵"), option("Harmony-Lila & Rosa", "🟣"))),
                round("p1_15", "Euer perfekter Abend", "Welche Szene gewinnt das große Finale?", ChristmasRoundKind.TOURNAMENT, option("Kamin & Geschichten", "🔥"), option("Musik & Spiele", "🎶"), option("Film & Snacks", "🎬"), option("Lichterspaziergang", "✨")),
            ),
        ),
        ChristmasPart(
            number = 2,
            title = "Wünsche & Geschenke",
            subtitle = "Überraschungen, Ideen und Herzenswünsche",
            symbol = ChristmasSymbol.GIFT,
            accent = Color(0xFFB96CFF),
            secondaryAccent = Color(0xFF6E7BFF),
            rounds = listOf(
                round("p2_01", "Geschenkpapier", "Welcher Look macht schon vor dem Öffnen Freude?", options = arrayOf(option("Dunkelblau & Sterne", "🌌"), option("Kraftpapier & Zweig", "🌿"), option("Rot & Gold", "🎀"), option("Bunt & verspielt", "🎉"))),
                round("p2_02", "Die beste Überraschung", "Welche Art Geschenk bleibt am längsten im Herzen?", ChristmasRoundKind.TOURNAMENT, option("Gemeinsames Erlebnis", "🎟️"), option("Etwas Selbstgemachtes", "🧶"), option("Langer Wunsch", "💫"), option("Persönliche Erinnerung", "📖")),
                round("p2_03", "Kleine Freude", "Was passt perfekt in einen Weihnachtsstrumpf?", options = arrayOf(option("Lieblingssüßigkeit", "🍫"), option("Mini-Spiel", "🎲"), option("Witziger Gutschein", "🎫"), option("Kleine Pflegeauszeit", "🫧"))),
                round("p2_04", "Geheimes Geschenk", "Wie sollte Wichteln bei euch aussehen?", ChristmasRoundKind.REVEAL, option("Lustig", "😂"), option("Nützlich", "🧰"), option("Handgemacht", "✂️"), option("Komplett überraschend", "❓")),
                round("p2_05", "Auspackmoment", "Wie öffnet ihr Geschenke am liebsten?", options = arrayOf(option("Alle nacheinander", "1️⃣"), option("Alle gleichzeitig", "🎊"), option("Erst die Kleinen", "🧦"), option("Mit Rätseln davor", "🧩"))),
                round("p2_06", "Erlebnis schenken", "Welches Erlebnis würdet ihr sofort einlösen?", ChristmasRoundKind.TOURNAMENT, option("Winter-Wochenende", "🏔️"), option("Konzertabend", "🎵"), option("Gemeinsames Dinner", "🍽️"), option("Spa-Tag", "♨️")),
                round("p2_07", "Selbstgemacht", "Was wäre das schönste handgemachte Geschenk?", options = arrayOf(option("Fotobuch", "📔"), option("Gebackene Box", "🍪"), option("Briefsammlung", "💌"), option("Eigene Dekoration", "🎨"))),
                round("p2_08", "Geschenkversteck", "Wo würde niemand zuerst suchen?", ChristmasRoundKind.SPOTLIGHT, option("Im leeren Koffer", "🧳"), option("Hinter den Büchern", "📚"), option("In der Küche", "🥣"), option("Direkt unterm Baum", "🎄")),
                round("p2_09", "Schleifen-Finale", "Welche Schleife krönt die schönste Box?", options = arrayOf(option("Große Samtschleife", "🎀"), option("Dünnes Goldband", "✨"), option("Schnur mit Anhänger", "🏷️"), option("Papierstern", "⭐"))),
                round("p2_10", "Wunschzettel", "Was gehört ganz oben auf euren gemeinsamen Wunschzettel?", ChristmasRoundKind.REVEAL, option("Mehr gemeinsame Zeit", "⏳"), option("Eine Reise", "✈️"), option("Ein neues Ritual", "🕯️"), option("Ein großes Projekt", "🏡")),
                round("p2_11", "Geschenkregel", "Welche Regel macht die Bescherung besser?", options = arrayOf(option("Nur ein Herzensgeschenk", "❤️"), option("Festes Budget", "💶"), option("Nur Erlebnisse", "🎟️"), option("Keine Regeln", "✨"))),
                round("p2_12", "Für die ganze Familie", "Welches gemeinsame Geschenk bringt alle zusammen?", ChristmasRoundKind.TOURNAMENT, option("Großes Brettspiel", "🎲"), option("Familienausflug", "🚌"), option("Karaoke-Set", "🎤"), option("Projektor für Filme", "📽️")),
                round("p2_13", "Die Reaktion", "Welche Reaktion ist beim Auspacken unbezahlbar?", options = arrayOf(option("Sprachloses Staunen", "😮"), option("Lautes Lachen", "😂"), option("Sofortige Umarmung", "🤗"), option("Freudentanz", "💃"))),
                round("p2_14", "Letzte-Minute-Rettung", "Was rettet ein vergessenes Geschenk am besten?", ChristmasRoundKind.SPOTLIGHT, option("Persönlicher Gutschein", "🎫"), option("Gemeinsamer Ausflug", "🚗"), option("Lieblingsessen", "🍝"), option("Ehrlicher Brief", "💌")),
                round("p2_15", "Geschenk des Jahres", "Welche Geschenkidee gewinnt Teil 2?", ChristmasRoundKind.TOURNAMENT, option("Zeit füreinander", "⏳"), option("Großer Wunsch", "🌠"), option("Eigene Kreation", "🎨"), option("Überraschungsreise", "🎒")),
            ),
        ),
        ChristmasPart(
            number = 3,
            title = "Filme & Weihnachtsklänge",
            subtitle = "Klassiker, Songs und gemütliches Popcorn",
            symbol = ChristmasSymbol.FILM_MUSIC,
            accent = Color(0xFF78E8FF),
            secondaryAccent = Color(0xFF8A8CFF),
            rounds = listOf(
                round("p3_01", "Filmklassiker", "Welcher Film eröffnet euren Weihnachtsmarathon?", ChristmasRoundKind.TOURNAMENT, option("Kevin – Allein zu Haus", "🏠"), option("Harry Potter", "⚡"), option("Der Grinch", "💚"), option("Buddy – Der Weihnachtself", "🧝")),
                round("p3_02", "Magischer Winter", "Welche Filmwelt fühlt sich im Dezember am magischsten an?", options = arrayOf(option("Hogwarts im Schnee", "🏰"), option("Verschneites New York", "🗽"), option("Nordpol-Werkstatt", "🎁"), option("Verzauberter Wald", "🌲"))),
                round("p3_03", "Soundtrack-Start", "Welcher Welthit muss zuerst laufen?", ChristmasRoundKind.TOURNAMENT, option("Last Christmas", "💔"), option("All I Want for Christmas Is You", "🔔"), option("Feliz Navidad", "🎉"), option("Jingle Bell Rock", "🎸")),
                round("p3_04", "Film-Snack", "Was steht neben der Fernbedienung?", options = arrayOf(option("Popcorn", "🍿"), option("Plätzchen", "🍪"), option("Nachos", "🧀"), option("Schokolade", "🍫"))),
                round("p3_05", "Weihnachtsstimme", "Welche Klangfarbe gehört für euch zu Weihnachten?", ChristmasRoundKind.REVEAL, option("Großer Chor", "🎼"), option("Sanftes Klavier", "🎹"), option("Streicher & Glocken", "🎻"), option("Warme Jazzband", "🎷")),
                round("p3_06", "Komödie oder Gefühl", "Welche Filmstimmung gewinnt heute?", options = arrayOf(option("Laut lachen", "😂"), option("Herzerwärmend", "❤️"), option("Magisch", "✨"), option("Abenteuerlich", "🗺️"))),
                round("p3_07", "Sofa-Setup", "Welcher Platz ist für den Filmabend perfekt?", options = arrayOf(option("Deckenburg", "🛋️"), option("Matratzenlager", "🛏️"), option("Kinosessel", "🎟️"), option("Kissen am Kamin", "🔥"))),
                round("p3_08", "Song-Duell", "Welcher Klassiker bleibt im Finale übrig?", ChristmasRoundKind.TOURNAMENT, option("White Christmas", "❄️"), option("Driving Home for Christmas", "🚗"), option("Rockin’ Around the Christmas Tree", "🎄"), option("Wonderful Christmastime", "✨")),
                round("p3_09", "Mitsingen", "Bei welchem Song-Typ singt wirklich jeder mit?", ChristmasRoundKind.SPOTLIGHT, option("Pop-Klassiker", "🎤"), option("Traditionelles Lied", "🔔"), option("Kinderlied", "🧸"), option("Rockige Weihnacht", "🎸")),
                round("p3_10", "Filmfigur", "Wer bringt die beste Weihnachtsenergie mit?", options = arrayOf(option("Cleveres Kind", "🧠"), option("Chaotischer Elf", "🧝"), option("Mürrischer Verwandter", "😤"), option("Magische Begleitung", "🪄"))),
                round("p3_11", "Filmkulisse", "Wo sollte eure eigene Weihnachtsszene spielen?", ChristmasRoundKind.REVEAL, option("Altes Schloss", "🏰"), option("Leuchtende Großstadt", "🌃"), option("Kleine Berghütte", "🏔️"), option("Festliches Familienhaus", "🏡")),
                round("p3_12", "Intro-Melodie", "Welches Instrument eröffnet euren Familienfilm?", options = arrayOf(option("Klavier", "🎹"), option("Glockenspiel", "🔔"), option("Streicher", "🎻"), option("Bläser", "🎺"))),
                round("p3_13", "Abspann", "Was passiert nach dem letzten Film?", options = arrayOf(option("Noch einen starten", "▶️"), option("Songs weiterhören", "🎶"), option("Über Szenen reden", "💬"), option("Direkt einschlafen", "😴"))),
                round("p3_14", "Klassiker-Mix", "Welche Kombination gewinnt euren Marathon?", ChristmasRoundKind.TOURNAMENT, option("Harry Potter & Kakao", "⚡"), option("Kevin & Popcorn", "🏠"), option("Grinch & Plätzchen", "💚"), option("Elf & Punsch", "🧝")),
                round("p3_15", "Euer Weihnachtsthema", "Welche Musikrichtung begleitet den Rest des Abends?", ChristmasRoundKind.TOURNAMENT, option("Episch-orchestral", "🎻"), option("Barock & festlich", "🎼"), option("Pop-Klassiker", "🎤"), option("Ruhiges Klavier", "🎹")),
            ),
        ),
        ChristmasPart(
            number = 4,
            title = "Magie & Familienzauber",
            subtitle = "Spiele, Wünsche und das große Finale",
            symbol = ChristmasSymbol.STAR,
            accent = Color(0xFFFFD978),
            secondaryAccent = Color(0xFFFF8C6B),
            rounds = listOf(
                round("p4_01", "Familienwerkstatt", "Welche Station eröffnet eure Weihnachtswerkstatt?", options = arrayOf(option("Karten gestalten", "✂️"), option("Plätzchen verzieren", "🍪"), option("Ornamente bauen", "⭐"), option("Geschenke verpacken", "🎁"))),
                round("p4_02", "Spieleabend", "Welches Spiel bringt alle sofort an den Tisch?", ChristmasRoundKind.TOURNAMENT, option("Schnelles Kartenspiel", "🃏"), option("Großes Brettspiel", "🎲"), option("Pantomime", "🎭"), option("Musik-Quiz", "🎵")),
                round("p4_03", "Weihnachtsmission", "Welche geheime Mission wäre am lustigsten?", ChristmasRoundKind.REVEAL, option("Geschenk unbemerkt tauschen", "🎁"), option("Drei Glocken verstecken", "🔔"), option("Ein Liedwort einschmuggeln", "🎶"), option("Jemanden zum Lachen bringen", "😂")),
                round("p4_04", "Sternenwunsch", "Welchen gemeinsamen Wunsch schickt ihr nach oben?", options = arrayOf(option("Gesundheit", "❤️"), option("Mehr gemeinsame Zeit", "⏳"), option("Ein Abenteuer", "🗺️"), option("Frieden & Ruhe", "🕊️"))),
                round("p4_05", "Familientalent", "Welches Talent gewinnt eure Weihnachtsshow?", ChristmasRoundKind.SPOTLIGHT, option("Singen", "🎤"), option("Geschichten erzählen", "📖"), option("Zaubertrick", "🪄"), option("Witze", "😂")),
                round("p4_06", "Winter-Challenge", "Welche Herausforderung nehmt ihr gemeinsam an?", options = arrayOf(option("Schneemann-Wettbewerb", "⛄"), option("Plätzchen-Duell", "🍪"), option("Lichter-Fotojagd", "📸"), option("Geschenk-Rätsel", "🧩"))),
                round("p4_07", "Dankbarkeitskarten", "Welche Karte würdet ihr zuerst aufdecken?", ChristmasRoundKind.REVEAL, option("Unser lustigster Moment", "😂"), option("Was ich an euch liebe", "❤️"), option("Was wir geschafft haben", "🏆"), option("Worauf ich mich freue", "🌠")),
                round("p4_08", "Mitternachtsmoment", "Wie soll der Abend seinen Höhepunkt erreichen?", options = arrayOf(option("Gemeinsamer Toast", "🥂"), option("Lichter draußen", "✨"), option("Lieblingssong", "🎶"), option("Runde Umarmungen", "🤗"))),
                round("p4_09", "Weihnachtskrone", "Welches Symbol wird euer Familienzeichen?", ChristmasRoundKind.TOURNAMENT, option("Leuchtender Stern", "⭐"), option("Goldene Glocke", "🔔"), option("Magische Schneeflocke", "❄️"), option("Kleine Geschenkbox", "🎁")),
                round("p4_10", "Geschichtenfunke", "Womit beginnt eure spontane Weihnachtsgeschichte?", ChristmasRoundKind.SPOTLIGHT, option("Ein verschwundener Stern", "⭐"), option("Eine sprechende Glocke", "🔔"), option("Ein geheimes Geschenk", "🎁"), option("Eine Tür im Schnee", "🚪")),
                round("p4_11", "Familienpreis", "Welche Auszeichnung sollte heute vergeben werden?", options = arrayOf(option("Bestes Lachen", "😂"), option("Größter Weihnachtsfan", "🎄"), option("Beste Überraschung", "🎁"), option("Gemütlichster Mensch", "🧦"))),
                round("p4_12", "Nächstes Weihnachten", "Was möchtet ihr nächstes Jahr unbedingt wiederholen?", ChristmasRoundKind.REVEAL, option("Dieses Spiel", "🎲"), option("Ein neues Reiseziel", "🧳"), option("Die gemeinsame Tafel", "🍽️"), option("Ein eigenes Ritual", "🕯️")),
                round("p4_13", "Magischer Gegenstand", "Welcher Gegenstand dürfte für eine Nacht lebendig werden?", options = arrayOf(option("Schneekugel", "🔮"), option("Spielzeugeisenbahn", "🚂"), option("Nussknacker", "🪵"), option("Sternenspitze", "⭐"))),
                round("p4_14", "Finale Überraschung", "Was liegt in der letzten leuchtenden Box?", ChristmasRoundKind.REVEAL, option("Ein gemeinsamer Wunsch", "🌠"), option("Ein Familienabenteuer", "🗺️"), option("Eine neue Tradition", "🕯️"), option("Zeit füreinander", "⏳")),
                round("p4_15", "Unser Weihnachten", "Welche Kraft beschreibt eure Familie heute am besten?", ChristmasRoundKind.TOURNAMENT, option("Wärme", "🔥"), option("Humor", "✨"), option("Zusammenhalt", "🤝"), option("Magie", "⭐")),
            ),
        ),
    )

    val allRounds: List<ChristmasRound> = parts.flatMap { it.rounds }

    init {
        check(parts.size == 4)
        check(parts.all { it.rounds.size == 15 })
        check(allRounds.size == 60)
        check(allRounds.map { it.id }.distinct().size == 60)
        check(allRounds.all { it.options.size == 4 })
    }
}
