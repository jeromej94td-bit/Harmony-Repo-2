package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.BuildConfig
import com.example.data.SupabaseConfig
import com.example.data.model.ProfileEntity
import com.example.ui.AppLanguage
import com.example.ui.components.HarmonyCard
import com.example.ui.components.formatTimestamp
import com.example.ui.session.AppSessionViewModel
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.ui.tr
import io.github.jan.supabase.auth.auth
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Real-user/couple overload of the existing ProfileSheet.
 *
 * The original one-argument account implementation on main remains available
 * to older callers. MainActivity selects this overload because it supplies the
 * required couple-state parameters below.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheet(
    profile: ProfileEntity,
    isEditProfileOpen: Boolean,
    isPaired: Boolean,
    isDemoMode: Boolean,
    partnerDisplayName: String?,
    onDismiss: () -> Unit,
    onToggleSimulator: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onCloseEditProfile: () -> Unit,
    onSaveEditProfile: (String, String, Long) -> Unit,
    onUpdateAvatar: (Uri, Boolean) -> Unit,
    onOpenDevStudio: (() -> Unit)? = null,
    isDarkMode: Boolean = true,
    onToggleDarkMode: ((Boolean) -> Unit)? = null,
    language: AppLanguage = AppLanguage.GERMAN,
    onLanguageChange: (AppLanguage) -> Unit = {},
    onOpenPartnerConnection: () -> Unit,
    onOpenHarmonyReset: () -> Unit,
    onOpenDeleteAccount: () -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val sessionViewModel: AppSessionViewModel = viewModel()
    val accountEmail = if (isDemoMode) {
        null
    } else {
        runCatching { SupabaseConfig.client.auth.currentSessionOrNull()?.user?.email }
            .getOrNull()
            ?.takeIf { it.isNotBlank() }
    }

    val userAvatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onUpdateAvatar(it, true) }
    }
    val partnerAvatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (isDemoMode) uri?.let { onUpdateAvatar(it, false) }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = HarmonyBg,
        scrimColor = Color.Black.copy(alpha = 0.72f),
        shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CoupleProfileAvatar(
                        path = profile.userAvatarPath,
                        fallback = profile.userName.take(1),
                        label = tr("Dein Bild", "Your photo"),
                        editable = true,
                        onClick = { userAvatarPicker.launch("image/*") }
                    )
                    Text(text = if (isPaired) "💕" else "♡", fontSize = 24.sp)
                    CoupleProfileAvatar(
                        path = profile.partnerAvatarPath,
                        fallback = profile.partnerName.take(1),
                        label = tr("Partnerbild", "Partner photo"),
                        editable = isDemoMode,
                        onClick = { if (isDemoMode) partnerAvatarPicker.launch("image/*") }
                    )
                }
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (isPaired) {
                        "${profile.userName} & ${partnerDisplayName ?: profile.partnerName}"
                    } else {
                        profile.userName
                    },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarmonyText
                )
                Spacer(Modifier.height(3.dp))
                Text(
                    text = when {
                        isDemoMode -> tr(
                            "Demo-Modus · keine Cloud-Kontodaten",
                            "Demo mode · no cloud account data"
                        )
                        isPaired -> tr(
                            "Mit deinem Partner verbunden",
                            "Connected with your partner"
                        )
                        else -> tr(
                            "Solo-Modus · Partner später verbinden",
                            "Solo mode · connect a partner later"
                        )
                    },
                    color = HarmonyMuted,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(18.dp))

            HarmonyCard {
                Column {
                    Text(tr("Profil", "Profile"), color = HarmonyText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(8.dp))
                    ProfileRow(tr("Dein Name", "Your name"), profile.userName)
                    if (isPaired || isDemoMode) {
                        ProfileRow(
                            tr("Partner", "Partner"),
                            partnerDisplayName ?: profile.partnerName
                        )
                    }
                    ProfileRow(tr("Zusammen seit", "Together since"), formatTimestamp(profile.startDate))

                    if (BuildConfig.DEBUG) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tr("Partner-Simulator", "Partner simulator"), color = HarmonyText, fontSize = 13.5.sp)
                            Switch(
                                checked = profile.simulatorEnabled,
                                onCheckedChange = { onToggleSimulator() },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = HarmonyPink
                                ),
                                modifier = Modifier.testTag("simulator_toggle")
                            )
                        }
                    }

                    if (onToggleDarkMode != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tr("Dunkles Design", "Dark mode"), color = HarmonyText, fontSize = 13.5.sp)
                            Switch(
                                checked = isDarkMode,
                                onCheckedChange = onToggleDarkMode,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = HarmonyPink
                                ),
                                modifier = Modifier.testTag("dark_mode_toggle")
                            )
                        }
                    }

                    Spacer(Modifier.height(6.dp))
                    Button(
                        onClick = onOpenEditProfile,
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("edit_profile_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.06f))
                    ) {
                        Text(tr("Bearbeiten", "Edit"), color = HarmonyText, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(12.dp))
            LanguageSelectorCard(language = language, onLanguageChange = onLanguageChange)
            Spacer(Modifier.height(12.dp))

            if (onOpenDevStudio != null) {
                HarmonyCard {
                    Column {
                        Text(tr("🛠️ Entwickler-Modus", "🛠️ Developer mode"), color = HarmonyText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            tr(
                                "Spiele & Inhalte bearbeiten",
                                "Edit games & content"
                            ),
                            color = HarmonyMuted,
                            fontSize = 11.5.sp
                        )
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenDevStudio()
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("open_dev_studio_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurple)
                        ) {
                            Text(tr("Entwickler Studio öffnen", "Open Developer Studio"), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            HarmonyCard {
                Column {
                    Text(tr("Konto", "Account"), color = HarmonyText, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(10.dp))
                    AccountIdentityCard(accountEmail = accountEmail, isDemoMode = isDemoMode)
                    Spacer(Modifier.height(12.dp))

                    if (!isDemoMode) {
                        Button(
                            onClick = onOpenPartnerConnection,
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("partner_connection_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
                        ) {
                            Text(
                                if (isPaired) tr("Verbindung ansehen", "View connection") else tr("Partner verbinden", "Connect partner"),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = onOpenHarmonyReset,
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("reset_harmony_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f))
                        ) {
                            Text(tr("Harmony zurücksetzen", "Reset Harmony"), color = HarmonyText, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(10.dp))
                    }

                    Button(
                        onClick = {
                            onDismiss()
                            onLogout()
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("logout_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurple)
                    ) {
                        Text(
                            if (isDemoMode) tr("Demo beenden", "Exit demo") else tr("Abmelden", "Log out"),
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isDemoMode) {
                        Spacer(Modifier.height(10.dp))
                        Button(
                            onClick = onOpenDeleteAccount,
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("delete_account_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                        ) {
                            Text(tr("Konto löschen", "Delete account"), color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth().testTag("close_profile_sheet_button")
            ) {
                Text(tr("Schließen", "Close"), color = HarmonyPink, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(20.dp))
        }
    }

    if (isEditProfileOpen) {
        CoupleProfileEditDialog(
            profile = profile,
            isDemoMode = isDemoMode,
            onDismiss = onCloseEditProfile,
            onSave = { userName, partnerName, startDate ->
                if (isDemoMode) {
                    onSaveEditProfile(userName, partnerName, startDate)
                } else {
                    sessionViewModel.updateProfileDisplayName(userName)
                    onSaveEditProfile(userName, profile.partnerName, startDate)
                }
            }
        )
    }
}

@Composable
private fun AccountIdentityCard(accountEmail: String?, isDemoMode: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, HarmonyLine, RoundedCornerShape(16.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(25.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                if (isDemoMode) tr("Demo-Modus", "Demo mode") else tr("Angemeldet als", "Signed in as"),
                color = HarmonyMuted,
                fontSize = 11.5.sp
            )
            Spacer(Modifier.height(2.dp))
            Text(
                if (isDemoMode) tr("Kein Benutzerkonto angemeldet", "No user account signed in") else accountEmail ?: tr("Harmony-Konto", "Harmony account"),
                color = HarmonyText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag("account_email")
            )
        }
    }
}

@Composable
private fun CoupleProfileEditDialog(
    profile: ProfileEntity,
    isDemoMode: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Long) -> Unit
) {
    var userEdit by remember(profile.userName) { mutableStateOf(profile.userName) }
    var partnerEdit by remember(profile.partnerName) { mutableStateOf(profile.partnerName) }
    var startEdit by remember(profile.startDate) { mutableStateOf(formatTimestamp(profile.startDate)) }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .background(Brush.verticalGradient(listOf(HarmonySurface2, HarmonySurface)))
                .border(1.dp, HarmonyLine, RoundedCornerShape(24.dp))
                .padding(22.dp)
        ) {
            Column {
                Text(tr("Profil bearbeiten", "Edit profile"), color = HarmonyText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(5.dp))
                Text(
                    if (isDemoMode) {
                        tr("Namen und Startdatum eurer Beziehung.", "Your names and relationship start date.")
                    } else {
                        tr("Du bearbeitest nur dein eigenes Harmony-Profil.", "You only edit your own Harmony profile.")
                    },
                    color = HarmonyMuted,
                    fontSize = 12.5.sp
                )
                Spacer(Modifier.height(14.dp))

                CoupleProfileTextField(
                    value = userEdit,
                    onValueChange = { userEdit = it },
                    placeholder = tr("Dein Name", "Your name"),
                    tag = "edit_user_name_input"
                )

                if (isDemoMode) {
                    Spacer(Modifier.height(9.dp))
                    CoupleProfileTextField(
                        value = partnerEdit,
                        onValueChange = { partnerEdit = it },
                        placeholder = tr("Name Partner", "Partner name"),
                        tag = "edit_partner_name_input"
                    )
                }

                Spacer(Modifier.height(9.dp))
                CoupleProfileTextField(
                    value = startEdit,
                    onValueChange = { startEdit = it },
                    placeholder = tr("Zusammen seit (TT.MM.JJJJ)", "Together since (DD.MM.YYYY)"),
                    tag = "edit_start_date_input"
                )

                Spacer(Modifier.height(18.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f))
                    ) {
                        Text(tr("Abbrechen", "Cancel"), color = HarmonyText)
                    }
                    Button(
                        onClick = {
                            val parsedDate = runCatching {
                                SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).parse(startEdit)?.time
                            }.getOrNull() ?: profile.startDate
                            onSave(userEdit.trim(), partnerEdit.trim(), parsedDate)
                        },
                        enabled = userEdit.trim().isNotEmpty(),
                        modifier = Modifier.weight(1f),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
                    ) {
                        Text(tr("Speichern", "Save"), color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun CoupleProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    tag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = HarmonyMuted) },
        modifier = Modifier.fillMaxWidth().testTag(tag),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HarmonyPink,
            unfocusedBorderColor = HarmonyLine,
            focusedTextColor = HarmonyText,
            unfocusedTextColor = HarmonyText
        )
    )
}

@Composable
private fun CoupleProfileAvatar(
    path: String?,
    fallback: String,
    label: String,
    editable: Boolean,
    onClick: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(74.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)))
                .border(2.dp, Color.White.copy(alpha = 0.72f), CircleShape)
                .let { base -> if (editable) base.clickable(onClick = onClick) else base },
            contentAlignment = Alignment.Center
        ) {
            if (!path.isNullOrBlank()) {
                AsyncImage(
                    model = File(path),
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                Text(
                    fallback.ifBlank { "?" }.uppercase(),
                    color = Color.White,
                    fontSize = 27.sp,
                    fontWeight = FontWeight.Black
                )
            }
            if (editable) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(25.dp)
                        .background(HarmonyPink, CircleShape)
                        .border(1.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text("＋", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Black)
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(label, color = HarmonyMuted, fontSize = 10.sp)
    }
}
