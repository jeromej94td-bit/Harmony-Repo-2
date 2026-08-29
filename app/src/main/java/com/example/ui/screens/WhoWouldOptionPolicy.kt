package com.example.ui.screens

enum class WhoWouldBottomRole {
    BOTH,
    NOBODY,
    ALTERNATIVE
}

data class WhoWouldBottomOption(
    val label: String,
    val role: WhoWouldBottomRole
)

data class WhoWouldOptionLayout(
    val bottomOptions: List<WhoWouldBottomOption>,
    val useCanonicalInstruction: Boolean
)

/**
 * Legacy "Wer würde eher?" packs use several valid third/fourth answers besides "Beide/Niemand".
 * Keep those labels as neutral alternatives instead of visually pretending they mean both/nobody.
 */
internal object WhoWouldOptionPolicy {
    private val bothLabels = setOf(
        "beide",
        "beide gleich",
        "beide zusammen",
        "wir beide",
        "wir beide gleich",
        "both",
        "both of us"
    )
    private val nobodyLabels = setOf(
        "niemand",
        "niemand von uns",
        "keiner",
        "keiner von uns",
        "nobody",
        "neither",
        "neither of us"
    )

    fun resolve(options: List<String>): WhoWouldOptionLayout {
        val bottom = options.drop(2).take(2).map { label ->
            WhoWouldBottomOption(
                label = label,
                role = when (normalize(label)) {
                    in bothLabels -> WhoWouldBottomRole.BOTH
                    in nobodyLabels -> WhoWouldBottomRole.NOBODY
                    else -> WhoWouldBottomRole.ALTERNATIVE
                }
            )
        }
        return WhoWouldOptionLayout(
            bottomOptions = bottom,
            useCanonicalInstruction = bottom.size == 2 &&
                bottom[0].role == WhoWouldBottomRole.BOTH &&
                bottom[1].role == WhoWouldBottomRole.NOBODY
        )
    }

    private fun normalize(value: String): String =
        value.trim().lowercase().replace(Regex("\\s+"), " ")
}
