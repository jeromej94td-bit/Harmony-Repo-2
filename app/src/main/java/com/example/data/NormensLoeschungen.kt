package com.example.data

/**
 * Zentrale Liste fuer bewusst entfernte Harmony-Fragen.
 *
 * Die Kennungen 1.1, 1.2, 1.3, ... sind die fortlaufende Loeschserie.
 * So bleiben manuelle Loeschungen auch dann wirksam, wenn generierte
 * Fragen-Dateien spaeter neu erzeugt werden.
 */
object NormensLoeschungen {
    data class Loeschung(
        val id: String,
        val packId: String,
        val frage: String,
    )

    val EINTRAEGE: List<Loeschung> = listOf(
        Loeschung(
            id = "1.1",
            packId = "h500_430_team_zukunft_offene_runde",
            frage = "Was möchtest du mir für unser gemeinsames Team heute von Herzen sagen?",
        ),
    )

    fun apply(packs: List<GenPack>): List<GenPack> {
        val nachPack = EINTRAEGE.groupBy { it.packId }
        return packs.map { pack ->
            val geloeschteFragen = nachPack[pack.id]
                ?.map { it.frage }
                ?.toSet()
                .orEmpty()

            if (geloeschteFragen.isEmpty()) {
                pack
            } else {
                pack.copy(
                    questions = pack.questions.filterNot { it.q in geloeschteFragen }
                )
            }
        }
    }
}
