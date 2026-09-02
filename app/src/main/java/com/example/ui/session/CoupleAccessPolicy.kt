package com.example.ui.session

internal fun requiresPartnerForTab(tabIndex: Int): Boolean = tabIndex == 2 || tabIndex == 3

internal fun isConnectedPartnerName(partnerName: String?): Boolean {
    val normalized = partnerName?.trim().orEmpty()
    return normalized.isNotEmpty() && !normalized.equals("Partner", ignoreCase = true)
}

internal fun shouldPollPartnerInvite(isPaired: Boolean, hasActiveInvite: Boolean): Boolean =
    !isPaired && hasActiveInvite
