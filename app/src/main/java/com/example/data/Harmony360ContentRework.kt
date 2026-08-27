package com.example.data

/**
 * Quality pass for generated Harmony 360 content.
 *
 * Older generated packs reused a small number of generic answer quartets across unrelated
 * subjects. The original generated files stay untouched, while obvious filler is replaced
 * deterministically at load time with shorter, coherent prompts. The visibly affected
 * Work & Career packs are explicitly rewritten so topic/answer coherence does not depend on a
 * generic vocabulary rotation.
 */
object Harmony360ContentRework {

    private val overusedOptionSets = listOf(
        setOf("Sicherheit", "Freiheit", "Abenteuer", "Komfort"),
        setOf("Jetzt genießen", "Langfristig planen", "Risiko eingehen", "Flexibel bleiben"),
        setOf("Karriere", "Familie", "Ausgewogen", "Sehr unabhängig"),
        setOf("Sofort ansprechen", "Erst fühlen", "Nähe suchen", "Raum geben"),
        setOf("Nähe", "Freiheit", "Humor", "Sicherheit"),
        setOf("Eine Umarmung", "Ein ehrliches Gespräch", "Gemeinsame Zeit", "Eine Überraschung"),
        setOf("Spontan", "Ritual", "Große Geste", "Kleine Geste"),
        setOf("Nähe", "Freiheit", "Sicherheit", "Abenteuer"),
        setOf("Spontan", "Geplant", "Vertraut", "Etwas völlig Neues"),
        setOf("Mehr Mut", "Mehr Gefühl", "Mehr Humor", "Mehr Konsequenz"),
        setOf("Druck", "Desinteresse", "Unklarheit", "Zu viel Kontrolle"),
        setOf("Zeit", "Persönliche Geste", "Überraschung", "Volle Aufmerksamkeit"),
        setOf("Gelassener als gedacht", "Mutiger als gedacht", "Sensibler als gedacht", "Spontaner als gedacht"),
        setOf("Vorfreude", "Nähe", "Neugier", "Anspannung"),
        setOf("Sehr unsicher", "Eher unsicher", "Ziemlich sicher", "Fast sicher"),
        setOf("Ruhe", "Nähe", "Abenteuer", "Überraschung"),
        setOf("Die sichere Wahl", "Die mutige Wahl", "Die romantische Wahl", "Die völlig verrückte Wahl"),
        setOf("Mehr Zeit", "Mehr Aufmerksamkeit", "Mehr Komfort", "Mehr Freiheit"),
        setOf("Kaum", "Ein bisschen", "Deutlich", "Extrem"),
        setOf("Planung", "Initiative", "Entscheidung", "Überraschung"),
        setOf("Etwas Neues", "Etwas Mutigeres", "Etwas Persönlicheres", "Etwas Ungeplanteres"),
        setOf("Routine", "Perfektion", "Erwartungen anderer", "Zu viel Planung"),
        setOf("Eine kleine persönliche Geste", "Ein großer unerwarteter Plan", "Ein mutiger erster Schritt", "Etwas nur für euch zwei"),
        setOf("Mehr Zeit", "Mehr Energie", "Mehr Freiheit", "Mehr Besonderheit"),
        setOf("Wir-Gefühl", "Persönlicher Wunsch", "Leichtigkeit", "Verlässlichkeit")
    )

    fun apply(pack: GenPack): GenPack {
        if ("harmony360" !in pack.tags) return pack

        workAndCareerOverrides[pack.id]?.let { questions ->
            return pack.copy(questions = questions)
        }

        val section = sectionNumber(pack) ?: return pack
        val vocabulary = sectionVocabulary[section] ?: return pack
        val subject = pack.title.substringBefore(" – ").trim()
        val offset = positiveHash(pack.id) % vocabulary.size
        var changed = false

        val questions = pack.questions.mapIndexed { index, question ->
            if (!shouldContextualize(pack, question.options)) {
                question
            } else {
                changed = true
                GenQuestion(
                    q = contextualPrompt(pack.cat, subject, index),
                    options = contextualOptions(vocabulary, offset, index),
                    defaultMine = question.defaultMine
                )
            }
        }

        return if (changed) pack.copy(questions = questions) else pack
    }

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private val workAndCareerOverrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_211_arbeitszeiten_entweder_oder" to listOf(
            q("Welche Arbeitszeit passt am besten zu deinem Alltag?", "Früh starten", "Spät starten", "Gleitzeit", "Vier-Tage-Woche"),
            q("Was wäre bei besseren Arbeitszeiten dein größter Gewinn?", "Mehr freie Abende", "Ausschlafen", "Planbare Freizeit", "Längeres Wochenende"),
            q("Welchen Tausch würdest du bei Arbeitszeiten am ehesten machen?", "Früher Start für frühen Feierabend", "Später Start für späten Feierabend", "Längere Tage für freien Freitag", "Wechselnde Zeiten für mehr Flexibilität"),
            q("Was wäre für eure Beziehung bei Arbeitszeiten am angenehmsten?", "Gemeinsamer Feierabend", "Gemeinsame Wochenenden", "Planbare Termine", "Spontane freie Zeit"),
            q("Was stört dich bei Arbeitszeiten am meisten?", "Schichtwechsel", "Kurzfristige Änderungen", "Sehr frühes Aufstehen", "Späte Feierabende"),
            q("Was wäre dir bei einem neuen Job wichtiger?", "Gleitzeit", "Homeoffice-Tage", "Feste Zeiten", "Vier-Tage-Woche"),
            q("Welche Arbeitszeit-Grenze würdest du ungern überschreiten?", "Regelmäßig nachts arbeiten", "Jedes Wochenende arbeiten", "Ständig erreichbar sein", "Oft spontan länger bleiben"),
            q("Welche Lösung würdest du als Paar bevorzugen?", "Ähnliche Arbeitszeiten", "Bewusst versetzte Zeiten", "Fixe gemeinsame Abende", "Flexible Woche planen")
        ),
        "h500_214_selbststaendigkeit_ranking" to listOf(
            q("Was wäre dir bei Selbstständigkeit am wichtigsten?", "Eigene Entscheidungen", "Flexible Zeit", "Finanzielle Chancen", "Sinnvolle Arbeit"),
            q("Welche Voraussetzung müsste für dich zuerst stimmen?", "Finanzielles Polster", "Gute Geschäftsidee", "Erste Kunden", "Unterstützung im Umfeld"),
            q("Was würdest du trotz Selbstständigkeit am stärksten schützen?", "Privatzeit", "Gesundheit", "Beziehung", "Finanzielle Stabilität"),
            q("Was reizt dich an Selbstständigkeit am meisten?", "Eigene Vision", "Kein Chef", "Wachstum", "Ortsfreiheit"),
            q("Was wäre für dich die größte Hürde?", "Unsicheres Einkommen", "Bürokratie", "Kundengewinnung", "Volle Verantwortung"),
            q("Wofür würdest du zuerst Geld investieren?", "Produkt oder Angebot", "Marketing", "Ausstattung", "Weiterbildung"),
            q("Was müsste dein Partner über deine Selbstständigkeit verstehen?", "Warum ich es will", "Wie viel Zeit es kostet", "Welches Risiko okay ist", "Wann ich Unterstützung brauche"),
            q("Was würde langfristig über Erfolg entscheiden?", "Gute Kunden", "Gesunde Finanzen", "Motivation", "Zeit für Privatleben")
        ),
        "h500_215_berufliche_veraenderung_prognose" to listOf(
            q("Was wäre deinem Partner bei einem Jobwechsel vermutlich am wichtigsten?", "Besseres Gehalt", "Mehr freie Zeit", "Spannendere Aufgaben", "Sicherer Vertrag"),
            q("Was würde deinen Partner am ehesten zu einem Wechsel bewegen?", "Schlechte Führung", "Keine Entwicklung", "Top-Angebot", "Zu wenig Privatleben"),
            q("Welches Risiko würde dein Partner am ehesten eingehen?", "Probezeit", "Neue Branche", "Weniger Gehalt für mehr Zeit", "Weiterer Arbeitsweg"),
            q("Was würde dein Partner bei einem neuen Job zuerst prüfen?", "Team", "Aufgaben", "Gehalt", "Arbeitszeiten"),
            q("Was würde deinem Partner beim Wechsel am meisten Sicherheit geben?", "Unterschriebener Vertrag", "Finanzpolster", "Deine Unterstützung", "Guter Plan B"),
            q("Wie würde dein Partner die Entscheidung eher treffen?", "Schnell aus dem Bauch", "Nach Zahlen und Fakten", "Nach mehreren Gesprächen", "Erst mit dir gemeinsam"),
            q("Was wäre für deinen Partner ein klares Nein?", "Toxisches Team", "Dauernde Überstunden", "Unklare Rolle", "Deutlich weniger Freizeit"),
            q("Womit könnte dein Partner dich beim Jobwechsel überraschen?", "Mehr Mut als gedacht", "Mehr Sicherheitsdenken", "Kompletter Branchenwechsel", "Plötzliches Ja")
        ),
        "h500_216_work_life_balance_szenario" to listOf(
            q("Ein wichtiger privater Termin kollidiert plötzlich mit Arbeit. Was tust du?", "Termin absagen", "Arbeit bewusst beenden", "Partner kurz anrufen", "Später Zeit nachholen"),
            q("Eine Deadline frisst euren gemeinsamen Abend. Wie reagierst du?", "Klare Endzeit setzen", "Abend verschieben", "Nur das Nötigste erledigen", "Partner mitentscheiden lassen"),
            q("Dein Partner sagt, du bist körperlich da, aber gedanklich noch bei der Arbeit. Was machst du?", "Handy weglegen", "Kurz erzählen und abschließen", "Gemeinsam rausgehen", "Erst 20 Minuten runterkommen"),
            q("Am freien Wochenende ruft der Chef an. Was ist dein erster Schritt?", "Nicht rangehen", "Kurz Dringlichkeit klären", "Später zurückrufen", "Nur bei echtem Notfall helfen"),
            q("Einer verdient mehr, hat aber fast keine freie Zeit. Was würdet ihr zuerst ändern?", "Arbeitszeit reduzieren", "Aufgaben zuhause neu verteilen", "Gemeinsame Zeit blocken", "Finanzielles Ziel prüfen"),
            q("Eine Dienstreise fällt auf einen wichtigen Paartermin. Was würdest du tun?", "Reise verschieben", "Paartermin verschieben", "Gemeinsame Alternative planen", "Priorität gemeinsam entscheiden"),
            q("Nach einer extremen Arbeitswoche seid ihr beide leer. Was hilft euch am meisten?", "Komplett nichts planen", "Gemeinsam essen", "Getrennt erholen", "Kurzer Ausflug"),
            q("Ein neuer Job bringt mehr Verantwortung, aber weniger Zeit. Was prüfst du zuerst?", "Gehalt", "Arbeitsstunden", "Entwicklungschance", "Auswirkung auf unser Leben")
        ),
        "h500_217_geheimnis_arbeitsplatz_geheime_wahl" to listOf(
            q("Welches Jobthema würdest du deinem Partner am ehesten erst spät erzählen?", "Wechselgedanken", "Gehaltswunsch", "Konflikt mit dem Chef", "Bewerbung woanders"),
            q("Was würdest du aus dem Büro am liebsten nie mit nach Hause nehmen?", "Lästereien", "Chef-Drama", "Konkurrenz", "Dauerstress"),
            q("Welche Information über deinen Job ist für dich am privatesten?", "Gehalt", "Fehler", "Konflikte", "Karrierepläne"),
            q("Was würdest du deinem Partner trotzdem immer sofort sagen?", "Job ist gefährdet", "Mobbing", "Überlastung", "Heftiger Konflikt"),
            q("Bei welchem Thema brauchst du erst Zeit, bevor du darüber redest?", "Kritik an mir", "Unsicherheit im Job", "Neid im Team", "Zweifel an der Karriere"),
            q("Was dürfte dein Partner von deinem Arbeitsplatz ruhig genauer wissen?", "Teamdynamik", "Chef-Verhältnis", "Arbeitsbelastung", "Zukunftschancen"),
            q("Welches Büro-Geheimnis wäre für dich noch harmlos?", "Überraschungsparty", "Interner Running Gag", "Kleine Panne", "Geplantes Geschenk fürs Team"),
            q("Wo endet für dich Privatsphäre und beginnt wichtiges Paarwissen?", "Geldrisiko", "Gesundheitsstress", "Jobwechsel", "Konflikt mit Folgen")
        ),
        "h500_219_berufliche_ziele_prioritaet" to listOf(
            q("Was steht bei deinen beruflichen Zielen ganz oben?", "Mehr Verantwortung", "Mehr Einkommen", "Mehr Freiheit", "Mehr Sinn"),
            q("Welche Entwicklung würde dich beruflich am meisten reizen?", "Fachlich besser werden", "Führung übernehmen", "Eigenes Projekt", "Branche wechseln"),
            q("Was darf für Karriereziele niemals dauerhaft leiden?", "Zeit zu zweit", "Gesundheit", "Wohnort", "Familienpläne"),
            q("Was wäre dir bei einem Karriereschritt am wichtigsten?", "Bessere Aufgaben", "Besseres Team", "Bessere Bezahlung", "Bessere Arbeitszeiten"),
            q("Wobei würdest du am ehesten Kompromisse machen?", "Titel", "Bürostandort", "Reisetage", "Tempo der Karriere"),
            q("Welche Art Erfolg bedeutet dir am meisten?", "Anerkennung", "Fachliche Stärke", "Finanzielle Freiheit", "Einfluss gestalten"),
            q("Was würdest du für ein großes Ziel am wenigsten opfern?", "Beziehung", "Gesundheit", "Freizeit", "Eigene Werte"),
            q("Was soll am Ende deiner Karriere übrig bleiben?", "Stolz auf die Arbeit", "Finanzielle Sicherheit", "Gute Erinnerungen", "Genug Zeit fürs Leben")
        ),
        "h500_221_nebenjob_entweder_oder" to listOf(
            q("Was wäre für dich der beste Grund für einen Nebenjob?", "Mehr Geld", "Spaßprojekt", "Neue Fähigkeiten", "Später selbstständig"),
            q("Wann wäre ein Nebenjob für dich am ehesten okay?", "Nur zeitweise", "Am Wochenende", "Abends", "Nur völlig flexibel"),
            q("Was dürfte ein Nebenjob auf keinen Fall kosten?", "Schlaf", "Zeit zu zweit", "Leistung im Hauptjob", "Erholung"),
            q("Welche Art Nebenjob würde am ehesten zu dir passen?", "Freelance online", "Kreativprojekt", "Gastro oder Event", "Handwerk oder Service"),
            q("Wofür würdest du das Extra-Geld am liebsten nutzen?", "Reise", "Rücklagen", "Große Anschaffung", "Eigenes Business"),
            q("Was wäre dir beim Nebenjob wichtiger?", "Hoher Stundenlohn", "Spaß", "Etwas lernen", "Freie Zeiteinteilung"),
            q("Wann würdest du einen Nebenjob wieder beenden?", "Wenn die Beziehung leidet", "Wenn der Schlaf leidet", "Wenn der Hauptjob leidet", "Wenn das Ziel erreicht ist"),
            q("Wie sollte dein Partner mit deinem Nebenjob umgehen?", "Aktiv unterstützen", "Neutral akzeptieren", "Mitentscheiden", "Mir freie Hand lassen")
        ),
        "h500_224_arbeitsweg_ranking" to listOf(
            q("Was ist dir auf dem Arbeitsweg am wichtigsten?", "Kurze Fahrzeit", "Wenig Umstiege", "Geringe Kosten", "Bequemer Weg"),
            q("Wie würdest du am liebsten zur Arbeit kommen?", "Zu Fuß", "Mit dem Fahrrad", "Mit Bus & Bahn", "Mit dem Auto"),
            q("Was nervt dich auf dem Arbeitsweg am meisten?", "Stau", "Verspätungen", "Volle Verkehrsmittel", "Parkplatzsuche"),
            q("Was macht einen guten Arbeitsweg für dich aus?", "Zuverlässigkeit", "Direkte Verbindung", "Flexibilität", "Ruhe unterwegs"),
            q("Was würdest du für einen deutlich kürzeren Arbeitsweg am ehesten ändern?", "Verkehrsmittel", "Arbeitszeiten", "Wohnort", "Arbeitsplatz"),
            q("Was ist dir morgens auf dem Weg zur Arbeit wichtiger?", "Mehr Schlaf", "Kein Umsteigen", "Frische Luft", "Planbare Ankunft"),
            q("Welcher kleine Luxus würde deinen Arbeitsweg am meisten verbessern?", "Sicherer Sitzplatz", "Gute Musik oder Podcast", "Kaffee unterwegs", "Kein Zeitdruck"),
            q("Was sollte euer Alltag trotz Arbeitsweg am meisten schützen?", "Zeit zu zweit", "Energie nach Feierabend", "Pünktlichkeit", "Spontane Pläne")
        ),
        "h500_225_ruhestand_prognose" to listOf(
            q("Was würde dein Partner im Ruhestand vermutlich am meisten genießen?", "Reisen", "Familie", "Hobbys", "Ruhe genießen"),
            q("Wann würde dein Partner am liebsten kürzertreten?", "So früh wie möglich", "Schrittweise Teilzeit", "Zum regulären Rentenalter", "Solange Arbeit Spaß macht"),
            q("Wofür würde dein Partner im Ruhestand morgens gern aufstehen?", "Reisen planen", "Familie oder Enkel", "Eigenes Projekt", "Sport und Natur"),
            q("Wo würde dein Partner später am liebsten leben?", "Am Meer", "In der Stadt", "Auf dem Land", "Zwischen mehreren Orten"),
            q("Was gäbe deinem Partner im Ruhestand finanziell am meisten Ruhe?", "Feste Rente", "Eigene Rücklagen", "Abbezahltes Zuhause", "Geringe laufende Kosten"),
            q("Wie viel Struktur würde dein Partner im Ruhestand wollen?", "Fester Wochenplan", "Ein paar Rituale", "Spontan leben", "Neue Aufgaben suchen"),
            q("Was würde dein Partner von der Arbeit am ehesten vermissen?", "Kollegen", "Aufgabe und Sinn", "Tagesstruktur", "Einkommen"),
            q("Womit könnte dein Partner dich im Ruhestand überraschen?", "Noch einmal studieren", "Lange reisen", "Ehrenamt", "Kleines Business")
        ),
        "h500_226_kuendigung_szenario" to listOf(
            q("Dein Partner kommt nach Hause und wurde gekündigt. Was tust du zuerst?", "Sofort zuhören", "Finanzen prüfen", "Erst einmal auffangen", "Nächste Schritte planen"),
            q("Dein Partner will selbst kündigen, hat aber noch keinen neuen Job. Wie reagierst du?", "Erst Gründe verstehen", "Finanzplan machen", "Bei Bewerbungen helfen", "Um Bedenkzeit bitten"),
            q("Die Kündigung trifft euch an einem ohnehin schlechten Tag. Was hilft jetzt?", "Abend freihalten", "Lieblingsessen organisieren", "Einfach zuhören", "Spaziergang machen"),
            q("Es gibt eine Abfindung. Was wäre euer erster sinnvoller Schritt?", "Rücklage bilden", "Kurze Auszeit", "Weiterbildung", "Direkt neue Jobsuche"),
            q("Ein neuer Job bietet weniger Geld, aber deutlich mehr Lebensqualität. Was prüfst du?", "Lebensqualität vergleichen", "Budget rechnen", "Entwicklungschance prüfen", "Bauchgefühl ernst nehmen"),
            q("Familie und Freunde geben ungefragt Ratschläge zur Kündigung. Was macht ihr?", "Grenzen setzen", "Anhören, selbst entscheiden", "Partner abschirmen", "Thema wechseln"),
            q("Wochen später ist noch keine neue Stelle da. Was wäre jetzt sinnvoll?", "Suchstrategie ändern", "Kurze Pause einlegen", "Netzwerk aktivieren", "Professionelle Hilfe holen"),
            q("Was sollte eine Kündigungsphase euch als Paar am besten zeigen?", "Wir sind ein Team", "Geld ist planbar", "Arbeit ist nicht alles", "Veränderung kann Chance sein")
        ),
        "h500_227_kollegen_geheime_wahl" to listOf(
            q("Welche Veränderung im Umgang mit Kollegen würdest du dir heimlich wünschen?", "Mehr Abstand", "Mehr Freundschaft", "Mehr Teamgefühl", "Klare Grenzen"),
            q("Was würdest du bei Kollegen lieber vermeiden?", "Private Details teilen", "Lästern", "Flirten", "Jobkonflikte nach Hause tragen"),
            q("Welche Art Kontakt mit Kollegen passt am besten zu dir?", "Nur beruflich", "Mittagspause zusammen", "Afterwork ab und zu", "Echte Freundschaft"),
            q("Welche Situation mit Kollegen wäre dir am unangenehmsten?", "Chef folgt Social Media", "Kollege schreibt spät", "Team kennt Beziehungsdetails", "Privater Streit wird Thema"),
            q("Welche Grenze ist dir bei Kollegen am wichtigsten?", "Keine privaten Chats nachts", "Keine Geheimnisse", "Keine Flirts", "Privatleben bleibt privat"),
            q("Was erzählst du deinem Partner über Kollegen am ehesten?", "Lustige Geschichten", "Konflikte", "Erfolge", "Fast nichts"),
            q("Was macht für dich ein gutes Team aus?", "Vertrauen", "Humor", "Kompetenz", "Hilfsbereitschaft"),
            q("Welche Entwicklung mit Kollegen würde dich positiv überraschen?", "Kollegen werden Freunde", "Komplett neues Team", "Gemeinsamer Trip", "Partner lernt das Team kennen")
        )
    )

    private fun shouldContextualize(pack: GenPack, options: List<String>): Boolean {
        if (options.size != 4 || overusedOptionSets.none { it == options.toSet() }) return false
        return pack.cat in setOf("h360_ranking", "h360_prognose", "h360_geheim", "h360_prioritaet", "tot")
    }

    private fun sectionNumber(pack: GenPack): Int? {
        val tag = pack.tags.firstOrNull { it.startsWith("h360_section_") } ?: return null
        return Regex("h360_section_(\\d{2})_").find(tag)?.groupValues?.getOrNull(1)?.toIntOrNull()
    }

    private fun positiveHash(value: String): Int = value.hashCode().let {
        if (it == Int.MIN_VALUE) 0 else kotlin.math.abs(it)
    }

    private fun contextualOptions(vocabulary: List<String>, offset: Int, questionIndex: Int): List<String> {
        val start = (offset + questionIndex * 3) % vocabulary.size
        return (0 until 4).map { vocabulary[(start + it) % vocabulary.size] }
    }

    private fun contextualPrompt(cat: String, subject: String, index: Int): String = when (cat) {
        "h360_prognose" -> when (index % 4) {
            0 -> "Was wäre deinem Partner bei „$subject“ vermutlich am wichtigsten?"
            1 -> "Welche Seite von „$subject“ passt am ehesten zu deinem Partner?"
            2 -> "Womit könnte dein Partner dich bei „$subject“ überraschen?"
            else -> "Was würde dein Partner bei „$subject“ wahrscheinlich zuerst wählen?"
        }
        "h360_geheim" -> when (index % 4) {
            0 -> "Was reizt dich bei „$subject“ heimlich am meisten?"
            1 -> "Was würdest du bei „$subject“ wählen, wenn niemand urteilt?"
            2 -> "Wovon hättest du bei „$subject“ insgeheim gern mehr?"
            else -> "Welche Seite von „$subject“ würdest du gern öfter ausleben?"
        }
        "h360_prioritaet" -> when (index % 4) {
            0 -> "Was hat bei „$subject“ für dich Vorrang?"
            1 -> "Was darf bei „$subject“ niemals zu kurz kommen?"
            2 -> "Was würdest du bei „$subject“ zuerst schützen?"
            else -> "Was ist bei „$subject“ für dich am wenigsten verhandelbar?"
        }
        "tot" -> when (index % 4) {
            0 -> "Was passt bei „$subject“ eher zu dir?"
            1 -> "Was würdest du bei „$subject“ spontan wählen?"
            2 -> "Welche Seite von „$subject“ spricht dich stärker an?"
            else -> "Was gewinnt bei „$subject“ aus dem Bauch heraus?"
        }
        else -> rankingPrompt(subject, index)
    }

    private fun rankingPrompt(subject: String, index: Int): String = when (index % 8) {
        0 -> "Was zählt für dich bei „$subject“ am meisten?"
        1 -> "Was darf bei „$subject“ auf keinen Fall zu kurz kommen?"
        2 -> "Worauf würdest du bei „$subject“ zuerst achten?"
        3 -> "Was macht „$subject“ für dich wirklich gut?"
        4 -> "Was wäre bei „$subject“ dein stärkster Pluspunkt?"
        5 -> "Was sollte dein Partner über deine Prioritäten bei „$subject“ wissen?"
        6 -> "Was würdest du bei „$subject“ am wenigsten opfern wollen?"
        else -> "Was hat bei „$subject“ langfristig den höchsten Stellenwert?"
    }

    private val sectionVocabulary: Map<Int, List<String>> = mapOf(
        1 to listOf("Zärtlichkeit", "Vertrauen", "Gemeinsame Zeit", "Aufmerksamkeit", "Geborgenheit", "Humor", "Freiraum", "Offenheit", "Spontanität", "Rituale", "Körpernähe", "Verlässlichkeit"),
        2 to listOf("Direktheit", "Zuhören", "Geduld", "Humor", "Klare Worte", "Nachfragen", "Ruhe", "Ehrlichkeit", "Timing", "Empathie", "Lösungen", "Offenheit"),
        3 to listOf("Gemeinsame Ziele", "Wohnort", "Familie", "Karriere", "Finanzielle Sicherheit", "Freiheit", "Abenteuer", "Planbarkeit", "Flexibilität", "Eigenes Zuhause", "Zeit zu zweit", "Persönliche Entwicklung"),
        4 to listOf("Natur", "Kultur", "Gutes Essen", "Abenteuer", "Entspannung", "Komfort", "Spontanität", "Planung", "Lokale Erlebnisse", "Meer", "Berge", "Neue Städte"),
        5 to listOf("Geschmack", "Qualität", "Atmosphäre", "Preis", "Abwechslung", "Gemeinsam kochen", "Neue Küchen", "Lieblingsgerichte", "Frische Zutaten", "Dessert", "Gemütlichkeit", "Spontane Genussmomente"),
        6 to listOf("Ordnung", "Ruhe", "Gemeinsame Zeit", "Rückzug", "Aufgabenteilung", "Spontanität", "Sauberkeit", "Gemütlichkeit", "Feste Routinen", "Flexibilität", "Privatsphäre", "Kleine Rituale"),
        7 to listOf("Bewegung", "Kreativität", "Entspannung", "Neue Erlebnisse", "Freunde", "Natur", "Musik", "Gaming", "Lernen", "Sport", "Kultur", "Zeit zu zweit"),
        8 to listOf("Loyalität", "Nähe", "Klare Grenzen", "Gemeinsame Zeit", "Unterstützung", "Ehrlichkeit", "Traditionen", "Freiraum", "Verlässlichkeit", "Humor", "Respekt", "Zusammenhalt"),
        9 to listOf("Rücklagen", "Genuss", "Investieren", "Schuldenfreiheit", "Gemeinsame Ziele", "Eigenes Budget", "Sicherheit", "Spontane Ausgaben", "Transparenz", "Vorsorge", "Große Wünsche", "Unabhängigkeit"),
        10 to listOf("Gutes Gehalt", "Flexible Arbeitszeit", "Sinnvolle Aufgaben", "Entwicklung", "Stabilität", "Eigenständigkeit", "Gutes Team", "Anerkennung", "Work-Life-Balance", "Verantwortung", "Kurzer Arbeitsweg", "Planbarkeit"),
        11 to listOf("Schlaf", "Bewegung", "Ernährung", "Entspannung", "Vorsorge", "Mentale Ruhe", "Alltagsbewegung", "Erholung", "Routine", "Motivation", "Gemeinsame Aktivität", "Zeit für sich"),
        12 to listOf("Zuhören", "Respekt", "Klare Worte", "Pause machen", "Nähe danach", "Entschuldigung", "Kompromiss", "Grenzen", "Ruhe", "Verständnis", "Lösung", "Vergebung"),
        13 to listOf("Ehrlichkeit", "Loyalität", "Freiheit", "Verantwortung", "Mitgefühl", "Mut", "Verlässlichkeit", "Neugier", "Gerechtigkeit", "Respekt", "Authentizität", "Bescheidenheit"),
        14 to listOf("Starke Geschichten", "Musik", "Atmosphäre", "Humor", "Spannung", "Emotionen", "Visuelle Welt", "Originalität", "Nostalgie", "Live-Erlebnis", "Gemeinsames Entdecken", "Lieblingsfiguren"),
        15 to listOf("Persönlicher Sinn", "Gemeinschaft", "Tradition", "Rituale", "Offene Fragen", "Toleranz", "Spiritualität", "Familie", "Freiheit", "Hoffnung", "Dankbarkeit", "Werte"),
        16 to listOf("Freiheit", "Fairness", "Sicherheit", "Verantwortung", "Chancengleichheit", "Solidarität", "Privatsphäre", "Bildung", "Umweltschutz", "Mitbestimmung", "Respekt", "Zusammenhalt"),
        17 to listOf("Verstanden werden", "Nähe", "Ruhe", "Offenheit", "Bestätigung", "Freiraum", "Zuhören", "Körperkontakt", "Humor", "Geduld", "Ehrlichkeit", "Sicherheit"),
        18 to listOf("Wortwitz", "Situationskomik", "Albernheit", "Selbstironie", "Trockener Humor", "Insider", "Memes", "Schwarzer Humor", "Spontane Sprüche", "Peinliche Momente", "Running Gags", "Lachanfälle"),
        19 to listOf("Magie", "Entdeckung", "Abenteuer", "Neue Welten", "Zeitreisen", "Superkräfte", "Geheimnisse", "Weltraum", "Parallelwelten", "Erfindungen", "Unbekannte Wesen", "Grenzenlose Freiheit"),
        20 to listOf("Ideen", "Planung", "Umsetzung", "Qualitätscheck", "Tempo", "Kommunikation", "Improvisation", "Verantwortung", "Motivation", "Entscheidungen", "Problemlösung", "Zusammenhalt")
    )
}
