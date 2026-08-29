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
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PredictionAnswerCodec
import com.example.data.model.ProfileEntity
import com.example.data.model.ProposalEitherOrRounds
import com.example.data.model.ProposalExperienceDefinitions
import com.example.data.model.ProposalExperienceRunnerPolicy
import com.example.data.model.ProposalFlowStepKind
import com.example.data.model.ProposalLocationDuels
import com.example.data.model.ProposalOpenPrompts
import com.example.data.model.ProposalPartnerPrediction
import com.example.data.model.ProposalPriorityRanking
import com.example.data.model.ProposalReveal
import com.example.data.model.ProposalRevealInput
import com.example.data.model.ProposalRevealResult
import com.example.data.model.ProposalRingImageDuels
import com.example.data.model.ProposalRunnerPosition
import com.example.data.model.ProposalScenarios
import com.example.data.model.RankingAnswerCodec
import com.example.data.model.toExperienceEitherOrRound
import com.example.data.model.toExperienceImageDuelRound
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyText

@Composable
internal fun ProposalExperienceScreen(
    profile: ProfileEntity,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    var started by remember { mutableStateOf(false) }
    var position by remember { mutableStateOf(ProposalRunnerPosition(0, 0)) }
    var eitherOrSelections by remember { mutableStateOf(emptyMap<String, String>()) }
    var locationSelections by remember { mutableStateOf(emptyMap<String, String>()) }
    var ringSelections by remember { mutableStateOf(emptyMap<String, String>()) }
    var rankedPriorityIds by remember { mutableStateOf(emptyList<String>()) }
    var rankingEncoded by remember { mutableStateOf<String?>(null) }
    var predictionAnswers by remember { mutableStateOf(emptyMap<String, String>()) }
    var scenarioSelections by remember { mutableStateOf(emptyMap<String, String>()) }
    var personalWishAnswers by remember { mutableStateOf(emptyMap<String, String>()) }

    fun advance() {
        ProposalExperienceRunnerPolicy.next(position)?.let { position = it }
    }

    val step = ProposalExperienceRunnerPolicy.steps.getOrNull(position.stepIndex)
        ?: ProposalExperienceDefinitions.perfectProposal.steps.last()
    val progress = ProposalExperienceRunnerPolicy.progress(position)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(
                        HarmonyPurple.copy(alpha = 0.52f),
                        HarmonyBg.copy(alpha = 0.98f),
                        Color.Black
                    )
                )
            )
            .testTag("proposal_experience_screen")
    ) {
        if (!started) {
            ProposalIntroPane(
                onStart = { started = true },
                onClose = onClose,
                modifier = Modifier.fillMaxSize()
            )
            return@Box
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UNSER PERFEKTER ANTRAG",
                        color = HarmonyPinkSoft,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "Schritt ${position.stepIndex + 1} von ${ProposalExperienceRunnerPolicy.steps.size}",
                        color = HarmonyMuted,
                        fontSize = 13.sp
                    )
                }
                TextButton(onClick = onClose) {
                    Text("Schließen", color = HarmonyText)
                }
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(99.dp)),
                color = HarmonyPink,
                trackColor = HarmonyPurple.copy(alpha = 0.35f)
            )
            Spacer(Modifier.height(14.dp))

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when (step.kind) {
                    ProposalFlowStepKind.EITHER_OR -> {
                        val round = ProposalEitherOrRounds.roundsFor(step.id)[position.itemIndex]
                        ExperienceEitherOrBoard(
                            round = round.toExperienceEitherOrRound(),
                            selectedChoice = eitherOrSelections[round.id],
                            onPick = { choice ->
                                eitherOrSelections = eitherOrSelections + (round.id to choice)
                                advance()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    ProposalFlowStepKind.IMAGE_DUEL -> {
                        if (step.id == "proposal_location") {
                            val round = ProposalLocationDuels.rounds[position.itemIndex]
                            AnimatedProposalLocationDuelBoard(
                                round = round,
                                selectedOptionId = locationSelections[round.id],
                                onPick = { option ->
                                    locationSelections = locationSelections + (round.id to option.id)
                                },
                                onTransitionFinished = { advance() },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            val round = ProposalRingImageDuels.rounds[position.itemIndex]
                            val context = LocalContext.current
                            ExperienceProductImageDuelBoard(
                                round = round.toExperienceImageDuelRound(),
                                selectedOptionId = ringSelections[round.id],
                                imageResolver = { imageKey ->
                                    context.resources.getIdentifier(imageKey, "drawable", context.packageName)
                                },
                                onPick = { option ->
                                    ringSelections = ringSelections + (round.id to option.id)
                                    advance()
                                },
                                kicker = "💎  RING-DUELL",
                                testTagPrefix = "proposal_ring",
                                rootTestTag = "proposal_ring_duel",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

                    ProposalFlowStepKind.RANKING -> {
                        val labels = ProposalPriorityRanking.priorities.map { it.label }
                        RankingSlotBoard(
                            question = "Was muss für euren Antrag am meisten stimmen?",
                            options = labels,
                            selectedAnswer = rankingEncoded,
                            profile = profile,
                            onPick = { encoded ->
                                rankingEncoded = encoded
                                val order = RankingAnswerCodec.decode(encoded, labels).orEmpty()
                                val idByLabel = ProposalPriorityRanking.priorities.associate { it.label to it.id }
                                rankedPriorityIds = order.mapNotNull(idByLabel::get)
                                advance()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    ProposalFlowStepKind.PARTNER_PREDICTION -> {
                        val round = ProposalPartnerPrediction.rounds[position.itemIndex]
                        PartnerPredictionBoard(
                            question = round.prompt,
                            options = round.options,
                            selectedAnswer = predictionAnswers[round.id],
                            profile = profile,
                            onPick = { encoded ->
                                predictionAnswers = predictionAnswers + (round.id to encoded)
                                advance()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    ProposalFlowStepKind.SCENARIO -> {
                        val round = ProposalScenarios.rounds[position.itemIndex]
                        ScenarioBoard(
                            question = round.prompt,
                            options = round.options,
                            selectedAnswer = scenarioSelections[round.id],
                            profile = profile,
                            onPick = { answer ->
                                scenarioSelections = scenarioSelections + (round.id to answer)
                                advance()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    ProposalFlowStepKind.OPEN_PROMPT -> {
                        val prompt = ProposalOpenPrompts.prompts[position.itemIndex]
                        ProposalOpenPromptPane(
                            prompt = prompt.prompt,
                            initialValue = personalWishAnswers[prompt.id].orEmpty(),
                            onContinue = { answer ->
                                personalWishAnswers = personalWishAnswers + (prompt.id to answer)
                                advance()
                            },
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    ProposalFlowStepKind.REVEAL -> {
                        val predictionMatches = predictionAnswers.values
                            .mapNotNull(PredictionAnswerCodec::decode)
                            .count { it.isHit }
                        val reveal = ProposalReveal.build(
                            ProposalRevealInput(
                                eitherOrSelections = eitherOrSelections,
                                locationSelections = locationSelections,
                                ringSelections = ringSelections,
                                rankedPriorityIds = rankedPriorityIds,
                                predictionMatches = predictionMatches,
                                predictionTotal = ProposalPartnerPrediction.rounds.size,
                                scenarioSelections = scenarioSelections,
                                personalWishAnswers = personalWishAnswers
                            )
                        )
                        ProposalRevealPane(
                            reveal = reveal,
                            onClose = onClose,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProposalIntroPane(
    onStart: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        TextButton(onClick = onClose, modifier = Modifier.align(Alignment.End)) {
            Text("Schließen", color = HarmonyMuted)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("💍", fontSize = 76.sp)
            Spacer(Modifier.height(18.dp))
            Text(
                text = "Unser perfekter Antrag",
                color = Color.White,
                fontSize = 34.sp,
                lineHeight = 39.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "Von der Stimmung über Ort und Ring bis zu euren eigenen Worten. Am Ende entsteht kein Punktestand, sondern euer gemeinsames Bild von diesem besonderen Moment.",
                color = HarmonyMuted,
                fontSize = 16.sp,
                lineHeight = 23.sp,
                textAlign = TextAlign.Center
            )
        }
        Button(
            onClick = onStart,
            modifier = Modifier.fillMaxWidth().height(58.dp).testTag("proposal_start"),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
        ) {
            Text("Reise beginnen", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ProposalOpenPromptPane(
    prompt: String,
    initialValue: String,
    onContinue: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember(prompt) { mutableStateOf(initialValue) }
    Column(
        modifier = modifier.padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("✍️  EURE EIGENEN WORTE", color = HarmonyPinkSoft, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(Modifier.height(12.dp))
        Text(
            prompt,
            color = Color.White,
            fontSize = 23.sp,
            lineHeight = 30.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(22.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier.fillMaxWidth().height(170.dp).testTag("proposal_open_answer"),
            placeholder = { Text("Schreib aus dem Gefühl heraus …", color = HarmonyMuted) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                focusedBorderColor = HarmonyPink,
                unfocusedBorderColor = Color.White.copy(alpha = 0.20f),
                cursorColor = HarmonyPink
            ),
            shape = RoundedCornerShape(22.dp)
        )
        Spacer(Modifier.height(18.dp))
        Button(
            onClick = { onContinue(text.trim()) },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
        ) {
            Text("Weiter", fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ProposalRevealPane(
    reveal: ProposalRevealResult,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(bottom = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("✨", fontSize = 58.sp)
        Spacer(Modifier.height(8.dp))
        Text(
            reveal.title,
            color = Color.White,
            fontSize = 30.sp,
            lineHeight = 36.sp,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(10.dp))
        Text(
            reveal.subtitle,
            color = HarmonyMuted,
            fontSize = 16.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(20.dp))
        reveal.sections.forEach { section ->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .background(HarmonyPurple.copy(alpha = 0.22f))
                    .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(22.dp))
                    .padding(16.dp)
            ) {
                Text(section.title, color = HarmonyPinkSoft, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(7.dp))
                section.values.forEach { value ->
                    Text("• $value", color = HarmonyText, fontSize = 15.sp, lineHeight = 21.sp)
                }
            }
        }
        Spacer(Modifier.height(14.dp))
        Text(
            reveal.closing,
            color = Color.White,
            fontSize = 18.sp,
            lineHeight = 25.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Spacer(Modifier.height(22.dp))
        Button(
            onClick = onClose,
            modifier = Modifier.fillMaxWidth().height(58.dp).testTag("proposal_finish"),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
        ) {
            Text("Zurück zu Harmony", fontWeight = FontWeight.ExtraBold)
        }
    }
}
