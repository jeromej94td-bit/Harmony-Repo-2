package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2

/**
 * Minimal fullscreen shell for obvious one-tap mechanics.
 * Only the actual question and choices are shown so the interaction stays on one screen.
 */
@Composable
internal fun QuestionOnlyMechanicShell(
    question: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val compact = screenHeight < 700
    val chromeMetrics = FullscreenMechanicChromeLayoutPolicy.metrics(
        screenHeightDp = screenHeight,
        fontScale = configuration.fontScale
    )
    val stageHeight = FullscreenMechanicStageHeightPolicy.stageHeightDp(screenHeight).dp
    val shape = RoundedCornerShape(if (compact) 26.dp else 30.dp)
    val horizontalPadding = if (compact) 14.dp else 18.dp
    val questionSizeSp = FullscreenMechanicChromeLayoutPolicy.questionSizeSp(
        screenHeightDp = screenHeight,
        questionLength = question.length,
        fontScale = configuration.fontScale
    )
    val questionLineHeight = FullscreenMechanicChromeLayoutPolicy
        .questionLineHeightSp(screenHeight, questionSizeSp)
        .sp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(stageHeight)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.46f),
                        HarmonySurface2.copy(alpha = 0.98f),
                        HarmonyBg.copy(alpha = 0.99f)
                    )
                )
            )
            .border(1.2.dp, HarmonyPink.copy(alpha = 0.54f), shape)
            .padding(
                horizontal = horizontalPadding,
                vertical = chromeMetrics.verticalPaddingDp.dp
            )
            .testTag("fullscreen_mechanic_stage"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = question,
            color = Color.White,
            fontSize = questionSizeSp.sp,
            lineHeight = questionLineHeight,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(chromeMetrics.contentGapDp.dp))
        Box(modifier = Modifier.fillMaxSize()) { content() }
    }
}
