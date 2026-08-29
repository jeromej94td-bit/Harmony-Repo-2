package com.example.data

/** Explicit Stage 05.2 curation for Harmony-360 Section 14 — Kultur & Medien. */
object Harmony360CultureMediaSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_291_musikgeschmack_entweder_oder" to CurationDecision.REWRITE,
        "h500_292_streaming_wer_eher" to CurationDecision.REWRITE,
        "h500_293_social_media_skala" to CurationDecision.REWRITE,
        "h500_294_museen_ranking" to CurationDecision.REWRITE,
        "h500_295_konzerte_prognose" to CurationDecision.REWRITE,
        "h500_296_buecher_szenario" to CurationDecision.REWRITE,
        "h500_297_kino_geheime_wahl" to CurationDecision.REWRITE,
        "h500_298_kindheitsmedien_memory" to CurationDecision.REWRITE,
        "h500_299_informationsquellen_prioritaet" to CurationDecision.REWRITE,
        "h500_300_kulturelle_identitaet_offene_runde" to CurationDecision.REWRITE,
        "h500_301_podcasts_entweder_oder" to CurationDecision.REWRITE,
        "h500_302_gaming_wer_eher" to CurationDecision.REWRITE,
        "h500_303_nachrichten_skala" to CurationDecision.REWRITE,
        "h500_304_theater_ranking" to CurationDecision.REWRITE,
        "h500_305_festivals_prognose" to CurationDecision.REWRITE,
        "h500_306_dokumentationen_szenario" to CurationDecision.REWRITE,
        "h500_307_kunst_geheime_wahl" to CurationDecision.REWRITE,
        "h500_310_medienkonsum_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_291_musikgeschmack_entweder_oder" to listOf(
            q("Wie hörst du einen Lieblingssong lieber?", "Studioaufnahme", "Live-Version"),
            q("Was zieht dich eher an?", "Starke Stimme", "Starker Beat"),
            q("Was gewinnt bei dir eher?", "Bekannte Lieblingsmusik", "Neue Künstler entdecken"),
            q("Wie hörst du lieber bewusst Musik?", "Ganzes Album", "Eigene Playlist"),
            q("Was spricht dich eher an?", "Text und Bedeutung", "Klang und Atmosphäre"),
            q("Bei völlig unterschiedlichem Musikgeschmack?", "Abwechselnd auswählen", "Gemeinsame Schnittmenge suchen")
        ),
        "h500_292_streaming_wer_eher" to listOf(
            whoQ("Wer startet eher noch eine neue Serie, obwohl schon mehrere angefangen sind?"),
            whoQ("Wer sagt eher 'nur eine Folge' und schaut dann drei?"),
            whoQ("Wer findet eher eine unbekannte Serie, die am Ende beide feiern?"),
            whoQ("Wer bricht eher eine Serie ab, wenn sie nach zwei Folgen nicht packt?"),
            whoQ("Wer liest eher vorher Bewertungen oder Zusammenfassungen?"),
            whoQ("Wer erinnert sich eher Monate später noch an Figuren, Namen und Details?")
        ),
        "h500_293_social_media_skala" to listOf(
            scaleQ("Wie wichtig ist Social Media für dich, um mit Menschen oder Themen verbunden zu bleiben?"),
            scaleQ("Wie leicht könntest du mehrere Tage komplett ohne Social Media auskommen?"),
            scaleQ("Wie stark beeinflusst Social Media, welche Orte, Produkte oder Trends du interessant findest?"),
            scaleQ("Wie wohl fühlst du dich damit, private Paarmomente öffentlich zu teilen?"),
            scaleQ("Wie häufig vergleichst du dich beim Scrollen unbewusst mit anderen?"),
            scaleQ("Wie wichtig ist dir, bewusst Grenzen für deine tägliche Social-Media-Zeit zu setzen?")
        ),
        "h500_294_museen_ranking" to listOf(
            q("Welche Museumsart würde dich am ehesten hineinziehen? Ordne.", "Kunstmuseum", "Geschichte", "Natur & Wissenschaft", "Design & Technik"),
            q("Was macht ein Museum für dich stark? Ordne.", "Originale sehen", "Geschichten verstehen", "Interaktive Elemente", "Besondere Architektur"),
            q("Wie möchtest du eine Ausstellung erleben? Ordne.", "Frei herumgehen", "Audioguide", "Geführte Tour", "Nur Highlights ansehen"),
            q("Was dürfte eine Ausstellung am wenigsten sein? Ordne vom größten Störfaktor.", "Überfüllt", "Zu textlastig", "Zu oberflächlich", "Schlecht inszeniert"),
            q("Was bleibt dir nach einem guten Museumsbesuch am ehesten? Ordne.", "Ein einzelnes Werk", "Eine neue Idee", "Eine starke Geschichte", "Die gemeinsame Erinnerung"),
            q("Wofür würdest du extra Zeit einplanen? Ordne.", "Sonderausstellung", "Lieblingskünstler", "Historisches Original", "Interaktive Ausstellung")
        ),
        "h500_295_konzerte_prognose" to listOf(
            q("Welche Art Konzert würde dein Partner wegen der Musik vermutlich zuerst wählen?", "Lieblingsartist", "Orchester oder Filmmusik", "Kleine neue Band", "Großes Pop- oder Rockkonzert"),
            q("Was ist deinem Partner beim Live-Erlebnis wahrscheinlich wichtiger?", "Stimme", "Instrumente", "Bühnenbild", "Publikumsenergie"),
            q("Welche Song-Auswahl würde dein Partner vermutlich bevorzugen?", "Viele bekannte Hits", "Komplettes neues Album", "Seltene ältere Songs", "Überraschender Mix"),
            q("Was würde deinen Partner musikalisch am ehesten enttäuschen?", "Schlechter Sound", "Zu kurze Setlist", "Zu viel Playback", "Lieblingssong fehlt"),
            q("Welche Live-Version würde dein Partner eher feiern?", "Nah am Original", "Komplett neu arrangiert", "Akustisch", "Mit großem Ensemble"),
            q("Was würde dein Partner am nächsten Tag wahrscheinlich zuerst erzählen?", "Bester Song", "Stärkster musikalischer Moment", "Überraschung im Set", "Gesamte Atmosphäre")
        ),
        "h500_296_buecher_szenario" to listOf(
            q("Ihr möchtet dasselbe Buch lesen, aber einer ist viel schneller. Wie macht ihr es?", "Kapitelweise gemeinsam", "Jeder im eigenen Tempo", "Nur an festen Punkten darüber reden", "Der Schnellere wartet"),
            q("Das Buch gefällt einem nach 80 Seiten gar nicht. Was passiert?", "Gemeinsam abbrechen", "Der andere liest weiter", "Noch ein paar Kapitel testen", "Zu einem neuen Buch wechseln"),
            q("Ihr wollt einen gemeinsamen Lesemonat starten. Wer wählt das erste Buch?", "Abwechselnd", "Gemeinsam aus einer Shortlist", "Der seltenere Leser", "Zufällig ziehen"),
            q("Eine Verfilmung erscheint, bevor einer das Buch beendet hat. Was macht ihr?", "Film wartet", "Trotzdem gemeinsam schauen", "Einer schaut schon", "Buch bewusst pausieren"),
            q("Ein empfohlenes Buch behandelt ein Thema, das euch sehr unterschiedlich interessiert. Was ist fair?", "Zusammen ausprobieren", "Nur Interessierter liest", "Kurzes Hörbuch testen", "Anderes gemeinsames Buch suchen"),
            q("Ihr diskutiert das Ende komplett unterschiedlich. Was macht den Abend besser?", "Argumente austauschen", "Interpretationen nebeneinander stehen lassen", "Rezensionen lesen", "Autor-Interview suchen")
        ),
        "h500_297_kino_geheime_wahl" to listOf(
            q("Was ist dein heimlicher Hauptgrund, ins Kino zu gehen?", "Große Leinwand und Sound", "Film ohne Ablenkung", "Gemeinsames Ritual", "Premiere sofort sehen"),
            q("Welchen Platz würdest du heimlich zuerst wählen?", "Mittig weiter hinten", "Ganz hinten", "Nähe Leinwand", "Rand mit mehr Ruhe"),
            q("Welche Vorstellung passt besser zu dir?", "Abends zur Hauptzeit", "Späte Vorstellung", "Nachmittags fast leer", "Premiere mit viel Publikum"),
            q("Was wäre dein Kino-Luxus?", "Premiumsitz", "Großes Soundformat", "Lieblingssnack", "Kleiner Saal mit wenig Menschen"),
            q("Welche Filmauswahl würdest du ohne Kompromiss treffen?", "Großer Blockbuster", "Arthouse", "Horror oder Thriller", "Animation"),
            q("Was machst du nach einem starken Film lieber?", "Sofort darüber reden", "Erst wirken lassen", "Kritiken lesen", "Soundtrack hören")
        ),
        "h500_298_kindheitsmedien_memory" to listOf(
            GenQuestion("Welche Serie oder Sendung aus deiner Kindheit konntest du damals nie verpassen?"),
            GenQuestion("Welcher Film aus deiner Kindheit löst heute sofort wieder ein bestimmtes Gefühl aus?"),
            GenQuestion("Welche Figur aus Buch, Fernsehen oder Spiel war früher dein heimlicher Favorit?"),
            GenQuestion("Welche Musik, Kassette, CD oder Radiosendung erinnert dich sofort an früher?"),
            GenQuestion("Gab es ein Spiel oder Medium, das du ständig mit Geschwistern oder Freunden geteilt hast?"),
            GenQuestion("Welches Kindheitsmedium würdest du deinem Partner heute gern zeigen, obwohl es vielleicht schlecht gealtert ist?")
        ),
        "h500_299_informationsquellen_prioritaet" to listOf(
            q("Was ist dir bei wichtigen Informationen am wichtigsten?", "Originalquelle", "Mehrere unabhängige Quellen", "Fachliche Einordnung", "Schnelle Übersicht"),
            q("Wem vertraust du bei einem komplizierten Thema eher?", "Fachmedium", "Öffentlich-rechtliches Angebot", "Direkte Expertin oder Experte", "Primärdokument"),
            q("Was prüfst du zuerst, bevor du etwas weitergibst?", "Quelle", "Datum", "Autor", "Bestätigung durch andere"),
            q("Was ist bei widersprüchlichen Berichten wichtiger?", "Mehr Kontext suchen", "Originalmaterial prüfen", "Abwarten", "Verschiedene Perspektiven vergleichen"),
            q("Welche Form hilft dir am meisten, ein Thema zu verstehen?", "Ausführlicher Artikel", "Podcast mit Fachleuten", "Video-Erklärung", "Daten und Grafiken"),
            q("Was ist für dich das stärkste Warnsignal?", "Keine Quelle genannt", "Extrem emotionale Überschrift", "Nur anonyme Behauptungen", "Veraltete Daten")
        ),
        "h500_300_kulturelle_identitaet_offene_runde" to listOf(
            GenQuestion("Welche Tradition, Sprache oder Gewohnheit fühlt sich für dich besonders nach Herkunft oder Zuhause an?"),
            GenQuestion("Welche kulturelle Seite von dir verstehen Menschen von außen oft erst spät?"),
            GenQuestion("Welche Tradition aus deiner Familie möchtest du unbedingt bewahren – und welche eher nicht?"),
            GenQuestion("Welche neue Kultur hat dich im Laufe deines Lebens überraschend stark beeinflusst?"),
            GenQuestion("Wie wichtig ist es dir, dass ein Partner deine kulturellen Prägungen aktiv kennenlernt?"),
            GenQuestion("Welche gemeinsame Tradition würdest du gern schaffen, die aus beiden Hintergründen etwas Eigenes macht?")
        ),
        "h500_301_podcasts_entweder_oder" to listOf(
            q("Was hörst du eher?", "True Crime", "Wissen & Wissenschaft"),
            q("Was passt eher zu dir?", "Ein Host erzählt", "Gespräch mit Gästen"),
            q("Welche Länge ist dir lieber?", "Unter 30 Minuten", "Eine Stunde oder länger"),
            q("Wie hörst du Podcasts eher?", "Nebenbei unterwegs", "Bewusst mit voller Aufmerksamkeit"),
            q("Was zieht dich eher rein?", "Eine Serie über viele Folgen", "Jede Folge neues Thema"),
            q("Was machst du eher bei einer starken Folge?", "Direkt weiterempfehlen", "Noch mehr zum Thema recherchieren")
        ),
        "h500_302_gaming_wer_eher" to listOf(
            whoQ("Wer interessiert sich bei einem Spiel eher zuerst für Welt und Geschichte?"),
            whoQ("Wer schaut eher Zwischensequenzen komplett statt sie wegzudrücken?"),
            whoQ("Wer erkennt eher Sprecher, Soundtracks oder Anspielungen aus anderen Medien?"),
            whoQ("Wer würde eher ein Spiel wegen des Art-Designs ausprobieren?"),
            whoQ("Wer liest eher Hintergrundtexte, Codex-Einträge oder Lore?"),
            whoQ("Wer würde eher nach dem Spielen noch Videos oder Analysen zur Geschichte anschauen?")
        ),
        "h500_303_nachrichten_skala" to listOf(
            scaleQ("Wie wichtig ist dir, täglich über aktuelle Nachrichten informiert zu sein?"),
            scaleQ("Wie häufig vergleichst du bei wichtigen Meldungen mehrere Quellen miteinander?"),
            scaleQ("Wie sehr achtest du darauf, Nachricht und Kommentar voneinander zu unterscheiden?"),
            scaleQ("Wie schnell prüfst du eine überraschende Meldung, bevor du sie glaubst oder teilst?"),
            scaleQ("Wie stark beeinflusst dich die Nachrichtenlage emotional im Alltag?"),
            scaleQ("Wie bewusst legst du Zeiten fest, in denen du keine Nachrichten konsumierst?")
        ),
        "h500_304_theater_ranking" to listOf(
            q("Welche Bühnenform reizt dich am meisten? Ordne.", "Schauspiel", "Musical", "Oper", "Tanz"),
            q("Was macht einen Theaterabend für dich besonders? Ordne.", "Starke Darsteller", "Inszenierung", "Musik & Klang", "Bühnenbild"),
            q("Welche Art Inszenierung liegt dir eher? Ordne.", "Klassisch", "Modern", "Experimentell", "Minimalistisch"),
            q("Was dürfte eine Aufführung am wenigsten sein? Ordne vom größten Störfaktor.", "Zu lang", "Unverständlich", "Zu vorhersehbar", "Mehr Effekt als Inhalt"),
            q("Was ist dir bei einer bekannten Geschichte wichtiger? Ordne.", "Texttreue", "Neue Interpretation", "Starke Figuren", "Aktueller Bezug"),
            q("Was bleibt nach einem guten Abend am ehesten hängen? Ordne.", "Eine Szene", "Ein Satz", "Ein Musikmoment", "Die Diskussion danach")
        ),
        "h500_305_festivals_prognose" to listOf(
            q("Welche Festivalrichtung würde dein Partner wegen des Programms vermutlich wählen?", "Rock & Alternative", "Pop", "Electronic", "Jazz, Klassik oder Weltmusik"),
            q("Was wäre deinem Partner beim Line-up wahrscheinlich wichtiger?", "Viele Lieblingsacts", "Neue Künstler entdecken", "Ein großer Headliner", "Stimmiges Gesamtprogramm"),
            q("Welche Bühne würde dein Partner eher spontan wählen?", "Große Hauptbühne", "Kleine Newcomer-Bühne", "Akustik-Set", "DJ- oder Nachtbühne"),
            q("Was würde deinen Partner musikalisch am ehesten überraschen?", "Unbekannter Act wird Highlight", "Genre gefällt plötzlich", "Special Guest", "Neue Version eines bekannten Songs"),
            q("Wie würde dein Partner Überschneidungen im Line-up lösen?", "Lieblingsact komplett", "Jeweils halbes Set", "Spontan nach Stimmung", "Gemeinsam priorisieren"),
            q("Was würde dein Partner nach dem Festival vermutlich zuerst speichern?", "Neue Songs", "Neue Künstler", "Live-Aufnahmen", "Gemeinsame Festival-Playlist")
        ),
        "h500_306_dokumentationen_szenario" to listOf(
            q("Ihr wollt eine Doku schauen: einer Natur, einer True Crime. Wie entscheidet ihr?", "Heute Natur, nächstes Mal True Crime", "Drittes Thema suchen", "Eine kurze Folge von beidem", "Abwechselnd wählen"),
            q("Eine Doku macht eine starke Behauptung, nennt aber kaum Quellen. Was macht ihr?", "Danach recherchieren", "Kritisch weiterschauen", "Abbrechen", "Gegendarstellung suchen"),
            q("Die Doku ist sehr gut, aber emotional schwer. Einer möchte pausieren. Was ist fair?", "Sofort Pause", "Folge beenden", "Später allein weiterschauen", "Auf leichteres Thema wechseln"),
            q("Ihr kennt das Thema kaum. Was hilft euch am meisten?", "Doku einfach wirken lassen", "Vorher kurz Kontext lesen", "Danach Quellen prüfen", "Gemeinsam offene Fragen sammeln"),
            q("Eine Doku widerspricht einer festen Meinung von dir. Was tust du zuerst?", "Argumente prüfen", "Quelle prüfen", "Mit Partner diskutieren", "Weitere Perspektive suchen"),
            q("Nach einer richtig starken Doku wollt ihr tiefer rein. Was macht ihr?", "Podcast hören", "Artikel lesen", "Weitere Doku", "Originalquellen suchen")
        ),
        "h500_307_kunst_geheime_wahl" to listOf(
            q("Welche Kunst würdest du heimlich am liebsten zuhause aufhängen?", "Fotografie", "Abstrakte Kunst", "Klassische Malerei", "Illustration oder Grafik"),
            q("Was zieht dich in einem Werk zuerst an?", "Farbe", "Motiv", "Idee", "Technik"),
            q("Wo würdest du Kunst am liebsten entdecken?", "Museum", "Kleine Galerie", "Straße und öffentlicher Raum", "Online oder Social Media"),
            q("Was würdest du eher kaufen?", "Werk eines unbekannten Künstlers", "Druck eines bekannten Werks", "Eigenes Foto groß drucken", "Handgemachtes Einzelstück"),
            q("Welche Kunst darf dich eher herausfordern?", "Provokant", "Unverständlich", "Emotional", "Politisch oder gesellschaftlich"),
            q("Welche gemeinsame Kunstidee würdest du heimlich mitmachen?", "Galerie-Hopping", "Malkurs", "Foto-Projekt", "Gemeinsam ein Werk auswählen")
        ),
        "h500_310_medienkonsum_offene_runde" to listOf(
            GenQuestion("Welches Medium nimmt im Alltag mehr Zeit ein, als du ihm eigentlich geben möchtest?"),
            GenQuestion("Wann fühlt sich Medienkonsum für dich wirklich nach Erholung an – und wann eher nach Zeitverlust?"),
            GenQuestion("Welche Inhalte möchtest du bewusst gemeinsam konsumieren und welche lieber allein?"),
            GenQuestion("Wie gehst du damit um, wenn einer etwas gemeinsam weiterschauen möchte und der andere nicht?"),
            GenQuestion("Welche medienfreie Situation würdest du in eurem Alltag gern stärker schützen?"),
            GenQuestion("Welche eine Veränderung würde euren Medienalltag für dich sofort besser machen?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        when {
            decisions[pack.id] == CurationDecision.ARCHIVE -> null
            pack.id in overrides -> pack.copy(questions = overrides.getValue(pack.id))
            else -> pack
        }
    }
}
