package com.example.data

/** Stage 05.1e: hand-curated Streit & Wiederannäherung content. */
object Harmony360RelationshipSection12Curation {

    private val scale = listOf("1 – gar nicht", "2", "3", "4", "5 – extrem")
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String) = GenQuestion(text, options.toList())
    private fun scaleQ(text: String) = GenQuestion(text, scale)
    private fun whoQ(text: String) = GenQuestion(text, who)

    internal val decisions = linkedMapOf(
        "h500_251_streitkultur_entweder_oder" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_252_entschuldigung_wer_eher" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_253_schweigen_skala" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_254_kompromisse_ranking" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_255_feedback_prognose" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_256_missverstaendnisse_szenario" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_257_geheimnisse_geheime_wahl" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_258_alter_streit_memory" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_259_gespraechsthemen_prioritaet" to Harmony360RelationshipQualityRework.CurationDecision.ARCHIVE,
        "h500_260_ehrlichkeit_offene_runde" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_261_zuhoeren_entweder_oder" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_262_recht_haben_wer_eher" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_263_tonfall_skala" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_264_timing_ranking" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_265_gefuehle_zeigen_prognose" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_266_nachgeben_szenario" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_267_humor_im_streit_geheime_wahl" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE,
        "h500_270_versoehnung_offene_runde" to Harmony360RelationshipQualityRework.CurationDecision.REWRITE
    )

    internal val archivedIds: Set<String> = decisions
        .filterValues { it == Harmony360RelationshipQualityRework.CurationDecision.ARCHIVE }
        .keys

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_251_streitkultur_entweder_oder" to listOf(
            q("Wenn ein Streit gerade heiß wird: Was hilft dir eher?", "Direkt klären", "Kurze Pause"),
            q("Was ist dir im Streit wichtiger?", "Verstanden werden", "Eine Lösung finden"),
            q("Wenn du verletzt bist: Was passiert eher?", "Ich werde leiser", "Ich werde direkter"),
            q("Was bringt dich eher zurück ins Gespräch?", "Nähe und ruhiger Ton", "Erst etwas Abstand"),
            q("Was ist für dich schlimmer?", "Unterbrochen werden", "Ignoriert werden"),
            q("Wann ist ein Streit für dich eher vorbei?", "Wenn wir uns verstehen", "Wenn wir eine konkrete Lösung haben")
        ),
        "h500_252_entschuldigung_wer_eher" to listOf(
            whoQ("Wer sagt nach einem Streit eher zuerst ehrlich „Entschuldigung“?"),
            whoQ("Wer merkt eher, dass ein schnelles „sorry“ noch nicht wirklich reicht?"),
            whoQ("Wer erklärt bei einer Entschuldigung eher konkret, was er anders machen will?"),
            whoQ("Wer braucht nach einer Entschuldigung eher noch etwas Zeit, bis es sich wieder gut anfühlt?"),
            whoQ("Wer kann eher sagen: „Ich war im Ton falsch, auch wenn mein Punkt bleibt“?"),
            whoQ("Wer macht nach einer Entschuldigung eher eine kleine Geste, um wieder Nähe herzustellen?")
        ),
        "h500_253_schweigen_skala" to listOf(
            scaleQ("Wie sehr brauchst du im Streit manchmal bewusst eine Pause, bevor du weiterreden kannst?"),
            scaleQ("Wie belastend ist Schweigen für dich, wenn du nicht weißt, ob es Pause oder Strafe bedeutet?"),
            scaleQ("Wie wichtig ist dir ein Satz wie „Ich brauche kurz Ruhe, aber wir reden später weiter“?"),
            scaleQ("Wie schnell interpretierst du längeres Schweigen als Ablehnung?"),
            scaleQ("Wie gut kannst du dem Partner Raum geben, ohne sofort eine Antwort zu verlangen?"),
            scaleQ("Wie zufrieden bist du damit, wie ihr nach einer stillen Phase wieder ins Gespräch findet?")
        ),
        "h500_254_kompromisse_ranking" to listOf(
            q("Was macht einen guten Kompromiss für dich aus? Ordne.", "Fairness für beide", "Eigene Grenze bleibt", "Praktisch umsetzbar", "Keiner fühlt sich übergangen"),
            q("Was sollte man vor einem Kompromiss zuerst klären? Ordne.", "Was ist wirklich wichtig?", "Wo ist die Grenze?", "Was ist verhandelbar?", "Was wäre ein dritter Weg?"),
            q("Wobei fällt dir Nachgeben am schwersten? Ordne.", "Zeit", "Geld", "Familie/Freunde", "Persönlicher Freiraum"),
            q("Welche Warnzeichen zeigen dir, dass ein Kompromiss nicht fair ist? Ordne.", "Immer derselbe gibt nach", "Unausgesprochener Groll", "Druck statt Zustimmung", "Eigene Grenze wird kleiner"),
            q("Was hilft euch eher bei festgefahrenen Positionen? Ordne.", "Pause", "Bedürfnis hinter der Meinung erklären", "Neue dritte Option", "Heute vertagen"),
            q("Was ist langfristig wichtiger als Recht zu behalten? Ordne.", "Respekt", "Vertrauen", "Fairness", "Gemeinsame Lösung")
        ),
        "h500_255_feedback_prognose" to listOf(
            q("Wie möchte dein Partner kritisches Feedback vermutlich am liebsten hören?", "Direkt und kurz", "Ruhig mit Beispiel", "Erst fragen, ob es gerade passt", "Mit Lösungsvorschlag"),
            q("Was trifft deinen Partner bei Kritik vermutlich am stärksten?", "Scharfer Ton", "Verallgemeinerungen", "Vor anderen kritisiert werden", "Nicht ausreden dürfen"),
            q("Was hilft deinem Partner vermutlich, Feedback nicht als Angriff zu hören?", "Ich-Botschaften", "Konkrete Situation", "Ruhiger Zeitpunkt", "Auch das Gute benennen"),
            q("Wie reagiert dein Partner wahrscheinlich eher, wenn Kritik überraschend kommt?", "Verteidigt sich", "Wird still", "Fragt nach", "Braucht erst Zeit"),
            q("Welche Form von Feedback würde dein Partner bei dir vermutlich am ehesten akzeptieren?", "Ein klarer Wunsch", "Eine ehrliche Beobachtung", "Eine Frage statt Vorwurf", "Ein gemeinsamer Lösungsvorschlag"),
            q("Was würde dein Partner wahrscheinlich als besonders fair empfinden?", "Gleiche Regel für beide", "Kein altes Thema dazunehmen", "Bei einem Punkt bleiben", "Später noch einmal nachfragen")
        ),
        "h500_256_missverstaendnisse_szenario" to listOf(
            q("Eine kurze Nachricht von deinem Partner klingt für dich plötzlich kalt. Was machst du?", "Nachfragen statt deuten", "Kurz anrufen", "Bis später warten", "Meine Wirkung offen sagen"),
            q("Ihr merkt mitten im Streit, dass ihr denselben Satz völlig unterschiedlich verstanden habt. Was zuerst?", "Jeder sagt, was angekommen ist", "Originalsatz wiederholen", "Absicht erklären", "Kurz Pause machen"),
            q("Dein Ton kam härter an, als du ihn gemeint hast. Wie reagierst du?", "Wirkung anerkennen", "Absicht erklären", "Entschuldigen und neu formulieren", "Erst zuhören, was verletzt hat"),
            q("Dein Partner sagt „ist schon okay“, aber du glaubst es nicht. Was tust du?", "Behutsam nachfragen", "Raum geben und später fragen", "Eigene Unsicherheit sagen", "Nicht weiterbohren"),
            q("Ein Missverständnis entsteht vor Freunden. Was wäre dir lieber?", "Kurz sofort klären", "Später privat reden", "Nur stoppen, nicht lösen", "Mit einem Signal vertagen"),
            q("Wie verhindert ihr, dass ein kleines Missverständnis zum großen Streit wird?", "Keine Gedanken lesen", "Beim konkreten Thema bleiben", "Ton prüfen", "Früher nachfragen")
        ),
        "h500_257_geheimnisse_geheime_wahl" to listOf(
            q("Was ist für dich eher Privatsphäre als Geheimnis?", "Eigene Gedanken", "Gespräche mit Freunden", "Kleine Überraschung", "Vergangene peinliche Geschichte"),
            q("Was müsste dein Partner dir unbedingt sagen, auch wenn es unangenehm ist?", "Etwas, das uns beide betrifft", "Große Ausgabe", "Kontakt, der Grenzen berührt", "Wichtige Zukunftsentscheidung"),
            q("Wo brauchst du in einer Beziehung bewusst einen eigenen privaten Raum?", "Handy", "Freundschaften", "Tagebuch/Gedanken", "Zeit allein"),
            q("Was wäre für dich eher ein Vertrauensbruch?", "Bewusst etwas Wichtiges verschweigen", "Halbe Wahrheit", "Lüge zum Selbstschutz", "Gemeinsame Sache ohne Absprache erzählen"),
            q("Welche kleine Sache darf ruhig geheim bleiben?", "Geschenk", "Überraschungsplan", "Harmloser Insider", "Persönlicher Wunsch bis ich bereit bin"),
            q("Was wäre dir bei einem schwierigen Geheimnis wichtiger?", "Früh erfahren", "Ehrlich vollständig erfahren", "Ruhig erklärt bekommen", "Zeit zum Verarbeiten")
        ),
        "h500_258_alter_streit_memory" to listOf(
            GenQuestion("An welchen alten Streit denkst du heute viel milder zurück als damals – und warum?"),
            GenQuestion("Welcher frühere Streit hat euch im Nachhinein etwas Wichtiges übereinander beigebracht?"),
            GenQuestion("Bei welchem alten Konflikt würdest du heute gern einen einzigen Satz anders sagen?"),
            GenQuestion("Gab es einen Streit, bei dem ihr eigentlich über etwas anderes gestritten habt als über das offensichtliche Thema?"),
            GenQuestion("Welche Versöhnung nach einem alten Streit ist dir positiv im Gedächtnis geblieben?"),
            GenQuestion("Welche alte Konflikt-Schleife möchtest du zwischen euch auf keinen Fall wiederholen?")
        ),
        "h500_260_ehrlichkeit_offene_runde" to listOf(
            GenQuestion("Welche Wahrheit möchtest du von deinem Partner lieber früh hören, auch wenn sie unangenehm ist?"),
            GenQuestion("Wann wird Ehrlichkeit für dich unnötig hart statt hilfreich?"),
            GenQuestion("Bei welchem Thema fällt es dir am schwersten, sofort ehrlich zu sagen, was du brauchst?"),
            GenQuestion("Gibt es einen Unterschied zwischen Privatsphäre und Verschweigen, der dir besonders wichtig ist?"),
            GenQuestion("Wie möchtest du hören, dass dein Partner mit etwas an dir unzufrieden ist?"),
            GenQuestion("Welche Form von Ehrlichkeit schafft für dich besonders viel Vertrauen?")
        ),
        "h500_261_zuhoeren_entweder_oder" to listOf(
            q("Wenn du verletzt bist: Was brauchst du zuerst?", "Nur zuhören", "Nachfragen"),
            q("Wenn du ein Problem erzählst: Was ist hilfreicher?", "Erst verstehen", "Direkt Lösung suchen"),
            q("Was zeigt dir eher, dass jemand wirklich zuhört?", "Blickkontakt", "Später noch einmal darauf zurückkommen"),
            q("Wenn du emotional wirst: Was ist besser?", "Ausreden lassen", "Kurz zusammenfassen, was angekommen ist"),
            q("Wenn ihr euch im Kreis dreht: Was hilft eher?", "Pause", "Noch eine konkrete Frage"),
            q("Was verletzt dich eher?", "Unterbrechen", "Sofort widersprechen")
        ),
        "h500_262_recht_haben_wer_eher" to listOf(
            whoQ("Wer kann eher mitten im Streit sagen: „Okay, da hast du recht“?"),
            whoQ("Wer googelt eher noch schnell nach, wer sachlich recht hat?"),
            whoQ("Wer besteht eher auf dem genauen Wortlaut eines Satzes?"),
            whoQ("Wer merkt eher, wenn Recht haben gerade weniger wichtig ist als verstanden werden?"),
            whoQ("Wer kann eher einen Punkt stehen lassen, ohne ihn noch dreimal zu erklären?"),
            whoQ("Wer lacht später eher darüber, wie wichtig das Rechthaben fünf Minuten lang war?")
        ),
        "h500_263_tonfall_skala" to listOf(
            scaleQ("Wie stark beeinflusst der Tonfall, ob du eine Kritik überhaupt annehmen kannst?"),
            scaleQ("Wie schnell fühlst du dich durch einen genervten Ton angegriffen, auch wenn die Worte sachlich sind?"),
            scaleQ("Wie wichtig ist dir, dass dein Partner bei Ärger trotzdem respektvoll bleibt?"),
            scaleQ("Wie gut merkst du selbst, wenn dein Ton schärfer wird als beabsichtigt?"),
            scaleQ("Wie leicht kannst du einen Satz noch einmal ruhiger sagen, ohne dich dabei klein zu fühlen?"),
            scaleQ("Wie zufrieden bist du mit eurem Tonfall, wenn ihr wirklich gestresst seid?")
        ),
        "h500_264_timing_ranking" to listOf(
            q("Wann ist ein schwieriges Thema für dich am besten aufgehoben? Ordne.", "Sobald es auffällt", "Wenn beide ruhig sind", "Am selben Abend", "Nach einer Nacht"),
            q("Welches Timing ist bei Kritik am wichtigsten? Ordne.", "Nicht vor anderen", "Nicht zwischen Tür und Angel", "Nicht kurz vorm Schlafen", "Nicht mitten im Arbeitsstress"),
            q("Was macht einen guten Gesprächszeitpunkt aus? Ordne.", "Genug Zeit", "Keine Ablenkung", "Emotionen etwas runter", "Beide wirklich ansprechbar"),
            q("Wann sollte man ein Thema lieber vertagen? Ordne.", "Einer ist übermüdet", "Einer muss gleich los", "Ton wird respektlos", "Man dreht sich nur im Kreis"),
            q("Was sollte trotz schlechtem Timing sofort gesagt werden? Ordne.", "Klare Grenze", "Wichtige Wahrheit", "Akute Verletzung", "Etwas, das beide direkt betrifft"),
            q("Was hilft beim Vertagen, damit es nicht wie Weglaufen wirkt? Ordne.", "Neuen Zeitpunkt nennen", "Kurz Nähe zeigen", "Thema benennen", "Versprechen, zurückzukommen")
        ),
        "h500_265_gefuehle_zeigen_prognose" to listOf(
            q("Was fällt deinem Partner vermutlich leichter zu zeigen?", "Traurigkeit", "Ärger", "Unsicherheit", "Enttäuschung"),
            q("Woran merkst du vermutlich zuerst, dass dein Partner verletzt ist?", "Wird still", "Wird direkter", "Sucht Nähe", "Zieht sich zurück"),
            q("Was braucht dein Partner vermutlich, um ein schwieriges Gefühl auszusprechen?", "Zeit", "Direkte Frage", "Sicherheit ohne Bewertung", "Nähe"),
            q("Welche Reaktion hilft deinem Partner vermutlich am meisten, wenn er Gefühle zeigt?", "Zuhören", "Bestätigen", "Nachfragen", "Einfach dableiben"),
            q("Was macht es deinem Partner vermutlich schwerer, offen zu sein?", "Sofortige Lösung", "Witz im falschen Moment", "Verteidigung", "Ungeduld"),
            q("Welche Aussage würde dein Partner vermutlich gern öfter hören?", "„Du darfst so fühlen“", "„Ich höre dir zu“", "„Wir müssen das nicht sofort lösen“", "„Wir sind trotzdem okay“")
        ),
        "h500_266_nachgeben_szenario" to listOf(
            q("Du merkst, dass du gerade nur nachgeben willst, damit endlich Ruhe ist. Was wäre gesünder?", "Noch einmal Bedürfnis sagen", "Pause nehmen", "Nur beim kleinen Punkt nachgeben", "Heute nicht entscheiden"),
            q("Dein Partner hat bei einem Thema deutlich mehr daran hängen als du. Was machst du?", "Bewusst entgegenkommen", "Nach Bedeutung fragen", "Abwechseln vereinbaren", "Dritten Weg suchen"),
            q("Du hast mehrmals hintereinander nachgegeben und wirst langsam bitter. Was jetzt?", "Muster offen ansprechen", "Konkretes Beispiel nennen", "Neue Regel vereinbaren", "Eigene Grenze setzen"),
            q("Einer will Frieden, der andere echte Klärung. Was wäre ein fairer nächster Schritt?", "Kurze Pause plus fester Gesprächstermin", "Ein Punkt jetzt, Rest später", "Erst zuhören", "Keine Entscheidung unter Druck"),
            q("Wann ist Nachgeben für dich liebevoll statt unfair?", "Wenn es mir wenig bedeutet", "Wenn es ausgeglichen ist", "Wenn ich frei zustimme", "Wenn meine Grenze nicht betroffen ist"),
            q("Wann solltest du lieber nicht nachgeben?", "Bei einer wichtigen Grenze", "Aus Angst vor Reaktion", "Wenn du später Groll erwartest", "Wenn die Entscheidung beide langfristig betrifft")
        ),
        "h500_267_humor_im_streit_geheime_wahl" to listOf(
            q("Wann hilft Humor dir im Streit wirklich?", "Wenn die Spannung schon sinkt", "Bei einem kleinen Missverständnis", "Wenn wir beide lachen können", "Erst nach der Klärung"),
            q("Wann wäre ein Witz im Streit für dich komplett falsch?", "Wenn ich verletzt bin", "Wenn ich mich nicht ernst genommen fühle", "Bei einer Grenze", "Wenn der Witz auf meine Kosten geht"),
            q("Welche Art Humor kann euch eher wieder verbinden?", "Insider", "Über die Situation lachen", "Selbstironie", "Später darüber lachen"),
            q("Was darf Humor im Streit nie ersetzen?", "Entschuldigung", "Zuhören", "Verantwortung", "Klärung"),
            q("Was wäre ein gutes Zeichen, dass Humor wieder okay ist?", "Beide werden weicher", "Das Thema ist verstanden", "Niemand fühlt sich ausgelacht", "Einer startet selbst damit"),
            q("Welche Rolle soll Humor nach einem Konflikt spielen?", "Druck rausnehmen", "Nähe herstellen", "Perspektive geben", "Erst mal gar keine")
        ),
        "h500_270_versoehnung_offene_runde" to listOf(
            GenQuestion("Woran merkst du ganz konkret: Jetzt ist es zwischen uns wirklich wieder gut?"),
            GenQuestion("Was brauchst du nach einem größeren Streit eher für Versöhnung: Worte, Nähe, Zeit oder eine konkrete Veränderung? Warum?"),
            GenQuestion("Welche kleine Geste nach einem Konflikt erreicht dich stärker, als dein Partner vielleicht denkt?"),
            GenQuestion("Was darf bei Versöhnung nicht übersprungen werden, damit der Streit nicht nur zugedeckt ist?"),
            GenQuestion("Wie soll dein Partner auf dich zukommen, wenn du noch verletzt bist, aber die Verbindung wieder willst?"),
            GenQuestion("Welche Art von Wiederannäherung hat bei euch schon einmal besonders gut funktioniert?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        when {
            pack.id in archivedIds -> null
            pack.id in overrides -> pack.copy(questions = overrides.getValue(pack.id))
            else -> pack
        }
    }
}
