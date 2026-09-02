package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.ui.session.AppSessionViewModel
import com.example.ui.session.SessionPhase
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import java.io.File

@Composable
internal fun SessionAwareTopBarAvatars(
    userName: String,
    partnerName: String,
    userAvatarPath: String?,
    partnerAvatarPath: String?,
    onProfileClick: () -> Unit
) {
    val sessionViewModel: AppSessionViewModel = viewModel()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val liveSession = sessionState.session
    val isDemoMode = sessionState.phase == SessionPhase.DEMO
    val livePartner = liveSession?.partner
    val showPartnerAvatar = isDemoMode || livePartner != null

    val resolvedUserName = if (isDemoMode) userName else liveSession?.profile?.displayName ?: userName
    val resolvedPartnerName = if (isDemoMode) partnerName else livePartner?.displayName.orEmpty()
    val remoteUserAvatar = if (isDemoMode) null else liveSession?.profile?.avatarUrl
    val remotePartnerAvatar = if (isDemoMode) null else livePartner?.avatarUrl

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClick = onProfileClick)
            .padding(2.dp)
            .testTag("avatars_button"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPinkSoft))),
            contentAlignment = Alignment.Center
        ) {
            when {
                !remoteUserAvatar.isNullOrBlank() -> AuthenticatedAvatarImage(
                    avatarRef = remoteUserAvatar,
                    displayName = resolvedUserName,
                    contentDescription = resolvedUserName,
                    modifier = Modifier.fillMaxSize()
                )
                isDemoMode && !userAvatarPath.isNullOrBlank() -> AsyncImage(
                    model = File(userAvatarPath),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                else -> androidx.compose.material3.Text(
                    resolvedUserName.take(1).ifBlank { "H" }.uppercase(),
                    color = Color.White,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 13.sp
                )
            }
        }

        if (showPartnerAvatar) {
            Box(
                modifier = Modifier
                    .offset(x = (-12).dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .border(2.dp, HarmonySurface, CircleShape)
                    .background(Brush.linearGradient(listOf(HarmonyPurple, HarmonyPurpleLight))),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !remotePartnerAvatar.isNullOrBlank() -> AuthenticatedAvatarImage(
                        avatarRef = remotePartnerAvatar,
                        displayName = resolvedPartnerName,
                        contentDescription = resolvedPartnerName,
                        modifier = Modifier.fillMaxSize()
                    )
                    isDemoMode && !partnerAvatarPath.isNullOrBlank() -> AsyncImage(
                        model = File(partnerAvatarPath),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    else -> androidx.compose.material3.Text(
                        resolvedPartnerName.take(1).ifBlank { "?" }.uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
