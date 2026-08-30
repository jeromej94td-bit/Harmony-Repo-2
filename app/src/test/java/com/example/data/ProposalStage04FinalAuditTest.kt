package com.example.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalStage04FinalAuditTest {

    @Test
    fun `stage 04 final audit passes all migration catalogue archive and journey contracts`() {
        val report = ProposalStage04FinalAudit.run()

        assertTrue(report.failures.joinToString("\n"), report.failures.isEmpty())
        assertEquals(5, report.legacyPackCount)
        assertEquals(4, report.bouquetRoundCount)
        assertEquals(4, report.weddingStyleRoundCount)
        assertEquals(6, report.weddingOpenPromptCount)
        assertEquals(10, report.ringAssetReuseCount)
        assertEquals(35, report.proposalJourneyPositionCount)
    }
}
