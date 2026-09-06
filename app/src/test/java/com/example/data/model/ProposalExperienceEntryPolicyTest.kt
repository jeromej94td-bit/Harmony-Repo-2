package com.example.data.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ProposalExperienceEntryPolicyTest {

    @Test
    fun `perfect proposal opens its dedicated fullscreen experience`() {
        assertTrue(ProposalExperienceEntryPolicy.opensFullscreenExperience("antrag"))
        assertFalse(ProposalExperienceEntryPolicy.opensFullscreenExperience("essenreden"))
    }
}
