package com.example.data

/**
 * Kuratierter Rework für die bestehenden Harmony-Packs zu Nähe, Intimität und Sex.
 *
 * Die IDs bleiben identisch zu den Default-Packs aus Models.kt. Der Registry-Layer
 * setzt diese Versionen deshalb zur Laufzeit als gezielte Overrides ein, ohne die
 * große Legacy-Datei destruktiv umzuschreiben.
 */
object GeneratedHarmonySexIntimacyRework {
    const val VERSION: Long = 1788034200000L

    val PACKS: List<GenPack> = listOf(
        GenPack(
            id = "naehe",
            title = "Nähe & Intimität",
            cat = "tief",
            topic = "sex",
            type = "quiz",
            tags = listOf("unterhaltung", "intimitaet", "beziehung", "rework"),
            emoji = "💞",
            questions = listOf(
                GenQuestion(
                    q = "Wann fühlst du dich mir körperlich am nächsten?",
                    options = listOf("Beim Einschlafen nebeneinander", "Bei einer langen Umarmung", "Bei einer spontanen Berührung", "Wenn wir bewusst Zeit nur für uns haben")
                ),
                GenQuestion(
                    q = "Was macht für dich den Unterschied zwischen geliebt werden und begehrt werden?",
                    options = listOf("Aufmerksamkeit", "Worte", "Blicke & Körpersprache", "Dass der andere Initiative zeigt")
                ),
                GenQuestion(
                    q = "Woran merkst du, dass du dich bei mir körperlich wirklich fallen lassen kannst?",
                    options = listOf("Ich fühle mich nicht bewertet", "Ich kann Wünsche offen sagen", "Ich muss nichts leisten", "Ich vertraue deiner Reaktion")
                ),
                GenQuestion(
                    q = "Was könnte ich öfter tun, damit du dich attraktiv und begehrt fühlst?",
                    options = listOf("Es mir sagen", "Es mir durch Nähe zeigen", "Öfter Initiative zeigen", "Mich im Alltag bewusster wahrnehmen")
                ),
                GenQuestion(
                    q = "Was bringt dich am schnellsten vom Alltagsmodus in einen Moment echter Nähe?",
                    options = listOf("Ungestörte Zeit", "Eine bewusste Berührung", "Ein tiefes Gespräch", "Etwas Spontanes nur für uns")
                ),
                GenQuestion(
                    q = "Was fällt dir bei Nähe am schwersten?",
                    options = listOf("Einen Wunsch aussprechen", "Eine Grenze setzen", "Initiative zeigen", "Zu sagen, dass ich gerade Raum brauche")
                ),
                GenQuestion(
                    q = "Welche Art von Initiative fühlt sich für dich bei körperlicher Nähe besonders schön an?",
                    options = listOf("Direkt und selbstbewusst", "Langsam und vorsichtig", "Verspielt", "Ich beginne lieber selbst")
                ),
                GenQuestion(
                    q = "Von welcher Form von Nähe würdest du dir im Alltag heimlich etwas mehr wünschen?",
                    options = listOf("Kuscheln", "Küssen", "Bewusste Berührungen", "Einfach eng beieinander sein")
                ),
                GenQuestion(
                    q = "Wann fühlt sich eine Berührung für dich an, als würde sie genau das Richtige sagen?",
                    options = listOf("Wenn ich gestresst bin", "Wenn ich traurig bin", "Wenn wir uns wiedersehen", "In einem ganz normalen Moment")
                ),
                GenQuestion(
                    q = "Was sollte ich über deine Wünsche und Grenzen bei Nähe besser verstehen?",
                    options = listOf("Wann ich Nähe brauche", "Wann ich Freiraum brauche", "Wie ich Berührungen mag", "Dass sich meine Bedürfnisse je nach Stimmung ändern")
                ),
                GenQuestion(
                    q = "Was brauchst du nach einem besonders intimen Moment am ehesten?",
                    options = listOf("Nähe & Kuscheln", "Reden & Lachen", "Ruhe & Entspannung", "Ein bisschen Raum für mich")
                ),
                GenQuestion(
                    q = "Welche Wahrheit über Nähe zwischen uns würdest du leichter auswählen als laut aussprechen?",
                    options = listOf("Ich wünsche mir manchmal mehr", "Manchmal brauche ich mehr Freiraum", "Ich wünsche mir öfter Initiative von dir", "Ich möchte selbst mutiger sein")
                )
            )
        ),
        GenPack(
            id = "intimleben",
            title = "Unser Intimleben",
            cat = "reden",
            topic = "sex",
            type = "quiz",
            tags = listOf("reden", "sex", "intimitaet", "rework"),
            emoji = "🔥",
            questions = listOf(
                GenQuestion(
                    q = "Wie kann dein Partner Sex mit dir am schönsten initiieren?",
                    options = listOf("Direkt ansprechen", "Mit körperlicher Annäherung", "Erst Stimmung entstehen lassen", "Spontan und überraschend")
                ),
                GenQuestion(
                    q = "Zu welcher Tageszeit hast du am liebsten Sex?",
                    options = listOf("Morgens", "Tagsüber", "Abends", "Nachts", "Kommt ganz auf die Stimmung an")
                ),
                GenQuestion(
                    q = "Was macht guten Sex für dich am stärksten aus?",
                    options = listOf("Leidenschaft", "Emotionale Nähe", "Verspieltheit & Spaß", "Dass wir beide wirklich im Moment sind")
                ),
                GenQuestion(
                    q = "Welche Rolle spielt Vorspiel für dich bei gutem Sex?",
                    options = listOf("Sehr wichtig", "Oft wichtig, aber nicht immer", "Die Stimmung entscheidet", "Ich mag es lieber eher direkt")
                ),
                GenQuestion(
                    q = "Wie offen bist du dafür, neue Dinge beim Sex gemeinsam auszuprobieren?",
                    options = listOf("Sehr offen", "Gerne, wenn wir vorher darüber reden", "Ab und zu", "Ich mag Vertrautes lieber")
                ),
                GenQuestion(
                    q = "Wie wohl fühlst du dich mit Dirty Talk oder erotischen Worten beim Sex?",
                    options = listOf("Mag ich sehr", "Manchmal, wenn es passt", "Nur eher dezent", "Ist nicht mein Ding")
                ),
                GenQuestion(
                    q = "Wie stehst du dazu, gemeinsam erotische Inhalte anzuschauen?",
                    options = listOf("Fände ich spannend", "Ab und zu okay", "Nur wenn wir beide Lust darauf haben", "Lieber nicht")
                ),
                GenQuestion(
                    q = "Was beeinflusst deine Lust im Alltag am stärksten?",
                    options = listOf("Stress & Müdigkeit", "Emotionale Nähe", "Zeit & Privatsphäre", "Anziehung & Stimmung")
                ),
                GenQuestion(
                    q = "Wenn einer von uns Lust auf Sex hat und der andere gerade nicht: Was fühlt sich für dich am besten an?",
                    options = listOf("Offen und ohne Druck sagen", "Nähe ohne Erwartung anbieten", "Auf einen anderen Moment verschieben", "Kurz darüber reden, was gerade gebraucht wird")
                ),
                GenQuestion(
                    q = "Was wünschst du dir bei sexuellen Wünschen am meisten von deinem Partner?",
                    options = listOf("Dass ich alles sagen darf", "Dass nachgefragt wird", "Dass Grenzen selbstverständlich respektiert werden", "Dass wir neugierig bleiben")
                ),
                GenQuestion(
                    q = "Wie wichtig ist dir Spontanität bei Sex?",
                    options = listOf("Sehr wichtig", "Schön, aber kein Muss", "Ich mag auch bewusst geplante Zeit", "Die Mischung macht es")
                ),
                GenQuestion(
                    q = "Wie fühlst du dich nach dem Sex am liebsten?",
                    options = listOf("Kuschelbedürftig", "Redselig und verspielt", "Müde und entspannt", "Ich brauche erst einmal etwas Raum")
                ),
                GenQuestion(
                    q = "Was sollte in unserem Sexleben auch nach vielen gemeinsamen Jahren niemals selbstverständlich werden?",
                    options = listOf("Begehren zeigen", "Über Wünsche reden", "Neugierig aufeinander bleiben", "Sich Zeit füreinander nehmen")
                )
            )
        )
    )
}
