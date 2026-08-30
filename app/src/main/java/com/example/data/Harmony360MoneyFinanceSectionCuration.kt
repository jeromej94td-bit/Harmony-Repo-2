package com.example.data

/** Explicit Stage 05.3 curation for Harmony-360 Section 09 — Geld & Finanzen. */
object Harmony360MoneyFinanceSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – sehr stark")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun open(text: String): GenQuestion = GenQuestion(q = text)
    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_191_gemeinsames_konto_entweder_oder" to CurationDecision.REWRITE,
        "h500_192_sparen_wer_eher" to CurationDecision.REWRITE,
        "h500_193_ausgaben_skala" to CurationDecision.REWRITE,
        "h500_194_investieren_ranking" to CurationDecision.REWRITE,
        "h500_195_groessere_anschaffungen_prognose" to CurationDecision.REWRITE,
        "h500_196_finanzielle_unabhaengigkeit_szenario" to CurationDecision.REWRITE,
        "h500_197_konsumverhalten_geheime_wahl" to CurationDecision.REWRITE,
        "h500_198_geld_in_der_kindheit_memory" to CurationDecision.REWRITE,
        "h500_199_finanzplanung_prioritaet" to CurationDecision.REWRITE,
        "h500_200_geld_und_werte_offene_runde" to CurationDecision.REWRITE,
        "h500_201_taschengeld_entweder_oder" to CurationDecision.REWRITE,
        "h500_202_haushaltsbuch_wer_eher" to CurationDecision.REWRITE,
        "h500_203_notgroschen_skala" to CurationDecision.REWRITE,
        "h500_204_luxus_ranking" to CurationDecision.REWRITE,
        "h500_205_spenden_prognose" to CurationDecision.REWRITE,
        "h500_206_erben_szenario" to CurationDecision.REWRITE,
        "h500_207_altersvorsorge_geheime_wahl" to CurationDecision.REWRITE,
        "h500_210_finanzielle_gespraeche_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_191_gemeinsames_konto_entweder_oder" to listOf(
            q("Welches Kontomodell fühlt sich für dich als Paar stimmiger an?", "Gemeinsame Fixkosten über ein Gemeinschaftskonto", "Fixkosten getrennt aufteilen"),
            q("Was wäre dir bei gemeinsamen Ausgaben lieber?", "Fester monatlicher Betrag von beiden", "Je nach Einkommen unterschiedlich einzahlen"),
            q("Wie transparent sollten persönliche Ausgaben sein?", "Nur gemeinsame Kosten besprechen", "Auch größere private Ausgaben offen ansprechen"),
            q("Was wäre dir bei einem Gemeinschaftskonto wichtiger?", "Klare Regeln von Anfang an", "Flexibel im Alltag entscheiden"),
            q("Wenn einer deutlich mehr verdient?", "Gleiche Beträge einzahlen", "An Einkommen orientieren"),
            q("Welche Lösung schützt für dich mehr Eigenständigkeit?", "Eigenes Konto plus Gemeinschaftskonto", "Fast alles gemeinsam verwalten")
        ),
        "h500_192_sparen_wer_eher" to listOf(
            whoQ("Wer setzt sich eher freiwillig ein konkretes Sparziel?"),
            whoQ("Wer bleibt bei einem Sparplan eher konsequent, wenn etwas Verlockendes auftaucht?"),
            whoQ("Wer schlägt eher vor, für ein gemeinsames Erlebnis früh Geld zurückzulegen?"),
            whoQ("Wer kontrolliert eher, ob ein Sparziel noch realistisch ist?"),
            whoQ("Wer würde eher einen Plan anpassen, statt ein Ziel komplett aufzugeben?"),
            whoQ("Wer freut sich eher sichtbar darüber, wenn ein gemeinsames Sparziel erreicht ist?")
        ),
        "h500_193_ausgaben_skala" to listOf(
            scaleQ("Wie wichtig ist dir, größere Ausgaben vorher miteinander abzusprechen?"),
            scaleQ("Wie stark beschäftigt dich eine unerwartet hohe Monatsausgabe?"),
            scaleQ("Wie leicht fällt es dir, Geld für schöne Erlebnisse ohne schlechtes Gewissen auszugeben?"),
            scaleQ("Wie wichtig ist dir ein persönliches Budget, über das jeder frei entscheiden kann?"),
            scaleQ("Wie sehr möchtest du regelmäßig wissen, wofür euer gemeinsames Geld ausgegeben wird?"),
            scaleQ("Wie gut könntest du damit leben, wenn dein Partner bei privaten Ausgaben deutlich anders tickt als du?")
        ),
        "h500_194_investieren_ranking" to listOf(
            q("Was sollte vor einer gemeinsamen Anlageentscheidung zuerst geklärt sein? Ordne.", "Risikotoleranz", "Zeithorizont", "Finanzieller Puffer", "Gemeinsames Ziel"),
            q("Was schafft für dich bei Geldanlage am meisten Vertrauen? Ordne.", "Verständlichkeit", "Transparente Kosten", "Nachvollziehbares Risiko", "Genug Zeit zum Entscheiden"),
            q("Welche Frage sollte ein Paar zuerst gemeinsam beantworten? Ordne.", "Wie viel Schwankung halten wir aus?", "Wann brauchen wir das Geld?", "Wie viel bleibt liquide?", "Wer kümmert sich um den Überblick?"),
            q("Was wäre für dich bei unterschiedlichen Meinungen am wichtigsten? Ordne.", "Kein Druck", "Beide verstehen die Entscheidung", "Nur gemeinsames Geld gemeinsam entscheiden", "Erst weiter informieren"),
            q("Was würde dich bei einer gemeinsamen Anlage am stärksten verunsichern? Ordne.", "Unklares Risiko", "Zu wenig Rücklagen", "Entscheidung unter Zeitdruck", "Einer versteht das Produkt nicht"),
            q("Was sollte langfristig wichtiger sein als kurzfristige Begeisterung? Ordne.", "Passung zum gemeinsamen Ziel", "Tragbares Risiko", "Verständliche Strategie", "Regelmäßige gemeinsame Überprüfung")
        ),
        "h500_195_groessere_anschaffungen_prognose" to listOf(
            q("Ab welcher Größenordnung würde dein Partner eine Anschaffung vermutlich gemeinsam besprechen wollen?", "Schon bei einigen hundert Euro", "Ab etwa einem Monatsbudget", "Nur bei wirklich großen Käufen", "Kommt ganz auf den Zweck an"),
            q("Was würde dein Partner vor einem größeren Kauf wahrscheinlich zuerst prüfen?", "Ob wir es wirklich brauchen", "Ob es ins Budget passt", "Qualität und Haltbarkeit", "Ob es eine günstigere Alternative gibt"),
            q("Wie würde dein Partner bei einem starken spontanen Kaufwunsch eher reagieren?", "Eine Nacht darüber schlafen", "Direkt entscheiden", "Erst mit mir besprechen", "Preis und Nutzen vergleichen"),
            q("Was wäre deinem Partner bei einer gemeinsamen Anschaffung vermutlich wichtiger?", "Lange Nutzungsdauer", "Guter Preis", "Hoher Komfort", "Dass beide sie wirklich wollen"),
            q("Wenn ihr euch bei einem Kauf nicht einig seid: Was würde dein Partner eher wollen?", "Kauf verschieben", "Kompromissmodell suchen", "Wer es mehr nutzt entscheidet stärker", "Budgetgrenze festlegen"),
            q("Was würde dein Partner nach einem Fehlkauf vermutlich am ehesten tun?", "Zurückgeben oder verkaufen", "Als Erfahrung abhaken", "Künftig genauer prüfen", "Mit Humor nehmen")
        ),
        "h500_196_finanzielle_unabhaengigkeit_szenario" to listOf(
            q("Einer von euch möchte beruflich kürzertreten, obwohl dadurch weniger Einkommen da ist. Was klärt ihr zuerst?", "Monatliche Fixkosten", "Persönliche Gründe", "Zeitplan", "Welche Rücklagen nötig sind"),
            q("Einer verdient plötzlich deutlich mehr als der andere. Was schützt eure finanzielle Eigenständigkeit am besten?", "Persönliches Geld für beide", "Faire Kostenverteilung", "Klare gemeinsame Ziele", "Regelmäßige offene Gespräche"),
            q("Einer möchte für einige Monate ohne Einkommen ein eigenes Projekt verfolgen. Was wäre fair?", "Vorher gemeinsames Budget festlegen", "Nur aus eigenen Rücklagen finanzieren", "Zeitlich begrenzte Unterstützung", "Erst später starten"),
            q("Ihr habt sehr unterschiedliche Vorstellungen von finanzieller Freiheit. Was macht ihr?", "Definitionen konkret vergleichen", "Gemeinsames Mindestziel finden", "Getrennte persönliche Ziele zulassen", "Plan später erneut prüfen"),
            q("Eine unerwartete Rechnung frisst einen großen Teil eurer Rücklagen. Wie reagiert ihr?", "Ausgaben vorübergehend reduzieren", "Sparziel verlängern", "Rücklagen zuerst wieder aufbauen", "Gemeinsam neue Prioritäten setzen"),
            q("Was wäre ein Warnsignal für fehlende finanzielle Unabhängigkeit in einer Beziehung?", "Einer darf kaum selbst entscheiden", "Geld wird als Druckmittel genutzt", "Wichtige Zahlen bleiben geheim", "Eigene Bedürfnisse werden ständig zurückgestellt")
        ),
        "h500_197_konsumverhalten_geheime_wahl" to listOf(
            q("Wofür gibst du heimlich am liebsten etwas mehr Geld aus?", "Essen und Genuss", "Reisen und Erlebnisse", "Technik oder Hobbys", "Komfort zuhause"),
            q("Welche Ausgabe rechtfertigst du vor dir selbst am schnellsten?", "Etwas, das Zeit spart", "Etwas, das lange hält", "Etwas, das Freude macht", "Etwas, das ich schon lange wollte"),
            q("Wo wärst du am ehesten bereit, deinen Konsum deutlich zu reduzieren?", "Kleidung", "Lieferdienste", "Abos", "Spontankäufe"),
            q("Was verführt dich eher zu einem ungeplanten Kauf?", "Rabatt", "Empfehlung", "Starker Wunsch im Moment", "Belohnungsgefühl"),
            q("Welche Ausgabe würdest du ungern von deinem Partner kommentiert bekommen?", "Hobby", "Kleidung", "Essen unterwegs", "Kleine Luxusartikel"),
            q("Was wäre für dich ein guter gemeinsamer Konsum-Grundsatz?", "Lieber weniger, dafür passend", "Erlebnisse vor Dingen", "Große Käufe erst nach Bedenkzeit", "Jeder hat einen freien persönlichen Bereich")
        ),
        "h500_198_geld_in_der_kindheit_memory" to listOf(
            open("Welche Erinnerung aus deiner Kindheit hat deine Haltung zu Geld besonders geprägt?"),
            open("Wann hast du als Kind zum ersten Mal verstanden, dass Geld in Familien sehr unterschiedlich verfügbar sein kann?"),
            open("Wofür hast du als Kind oder Jugendlicher zum ersten Mal länger gespart?"),
            open("Welche Aussage über Geld hast du zuhause besonders oft gehört?"),
            open("Gab es in deiner Kindheit etwas, das finanziell knapp war und das du bis heute nicht vergessen hast?"),
            open("Welche Geldregel aus deiner Kindheit würdest du heute übernehmen – und welche bewusst anders machen?")
        ),
        "h500_199_finanzplanung_prioritaet" to listOf(
            q("Was sollte bei eurer Finanzplanung zuerst abgesichert sein?", "Laufende Fixkosten", "Notfallpuffer", "Gemeinsame Ziele", "Persönlicher Freiraum"),
            q("Welches Ziel verdient bei zusätzlichem Geld zuerst Aufmerksamkeit?", "Rücklagen", "Schulden reduzieren", "Großes gemeinsames Ziel", "Lebensqualität heute"),
            q("Was macht eine Finanzplanung für dich brauchbar?", "Einfacher Überblick", "Konkrete Monatsziele", "Viel Flexibilität", "Klare langfristige Richtung"),
            q("Welche Zahl sollte ein Paar am ehesten gemeinsam kennen?", "Fixkosten pro Monat", "Gemeinsame Rücklagen", "Offene Verpflichtungen", "Budget für gemeinsame Wünsche"),
            q("Was sollte bei einem knappen Monat am wenigsten leiden?", "Grundlegende Sicherheit", "Gesundheit und Alltag", "Gemeinsame Verlässlichkeit", "Ein kleiner persönlicher Freiraum"),
            q("Wie oft sollte Finanzplanung für euch Thema sein?", "Monatlich kurz", "Vierteljährlich gründlich", "Bei größeren Veränderungen", "Nur wenn es einen konkreten Anlass gibt")
        ),
        "h500_200_geld_und_werte_offene_runde" to listOf(
            open("Woran merkst du, dass Geld für dich eher Sicherheit, Freiheit, Status oder Möglichkeiten bedeutet?"),
            open("Für welche Sache würdest du bewusst weniger verdienen, wenn sie dir dafür mehr Lebensqualität gibt?"),
            open("Welche Ausgabe sagt deiner Meinung nach besonders viel über die Werte eines Menschen aus?"),
            open("Wann wird Sparsamkeit für dich vernünftig – und wann wird sie zu Einschränkung?"),
            open("Bei welchem finanziellen Thema würdest du niemals nur nach dem günstigsten Preis entscheiden?"),
            open("Welche gemeinsame Geldentscheidung sollte eure Werte als Paar am deutlichsten widerspiegeln?")
        ),
        "h500_201_taschengeld_entweder_oder" to listOf(
            q("Falls ihr euch ein frei verfügbares persönliches Budget setzt: Wie wäre es dir lieber?", "Gleicher Betrag für beide", "Jeder bestimmt seinen Betrag selbst"),
            q("Was sollte mit persönlichem Budget möglich sein?", "Komplett ohne Rechtfertigung ausgeben", "Größere Käufe trotzdem kurz ansprechen"),
            q("Wenn einer sein persönliches Budget nicht nutzt?", "Ansparen für später", "Im nächsten Monat neu starten"),
            q("Was fühlt sich fairer an?", "Persönliches Budget unabhängig vom Einkommen", "Persönliches Budget am Einkommen orientieren"),
            q("Wenn einer häufig mehr persönlichen Spielraum braucht?", "Gemeinsame Regel neu verhandeln", "Jeder löst das aus seinem eigenen Anteil"),
            q("Wofür ist persönliches Budget wichtiger?", "Eigenständigkeit", "Konflikte über Kleinausgaben vermeiden")
        ),
        "h500_202_haushaltsbuch_wer_eher" to listOf(
            whoQ("Wer würde ein Haushaltsbuch eher konsequent über mehrere Monate führen?"),
            whoQ("Wer entdeckt eher ein Abo oder eine Ausgabe, die längst nicht mehr gebraucht wird?"),
            whoQ("Wer möchte eher Kategorien und Monatslimits festlegen?"),
            whoQ("Wer würde eher sagen: Der grobe Überblick reicht völlig?"),
            whoQ("Wer erinnert eher daran, gemeinsame Ausgaben einzutragen?"),
            whoQ("Wer würde aus den Zahlen eher einen konkreten neuen Plan ableiten?")
        ),
        "h500_203_notgroschen_skala" to listOf(
            scaleQ("Wie wichtig ist dir ein Notgroschen, bevor ihr Geld für größere Wünsche einplant?"),
            scaleQ("Wie unruhig würdest du werden, wenn eure Rücklage durch eine unerwartete Ausgabe stark sinkt?"),
            scaleQ("Wie wichtig ist dir, gemeinsam festzulegen, wofür der Notgroschen wirklich gedacht ist?"),
            scaleQ("Wie stark würde ein vorhandener Notgroschen deine Bereitschaft zu einer größeren Lebensveränderung erhöhen?"),
            scaleQ("Wie wichtig ist dir, einen verbrauchten Notgroschen anschließend wieder gezielt aufzufüllen?"),
            scaleQ("Wie sehr sollte jeder zusätzlich eine eigene kleine Reserve haben dürfen?")
        ),
        "h500_204_luxus_ranking" to listOf(
            q("Was fühlt sich für dich am ehesten wie echter Luxus an? Ordne.", "Freie Zeit", "Reisen", "Schönes Zuhause", "Hochwertige Dinge"),
            q("Wofür würdest du am ehesten bewusst mehr bezahlen? Ordne.", "Qualität", "Komfort", "Besonderes Erlebnis", "Zeitersparnis"),
            q("Was macht Luxus für dich am wenigsten attraktiv? Ordne vom größten Störfaktor.", "Statusdruck", "Hoher Preis", "Unnötige Verschwendung", "Kurze Freude"),
            q("Welche Luxusausgabe wäre als Paar am ehesten sinnvoll? Ordne.", "Gemeinsame Reise", "Besseres Zuhause", "Besonderes Essen", "Mehr freie Zeit erkaufen"),
            q("Was sollte vor einer Luxusausgabe zuerst stimmen? Ordne.", "Rücklagen", "Beide wollen es", "Keine wichtigen Rechnungen offen", "Ausgabe passt zu unseren Prioritäten"),
            q("Was wäre langfristig der schönste Luxus? Ordne.", "Weniger finanzieller Druck", "Mehr gemeinsame Zeit", "Mehr Wahlfreiheit", "Besondere Erinnerungen")
        ),
        "h500_205_spenden_prognose" to listOf(
            q("Für welchen Bereich würde dein Partner vermutlich am ehesten spenden?", "Menschen in Not", "Tiere", "Umwelt", "Lokale Projekte"),
            q("Wie würde dein Partner lieber helfen?", "Regelmäßig kleiner Betrag", "Einmal gezielt größer", "Zeit statt Geld", "Je nach konkretem Anlass"),
            q("Was wäre deinem Partner vor einer Spende vermutlich am wichtigsten?", "Nachvollziehbare Wirkung", "Vertrauenswürdige Organisation", "Persönlicher Bezug", "Niedrige Verwaltungskosten"),
            q("Wie würde dein Partner auf eine spontane Spendensammlung reagieren?", "Direkt etwas geben", "Erst kurz prüfen", "Lieber später gezielt spenden", "Eher nicht spontan entscheiden"),
            q("Sollten gemeinsame Spenden aus Sicht deines Partners gemeinsam entschieden werden?", "Immer", "Ab einer bestimmten Höhe", "Nur aus gemeinsamem Geld", "Jeder entscheidet für sich"),
            q("Was würde deinen Partner vermutlich stärker motivieren?", "Konkrete Geschichte", "Messbare Wirkung", "Persönliche Erfahrung", "Empfehlung einer vertrauten Person")
        ),
        "h500_206_erben_szenario" to listOf(
            q("Einer von euch erhält unerwartet ein größeres Erbe. Was sollte zuerst passieren?", "Zeit zum Verarbeiten nehmen", "Rechtliche und finanzielle Fragen klären", "Noch nichts Großes entscheiden", "Offen über Wünsche sprechen"),
            q("Ein Erbe ist emotional mit einer schwierigen Familiengeschichte verbunden. Wie geht ihr damit um?", "Emotionen zuerst ernst nehmen", "Entscheidungen vertagen", "Mit Familie sprechen", "Externe Beratung für Sachfragen nutzen"),
            q("Ihr habt unterschiedliche Vorstellungen, ob ein Erbe als persönliches oder gemeinsames Geld gesehen wird. Was wäre fair?", "Herkunft des Geldes respektieren", "Gemeinsame Ziele freiwillig besprechen", "Klare Grenze zwischen privat und gemeinsam", "Keine Erwartungen ohne Absprache"),
            q("Zum Erbe gehört eine Immobilie, die einer behalten und der andere verkaufen würde. Was tut ihr?", "Nutzung und Kosten durchrechnen", "Emotionale Bedeutung besprechen", "Entscheidung nicht überstürzen", "Mehrere realistische Optionen vergleichen"),
            q("Verwandte äußern starke Erwartungen an die Verwendung des Erbes. Was schützt ihr zuerst?", "Eigene Entscheidungshoheit", "Familiären Frieden", "Klare Kommunikation", "Zeit ohne Druck"),
            q("Was wäre bei einem Erbe als Paar am problematischsten?", "Unausgesprochene Ansprüche", "Geheime Entscheidungen", "Druck von außen", "Zu schnelle große Ausgaben")
        ),
        "h500_207_altersvorsorge_geheime_wahl" to listOf(
            q("Was beruhigt dich beim Gedanken an später heimlich am meisten?", "Genug finanzieller Puffer", "Geringe laufende Kosten", "Eigene vier Wände", "Flexibel bleiben können"),
            q("Was wäre dein persönlicher Wunsch für die Zeit nach dem Berufsleben?", "Viel reisen", "Ruhiger Alltag", "Eigene Projekte", "Mehr Zeit für Familie und Freunde"),
            q("Welche Unsicherheit beschäftigt dich bei Altersvorsorge am ehesten?", "Lebenshaltungskosten", "Gesundheitskosten", "Wie lange Geld reichen muss", "Zu wenig früh geplant zu haben"),
            q("Was würdest du bei Vorsorge lieber vermeiden?", "Etwas unterschreiben, das ich nicht verstehe", "Alles auf eine einzige Idee setzen", "Zu wenig flexibel bleiben", "Das Thema jahrelang verdrängen"),
            q("Was wäre dir bei gemeinsamer Vorsorge am wichtigsten?", "Beide kennen den Überblick", "Eigene Vorsorge bleibt sichtbar", "Gemeinsame Ziele sind klar", "Regelmäßig neu besprechen"),
            q("Was wäre später für dich das stärkste Gefühl von finanzieller Freiheit?", "Nicht jeden Euro rechnen müssen", "Wohnort frei wählen", "Arbeitszeit selbst bestimmen", "Andere unterstützen können")
        ),
        "h500_210_finanzielle_gespraeche_offene_runde" to listOf(
            open("Welches Geldthema fällt dir am schwersten offen anzusprechen – Einkommen, Ausgaben, Schulden, Sparen oder etwas anderes?"),
            open("Was müsste dein Partner tun, damit sich ein Gespräch über Geld für dich sicher statt kontrollierend anfühlt?"),
            open("Welche finanzielle Information sollte in einer festen Beziehung niemals bewusst verschwiegen werden?"),
            open("Wie möchtest du über Schulden oder finanzielle Verpflichtungen sprechen, ohne dass daraus sofort Schuldzuweisungen entstehen?"),
            open("Wann wäre für dich der richtige Zeitpunkt für ein ernstes gemeinsames Finanzgespräch?"),
            open("Welche eine Geldregel würde euch deiner Meinung nach langfristig die meisten unnötigen Konflikte ersparen?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map { pack ->
        if (pack.id in overrides) pack.copy(questions = overrides.getValue(pack.id)) else pack
    }
}
