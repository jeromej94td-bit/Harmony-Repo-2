package com.example.data.model

/** Additional non-image packs that extend Harmony without touching existing visual Das-oder-Das content. */
object HarmonyExpansionPacks {
    val PACKS: List<QuestionPack> = listOf(
        QuestionPack(
            id = "alltagsgeheimnisse",
            title = "Kleine Alltagsgeständnisse",
            tags = listOf("alltag", "humor", "kennenlernen"),
            cat = "reden",
            topic = "kennen",
            type = "quiz",
            emoji = "🤭",
            questions = listOf(
                Question("Was machst du heimlich öfter, als du zugeben möchtest?", listOf("Snacks verstecken", "Noch eine Folge schauen", "Online shoppen", "Gar nichts davon")),
                Question("Welche kleine Angewohnheit von dir würde in einer Realityshow sofort auffallen?", listOf("Ich rede mit mir selbst", "Ich verliere ständig Dinge", "Ich plane alles", "Ich improvisiere alles")),
                Question("Wobei schiebst du Dinge am häufigsten bis zur letzten Minute auf?", listOf("Haushalt", "Nachrichten beantworten", "Termine organisieren", "Papierkram")),
                Question("Was wäre dein typischer Mitternachts-Snack?", listOf("Süß", "Salzig", "Reste vom Abendessen", "Ich esse nachts nichts")),
                Question("Welche Kleinigkeit kann deine Stimmung sofort verbessern?", listOf("Essen", "Eine Umarmung", "Musik", "Ruhe")),
                Question("Was würdest du eher vergessen, wenn niemand dich erinnert?", listOf("Geburtstage", "Einkauf", "Ladegerät", "Termine")),
                Question("Was machst du zu Hause, wenn du sicher bist, dass niemand zusieht?", listOf("Tanzen", "Laut mitsingen", "Komische Stimmen", "Auf dem Sofa versacken")),
                Question("Welche Aufgabe im Haushalt würdest du am liebsten für immer abgeben?", listOf("Bad putzen", "Wäsche", "Geschirr", "Aufräumen"))
            )
        ),
        QuestionPack(
            id = "zukunftzuzweit",
            title = "Zukunft zu zweit",
            tags = listOf("zukunft", "träume", "beziehung"),
            cat = "tief",
            topic = "beziehung",
            type = "quiz",
            emoji = "🔭",
            questions = listOf(
                Question("Wie sieht ein richtig guter gemeinsamer Sonntag in fünf Jahren aus?", listOf("Langsam zu Hause", "Unterwegs und aktiv", "Mit Familie oder Freunden", "Ganz spontan")),
                Question("Was wäre ein gemeinsames Projekt, auf das du wirklich stolz wärst?", listOf("Ein Zuhause gestalten", "Eine große Reise", "Etwas Eigenes aufbauen", "Ein soziales Projekt")),
                Question("Wie wichtig ist dir, später in der Nähe von Familie zu wohnen?", listOf("Sehr wichtig", "Schön, aber kein Muss", "Eher unwichtig", "Kommt auf die Lebensphase an")),
                Question("Welche Art von Zuhause passt am ehesten zu euch?", listOf("Wohnung mitten in der Stadt", "Haus mit Garten", "Etwas Kleines und flexibles", "Mehrere Orte im Wechsel")),
                Question("Was möchtest du im Alter über eure Beziehung sagen können?", listOf("Wir waren ein Team", "Wir haben viel erlebt", "Wir haben uns nie verloren", "Wir haben uns immer weiterentwickelt")),
                Question("Wie plant ihr große Ziele am liebsten?", listOf("Mit konkretem Plan", "Mit grober Richtung", "Einer plant, einer ergänzt", "Wir lassen vieles entstehen")),
                Question("Was sollte in eurem zukünftigen Alltag auf keinen Fall verloren gehen?", listOf("Humor", "Dates", "Eigene Freiräume", "Gemeinsame Rituale")),
                Question("Welche Erinnerung würdest du in zehn Jahren am liebsten gemeinsam geschaffen haben?", listOf("Eine besondere Reise", "Ein großes Lebensziel", "Viele kleine Alltagsmomente", "Etwas völlig Unerwartetes"))
            )
        ),
        QuestionPack(
            id = "reisegefuehle",
            title = "Reisegefühle",
            tags = listOf("reisen", "abenteuer", "paar"),
            cat = "reden",
            topic = "reisen",
            type = "quiz",
            emoji = "🧳",
            questions = listOf(
                Question("Was ist dir auf einer gemeinsamen Reise wichtiger?", listOf("Viel sehen", "Gut essen", "Entspannen", "Einfach zusammen sein")),
                Question("Wie viel Planung brauchst du vor einer Reise?", listOf("Jeden Tag geplant", "Nur die wichtigsten Punkte", "Fast gar nichts", "Mein Partner darf planen")),
                Question("Was wäre für dich der schönste spontane Reise-Moment?", listOf("Unbekanntes Restaurant entdecken", "Sonnenuntergang finden", "Mit Einheimischen ins Gespräch kommen", "Plan komplett ändern")),
                Question("Welcher Reisestress bringt dich am ehesten aus der Ruhe?", listOf("Verspätungen", "Hunger", "Orientierung verlieren", "Zu wenig Schlaf")),
                Question("Was gehört für dich in jedes Paar-Reisefoto?", listOf("Wir beide", "Der Ort", "Etwas Lustiges", "Fotos sind mir nicht so wichtig")),
                Question("Welche Unterkunft macht Urlaub für dich besonders?", listOf("Schönes Hotel", "Kleine Ferienwohnung", "Außergewöhnlicher Ort", "Hauptsache gute Lage")),
                Question("Was würdest du lieber gemeinsam lernen?", listOf("Eine neue Sprache", "Kochen vor Ort", "Tanzen", "Eine Outdoor-Aktivität")),
                Question("Wann fühlt sich eine Reise für dich gelungen an?", listOf("Wenn alles klappt", "Wenn wir viel erlebt haben", "Wenn wir uns näher fühlen", "Wenn es eine gute Geschichte danach gibt"))
            )
        ),
        QuestionPack(
            id = "essensrituale",
            title = "Essen, Rituale & Genuss",
            tags = listOf("essen", "alltag", "genuss"),
            cat = "reden",
            topic = "essen",
            type = "quiz",
            emoji = "🍜",
            questions = listOf(
                Question("Welches Essen könnte bei euch zu einem festen Paar-Ritual werden?", listOf("Frühstück", "Pizza-Abend", "Sushi", "Gemeinsam kochen")),
                Question("Was ist dir wichtiger, wenn ihr essen geht?", listOf("Geschmack", "Atmosphäre", "Preis-Leistung", "Etwas Neues entdecken")),
                Question("Wie teilt ihr Essen am liebsten?", listOf("Jeder sein eigenes", "Alles in die Mitte", "Nur probieren", "Kommt aufs Essen an")),
                Question("Welches Küchen-Chaos könntest du deinem Partner am ehesten verzeihen?", listOf("Zu viel Salz", "Angebrannt", "Überall Geschirr", "Falsche Zutaten")),
                Question("Was wäre euer perfektes Frühstück zu zweit?", listOf("Großer Brunch", "Kaffee und Gebäck", "Herzhaft", "Etwas ganz Schnelles")),
                Question("Womit kann man dich kulinarisch am leichtesten überraschen?", listOf("Dessert", "Street Food", "Fine Dining", "Selbstgekochtes")),
                Question("Wie wichtig ist dir, neue Gerichte gemeinsam auszuprobieren?", listOf("Sehr wichtig", "Ab und zu", "Nur wenn es vertraut klingt", "Ich bleibe gern bei Favoriten")),
                Question("Welche Rolle spielt Essen für romantische Momente?", listOf("Eine große", "Eine nette Ergänzung", "Fast keine", "Kommt auf den Anlass an"))
            )
        ),
        QuestionPack(
            id = "moralischegrauzonen",
            title = "Moralische Grauzonen",
            tags = listOf("werte", "diskussion", "moral"),
            cat = "zust",
            topic = "moral",
            type = "quiz",
            emoji = "⚖️",
            questions = listOf(
                Question("Ein Freund bittet dich, für ihn zu lügen. Wie reagierst du?", listOf("Ich helfe ihm", "Nur bei einer Kleinigkeit", "Ich lehne ab", "Ich will erst alles wissen")),
                Question("Du findest eine Geldbörse ohne Ausweis. Was tust du?", listOf("Fundbüro", "Online nach Besitzer suchen", "Am Fundort abgeben", "Kommt auf den Inhalt an")),
                Question("Ist eine gut gemeinte Lüge manchmal besser als die Wahrheit?", listOf("Ja", "Nur selten", "Eher nicht", "Nein")),
                Question("Sollte man einen Freund konfrontieren, wenn sein Partner offensichtlich flirtet?", listOf("Sofort", "Nur wenn es eindeutig ist", "Erst mit dem flirtenden Partner reden", "Ich mische mich nicht ein")),
                Question("Darf man eine Überraschung ruinieren, wenn man weiß, dass sie der Person nicht gefallen wird?", listOf("Ja", "Nur bei größeren Folgen", "Nein", "Ich würde Hinweise geben")),
                Question("Ist es okay, bei einem Geschenk nach dem Preis zu fragen?", listOf("Ja", "Nur bei engen Menschen", "Eher nicht", "Nie")),
                Question("Wenn ein Restaurant eure Rechnung zu niedrig berechnet, was macht ihr?", listOf("Sofort melden", "Nur bei großem Fehler melden", "Nichts sagen", "Kommt auf das Restaurant an")),
                Question("Wie wichtig ist dir, dass Partner dieselben Grundwerte teilen?", listOf("Unverzichtbar", "Bei den wichtigsten Themen", "Unterschiede sind okay", "Das zeigt sich erst mit der Zeit"))
            )
        ),
        QuestionPack(
            id = "filmabendentscheidung",
            title = "Der perfekte Filmabend",
            tags = listOf("filme", "alltag", "spaß"),
            cat = "wer",
            topic = "filme_serien",
            type = "quiz",
            emoji = "🎬",
            questions = listOf(
                Question("Was entscheidet bei euch zuerst über einen Film?", listOf("Genre", "Trailer", "Bewertung", "Wer mitspielt")),
                Question("Was darf beim Filmabend niemals fehlen?", listOf("Popcorn", "Decke", "Getränke", "Handys außer Reichweite")),
                Question("Wie reagierst du, wenn der andere während des Films einschläft?", listOf("Weitergucken", "Film pausieren", "Ihn wecken", "Selbst schlafen")),
                Question("Welche Art Film sorgt am ehesten für Gesprächsstoff danach?", listOf("Drama", "Thriller", "Dokumentation", "Science-Fiction")),
                Question("Wie viele Folgen einer Serie sind an einem Abend noch vernünftig?", listOf("Eine", "Zwei bis drei", "Bis wir müde sind", "Die ganze Staffel")),
                Question("Was ist schlimmer?", listOf("Spoiler", "Ständig aufs Handy schauen", "Laut kommentieren", "Den Film nach zehn Minuten abbrechen")),
                Question("Wer sollte bei Uneinigkeit entscheiden?", listOf("Abwechseln", "Wer zuletzt nachgegeben hat", "Zufall", "Wir suchen etwas Drittes")),
                Question("Welches Ende magst du lieber?", listOf("Happy End", "Überraschend", "Offen", "Bittersüß"))
            )
        )
    )
}
