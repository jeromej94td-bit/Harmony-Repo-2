package com.example.ui.session

import com.example.data.model.ProfileEntity
import com.example.data.session.AppSession
import com.example.data.session.UserProfile
import org.junit.Assert.assertEquals
import org.junit.Test

class SessionProfilePresentationTest {

    @Test
    fun `uses the paired account avatars on question choice cards`() {
        val localProfile = ProfileEntity(
            userName = "Local name",
            partnerName = "Local partner",
            userAvatarPath = "local-user.jpg",
            partnerAvatarPath = "local-partner.jpg"
        )
        val session = AppSession(
            userId = "user-1",
            email = "user@example.com",
            profile = UserProfile("user-1", "Jerome", "https://cdn.example/user.jpg"),
            coupleId = "couple-1",
            partner = UserProfile("user-2", "Alex", "https://cdn.example/partner.jpg")
        )

        val display = session.questionDisplayProfile(localProfile)

        assertEquals("Jerome", display.userName)
        assertEquals("Alex", display.partnerName)
        assertEquals("https://cdn.example/user.jpg", display.userAvatarPath)
        assertEquals("https://cdn.example/partner.jpg", display.partnerAvatarPath)
    }
}
