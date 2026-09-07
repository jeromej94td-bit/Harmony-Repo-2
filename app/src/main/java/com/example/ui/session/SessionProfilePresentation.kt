package com.example.ui.session

import com.example.data.model.ProfileEntity
import com.example.data.session.AppSession

internal fun AppSession.questionDisplayProfile(
    localProfile: ProfileEntity,
    useLocalAvatarFallback: Boolean = true
): ProfileEntity = localProfile.copy(
    userName = profile.displayName,
    partnerName = partner?.displayName ?: localProfile.partnerName,
    userAvatarPath = profile.avatarUrl ?: localProfile.userAvatarPath.takeIf { useLocalAvatarFallback },
    partnerAvatarPath = partner?.avatarUrl ?: localProfile.partnerAvatarPath.takeIf { useLocalAvatarFallback }
)
