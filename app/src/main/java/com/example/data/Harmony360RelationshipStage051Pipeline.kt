package com.example.data

/** Single final Stage-05.1 list pipeline; section modules stay explicit and independently testable. */
object Harmony360RelationshipStage051Pipeline {
    fun apply(packs: List<GenPack>): List<GenPack> =
        Harmony360NeedNowQuickGame.appendTo(
            Harmony360RelationshipSection12Curation.apply(
                Harmony360RelationshipSection06Curation.apply(
                    Harmony360RelationshipQualityRework.apply(packs)
                )
            )
        )
}
