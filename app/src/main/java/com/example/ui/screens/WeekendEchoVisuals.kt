package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.WeekendEchoCandidate
import com.example.data.model.WeekendEchoDuel
import com.example.data.model.WeekendEchoMotif
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurpleLight
import android.provider.Settings

@DrawableRes
internal fun WeekendEchoMotif.drawableRes(): Int = when (this) {
    WeekendEchoMotif.COUCH_BLANKET -> R.drawable.weekend_echo_couch
    WeekendEchoMotif.CINEMA -> R.drawable.weekend_echo_cinema
    WeekendEchoMotif.CITY -> R.drawable.weekend_echo_city
    WeekendEchoMotif.COUNTRY -> R.drawable.weekend_echo_country
    WeekendEchoMotif.ROADTRIP -> R.drawable.weekend_echo_roadtrip
    WeekendEchoMotif.TRAIN -> R.drawable.weekend_echo_train
    WeekendEchoMotif.CAMPING -> R.drawable.weekend_echo_camping
    WeekendEchoMotif.HOTEL -> R.drawable.weekend_echo_hotel
}

internal enum class WeekendEchoSide(val tag: String) {
    LEFT("left"),
    RIGHT("right")
}

@Composable
internal fun WeekendSplitScene(
    duel: WeekendEchoDuel,
    state: WeekendEchoRoundState,
    onSealPick: (WeekendEchoCandidate) -> Unit,
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sealsEnabled = state.phase == WeekendEchoPhase.PERSON_A ||
        state.phase == WeekendEchoPhase.PERSON_B ||
        state.phase == WeekendEchoPhase.REVEALED
    val revealed = state.exposesAnswers

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(Color(0xFF120B2C))
            .testTag("weekend_echo_background")
    ) {
        Row(Modifier.fillMaxSize()) {
            SceneHalf(duel.left.motif, Modifier.weight(1f).fillMaxHeight())
            SceneHalf(duel.right.motif, Modifier.weight(1f).fillMaxHeight())
        }

        Canvas(Modifier.fillMaxSize()) {
            val seamWidth = size.width * 0.055f
            drawRect(
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Transparent,
                        Color(0xFF45D9FF).copy(alpha = 0.58f),
                        Color(0xFFE96BFF).copy(alpha = 0.82f),
                        Color(0xFFFF8B66).copy(alpha = 0.55f),
                        Color.Transparent
                    )
                ),
                topLeft = androidx.compose.ui.geometry.Offset(size.width / 2f - seamWidth / 2f, 0f),
                size = androidx.compose.ui.geometry.Size(seamWidth, size.height)
            )
        }

        WalkingPandaPair(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 4.dp, bottom = 76.dp)
                .size(width = 176.dp, height = 184.dp)
                .testTag("weekend_echo_pandas")
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(horizontal = 26.dp, vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            RotatingEchoSeal(
                side = WeekendEchoSide.LEFT,
                candidate = duel.left,
                selected = state.firstMotifKey == duel.left.motif.key || state.secondMotifKey == duel.left.motif.key,
                revealed = revealed,
                enabled = sealsEnabled,
                onClick = { if (revealed) onContinue() else onSealPick(duel.left) }
            )
            RotatingEchoSeal(
                side = WeekendEchoSide.RIGHT,
                candidate = duel.right,
                selected = state.firstMotifKey == duel.right.motif.key || state.secondMotifKey == duel.right.motif.key,
                revealed = revealed,
                enabled = sealsEnabled,
                onClick = { if (revealed) onContinue() else onSealPick(duel.right) }
            )
        }
    }
}

@Composable
private fun SceneHalf(motif: WeekendEchoMotif, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(30.dp))) {
        Image(
            painter = painterResource(motif.drawableRes()),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0x16090720), Color.Transparent, Color(0xB80A061C))
                    )
                )
        )
    }
}

@Composable
private fun WalkingPandaPair(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.weekend_echo_panda_pair),
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = modifier
    )
}

@Composable
private fun RotatingEchoSeal(
    side: WeekendEchoSide,
    candidate: WeekendEchoCandidate,
    selected: Boolean,
    revealed: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val animationsEnabled = runCatching {
        Settings.Global.getFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
    }.getOrDefault(true)
    val transition = rememberInfiniteTransition(label = "weekend_echo_seal")
    val rotation by transition.animateFloat(
        initialValue = 0f,
        targetValue = if (animationsEnabled) 360f else 0f,
        animationSpec = infiniteRepeatable(tween(6_000, easing = LinearEasing)),
        label = "weekend_echo_seal_rotation"
    )
    val accent = if (side == WeekendEchoSide.LEFT) HarmonyPurpleLight else HarmonyPink

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Color(0xD8261248))
                .border(1.dp, Color.White.copy(alpha = 0.30f), CircleShape)
                .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
                .testTag("weekend_echo_${side.tag}_seal"),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                Modifier
                    .fillMaxSize()
                    .padding(5.dp)
                    .graphicsLayer { rotationZ = rotation }
            ) {
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(accent, Color(0xFF55E8FF), HarmonyGold, accent)
                    ),
                    style = Stroke(width = if (selected && revealed) 8f else 5f)
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.24f),
                    radius = size.minDimension * 0.38f,
                    style = Stroke(width = 2f)
                )
            }
            Text(candidate.motif.symbol, fontSize = 34.sp)
        }
        AnimatedVisibility(visible = revealed) {
            Text(
                text = candidate.motif.displayText,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 7.dp)
            )
        }
    }
}
