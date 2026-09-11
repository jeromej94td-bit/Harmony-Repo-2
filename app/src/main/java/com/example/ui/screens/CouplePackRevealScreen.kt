package com.example.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.couple.CouplePackQuestionResult
import com.example.data.couple.CoupleQuestionRepository
import com.example.data.couple.CoupleRevealState
import com.example.data.couple.PartnerPackRevealPolicy
import com.example.data.model.QuestionPack
import com.example.data.session.AppSession
import com.example.data.session.UserProfile
import com.example.ui.contentText
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

@Composable
fun CouplePackRevealScreen(
    pack: QuestionPack,
    session: AppSession,
    answers: Map<Int, String>,
    repository: CoupleQuestionRepository,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val wholePackReveal = PartnerPackRevealPolicy.isWholePackRevealEnabled(pack.id)
    var results by remember(pack.id, session.coupleId) { mutableStateOf<List<CouplePackQuestionResult>>(emptyList()) }
    var revealedIndexes by remember(pack.id, session.coupleId) { mutableStateOf<Set<Int>>(emptySet()) }
    var wholePackRevealed by remember(pack.id, session.coupleId) { mutableStateOf(false) }
    var isLoading by remember(pack.id, session.coupleId) { mutableStateOf(true) }
    var errorMessage by remember(pack.id, session.coupleId) { mutableStateOf<String?>(null) }

    LaunchedEffect(pack.id, session.coupleId, answers, wholePackReveal) {
        if (!session.isPaired) return@LaunchedEffect

        if (wholePackReveal) {
            val initialResults = runCatching { repository.getPackResults(pack.id) }
                .getOrDefault(emptyList())
            if (initialResults.isNotEmpty()) {
                results = initialResults
                isLoading = false
            }

            val alreadyCompleted = initialResults.firstOrNull()?.myPackCompleted == true
            if (!alreadyCompleted) {
                val totalQuestions = if (pack.type == "tot") pack.pairs.size else pack.questions.size
                val completeLocalRun = totalQuestions > 0 &&
                    (0 until totalQuestions).all { !answers[it].isNullOrBlank() }

                if (!completeLocalRun) {
                    errorMessage = "Beantworte zuerst alle Fragen. Erst danach wird dein Durchlauf für den gemeinsamen Reveal abgeschlossen."
                    isLoading = false
                    return@LaunchedEffect
                }

                for (questionIndex in 0 until totalQuestions) {
                    val answerText = answers[questionIndex].orEmpty()
                    runCatching { repository.submitAnswer(pack.id, questionIndex, answerText) }
                        .onFailure {
                            errorMessage = "Deine Antworten konnten gerade nicht vollständig synchronisiert werden."
                            isLoading = false
                            return@LaunchedEffect
                        }
                }

                runCatching { repository.completePartnerPack(pack.id) }
                    .onFailure {
                        errorMessage = "Dein fertiger Durchlauf konnte gerade nicht abgeschlossen werden."
                        isLoading = false
                        return@LaunchedEffect
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

                if (results.firstOrNull()?.readyToReveal == true) break
                delay(3_000)
            }
        } else {
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

                val answeredIndexes = answers.keys
                val allAnsweredQuestionsReady = answeredIndexes.isNotEmpty() && answeredIndexes.all { index ->
                    results.firstOrNull { it.questionIndex == index }?.readyToReveal == true
                }
                if (allAnsweredQuestionsReady) break
                delay(3_000)
            }
        }
    }

    val myPackCompleted = results.firstOrNull()?.myPackCompleted == true
    val partnerPackCompleted = results.firstOrNull()?.partnerPackCompleted == true
    val wholePackReady = PartnerPackRevealPolicy.canRevealPartnerAnswers(
        myCompleted = myPackCompleted,
        partnerCompleted = partnerPackCompleted
    )

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
                        text = if (wholePackReveal) {
                            "Eure Antworten bleiben getrennt, bis ihr beide das ganze Spiel ausgefüllt habt. Danach enthüllt ihr alles gemeinsam."
                        } else {
                            "Eure Antworten bleiben getrennt, bis ihr beide dieselbe Frage beantwortet habt."
                        },
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

            if (wholePackReveal && !isLoading && errorMessage.isNullOrBlank()) {
                item(key = "whole_pack_status") {
                    WholePackRevealStatusCard(
                        session = session,
                        myCompleted = myPackCompleted,
                        partnerCompleted = partnerPackCompleted,
                        revealed = wholePackRevealed,
                        onReveal = { wholePackRevealed = true }
                    )
                }
            }

            val indexes = if (wholePackReveal) {
                val total = if (pack.type == "tot") pack.pairs.size else pack.questions.size
                (0 until total).toList()
            } else {
                answers.keys.sorted()
            }

            items(indexes, key = { "question_$it" }) { questionIndex ->
                val result = results.firstOrNull { it.questionIndex == questionIndex }
                if (wholePackReveal) {
                    WholePackQuestionRevealCard(
                        questionNumber = questionIndex + 1,
                        questionText = questionLabel(pack, questionIndex),
                        session = session,
                        myAnswer = result?.myAnswerText ?: answers[questionIndex].orEmpty(),
                        partnerAnswer = if (wholePackReady && wholePackRevealed) result?.partnerAnswerText else null,
                        readyToReveal = wholePackReady,
                        revealed = wholePackRevealed
                    )
                } else {
                    CoupleQuestionRevealCard(
                        questionNumber = questionIndex + 1,
                        questionText = questionLabel(pack, questionIndex),
                        session = session,
                        myAnswer = answers[questionIndex].orEmpty(),
                        result = result,
                        revealed = questionIndex in revealedIndexes,
                        onReveal = { revealedIndexes = revealedIndexes + questionIndex }
                    )
                }
            }

            item(key = "close") {
                Spacer(Modifier.height(8.dp))
                TextButton(onClick = onClose, modifier = Modifier.fillMaxWidth()) {
                    Text("Zurück zu Harmony", color = HarmonyPink, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun WholePackRevealStatusCard(
    session: AppSession,
    myCompleted: Boolean,
    partnerCompleted: Boolean,
    revealed: Boolean,
    onReveal: () -> Unit
) {
    val partner = session.partner ?: return
    val ready = PartnerPackRevealPolicy.canRevealPartnerAnswers(myCompleted, partnerCompleted)
    val shape = RoundedCornerShape(24.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.linearGradient(
                    listOf(
                        HarmonyPink.copy(alpha = 0.18f),
                        HarmonyPurple.copy(alpha = 0.17f),
                        HarmonySurface2.copy(alpha = 0.96f)
                    )
                )
            )
            .border(1.dp, HarmonyPink.copy(alpha = 0.34f), shape)
            .padding(17.dp)
            .testTag("whole_pack_reveal_status")
    ) {
        when {
            ready && !revealed -> {
                Text(
                    "Ihr seid beide fertig 💞",
                    color = HarmonyText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Alle Antworten sind bereit. Enthüllt jetzt das komplette Spiel gemeinsam.",
                    color = HarmonyMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
                Spacer(Modifier.height(14.dp))
                Button(
                    onClick = onReveal,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("whole_pack_reveal_button"),
                    shape = RoundedCornerShape(17.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
                ) {
                    Text("Antworten enthüllen", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
            ready && revealed -> {
                Text(
                    "Eure Antworten sind enthüllt ✨",
                    color = HarmonyText,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(5.dp))
                Text(
                    "Vergleicht jetzt Frage für Frage eure Auswahl.",
                    color = HarmonyMuted,
                    fontSize = 13.sp
                )
            }
            myCompleted -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoupleRemoteAvatar(session.profile, 42)
                    Column(Modifier.padding(start = 11.dp).weight(1f)) {
                        Text(
                            "Dein Durchlauf ist gespeichert ✓",
                            color = HarmonyText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "Warte auf ${partner.displayName}. Bis dahin bleiben alle Partnerantworten verborgen.",
                            color = HarmonyMuted,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
            else -> {
                Text(
                    "Dein Durchlauf wird abgeschlossen …",
                    color = HarmonyMuted,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun WholePackQuestionRevealCard(
    questionNumber: Int,
    questionText: String,
    session: AppSession,
    myAnswer: String,
    partnerAnswer: String?,
    readyToReveal: Boolean,
    revealed: Boolean
) {
    val partner = session.partner ?: return
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HarmonySurface2.copy(alpha = 0.92f))
            .border(1.dp, HarmonyLine, shape)
            .padding(16.dp)
            .testTag("whole_pack_question_$questionNumber")
    ) {
        Text("Frage $questionNumber", color = HarmonyPink, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(5.dp))
        Text(questionText, color = HarmonyText, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, lineHeight = 21.sp)
        Spacer(Modifier.height(14.dp))

        CoupleAnswerRow(session.profile, myAnswer, HarmonyPink)
        Spacer(Modifier.height(9.dp))

        if (readyToReveal && revealed && !partnerAnswer.isNullOrBlank()) {
            CoupleAnswerRow(partner, partnerAnswer, HarmonyPurple)
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(HarmonyPurple.copy(alpha = 0.08f))
                    .border(1.dp, HarmonyPurple.copy(alpha = 0.20f), RoundedCornerShape(16.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoupleRemoteAvatar(partner, 42)
                Column(Modifier.padding(start = 10.dp).weight(1f)) {
                    Text(partner.displayName, color = HarmonyMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(2.dp))
                    Text(
                        if (readyToReveal) "Bereit zum gemeinsamen Enthüllen 🔒" else "Antwort bleibt noch verborgen 🔒",
                        color = HarmonyMuted,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
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
    questionNumber: Int,
    questionText: String,
    session: AppSession,
    myAnswer: String,
    result: CouplePackQuestionResult?,
    revealed: Boolean,
    onReveal: () -> Unit
) {
    val partner = session.partner ?: return
    val state = result?.revealState ?: CoupleRevealState.WAITING_FOR_PARTNER
    val shape = RoundedCornerShape(22.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(HarmonySurface2.copy(alpha = 0.92f))
            .border(1.dp, HarmonyLine, shape)
            .padding(16.dp)
    ) {
        Text("Frage $questionNumber", color = HarmonyPink, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(5.dp))
        Text(questionText, color = HarmonyText, fontSize = 15.5.sp, fontWeight = FontWeight.Bold, lineHeight = 21.sp)
        Spacer(Modifier.height(14.dp))

        when {
            state == CoupleRevealState.READY && !revealed -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoupleRemoteAvatar(partner, 38)
                    Column(Modifier.padding(start = 10.dp).weight(1f)) {
                        Text("${partner.displayName} hat geantwortet ✓", color = HarmonyText, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                        Text("Beide Antworten sind jetzt bereit.", color = HarmonyMuted, fontSize = 11.5.sp)
                    }
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = onReveal,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
                ) {
                    Text("Ergebnis ansehen", color = Color.White, fontWeight = FontWeight.ExtraBold)
                }
            }
            state == CoupleRevealState.READY && revealed -> {
                CoupleAnswerRow(session.profile, result?.myAnswerText ?: myAnswer, HarmonyPink)
                Spacer(Modifier.height(9.dp))
                CoupleAnswerRow(partner, result?.partnerAnswerText.orEmpty(), HarmonyPurple)
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
private fun CoupleAnswerRow(profile: UserProfile, answer: String, accent: Color) {
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
        Column(Modifier.padding(start = 10.dp).weight(1f)) {
            Text(profile.displayName, color = HarmonyMuted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(2.dp))
            Text(answer, color = HarmonyText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, lineHeight = 19.sp)
        }
    }
}

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
