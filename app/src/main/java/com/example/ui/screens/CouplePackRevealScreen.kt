package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.couple.CouplePackQuestionResult
import com.example.data.couple.CoupleQuestionRepository
import com.example.data.couple.CoupleRevealState
import com.example.data.model.QuestionPack
import com.example.data.model.TravelDestinationCatalog
import com.example.data.session.AppSession
import com.example.data.session.UserProfile
import com.example.ui.components.TotImageProvider
import com.example.ui.contentText
import com.example.ui.tr
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.coroutines.coroutineContext
import java.io.File

@Composable
fun CouplePackRevealScreen(
    pack: QuestionPack,
    session: AppSession,
    answers: Map<Int, String>,
    repository: CoupleQuestionRepository,
    onReplay: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var results by remember(pack.id, session.coupleId) { mutableStateOf<List<CouplePackQuestionResult>>(emptyList()) }
    var animatedIndexes by remember(pack.id, session.coupleId) { mutableStateOf<Set<Int>>(emptySet()) }
    var isLoading by remember(pack.id, session.coupleId) { mutableStateOf(true) }
    var errorMessage by remember(pack.id, session.coupleId) { mutableStateOf<String?>(null) }

    LaunchedEffect(pack.id, session.coupleId, answers) {
        if (!session.isPaired) return@LaunchedEffect
        answers.forEach { (questionIndex, answerText) ->
            if (answerText.isNotBlank()) {
                runCatching { repository.submitAnswer(pack.id, questionIndex, answerText) }
            }
        }

        while (coroutineContext.isActive) {
            runCatching { repository.getPackResults(pack.id) }
                .onSuccess { loaded ->
                    results = loaded
                    errorMessage = null
                    isLoading = false
                }
                .onFailure {
                    if (results.isEmpty()) {
                        errorMessage = "Die gemeinsamen Ergebnisse konnten gerade nicht geladen werden."
                        isLoading = false
                    }
                }

            // Keep the result view synchronized for as long as it is open. A partner can
            // change an already answered question, so stopping after the first READY state
            // would freeze the old answer on screen.
            delay(1_500)
        }
    }

    Box(
        modifier = modifier.fillMaxSize().background(
            Brush.verticalGradient(listOf(Color(0xFF17071F), HarmonyBg, Color(0xFF07020A)))
        )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(18.dp, 34.dp, 18.dp, 34.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "header") {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("💕", fontSize = 44.sp)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = contentText(pack.title),
                        color = HarmonyText,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = "Eure Antworten bleiben getrennt, bis ihr beide dieselbe Frage beantwortet habt.",
                        color = HarmonyMuted,
                        fontSize = 13.5.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 19.sp
                    )
                    Spacer(Modifier.height(16.dp))
                    CoupleHeaderProfiles(session)
                }
            }

            if (isLoading && results.isEmpty()) {
                item(key = "loading") {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = HarmonyPink)
                    }
                }
            }

            if (!errorMessage.isNullOrBlank()) {
                item(key = "error") {
                    Text(
                        text = errorMessage.orEmpty(),
                        color = Color(0xFFFF8CA8),
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                    )
                }
            }

            val indexes = answers.keys.sorted()
            items(indexes, key = { "question_$it" }) { questionIndex ->
                val result = results.firstOrNull { it.questionIndex == questionIndex }
                CoupleQuestionRevealCard(
                    pack = pack,
                    questionNumber = questionIndex + 1,
                    questionText = questionLabel(pack, questionIndex),
                    session = session,
                    myAnswer = answers[questionIndex].orEmpty(),
                    result = result,
                    alreadyAnimated = questionIndex in animatedIndexes,
                    onAnimationFinished = { animatedIndexes = animatedIndexes + questionIndex }
                )
            }

            item(key = "close") {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                    Text("Zurück zu Harmony", color = HarmonyPink, fontWeight = FontWeight.Bold)
                }
            }
        }

        IconButton(
            onClick = onReplay,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 8.dp, end = 12.dp)
                .size(42.dp)
                .clip(CircleShape)
                .background(Color(0xFF24102F).copy(alpha = 0.96f))
                .border(1.dp, HarmonyPink.copy(alpha = 0.42f), CircleShape)
                .testTag("couple_replay_button")
        ) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = tr("Fragen neu beantworten", "Answer questions again"),
                tint = Color.White
            )
        }
    }
}

@Composable
private fun CoupleHeaderProfiles(session: AppSession) {
    val partner = session.partner ?: return
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        CoupleRemoteAvatar(session.profile, 58)
        Text("   💗   ", fontSize = 20.sp)
        CoupleRemoteAvatar(partner, 58)
    }
    Spacer(Modifier.height(7.dp))
    Text(
        text = "${session.profile.displayName} & ${partner.displayName}",
        color = HarmonyText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun CoupleQuestionRevealCard(
    pack: QuestionPack,
    questionNumber: Int,
    questionText: String,
    session: AppSession,
    myAnswer: String,
    result: CouplePackQuestionResult?,
    alreadyAnimated: Boolean,
    onAnimationFinished: () -> Unit
) {
    val partner = session.partner ?: return
    val state = result?.revealState ?: CoupleRevealState.WAITING_FOR_PARTNER
    val resolvedMyAnswer = result?.myAnswerText ?: myAnswer
    val resolvedPartnerAnswer = result?.partnerAnswerText.orEmpty()
    val questionIndex = questionNumber - 1
    val myPresentation = remember(pack.id, questionIndex, resolvedMyAnswer) {
        coupleResultPresentation(pack, questionIndex, resolvedMyAnswer)
    }
    val partnerPresentation = remember(pack.id, questionIndex, resolvedPartnerAnswer) {
        coupleResultPresentation(pack, questionIndex, resolvedPartnerAnswer)
    }
    val myImageModel = remember(pack.id, resolvedMyAnswer, myPresentation.localImagePath, TotImageProvider.version) {
        localResultImageModel(myPresentation.localImagePath)
            ?: if (pack.type == "tot" && resolvedMyAnswer.isNotBlank()) {
                val assetKey = TravelDestinationCatalog.assetKeyFor(pack.id, resolvedMyAnswer)
                    ?: TotImageProvider.totAssetKey(pack.id, resolvedMyAnswer)
                TotImageProvider.getImageUrl(assetKey = assetKey, legacyAssetKey = resolvedMyAnswer)
            } else null
    }
    val partnerImageModel = remember(pack.id, resolvedPartnerAnswer, partnerPresentation.localImagePath, TotImageProvider.version) {
        localResultImageModel(partnerPresentation.localImagePath)
            ?: if (pack.type == "tot" && resolvedPartnerAnswer.isNotBlank()) {
                val assetKey = TravelDestinationCatalog.assetKeyFor(pack.id, resolvedPartnerAnswer)
                    ?: TotImageProvider.totAssetKey(pack.id, resolvedPartnerAnswer)
                TotImageProvider.getImageUrl(assetKey = assetKey, legacyAssetKey = resolvedPartnerAnswer)
            } else null
    }
    var cardEntered by remember(questionNumber) { mutableStateOf(false) }
    var ownVisible by remember(questionNumber) { mutableStateOf(alreadyAnimated) }
    var partnerVisible by remember(questionNumber) { mutableStateOf(alreadyAnimated) }

    LaunchedEffect(questionNumber) {
        cardEntered = true
    }
    LaunchedEffect(state, alreadyAnimated, resolvedMyAnswer, resolvedPartnerAnswer) {
        if (state == CoupleRevealState.READY) {
            if (alreadyAnimated) {
                ownVisible = true
                partnerVisible = true
            } else {
                ownVisible = false
                partnerVisible = false
                delay(110)
                ownVisible = true
                delay(230)
                partnerVisible = true
                delay(440)
                onAnimationFinished()
            }
        } else {
            ownVisible = false
            partnerVisible = false
        }
    }

    val cardProgress by animateFloatAsState(
        targetValue = if (cardEntered) 1f else 0f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing),
        label = "result_card_enter"
    )
    val shape = RoundedCornerShape(22.dp)
    val showOwnTotPreview = pack.type == "tot" &&
        myAnswer.isNotBlank() &&
        state != CoupleRevealState.NEEDS_OWN_ANSWER &&
        state != CoupleRevealState.READY

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                alpha = cardProgress
                translationY = (1f - cardProgress) * 24f
            }
            .clip(shape)
            .background(HarmonySurface2.copy(alpha = 0.92f))
            .border(1.dp, HarmonyLine, shape)
            .padding(16.dp)
    ) {
        Text("Frage $questionNumber", color = HarmonyPink, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(5.dp))
        Text(questionText, color = HarmonyText, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, lineHeight = 21.sp)
        if (showOwnTotPreview) {
            Spacer(Modifier.height(14.dp))
            TotOwnAnswerPreview(pack = pack, myAnswer = myAnswer)
        }
        Spacer(Modifier.height(14.dp))

        when {
            state == CoupleRevealState.READY -> {
                AnimatedVisibility(
                    visible = ownVisible,
                    enter = fadeIn(tween(220)) + slideInVertically(
                        initialOffsetY = { it / 5 },
                        animationSpec = tween(300, easing = FastOutSlowInEasing)
                    )
                ) {
                    CoupleAnswerRow(
                        profile = session.profile,
                        presentation = myPresentation,
                        accent = HarmonyPink,
                        answerImageModel = myImageModel
                    )
                }
                Spacer(Modifier.height(9.dp))
                AnimatedVisibility(
                    visible = partnerVisible,
                    enter = fadeIn(tween(120))
                ) {
                    FlippingCoupleAnswerRow(
                        profile = partner,
                        presentation = partnerPresentation,
                        accent = HarmonyPurple,
                        answerImageModel = partnerImageModel
                    )
                }
            }
            state == CoupleRevealState.NEEDS_OWN_ANSWER -> {
                Text(
                    "Beantworte diese Frage zuerst selbst, bevor eine Partnerantwort sichtbar werden kann.",
                    color = HarmonyMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
            else -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoupleRemoteAvatar(session.profile, 36)
                    Column(Modifier.padding(start = 9.dp).weight(1f)) {
                        Text("Deine Antwort ist gespeichert ✓", color = HarmonyText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "Warte auf ${partner.displayName}. Die Antwort bleibt bis dahin verborgen.",
                            color = HarmonyMuted,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TotOwnAnswerPreview(
    pack: QuestionPack,
    myAnswer: String
) {
    val assetKey = remember(pack.id, myAnswer) {
        TravelDestinationCatalog.assetKeyFor(pack.id, myAnswer)
            ?: TotImageProvider.totAssetKey(pack.id, myAnswer)
    }
    val imageModel = remember(assetKey, myAnswer, TotImageProvider.version) {
        TotImageProvider.getImageUrl(
            assetKey = assetKey,
            legacyAssetKey = myAnswer
        )
    }
    val shape = RoundedCornerShape(16.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HarmonyPink.copy(alpha = 0.10f))
            .border(1.dp, HarmonyPink.copy(alpha = 0.28f), shape)
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = imageModel,
            contentDescription = contentText(myAnswer),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(68.dp)
                .clip(RoundedCornerShape(13.dp))
        )
        Column(
            modifier = Modifier
                .padding(start = 12.dp)
                .weight(1f)
        ) {
            Text(
                text = tr("Deine Auswahl", "Your choice"),
                color = HarmonyPink,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(Modifier.height(3.dp))
            Text(
                text = contentText(myAnswer),
                color = HarmonyText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 19.sp
            )
        }
    }
}

@Composable
private fun CoupleAnswerRow(
    profile: UserProfile,
    presentation: CoupleResultPresentation,
    accent: Color,
    answerImageModel: Any? = null
) {
    val answerImageRes = presentation.imageRes
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(accent.copy(alpha = 0.12f))
            .border(1.dp, accent.copy(alpha = 0.30f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoupleRemoteAvatar(profile, 42)
        if (answerImageRes != null || answerImageModel != null) {
            Spacer(Modifier.size(10.dp))
            val imageModifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, accent.copy(alpha = 0.34f), RoundedCornerShape(14.dp))
            if (answerImageRes != null) {
                Image(
                    painter = painterResource(answerImageRes),
                    contentDescription = contentText(presentation.displayText),
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            } else {
                AsyncImage(
                    model = answerImageModel,
                    contentDescription = contentText(presentation.displayText),
                    contentScale = ContentScale.Crop,
                    modifier = imageModifier
                )
            }
        }
        Column(Modifier.padding(start = 10.dp).weight(1f)) {
            Text(profile.displayName, color = HarmonyMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(
                text = contentText(presentation.displayText),
                color = HarmonyText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 19.sp
            )
            presentation.detailLines.forEach { line ->
                Spacer(Modifier.height(3.dp))
                Text(
                    text = line,
                    color = HarmonyMuted,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun FlippingCoupleAnswerRow(
    profile: UserProfile,
    presentation: CoupleResultPresentation,
    accent: Color,
    answerImageModel: Any? = null
) {
    val density = LocalDensity.current.density
    val animationKey = presentation.displayText + "|" + presentation.detailLines.joinToString("|")
    val rotation = remember(profile.userId, animationKey) { Animatable(88f) }
    LaunchedEffect(profile.userId, animationKey) {
        rotation.snapTo(88f)
        rotation.animateTo(
            targetValue = 0f,
            animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
        )
    }
    Box(
        modifier = Modifier.graphicsLayer {
            rotationY = rotation.value
            cameraDistance = 14f * density
            alpha = (1f - rotation.value / 110f).coerceIn(0.28f, 1f)
        }
    ) {
        CoupleAnswerRow(
            profile = profile,
            presentation = presentation,
            accent = accent,
            answerImageModel = answerImageModel
        )
    }
}

private fun localResultImageModel(path: String?): Any? = path
    ?.takeIf(String::isNotBlank)
    ?.let(::File)
    ?.takeIf(File::exists)

@Composable
private fun CoupleRemoteAvatar(profile: UserProfile, size: Int) {
    Box(
        modifier = Modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurple)))
            .border(1.5.dp, Color.White.copy(alpha = 0.62f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!profile.avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = profile.avatarUrl,
                contentDescription = profile.displayName,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(CircleShape)
            )
        } else {
            Text(
                text = profile.displayName.take(1).uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = (size * 0.36f).sp
            )
        }
    }
}

@Composable
private fun questionLabel(pack: QuestionPack, index: Int): String = when (pack.type) {
    "tot" -> pack.pairs.getOrNull(index)?.let { "${contentText(it.first)} oder ${contentText(it.second)}?" }.orEmpty()
    else -> contentText(pack.questions.getOrNull(index)?.q.orEmpty())
}
