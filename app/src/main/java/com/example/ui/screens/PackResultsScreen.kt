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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.couple.CoupleQuestionRepository
import com.example.data.model.AnswerEntity
import com.example.data.model.CoupleChoice
import com.example.data.model.EitherOrAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.data.model.QuestionPack
import com.example.ui.contentText
import com.example.ui.session.AppSessionViewModel
import com.example.ui.session.SessionPhase
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.ui.theme.topicAccentColor
import com.example.util.LanguageManager

data class PackResultRow(
    val questionIndex: Int,
    val prompt: String,
    val answerText: String?,
    val coupleChoice: CoupleChoice?
)

fun hasCompletePackResults(pack: QuestionPack, answers: List<AnswerEntity>): Boolean {
    val total = if (pack.type == "tot") pack.pairs.size else pack.questions.size
    if (total <= 0) return false
    val answeredIndexes = answers.asSequence()
        .filter { it.packId == pack.id && it.questionIndex in 0 until total }
        .map { it.questionIndex }
        .toSet()
    return answeredIndexes.size >= total
}

fun buildPackResultRows(pack: QuestionPack, answers: List<AnswerEntity>): List<PackResultRow> {
    val answerByIndex = answers
        .asSequence()
        .filter { it.packId == pack.id }
        .associateBy { it.questionIndex }

    val total = if (pack.type == "tot") pack.pairs.size else pack.questions.size
    return (0 until total).map { index ->
        val prompt = if (pack.type == "tot") {
            pack.pairs.getOrNull(index)?.let { (left, right) -> "$left  ↔  $right" }
                ?: "Frage ${index + 1}"
        } else {
            pack.questions.getOrNull(index)?.q ?: "Frage ${index + 1}"
        }
        val answerText = answerByIndex[index]?.answerText
        PackResultRow(
            questionIndex = index,
            prompt = prompt,
            answerText = answerText,
            coupleChoice = answerText?.let(EitherOrAnswerCodec::decode)
        )
    }
}

@Composable
fun PackResultsScreen(
    pack: QuestionPack,
    answers: List<AnswerEntity>,
    profile: ProfileEntity,
    appLanguage: String,
    onReplay: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sessionViewModel: AppSessionViewModel = viewModel()
    val sessionState by sessionViewModel.uiState.collectAsStateWithLifecycle()
    val session = sessionState.session

    if (sessionState.phase == SessionPhase.READY && session?.isPaired == true) {
        val repository = remember { CoupleQuestionRepository() }
        CouplePackRevealScreen(
            pack = pack,
            session = session,
            answers = answers.associate { it.questionIndex to it.answerText },
            repository = repository,
            onClose = onClose,
            modifier = modifier
        )
        return
    }

    val displayPack = remember(pack, appLanguage) { LanguageManager.translatePack(pack, appLanguage) }
    val rows = remember(displayPack, answers) { buildPackResultRows(displayPack, answers) }
    val answeredCount = rows.count { !it.answerText.isNullOrBlank() }
    val accent = topicAccentColor(pack.topic)

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("pack_results_screen"),
        color = HarmonyBg
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            accent.copy(alpha = 0.22f),
                            HarmonyBg,
                            HarmonyBg
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.08f))
                            .testTag("close_results_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = LanguageManager.tr("Zurück", appLanguage),
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.size(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = LanguageManager.tr("Ergebnisse", appLanguage),
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = displayPack.title,
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(accent.copy(alpha = 0.22f))
                            .border(1.dp, accent.copy(alpha = 0.55f), CircleShape)
                            .padding(horizontal = 11.dp, vertical = 7.dp)
                    ) {
                        Text(
                            text = "$answeredCount/${rows.size}",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 18.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, bottom = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = displayPack.emoji.ifBlank { "💞" },
                                fontSize = 42.sp
                            )
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = LanguageManager.tr("Eure gespeicherten Antworten", appLanguage),
                                color = HarmonyText,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = LanguageManager.tr(
                                    "Übersprungene Fragen werden nicht als Antwort gespeichert und hier entsprechend markiert.",
                                    appLanguage
                                ),
                                color = HarmonyMuted,
                                fontSize = 11.5.sp,
                                lineHeight = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    items(rows, key = { it.questionIndex }) { row ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(
                                    Brush.linearGradient(
                                        listOf(
                                            HarmonySurface2,
                                            HarmonySurface,
                                            accent.copy(alpha = 0.10f)
                                        )
                                    )
                                )
                                .border(1.dp, HarmonyLine, RoundedCornerShape(18.dp))
                                .padding(14.dp)
                                .testTag("result_row_${row.questionIndex}")
                        ) {
                            Text(
                                text = "${row.questionIndex + 1}. ${contentText(row.prompt)}",
                                color = HarmonyText,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                lineHeight = 19.sp
                            )
                            Spacer(Modifier.height(8.dp))

                            val coupleChoice = row.coupleChoice
                            if (coupleChoice != null) {
                                ResultAnswerLine(
                                    label = profile.userName,
                                    answer = contentText(coupleChoice.userChoice),
                                    accent = HarmonyPink
                                )
                                Spacer(Modifier.height(6.dp))
                                ResultAnswerLine(
                                    label = profile.partnerName,
                                    answer = contentText(coupleChoice.partnerChoice),
                                    accent = HarmonyPurple
                                )
                            } else if (!row.answerText.isNullOrBlank()) {
                                Text(
                                    text = contentText(row.answerText),
                                    color = HarmonyPinkSoft,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    lineHeight = 18.sp
                                )
                            } else {
                                Text(
                                    text = LanguageManager.tr("Übersprungen / nicht beantwortet", appLanguage),
                                    color = HarmonyMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    item {
                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = onReplay,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .testTag("replay_pack_button"),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = accent)
                        ) {
                            Text(
                                text = LanguageManager.tr("Nochmal spielen", appLanguage),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        TextButton(
                            onClick = onClose,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("results_back_button")
                        ) {
                            Text(
                                text = LanguageManager.tr("Zurück zu den Spielen", appLanguage),
                                color = HarmonyMuted
                            )
                        }
                        Spacer(Modifier.height(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ResultAnswerLine(
    label: String,
    answer: String,
    accent: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(accent.copy(alpha = 0.10f))
            .border(1.dp, accent.copy(alpha = 0.20f), RoundedCornerShape(12.dp))
            .padding(horizontal = 11.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(accent),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label.take(1).uppercase(),
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.size(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = HarmonyMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = answer,
                color = HarmonyText,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}