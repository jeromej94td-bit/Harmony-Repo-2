package com.example.data

/** Explicit Stage 05.3 curation for Harmony-360 Section 08 — Freunde & Familie. */
object Harmony360FriendsFamilySectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_171_paarabende_entweder_oder" to CurationDecision.REWRITE,
        "h500_172_spieleabende_wer_eher" to CurationDecision.REWRITE,
        "h500_173_party_skala" to CurationDecision.REWRITE,
        "h500_174_familientreffen_ranking" to CurationDecision.REWRITE,
        "h500_175_schwiegereltern_prognose" to CurationDecision.REWRITE,
        "h500_176_beste_freunde_szenario" to CurationDecision.REWRITE,
        "h500_177_alte_freunde_geheime_wahl" to CurationDecision.REWRITE,
        "h500_178_neue_leute_memory" to CurationDecision.REWRITE,
        "h500_179_nachbarn_prioritaet" to CurationDecision.REWRITE,
        "h500_180_konflikte_im_umfeld_offene_runde" to CurationDecision.REWRITE,
        "h500_181_geburtstage_entweder_oder" to CurationDecision.REWRITE,
        "h500_182_feiertage_wer_eher" to CurationDecision.REWRITE,
        "h500_183_traditionen_skala" to CurationDecision.REWRITE,
        "h500_184_geschenke_ranking" to CurationDecision.REWRITE,
        "h500_185_kinderbesuch_prognose" to CurationDecision.REWRITE,
        "h500_186_elternabende_szenario" to CurationDecision.REWRITE,
        "h500_187_familienurlaub_geheime_wahl" to CurationDecision.REWRITE,
        "h500_190_gemeinsamer_freundeskreis_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_171_paarabende_entweder_oder" to listOf(
            q("Wie verbringt ihr einen freien Paarabend eher?", "Gemütlich zuhause", "Bewusst ausgehen"),
            q("Was klingt eher nach einem guten Abend?", "Zu zweit etwas Neues testen", "Vertrautes Lieblingsritual"),
            q("Wenn Freunde fragen, ob ihr euch anschließt?", "Doppeldate mit Freunden", "Heute nur wir zwei"),
            q("Was ist dir eher wichtig?", "Vorher einen Plan haben", "Spontan entscheiden"),
            q("Wobei redet ihr wahrscheinlich mehr miteinander?", "Gemeinsam kochen", "Langer Spaziergang"),
            q("Nach einer anstrengenden Woche?", "Früh Ruhe machen", "Trotzdem noch etwas Besonderes erleben")
        ),
        "h500_172_spieleabende_wer_eher" to listOf(
            whoQ("Wer erklärt bei einem Spieleabend eher freiwillig die Regeln?"),
            whoQ("Wer wird bei einem knappen Spielstand eher ehrgeizig?"),
            whoQ("Wer schlägt eher ein kooperatives Spiel vor, damit alle zusammen gewinnen?"),
            whoQ("Wer merkt eher, wenn jemand am Tisch keine Lust mehr hat?"),
            whoQ("Wer würde eher noch eine letzte Runde durchsetzen, obwohl es spät ist?"),
            whoQ("Wer kann eine Niederlage eher sofort mit Humor nehmen?")
        ),
        "h500_173_party_skala" to listOf(
            scaleQ("Wie gern bist du auf Feiern mit vielen Menschen, die du kaum kennst?"),
            scaleQ("Wie wichtig ist dir, dass ihr auf einer Party viel Zeit miteinander verbringt?"),
            scaleQ("Wie leicht kommst du auf Feiern selbst mit neuen Leuten ins Gespräch?"),
            scaleQ("Wie sehr stört es dich, wenn einer deutlich früher nach Hause möchte?"),
            scaleQ("Wie gern würdest du auch einmal getrennt auf unterschiedliche Feiern gehen?"),
            scaleQ("Wie wichtig ist dir, am nächsten Tag noch gemeinsame Ruhezeit einzuplanen?")
        ),
        "h500_174_familientreffen_ranking" to listOf(
            q("Was macht ein Familientreffen für dich am wertvollsten? Ordne.", "Zeit für echte Gespräche", "Gemeinsam essen", "Traditionen erleben", "Alle einmal wiedersehen"),
            q("Was hilft dir bei großen Familientreffen am meisten? Ordne.", "Klare Anfangs- und Endzeit", "Rückzugsmöglichkeit", "Vertraute Person an der Seite", "Lockere Atmosphäre"),
            q("Welche Rolle übernimmst du eher? Ordne.", "Gespräche starten", "Praktisch mithelfen", "Zwischen Gruppen wechseln", "Eher beobachten"),
            q("Was sollte bei unterschiedlichen Familienkulturen zuerst zählen? Ordne.", "Respekt", "Offene Absprachen", "Fairer Wechsel", "Eigene Paargrenzen"),
            q("Was wäre für dich bei einem Treffen am schwierigsten? Ordne.", "Alte Konflikte", "Zu wenig Privatsphäre", "Erwartungsdruck", "Zu langer Besuch"),
            q("Was sollte nach einem guten Familientreffen hängen bleiben? Ordne.", "Nähe", "Neue Geschichten", "Entspannte Stimmung", "Vorfreude aufs Wiedersehen")
        ),
        "h500_175_schwiegereltern_prognose" to listOf(
            q("Wie viel Kontakt zu Schwiegereltern würde dein Partner vermutlich angenehm finden?", "Sehr regelmäßig", "Alle paar Wochen", "Zu besonderen Anlässen", "Eher selten"),
            q("Was wäre deinem Partner bei Besuchen wahrscheinlich am wichtigsten?", "Herzliche Atmosphäre", "Klare Zeitabsprachen", "Eigener Rückzugsraum", "Gemeinsame Aktivität"),
            q("Wie würde dein Partner bei ungefragten Ratschlägen eher reagieren?", "Direkt widersprechen", "Höflich stehen lassen", "Später mit dir besprechen", "Humorvoll umlenken"),
            q("Welche Grenzen wären deinem Partner gegenüber Schwiegereltern vermutlich am wichtigsten?", "Privatsphäre", "Eigene Entscheidungen", "Besuchszeiten", "Umgangston"),
            q("Was würde dein Partner von dir in einem unangenehmen Moment eher erwarten?", "Sofort Partei ergreifen", "Situation beruhigen", "Später klar Stellung beziehen", "Erst nachfragen, was gebraucht wird"),
            q("Was könnte das Verhältnis aus Sicht deines Partners am stärksten verbessern?", "Mehr gemeinsame Zeit", "Mehr direkte Gespräche", "Weniger Erwartungen", "Mehr gegenseitiges Verständnis")
        ),
        "h500_176_beste_freunde_szenario" to listOf(
            q("Dein bester Freund und dein Partner verstehen sich plötzlich gar nicht mehr. Was tust du zuerst?", "Mit beiden getrennt reden", "Gemeinsames Gespräch suchen", "Klare Grenzen setzen", "Erst beobachten, was wirklich passiert"),
            q("Ein bester Freund plant ständig spontan und dein Partner fühlt sich übergangen. Wie löst ihr es?", "Früher Bescheid geben", "Feste Paarzeit schützen", "Spontane Treffen begrenzen", "Je nach Woche entscheiden"),
            q("Ein enger Freund erzählt dir etwas Vertrauliches, das deinen Partner indirekt betrifft. Was ist fair?", "Vertraulichkeit halten", "Freund um Freigabe bitten", "Nur das Nötigste sagen", "Situation offen mit Freund klären"),
            q("Dein Partner fühlt sich bei Treffen mit deinem besten Freund außen vor. Was machst du?", "Mehr einbeziehen", "Treffen teilweise getrennt halten", "Offen nach dem Grund fragen", "Gemeinsame Aktivität wählen"),
            q("Ein bester Freund kritisiert eure Beziehung wiederholt. Wo ziehst du die Grenze?", "Sofort klar widersprechen", "Einmal ernsthaft besprechen", "Thema Beziehung ausklammern", "Kontakt vorübergehend reduzieren"),
            q("Ihr habt am selben Abend Paarpläne und ein Freund braucht dringend Unterstützung. Wie entscheidet ihr?", "Notfall geht vor", "Erst gemeinsam Lage klären", "Zeit aufteilen", "Andere Hilfe organisieren")
        ),
        "h500_177_alte_freunde_geheime_wahl" to listOf(
            q("Was wünschst du dir heimlich bei einem Wiedersehen mit alten Freunden?", "Sofort wieder Vertrautheit", "Neue Seiten entdecken", "Einfach viel lachen", "Alte Geschichten austauschen"),
            q("Was wäre dir bei alten Freundschaften am wichtigsten?", "Selten, aber tief verbunden", "Regelmäßig Kontakt", "Spontane Treffen", "Gemeinsame Tradition"),
            q("Was würdest du eher nur mit alten Freunden machen?", "Erinnerungsreise", "Langer Abend ohne Plan", "Alte Orte besuchen", "Fotos und Geschichten anschauen"),
            q("Was könnte dich bei einem Wiedersehen am meisten überraschen?", "Wie ähnlich ihr noch seid", "Wie unterschiedlich ihr geworden seid", "Wie schnell Nähe zurückkommt", "Wie wenig die Zeit zählt"),
            q("Was würdest du dir von deinem Partner bei solchen Treffen wünschen?", "Interesse an meinen Freunden", "Freiraum für mich", "Mitkommen und kennenlernen", "Später zuhören, wie es war"),
            q("Welche alte Freundschaft würdest du eher wiederbeleben?", "Eine sehr enge", "Eine lustige", "Eine inspirierende", "Eine, die ohne Streit eingeschlafen ist")
        ),
        "h500_178_neue_leute_memory" to listOf(
            GenQuestion("Wann hast du zuletzt jemanden kennengelernt, bei dem die Sympathie sofort da war?"),
            GenQuestion("Welche ungewöhnliche Situation hat dir einmal eine wichtige neue Freundschaft gebracht?"),
            GenQuestion("Bei welcher ersten Begegnung hast du dich komplett in jemandem getäuscht – positiv oder negativ?"),
            GenQuestion("Wer hat dich einmal besonders leicht in eine neue Gruppe hineingeholt?"),
            GenQuestion("Welche neue Bekanntschaft hat dir später eine völlig neue Welt, Idee oder Aktivität gezeigt?"),
            GenQuestion("Was war die schönste spontane Einladung von Menschen, die du damals kaum kanntest?")
        ),
        "h500_179_nachbarn_prioritaet" to listOf(
            q("Was ist dir bei Nachbarn am wichtigsten?", "Freundlicher Abstand", "Verlässliche Hilfe", "Kurze Gespräche", "Echte Gemeinschaft"),
            q("Was sollte im Haus oder in der Straße selbstverständlich sein?", "Rücksicht auf Ruhe", "Saubere Gemeinschaftsflächen", "Direkte Kommunikation", "Pakete füreinander annehmen"),
            q("Wobei würdest du Nachbarn am ehesten helfen?", "Kleine Alltagssache", "Notfall", "Urlaub und Pflanzen", "Werkzeug oder Gegenstände leihen"),
            q("Welche Grenze ist dir am wichtigsten?", "Keine unangekündigten Besuche", "Privates bleibt privat", "Keine dauernden Gefallen", "Ruhezeiten respektieren"),
            q("Was wäre bei einem Konflikt dein erster Schritt?", "Direkt freundlich ansprechen", "Erst abwarten", "Gemeinsame Lösung vorschlagen", "Hausverwaltung nur wenn nötig"),
            q("Was macht gute Nachbarschaft langfristig aus?", "Respekt", "Hilfsbereitschaft", "Verlässlichkeit", "Unkomplizierter Umgang")
        ),
        "h500_180_konflikte_im_umfeld_offene_runde" to listOf(
            GenQuestion("Wenn jemand aus Familie oder Freundeskreis euren Partner unfair behandelt: Wie wünschst du dir, dass ihr als Paar damit umgeht?"),
            GenQuestion("Wann sollte man sich in einen Konflikt des Partners mit seinem Umfeld einmischen – und wann eher nicht?"),
            GenQuestion("Welche Art Kritik von Freunden oder Familie an eurer Beziehung würdest du ernst nehmen?"),
            GenQuestion("Wie möchtest du damit umgehen, wenn du eine Person im Umfeld deines Partners wirklich nicht magst?"),
            GenQuestion("Welche Grenze gegenüber Familie oder Freunden sollte ein Partner niemals ohne Rücksprache überschreiten?"),
            GenQuestion("Was hilft euch am meisten, wenn ein Konflikt von außen plötzlich zwischen euch beiden landet?")
        ),
        "h500_181_geburtstage_entweder_oder" to listOf(
            q("Wie feierst du deinen eigenen Geburtstag lieber?", "Klein und persönlich", "Groß mit vielen Leuten"),
            q("Was bedeutet dir mehr?", "Geplante Feier", "Spontaner schöner Tag"),
            q("Was ist dir bei Glückwünschen lieber?", "Viele Nachrichten", "Wenige persönliche Worte"),
            q("Was würdest du eher wählen?", "Gemeinsames Erlebnis", "Materielles Geschenk"),
            q("Wenn der Geburtstag auf einen Arbeitstag fällt?", "Am selben Tag feiern", "Aufs Wochenende verschieben"),
            q("Bei der Planung durch den Partner?", "Überraschung", "Vorher gemeinsam abstimmen")
        ),
        "h500_182_feiertage_wer_eher" to listOf(
            whoQ("Wer plant Feiertage eher Wochen vorher?"),
            whoQ("Wer möchte eher möglichst viele Familienmitglieder an einem Tag sehen?"),
            whoQ("Wer achtet eher darauf, dass beide Familien fair berücksichtigt werden?"),
            whoQ("Wer würde eher eine alte Feiertagstradition verändern?"),
            whoQ("Wer braucht nach einem vollen Feiertag eher Ruhe nur für sich?"),
            whoQ("Wer würde eher vorschlagen, einen Feiertag einmal komplett nur zu zweit zu verbringen?")
        ),
        "h500_183_traditionen_skala" to listOf(
            scaleQ("Wie wichtig sind dir feste Familientraditionen über viele Jahre?"),
            scaleQ("Wie wichtig ist dir, Traditionen aus beiden Familien gleichberechtigt zu verbinden?"),
            scaleQ("Wie offen bist du dafür, Traditionen bewusst zu verändern, wenn sie nicht mehr zu euch passen?"),
            scaleQ("Wie sehr geben dir wiederkehrende Rituale ein Gefühl von Zuhause?"),
            scaleQ("Wie wichtig wäre dir eine ganz neue Tradition, die nur euch als Paar gehört?"),
            scaleQ("Wie leicht kannst du eine Tradition auslassen, wenn sie in einem Jahr Stress statt Freude macht?")
        ),
        "h500_184_geschenke_ranking" to listOf(
            q("Was macht ein Geschenk für dich wertvoll? Ordne.", "Persönliche Bedeutung", "Genaue Aufmerksamkeit", "Gemeinsame Erinnerung", "Praktischer Nutzen"),
            q("Welche Geschenkart freut dich am meisten? Ordne.", "Erlebnis", "Selbstgemachtes", "Etwas Gewünschtes", "Kleine Überraschung"),
            q("Was zählt bei einem Geschenk am wenigsten? Ordne vom größten Störfaktor.", "Hoher Preis", "Perfekte Verpackung", "Trendfaktor", "Beeindruckende Größe"),
            q("Was zeigt für dich am stärksten: Du kennst mich wirklich? Ordne.", "Ein Detail gemerkt", "Passenden Moment gewählt", "Persönliche Botschaft", "Etwas gemeinsam geplant"),
            q("Bei Geschenken für Familie und Freunde: Was priorisierst du? Ordne.", "Passend zur Person", "Im Budget bleiben", "Rechtzeitig kümmern", "Nicht aus Pflicht schenken"),
            q("Was wäre dir bei Geschenken als Paar am wichtigsten? Ordne.", "Kein Konkurrenzdenken", "Ehrliche Wünsche", "Gemeinsames Budgetgefühl", "Überraschung bleibt möglich")
        ),
        "h500_185_kinderbesuch_prognose" to listOf(
            q("Falls Kinder aus Familie oder Freundeskreis zu Besuch sind: Was wäre deinem Partner vermutlich am wichtigsten?", "Sicherer Rahmen", "Viel gemeinsame Beschäftigung", "Entspannte Regeln", "Genug Rückzug"),
            q("Wenn Kinder zu Besuch kommen: Welche Vorbereitung würde dein Partner wahrscheinlich zuerst machen?", "Wohnung kindersicher prüfen", "Essen und Getränke planen", "Spiel oder Aktivität bereitlegen", "Tagesablauf locker halten"),
            q("Falls ein Kind sehr schüchtern ist: Wie würde dein Partner vermutlich reagieren?", "Zeit geben", "Behutsam einbeziehen", "Eltern übernehmen lassen", "Ruhige Beschäftigung anbieten"),
            q("Wenn es beim Kinderbesuch laut und chaotisch wird: Was würde dein Partner eher tun?", "Gelassen bleiben", "Klare Grenze setzen", "Aktivität wechseln", "Kurze Ruhephase schaffen"),
            q("Falls Regeln der Eltern anders sind als eure Gewohnheiten: Was wäre deinem Partner wahrscheinlich wichtig?", "Elternregeln respektieren", "Vorher kurz absprechen", "Nur bei Sicherheit eingreifen", "Flexibel bleiben"),
            q("Wenn der Besuch vorbei ist: Was würde dein Partner wahrscheinlich zuerst brauchen?", "Ruhe", "Aufräumen", "Über den Tag reden", "Direkt etwas Eigenes machen")
        ),
        "h500_186_elternabende_szenario" to listOf(
            q("Falls Elternabende in eurem Leben relevant sind und beide Zeit haben: Wer geht hin?", "Abwechselnd", "Gemeinsam", "Wer das Thema besser kennt", "Wer zeitlich flexibler ist"),
            q("Wenn ein Elternabend mit einem wichtigen privaten Termin kollidiert: Wie entscheidet ihr?", "Bedeutung vergleichen", "Einer übernimmt Elternabend", "Privattermin verschieben", "Alternative Information organisieren"),
            q("Falls auf einem Elternabend ein schwieriges Thema auftaucht: Was wäre euer erster Schritt danach?", "In Ruhe zu zweit besprechen", "Direkt Rückfragen stellen", "Weitere Informationen sammeln", "Erst eine Nacht darüber schlafen"),
            q("Wenn ihr nach einem Elternabend unterschiedlich bewertet, was wichtig ist: Wie löst ihr es?", "Argumente sammeln", "Prioritäten festlegen", "Dritte Perspektive einholen", "Später erneut besprechen"),
            q("Falls nur einer beim Elternabend war: Wie sollte die Übergabe zuhause aussehen?", "Kurze Zusammenfassung", "Nur Entscheidungen nennen", "Notizen gemeinsam durchgehen", "Nur relevante Punkte teilen"),
            q("Wenn Elternabende euch regelmäßig stressen: Was würdet ihr verändern?", "Früher planen", "Zuständigkeit wechseln", "Nur relevante Termine besuchen", "Aufgaben danach klar teilen")
        ),
        "h500_187_familienurlaub_geheime_wahl" to listOf(
            q("Was wäre dein heimlicher Wunsch bei einem Familienurlaub?", "Mehr Zeit nur zu zweit", "Mehr gemeinsame Familienzeit", "Mehr eigene Auszeiten", "Mehr spontane Unternehmungen"),
            q("Welche Unterkunft würdest du heimlich bevorzugen?", "Eigenes Apartment", "Gemeinsames Ferienhaus", "Hotel mit Rückzug", "Mehrere Unterkünfte nah beieinander"),
            q("Was würdest du im Urlaub am liebsten selbst bestimmen?", "Tagesrhythmus", "Ausflüge", "Essen", "Wie viel Zeit ihr mit allen verbringt"),
            q("Was wäre dir bei einem längeren Familienurlaub am wichtigsten?", "Klare Absprachen", "Eigener Rückzugsraum", "Flexible Pläne", "Faire Kostenaufteilung"),
            q("Welche Situation würdest du am ehesten vermeiden wollen?", "Jeder macht alles gemeinsam", "Ungeklärte Erwartungen", "Streit über Geld", "Keine Zeit nur als Paar"),
            q("Was könnte einen Familienurlaub für dich besonders schön machen?", "Gemeinsames Lieblingsritual", "Ein besonderer Ausflug", "Entspannte Abende", "Neue gemeinsame Erinnerung")
        ),
        "h500_190_gemeinsamer_freundeskreis_offene_runde" to listOf(
            GenQuestion("Wie wichtig ist dir ein gemeinsamer Freundeskreis – und wie viel möchtest du bewusst getrennt halten?"),
            GenQuestion("Was macht für dich Menschen aus, mit denen ihr euch beide langfristig wohlfühlen könnt?"),
            GenQuestion("Wie sollte es laufen, wenn einer einen gemeinsamen Freund deutlich enger mag als der andere?"),
            GenQuestion("Welche Dinge aus eurer Beziehung gehören für dich auch gegenüber gemeinsamen Freunden klar ins Private?"),
            GenQuestion("Was wäre für dich ein Warnsignal, wenn sich ein gemeinsamer Freundeskreis stark auf eure Beziehung auswirkt?"),
            GenQuestion("Welche Art gemeinsamer Freundschaft würdest du gern in den nächsten Jahren stärker aufbauen?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.map { pack ->
        if (pack.id in overrides) pack.copy(questions = overrides.getValue(pack.id)) else pack
    }
}
