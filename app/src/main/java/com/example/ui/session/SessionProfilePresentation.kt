package com.example.ui.session

import com.example.data.model.ProfileEntity
import com.example.data.session.AppSession

internal fun AppSession.questionDisplayProfile(localProfile: ProfileEntity): ProfileEntity = localProfile.copy(
    userName = profile.displayName,
    partnerName = partner?.displayName ?: localProfile.partnerName,
    userAvatarPath = profile.avatarUrl ?: localProfile.userAvatarPath,
    partnerAvatarPath = partner?.avatarUrl ?: localProfile.partnerAvatarPath
)
