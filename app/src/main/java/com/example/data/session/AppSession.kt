package com.example.data.session

enum class PairingState {
    SOLO,
    PAIRED
}

data class UserProfile(
    val userId: String,
    val displayName: String,
    val avatarUrl: String?
)

data class AppSession(
    val userId: String,
    val email: String?,
    val profile: UserProfile,
    val coupleId: String?,
    val partner: UserProfile?
) {
    val isPaired: Boolean
        get() = coupleId != null && partner != null

    val pairingState: PairingState
        get() = if (isPaired) PairingState.PAIRED else PairingState.SOLO
}

data class PartnerInvite(
    val code: String,
    val expiresAt: String
)
