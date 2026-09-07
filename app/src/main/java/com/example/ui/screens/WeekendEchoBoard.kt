package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnswerEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.WeekendEchoAnswerCodec
import com.example.data.model.WeekendEchoSelector
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurpleLight

@Composable
internal fun WeekendEchoBoard(
    question: String,
    options: List<String>,
    questionIndex: Int,
    historicalAnswers: List<AnswerEntity>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val duel = remember(historicalAnswers, questionIndex) {
        WeekendEchoSelector.select(historicalAnswers, questionIndex)
    }
    if (duel == null) {
        ScenarioBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )
        return
    }

    val restored = remember(selectedAnswer) {
        selectedAnswer?.let(WeekendEchoAnswerCodec::decode)
    }
    var phaseName by rememberSaveable(questionIndex, selectedAnswer) {
        mutableStateOf(
            if (restored == null) WeekendEchoPhase.PERSON_A.name else WeekendEchoPhase.REVEALED.name
        )
    }
    var firstMotifKey by rememberSaveable(questionIndex, selectedAnswer) {
        mutableStateOf(restored?.firstMotifKey)
    }
    var secondMotifKey by rememberSaveable(questionIndex, selectedAnswer) {
        mutableStateOf(restored?.secondMotifKey)
    }
    var submitted by rememberSaveable(questionIndex, selectedAnswer) {
        mutableStateOf(restored != null)
    }

    val state = WeekendEchoRoundState(
        phase = WeekendEchoPhase.valueOf(phaseName),
        firstMotifKey = firstMotifKey,
        secondMotifKey = secondMotifKey
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0820), Color(0xFF1B0D35), Color(0xFF090716))
                )
            )
            .padding(horizontal = 18.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "UNSER KOMPASS  ·  KAPITEL ${questionIndex + 1}",
            color = HarmonyGold,
            fontSize = 12.sp,
            letterSpacing = 1.6.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = question,
            color = Color.White,
            fontSize = 24.sp,
            lineHeight = 29.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(10.dp))
        AnimatedContent(
            targetState = state.phase,
            transitionSpec = {
                androidx.compose.animation.fadeIn(tween(240)) togetherWith
                    androidx.compose.animation.fadeOut(tween(180))
            },
            label = "weekend_echo_phase"
        ) { phase ->
            val status = when (phase) {
                WeekendEchoPhase.PERSON_A -> "${profile.userName} wählt"
                WeekendEchoPhase.HANDOFF -> "Wahl sicher verborgen"
                WeekendEchoPhase.PERSON_B -> "${profile.partnerName} wählt"
                WeekendEchoPhase.READY_TO_REVEAL -> "Euer Moment ist bereit"
                WeekendEchoPhase.REVEALED -> "Euer Kompass"
            }
            Text(
                text = status,
                color = Color.White.copy(alpha = 0.74f),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(Modifier.height(10.dp))

        WeekendSplitScene(
            duel = duel,
            state = state,
            onSealPick = { candidate ->
                val next = state.pick(candidate.motif.key)
                phaseName = next.phase.name
                firstMotifKey = next.firstMotifKey
                secondMotifKey = next.secondMotifKey
            },
            onContinue = {
                val first = firstMotifKey
                val second = secondMotifKey
                if (state.canContinue && !submitted && first != null && second != null) {
                    submitted = true
                    onPick(WeekendEchoAnswerCodec.encode(first, second))
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        when (state.phase) {
            WeekendEchoPhase.HANDOFF -> {
                Spacer(Modifier.height(12.dp))
                EchoAction(
                    text = "Handy an ${profile.partnerName} übergeben",
                    tag = "weekend_echo_handoff",
                    onClick = { phaseName = state.handoffReady().phase.name }
                )
            }

            WeekendEchoPhase.READY_TO_REVEAL -> {
                Spacer(Modifier.height(12.dp))
                EchoAction(
                    text = "Siegel gemeinsam öffnen",
                    tag = "weekend_echo_reveal",
                    onClick = { phaseName = state.reveal().phase.name }
                )
            }

            else -> Unit
        }
    }
}

@Composable
private fun EchoAction(text: String, tag: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(22.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(HarmonyPurpleLight.copy(alpha = 0.72f), HarmonyPink.copy(alpha = 0.78f))
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.28f), RoundedCornerShape(22.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .testTag(tag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )
    }
}
