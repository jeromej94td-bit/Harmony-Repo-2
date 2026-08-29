package com.example.data.model

/**
 * Routing-Regeln für den kuratierten Sex-&-Intimität-Bereich.
 *
 * Die Policy bleibt absichtlich rein und UI-unabhängig: Der Runner kann damit
 * entscheiden, welche Fragen als geheime Zwei-Personen-Wahl gespielt werden,
 * wann ein kompakter Antwortmodus sinnvoll ist und wo ein neutrales Überspringen
 * angeboten werden darf.
 */
object SexIntimacyRevealPolicy {
    private val packIds = setOf("naehe", "intimleben")

    private val privateRevealQuestions = setOf(
        "Zu welcher Tageszeit hast du am liebsten Sex?",
        "Wie oft würdest du dir Sex idealerweise wünschen – unabhängig davon, wie oft wir aktuell Sex haben?",
        "Welche Rolle spielt Vorspiel für dich bei gutem Sex?",
        "Was beeinflusst deine Lust im Alltag am stärksten?",
        "Magst du es lieber, wenn Sex spontan entsteht oder wenn wir uns bewusst Zeit dafür nehmen?",
        "Wie möchtest du am liebsten merken, dass ich gerade Lust auf dich habe?"
    )

    fun isSexIntimacyPack(packId: String, topic: String): Boolean =
        topic == "sex" && packId in packIds

    fun allowsSkip(packId: String, topic: String): Boolean =
        isSexIntimacyPack(packId, topic)

    fun useCompactAnswerLayout(packId: String, topic: String, optionCount: Int): Boolean =
        isSexIntimacyPack(packId, topic) && optionCount >= 4

    fun usesPrivateCoupleReveal(packId: String, topic: String, questionText: String): Boolean =
        isSexIntimacyPack(packId, topic) && questionText in privateRevealQuestions
}
