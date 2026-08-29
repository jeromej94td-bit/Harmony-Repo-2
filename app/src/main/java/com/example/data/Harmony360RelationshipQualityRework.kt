package com.example.data

/**
 * Final list-level curation layer for 360 Rework Stage 05.1.
 *
 * The large generated section files remain untouched. This layer is intentionally explicit:
 * only relationship-facing Sections 01, 02, 06 and 12 may be archived or manually overridden.
 */
object Harmony360RelationshipQualityRework {

    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val stage051SectionTags = setOf(
        "h360_section_01_beziehung_naehe",
        "h360_section_02_kommunikation",
        "h360_section_06_alltag_zuhause",
        "h360_section_12_kommunikation_konflikte"
    )

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val section01Decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_001_zuneigung_im_alltag_entweder_oder" to CurationDecision.REWRITE,
        "h500_002_quality_time_wer_eher" to CurationDecision.REWRITE,
        "h500_003_kleine_gesten_skala" to CurationDecision.REWRITE,
        "h500_004_koerpernaehe_ranking" to CurationDecision.REWRITE,
        "h500_005_komplimente_prognose" to CurationDecision.REWRITE,
        "h500_006_vermissen_szenario" to CurationDecision.REWRITE,
        "h500_007_wiedersehen_geheime_wahl" to CurationDecision.REWRITE,
        "h500_008_geborgenheit_memory" to CurationDecision.REWRITE,
        "h500_009_romantik_prioritaet" to CurationDecision.REWRITE,
        "h500_010_vertrauen_offene_runde" to CurationDecision.KEEP,
        "h500_012_emotionale_sicherheit_wer_eher" to CurationDecision.REWRITE,
        "h500_013_naehe_nach_streit_skala" to CurationDecision.REWRITE,
        "h500_014_gemeinsame_rituale_ranking" to CurationDecision.REWRITE,
        "h500_015_aufmerksamkeit_prognose" to CurationDecision.REWRITE,
        "h500_017_flirten_in_der_beziehung_geheime_wahl" to CurationDecision.REWRITE,
        "h500_018_ueberraschungen_memory" to CurationDecision.KEEP,
        "h500_022_zaertlichkeit_wer_eher" to CurationDecision.ARCHIVE,
        "h500_023_verbundenheit_skala" to CurationDecision.ARCHIVE
    )

    internal val section02Decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_026_zuhoeren_szenario" to CurationDecision.REWRITE,
        "h500_027_missverstaendnisse_geheime_wahl" to CurationDecision.ARCHIVE,
        "h500_028_schwierige_gespraeche_memory" to CurationDecision.REWRITE,
        "h500_029_ehrlichkeit_prioritaet" to CurationDecision.REWRITE,
        "h500_030_direkte_worte_offene_runde" to CurationDecision.KEEP,
        "h500_031_zwischen_den_zeilen_entweder_oder" to CurationDecision.REWRITE,
        "h500_032_textnachrichten_wer_eher" to CurationDecision.REWRITE,
        "h500_033_telefonieren_skala" to CurationDecision.REWRITE,
        "h500_034_streitkultur_ranking" to CurationDecision.REWRITE,
        "h500_035_entschuldigungen_prognose" to CurationDecision.REWRITE,
        "h500_036_kritik_annehmen_szenario" to CurationDecision.REWRITE,
        "h500_037_beduerfnisse_aussprechen_geheime_wahl" to CurationDecision.REWRITE,
        "h500_038_grenzen_erklaeren_memory" to CurationDecision.REWRITE,
        "h500_039_humor_im_gespraech_prioritaet" to CurationDecision.REWRITE,
        "h500_040_schweigen_offene_runde" to CurationDecision.KEEP,
        "h500_043_respekt_im_streit_skala" to CurationDecision.REWRITE,
        "h500_044_gefuehle_benennen_ranking" to CurationDecision.REWRITE,
        "h500_049_tabuthemen_prioritaet" to CurationDecision.REWRITE
    )

    private val section01Overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_001_zuneigung_im_alltag_entweder_oder" to listOf(
            q("Was fühlt sich im Alltag für dich mehr nach Zuneigung an?", "Eine kleine Berührung", "Eine liebe Nachricht"),
            q("Wenn ihr euch nach einem langen Tag wiederseht: Was brauchst du eher?", "Erst umarmen", "Erst erzählen"),
            q("Was bedeutet dir mehr?", "Etwas merken, ohne dass ich frage", "Etwas Schönes bewusst planen"),
            q("Wie fühlt sich Zuneigung in der Öffentlichkeit für dich besser an?", "Hand halten", "Nähe lieber privat"),
            q("Wenn du gestresst bist: Was wirkt liebevoller?", "Kurz Nähe geben", "Mir praktisch etwas abnehmen"),
            q("Was trifft dich stärker?", "Spontanes „Ich denk an dich“", "Ein verlässliches kleines Ritual")
        ),
        "h500_002_quality_time_wer_eher" to listOf(
            whoQ("Wer legt bei gemeinsamer Zeit eher als Erstes das Handy wirklich weg?"),
            whoQ("Wer macht aus einem normalen Einkauf eher spontan ein kleines Date?"),
            whoQ("Wer merkt eher, wenn ihr zwar nebeneinander seid, aber gerade kaum miteinander?"),
            whoQ("Wer plant eher bewusst Zeit nur für euch zwei ein?"),
            whoQ("Wer schlägt eher einen Spaziergang vor, nur um in Ruhe zu reden?"),
            whoQ("Wer sagt eher „nur eine Folge“ und macht daraus plötzlich vier?"),
            whoQ("Wer wirft eher einen zu vollen Plan um und entscheidet sich für einen ruhigen Abend zu zweit?"),
            whoQ("Wer schützt eure gemeinsame Zeit eher vor Arbeit, Nachrichten und anderen Verpflichtungen?")
        ),
        "h500_003_kleine_gesten_skala" to listOf(
            scaleQ("Wie viel bedeutet es dir, wenn dein Partner ohne Nachfrage deinen Lieblingssnack mitbringt?"),
            scaleQ("Wie viel bedeutet dir eine kurze Nachricht vor einem wichtigen Termin?"),
            scaleQ("Wie stark fühlst du dich gesehen, wenn dein Partner eine nervige Aufgabe einfach übernimmt?"),
            scaleQ("Wie wichtig ist dir, dass kleine Details aus euren Gesprächen später noch erinnert werden?"),
            scaleQ("Wie viel macht eine kleine Berührung im Vorbeigehen für dich aus?"),
            scaleQ("Wie sehr ist „Ich hab dir das letzte Stück aufgehoben“ für dich tatsächlich ein Liebesbeweis?"),
            scaleQ("Wie wichtig ist dir, dass dein Partner später noch einmal nachfragt, wie etwas für dich ausgegangen ist?")
        ),
        "h500_004_koerpernaehe_ranking" to listOf(
            q("Welche Form von Körpernähe gibt dir am meisten? Ordne.", "Hand halten", "Umarmung", "Kuscheln", "Nähe beim Einschlafen"),
            q("Wann suchst du Körpernähe am ehesten? Ordne.", "Bei der Begrüßung", "Nach einem schweren Tag", "Beim Entspannen", "Beim Abschied"),
            q("Wie soll dein Partner Nähe am liebsten beginnen? Ordne.", "Direkt fragen", "Hand ausstrecken", "Näher rücken", "Blickkontakt suchen"),
            q("Womit fühlst du dich in der Öffentlichkeit am wohlsten? Ordne.", "Hand halten", "Arm umlegen", "Kurzer Kuss", "Nähe lieber privat"),
            q("Wenn du traurig bist: Welche Nähe hilft dir am ehesten? Ordne.", "Lange Umarmung", "Kopf an die Schulter", "Hand halten", "Erst Raum, dann Nähe"),
            q("Welche Grenze bei Körpernähe ist dir am wichtigsten? Ordne.", "Beim Schlafen Freiraum", "Öffentlich zurückhaltend", "Nicht ungefragt anfassen", "Nähe nicht als Streitlösung erzwingen")
        ),
        "h500_005_komplimente_prognose" to listOf(
            q("Welches Kompliment würde dein Partner vermutlich am liebsten hören?", "Über das Aussehen", "Über den Charakter", "Über etwas Geschafftes", "Über etwas, das er für dich tut"),
            q("Wie kommt ein Kompliment bei deinem Partner vermutlich am besten an?", "Spontan nebenbei", "Privat und direkt", "Als Nachricht", "Mit einem konkreten Beispiel"),
            q("Was macht ein Kompliment für deinen Partner am ehesten unangenehm?", "Zu allgemein", "Vor vielen Leuten", "Zu übertrieben", "Schlechtes Timing"),
            q("Wenn dein Partner an sich zweifelt: Was würde vermutlich am meisten helfen?", "„Ich glaube an dich“", "Eine konkrete Stärke nennen", "„Ich bin stolz auf dich“", "Zeigen, was du an ihm schätzt"),
            q("Welche Art Kompliment glaubt dein Partner dir wahrscheinlich sofort?", "Kurz und ehrlich", "Sehr konkret", "Verspielt", "Unerwartet im Alltag"),
            q("Womit könntest du deinen Partner beim nächsten Kompliment wirklich überraschen?", "Etwas nennen, das sonst keiner bemerkt", "Eine alte Stärke erinnern", "Eine kleine Nachricht hinterlassen", "Ein Kompliment genau im richtigen Moment")
        ),
        "h500_006_vermissen_szenario" to listOf(
            q("Ihr seid mehrere Tage getrennt und habt beide wenig Zeit. Was hält euch am ehesten nah?", "Fester kurzer Anruf", "Sprachnachrichten zwischendurch", "Fotos aus dem Alltag", "Kein Druck, abends ein Lebenszeichen"),
            q("Du vermisst deinen Partner, aber seine Nachrichten werden plötzlich knapp. Was machst du?", "Direkt nachfragen", "Liebe Nachricht ohne Druck", "Bis später warten", "Einen Zeitpunkt zum Reden vorschlagen"),
            q("Du merkst: Du vermisst gerade deutlich stärker als dein Partner. Was hilft dir am meisten?", "Es offen sagen", "Mehr Kontakt vereinbaren", "Mich bewusst ablenken", "Den nächsten gemeinsamen Moment planen"),
            q("Beim Wiedersehen wollt ihr Unterschiedliches: einer Ruhe, einer sofort etwas erleben. Was macht ihr?", "Erst zusammen ankommen", "Kurze Pause, dann raus", "Heute Ruhe, morgen Erlebnis", "Spontan nach Stimmung entscheiden"),
            q("Nach einem schönen Besuch steht der Abschied an. Was macht ihn für dich leichter?", "Nächsten Termin festlegen", "Lange Umarmung", "Noch eine gemeinsame Kleinigkeit", "Später eine Nachricht bekommen"),
            q("Bis zum nächsten Treffen dauert es länger als gehofft. Was würdest du zuerst einführen?", "Festen Videoabend", "Gemeinsame kleine Challenge", "Überraschung per Post", "Tägliches Mini-Update")
        ),
        "h500_007_wiedersehen_geheime_wahl" to listOf(
            q("Was wäre bei einem Wiedersehen heimlich dein perfekter erster Moment?", "Lange Umarmung", "Sofort losreden", "Gemeinsam etwas essen", "Erst einmal nur ankommen"),
            q("Was fändest du schöner?", "Überraschungs-Wiedersehen", "Genau wissen, wann es passiert", "Kleine Überraschung beim Treffen", "Alles ganz unkompliziert"),
            q("Die erste Stunde zusammen: Was zieht dich mehr an?", "Zuhause bleiben", "Spazieren gehen", "Lieblingsort besuchen", "Etwas Neues machen"),
            q("Was sollte beim Wiedersehen eher passieren?", "Handy weg", "Foto zusammen", "Lieblingsessen", "Kleine persönliche Geste"),
            q("Wie soll der erste Abend am liebsten enden?", "Lange reden", "Film und kuscheln", "Noch einmal raus", "Früh schlafen und Nähe genießen"),
            q("Welches kleine Wiedersehens-Ritual würdest du gern nur für euch haben?", "Bestimmte Umarmung", "Ein gemeinsamer Snack", "Ein Satz nur für euch", "Immer derselbe erste Ort")
        ),
        "h500_008_geborgenheit_memory" to listOf(
            GenQuestion("Wann hast du dich bei deinem Partner zuletzt ganz selbstverständlich sicher gefühlt?"),
            GenQuestion("Welche kleine Reaktion deines Partners hat dich einmal stärker beruhigt, als er wahrscheinlich gemerkt hat?"),
            GenQuestion("Welcher gemeinsame Ort fühlt sich für dich am meisten nach Geborgenheit an?"),
            GenQuestion("Erinnerst du dich an einen schweren Tag, an dem dein Partner genau richtig für dich da war? Was war es?"),
            GenQuestion("Welche unerwartete Kleinigkeit hat dir einmal gezeigt: Hier darf ich einfach ich sein?"),
            GenQuestion("Welche zukünftige Erinnerung würde für dich perfekt zu dem Wort Geborgenheit passen?")
        ),
        "h500_009_romantik_prioritaet" to listOf(
            q("Was macht Romantik für dich am stärksten aus?", "Persönlicher Gedanke", "Zeit nur für uns", "Überraschung", "Besondere Atmosphäre"),
            q("Wenn nur eine romantische Sache bleiben dürfte: welche?", "Date-Abend", "Liebe Nachricht", "Kleine spontane Geste", "Gemeinsames Ritual"),
            q("Wo fühlt sich Romantik für dich besser an?", "Ganz privat", "Unterwegs zu zweit", "Bei einem besonderen Anlass", "Mitten im normalen Alltag"),
            q("Was ist dir wichtiger?", "Spontan und echt", "Geplant und besonders", "Einfach und persönlich", "Selten, dafür groß"),
            q("Worauf könntest du bei Romantik am ehesten verzichten?", "Teure Geschenke", "Perfekte Fotos", "Große Inszenierung", "Klassische Klischees"),
            q("Was darf bei einem romantischen Moment nie fehlen?", "Aufmerksamkeit", "Ehrlichkeit", "Nähe", "Etwas Persönliches")
        ),
        "h500_012_emotionale_sicherheit_wer_eher" to listOf(
            whoQ("Wer merkt eher, wenn der andere sich innerlich zurückzieht?"),
            whoQ("Wer sagt nach einem schwierigen Gespräch eher ausdrücklich: „Wir sind trotzdem okay“?"),
            whoQ("Wer fragt eher nach, ob der andere gut angekommen ist?"),
            whoQ("Wer spricht eher als Erstes aus, wenn gerade Sicherheit oder Bestätigung fehlt?"),
            whoQ("Wer akzeptiert ein klares Nein eher sofort, ohne es persönlich zu nehmen?"),
            whoQ("Wer kommt nach einem ernsten Gespräch eher später noch einmal darauf zurück?"),
            whoQ("Wer schafft es eher, dem anderen Sicherheit zu geben, ohne sofort eine Lösung zu verlangen?")
        ),
        "h500_013_naehe_nach_streit_skala" to listOf(
            scaleQ("Wie sehr brauchst du nach einem Streit erst Zeit für dich, bevor sich Nähe wieder gut anfühlt?"),
            scaleQ("Wie wichtig ist dir eine verbale Klärung, bevor eine Umarmung wirklich hilft?"),
            scaleQ("Wie schnell fühlt sich körperliche Nähe nach einem Streit für dich wieder richtig an?"),
            scaleQ("Wie leicht kannst du nach einem Streit selbst sagen: „Ich brauche gerade eine Umarmung“?"),
            scaleQ("Wie wichtig ist dir nach einem Streit die ausdrückliche Bestätigung, dass ihr als Paar okay seid?"),
            scaleQ("Wie zufrieden bist du damit, wie ihr nach Streit wieder zueinander findet?")
        ),
        "h500_014_gemeinsame_rituale_ranking" to listOf(
            q("Welche kleinen Rituale wären dir im Alltag am wertvollsten? Ordne.", "Morgenkaffee zusammen", "Gute-Nacht-Kuss", "Handyfreies Essen", "Feste Zeit nur für uns"),
            q("Welche Wochenrituale würdest du am ehesten schützen? Ordne.", "Gemeinsam kochen", "Spaziergang", "Filmabend", "Kleines Date"),
            q("Welche Mini-Rituale geben dir am meisten Nähe? Ordne.", "Begrüßungsumarmung", "Nach dem Tag fragen", "Kurze Nachricht", "Zusammen einschlafen"),
            q("Was sollte ein gutes Ritual für euch vor allem sein? Ordne.", "Einfach", "Verlässlich", "Persönlich", "Flexibel"),
            q("Welche Rituale würdest du lieber spontan statt fest haben? Ordne.", "Date-Abend", "Frühstück", "Telefonat", "Kleine Überraschung"),
            q("Welche Gewohnheit könnte später einmal typisch „ihr zwei“ sein? Ordne.", "Eigener Feiertag", "Bestimmtes Sonntagsritual", "Jährlicher Lieblingsort", "Kleiner täglicher Insider")
        ),
        "h500_015_aufmerksamkeit_prognose" to listOf(
            q("Wodurch fühlt sich dein Partner vermutlich am stärksten gesehen?", "Handy wirklich weglegen", "Nachfragen und zuhören", "Ein Detail erinnern", "Von selbst Zeit einplanen"),
            q("Was stört deinen Partner bei Aufmerksamkeit vermutlich am meisten?", "Nebenbei aufs Handy schauen", "Unterbrechen", "Zu schnell Lösungen anbieten", "Nur halb zuhören"),
            q("Welche kleine Aufmerksamkeit würde dein Partner wahrscheinlich sofort bemerken?", "Lieblingsgetränk mitbringen", "Nach einem Termin fragen", "Eine Aufgabe abnehmen", "Eine liebe Nachricht"),
            q("Wann braucht dein Partner deine Aufmerksamkeit vermutlich besonders?", "Nach einem schweren Tag", "Vor etwas Wichtigem", "Wenn er still wird", "Wenn etwas richtig gut lief"),
            q("Was verwechselt dein Partner vielleicht am wenigsten mit echter Aufmerksamkeit?", "Viele Nachrichten", "Teure Geschenke", "Volle Präsenz", "Große Worte"),
            q("Welche Form von Aufmerksamkeit würde deinen Partner am ehesten positiv überraschen?", "Ein ungeplanter gemeinsamer Moment", "Ein altes Detail wieder aufgreifen", "Ein bewusstes Kompliment", "Ein Abend ohne Ablenkung")
        ),
        "h500_017_flirten_in_der_beziehung_geheime_wahl" to listOf(
            q("Wie würdest du mit deinem Partner heimlich am liebsten öfter flirten?", "Blicke", "Freche Nachricht", "Kompliment", "Kleine Berührung"),
            q("Welche Art zu flirten fühlt sich für dich am meisten nach euch an?", "Verspielt", "Romantisch", "Direkt", "Sehr subtil"),
            q("Was wäre ein kleines Flirt-Signal, das dich sofort erreicht?", "Bestimmter Blick", "Insider-Satz", "Hand an der Hüfte", "Unerwartetes Kompliment"),
            q("Wo macht Flirten dir am meisten Spaß?", "Zuhause", "Unterwegs", "Beim Schreiben", "Wenn andere nichts merken"),
            q("Was würdest du beim Flirten gern öfter vom Partner bekommen?", "Initiative", "Humor", "Mut", "Aufmerksamkeit"),
            q("Welche kleine Flirt-Idee würdest du selbst am ehesten ausprobieren?", "Nachricht mitten am Tag", "Mini-Date ankündigen", "Geheimes Kompliment", "Bewusst länger Blickkontakt halten")
        )
    )

    private val section02Overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_026_zuhoeren_szenario" to listOf(
            q("Du erzählst deinem Partner von einem Problem. Was brauchst du zuerst?", "Nur zuhören", "Fragen stellen", "Gemeinsam Lösung suchen", "Erst kurz Ruhe"),
            q("Du bist sichtbar aufgewühlt, findest aber noch keine Worte. Was wäre am hilfreichsten?", "Ruhig dableiben", "Sanft nachfragen", "Eine Umarmung anbieten", "Später noch einmal fragen"),
            q("Du erzählst etwas Wichtiges und dein Partner schaut aufs Handy. Was trifft deinen Wunsch am besten?", "Handy weg und Blickkontakt", "Kurz sagen, wann er zuhören kann", "Nachfragen, was dir wichtig ist", "Gespräch bewusst später fortsetzen"),
            q("Du brauchst keinen Rat, bekommst aber sofort Lösungen. Was wäre die beste Korrektur?", "„Bitte hör nur kurz zu“", "Frage stellen statt lösen", "Kurz Pause machen", "Später gemeinsam Lösungen sammeln"),
            q("Woran merkst du am ehesten, dass dir wirklich zugehört wurde?", "Rückfrage zum Detail", "Gefühl wird benannt", "Späteres Nachfragen", "Nichts wird sofort bewertet"),
            q("Wann redest du über etwas Schwieriges am liebsten?", "Sofort", "Nach kurzem Runterkommen", "Beim Spaziergang", "Wenn wirklich Zeit dafür ist")
        ),
        "h500_028_schwierige_gespraeche_memory" to listOf(
            GenQuestion("Welches schwierige Gespräch zwischen euch hat im Nachhinein wirklich etwas verbessert?"),
            GenQuestion("Wann hast du dich in einem ernsten Gespräch einmal überraschend verstanden gefühlt?"),
            GenQuestion("Welcher Satz deines Partners hat in einem schwierigen Gespräch mehr geholfen als jede Lösung?"),
            GenQuestion("Gab es ein Gespräch, vor dem du lange Angst hattest und das dann viel besser lief als gedacht?"),
            GenQuestion("Welche Art, ein schwieriges Thema anzusprechen, hat bei euch schon einmal besonders gut funktioniert?"),
            GenQuestion("Was würdest du bei eurem nächsten schwierigen Gespräch bewusst anders machen?")
        ),
        "h500_029_ehrlichkeit_prioritaet" to listOf(
            q("Was ist dir bei einer unangenehmen Wahrheit am wichtigsten?", "Früh sagen", "Vollständig sagen", "Taktvoll sagen", "Privat sagen"),
            q("Welche Wahrheit sollte in einer Beziehung am wenigsten aufgeschoben werden?", "Geldproblem", "Zweifel an einer Entscheidung", "Verletzte Grenze", "Etwas, das Vertrauen betrifft"),
            q("Was wiegt bei Ehrlichkeit schwerer?", "Der richtige Zeitpunkt", "Die ganze Wahrheit", "Der richtige Ton", "Mut, es überhaupt zu sagen"),
            q("Was wäre für dich eher noch okay?", "Kleine Überraschung verschweigen", "Zeit brauchen vor einem Gespräch", "Details aus Privatsphäre behalten", "Nichts davon, wenn es uns betrifft"),
            q("Wann sollte Ehrlichkeit für dich sofort kommen?", "Wenn eine Entscheidung uns beide betrifft", "Wenn Vertrauen gefährdet ist", "Wenn Geld betroffen ist", "Wenn eine Grenze verletzt wurde"),
            q("Was macht eine schwierige Wahrheit für dich leichter annehmbar?", "Konkrete Verantwortung", "Keine Ausreden", "Ruhiger Zeitpunkt", "Danach gemeinsam überlegen")
        ),
        "h500_031_zwischen_den_zeilen_entweder_oder" to listOf(
            q("Wenn dich etwas stört: Was ist eher dein natürlicher Weg?", "Direkt sagen", "Erst vorsichtig andeuten"),
            q("Wenn dein Partner ungewöhnlich kurz antwortet: Was machst du eher?", "Direkt nachfragen", "Erst Stimmung beobachten"),
            q("Was verrät bei dir mehr als deine Worte?", "Tonfall", "Körpersprache"),
            q("Bei einem sensiblen Thema: Was ist dir lieber?", "Klar und direkt", "Behutsam und Schritt für Schritt"),
            q("Wenn du „alles gut“ sagst, obwohl nicht alles gut ist: Was wünschst du dir eher?", "Noch einmal nachfragen", "Mir erst Raum geben"),
            q("Was verhindert bei euch eher Missverständnisse?", "Fragen statt vermuten", "Gefühle direkt benennen")
        ),
        "h500_032_textnachrichten_wer_eher" to listOf(
            whoQ("Wer schreibt eher zuerst „alles gut“, obwohl man merkt, dass nicht alles gut ist?"),
            whoQ("Wer schickt eher eine Sprachnachricht, weil der Text sonst viel zu lang würde?"),
            whoQ("Wer schaut eher aufs Handy und denkt sich: Ich antworte gleich – und vergisst es dann?"),
            whoQ("Wer merkt eher an einem Punkt statt einem Emoji, dass irgendetwas anders ist?"),
            whoQ("Wer schreibt eher noch eine Gute-Nacht-Nachricht, obwohl ihr euch am nächsten Morgen seht?"),
            whoQ("Wer würde ein wirklich wichtiges Thema eher nicht per Text klären wollen?"),
            whoQ("Wer schickt eher ein Foto aus dem Alltag nur mit „musste an dich denken“?")
        ),
        "h500_033_telefonieren_skala" to listOf(
            scaleQ("Wie wichtig sind dir spontane kurze Anrufe im Alltag?"),
            scaleQ("Wie sehr stört es dich, wenn beim Telefonieren nebenbei etwas anderes gemacht wird?"),
            scaleQ("Wie gern besprichst du schwierige Themen lieber am Telefon als per Nachricht?"),
            scaleQ("Wie wichtig ist dir ein fester Anruf, wenn ihr euch mehrere Tage nicht seht?"),
            scaleQ("Wie wohl fühlst du dich mit längerer gemeinsamer Stille am Telefon?"),
            scaleQ("Wie sehr bevorzugst du einen kurzen Anruf gegenüber zehn einzelnen Nachrichten?")
        ),
        "h500_034_streitkultur_ranking" to listOf(
            q("Was sollte in einem Streit für dich zuerst geschützt werden? Ordne.", "Respekt", "Ehrlichkeit", "Zuhören", "Lösungswille"),
            q("Was hilft dir am meisten, wenn die Stimmung hochkocht? Ordne.", "Kurze Pause", "Ruhiger Ton", "Konkretes Thema behalten", "Erst ausreden lassen"),
            q("Was macht einen Streit für dich am schnellsten unfair? Ordne.", "Beleidigungen", "Alte Themen hervorholen", "Unterbrechen", "Mit Trennung drohen"),
            q("Woran merkst du am ehesten, dass ein Streit wirklich vorbei ist? Ordne.", "Beide verstanden", "Entschuldigung", "Konkrete Lösung", "Nähe wieder möglich"),
            q("Was sollte nach einem Streit eher passieren? Ordne.", "Kurz nachfragen, wie es geht", "Verabredete Änderung", "Gemeinsam runterkommen", "Später noch einmal prüfen"),
            q("Welche Fähigkeit würdest du für eure Streitkultur am liebsten verstärken? Ordne.", "Früher stoppen", "Besser zuhören", "Klarer sagen, was weh tut", "Schneller wieder verbinden")
        ),
        "h500_035_entschuldigungen_prognose" to listOf(
            q("Welche Entschuldigung würde dein Partner vermutlich am ehesten glauben?", "Klare Verantwortung", "Konkretes Bedauern", "Verhalten wirklich ändern", "Fragen, was jetzt hilft"),
            q("Was macht eine Entschuldigung für deinen Partner vermutlich unglaubwürdig?", "„Aber du…“", "Zu spät", "Nur schnell Ruhe wollen", "Dasselbe wiederholen"),
            q("Wann will dein Partner eine Entschuldigung vermutlich lieber hören?", "Sofort", "Nach kurzer Pause", "Erst wenn beide ruhig sind", "Wenn auch über die Lösung geredet wird"),
            q("Was ist deinem Partner nach einer Entschuldigung wahrscheinlich wichtiger?", "Umarmung", "Zeit", "Konkrete Veränderung", "Noch einmal darüber reden"),
            q("Welche Form passt vermutlich besser zu deinem Partner?", "Kurz und direkt", "Ausführlich erklären", "Schriftlich, wenn Worte schwerfallen", "Mit einer Handlung zeigen"),
            q("Womit könntest du deinen Partner bei einer Entschuldigung positiv überraschen?", "Ohne Rechtfertigung", "Ein Detail wirklich verstanden haben", "Selbst eine Lösung vorschlagen", "Später noch einmal nachfragen")
        ),
        "h500_036_kritik_annehmen_szenario" to listOf(
            q("Dein Partner kritisiert etwas, das du ganz anders siehst. Was hilft dir am ehesten, nicht sofort dichtzumachen?", "Konkretes Beispiel", "Ruhiger Ton", "Erst meine Sicht hören", "Kurz Zeit zum Nachdenken"),
            q("Du merkst, dass du dich sofort verteidigst. Was wäre dein sinnvollster nächster Schritt?", "Eine Rückfrage stellen", "Kurz Pause machen", "Zusammenfassen, was angekommen ist", "Erst später antworten"),
            q("Die Kritik trifft einen wunden Punkt. Was würdest du lieber sagen?", "„Das tut gerade weh“", "„Ich brauche kurz Zeit“", "„Was genau meinst du?“", "„Lass uns später weiterreden“"),
            q("Dein Partner kritisiert dein Verhalten. Was sollte möglichst nicht passieren?", "Aus Verhalten wird Charakter", "Alte Fehler kommen dazu", "Ton wird abwertend", "Es geht nur ums Gewinnen"),
            q("Du verstehst die Kritik, bist aber noch nicht einverstanden. Was ist fair?", "Verständnis zeigen", "Eigene Sicht ruhig erklären", "Konkrete Änderung testen", "Später erneut besprechen"),
            q("Was macht Feedback für dich am ehesten annehmbar?", "Konkreter Wunsch", "Ein Thema zur Zeit", "Guter Zeitpunkt", "Keine Verallgemeinerungen")
        ),
        "h500_037_beduerfnisse_aussprechen_geheime_wahl" to listOf(
            q("Welches Bedürfnis fällt dir am schwersten direkt auszusprechen?", "Mehr Nähe", "Mehr Ruhe", "Mehr Hilfe", "Mehr Zeit für mich"),
            q("Wenn du etwas brauchst: Wie sagst du es am liebsten?", "Direkt", "Mit konkretem Wunsch", "Erst erklären, warum", "Im richtigen ruhigen Moment"),
            q("Was lässt dich ein Bedürfnis eher verschweigen?", "Angst vor Ablehnung", "Nicht belasten wollen", "Selbst noch unsicher", "Hoffen, dass es bemerkt wird"),
            q("Welcher Satz wäre für dich am hilfreichsten?", "„Ich brauche gerade…“", "„Kannst du mir helfen mit…?“", "„Ich wünsche mir mehr…“", "„Heute schaffe ich das nicht allein“"),
            q("Was wäre mutiger für dich?", "Um Nähe bitten", "Um Raum bitten", "Um Unterstützung bitten", "Ein klares Nein sagen"),
            q("Wann fällt es dir leichter, Bedürfnisse auszusprechen?", "Sofort", "Wenn ich ruhig bin", "Beim Spaziergang", "Wenn der Partner zuerst fragt")
        ),
        "h500_038_grenzen_erklaeren_memory" to listOf(
            GenQuestion("Welche Grenze von dir wurde einmal sofort respektiert, sodass du dich besonders sicher gefühlt hast?"),
            GenQuestion("Wann musstest du ein klares Nein wiederholen, obwohl du dachtest, es sei schon verstanden?"),
            GenQuestion("Welche frühere Erfahrung erklärt eine Grenze von dir heute am besten?"),
            GenQuestion("Welche Grenze fällt dir besonders schwer zu erklären, obwohl sie dir wichtig ist?"),
            GenQuestion("Wann hat dein Partner eine Grenze von dir besser verstanden, nachdem du erklärt hast, warum sie wichtig ist?"),
            GenQuestion("Welche Grenze soll dein Partner niemals erraten müssen, sondern von dir klar hören?")
        ),
        "h500_039_humor_im_gespraech_prioritaet" to listOf(
            q("Wann hilft Humor in einem schwierigen Gespräch am meisten?", "Wenn Spannung raus muss", "Wenn beide schon ruhiger sind", "Wenn es ein gemeinsamer Insider ist", "Wenn er niemanden lächerlich macht"),
            q("Wann ist Humor für dich eher unpassend?", "Wenn ich mich verletzlich zeige", "Bei einer klaren Grenze", "Bei echter Angst", "Wenn ich mich nicht ernst genommen fühle"),
            q("Was soll Humor im Gespräch eher leisten?", "Nähe schaffen", "Druck rausnehmen", "Uns selbst nicht zu ernst nehmen", "Einen Neustart ermöglichen"),
            q("Was ist die wichtigste Grenze bei Witzen zwischen euch?", "Kein Spott über Unsicherheiten", "Kein Publikum für private Themen", "Stopp heißt Stopp", "Nicht statt einer Entschuldigung"),
            q("Welche Art Humor verbindet euch am stärksten?", "Insider", "Selbstironie", "Alberne Beobachtungen", "Gemeinsames Necken"),
            q("Woran merkst du, dass Humor gerade wirklich hilft?", "Beide lachen", "Ton wird weicher", "Nähe kommt zurück", "Das Thema kann danach weitergehen")
        ),
        "h500_043_respekt_im_streit_skala" to listOf(
            scaleQ("Wie wichtig ist dir, dass im Streit niemand unterbrochen wird?"),
            scaleQ("Wie wichtig ist dir, dass alte Fehler nicht als Munition hervorgeholt werden?"),
            scaleQ("Wie stark trifft dich ein abwertender Ton, selbst wenn der Inhalt berechtigt ist?"),
            scaleQ("Wie wichtig ist dir, dass ein Stopp oder eine Pause im Streit akzeptiert wird?"),
            scaleQ("Wie wichtig ist dir, dass niemals mit Trennung gedroht wird, nur um Druck zu machen?"),
            scaleQ("Wie zufrieden bist du damit, wie respektvoll ihr in angespannten Momenten bleibt?")
        ),
        "h500_044_gefuehle_benennen_ranking" to listOf(
            q("Welche Sätze helfen dir am meisten, ein Gefühl statt einen Vorwurf auszusprechen? Ordne.", "Ich bin gerade überfordert", "Ich bin verletzt", "Ich bin unsicher", "Ich brauche kurz Ruhe"),
            q("Welche Gefühle kannst du am leichtesten direkt benennen? Ordne.", "Freude", "Traurigkeit", "Angst", "Wut"),
            q("Was hilft dir am ehesten herauszufinden, was du eigentlich fühlst? Ordne.", "Kurz allein sein", "Darüber reden", "Körperreaktion beachten", "Aufschreiben"),
            q("Welche Aussage öffnet eher ein Gespräch? Ordne.", "Ich fühle mich übergangen", "Ich brauche Unterstützung", "Ich bin gerade angespannt", "Ich weiß noch nicht genau, was los ist"),
            q("Welche Reaktion deines Partners hilft dir beim Benennen von Gefühlen? Ordne.", "Geduldig warten", "Nachfragen", "Nicht sofort lösen", "Gefühl spiegeln"),
            q("Was würdest du gern früher aussprechen, bevor daraus Streit wird? Ordne.", "Überforderung", "Enttäuschung", "Eifersucht", "Bedürfnis nach Abstand")
        ),
        "h500_049_tabuthemen_prioritaet" to listOf(
            q("Welche schwierigen Themen sollten Paare deiner Meinung nach am wenigsten dauerhaft vermeiden?", "Geld", "Eifersucht", "Intimität", "Zukunft"),
            q("Was braucht ein Tabuthema zuerst, damit du darüber reden kannst?", "Vertrauen", "Zeit", "Privatsphäre", "Kein Urteil"),
            q("Welches Thema würdest du lieber früh als erst im Konflikt klären?", "Familiengrenzen", "Finanzen", "Kinderwunsch", "Kontakt zu Ex-Partnern"),
            q("Was ist dir bei einem sehr persönlichen Thema am wichtigsten?", "Nicht drängen", "Ehrlich antworten", "Vertraulich bleiben", "Pausieren dürfen"),
            q("Wann sollte ein unangenehmes Thema trotzdem angesprochen werden?", "Wenn es Verhalten beeinflusst", "Wenn Vertrauen betroffen ist", "Wenn es eine gemeinsame Entscheidung betrifft", "Wenn einer dauerhaft darunter leidet"),
            q("Was macht ein Tabuthema für dich eher besprechbar?", "Konkrete Frage", "Ruhiger Moment", "Eigene Gefühle zuerst nennen", "Keine schnelle Lösung erwarten")
        )
    )

    private val archivedIds: Set<String> = buildSet {
        addAll(section01Decisions.filterValues { it == CurationDecision.ARCHIVE }.keys)
        addAll(section02Decisions.filterValues { it == CurationDecision.ARCHIVE }.keys)
    }

    private val questionOverrides: Map<String, List<GenQuestion>> = buildMap {
        putAll(section01Overrides)
        putAll(section02Overrides)
    }

    internal fun isStage051(pack: GenPack): Boolean =
        pack.tags.any(stage051SectionTags::contains)

    fun apply(packs: List<GenPack>): List<GenPack> =
        applyRules(
            packs = packs,
            archivedIds = archivedIds,
            questionOverrides = questionOverrides
        )

    internal fun applyRules(
        packs: List<GenPack>,
        archivedIds: Set<String>,
        questionOverrides: Map<String, List<GenQuestion>>
    ): List<GenPack> = packs.mapNotNull { pack ->
        if (!isStage051(pack)) {
            pack
        } else if (pack.id in archivedIds) {
            null
        } else {
            questionOverrides[pack.id]?.let { questions ->
                pack.copy(questions = questions)
            } ?: pack
        }
    }
}
