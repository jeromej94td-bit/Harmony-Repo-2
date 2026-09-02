package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.session.AppSession
import com.example.data.session.PartnerInvite
import com.example.data.session.UserProfile
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyText

private enum class PartnerConnectionMode {
    CHOOSE,
    ENTER_CODE,
    GENERATED_CODE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartnerConnectionSheet(
    session: AppSession,
    activeInvite: PartnerInvite?,
    actionInProgress: Boolean,
    errorMessage: String?,
    onDismiss: () -> Unit,
    onCreateCode: () -> Unit,
    onJoinCode: (String) -> Unit,
    onClearInvite: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var mode by remember { mutableStateOf(PartnerConnectionMode.CHOOSE) }
    var codeInput by remember { mutableStateOf("") }

    LaunchedEffect(activeInvite) {
        if (activeInvite != null) mode = PartnerConnectionMode.GENERATED_CODE
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = HarmonyBg,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("💕", fontSize = 34.sp)
            Spacer(Modifier.height(8.dp))

            if (session.isPaired && session.partner != null) {
                PairedPartnerContent(
                    session = session,
                    actionInProgress = actionInProgress,
                    onDisconnect = onDisconnect
                )
            } else {
                Text(
                    text = "Partner verbinden",
                    color = HarmonyText,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Du kannst Harmony auch alleine entdecken. Wenn dein Partner bereit ist, verbindet ihr eure Konten mit einem einmaligen Code.",
                    color = HarmonyMuted,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(22.dp))

                when (mode) {
                    PartnerConnectionMode.CHOOSE -> ChoosePairingMethod(
                        enabled = !actionInProgress,
                        onCreateCode = onCreateCode,
                        onEnterCode = { mode = PartnerConnectionMode.ENTER_CODE }
                    )

                    PartnerConnectionMode.ENTER_CODE -> EnterPartnerCode(
                        code = codeInput,
                        enabled = !actionInProgress,
                        onCodeChange = { input ->
                            codeInput = input.uppercase()
                                .filter { it.isLetterOrDigit() }
                                .take(6)
                        },
                        onConnect = { onJoinCode(codeInput) },
                        onBack = { mode = PartnerConnectionMode.CHOOSE }
                    )

                    PartnerConnectionMode.GENERATED_CODE -> GeneratedPartnerCode(
                        invite = activeInvite,
                        enabled = !actionInProgress,
                        onGenerateAgain = onCreateCode,
                        onBack = {
                            onClearInvite()
                            mode = PartnerConnectionMode.CHOOSE
                        }
                    )
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                Spacer(Modifier.height(14.dp))
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF8CA8),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(16.dp))
            TextButton(onClick = onDismiss, enabled = !actionInProgress) {
                Text("Schließen", color = HarmonyPink, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
        }
    }
}

@Composable
private fun ChoosePairingMethod(
    enabled: Boolean,
    onCreateCode: () -> Unit,
    onEnterCode: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        HarmonyPrimaryAction(
            text = "Code erstellen",
            enabled = enabled,
            onClick = onCreateCode
        )
        HarmonySecondaryAction(
            text = "Code eingeben",
            enabled = enabled,
            onClick = onEnterCode
        )
    }
}

@Composable
private fun EnterPartnerCode(
    code: String,
    enabled: Boolean,
    onCodeChange: (String) -> Unit,
    onConnect: () -> Unit,
    onBack: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Code deines Partners",
            color = HarmonyText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Gib den sechsstelligen Harmony-Code ein. Danach seid genau ihr beide miteinander verbunden.",
            color = HarmonyMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))
        OutlinedTextField(
            value = code,
            onValueChange = onCodeChange,
            singleLine = true,
            placeholder = { Text("ABC123", color = HarmonyMuted) },
            textStyle = androidx.compose.ui.text.TextStyle(
                color = HarmonyText,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HarmonyPink,
                unfocusedBorderColor = HarmonyLine,
                focusedContainerColor = Color.White.copy(alpha = 0.05f),
                unfocusedContainerColor = Color.White.copy(alpha = 0.04f),
                cursorColor = HarmonyPink
            ),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
        HarmonyPrimaryAction(
            text = "Verbinden",
            enabled = enabled && code.length == 6,
            onClick = onConnect
        )
        TextButton(onClick = onBack, enabled = enabled) {
            Text("Zurück", color = HarmonyMuted)
        }
    }
}

@Composable
private fun GeneratedPartnerCode(
    invite: PartnerInvite?,
    enabled: Boolean,
    onGenerateAgain: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            "Dein Harmony-Code",
            color = HarmonyText,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Teile diesen Code mit genau einer Person. Sobald er eingelöst wurde, ist eure Verbindung hergestellt.",
            color = HarmonyMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(
                    Brush.linearGradient(
                        listOf(
                            HarmonyPink.copy(alpha = 0.28f),
                            HarmonyPurple.copy(alpha = 0.30f)
                        )
                    )
                )
                .border(1.dp, HarmonyPink.copy(alpha = 0.5f), RoundedCornerShape(24.dp))
                .padding(vertical = 24.dp, horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = invite?.code?.chunked(3)?.joinToString(" ") ?: "··· ···",
                color = Color.White,
                fontSize = 31.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 5.sp
            )
        }

        Spacer(Modifier.height(8.dp))
        Text("24 Stunden gültig", color = HarmonyMuted, fontSize = 12.sp)
        Spacer(Modifier.height(16.dp))

        HarmonyPrimaryAction(
            text = "Code teilen",
            enabled = enabled && invite != null,
            onClick = {
                val code = invite?.code ?: return@HarmonyPrimaryAction
                val share = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "Verbinde dich mit mir in Harmony 💕 Mein Code: $code"
                    )
                }
                context.startActivity(Intent.createChooser(share, "Harmony-Code teilen"))
            }
        )
        Spacer(Modifier.height(10.dp))
        HarmonySecondaryAction(
            text = "Neuen Code erstellen",
            enabled = enabled,
            onClick = onGenerateAgain
        )
        TextButton(onClick = onBack, enabled = enabled) {
            Text("Andere Option wählen", color = HarmonyMuted)
        }
    }
}

@Composable
private fun PairedPartnerContent(
    session: AppSession,
    actionInProgress: Boolean,
    onDisconnect: () -> Unit
) {
    val partner = requireNotNull(session.partner)
    Text(
        text = "Ihr seid verbunden 💕",
        color = HarmonyText,
        fontSize = 23.sp,
        fontWeight = FontWeight.ExtraBold
    )
    Spacer(Modifier.height(8.dp))
    Text(
        text = "Harmony erkennt euch jetzt als Couple.",
        color = HarmonyMuted,
        fontSize = 14.sp
    )
    Spacer(Modifier.height(22.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RemoteProfileAvatar(session.profile)
        Text("  💗  ", fontSize = 24.sp)
        RemoteProfileAvatar(partner)
    }
    Spacer(Modifier.height(10.dp))
    Text(
        "${session.profile.displayName} & ${partner.displayName}",
        color = HarmonyText,
        fontWeight = FontWeight.Bold,
        fontSize = 17.sp
    )
    Spacer(Modifier.height(24.dp))
    HarmonySecondaryAction(
        text = "Verbindung trennen",
        enabled = !actionInProgress,
        onClick = onDisconnect
    )
}

@Composable
private fun RemoteProfileAvatar(profile: UserProfile) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)))
            .border(2.dp, Color.White.copy(alpha = 0.74f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!profile.avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = profile.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.matchParentSize().clip(CircleShape)
            )
        } else {
            Text(
                profile.displayName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 25.sp
            )
        }
    }
}

@Composable
private fun HarmonyPrimaryAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = HarmonyPink,
            contentColor = Color.White,
            disabledContainerColor = Color.White.copy(alpha = 0.12f),
            disabledContentColor = Color.White.copy(alpha = 0.4f)
        )
    ) {
        Text(text, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
    }
}

@Composable
private fun HarmonySecondaryAction(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.White.copy(alpha = 0.08f),
            contentColor = HarmonyText,
            disabledContainerColor = Color.White.copy(alpha = 0.04f),
            disabledContentColor = Color.White.copy(alpha = 0.35f)
        )
    ) {
        Text(text, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}
