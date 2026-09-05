package com.example.ui.christmas

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private enum class ChristmasPage { PARTS, ROUND, COMPLETE }

@Composable
fun ChristmasCategoryVisual(
    accent: Color,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "christmas_category_snowflake")
    val pulse by transition.animateFloat(.94f, 1.06f, infiniteRepeatable(tween(1700, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "snowflake_pulse")
    val glow by transition.animateFloat(.54f, 1f, infiniteRepeatable(tween(2100), RepeatMode.Reverse), label = "snowflake_glow")
    Canvas(modifier.scale(pulse)) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.minDimension * .33f
        drawCircle(Brush.radialGradient(listOf(accent.copy(.30f * glow), Color(0xFFB96CFF).copy(.16f), Color.Transparent), center, radius * 1.65f), radius * 1.65f, center)
        repeat(6) { arm ->
            val angle = arm * PI.toFloat() / 3f
            val end = Offset(center.x + cos(angle) * radius, center.y + sin(angle) * radius)
            drawLine(accent.copy(.20f * glow), center, end, 9f, StrokeCap.Round)
            drawLine(Color.White.copy(.92f), center, end, 2.1f, StrokeCap.Round)
            listOf(.52f, .76f).forEach { point ->
                val root = Offset(center.x + cos(angle) * radius * point, center.y + sin(angle) * radius * point)
                listOf(-.64f, .64f).forEach { branch ->
                    val branchAngle = angle + PI.toFloat() + branch
                    val branchEnd = Offset(root.x + cos(branchAngle) * radius * .22f, root.y + sin(branchAngle) * radius * .22f)
                    drawLine(Color.White.copy(.82f), root, branchEnd, 1.7f, StrokeCap.Round)
                }
            }
        }
        drawCircle(Color.White.copy(.95f), 3.2f, center)
    }
}

@Composable
fun ChristmasExperienceScreen(onExit: () -> Unit, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val audio = remember { ChristmasAudioController(context) }
    var page by remember { mutableStateOf(ChristmasPage.PARTS) }
    var selectedPart by remember { mutableStateOf<ChristmasPart?>(null) }
    var playMode by remember { mutableStateOf(ChristmasPlayMode.TOGETHER) }
    var showExitDialog by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        audio.enterChristmasGame()
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> audio.pause()
                Lifecycle.Event.ON_START -> audio.resume()
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            audio.leaveChristmasGame()
        }
    }

    fun handleBack() {
        when (page) {
            ChristmasPage.PARTS -> showExitDialog = true
            ChristmasPage.ROUND, ChristmasPage.COMPLETE -> {
                page = ChristmasPage.PARTS
                selectedPart = null
            }
        }
    }
    BackHandler { handleBack() }

    Surface(modifier.fillMaxSize(), color = ChristmasNight) {
        Box(Modifier.fillMaxSize()) {
            ChristmasAtmosphere(Modifier.fillMaxSize())
            AnimatedContent(
                targetState = page,
                transitionSpec = {
                    (fadeIn(tween(500)) + slideInVertically(tween(550)) { it / 10 }) togetherWith
                        (fadeOut(tween(300)) + slideOutVertically(tween(360)) { -it / 12 })
                },
                label = "christmas_page",
            ) { target ->
                when (target) {
                    ChristmasPage.PARTS -> ChristmasPartSelector(
                        playMode = playMode,
                        onPlayModeChange = { playMode = it },
                        onPartSelected = {
                            selectedPart = it
                            page = ChristmasPage.ROUND
                        },
                        onBack = { showExitDialog = true },
                    )
                    ChristmasPage.ROUND -> selectedPart?.let { part ->
                        ChristmasRoundPlayer(
                            part = part,
                            playMode = playMode,
                            audio = audio,
                            onBack = { handleBack() },
                            onComplete = { page = ChristmasPage.COMPLETE },
                        )
                    }
                    ChristmasPage.COMPLETE -> ChristmasPartComplete(
                        part = selectedPart,
                        onParts = { handleBack() },
                    )
                }
            }
        }
    }

    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            containerColor = Color(0xFF21102A),
            title = { Text("Weihnachtsspiel verlassen?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("Die Musik endet erst, wenn ihr das Weihnachtsspiel vollständig verlasst.", color = ChristmasMuted) },
            confirmButton = { TextButton(onClick = onExit) { Text("Verlassen", color = ChristmasPink) } },
            dismissButton = { TextButton(onClick = { showExitDialog = false }) { Text("Bleiben", color = Color.White) } },
        )
    }
}

@Composable
private fun ChristmasPartSelector(
    playMode: ChristmasPlayMode,
    onPlayModeChange: (ChristmasPlayMode) -> Unit,
    onPartSelected: (ChristmasPart) -> Unit,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 18.dp, end = 18.dp, top = 24.dp, bottom = 38.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SmallRoundButton("‹", onBack)
                Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("UNSER WEIHNACHTEN", color = ChristmasPink, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.1.sp)
                    Text("Wählt euren Teil", color = Color.White, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.size(40.dp))
            }
            Spacer(Modifier.height(9.dp))
            PlayModeControl(playMode, onPlayModeChange)
            Spacer(Modifier.height(7.dp))
        }

        itemsIndexed(ChristmasGameDefinition.parts, key = { _, it -> it.number }) { index, part ->
            var visible by remember(part.number) { mutableStateOf(false) }
            LaunchedEffect(part.number) {
                delay(130L + index * 125L)
                visible = true
            }
            AnimatedVisibility(visible, enter = fadeIn(tween(560)) + slideInVertically(tween(610)) { it / 4 }) {
                ChristmasPartCard(part) { onPartSelected(part) }
            }
        }
    }
}

@Composable
private fun PlayModeControl(mode: ChristmasPlayMode, onChange: (ChristmasPlayMode) -> Unit) {
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text("Spielmodus", color = ChristmasMuted, fontSize = 11.sp)
        Spacer(Modifier.width(8.dp))
        listOf(ChristmasPlayMode.TOGETHER to "Gemeinsam", ChristmasPlayMode.ROUND_ROBIN to "Reihum").forEach { (value, label) ->
            val selected = mode == value
            Box(
                Modifier
                    .clip(RoundedCornerShape(50))
                    .background(if (selected) Color(0xFF8E45D0).copy(.30f) else Color.White.copy(.045f))
                    .clickable { onChange(value) }
                    .padding(horizontal = 10.dp, vertical = 5.dp)
                    .testTag("christmas_mode_${value.name.lowercase()}"),
            ) {
                Text(label, color = if (selected) Color(0xFFE8C8FF) else ChristmasMuted, fontSize = 10.5.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium)
            }
            Spacer(Modifier.width(5.dp))
        }
    }
}

@Composable
private fun ChristmasPartCard(part: ChristmasPart, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(Brush.horizontalGradient(listOf(Color(0xDB2C1238), Color(0xE0180A25), Color(0xE10C0613))))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("christmas_part_${part.number}"),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ChristmasLightSymbol(part, Modifier.size(112.dp), part.number * .17f)
        Column(Modifier.weight(1f).padding(start = 7.dp)) {
            Text("TEIL ${part.number}", color = part.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.4.sp)
            Spacer(Modifier.height(4.dp))
            Text(part.title, color = Color.White, fontSize = 18.sp, lineHeight = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(5.dp))
            Text(part.subtitle, color = ChristmasMuted, fontSize = 12.5.sp, lineHeight = 17.sp)
            Spacer(Modifier.height(6.dp))
            Text("15 Runden", color = part.accent.copy(.82f), fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
        }
        Text("›", color = part.accent, fontSize = 34.sp)
    }
}

@Composable
private fun ChristmasRoundPlayer(
    part: ChristmasPart,
    playMode: ChristmasPlayMode,
    audio: ChristmasAudioController,
    onBack: () -> Unit,
    onComplete: () -> Unit,
) {
    var roundIndex by remember(part.number) { mutableIntStateOf(0) }
    val round = part.rounds[roundIndex]
    var selected by remember(round.id) { mutableStateOf<ChristmasOption?>(null) }
    var revealed by remember(round.id) { mutableStateOf(setOf<Int>()) }
    var tournamentWinner by remember(round.id) { mutableStateOf<ChristmasOption?>(null) }
    var tournamentOpponent by remember(round.id) { mutableIntStateOf(1) }
    val haptics = LocalHapticFeedback.current

    Column(
        Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(horizontal = 17.dp, vertical = 19.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            SmallRoundButton("‹", onBack)
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("TEIL ${part.number} · ${roundIndex + 1}/15", color = part.accent, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                if (playMode == ChristmasPlayMode.ROUND_ROBIN) {
                    Text("Person ${(roundIndex % 4) + 1} ist dran", color = ChristmasMuted, fontSize = 10.5.sp)
                }
            }
            Spacer(Modifier.size(40.dp))
        }
        Spacer(Modifier.height(8.dp))
        ChristmasProgress((roundIndex + 1) / 15f, part.accent)
        Spacer(Modifier.height(15.dp))

        AnimatedContent(round.id, label = "christmas_round_content") {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(round.title.uppercase(), color = part.accent, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
                Spacer(Modifier.height(7.dp))
                Text(round.prompt, color = Color.White, fontSize = 24.sp, lineHeight = 29.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                Spacer(Modifier.height(17.dp))

                when (round.kind) {
                    ChristmasRoundKind.TOURNAMENT -> {
                        val left = tournamentWinner ?: round.options.first()
                        val right = round.options[tournamentOpponent]
                        Text("DUELL ${tournamentOpponent} VON 3", color = ChristmasMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(8.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                            listOf(left, right).forEachIndexed { index, option ->
                                ChristmasOptionCard(
                                    option = option,
                                    index = index,
                                    accent = part.accent,
                                    selected = selected == option,
                                    hidden = false,
                                    onRevealSound = { audio.playCardFlip() },
                                    onClick = {
                                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                        if (tournamentOpponent < round.options.lastIndex) {
                                            tournamentWinner = option
                                            tournamentOpponent++
                                            audio.playCardFlip()
                                        } else selected = option
                                    },
                                    modifier = Modifier.weight(1f).aspectRatio(.73f),
                                )
                            }
                        }
                    }
                    ChristmasRoundKind.SPOTLIGHT -> {
                        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
                            round.options.forEachIndexed { index, option ->
                                ChristmasOptionCard(
                                    option, index, part.accent, selected == option, false, { audio.playCardFlip() },
                                    onClick = { selected = option; haptics.performHapticFeedback(HapticFeedbackType.LongPress) },
                                    modifier = Modifier.fillMaxWidth().height(86.dp),
                                    compact = true,
                                )
                            }
                        }
                    }
                    else -> {
                        round.options.chunked(2).forEachIndexed { row, options ->
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                options.forEachIndexed { column, option ->
                                    val index = row * 2 + column
                                    ChristmasOptionCard(
                                        option = option,
                                        index = index,
                                        accent = part.accent,
                                        selected = selected == option,
                                        hidden = round.kind == ChristmasRoundKind.REVEAL && index !in revealed,
                                        onRevealSound = { audio.playCardFlip() },
                                        onClick = {
                                            if (round.kind == ChristmasRoundKind.REVEAL && index !in revealed) {
                                                revealed = revealed + index
                                                audio.playCardFlip()
                                            } else {
                                                selected = option
                                                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                                            }
                                        },
                                        modifier = Modifier.weight(1f).aspectRatio(.91f),
                                    )
                                }
                            }
                            if (row == 0) Spacer(Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        AnimatedVisibility(selected != null, enter = fadeIn(tween(250)) + slideInVertically { it / 2 }) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(Brush.horizontalGradient(listOf(part.accent, part.secondaryAccent)))
                    .clickable {
                        if (roundIndex == part.rounds.lastIndex) onComplete() else roundIndex++
                    }
                    .padding(vertical = 15.dp)
                    .testTag("christmas_next_round"),
                contentAlignment = Alignment.Center,
            ) {
                Text(if (roundIndex == 14) "Teil abschließen ✨" else "Nächste Runde", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun ChristmasOptionCard(
    option: ChristmasOption,
    index: Int,
    accent: Color,
    selected: Boolean,
    hidden: Boolean,
    onRevealSound: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    val reveal = remember(option.label) { Animatable(0f) }
    val density = LocalDensity.current.density
    LaunchedEffect(option.label) {
        delay(95L + index * 115L)
        onRevealSound()
        reveal.animateTo(1f, tween(520, easing = FastOutSlowInEasing))
    }
    val progress = reveal.value
    val direction = if (index % 2 == 0) -1f else 1f
    val shape = RoundedCornerShape(if (compact) 20.dp else 24.dp)

    Row(
        modifier = modifier
            .graphicsLayer {
                alpha = progress
                rotationY = direction * 72f * (1f - progress)
                translationX = direction * 18f * density * (1f - progress)
                scaleX = .89f + progress * .11f
                scaleY = .89f + progress * .11f
                transformOrigin = TransformOrigin(.5f, .5f)
                cameraDistance = 26f * density
                shadowElevation = if (selected) 16f else 5f
            }
            .clip(shape)
            .background(
                if (selected) Brush.radialGradient(listOf(accent.copy(.38f), Color(0xFF24102E), Color(0xFF120819)))
                else Brush.verticalGradient(listOf(Color(0xFF2A1535), Color(0xFF14091C)))
            )
            .clickable(onClick = onClick)
            .padding(if (compact) 11.dp else 12.dp)
            .testTag("christmas_option_$index"),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        if (hidden) {
            ChristmasMysteryArt(accent, Modifier.size(if (compact) 54.dp else 64.dp))
            Spacer(Modifier.width(if (compact) 10.dp else 7.dp))
            Text(
                "Antippen & aufdecken",
                color = ChristmasMuted,
                fontSize = if (compact) 13.sp else 11.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        } else {
            ChristmasOptionArt(option.emoji, accent, Modifier.size(if (compact) 57.dp else 64.dp))
            Spacer(Modifier.width(if (compact) 11.dp else 7.dp))
            Text(
                option.label,
                color = Color.White,
                fontSize = if (compact) 15.sp else 12.5.sp,
                lineHeight = if (compact) 17.sp else 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ChristmasOptionArt(emoji: String, accent: Color, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "option_glow_$emoji")
    val pulse by transition.animateFloat(.72f, 1f, infiniteRepeatable(tween(2100, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "option_glow")
    Box(modifier, contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Brush.radialGradient(listOf(accent.copy(.28f * pulse), Color.Transparent)), radius = size.minDimension * .50f)
            val path = Path().apply {
                moveTo(size.width * .06f, size.height * .68f)
                cubicTo(size.width * .28f, size.height * .13f, size.width * .78f, size.height * .13f, size.width * .94f, size.height * .57f)
            }
            drawPath(path, accent.copy(.12f * pulse), style = Stroke(size.minDimension * .15f, cap = StrokeCap.Round))
            drawPath(path, Brush.horizontalGradient(listOf(Color.Transparent, accent, Color.White, Color.Transparent)), style = Stroke(size.minDimension * .023f, cap = StrokeCap.Round))
        }
        Text(emoji, fontSize = if (emoji.length > 3) 31.sp else 38.sp)
    }
}

@Composable
private fun ChristmasMysteryArt(accent: Color, modifier: Modifier) {
    val transition = rememberInfiniteTransition(label = "mystery")
    val pulse by transition.animateFloat(.92f, 1.06f, infiniteRepeatable(tween(1250), RepeatMode.Reverse), label = "mystery_pulse")
    Box(modifier.scale(pulse), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            drawCircle(Brush.radialGradient(listOf(accent.copy(.32f), Color.Transparent)), radius = size.minDimension * .48f)
            repeat(7) { index ->
                val angle = index * (2f * PI.toFloat() / 7f)
                val center = Offset(size.width / 2 + cos(angle) * size.width * .34f, size.height / 2 + sin(angle) * size.height * .34f)
                drawCircle(Color.White.copy(.30f + index * .06f), 1.2f + index % 2, center)
            }
        }
        Text("✦", color = Color.White, fontSize = 35.sp, fontWeight = FontWeight.Light)
    }
}

@Composable
private fun ChristmasPartComplete(part: ChristmasPart?, onParts: () -> Unit) {
    val resolved = part ?: return
    Column(Modifier.fillMaxSize().padding(26.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        ChristmasLightSymbol(resolved, Modifier.size(240.dp), .23f)
        Text("Teil ${resolved.number} geschafft", color = resolved.accent, fontSize = 13.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.8.sp)
        Spacer(Modifier.height(8.dp))
        Text("Euer Weihnachtslicht leuchtet weiter", color = Color.White, fontSize = 27.sp, lineHeight = 32.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(25.dp))
        Box(Modifier.clip(RoundedCornerShape(18.dp)).background(Brush.horizontalGradient(listOf(resolved.accent, resolved.secondaryAccent))).clickable(onClick = onParts).padding(horizontal = 30.dp, vertical = 14.dp)) {
            Text("Zu den vier Teilen", color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ChristmasLightSymbol(part: ChristmasPart, modifier: Modifier, phaseOffset: Float) {
    val entrance = remember(part.number) { Animatable(0f) }
    LaunchedEffect(part.number) {
        entrance.animateTo(1f, keyframes { durationMillis = 920; 0f at 0; .72f at 350; 1.12f at 610; 1f at 920 })
    }
    val transition = rememberInfiniteTransition(label = "part_symbol_${part.number}")
    val pulse by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(2700 + part.number * 140, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse")
    val travel by transition.animateFloat(0f, 1f, infiniteRepeatable(tween(4300 + part.number * 180, easing = LinearEasing), RepeatMode.Restart), label = "travel")
    val sparks = remember(part.number) { val r = Random(7200 + part.number); List(10) { Triple(.10f + r.nextFloat() * .8f, .09f + r.nextFloat() * .82f, r.nextFloat() * 6.28f) } }

    Canvas(modifier) {
        val center = Offset(size.width / 2, size.height / 2)
        val glow = .72f + pulse * .28f
        drawCircle(Brush.radialGradient(listOf(part.accent.copy(.22f * glow), part.secondaryAccent.copy(.08f), Color.Transparent), center, size.minDimension * .49f), size.minDimension * .49f, center)
        val trail = christmasTrail(part.number)
        drawPath(trail, part.accent.copy(.09f * glow), style = Stroke(size.minDimension * .11f, cap = StrokeCap.Round))
        drawPath(trail, Brush.linearGradient(listOf(Color.Transparent, part.accent, part.secondaryAccent, Color.Transparent)), alpha = .58f * glow, style = Stroke(size.minDimension * .025f, cap = StrokeCap.Round))
        drawPath(trail, Color.White.copy(.40f * glow), style = Stroke(size.minDimension * .006f, cap = StrokeCap.Round))
        val measure = PathMeasure().apply { setPath(trail, false) }
        val comet = measure.getPosition(((travel + phaseOffset) % 1f) * measure.length)
        drawCircle(part.accent.copy(.22f), size.minDimension * .075f, comet, blendMode = BlendMode.Screen)
        drawCircle(Color.White.copy(.96f), size.minDimension * .017f, comet)
        val symbolScale = (.96f + pulse * .06f) * entrance.value.coerceAtLeast(.01f)
        scale(symbolScale, symbolScale, center) { drawChristmasSymbol(part.symbol, center, size.minDimension * .27f, part.accent, glow) }
        val burst = (1f - abs(entrance.value - .72f) / .40f).coerceIn(0f, 1f)
        if (burst > 0f) {
            val reach = size.minDimension * (.18f + burst * .18f)
            drawLine(Color.White.copy(burst * .56f), Offset(center.x - reach, center.y), Offset(center.x + reach, center.y), 1.4f)
            drawLine(part.accent.copy(burst * .42f), Offset(center.x - reach * .58f, center.y + reach * .58f), Offset(center.x + reach * .58f, center.y - reach * .58f), 2f)
        }
        sparks.forEachIndexed { index, spark ->
            val wave = (sin((pulse * 2f * PI + spark.third + index).toFloat()) + 1f) * .5f
            val point = Offset(size.width * spark.first, size.height * spark.second)
            drawCircle(part.accent.copy(.10f * wave), 7f, point)
            drawCircle(Color.White.copy(.18f + .68f * wave), 1.4f, point)
        }
    }
}

private fun DrawScope.christmasTrail(number: Int): Path = when (number) {
    1 -> Path().apply { moveTo(size.width * .08f, size.height * .66f); cubicTo(size.width * .18f, size.height * .14f, size.width * .75f, size.height * .12f, size.width * .92f, size.height * .55f) }
    2 -> Path().apply { moveTo(size.width * .10f, size.height * .36f); cubicTo(size.width * .35f, size.height * .05f, size.width * .94f, size.height * .29f, size.width * .78f, size.height * .88f) }
    3 -> Path().apply { moveTo(size.width * .07f, size.height * .72f); cubicTo(size.width * .36f, size.height * .98f, size.width * .94f, size.height * .72f, size.width * .86f, size.height * .18f) }
    else -> Path().apply { moveTo(size.width * .12f, size.height * .84f); cubicTo(size.width * .66f, size.height * .98f, size.width * .98f, size.height * .45f, size.width * .72f, size.height * .10f) }
}

private fun DrawScope.drawChristmasSymbol(symbol: ChristmasSymbol, center: Offset, radius: Float, accent: Color, glow: Float) {
    fun line(path: Path, width: Float = radius * .075f) {
        drawPath(path, accent.copy(.17f * glow), style = Stroke(width * 4.2f, cap = StrokeCap.Round))
        drawPath(path, accent.copy(.76f * glow), style = Stroke(width * 1.8f, cap = StrokeCap.Round))
        drawPath(path, Color.White.copy(.94f), style = Stroke(width, cap = StrokeCap.Round))
    }
    when (symbol) {
        ChristmasSymbol.CANDLE -> {
            line(Path().apply { moveTo(center.x-radius*.24f,center.y+radius*.58f); lineTo(center.x-radius*.18f,center.y-radius*.12f); lineTo(center.x+radius*.23f,center.y-radius*.12f); lineTo(center.x+radius*.27f,center.y+radius*.58f) })
            line(Path().apply { moveTo(center.x,center.y-radius*.20f); cubicTo(center.x-radius*.25f,center.y-radius*.48f,center.x+radius*.02f,center.y-radius*.82f,center.x+radius*.10f,center.y-radius*.98f); cubicTo(center.x+radius*.38f,center.y-radius*.54f,center.x+radius*.24f,center.y-radius*.23f,center.x,center.y-radius*.20f) })
            line(Path().apply { moveTo(center.x-radius*.30f,center.y+radius*.52f); cubicTo(center.x-radius*.78f,center.y+radius*.26f,center.x-radius*.78f,center.y-radius*.24f,center.x-radius*.42f,center.y-radius*.50f); moveTo(center.x+radius*.32f,center.y+radius*.52f); cubicTo(center.x+radius*.78f,center.y+radius*.24f,center.x+radius*.74f,center.y-radius*.24f,center.x+radius*.43f,center.y-radius*.48f) }, radius*.055f)
        }
        ChristmasSymbol.GIFT -> {
            line(Path().apply { moveTo(center.x-radius*.72f,center.y-radius*.22f); lineTo(center.x+radius*.72f,center.y-radius*.22f); lineTo(center.x+radius*.62f,center.y+radius*.66f); lineTo(center.x-radius*.62f,center.y+radius*.66f); close(); moveTo(center.x,center.y-radius*.48f); lineTo(center.x,center.y+radius*.66f); moveTo(center.x-radius*.82f,center.y-radius*.22f); lineTo(center.x-radius*.82f,center.y-radius*.48f); lineTo(center.x+radius*.82f,center.y-radius*.48f); lineTo(center.x+radius*.82f,center.y-radius*.22f) })
            line(Path().apply { moveTo(center.x,center.y-radius*.50f); cubicTo(center.x-radius*.16f,center.y-radius*.92f,center.x-radius*.70f,center.y-radius*.85f,center.x-radius*.48f,center.y-radius*.48f); moveTo(center.x,center.y-radius*.50f); cubicTo(center.x+radius*.16f,center.y-radius*.92f,center.x+radius*.70f,center.y-radius*.85f,center.x+radius*.48f,center.y-radius*.48f) })
        }
        ChristmasSymbol.FILM_MUSIC -> {
            line(Path().apply { moveTo(center.x-radius*.74f,center.y-radius*.28f); lineTo(center.x+radius*.68f,center.y-radius*.52f); lineTo(center.x+radius*.74f,center.y-radius*.20f); lineTo(center.x-radius*.68f,center.y+radius*.03f); close(); moveTo(center.x-radius*.67f,center.y+radius*.04f); lineTo(center.x-radius*.55f,center.y+radius*.68f); lineTo(center.x+radius*.60f,center.y+radius*.48f); lineTo(center.x+radius*.72f,center.y-radius*.18f) })
            line(Path().apply { moveTo(center.x+radius*.12f,center.y+radius*.42f); lineTo(center.x+radius*.06f,center.y+radius*.04f); lineTo(center.x+radius*.43f,center.y-radius*.02f); lineTo(center.x+radius*.48f,center.y+radius*.31f); moveTo(center.x+radius*.12f,center.y+radius*.42f); cubicTo(center.x-radius*.10f,center.y+radius*.53f,center.x-radius*.22f,center.y+radius*.28f,center.x+radius*.02f,center.y+radius*.24f) }, radius*.06f)
        }
        ChristmasSymbol.STAR -> {
            val star = Path(); repeat(10) { i -> val angle=-PI/2+i*PI/5; val r=if(i%2==0) radius else radius*.43f; val p=Offset(center.x+cos(angle).toFloat()*r,center.y+sin(angle).toFloat()*r); if(i==0) star.moveTo(p.x,p.y) else star.lineTo(p.x,p.y) }; star.close(); line(star,radius*.065f); drawCircle(Color.White.copy(.94f),radius*.08f,center)
        }
    }
}

@Composable
private fun ChristmasAtmosphere(modifier: Modifier) {
    val flakes = remember { val random=Random(2412); List(44) { Triple(random.nextFloat(),random.nextFloat(),.35f+random.nextFloat()*.65f) } }
    val transition=rememberInfiniteTransition(label="christmas_snow")
    val drift by transition.animateFloat(0f,1f,infiniteRepeatable(tween(19000,easing=LinearEasing),RepeatMode.Restart),label="snow_drift")
    Canvas(modifier) {
        drawCircle(Brush.radialGradient(listOf(Color(0xFF7B35C9).copy(.24f),Color.Transparent)),size.width*.85f,Offset(size.width*.88f,size.height*.04f))
        drawCircle(Brush.radialGradient(listOf(Color(0xFFD5328C).copy(.14f),Color.Transparent)),size.width*.68f,Offset(size.width*.02f,size.height*.30f))
        flakes.forEachIndexed { index,(x,y,speed) -> val yy=((y+drift*speed)%1f)*size.height; val xx=x*size.width+sin(drift*6.283f+index)*10f; val r=.8f+speed*1.7f; drawCircle(Color.White.copy(.10f+speed*.34f),r*3f,Offset(xx,yy)); drawCircle(Color.White.copy(.42f+speed*.45f),r,Offset(xx,yy)) }
    }
}

@Composable private fun ChristmasProgress(progress: Float, accent: Color) {
    Box(Modifier.fillMaxWidth().height(5.dp).clip(CircleShape).background(Color.White.copy(.08f))) {
        Box(Modifier.fillMaxWidth(progress.coerceIn(0f,1f)).fillMaxHeight().background(Brush.horizontalGradient(listOf(ChristmasPink,accent))))
    }
}

@Composable private fun SmallRoundButton(label: String, onClick: () -> Unit) {
    Box(Modifier.size(40.dp).clip(CircleShape).background(Color.White.copy(.07f)).clickable(onClick=onClick),contentAlignment=Alignment.Center) { Text(label,color=Color.White,fontSize=28.sp,fontWeight=FontWeight.Light) }
}

private val ChristmasNight = Color(0xFF08020D)
private val ChristmasPink = Color(0xFFFF5DB7)
private val ChristmasMuted = Color(0xFFD0C0D6)
