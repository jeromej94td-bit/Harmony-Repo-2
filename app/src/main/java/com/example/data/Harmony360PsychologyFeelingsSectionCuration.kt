package com.example.data

/** Explicit Stage 05.4 curation for Harmony-360 Section 17 — Psychologie & Gefühle. */
object Harmony360PsychologyFeelingsSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")
    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – sehr stark")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun open(text: String): GenQuestion = GenQuestion(q = text)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)
    private fun scaleQ(text: String): GenQuestion = GenQuestion(q = text, options = scale)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_351_selbstreflexion_entweder_oder" to CurationDecision.ARCHIVE,
        "h500_352_einfuehlungsvermoegen_wer_eher" to CurationDecision.REWRITE,
        "h500_353_verletzlichkeit_skala" to CurationDecision.REWRITE,
        "h500_354_bindungsmuster_ranking" to CurationDecision.ARCHIVE,
        "h500_355_eifersucht_prognose" to CurationDecision.REWRITE,
        "h500_356_aengste_szenario" to CurationDecision.ARCHIVE,
        "h500_357_wuensche_und_beduerfnisse_geheime_wahl" to CurationDecision.REWRITE,
        "h500_358_kindheitstraumata_memory" to CurationDecision.REWRITE,
        "h500_359_emotionale_sicherheit_prioritaet" to CurationDecision.ARCHIVE,
        "h500_360_liebeserklaerung_offene_runde" to CurationDecision.ARCHIVE,
        "h500_361_selbstwertgefuehl_entweder_oder" to CurationDecision.REWRITE,
        "h500_362_troesten_wer_eher" to CurationDecision.REWRITE,
        "h500_363_stimmungsschwankungen_skala" to CurationDecision.ARCHIVE,
        "h500_364_vertrauen_ranking" to CurationDecision.ARCHIVE,
        "h500_365_vergebung_prognose" to CurationDecision.ARCHIVE,
        "h500_366_stressreaktionen_szenario" to CurationDecision.REWRITE,
        "h500_367_sehnsuechte_geheime_wahl" to CurationDecision.REWRITE,
        "h500_370_gemeinsame_psychohygiene_offene_runde" to CurationDecision.ARCHIVE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_352_einfuehlungsvermoegen_wer_eher" to listOf(
            whoQ("Wer merkt eher, dass sich die Stimmung des anderen verändert hat, bevor etwas gesagt wird?"),
            whoQ("Wer fragt eher nach, wenn hinter einem 'alles gut' offensichtlich noch etwas steckt?"),
            whoQ("Wer kann eher zuhören, ohne sofort eine Lösung vorzuschlagen?"),
            whoQ("Wer erinnert sich eher daran, was den anderen in einer schwierigen Situation beruhigt?"),
            whoQ("Wer erkennt eher, wann Nähe hilft und wann etwas Raum besser wäre?"),
            whoQ("Wer schafft es eher, die Sicht des anderen nachzuvollziehen, obwohl er selbst anders empfindet?")
        ),
        "h500_353_verletzlichkeit_skala" to listOf(
            scaleQ("Wie leicht fällt es dir, offen zu sagen, wenn dich etwas wirklich getroffen hat?"),
            scaleQ("Wie sicher fühlst du dich dabei, deinem Partner eine Unsicherheit zu zeigen?"),
            scaleQ("Wie leicht kannst du zugeben, dass du gerade nicht stark oder souverän sein möchtest?"),
            scaleQ("Wie offen kannst du über etwas sprechen, für das du dich ein wenig schämst?"),
            scaleQ("Wie gut kannst du eine ehrliche Reaktion des Partners aushalten, wenn du dich geöffnet hast?"),
            scaleQ("Wie stark hilft dir Verletzlichkeit dabei, echte Nähe entstehen zu lassen?")
        ),
        "h500_355_eifersucht_prognose" to listOf(
            q("Welche Situation würde deinen Partner vermutlich am ehesten verunsichern?", "Flirten mit jemand anderem", "Sehr enger Kontakt zu einer Ex-Person", "Viele private Nachrichten mit jemand Neuem", "Gefühl, bewusst ausgeschlossen zu werden"),
            q("Was würde deinem Partner in einem eifersüchtigen Moment wahrscheinlich am meisten helfen?", "Klare Einordnung", "Offenes Gespräch", "Etwas Nähe", "Kurz Zeit zum Sortieren"),
            q("Welche Grenze wäre deinem Partner vermutlich am wichtigsten?", "Keine Heimlichkeiten", "Respektvoller Umgang mit Ex-Kontakten", "Flirten klar einordnen", "Private Paarprobleme nicht nach außen tragen"),
            q("Wie würde dein Partner eine kleine Unsicherheit eher ansprechen?", "Direkt fragen", "Erst beobachten", "Mit Humor antesten", "Später in Ruhe ansprechen"),
            q("Was würde dein Partner eher als beruhigendes Zeichen sehen?", "Transparenz ohne Kontrolle", "Verlässliches Verhalten", "Klare Zuneigung", "Eigene Freiheit wird ebenfalls respektiert"),
            q("Was wäre für deinen Partner bei Eifersucht vermutlich am problematischsten?", "Kontrolle", "Unterstellungen", "Heimlichkeiten", "Gefühle gar nicht ansprechen")
        ),
        "h500_357_wuensche_und_beduerfnisse_geheime_wahl" to listOf(
            q("Welches Bedürfnis sagst du manchmal später, als es eigentlich gut wäre?", "Ruhe", "Nähe", "Unterstützung", "Zeit für mich"),
            q("Wovon würdest du dir im gemeinsamen Alltag heimlich etwas mehr wünschen?", "Bewusste Zeit zu zweit", "Spontane Zuneigung", "Praktische Entlastung", "Mehr Leichtigkeit"),
            q("Was fällt dir schwerer direkt einzufordern?", "Trost", "Anerkennung", "Freiraum", "Hilfe"),
            q("Welcher Wunsch wird bei dir am ehesten klein geredet, obwohl er dir wichtig ist?", "Gemeinsames Erlebnis", "Ruhe zuhause", "Mehr Körpernähe", "Ein persönliches Projekt"),
            q("Was wünschst du dir eher, wenn du einen schlechten Tag hattest?", "Zuhören", "Ablenkung", "Nähe", "In Ruhe gelassen werden"),
            q("Welches Bedürfnis sollte dein Partner über dich noch genauer kennen?", "Wie ich Trost brauche", "Wie viel Freiraum ich brauche", "Wie ich Anerkennung wahrnehme", "Wie ich gemeinsame Zeit erlebe")
        ),
        "h500_358_kindheitstraumata_memory" to listOf(
            open("Welche Erfahrung aus deiner Kindheit hat besonders geprägt, was sich für dich heute nach Geborgenheit anfühlt?"),
            open("Welche Regel oder Stimmung aus deinem Elternhaus möchtest du in deinem heutigen Leben bewusst anders gestalten?"),
            open("Wann hast du dich als Kind besonders verstanden oder unterstützt gefühlt?"),
            open("Gab es etwas, worüber bei euch zuhause kaum gesprochen wurde und das deinen Umgang mit Gefühlen bis heute beeinflusst?"),
            open("Welche Reaktion anderer Menschen aus deiner Kindheit macht verständlich, warum du heute bei bestimmten Situationen empfindlicher bist?"),
            open("Was sollte dein Partner über deine Kindheit wissen, um manche deiner Reaktionen besser einordnen zu können?")
        ),
        "h500_361_selbstwertgefuehl_entweder_oder" to listOf(
            q("Was stärkt dein Selbstgefühl eher?", "Etwas aus eigener Kraft schaffen", "Ehrliche Anerkennung bekommen"),
            q("Wenn du an dir zweifelst: Was hilft eher?", "Mich an eigene Erfolge erinnern", "Mit jemand Vertrautem sprechen"),
            q("Was ist für dich schwieriger?", "Ein Kompliment annehmen", "Um Hilfe bitten"),
            q("Woran misst du dich eher?", "An meinen eigenen Maßstäben", "Am Feedback anderer"),
            q("Nach einem Fehler?", "Erst analysieren und lernen", "Erst freundlich mit mir selbst sein"),
            q("Was möchtest du stärker schützen?", "Mein eigenes Tempo", "Meine eigenen Grenzen")
        ),
        "h500_362_troesten_wer_eher" to listOf(
            whoQ("Wer merkt eher, ob der andere gerade Trost statt einer Lösung braucht?"),
            whoQ("Wer nimmt den anderen eher einfach in den Arm, ohne viele Fragen zu stellen?"),
            whoQ("Wer findet eher die richtigen Worte, wenn etwas wirklich wehgetan hat?"),
            whoQ("Wer kann den anderen eher mit einer kleinen Ablenkung wieder auffangen?"),
            whoQ("Wer fragt eher später noch einmal nach, ob es wirklich wieder besser ist?"),
            whoQ("Wer respektiert eher sofort, wenn der andere beim Trösten erst einmal Ruhe möchte?")
        ),
        "h500_366_stressreaktionen_szenario" to listOf(
            q("Einer wird unter Stress still, der andere redet immer mehr. Was hilft zuerst?", "Kurz Bedürfnisse benennen", "Zehn Minuten Pause", "Nur das Dringende klären", "Festen Gesprächszeitpunkt vereinbaren"),
            q("Einer ist nach der Arbeit gereizt und merkt es selbst kaum. Wie sollte der andere reagieren?", "Behutsam darauf hinweisen", "Erst ankommen lassen", "Praktisch entlasten", "Fragen, was gerade gebraucht wird"),
            q("Ihr seid beide gleichzeitig überfordert und eine Kleinigkeit eskaliert. Was stoppt die Spirale?", "Thema kurz unterbrechen", "Lautstärke bewusst senken", "Nur einen Punkt klären", "Später neu anfangen"),
            q("Unter Druck übernimmt einer plötzlich alles allein. Was wäre fairer?", "Aufgaben sichtbar verteilen", "Um konkrete Hilfe bitten", "Unwichtiges streichen", "Kurz gemeinsam priorisieren"),
            q("Einer braucht Nähe, der andere unter Stress eher Abstand. Wie findet ihr einen Mittelweg?", "Kurzes Zeichen plus Raum", "Zeitpunkt für Nähe vereinbaren", "Bedürfnisse nicht persönlich nehmen", "Kleine gemeinsame Pause"),
            q("Eine stressige Phase dauert länger als gedacht. Was schützt eure Beziehung am besten?", "Regelmäßiger kurzer Check-in", "Ansprüche im Alltag senken", "Kleine gemeinsame Rituale behalten", "Früh sagen, wenn es zu viel wird")
        ),
        "h500_367_sehnsuechte_geheime_wahl" to listOf(
            q("Wonach sehnst du dich im Moment am ehesten?", "Mehr Ruhe", "Mehr Abenteuer", "Mehr Nähe", "Mehr Zeit für mich"),
            q("Welchen Wunsch würdest du gern irgendwann gemeinsam erfüllen?", "Längere Reise", "Eigenes Zuhause gestalten", "Gemeinsames Projekt", "Mehr freie Zeit"),
            q("Was fehlt dir manchmal, obwohl eigentlich alles okay wirkt?", "Spontaneität", "Tiefe Gespräche", "Leichtigkeit", "Rückzug"),
            q("Welche Art Veränderung zieht dich heimlich am meisten an?", "Neuer Ort", "Neues gemeinsames Ziel", "Anderer Alltagsrhythmus", "Mehr persönliche Freiheit"),
            q("Was würdest du dir wünschen, wenn Geld und Termine kurz keine Rolle spielten?", "Einfach wegfahren", "Zeit nur zu zweit", "Etwas Neues lernen", "Ein paar Tage ganz für mich"),
            q("Welche Sehnsucht sollte dein Partner über dich genauer kennen?", "Nach Sicherheit", "Nach Entwicklung", "Nach Nähe", "Nach Eigenständigkeit")
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
