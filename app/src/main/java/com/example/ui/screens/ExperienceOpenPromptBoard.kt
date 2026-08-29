package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExperienceOpenPromptRound
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft

/**
 * Reusable open-prompt surface with caller-owned answer state.
 *
 * It preserves the existing proposal copy, dimensions and test tag while removing proposal-specific
 * naming from the mechanic API.
 */
@Composable
internal fun ExperienceOpenPromptBoard(
    round: ExperienceOpenPromptRound,
    initialValue: String,
    onContinue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(round.id) { mutableStateOf(initialValue) }

    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "✍️  EURE EIGENEN WORTE",
            color = HarmonyPinkSoft,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(12.dp))
        Text(
            round.prompt,
            color = Color.White,
            fontSize = 23.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(22.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .testTag("proposal_open_answer"),
            placeholder = { Text("Schreib aus dem Gefühl heraus …", color = HarmonyMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = HarmonyPink,
                unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                cursorColor = HarmonyPink
            ),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(22.dp)
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = { onContinue(text.trim()) },
            enabled = text.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
        ) {
            Text("Weiter", fontWeight = FontWeight.ExtraBold)
        }
    }
}
