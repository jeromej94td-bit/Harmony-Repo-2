package com.example.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ProfileEntity
import com.example.ui.util.triggerMiniVibration
import kotlinx.coroutines.launch
internal val IslandBookGold = Color(0xFFFFC96B)
internal val IslandBookPaper = Color(0xFFFFF3D9)
internal val IslandBookPaperDeep = Color(0xFFF4DDB8)
private val IslandBookInk = Color(0xFF251334)
private val IslandBookNavy = Color(0xFF07152D)
private val IslandBookPurple = Color(0xFF40145B)
private val IslandBookPink = Color(0xFFFF4F9D)
private val IslandBookTeal = Color(0xFF64E4E7)

@Composable
internal fun IslandHoppingBookBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    questionIndex: Int,
    totalQuestions: Int,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val pageTurn = remember(questionIndex) { Animatable(0f) }
    var turningAnswer by remember(questionIndex) { mutableStateOf<String?>(null) }

    fun beginPageTurn(answer: String) {
        if (turningAnswer != null) return
        turningAnswer = answer
        triggerMiniVibration(context, 34L)
        scope.launch {
            pageTurn.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 760, easing = FastOutSlowInEasing)
            )
            onPick(answer)
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        IslandBookPurple.copy(alpha = 0.78f),
                        IslandBookNavy,
                        Color(0xFF030610)
                    ),
                    radius = 1_450f
                )
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .testTag("island_hopping_book_board")
    ) {
        IslandHoppingMagicAtmosphere()

        IslandHoppingPageTurn(
            progress = pageTurn.value,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "HARMONY  •  INSELHOPPING",
                    color = IslandBookPurple,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.4.sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "Frage ${questionIndex + 1} von ${totalQuestions.coerceAtLeast(1)}",
                    color = IslandBookPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("island_book_progress")
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    text = prompt,
                    color = IslandBookInk,
                    fontSize = if (prompt.length > 58) 23.sp else 27.sp,
                    lineHeight = if (prompt.length > 58) 28.sp else 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp)
                )
                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items.take(2).forEachIndexed { index, item ->
                            IslandHoppingAnswerCard(
                                label = item.label,
                                accent = islandOptionAccent(index),
                                selected = turningAnswer == item.raw || selectedAnswer == item.raw,
                                onClick = { beginPageTurn(item.raw) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .testTag("island_book_option_$index")
                            )
                        }
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items.drop(2).take(2).forEachIndexed { offset, item ->
                            val index = offset + 2
                            IslandHoppingAnswerCard(
                                label = item.label,
                                accent = islandOptionAccent(index),
                                selected = turningAnswer == item.raw || selectedAnswer == item.raw,
                                onClick = { beginPageTurn(item.raw) },
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxSize()
                                    .testTag("island_book_option_$index")
                            )
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "✦  Jede Antwort öffnet die nächste Seite  ✦",
                    color = IslandBookPurple.copy(alpha = 0.70f),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun IslandHoppingAnswerCard(
    label: String,
    accent: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.linearGradient(
                    if (selected) {
                        listOf(accent.copy(alpha = 0.28f), Color.White.copy(alpha = 0.78f))
                    } else {
                        listOf(Color.White.copy(alpha = 0.60f), accent.copy(alpha = 0.10f))
                    }
                )
            )
            .border(
                width = if (selected) 2.2.dp else 1.4.dp,
                color = accent.copy(alpha = if (selected) 0.96f else 0.72f),
                shape = shape
            )
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = IslandBookInk,
            fontSize = 17.sp,
            lineHeight = 21.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

private fun islandOptionAccent(index: Int): Color = when (index) {
    0 -> IslandBookPink
    1 -> Color(0xFF7390F5)
    2 -> IslandBookTeal
    else -> IslandBookGold
}

@Composable
private fun IslandHoppingMagicAtmosphere() {
    Canvas(Modifier.fillMaxSize()) {
        val center = Offset(size.width * 0.52f, size.height * 0.42f)
        drawCircle(
            brush = Brush.radialGradient(
                listOf(
                    IslandBookTeal.copy(alpha = 0.16f),
                    IslandBookPink.copy(alpha = 0.10f),
                    Color.Transparent
                ),
                center = center,
                radius = size.minDimension * 0.62f
            ),
            radius = size.minDimension * 0.62f,
            center = center
        )
        repeat(18) { index ->
            val x = size.width * (((index * 37) % 97) / 97f)
            val y = size.height * (((index * 53) % 89) / 89f)
            val color = if (index % 3 == 0) IslandBookTeal else IslandBookGold
            drawCircle(color.copy(alpha = 0.46f), 1.2.dp.toPx(), Offset(x, y))
        }
        drawCircle(
            color = IslandBookPink.copy(alpha = 0.16f),
            radius = size.minDimension * 0.34f,
            center = center,
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2.dp.toPx())
        )
    }
}
