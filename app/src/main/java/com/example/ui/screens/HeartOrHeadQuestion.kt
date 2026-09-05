package com.example.ui.screens

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.tr
import kotlinx.coroutines.delay

private const val HEART_HEAD_QUESTION_FADE_MILLIS = 160

private data class AtlasCrop(
    @DrawableRes val atlasRes: Int,
    val x: Int,
    val y: Int,
    val width: Int = 256,
    val height: Int = 320
)

private sealed interface HeartHeadVisual {
    data class Atlas(val crop: AtlasCrop) : HeartHeadVisual
    data class Drawable(@DrawableRes val res: Int) : HeartHeadVisual
}

private data class HeartHeadRoundVisuals(
    val visuals: List<HeartHeadVisual>,
    val subtitleDe: String,
    val subtitleEn: String
)

private fun a1(x: Int, y: Int, width: Int = 256, height: Int = 320) =
    HeartHeadVisual.Atlas(AtlasCrop(R.drawable.heart_head_panda_atlas_01, x, y, width, height))

private fun a2(x: Int, y: Int, width: Int = 256, height: Int = 320) =
    HeartHeadVisual.Atlas(AtlasCrop(R.drawable.heart_head_panda_atlas_02, x, y, width, height))

private fun heartOrHeadVisuals(kind: HarmonyImageChoiceKind): HeartHeadRoundVisuals = when (kind) {
    HarmonyImageChoiceKind.HEART_HEAD_DATE -> HeartHeadRoundVisuals(
        visuals = listOf(
            a1(128, 615),
            a1(516, 185),
            a2(128, 615),
            a1(128, 185)
        ),
        subtitleDe = "Welcher Moment zieht dich spontan an?",
        subtitleEn = "Which moment pulls you in first?"
    )
    HarmonyImageChoiceKind.HEART_HEAD_GIFT -> HeartHeadRoundVisuals(
        visuals = listOf(
            a2(174, 185),
            a2(516, 185),
            a2(548, 205, 224, 280),
            a2(128, 615)
        ),
        subtitleDe = "Welche Geste fühlt sich für dich besonders an?",
        subtitleEn = "Which gesture feels most meaningful to you?"
    )
    HarmonyImageChoiceKind.HEART_HEAD_CONFLICT -> HeartHeadRoundVisuals(
        visuals = listOf(
            a2(516, 615),
            a1(128, 185),
            a1(128, 615),
            a1(516, 615)
        ),
        subtitleDe = "Wie findet ihr nach Spannung wieder zueinander?",
        subtitleEn = "How do you find your way back to each other?"
    )
    HarmonyImageChoiceKind.HEART_HEAD_FUTURE -> HeartHeadRoundVisuals(
        visuals = listOf(
            a1(516, 185),
            a1(516, 615),
            a2(128, 615),
            a2(516, 615)
        ),
        subtitleDe = "Was trägt euch gemeinsam nach vorn?",
        subtitleEn = "What carries you forward together?"
    )
    HarmonyImageChoiceKind.HEART_HEAD_LOVE -> HeartHeadRoundVisuals(
        visuals = listOf(
            a1(540, 205, 224, 280),
            a2(128, 185),
            a1(128, 185),
            a1(516, 615)
        ),
        subtitleDe = "Woran spürst du Liebe im Alltag?",
        subtitleEn = "How do you feel love in everyday life?"
    )
    HarmonyImageChoiceKind.HEART_HEAD_FINAL -> HeartHeadRoundVisuals(
        visuals = listOf(
            HeartHeadVisual.Drawable(R.drawable.heart_head_final_heart),
            HeartHeadVisual.Drawable(R.drawable.heart_head_final_head),
            HeartHeadVisual.Drawable(R.drawable.heart_head_final_gut),
            HeartHeadVisual.Drawable(R.drawable.heart_head_final_balance)
        ),
        subtitleDe = "Herz, Kopf, Bauchgefühl – oder genau die Mischung?",
        subtitleEn = "Heart, head, instinct – or exactly the balance?"
    )
    else -> error("Unsupported Herz oder Kopf kind: $kind")
}

@Composable
private fun heartOrHeadQuestionText(question: String): String = when (question) {
    "Welcher Abend fühlt sich am meisten nach dir an?" -> tr(question, "Which evening feels most like you?")
    "Was bedeutet dir bei einem Geschenk am meisten?" -> tr(question, "What matters most to you in a gift?")
    "Wie gehst du eher mit Spannung um?" -> tr(question, "How do you usually handle tension?")
    "Was gibt dir in eurer Zukunft am meisten Sicherheit?" -> tr(question, "What gives you the most security in your future together?")
    "Was zeigt Liebe für dich im Alltag am stärksten?" -> tr(question, "What shows love most strongly to you in everyday life?")
    "Wenn du lieben müsstest – worauf vertraust du zuerst?" -> tr(question, "When you love, what do you trust first?")
    else -> tr(question, question)
}

@Composable
private fun heartOrHeadOptionText(option: String): String = when (option) {
    "Spontaner Nachtspaziergang" -> tr(option, "Spontaneous night walk")
    "Geplantes Dinner" -> tr(option, "Planned dinner")
    "Picknick bei Sonnenuntergang" -> tr(option, "Sunset picnic")
    "Gemütlicher Filmabend" -> tr(option, "Cozy movie night")
    "Handgeschriebener Brief" -> tr(option, "Handwritten letter")
    "Praktisches Geschenk" -> tr(option, "Practical gift")
    "Kleine Überraschung" -> tr(option, "Small surprise")
    "Gemeinsames Erlebnis" -> tr(option, "Shared experience")
    "Sofort reden" -> tr(option, "Talk right away")
    "Erst Ruhe, dann klären" -> tr(option, "Pause first, then talk")
    "Nähe suchen" -> tr(option, "Seek closeness")
    "Gedanken sortieren" -> tr(option, "Sort out my thoughts")
    "Ein starkes Gefühl füreinander" -> tr(option, "A strong feeling for each other")
    "Ein konkreter gemeinsamer Plan" -> tr(option, "A concrete shared plan")
    "Gemeinsame Erinnerungen" -> tr(option, "Shared memories")
    "Klare Absprachen & Stabilität" -> tr(option, "Clear agreements & stability")
    "Ein tiefer Blick" -> tr(option, "A deep look")
    "Verlässliche Unterstützung" -> tr(option, "Reliable support")
    "Spontane Zärtlichkeit" -> tr(option, "Spontaneous affection")
    "Mitdenken & Organisieren" -> tr(option, "Thinking ahead & organizing")
    "Herz" -> tr(option, "Heart")
    "Kopf" -> tr(option, "Head")
    "Bauchgefühl" -> tr(option, "Instinct")
    "Balance" -> tr(option, "Balance")
    else -> tr(option, option)
}

@Composable
internal fun HeartOrHeadQuestion(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    kind: HarmonyImageChoiceKind,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val round = heartOrHeadVisuals(kind)
    val answerOptions = options.take(4)
    require(answerOptions.size == 4) { "Herz oder Kopf requires exactly four options" }

    var exiting by remember(question, kind) { mutableStateOf(false) }
    var pendingSelection by remember(question, kind) { mutableStateOf<String?>(null) }
    val questionAlpha = remember(question, kind) { Animatable(0f) }
    val haptics = LocalHapticFeedback.current

    LaunchedEffect(question, kind) {
        questionAlpha.snapTo(0f)
        exiting = false
        pendingSelection = null
        delay(heartOrHeadCardsTransitionDurationMillis(answerOptions.size))
        questionAlpha.animateTo(
            1f,
            animationSpec = tween(HEART_HEAD_QUESTION_FADE_MILLIS, easing = FastOutSlowInEasing)
        )
    }

    LaunchedEffect(exiting, pendingSelection) {
        val answer = pendingSelection ?: return@LaunchedEffect
        if (!exiting) return@LaunchedEffect
        delay(heartOrHeadCardsTransitionDurationMillis(answerOptions.size))
        questionAlpha.animateTo(
            0f,
            animationSpec = tween(HEART_HEAD_QUESTION_FADE_MILLIS, easing = FastOutSlowInEasing)
        )
        onPick(answer)
    }

    val containerShape = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(containerShape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2A1038),
                        Color(0xFF1A0B29),
                        Color(0xFF100817)
                    )
                )
            )
            .border(
                1.2.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0xFFFF4AAA).copy(alpha = 0.72f),
                        Color(0xFFB567FF).copy(alpha = 0.64f),
                        Color(0xFFFF75C8).copy(alpha = 0.58f)
                    )
                ),
                containerShape
            )
            .padding(horizontal = 10.dp, vertical = 16.dp)
            .testTag("heart_or_head_question"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = tr(round.subtitleDe, round.subtitleEn),
            color = Color(0xFFFFB9DE),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .graphicsLayer { alpha = questionAlpha.value }
                .padding(horizontal = 10.dp)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = heartOrHeadQuestionText(question),
            color = Color(0xFFFFF7FC),
            fontSize = 24.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .graphicsLayer { alpha = questionAlpha.value }
                .padding(horizontal = 8.dp)
                .testTag("heart_or_head_prompt")
        )
        Spacer(modifier = Modifier.height(14.dp))

        answerOptions.chunked(2).forEachIndexed { rowIndex, rowOptions ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowOptions.forEachIndexed { columnIndex, option ->
                    val index = rowIndex * 2 + columnIndex
                    HeartOrHeadCard(
                        animationKey = "heart_head_${kind.name}_${question}_$index",
                        index = index,
                        option = option,
                        visual = round.visuals[index],
                        selected = selectedAnswer == option || pendingSelection == option,
                        exiting = exiting,
                        inputEnabled = questionAlpha.value > 0.98f && !exiting,
                        onClick = {
                            if (!exiting) {
                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                pendingSelection = option
                                exiting = true
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            if (rowIndex == 0) Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun HeartOrHeadCard(
    animationKey: Any,
    index: Int,
    option: String,
    visual: HeartHeadVisual,
    selected: Boolean,
    exiting: Boolean,
    inputEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val reveal = remember(animationKey) { Animatable(0f) }
    val exit = remember(animationKey) { Animatable(0f) }
    val density = LocalDensity.current.density
    val shape = RoundedCornerShape(22.dp)
    val accessibilityState = if (selected) {
        tr("Ausgewählt", "Selected")
    } else {
        tr("Nicht ausgewählt", "Not selected")
    }

    LaunchedEffect(animationKey) {
        reveal.snapTo(0f)
        exit.snapTo(0f)
        delay(heartOrHeadCardDelayMillis(index))
        reveal.animateTo(
            1f,
            animationSpec = tween(
                durationMillis = heartOrHeadCardFlipDurationMillis().toInt(),
                easing = FastOutSlowInEasing
            )
        )
    }

    LaunchedEffect(exiting, animationKey) {
        if (!exiting) {
            exit.snapTo(0f)
            return@LaunchedEffect
        }
        delay(heartOrHeadCardDelayMillis(index))
        exit.animateTo(
            1f,
            animationSpec = tween(
                durationMillis = heartOrHeadCardFlipDurationMillis().toInt(),
                easing = FastOutSlowInEasing
            )
        )
    }

    val enterProgress = reveal.value.coerceIn(0f, 1f)
    val exitProgress = exit.value.coerceIn(0f, 1f)
    val alpha = (enterProgress * (1f - exitProgress * 0.72f)).coerceIn(0f, 1f)
    val selectedScale = if (selected && !exiting) 1.025f else 1f

    Column(
        modifier = modifier
            .graphicsLayer {
                this.alpha = alpha
                rotationY = -88f * (1f - enterProgress) + 92f * exitProgress
                translationY = 18f * (1f - enterProgress) - 8f * exitProgress
                scaleX = (0.92f + enterProgress * 0.08f) * selectedScale
                scaleY = (0.94f + enterProgress * 0.06f) * selectedScale
                transformOrigin = TransformOrigin.Center
                cameraDistance = 30f * density
                shadowElevation = if (selected) 18f else 6f
            }
            .clip(shape)
            .background(Color(0xFF170C22))
            .border(
                width = if (selected) 2.4.dp else 1.dp,
                brush = Brush.linearGradient(
                    if (selected) {
                        listOf(Color.White, Color(0xFFFF4AAA), Color(0xFFB567FF), Color(0xFFFF75C8))
                    } else {
                        listOf(
                            Color.White.copy(alpha = 0.20f),
                            Color(0xFFFF4AAA).copy(alpha = 0.50f),
                            Color(0xFFB567FF).copy(alpha = 0.42f)
                        )
                    }
                ),
                shape = shape
            )
            .clickable(enabled = inputEnabled && enterProgress > 0.98f, onClick = onClick)
            .semantics {
                stateDescription = accessibilityState
            }
            .testTag(if (selected) "heart_head_option_${index}_selected" else "heart_head_option_$index")
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.82f)
                .background(Color(0xFF24102F))
        ) {
            when (visual) {
                is HeartHeadVisual.Atlas -> HeartOrHeadAtlasImage(visual.crop)
                is HeartHeadVisual.Drawable -> Image(
                    painter = painterResource(visual.res),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(22.dp)
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xFF170C22).copy(alpha = 0.22f)
                            )
                        )
                    )
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFF3A163E),
                            Color(0xFF25102F),
                            Color(0xFF3B153D)
                        )
                    )
                )
                .padding(horizontal = 9.dp, vertical = 9.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = heartOrHeadOptionText(option),
                color = Color(0xFFFFF7FC),
                fontSize = 13.sp,
                lineHeight = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
        }
    }
}

@Composable
private fun HeartOrHeadAtlasImage(crop: AtlasCrop) {
    val bitmap = ImageBitmap.imageResource(crop.atlasRes)
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawIntoCanvas {
            drawImage(
                image = bitmap,
                srcOffset = IntOffset(crop.x, crop.y),
                srcSize = IntSize(crop.width, crop.height),
                dstOffset = IntOffset.Zero,
                dstSize = IntSize(size.width.toInt().coerceAtLeast(1), size.height.toInt().coerceAtLeast(1))
            )
        }
    }
}
