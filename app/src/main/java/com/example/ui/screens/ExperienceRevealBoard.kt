package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExperienceRevealResult
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyText

@Composable
internal fun ExperienceRevealBoard(
    reveal: ExperienceRevealResult,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    closeButtonTestTag: String = "experience_reveal_close",
    closeLabel: String = "Zurück zu Harmony"
) {
    Column(
        modifier = modifier
            .testTag("experience_reveal_board")
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("✨", fontSize = 58.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            reveal.title,
            color = Color.White,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            reveal.subtitle,
            color = HarmonyMuted,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        reveal.sections.forEach { section ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(HarmonyPurple.copy(alpha = 0.22f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                    .padding(16.dp)
            ) {
                Text(
                    section.title,
                    color = HarmonyPinkSoft,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(7.dp))
                section.values.forEach { value ->
                    Text("• $value", color = HarmonyText, fontSize = 15.sp, lineHeight = 21.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            reveal.closing,
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onClose,
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .testTag(closeButtonTestTag),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
        ) {
            Text(closeLabel, fontWeight = FontWeight.ExtraBold)
        }
    }
}
