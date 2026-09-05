package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface

@Composable
internal fun TypeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) HarmonyPurple else HarmonySurface,
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (selected) null else BorderStroke(1.dp, HarmonyLine),
    ) {
        Text(label, color = if (selected) Color.White else HarmonyMuted, fontSize = 12.sp)
    }
}
