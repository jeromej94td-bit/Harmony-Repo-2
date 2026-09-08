package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.model.AnswerEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SecretPlanAnswerCodec
import com.example.data.model.SecretPlanCatalog
import com.example.data.model.SecretPlanChoice
import com.example.data.model.SecretPlanPair
import com.example.data.model.SecretPlanPlanLine
import com.example.data.model.SecretPlanProgress
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple

private enum class SecretPlanPhase { PERSON_A, HANDOFF, PERSON_B, SEALED, FINAL_PLAN }

private val BookInk = Color(0xFF4A215B)
private val BookRose = Color(0xFFD34C91)
private val BookGold = Color(0xFFFFC86A)
private val BookCream = Color(0xFFFFF2ED)

@Composable
internal fun SecretPlanBoard(
    question: String,
    options: List<String>,
    questionIndex: Int,
    historicalAnswers: List<AnswerEntity>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val restored = selectedAnswer?.let(SecretPlanAnswerCodec::decode)
    var phase by rememberSaveable(questionIndex, selectedAnswer) {
        mutableStateOf(if (restored != null) SecretPlanPhase.FINAL_PLAN else SecretPlanPhase.PERSON_A)
    }
    var first by rememberSaveable(questionIndex, selectedAnswer) { mutableStateOf(restored?.first) }
    var second by rememberSaveable(questionIndex, selectedAnswer) { mutableStateOf(restored?.second) }
    var customText by rememberSaveable(questionIndex, phase) { mutableStateOf("") }
    var showingCustom by rememberSaveable(questionIndex, phase) { mutableStateOf(false) }
    var pendingChoice by remember { mutableStateOf<SecretPlanChoice?>(null) }
    var pendingForFirst by remember { mutableStateOf(true) }
    var submitted by rememberSaveable(questionIndex, selectedAnswer) { mutableStateOf(restored != null) }
    val motion = remember { Animatable(0f) }

    LaunchedEffect(pendingChoice) {
        val choice = pendingChoice ?: return@LaunchedEffect
        motion.snapTo(0f)
        motion.animateTo(0.15f, tween(SecretPlanMotionTimeline.LIFT_END_MS, easing = FastOutSlowInEasing))
        motion.animateTo(
            0.72f,
            tween(
                SecretPlanMotionTimeline.PAGE_TURN_END_MS - SecretPlanMotionTimeline.LIFT_END_MS,
                easing = FastOutSlowInEasing
            )
        )
        motion.animateTo(
            0.95f,
            tween(
                SecretPlanMotionTimeline.SEAL_END_MS - SecretPlanMotionTimeline.PAGE_TURN_END_MS,
                easing = FastOutSlowInEasing
            )
        )
        motion.animateTo(
            1f,
            tween(
                SecretPlanMotionTimeline.TOTAL_DURATION_MS - SecretPlanMotionTimeline.SEAL_END_MS,
                easing = FastOutSlowInEasing
            )
        )
        if (pendingForFirst) {
            first = choice
            phase = SecretPlanPhase.HANDOFF
        } else {
            second = choice
            phase = SecretPlanPhase.SEALED
        }
        pendingChoice = null
        motion.snapTo(0f)
    }

    val pair = first?.let { a -> second?.let { b -> SecretPlanPair(a, b) } }
    val finalLines = pair?.let { SecretPlanProgress.lines(historicalAnswers, questionIndex, it) }

    FullscreenMechanicShell(
        kicker = "DER GEHEIME PLAN",
        question = question,
        instruction = when (phase) {
            SecretPlanPhase.PERSON_A -> "Kapitel ${questionIndex + 1} von 3 · Deine Wahl bleibt geheim"
            SecretPlanPhase.HANDOFF -> "Die Seite ist sicher versiegelt"
            SecretPlanPhase.PERSON_B -> "Kapitel ${questionIndex + 1} von 3 · ${profile.partnerName} wählt geheim"
            SecretPlanPhase.SEALED -> "Beide Seiten sind versiegelt"
            SecretPlanPhase.FINAL_PLAN -> "Eure drei Kapitel sind vollständig"
        },
        modifier = modifier.testTag("secret_plan_board")
    ) {
        Box(Modifier.fillMaxSize().background(Color(0xFF6D3F88)).testTag("secret_plan_background")) {
            AnimatedContent(phase, label = "secret-plan-phase") { current ->
                when (current) {
                    SecretPlanPhase.PERSON_A, SecretPlanPhase.PERSON_B -> {
                        val selectFirst = current == SecretPlanPhase.PERSON_A
                        if (showingCustom) {
                            CustomPlanChoice(
                                value = customText,
                                onValueChange = { customText = it.take(80) },
                                onSave = {
                                    pendingForFirst = selectFirst
                                    pendingChoice = SecretPlanChoice.Custom(customText.trim().replace(Regex("\\s+"), " "))
                                    showingCustom = false
                                }
                            )
                        } else {
                            SecretPlanMagicBook(
                                options = options.filterNot { it == SecretPlanCatalog.CUSTOM_OPTION },
                                selectedText = pendingChoice?.text,
                                motionProgress = motion.value,
                                inputEnabled = pendingChoice == null,
                                tagPrefix = if (selectFirst) "secret_plan_first_option" else "secret_plan_second_option",
                                onSelect = { raw ->
                                    if (pendingChoice == null) {
                                        pendingForFirst = selectFirst
                                        pendingChoice = SecretPlanChoice.Preset(raw)
                                    }
                                },
                                onCustom = { if (pendingChoice == null) showingCustom = true }
                            )
                        }
                    }
                    SecretPlanPhase.HANDOFF -> PairPrivateHandoffPane(
                        name = profile.partnerName,
                        title = "Übergib das Buch an ${profile.partnerName}",
                        body = "Deine Seite bleibt geschlossen.",
                        buttonTag = "secret_plan_handoff",
                        onReady = { phase = SecretPlanPhase.PERSON_B }
                    )
                    SecretPlanPhase.SEALED -> SealedBookPane(
                        questionIndex = questionIndex,
                        onContinue = {
                            if (questionIndex == 2 && finalLines != null) phase = SecretPlanPhase.FINAL_PLAN
                            else if (pair != null && !submitted) {
                                submitted = true
                                onPick(SecretPlanAnswerCodec.encode(pair.first, pair.second))
                            }
                        }
                    )
                    SecretPlanPhase.FINAL_PLAN -> FinalSecretPlan(
                        lines = finalLines.orEmpty(),
                        onSave = {
                            if (pair != null && !submitted) {
                                submitted = true
                                onPick(SecretPlanAnswerCodec.encode(pair.first, pair.second))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun SecretPlanMagicBook(
    options: List<String>,
    selectedText: String?,
    motionProgress: Float,
    inputEnabled: Boolean,
    tagPrefix: String,
    onSelect: (String) -> Unit,
    onCustom: () -> Unit
) {
    BoxWithConstraints(Modifier.fillMaxSize().testTag("secret_plan_magic_book")) {
        Image(
            painter = painterResource(R.drawable.secret_plan_magic_book),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Image(
            painter = painterResource(R.drawable.weekend_echo_panda_pair),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 22.dp, top = 48.dp)
                .size(width = 132.dp, height = 132.dp)
                .alpha(0.94f)
                .testTag("secret_plan_pandas"),
            contentScale = ContentScale.Fit
        )
        val pageTurn = ((motionProgress - 0.15f) / 0.57f).coerceIn(0f, 1f)
        val seal = ((motionProgress - 0.72f) / 0.23f).coerceIn(0f, 1f)
        val bookStageHeight = maxHeight
        val secondaryAlpha = if (selectedText != null) {
            (1f - pageTurn * 2.2f).coerceIn(0f, 1f)
        } else {
            1f
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .padding(top = maxHeight * 0.31f, bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val lift = (motionProgress / 0.15f).coerceIn(0f, 1f)
            fun Modifier.selectedPageMotion(index: Int): Modifier = graphicsLayer {
                translationY = (
                    (-8).dp * lift + bookStageHeight * (0.36f * pageTurn)
                ).toPx()
                rotationX = pageTurn * 72f
                rotationZ = (index - 1.5f) * pageTurn * 3f
                scaleX = 1f - seal * 0.55f
                scaleY = 1f - seal * 0.55f
                alpha = 1f - seal * 0.72f
                shadowElevation = (10.dp + 18.dp * lift).toPx()
                cameraDistance = 18f * density
            }

            val visibleOptions = options.take(4)
            val customSelected = selectedText != null && selectedText !in visibleOptions
            if (customSelected) {
                BookAnswerTab(
                    text = selectedText.orEmpty(),
                    enabled = false,
                    selected = true,
                    modifier = Modifier
                        .selectedPageMotion(index = 1)
                        .testTag("secret_plan_custom_selected"),
                    onClick = {}
                )
            } else {
                visibleOptions.forEachIndexed { index, text ->
                    val selected = selectedText == text
                    BookAnswerTab(
                        text = text,
                        enabled = inputEnabled,
                        selected = selected,
                        modifier = Modifier
                            .alpha(if (selected) 1f else secondaryAlpha)
                            .then(if (selected) Modifier.selectedPageMotion(index) else Modifier)
                            .testTag("${tagPrefix}_$index"),
                        onClick = { onSelect(text) }
                    )
                }
                Text(
                    text = "Eigene Idee …",
                    color = BookInk,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .alpha(secondaryAlpha)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(enabled = inputEnabled, onClick = onCustom)
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                        .testTag("secret_plan_custom_option")
                )
            }
            Spacer(Modifier.weight(1f))
            HeartSeal(progress = motionProgress)
        }
    }
}

@Composable
private fun BookAnswerTab(
    text: String,
    enabled: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val shape = RoundedCornerShape(topStart = 24.dp, bottomEnd = 24.dp, topEnd = 10.dp, bottomStart = 10.dp)
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .focusProperties { canFocus = false }
            .clickable(enabled = enabled, onClick = onClick),
        shape = shape,
        color = if (selected) Color(0xFFF7C6E5) else Color(0xE8FFF7F1),
        shadowElevation = if (selected) 14.dp else 3.dp
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .border(1.dp, if (selected) BookRose else BookGold.copy(alpha = 0.58f), shape)
                .padding(horizontal = 18.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(text, color = BookInk, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun HeartSeal(progress: Float, modifier: Modifier = Modifier) {
    val sealProgress = ((progress - 0.72f) / 0.23f).coerceIn(0f, 1f)
    Box(
        modifier = modifier
            .size(68.dp)
            .graphicsLayer {
                scaleX = 1f + sealProgress * 0.24f
                scaleY = 1f + sealProgress * 0.24f
                rotationZ = progress * 180f
            }
            .drawBehind {
                drawCircle(BookGold.copy(alpha = 0.28f + sealProgress * 0.34f), radius = size.minDimension * (0.62f + sealProgress * 0.15f))
                drawCircle(BookRose.copy(alpha = 0.72f), radius = size.minDimension * 0.48f)
            }
            .testTag("secret_plan_heart_seal"),
        contentAlignment = Alignment.Center
    ) {
        Icon(Icons.Filled.Favorite, contentDescription = "Antwort versiegeln", tint = BookCream, modifier = Modifier.size(28.dp))
    }
}

@Composable
private fun CustomPlanChoice(value: String, onValueChange: (String) -> Unit, onSave: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.secret_plan_magic_book), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Column(
            Modifier.fillMaxSize().padding(horizontal = 28.dp, vertical = 54.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Deine eigene Idee", color = BookInk, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value,
                onValueChange,
                modifier = Modifier.fillMaxWidth().testTag("secret_plan_custom_input"),
                label = { Text("Zum Beispiel: Lissabon") },
                singleLine = true
            )
            Spacer(Modifier.height(16.dp))
            PrimaryMechanicButton("Idee ins Buch legen", enabled = value.trim().isNotEmpty(), onClick = onSave, testTag = "secret_plan_custom_save")
        }
    }
}

@Composable
private fun SealedBookPane(questionIndex: Int, onContinue: () -> Unit) {
    Box(Modifier.fillMaxSize()) {
        Image(painterResource(R.drawable.secret_plan_magic_book), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Column(
            Modifier.fillMaxSize().padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            HeartSeal(progress = 0.92f)
            Spacer(Modifier.height(16.dp))
            Text("Beide Ideen sind versiegelt", color = BookInk, fontWeight = FontWeight.Bold, fontSize = 20.sp, textAlign = TextAlign.Center)
            Spacer(Modifier.height(22.dp))
            PrimaryMechanicButton(
                text = if (questionIndex == 2) "Planbuch öffnen" else "Nächstes Kapitel",
                onClick = onContinue,
                testTag = "secret_plan_chapter_continue"
            )
        }
    }
}

@Composable
private fun FinalSecretPlan(lines: List<SecretPlanPlanLine>, onSave: () -> Unit) {
    Box(Modifier.fillMaxSize().testTag("secret_plan_final_card")) {
        Image(painterResource(R.drawable.secret_plan_magic_book), null, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(tween(900)) + scaleIn(tween(1_200), initialScale = 0.88f)
        ) {
            Column(
                Modifier.fillMaxSize().padding(horizontal = 34.dp, vertical = 34.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(96.dp))
                Text("Euer geheimer Plan", color = BookInk, fontSize = 24.sp, fontWeight = FontWeight.Black)
                Spacer(Modifier.height(14.dp))
                Text("Jetzt planen", color = HarmonyPurple, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                lines.filter { it.isShared }.forEach { PlanLine(it.first.text, BookGold) }
                Spacer(Modifier.height(12.dp))
                Text("Auch schön", color = BookRose, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                lines.filterNot { it.isShared }.forEach {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        PlanLine(it.first.text, HarmonyPink, Modifier.weight(1f))
                        PlanLine(it.second.text, Color(0xFF4FBFC4), Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.weight(1f))
                PrimaryMechanicButton("Plan speichern", onClick = onSave, testTag = "secret_plan_save")
            }
        }
    }
}

@Composable
private fun PlanLine(text: String, accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .padding(top = 7.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Brush.horizontalGradient(listOf(accent.copy(alpha = 0.26f), Color.White.copy(alpha = 0.58f))))
            .border(1.dp, accent.copy(alpha = 0.72f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = BookInk, fontSize = 14.sp, textAlign = TextAlign.Center, fontWeight = FontWeight.SemiBold)
    }
}
