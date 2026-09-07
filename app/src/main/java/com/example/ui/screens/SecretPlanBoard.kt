package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnswerEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SecretPlanAnswerCodec
import com.example.data.model.SecretPlanCatalog
import com.example.data.model.SecretPlanChoice
import com.example.data.model.SecretPlanPair
import com.example.data.model.SecretPlanProgress
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2

private enum class SecretPlanPhase { PERSON_A, HANDOFF, PERSON_B, SEALED, FINAL_PLAN }

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
    var customForFirst by rememberSaveable(questionIndex, phase) { mutableStateOf(true) }
    var showingCustom by rememberSaveable(questionIndex, phase) { mutableStateOf(false) }
    var submitted by rememberSaveable(questionIndex, selectedAnswer) { mutableStateOf(restored != null) }

    val pair = first?.let { a -> second?.let { b -> SecretPlanPair(a, b) } }
    val finalLines = pair?.let { SecretPlanProgress.lines(historicalAnswers, questionIndex, it) }

    FullscreenMechanicShell(
        kicker = "🗺️ DER GEHEIME PLAN",
        question = question,
        instruction = when (phase) {
            SecretPlanPhase.PERSON_A -> "Wähle deine Idee – sie bleibt verdeckt."
            SecretPlanPhase.HANDOFF -> "Deine Idee ist sicher verschlossen."
            SecretPlanPhase.PERSON_B -> "Jetzt wählt ${profile.partnerName} ebenfalls geheim."
            SecretPlanPhase.SEALED -> "Beide Ideen sind versiegelt."
            SecretPlanPhase.FINAL_PLAN -> "Eure Ideen ergeben zusammen einen Plan."
        },
        modifier = modifier.testTag("secret_plan_board")
    ) {
        Box(Modifier.fillMaxSize().background(HarmonySurface2).testTag("secret_plan_background")) {
            AnimatedContent(phase, label = "secret-plan-phase") { current ->
                when (current) {
                    SecretPlanPhase.PERSON_A, SecretPlanPhase.PERSON_B -> {
                        val selectFirst = current == SecretPlanPhase.PERSON_A
                        if (showingCustom) {
                            CustomPlanChoice(
                                value = customText,
                                onValueChange = { customText = it.take(80) },
                                onSave = {
                                    val choice = SecretPlanChoice.Custom(customText.trim().replace(Regex("\\s+"), " "))
                                    if (selectFirst) {
                                        first = choice
                                        phase = SecretPlanPhase.HANDOFF
                                    } else {
                                        second = choice
                                        phase = SecretPlanPhase.SEALED
                                    }
                                    showingCustom = false
                                }
                            )
                        } else {
                            Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                LargeOptionGrid(
                                    items = mechanicOptions(options.filterNot { it == SecretPlanCatalog.CUSTOM_OPTION }, profile),
                                    selectedRaw = null,
                                    onSelect = { item ->
                                        if (selectFirst) {
                                            first = SecretPlanChoice.Preset(item.raw)
                                            phase = SecretPlanPhase.HANDOFF
                                        } else {
                                            second = SecretPlanChoice.Preset(item.raw)
                                            phase = SecretPlanPhase.SEALED
                                        }
                                    },
                                    modifier = Modifier.weight(1f),
                                    tagPrefix = if (selectFirst) "secret_plan_first_option" else "secret_plan_second_option"
                                )
                                PrimaryMechanicButton(
                                    text = SecretPlanCatalog.CUSTOM_OPTION,
                                    onClick = { customForFirst = selectFirst; showingCustom = true },
                                    testTag = "secret_plan_custom_option"
                                )
                            }
                        }
                    }
                    SecretPlanPhase.HANDOFF -> PairPrivateHandoffPane(
                        name = profile.partnerName,
                        title = "Übergib das Handy an ${profile.partnerName}",
                        body = "Deine Idee bleibt verdeckt.",
                        buttonTag = "secret_plan_handoff",
                        onReady = { phase = SecretPlanPhase.PERSON_B }
                    )
                    SecretPlanPhase.SEALED -> Column(
                        Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("✦ Beide Ideen sind versiegelt", color = HarmonyPink, fontWeight = FontWeight.Bold, fontSize = 21.sp)
                        Spacer(Modifier.height(18.dp))
                        PrimaryMechanicButton(
                            text = if (questionIndex == 2) "Gemeinsamen Plan öffnen" else "Nächstes Kapitel",
                            onClick = {
                                if (questionIndex == 2 && finalLines != null) phase = SecretPlanPhase.FINAL_PLAN
                                else if (pair != null && !submitted) { submitted = true; onPick(SecretPlanAnswerCodec.encode(pair.first, pair.second)) }
                            },
                            testTag = "secret_plan_chapter_continue"
                        )
                    }
                    SecretPlanPhase.FINAL_PLAN -> FinalSecretPlan(
                        lines = finalLines.orEmpty(),
                        onSave = {
                            if (pair != null && !submitted) { submitted = true; onPick(SecretPlanAnswerCodec.encode(pair.first, pair.second)) }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CustomPlanChoice(value: String, onValueChange: (String) -> Unit, onSave: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        Text("Deine eigene Idee", color = HarmonyPink, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(value, onValueChange, modifier = Modifier.fillMaxWidth().testTag("secret_plan_custom_input"), label = { Text("Zum Beispiel: Lissabon") })
        Spacer(Modifier.height(16.dp))
        PrimaryMechanicButton("Idee versiegeln", enabled = value.trim().isNotEmpty(), onClick = onSave, testTag = "secret_plan_custom_save")
    }
}

@Composable
private fun FinalSecretPlan(lines: List<com.example.data.model.SecretPlanPlanLine>, onSave: () -> Unit) {
    Column(Modifier.fillMaxSize().padding(20.dp).testTag("secret_plan_final_card"), horizontalAlignment = Alignment.CenterHorizontally) {
        Text("Euer geheimer Plan", color = HarmonyPink, fontSize = 26.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(18.dp))
        Text("Jetzt planen", color = HarmonyPurple, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        lines.filter { it.isShared }.forEach { Text("✦ ${it.first.text}", color = HarmonyPink, textAlign = TextAlign.Center) }
        Spacer(Modifier.height(18.dp))
        Text("Auch schön", color = HarmonyPurple, fontWeight = FontWeight.Bold, fontSize = 19.sp)
        lines.filterNot { it.isShared }.forEach { Text("${it.first.text}  ·  ${it.second.text}", color = HarmonyPink, textAlign = TextAlign.Center) }
        Spacer(Modifier.weight(1f))
        PrimaryMechanicButton("Plan speichern", onClick = onSave, testTag = "secret_plan_save")
    }
}
