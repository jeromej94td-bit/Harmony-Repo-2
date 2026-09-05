package com.example.ui.screens

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.BuildConfig
import com.example.data.developer.DeveloperFeedbackDraft
import com.example.data.developer.DeveloperFeedbackPriority
import com.example.data.developer.DeveloperFeedbackType
import com.example.data.developer.DeveloperReviewContext
import com.example.data.developer.ExecutionMode
import com.example.ui.HarmonyViewModel
import com.example.ui.developer.DeveloperReviewViewModel
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonyText

@Composable
fun DeveloperReviewQuickNote(
    modifier: Modifier = Modifier,
) {
    val reviewViewModel: DeveloperReviewViewModel = viewModel()
    val harmonyViewModel: HarmonyViewModel = viewModel()
    val reviewState by reviewViewModel.state.collectAsStateWithLifecycle()
    val harmonyState by harmonyViewModel.uiState.collectAsStateWithLifecycle()

    var dialogOpen by remember { mutableStateOf(false) }
    var note by remember { mutableStateOf("") }
    var inviteCode by remember { mutableStateOf("") }
    var feedbackType by remember { mutableStateOf(DeveloperFeedbackType.CHANGE) }
    var priority by remember { mutableStateOf(DeveloperFeedbackPriority.MEDIUM) }
    var executionMode by remember { mutableStateOf(ExecutionMode.REVIEW_FIRST) }

    val activeRun = harmonyState.activeRun
    val currentQuestion = activeRun
        ?.takeIf { it.pack.type != "tot" }
        ?.pack
        ?.questions
        ?.getOrNull(activeRun.currentIndex)

    val context = if (activeRun != null) {
        DeveloperReviewContext(
            screen = "QuizRunnerScreen",
            route = "games/runner",
            gameId = activeRun.pack.id,
            round = activeRun.currentIndex + 1,
            questionId = "${activeRun.pack.id}:${activeRun.currentIndex}",
            questionText = currentQuestion?.q,
        )
    } else {
        DeveloperReviewContext(
            screen = "GamesScreen",
            route = "games",
        )
    }

    TextButton(
        onClick = {
            dialogOpen = true
            reviewViewModel.clearMessage()
            reviewViewModel.checkAccess()
        },
        modifier = modifier,
    ) {
        Text("🛠 Notiz", color = HarmonyGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }

    if (!dialogOpen) return

    Dialog(onDismissRequest = { if (!reviewState.isBusy) dialogOpen = false }) {
        Surface(
            shape = RoundedCornerShape(22.dp),
            color = HarmonyBg,
            border = BorderStroke(1.dp, HarmonyPurpleLight),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp),
            ) {
                Text("🛠 Harmony Developer Review", color = HarmonyText, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(Modifier.height(4.dp))
                Text(contextSummary(context), color = HarmonyMuted, fontSize = 11.sp)
                Spacer(Modifier.height(14.dp))

                when {
                    !reviewState.accessChecked -> {
                        Text(
                            if (reviewState.isBusy) "Entwicklerzugang wird geprüft …" else "Entwicklerzugang prüfen …",
                            color = HarmonyMuted,
                        )
                        LaunchedEffect(Unit) { reviewViewModel.checkAccess() }
                    }

                    !reviewState.isAdmin -> {
                        Text("Einmalige Aktivierung", color = HarmonyPurpleLight, fontWeight = FontWeight.Bold)
                        Text(
                            "Der Review-Modus schreibt nur für freigeschaltete Entwickler in die private Supabase-Inbox.",
                            color = HarmonyMuted,
                            fontSize = 12.sp,
                        )
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(
                            value = inviteCode,
                            onValueChange = { inviteCode = it },
                            label = { Text("Developer-Code") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(Modifier.height(8.dp))
                        ActionPill(
                            label = if (reviewState.isBusy) "Aktiviere …" else "Aktivieren",
                            selected = true,
                            enabled = !reviewState.isBusy && inviteCode.isNotBlank(),
                            onClick = { reviewViewModel.enroll(inviteCode) },
                        )
                    }

                    else -> {
                        Text("Was soll hier geändert werden?", color = HarmonyText, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it.take(5000) },
                            minLines = 3,
                            maxLines = 7,
                            placeholder = { Text("z. B. Diese vier Karten müssen auf einen Blick sichtbar sein …") },
                            modifier = Modifier.fillMaxWidth(),
                        )

                        Spacer(Modifier.height(12.dp))
                        Text("Art", color = HarmonyMuted, fontSize = 11.sp)
                        ChoiceRow(
                            choices = listOf(
                                DeveloperFeedbackType.BUG to "🔴 Bug",
                                DeveloperFeedbackType.UI to "🟠 UI",
                                DeveloperFeedbackType.CHANGE to "🟡 Änderung",
                                DeveloperFeedbackType.IDEA to "🔵 Idee",
                            ),
                            selected = feedbackType,
                            onSelect = { feedbackType = it },
                        )

                        Spacer(Modifier.height(9.dp))
                        Text("Priorität", color = HarmonyMuted, fontSize = 11.sp)
                        ChoiceRow(
                            choices = listOf(
                                DeveloperFeedbackPriority.BLOCKER to "Blocker",
                                DeveloperFeedbackPriority.HIGH to "Hoch",
                                DeveloperFeedbackPriority.MEDIUM to "Normal",
                                DeveloperFeedbackPriority.LOW to "Niedrig",
                            ),
                            selected = priority,
                            onSelect = { priority = it },
                        )

                        Spacer(Modifier.height(9.dp))
                        Text("Ausführung", color = HarmonyMuted, fontSize = 11.sp)
                        ChoiceRow(
                            choices = listOf(
                                ExecutionMode.AUTO_SAFE to "Sicher automatisch",
                                ExecutionMode.REVIEW_FIRST to "Erst prüfen",
                                ExecutionMode.IDEA_ONLY to "Nur Idee",
                            ),
                            selected = executionMode,
                            onSelect = { executionMode = it },
                        )

                        Spacer(Modifier.height(14.dp))
                        ActionPill(
                            label = if (reviewState.isBusy) "Speichere …" else "In Developer Inbox speichern",
                            selected = true,
                            enabled = !reviewState.isBusy && note.isNotBlank(),
                            onClick = {
                                reviewViewModel.submit(
                                    draft = DeveloperFeedbackDraft(
                                        note = note,
                                        type = feedbackType,
                                        priority = priority,
                                        executionMode = executionMode,
                                    ),
                                    context = context,
                                    appVersion = BuildConfig.VERSION_NAME,
                                    buildNumber = BuildConfig.VERSION_CODE.toString(),
                                    gitCommit = "runtime-unavailable",
                                    device = mapOf(
                                        "manufacturer" to Build.MANUFACTURER,
                                        "model" to Build.MODEL,
                                        "sdk" to Build.VERSION.SDK_INT.toString(),
                                    ),
                                    onSaved = {
                                        note = ""
                                        dialogOpen = false
                                    },
                                )
                            },
                        )
                    }
                }

                reviewState.error?.let {
                    Spacer(Modifier.height(9.dp))
                    Text(it, color = HarmonyPink, fontSize = 12.sp)
                }
                reviewState.message?.let {
                    Spacer(Modifier.height(9.dp))
                    Text(it, color = HarmonyGold, fontSize = 12.sp)
                }

                Spacer(Modifier.height(8.dp))
                TextButton(
                    onClick = { if (!reviewState.isBusy) dialogOpen = false },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Schließen", color = HarmonyMuted)
                }
            }
        }
    }
}

private fun contextSummary(context: DeveloperReviewContext): String = buildList {
    context.gameId?.let { add("Spiel: $it") }
    context.round?.let { add("Runde: $it") }
    context.questionText?.let { add("Frage: ${it.take(110)}") }
    if (isEmpty()) add("Bereich: ${context.screen ?: "Harmony"}")
}.joinToString(" · ")

@Composable
private fun <T> ChoiceRow(
    choices: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        choices.forEach { (value, label) ->
            ActionPill(
                label = label,
                selected = value == selected,
                enabled = true,
                onClick = { onSelect(value) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ActionPill(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .clickable(enabled = enabled, onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        color = when {
            !enabled -> HarmonySurface.copy(alpha = 0.45f)
            selected -> HarmonyPurple.copy(alpha = 0.58f)
            else -> HarmonySurface
        },
        border = BorderStroke(
            1.dp,
            if (selected) HarmonyPurpleLight else HarmonyLine,
        ),
    ) {
        Text(
            label,
            color = if (enabled) Color.White else HarmonyMuted,
            fontSize = 10.5.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 8.dp),
        )
    }
}
