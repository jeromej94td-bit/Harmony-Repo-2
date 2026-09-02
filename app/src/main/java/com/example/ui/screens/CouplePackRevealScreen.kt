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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.couple.CouplePackQuestionResult
import com.example.data.couple.CoupleQuestionRepository
import com.example.data.couple.CoupleRevealState
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
    var results by remember(pack.id, session.coupleId) { mutableStateOf<List<CouplePackQuestionResult>>(emptyList()) }
    var revealedIndexes by remember(pack.id, session.coupleId) { mutableStateOf<Set<Int>>(emptySet()) }
    var isLoading by remember(pack.id, session.coupleId) { mutableStateOf(true) }
    var errorMessage by remember(pack.id, session.coupleId) { mutableStateOf<String?>(null) }

    LaunchedEffect(pack.id, session.coupleId, answers) {
        if (!session.isPaired) return@LaunchedEffect

        answers.forEach { (questionIndex, answerText) ->
            if (answerText.isNotBlank()) {
                runCatching {
                    repository.submitAnswer(pack.id, questionIndex, answerText)
                }
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

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF17071F), HarmonyBg, Color(0xFF07020A))
                )
            )
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                start = 18.dp,
                end = 18.dp,
                top = 34.dp,
                bottom = 34.dp
            ),
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
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 34.dp),
                        contentAlignment = Alignment.Center
                    ) {
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
                val questionText = questionLabel(pack, questionIndex)
                CoupleQuestionRevealCard(
                    questionNumber = questionIndex + 1,
                    questionText = questionText,
                    session = session,
                    myAnswer = answers[questionIndex].orEmpty(),
                    result = result,
                    revealed = questionIndex in revealedIndexes,
                    onReveal = { revealedIndexes = revealedIndexes + questionIndex }
                )
            }

            item(key = "close") {
                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Zurück zu Harmony", color = HarmonyPink, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CoupleHeaderProfiles(session: AppSession) {
    val partner = session.partner ?: return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
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
        Text(
            text = "Frage $questionNumber",
            color = HarmonyPink,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(5.dp))
        Text(
            text = questionText,
            color = HarmonyText,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 21.sp
        )
        Spacer(Modifier.height(14.dp))

        when {
            state == CoupleRevealState.READY && !revealed -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoupleRemoteAvatar(partner, 38)
                    Column(Modifier.padding(start = 10.dp).weight(1f)) {
                        Text(
                            text = "${partner.displayName} hat geantwortet ✓",
                            color = HarmonyText,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Beide Antworten sind jetzt bereit.",
                            color = HarmonyMuted,
                            fontSize = 11.5.sp
                        )
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
                CoupleAnswerRow(
                    profile = session.profile,
                    answer = result?.myAnswerText ?: myAnswer,
                    accent = HarmonyPink
                )
                Spacer(Modifier.height(9.dp))
                CoupleAnswerRow(
                    profile = partner,
                    answer = result?.partnerAnswerText.orEmpty(),
                    accent = HarmonyPurple
                )
            }

            state == CoupleRevealState.NEEDS_OWN_ANSWER -> {
                Text(
                    text = "Beantworte diese Frage zuerst selbst, bevor eine Partnerantwort sichtbar werden kann.",
                    color = HarmonyMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }

            else -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CoupleRemoteAvatar(session.profile, 36)
                    Column(Modifier.padding(start = 9.dp).weight(1f)) {
                        Text(
                            text = "Deine Antwort ist gespeichert ✓",
                            color = HarmonyText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Warte auf ${partner.displayName}. Die Antwort bleibt bis dahin verborgen.",
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
private fun CoupleAnswerRow(
    profile: UserProfile,
    answer: String,
    accent: Color
) {
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
            Text(
                text = profile.displayName,
                color = HarmonyMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = answer,
                color = HarmonyText,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 19.sp
            )
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
