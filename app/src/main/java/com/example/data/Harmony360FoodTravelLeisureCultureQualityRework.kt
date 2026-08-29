package com.example.data

/**
 * Stage 05.2 curation boundary for food, travel, leisure and culture content.
 *
 * The large generated section files stay untouched as raw source. Curation is explicit
 * by stable pack ID so every rewrite/archive decision remains reviewable and reversible.
 */
object Harmony360FoodTravelLeisureCultureQualityRework {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    internal val sectionTags: Set<String> = setOf(
        "h360_section_04_reisen_abenteuer",
        "h360_section_05_essen_genuss",
        "h360_section_07_freizeit_hobbys",
        "h360_section_14_kultur_medien"
    )

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val travelDecisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_076_wochenendtrip_szenario" to CurationDecision.REWRITE,
        "h500_077_roadtrip_geheime_wahl" to CurationDecision.REWRITE,
        "h500_078_strandurlaub_memory" to CurationDecision.REWRITE,
        "h500_079_bergurlaub_prioritaet" to CurationDecision.REWRITE,
        "h500_080_staedtereise_offene_runde" to CurationDecision.REWRITE,
        "h500_081_fernreise_entweder_oder" to CurationDecision.REWRITE,
        "h500_082_zugreise_wer_eher" to CurationDecision.REWRITE,
        "h500_083_camping_skala" to CurationDecision.REWRITE,
        "h500_084_wellnessurlaub_ranking" to CurationDecision.REWRITE,
        "h500_085_abenteuerurlaub_prognose" to CurationDecision.REWRITE,
        "h500_086_kulinarische_reise_szenario" to CurationDecision.REWRITE,
        "h500_089_inselhopping_prioritaet" to CurationDecision.REWRITE,
        "h500_091_luxusreise_entweder_oder" to CurationDecision.REWRITE,
        "h500_092_backpacking_wer_eher" to CurationDecision.REWRITE,
        "h500_093_spontantrip_skala" to CurationDecision.REWRITE,
        "h500_097_unterkuenfte_geheime_wahl" to CurationDecision.REWRITE,
        "h500_099_traumziele_prioritaet" to CurationDecision.REWRITE,
        "h500_100_unser_perfekter_reisetag_offene_runde" to CurationDecision.REWRITE
    )

    private val travelOverrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_076_wochenendtrip_szenario" to listOf(
            q("Freitagabend: Einer will Großstadt, der andere raus ins Grüne. Wie entscheidet ihr?", "Ziele und Bedürfnisse vergleichen", "Halb Stadt, halb Natur", "Diesmal entscheidet einer", "Ein drittes Ziel suchen"),
            q("Ihr habt nur zwei Tage. Wie soll sich der Wochenendtrip anfühlen?", "Ein Highlight plus freie Zeit", "Möglichst viel erleben", "Erst vor Ort entscheiden", "Vor allem ausschlafen und erholen"),
            q("Eure Vorstellungen vom Budget liegen weit auseinander. Was macht ihr?", "Gemeinsames Limit festlegen", "Bei einer Sache bewusst gönnen", "Günstig reisen, Erlebnis priorisieren", "Extras zahlt jeder selbst"),
            q("Dauerregen zerstört euren Plan. Was wäre eure beste Reaktion?", "Gute Indoor-Alternative suchen", "Trotzdem raus und durchziehen", "Spontan das Ziel wechseln", "Den Tag bewusst langsam machen"),
            q("Samstagmittag ist einer deutlich erschöpfter als der andere. Was passiert?", "Tempo für beide senken", "Ein paar Stunden getrennt verbringen", "Nur den wichtigsten Plan behalten", "Pause machen und später neu entscheiden"),
            q("Sonntag: Einer möchte früh heim, der andere jede Minute nutzen. Was ist fair?", "Früh zurückfahren", "Bis abends bleiben", "Mittags als Kompromiss fahren", "Verkehr und Energie entscheiden lassen")
        ),
        "h500_077_roadtrip_geheime_wahl" to listOf(
            q("Welche Route würdest du für einen Roadtrip heimlich sofort wählen?", "Küstenstraße", "Kurvige Landstraße", "Schnell über die Autobahn", "Route erst unterwegs entscheiden"),
            q("Wie viele ungeplante Stopps würdest du dir wirklich wünschen?", "So viele wie möglich", "Ein oder zwei gute", "Nur wenn etwas Besonderes auftaucht", "Am liebsten gar keine"),
            q("Was soll im Auto den Ton angeben?", "Gemeinsame Playlist", "Podcasts", "Abwechselnd Musik wählen", "Einfach reden und Ruhe haben"),
            q("Wie würdest du das Fahren am liebsten aufteilen?", "Genau halb und halb", "Wer gerade Lust hat", "Der sicherere Fahrer mehr", "Ich würde am liebsten kaum fahren"),
            q("Wo würdest du unterwegs am liebsten schlafen?", "Kleines Motel", "Schönes Hotel", "Camping", "Außergewöhnliche Unterkunft"),
            q("Ein Schild verspricht einen Aussichtspunkt 40 Minuten abseits der Route. Was wählst du?", "Sofort abbiegen", "Nur wenn genug Zeit ist", "Partner entscheiden lassen", "Auf der Route bleiben")
        ),
        "h500_078_strandurlaub_memory" to listOf(
            GenQuestion("Welche Strand-Erinnerung aus deinem Leben taucht als Erstes auf – und warum genau die?"),
            GenQuestion("Wann warst du am Meer zuletzt so entspannt, dass du komplett die Zeit vergessen hast?"),
            GenQuestion("Welche lustige Szene mit Sand, Sonne oder Wasser würdest du gern noch einmal erleben?"),
            GenQuestion("Welches Essen oder Getränk gehört für dich untrennbar zu einem perfekten Strandtag?"),
            GenQuestion("Gab es einmal einen Sonnenauf- oder -untergang am Wasser, den du bis heute im Kopf hast?"),
            GenQuestion("Welche kleine Strand-Tradition würdest du gern irgendwann nur mit deinem Partner haben?")
        ),
        "h500_079_bergurlaub_prioritaet" to listOf(
            q("Was muss bei einem Bergurlaub auf Platz 1 stehen?", "Große Aussicht", "Wandern", "Gemütliche Hütte", "Ruhe und Erholung"),
            q("Wenn das Wetter nur für eine Sache reicht: Was bleibt?", "Gipfeltour", "Leichte Panoramarunde", "Wellness in der Unterkunft", "Langer Hüttenabend"),
            q("Was entscheidet für dich über eine gute Unterkunft in den Bergen?", "Direkt am Wanderweg", "Gutes Bett", "Sauna oder Wellness", "Besondere Aussicht"),
            q("Was ist bei unterschiedlicher Kondition am wichtigsten?", "Tempo des Langsameren", "Getrennte Touren erlauben", "Gemeinsame mittlere Route", "Abwechselnd Wünsche erfüllen"),
            q("Wofür würdest du am ehesten extra Geld ausgeben?", "Bergbahn", "Schöne Unterkunft", "Gutes Essen", "Geführtes Erlebnis"),
            q("Was dürfte bei einem Bergurlaub am wenigsten fehlen?", "Natur", "Bewegung", "Gemeinsame Zeit", "Komfort")
        ),
        "h500_080_staedtereise_offene_runde" to listOf(
            GenQuestion("Wie sieht für dich der perfekte Rhythmus einer Städtereise aus: früh los oder langsam starten?"),
            GenQuestion("Wie viele feste Must-sees verträgst du an einem Tag, bevor Reisen für dich nach Arbeit klingt?"),
            GenQuestion("Wofür würdest du in einer fremden Stadt am ehesten Zeit opfern: Essen, Kultur, Shopping oder einfach Herumlaufen?"),
            GenQuestion("Wann wäre es für dich völlig okay, auf einer gemeinsamen Städtereise ein paar Stunden allein loszuziehen?"),
            GenQuestion("Bei welchem Reise-Thema willst du vorher Klarheit: Budget, Wege, Restaurantplanung oder Tagesprogramm?"),
            GenQuestion("Was macht den Abend in einer neuen Stadt für dich perfekt – und was wäre dir schon zu viel?")
        ),
        "h500_081_fernreise_entweder_oder" to listOf(
            q("Was wäre dir bei einer Fernreise am wichtigsten?", "Mehr Komfort im Flug", "Mehr Budget am Ziel", "Länger bleiben", "Kürzer, dafür besonderer"),
            q("Wie möchtest du die ersten Tage angehen?", "Direkt volles Programm", "Erst ankommen und Jetlag auskurieren", "Jeden Tag nur ein Highlight", "Komplett spontan"),
            q("Welche Unterkunft passt eher zu dir?", "Gutes Hotel", "Apartment mit Küche", "Kleine lokale Unterkunft", "Außergewöhnlicher Ort"),
            q("Was zieht dich in einem weit entfernten Land am stärksten an?", "Essen", "Natur", "Kultur und Geschichte", "Alltag der Menschen"),
            q("Wo würdest du am ehesten sparen?", "Flugkomfort", "Unterkunft", "Restaurants", "Touren und Aktivitäten"),
            q("Was würdest du vor Abflug am liebsten geklärt haben?", "Komplette Route", "Nur erste Nächte", "Transport zwischen Orten", "Fast nichts – vor Ort schauen")
        ),
        "h500_082_zugreise_wer_eher" to listOf(
            whoQ("Wer prüft vor einer Zugreise eher dreimal Gleis, Wagen und Sitzplatz?"),
            whoQ("Wer packt eher Snacks ein, obwohl die Fahrt nur zwei Stunden dauert?"),
            whoQ("Wer möchte eher unbedingt am Fenster sitzen?"),
            whoQ("Wer bleibt bei einer verpassten Verbindung eher ruhig und findet den neuen Plan?"),
            whoQ("Wer würde eher zu viel Gepäck in die Ablage wuchten?"),
            whoQ("Wer schlägt eher vor, für eine schöne Strecke bewusst den langsameren Zug zu nehmen?")
        ),
        "h500_083_camping_skala" to listOf(
            scaleQ("Wie gut könntest du mehrere Nächte wirklich gern in einem Zelt schlafen?"),
            scaleQ("Wie entspannt wärst du mit wenig Platz und deutlich weniger Komfort als zuhause?"),
            scaleQ("Wie wenig würde dich Regen beim Camping aus der Ruhe bringen?"),
            scaleQ("Wie okay wäre für dich ein Campingplatz ohne eigenes Bad direkt an der Unterkunft?"),
            scaleQ("Wie gern würdest du beim Campen jeden Tag einfach und selbst kochen?"),
            scaleQ("Wie groß wäre deine Lust auf mehrere Tage Camping nur mit leichtem Gepäck?")
        ),
        "h500_084_wellnessurlaub_ranking" to listOf(
            q("Was gehört für dich bei Wellness ganz nach oben? Ordne.", "Sauna & Dampfbad", "Massage", "Pool", "Einfach schlafen und nichts tun"),
            q("Was entscheidet am stärksten, ob du wirklich abschalten kannst? Ordne.", "Ruhe", "Gutes Bett", "Wenig Termine", "Handy möglichst weg"),
            q("Wofür würdest du bei einem Wellnessurlaub zuerst Geld ausgeben? Ordne.", "Massage", "Besseres Zimmer", "Sehr gutes Essen", "Private Spa-Zeit"),
            q("Wie sähe dein perfekter Wellness-Nachmittag aus? Ordne.", "Sauna", "Pool", "Anwendung", "Lesen oder schlafen"),
            q("Was wäre dir als Paar wichtiger? Ordne.", "Gemeinsame Anwendungen", "Viel Ruhe nebeneinander", "Gutes Abendessen", "Zeit ohne festen Plan"),
            q("Was darf trotz Wellness nicht fehlen? Ordne.", "Bewegung draußen", "Langes Frühstück", "Früher Abend im Bett", "Ein kleiner Ausflug")
        ),
        "h500_085_abenteuerurlaub_prognose" to listOf(
            q("Welche Aktivität würde dein Partner wahrscheinlich zuerst wählen?", "Rafting", "Klettersteig", "Mountainbike-Tour", "Paragliding"),
            q("Wie viel Nervenkitzel braucht dein Partner vermutlich?", "Lieber kontrolliert", "Ein bisschen Adrenalin", "Deutlich aufregend", "Je wilder, desto besser"),
            q("Was wäre deinem Partner bei einem Abenteuer am wichtigsten?", "Gute Vorbereitung", "Gemeinsam durchziehen", "Etwas völlig Neues", "Eine starke Geschichte danach"),
            q("Wann würde dein Partner eher Nein sagen?", "Zu große Höhe", "Zu wenig Kontrolle", "Zu anstrengend", "Zu viel Risiko"),
            q("Was würde deinem Partner nach einem intensiven Tag am meisten guttun?", "Ruhe", "Gutes Essen", "Noch etwas erleben", "Über den Tag reden"),
            q("Wer soll bei einer neuen Aktivität eher den ersten Schritt machen?", "Mein Partner", "Ich", "Beide gleichzeitig", "Ein Guide gibt den Impuls")
        ),
        "h500_086_kulinarische_reise_szenario" to listOf(
            q("Ihr steht vor einem beliebten Stand, dessen Gerichte ihr nicht kennt. Was macht ihr?", "Streetfood probieren", "Erst fragen, was drin ist", "Etwas Bekanntes suchen", "Zwei Dinge teilen"),
            q("Einer möchte ein teures Degustationsmenü, der andere lieber viele kleine lokale Orte. Wie löst ihr es?", "Heute Fine Dining, morgen lokal", "Nur lokale Küche", "Das besondere Menü wählen", "Budget teilen und beides kleiner machen"),
            q("Ein Gericht ist für einen von euch viel zu scharf. Was passiert?", "Gerichte tauschen", "Etwas Neues bestellen", "Zusammen weiterprobieren", "Der andere isst es, ich bestelle separat"),
            q("Ihr entdeckt spontan ein Restaurant, habt aber schon eine Reservierung. Was macht ihr?", "Spontanen Fund wählen", "Reservierung behalten", "Nur einen Snack probieren", "Münzwurf und nicht diskutieren"),
            q("Eine Unverträglichkeit oder Ernährungsgrenze macht die Auswahl schwierig. Was ist fair?", "Passenden Ort gemeinsam suchen", "Betroffene Person wählt", "Mehrere kleine Stopps machen", "Vorher konkret recherchieren"),
            q("Am letzten Abend könnt ihr nur ein Essen wiederholen. Was entscheidet?", "Bestes Gericht", "Schönste Atmosphäre", "Ort mit gemeinsamer Erinnerung", "Noch einmal etwas völlig Neues")
        ),
        "h500_089_inselhopping_prioritaet" to listOf(
            q("Was ist beim Inselhopping wichtiger?", "Weniger Inseln, mehr Zeit", "Viele Inseln sehen", "Spontan länger bleiben", "Feste Route ohne Stress"),
            q("Was muss auf den Inseln unbedingt passieren?", "Strandtag", "Schnorcheln oder Boot", "Ort und Kultur entdecken", "Wandern oder Aussicht"),
            q("Worauf würdest du bei engem Budget am wenigsten verzichten?", "Schöne Unterkunft", "Gutes Essen", "Bootsausflüge", "Mehr Reisetage"),
            q("Was nervt dich beim Inselwechsel am ehesten?", "Frühes Aufstehen", "Fahrpläne", "Gepäck tragen", "Zeit im Transit verlieren"),
            q("Was soll bei der Route den Ausschlag geben?", "Schönste Strände", "Abwechslungsreiche Inseln", "Kurze Verbindungen", "Empfehlungen vor Ort"),
            q("Was wäre dein perfekter letzter Inselabend?", "Sonnenuntergang am Strand", "Kleines Restaurant", "Noch einmal durch den Ort laufen", "Früh schlafen für die Heimreise")
        ),
        "h500_091_luxusreise_entweder_oder" to listOf(
            q("Was fühlt sich für dich eher nach echtem Luxus an?", "Große Suite", "Kleine besondere Boutique-Unterkunft", "Private Villa", "Außergewöhnliche Lodge"),
            q("Wofür würdest du am liebsten mehr bezahlen?", "Top-Lage", "Service", "Privatsphäre", "Design und Atmosphäre"),
            q("Welches Extra reizt dich am meisten?", "Spa", "Fine Dining", "Private Tour", "Pool nur für wenige Gäste"),
            q("Was wäre dir wichtiger?", "Perfekt organisiert", "Maximal flexibel", "Alles vor Ort", "Jeden Tag etwas Besonderes"),
            q("Was darf trotz Luxus nicht verloren gehen?", "Lokales Leben", "Spontane Entdeckungen", "Einfachheit", "Zeit nur zu zweit"),
            q("Wo würdest du Luxus am ehesten weglassen?", "Flug", "Zimmergröße", "Restaurants", "Transfers")
        ),
        "h500_092_backpacking_wer_eher" to listOf(
            whoQ("Wer findet beim Backpacking eher die günstigste gute Verbindung?"),
            whoQ("Wer packt eher so leicht, dass noch Platz im Rucksack bleibt?"),
            whoQ("Wer kommt eher mit anderen Reisenden ins Gespräch?"),
            whoQ("Wer schläft eher problemlos im einfachen Hostelzimmer?"),
            whoQ("Wer würde eher spontan einen Ort länger bleiben, weil es sich richtig anfühlt?"),
            whoQ("Wer behält eher Pässe, Tickets und Geld im Blick, wenn es chaotisch wird?")
        ),
        "h500_093_spontantrip_skala" to listOf(
            scaleQ("Wie wahrscheinlich wäre es, dass du innerhalb der nächsten 24 Stunden wirklich spontan losfahren würdest?"),
            scaleQ("Wie entspannt wärst du, wenn nur die erste Nacht gebucht wäre?"),
            scaleQ("Wie gut könntest du damit leben, das genaue Ziel erst unterwegs festzulegen?"),
            scaleQ("Wie wenig Gepäck würdest du für einen spontanen Kurztrip wirklich brauchen?"),
            scaleQ("Wie entspannt wärst du mit einem groben statt exakt geplanten Reisebudget?"),
            scaleQ("Wie gern würdest du deinem Partner das Ziel komplett als Überraschung überlassen?")
        ),
        "h500_097_unterkuenfte_geheime_wahl" to listOf(
            q("Welche Unterkunft würdest du heimlich sofort buchen?", "Baumhaus", "Boutique-Hotel", "Apartment", "Tiny House"),
            q("Welches Detail ist dir wichtiger, als du meistens zugibst?", "Sehr gutes Bett", "Eigenes Bad", "Schöne Aussicht", "Perfekte Lage"),
            q("Was würdest du für eine besondere Unterkunft am ehesten akzeptieren?", "Längeren Weg", "Weniger Platz", "Höheren Preis", "Weniger Service"),
            q("Was wäre für dich der größte Stimmungskiller?", "Hellhörige Wände", "Schlechtes Bett", "Unsauberes Bad", "Weit weg von allem"),
            q("Was würdest du gern einmal ausprobieren?", "Glamping", "Hausboot", "Berghütte", "Historisches Gebäude"),
            q("Wenn die Unterkunft selbst das Erlebnis sein soll: Was gewinnt?", "Design", "Natur drumherum", "Privatsphäre", "Außergewöhnliche Geschichte")
        ),
        "h500_099_traumziele_prioritaet" to listOf(
            q("Was macht ein Ziel für dich zum Traumziel?", "Spektakuläre Natur", "Kultur und Geschichte", "Essen", "Einmaliges Erlebnis"),
            q("Was beeinflusst deine Wahl am stärksten?", "Budget", "Klima", "Reisezeit", "Flugdauer"),
            q("Wofür würdest du bei einem Traumziel am ehesten eine lange Anreise akzeptieren?", "Naturwunder", "Besondere Kultur", "Tierwelt", "Ort von meiner Lebensliste"),
            q("Was darf ein Traumziel trotz großer Erwartungen nicht werden?", "Zu durchgetaktet", "Zu touristisch", "Zu teuer vor Ort", "Nur eine Foto-Kulisse"),
            q("Was wäre dir für die gemeinsame Wahl wichtiger?", "Beide wollen es gleich stark", "Einer hat einen großen Lebenstraum", "Es passt zum Budget", "Es ist etwas Neues für beide"),
            q("Welcher Faktor würde ein Traumziel am ehesten wieder von deiner Liste werfen?", "Zu wenig Zeit", "Zu hohe Kosten", "Falsche Jahreszeit", "Zu komplizierte Anreise")
        ),
        "h500_100_unser_perfekter_reisetag_offene_runde" to listOf(
            GenQuestion("Wie beginnt euer perfekter Reisetag: langes Frühstück oder möglichst früh raus – und warum?"),
            GenQuestion("Wie viele feste Pläne braucht ein guter Reisetag für dich, bevor er sich zu durchgetaktet anfühlt?"),
            GenQuestion("Welches eine Erlebnis dürfte an deinem perfekten Reisetag auf keinen Fall fehlen?"),
            GenQuestion("Wann möchtest du unterwegs lieber einfach sitzen, schauen und nichts abhaken?"),
            GenQuestion("Wie wichtig sind Fotos und Handy für dich an einem besonderen Reisetag?"),
            GenQuestion("Was wäre dein idealer Abschluss: gutes Essen, Sonnenuntergang, Nachtleben oder früh zurück in die Unterkunft?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        when {
            travelDecisions[pack.id] == CurationDecision.ARCHIVE -> null
            pack.id in travelOverrides -> pack.copy(questions = travelOverrides.getValue(pack.id))
            else -> pack
        }
    }
}
