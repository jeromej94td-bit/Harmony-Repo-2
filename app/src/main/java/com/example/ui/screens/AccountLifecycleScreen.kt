package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyText

enum class AccountLifecycleMode {
    RESET_HARMONY,
    DELETE_ACCOUNT
}

@Composable
fun AccountLifecycleScreen(
    mode: AccountLifecycleMode,
    actionInProgress: Boolean,
    errorMessage: String?,
    onBack: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var consent by remember(mode) { mutableStateOf(false) }
    val isReset = mode == AccountLifecycleMode.RESET_HARMONY

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF13051C), Color(0xFF09020F), Color(0xFF050109))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 26.dp, vertical = 28.dp)
        ) {
            IconButton(
                onClick = onBack,
                enabled = !actionInProgress,
                modifier = Modifier
                    .size(54.dp)
                    .background(Color.Black.copy(alpha = 0.62f), CircleShape)
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Zurück",
                    tint = Color.White
                )
            }

            Spacer(Modifier.height(42.dp))
            Text(
                text = if (isReset) "Harmony zurücksetzen" else "Wir sind traurig, dass du gehst",
                color = HarmonyText,
                fontSize = 31.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(Modifier.height(28.dp))

            if (isReset) {
                LifecycleBullet("Dein Login bleibt bestehen. Du kannst dich danach weiterhin ganz normal bei Harmony anmelden.")
                LifecycleBullet("Die Verbindung zu deinem Partner wird getrennt. Ihr seid danach beide wieder frei für eine neue Verbindung.")
                LifecycleBullet("Dein Harmony-Fortschritt und der gemeinsame Beziehungsverlauf dieser Verbindung werden dauerhaft gelöscht.")
                LifecycleBullet("Das Zurücksetzen ist nicht rückgängig zu machen.")
            } else {
                LifecycleBullet("Dein Harmony-Konto und deine persönlichen Kontodaten werden dauerhaft gelöscht.")
                LifecycleBullet("Wenn du mit einem Partner verbunden bist, werdet ihr beim Löschen automatisch entkoppelt.")
                LifecycleBullet("Dein persönlicher Harmony-Fortschritt wird entfernt. Dein ehemaliger Partner kann sich später neu verbinden.")
                LifecycleBullet("Die Löschung deines Kontos ist unwiderruflich und kann nicht wiederhergestellt werden.")
            }

            Spacer(Modifier.weight(1f))

            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage,
                    color = Color(0xFFFF8CA8),
                    fontSize = 13.sp,
                    modifier = Modifier.padding(bottom = 14.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isReset) {
                        "Ich stimme zu, Harmony zurückzusetzen"
                    } else {
                        "Ich stimme zu, mein Konto zu löschen"
                    },
                    color = HarmonyPink,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f).padding(end = 14.dp)
                )
                Switch(
                    checked = consent,
                    onCheckedChange = { consent = it },
                    enabled = !actionInProgress,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = HarmonyPink,
                        uncheckedThumbColor = Color.White.copy(alpha = 0.72f),
                        uncheckedTrackColor = Color.White.copy(alpha = 0.18f)
                    )
                )
            }

            Spacer(Modifier.height(18.dp))
            Button(
                onClick = onConfirm,
                enabled = consent && !actionInProgress,
                modifier = Modifier.fillMaxWidth().height(62.dp),
                shape = RoundedCornerShape(22.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isReset) HarmonyPurple else Color(0xFFD63A64),
                    contentColor = Color.White,
                    disabledContainerColor = Color.White.copy(alpha = 0.16f),
                    disabledContentColor = Color.White.copy(alpha = 0.42f)
                )
            ) {
                Text(
                    text = if (isReset) "Harmony zurücksetzen" else "Konto löschen",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Spacer(Modifier.height(22.dp))
        }
    }
}

@Composable
private fun LifecycleBullet(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(34.dp)
                .background(HarmonyPink, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("✓", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }
        Text(
            text = text,
            color = HarmonyText,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            modifier = Modifier.padding(start = 14.dp).weight(1f)
        )
    }
}
