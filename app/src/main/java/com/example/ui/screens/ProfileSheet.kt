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
import coil.compose.AsyncImage
import com.example.data.model.ProfileEntity
import com.example.data.session.AppSession
import com.example.ui.AppLanguage
import com.example.ui.components.HarmonyCard
import com.example.ui.components.formatTimestamp
import com.example.ui.tr
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSheet(
    profile: ProfileEntity,
    session: AppSession,
    isEditProfileOpen: Boolean,
    onDismiss: () -> Unit,
    onOpenEditProfile: () -> Unit,
    onCloseEditProfile: () -> Unit,
    onSaveEditProfile: (String, String, Long) -> Unit,
    onUpdateAvatar: (Uri, Boolean) -> Unit,
    onOpenDevStudio: (() -> Unit)? = null,
    isDarkMode: Boolean = true,
    onToggleDarkMode: ((Boolean) -> Unit)? = null,
    language: AppLanguage = AppLanguage.GERMAN,
    onLanguageChange: (AppLanguage) -> Unit = {},
    isDemoMode: Boolean = false,
    onOpenPartnerConnection: () -> Unit = {},
    onOpenHarmonyReset: () -> Unit = {},
    onOpenDeleteAccount: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()
    val isPaired = session.isPaired
    val partner = session.partner
    val accountEmail = session.email?.takeIf { it.isNotBlank() }
    val ownName = if (isDemoMode) profile.userName else session.profile.displayName
    val partnerName = if (isDemoMode) profile.partnerName else partner?.displayName.orEmpty()

    val userAvatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { onUpdateAvatar(it, true) }
    }
    val partnerAvatarPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (isDemoMode) uri?.let { onUpdateAvatar(it, false) }
    }

    val userAvatarModel: Any? = profile.userAvatarPath?.let(::File)
        ?: if (!isDemoMode) session.profile.avatarUrl else null
    val partnerAvatarModel: Any? = if (isDemoMode) {
        profile.partnerAvatarPath?.let(::File)
    } else {
        partner?.avatarUrl
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
                if (isDemoMode || isPaired) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ProfileAvatar(
                            model = userAvatarModel,
                            fallback = ownName.take(1),
                            label = tr("Dein Bild", "Your photo"),
                            editable = true,
                            onClick = { userAvatarPicker.launch("image/*") }
                        )
                        Text(text = "💕", fontSize = 24.sp)
                        ProfileAvatar(
                            model = partnerAvatarModel,
                            fallback = partnerName.take(1),
                            label = tr("Partnerbild", "Partner photo"),
                            editable = isDemoMode,
                            onClick = { if (isDemoMode) partnerAvatarPicker.launch("image/*") }
                        )
                    }
                } else {
                    ProfileAvatar(
                        model = userAvatarModel,
                        fallback = ownName.take(1),
                        label = tr("Dein Bild", "Your photo"),
                        editable = true,
                        onClick = { userAvatarPicker.launch("image/*") }
                    )
                }

                Spacer(modifier = Modifier.height(9.dp))
                Text(
                    text = when {
                        isDemoMode -> "$ownName & $partnerName"
                        isPaired -> "$ownName & $partnerName"
                        else -> session.profile.displayName
                    },
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Bold,
                    color = HarmonyText,
                    modifier = Modifier.testTag("real_profile_title")
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = when {
                        isDemoMode -> "Demo-Modus · keine Cloud-Kontodaten werden angelegt"
                        isPaired -> "Verbunden mit ${partner?.displayName.orEmpty()}"
                        else -> "Noch nicht verbunden · verbinde einen echten Harmony-Account"
                    },
                    fontSize = 12.sp,
                    color = HarmonyMuted,
                    modifier = Modifier.testTag("pairing_status")
                )

                if (!isDemoMode && accountEmail != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.05f))
                            .border(1.dp, HarmonyLine, RoundedCornerShape(16.dp))
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.AccountCircle,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tr("Angemeldet als", "Signed in as"),
                                fontSize = 10.5.sp,
                                color = HarmonyMuted
                            )
                            Text(
                                text = accountEmail,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = HarmonyText,
                                modifier = Modifier.testTag("account_email")
                            )
                        }
                    }
                }
            }

            if (!isDemoMode) {
                Spacer(modifier = Modifier.height(16.dp))
                HarmonyCard {
                    Column {
                        Text(
                            text = if (isPaired) "💕 Eure Verbindung" else "💕 Partner verbinden",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = HarmonyText
                        )
                        Spacer(modifier = Modifier.height(5.dp))
                        Text(
                            text = if (isPaired) {
                                "Ihr seid mit zwei echten Supabase-Konten verbunden. Öffne hier eure Verbindung oder trenne sie."
                            } else {
                                "Erstelle einen echten 6-stelligen Harmony-Code oder gib den Code deines Partners ein."
                            },
                            fontSize = 12.5.sp,
                            color = HarmonyMuted
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onOpenPartnerConnection,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("partner_connection_button"),
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
                        ) {
                            Text(
                                text = if (isPaired) "Verbindung ansehen" else "Code erstellen oder eingeben",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            HarmonyCard {
                Column {
                    Text(text = tr("Profil", "Profile"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HarmonyText)
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfileRow(label = tr("Dein Name", "Your name"), value = ownName)
                    if (isDemoMode || isPaired) {
                        ProfileRow(label = tr("Partnerin", "Partner"), value = partnerName)
                        if (profile.startDate > 0L) {
                            ProfileRow(
                                label = tr("Zusammen seit", "Together since"),
                                value = formatTimestamp(profile.startDate)
                            )
                        }
                    }

                    if (onToggleDarkMode != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = tr("Dunkles Design", "Dark mode"), fontSize = 13.5.sp, color = HarmonyText)
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

                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = onOpenEditProfile,
                        modifier = Modifier.fillMaxWidth().height(52.dp).testTag("edit_profile_button"),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.06f))
                    ) {
                        Text(text = tr("Bearbeiten", "Edit"), color = HarmonyText, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            LanguageSelectorCard(language = language, onLanguageChange = onLanguageChange)
            Spacer(modifier = Modifier.height(12.dp))

            if (onOpenDevStudio != null) {
                HarmonyCard {
                    Column {
                        Text(
                            text = tr("🛠️ Entwickler-Modus", "🛠️ Developer mode"),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = HarmonyText
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = tr(
                                "Spiele & Städte bearbeiten, Ordner reinladen, Bilder anpassen",
                                "Edit games and destinations, import folders, adjust images"
                            ),
                            fontSize = 11.5.sp,
                            color = HarmonyMuted
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = {
                                onDismiss()
                                onOpenDevStudio()
                            },
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("open_dev_studio_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurple)
                        ) {
                            Text(
                                text = tr("Entwickler Studio Öffnen", "Open Developer Studio"),
                                color = Color.White,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            HarmonyCard {
                Column {
                    Text(text = tr("Konto", "Account"), fontSize = 15.sp, fontWeight = FontWeight.Bold, color = HarmonyText)

                    if (!isDemoMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = onOpenHarmonyReset,
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("reset_harmony_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f))
                        ) {
                            Text("Harmony zurücksetzen", color = HarmonyText, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
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
                            text = if (isDemoMode) "Demo beenden" else tr("Abmelden", "Log out"),
                            color = Color.White,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (!isDemoMode) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onOpenDeleteAccount,
                            modifier = Modifier.fillMaxWidth().height(52.dp).testTag("delete_account_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red.copy(alpha = 0.8f))
                        ) {
                            Text(text = tr("Konto löschen", "Delete account"), color = Color.White, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth().testTag("close_profile_sheet_button")) {
                Text(text = tr("Schließen", "Close"), color = HarmonyPink, fontSize = 13.5.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (isEditProfileOpen) {
        var userEdit by remember(session.userId, isDemoMode) {
            mutableStateOf(if (isDemoMode) profile.userName else session.profile.displayName)
        }
        var partnerEdit by remember { mutableStateOf(profile.partnerName) }
        var startEdit by remember { mutableStateOf(formatTimestamp(profile.startDate)) }

        Dialog(onDismissRequest = onCloseEditProfile) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(24.dp))
                    .background(Brush.verticalGradient(listOf(HarmonySurface2, HarmonySurface)))
                    .border(1.dp, HarmonyLine, RoundedCornerShape(24.dp))
                    .padding(22.dp)
            ) {
                Column {
                    Text(text = tr("Profil bearbeiten", "Edit profile"), fontSize = 17.sp, fontWeight = FontWeight.Bold, color = HarmonyText)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isDemoMode) {
                            tr("Namen und Startdatum eurer Beziehung.", "Your names and relationship start date.")
                        } else {
                            tr("Dein Name wird in deinem echten Harmony-Profil gespeichert.", "Your name is stored in your real Harmony profile.")
                        },
                        fontSize = 13.sp,
                        color = HarmonyMuted
                    )
                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = userEdit,
                        onValueChange = { userEdit = it },
                        placeholder = { Text(tr("Dein Name", "Your name"), color = HarmonyMuted) },
                        modifier = Modifier.fillMaxWidth().testTag("edit_user_name_input"),
                        colors = profileTextFieldColors()
                    )

                    if (isDemoMode) {
                        Spacer(modifier = Modifier.height(9.dp))
                        OutlinedTextField(
                            value = partnerEdit,
                            onValueChange = { partnerEdit = it },
                            placeholder = { Text(tr("Name Partnerin", "Partner's name"), color = HarmonyMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("edit_partner_name_input"),
                            colors = profileTextFieldColors()
                        )
                    }

                    if (isDemoMode || isPaired) {
                        Spacer(modifier = Modifier.height(9.dp))
                        OutlinedTextField(
                            value = startEdit,
                            onValueChange = { startEdit = it },
                            placeholder = { Text(tr("Zusammen seit (TT.MM.JJJJ)", "Together since (DD.MM.YYYY)"), color = HarmonyMuted) },
                            modifier = Modifier.fillMaxWidth().testTag("edit_start_date_input"),
                            colors = profileTextFieldColors()
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Button(
                            onClick = onCloseEditProfile,
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White.copy(alpha = 0.08f))
                        ) {
                            Text(text = tr("Abbrechen", "Cancel"), color = HarmonyText)
                        }
                        Button(
                            onClick = {
                                val parsedDate = try {
                                    SimpleDateFormat("dd.MM.yyyy", Locale.GERMAN).parse(startEdit)?.time ?: profile.startDate
                                } catch (_: Exception) {
                                    profile.startDate
                                }
                                val savedPartnerName = if (isDemoMode) partnerEdit else partner?.displayName.orEmpty()
                                onSaveEditProfile(userEdit, savedPartnerName, parsedDate)
                            },
                            modifier = Modifier.weight(1f),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
                        ) {
                            Text(text = tr("Speichern", "Save"), color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = HarmonyPink,
    unfocusedBorderColor = HarmonyLine,
    focusedTextColor = HarmonyText,
    unfocusedTextColor = HarmonyText
)

@Composable
fun ProfileRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 13.5.sp, color = HarmonyText)
        Text(text = value, fontSize = 13.sp, color = HarmonyMuted)
    }
}

@Composable
private fun ProfileAvatar(
    model: Any?,
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
                .then(if (editable) Modifier.clickable(onClick = onClick) else Modifier),
            contentAlignment = Alignment.Center
        ) {
            if (model != null) {
                AsyncImage(
                    model = model,
                    contentDescription = label,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().clip(CircleShape)
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
