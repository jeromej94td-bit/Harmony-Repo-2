package com.example.data

/** Explicit Stage 05.5 curation for Harmony-360 Section 18 — Humor & Lachen. */
object Harmony360HumorSectionCuration {
    internal enum class CurationDecision { KEEP, REWRITE, MERGE, ARCHIVE }

    private const val SECTION = "h360_section_18_humor_lachen"
    private val who = listOf("{user}", "{partner}", "Beide", "Niemand")

    private fun q(text: String, vararg options: String): GenQuestion =
        GenQuestion(q = text, options = options.toList())

    private fun open(text: String): GenQuestion = GenQuestion(q = text)
    private fun whoQ(text: String): GenQuestion = GenQuestion(q = text, options = who)

    internal val decisions: Map<String, CurationDecision> = linkedMapOf(
        "h500_371_humor_entweder_oder" to CurationDecision.REWRITE,
        "h500_372_lachen_wer_eher" to CurationDecision.REWRITE,
        "h500_373_schadenfreude_skala" to CurationDecision.ARCHIVE,
        "h500_374_witze_ranking" to CurationDecision.ARCHIVE,
        "h500_375_comedy_prognose" to CurationDecision.ARCHIVE,
        "h500_376_peinliche_momente_szenario" to CurationDecision.REWRITE,
        "h500_377_insider_witze_geheime_wahl" to CurationDecision.REWRITE,
        "h500_378_lachflashs_memory" to CurationDecision.REWRITE,
        "h500_379_humor_im_alltag_prioritaet" to CurationDecision.REWRITE,
        "h500_380_ironie_offene_runde" to CurationDecision.REWRITE,
        "h500_381_galgenhumor_entweder_oder" to CurationDecision.ARCHIVE,
        "h500_382_necken_wer_eher" to CurationDecision.REWRITE,
        "h500_383_kitzelig_skala" to CurationDecision.ARCHIVE,
        "h500_384_karikaturen_ranking" to CurationDecision.ARCHIVE,
        "h500_385_parodien_prognose" to CurationDecision.ARCHIVE,
        "h500_386_missgeschicke_szenario" to CurationDecision.ARCHIVE,
        "h500_387_schwarzer_humor_geheime_wahl" to CurationDecision.REWRITE,
        "h500_390_gemeinsam_lachen_offene_runde" to CurationDecision.REWRITE
    )

    private val overrides: Map<String, List<GenQuestion>> = mapOf(
        "h500_371_humor_entweder_oder" to listOf(
            q("Welche Art Humor bringt dich eher zum Lachen?", "Trocken & subtil", "Laut & albern"),
            q("Was trifft deinen Humor eher?", "Wortwitz", "Situationskomik"),
            q("Was macht dir mehr Spaß?", "Selbst einen Spruch bringen", "Über den anderen lachen müssen"),
            q("Welche Comedy magst du eher?", "Alltagsbeobachtungen", "Völlig absurde Ideen"),
            q("Wenn etwas schiefgeht?", "Erst lösen, später lachen", "Wenn möglich direkt Humor reinbringen"),
            q("Was ist dir bei Humor wichtiger?", "Überraschend sein", "Niemanden unnötig verletzen")
        ),
        "h500_372_lachen_wer_eher" to listOf(
            whoQ("Wer bringt den anderen eher zum Lachen, wenn die Stimmung gerade schwer ist?"),
            whoQ("Wer bekommt eher einen Lachanfall in einem völlig unpassenden Moment?"),
            whoQ("Wer kann eher über einen eigenen kleinen Fehler lachen?"),
            whoQ("Wer schickt dem anderen eher etwas Lustiges mitten im Alltag?"),
            whoQ("Wer kann bei einer absurden Situation schlechter ernst bleiben?"),
            whoQ("Wer erinnert sich eher Monate später noch an einen Satz, über den ihr beide lachen musstet?")
        ),
        "h500_376_peinliche_momente_szenario" to listOf(
            q("Dein Partner erzählt vor anderen eine peinliche Geschichte über dich. Was wäre dir am liebsten?", "Kurz stoppen und Grenze zeigen", "Mitlachen, wenn es wirklich okay ist", "Thema elegant wechseln", "Später unter vier Augen ansprechen"),
            q("Dir passiert vor anderen ein richtig unangenehmer Versprecher. Wie soll dein Partner reagieren?", "Schnell retten", "Mit mir darüber lachen", "Einfach normal weitermachen", "Kurz Nähe oder Rückhalt geben"),
            q("Dein Partner stolpert öffentlich und alle schauen. Was tust du zuerst?", "Prüfen, ob alles okay ist", "Diskret helfen", "Nur lachen, wenn er selbst lacht", "Die Aufmerksamkeit weglenken"),
            q("Ein gemeinsamer Witz kommt bei einer anderen Person schlecht an. Was macht ihr?", "Direkt entschuldigen", "Kurz erklären, aber nicht rechtfertigen", "Thema beenden", "Später gemeinsam besprechen, was zu weit ging"),
            q("Einer von euch wird auf einer Feier unangenehm aufgezogen. Was sollte der andere tun?", "Klar Partei ergreifen", "Mit einem Themenwechsel helfen", "Nachfragen, ob Hilfe gewünscht ist", "Gemeinsam die Situation verlassen"),
            q("Nach einem peinlichen Moment ist einer noch lange beschämt. Was hilft am meisten?", "Nicht weiter darauf herumreiten", "Normalität herstellen", "Liebevoll relativieren", "Zuhören, wenn er darüber reden will")
        ),
        "h500_377_insider_witze_geheime_wahl" to listOf(
            q("Welche Art Insider zwischen euch macht dir heimlich am meisten Spaß?", "Ein völlig sinnloses Codewort", "Eine gemeinsame Imitation", "Ein Satz aus einer alten Situation", "Ein Blick, den nur ihr versteht"),
            q("Welchen Insider würdest du gern wieder öfter benutzen?", "Einen aus eurer Anfangszeit", "Einen aus einem Urlaub", "Einen aus einer Panne", "Einen aus eurem Alltag"),
            q("Was ist das Schönste an einem Insider, den nur ihr beide versteht?", "Sofortige Verbundenheit", "Niemand braucht eine Erklärung", "Er holt eine Erinnerung zurück", "Er kann Spannung rausnehmen"),
            q("Wo wäre ein Insider für dich am lustigsten?", "In einer ernsten Alltagssituation", "Unterwegs in der Öffentlichkeit", "Beim Schreiben", "Bei einem Familien- oder Freundetreffen"),
            q("Welche kleine Geheim-Sprache würdest du als Paar gern haben?", "Code für 'Rette mich hier raus'", "Code für 'Ich liebe dich'", "Code für 'Ich muss lachen'", "Code für 'Später reden wir darüber'"),
            q("Was sollte bei Insidern immer gelten?", "Nie gegen den anderen verwenden", "Private Dinge bleiben privat", "Ein Stoppsignal zählt sofort", "Beide dürfen wirklich darüber lachen")
        ),
        "h500_378_lachflashs_memory" to listOf(
            open("Wann hattet ihr zusammen einen Lachflash, bei dem ihr kaum noch aufhören konntet?"),
            open("Bei welchem eigentlich völlig unwichtigen Moment musst du heute noch lachen, wenn du daran denkst?"),
            open("Wann hat dein Partner dich zum ersten Mal so richtig unerwartet zum Lachen gebracht?"),
            open("Welche gemeinsame Panne wurde im Nachhinein zu einer eurer lustigsten Erinnerungen?"),
            open("Welcher Satz oder Versprecher von euch hat sich dauerhaft in eurem Humor festgesetzt?"),
            open("Welche Situation würdest du gern noch einmal erleben, nur um wieder genauso zusammen zu lachen?")
        ),
        "h500_379_humor_im_alltag_prioritaet" to listOf(
            q("Wobei hilft Humor euch im Alltag am meisten?", "Spannung rausnehmen", "Routine leichter machen", "Nähe herstellen", "Kleine Fehler abhaken"),
            q("Was sollte Humor in einer Beziehung niemals ersetzen?", "Eine ehrliche Entschuldigung", "Ein ernstes Gespräch", "Ein klares Stoppsignal", "Echte Rücksicht"),
            q("Wo wünschst du dir im Alltag mehr Leichtigkeit?", "Bei Stress", "Bei Haushalt & Organisation", "Bei kleinen Meinungsverschiedenheiten", "Wenn Pläne schiefgehen"),
            q("Was ist bei einem Witz wichtiger als die Pointe?", "Der richtige Moment", "Dass beide lachen können", "Dass Privates privat bleibt", "Dass ein Nein respektiert wird"),
            q("Welche Form von Humor verbindet euch am meisten?", "Gemeinsame Insider", "Spontane Sprüche", "Über euch selbst lachen", "Absurde gemeinsame Ideen"),
            q("Was sollte auch in ernsten Phasen nicht ganz verschwinden?", "Ein liebevoller Blick", "Ein kleiner Insider", "Die Fähigkeit über Kleinigkeiten zu lachen", "Humor ohne Probleme kleinzureden")
        ),
        "h500_380_ironie_offene_runde" to listOf(
            open("Wann funktioniert Ironie zwischen euch richtig gut – und wann eher nicht?"),
            open("Gab es einen Moment, in dem deine Ironie missverstanden wurde? Was hätte geholfen?"),
            open("Bei welchen Themen möchtest du lieber klare Worte statt ironischer Sprüche hören?"),
            open("Woran merkst du, ob dein Partner gerade Spaß macht oder eigentlich etwas Ernstes sagen will?"),
            open("Welche Grenze sollte Ironie in einem Streit für dich niemals überschreiten?"),
            open("Wie kann man nach einem ironischen Spruch gut reparieren, wenn er den anderen doch getroffen hat?")
        ),
        "h500_382_necken_wer_eher" to listOf(
            whoQ("Wer neckt den anderen eher wegen kleiner Alltagsmacken?"),
            whoQ("Wer merkt schneller, wenn aus Spaß gerade genug geworden ist?"),
            whoQ("Wer kann eher direkt sagen: Das war mir jetzt zu viel?"),
            whoQ("Wer entschuldigt sich eher sofort, wenn ein Spruch doch getroffen hat?"),
            whoQ("Wer findet eher eine liebevolle Art zu necken, ohne den anderen bloßzustellen?"),
            whoQ("Wer muss eher selbst lachen, bevor der eigene Neck-Spruch überhaupt fertig ist?")
        ),
        "h500_387_schwarzer_humor_geheime_wahl" to listOf(
            q("Wie stehst du heimlich zu sehr schwarzem Humor?", "Mag ich ziemlich gern", "Nur bei manchen Themen", "Nur im vertrauten Kreis", "Eher nicht mein Humor"),
            q("Wann funktioniert schwarzer Humor für dich am ehesten?", "Wenn alle Beteiligten den Ton kennen", "Wenn niemand persönlich getroffen wird", "Wenn er Druck aus einer schweren Lage nimmt", "Nur wenn ich selbst das Thema eröffne"),
            q("Welche Grenze darf schwarzer Humor für dich nie überschreiten?", "Persönliche Wunden ausnutzen", "Private Geheimnisse verwenden", "Ein klares Stoppsignal ignorieren", "Jemanden gezielt kleinmachen"),
            q("Was sollte dein Partner bei deinem Humor besser kennen?", "Welche Themen tabu sind", "Wann ich nur Spannung abbauen will", "Wann ich gerade keinen Witz brauche", "Wie direkt mein Stoppsignal ist"),
            q("Wenn ein schwarzer Witz danebenliegt: Was ist dir am wichtigsten?", "Nicht verteidigen, sondern zuhören", "Kurz ehrlich entschuldigen", "Grenze für später merken", "Das Thema nicht weiter treiben"),
            q("Was macht dunklen Humor zwischen zwei Menschen überhaupt okay?", "Vertrauen", "Gemeinsame Grenzen", "Gegenseitiger Respekt", "Dass beide jederzeit aussteigen können")
        ),
        "h500_390_gemeinsam_lachen_offene_runde" to listOf(
            open("Was bringt dich an deinem Partner am zuverlässigsten zum Lachen?"),
            open("Welche Art von Humor ist etwas ganz Eigenes zwischen euch geworden?"),
            open("Wann hat gemeinsames Lachen euch schon einmal aus einer angespannten Situation geholfen?"),
            open("Bei welchem Thema möchtest du auch im Spaß besonders respektvoll behandelt werden?"),
            open("Was sagt die Art, wie ihr zusammen lacht, über eure Beziehung aus?"),
            open("Welche neue gemeinsame Erinnerung möchtest du unbedingt einmal haben, über die ihr später jahrelang lachen könnt?")
        )
    )

    fun apply(packs: List<GenPack>): List<GenPack> = packs.mapNotNull { pack ->
        if (SECTION !in pack.tags) return@mapNotNull pack

        when (decisions[pack.id]) {
            CurationDecision.ARCHIVE -> null
            CurationDecision.REWRITE -> pack.copy(questions = overrides.getValue(pack.id))
            CurationDecision.KEEP, CurationDecision.MERGE -> pack
            null -> null
        }
    }
}
