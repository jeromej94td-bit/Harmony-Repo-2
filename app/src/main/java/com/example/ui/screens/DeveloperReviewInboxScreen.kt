package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.developer.DeveloperFeedbackItem
import com.example.data.developer.DeveloperFeedbackPriority
import com.example.data.developer.DeveloperFeedbackStatus
import com.example.data.developer.DeveloperFeedbackType
import com.example.ui.developer.DeveloperReviewInboxFilter
import com.example.ui.developer.DeveloperReviewInboxPolicy
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
fun DeveloperReviewInboxScreen(
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reviewViewModel: DeveloperReviewViewModel = viewModel()
    val state by reviewViewModel.state.collectAsStateWithLifecycle()

    var query by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<DeveloperFeedbackStatus?>(DeveloperFeedbackStatus.NEW) }
    var selectedPriority by remember { mutableStateOf<DeveloperFeedbackPriority?>(null) }
    var selectedType by remember { mutableStateOf<DeveloperFeedbackType?>(null) }

    LaunchedEffect(Unit) {
        reviewViewModel.checkAccess()
    }
    LaunchedEffect(state.accessChecked, state.isAdmin) {
        if (state.accessChecked && state.isAdmin) {
            reviewViewModel.refreshInbox()
        }
    }

    val filter = DeveloperReviewInboxFilter(
        statuses = selectedStatus?.let(::setOf) ?: emptySet(),
        priorities = selectedPriority?.let(::setOf) ?: emptySet(),
        types = selectedType?.let(::setOf) ?: emptySet(),
        query = query,
    )
    val groups = remember(state.feedbackItems, filter) {
        DeveloperReviewInboxPolicy.apply(state.feedbackItems, filter)
    }
    val newCount = state.feedbackItems.count { it.status == DeveloperFeedbackStatus.NEW }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = HarmonyBg,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        "🛠 Developer Inbox",
                        color = HarmonyText,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                    )
                    Text(
                        "Offene Hinweise: $newCount",
                        color = HarmonyGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (state.isAdmin) {
                    TextButton(
                        enabled = !state.isRefreshingInbox,
                        onClick = { reviewViewModel.refreshInbox() },
                    ) {
                        Text("Aktualisieren", color = HarmonyPurpleLight)
                    }
                }
                TextButton(onClick = onClose) {
                    Text("Schließen", color = HarmonyMuted)
                }
            }

            Spacer(Modifier.height(10.dp))

            when {
                !state.accessChecked -> {
                    InboxCenteredMessage("Entwicklerzugang wird geprüft …", showSpinner = true)
                }

                !state.isAdmin -> {
                    InboxCenteredMessage("Developer Inbox ist nur für freigeschaltete Entwickler verfügbar.")
                }

                else -> {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it.take(200) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("Suchen") },
                        placeholder = { Text("Spiel, Notiz, Frage oder Element") },
                    )

                    Spacer(Modifier.height(8.dp))
                    InboxFilterRow(
                        title = "Status",
                        allLabel = "Alle",
                        allSelected = selectedStatus == null,
                        onAll = { selectedStatus = null },
                        choices = DeveloperFeedbackStatus.entries.map { status ->
                            status to when (status) {
                                DeveloperFeedbackStatus.NEW -> "Neu"
                                DeveloperFeedbackStatus.REVIEWED -> "Geprüft"
                                DeveloperFeedbackStatus.IN_PROGRESS -> "In Arbeit"
                                DeveloperFeedbackStatus.FIXED -> "Behoben"
                                DeveloperFeedbackStatus.VERIFIED -> "Verifiziert"
                            }
                        },
                        selected = selectedStatus,
                        onSelect = { selectedStatus = it },
                    )
                    InboxFilterRow(
                        title = "Priorität",
                        allLabel = "Alle",
                        allSelected = selectedPriority == null,
                        onAll = { selectedPriority = null },
                        choices = listOf(
                            DeveloperFeedbackPriority.BLOCKER to "Blocker",
                            DeveloperFeedbackPriority.HIGH to "Hoch",
                            DeveloperFeedbackPriority.MEDIUM to "Normal",
                            DeveloperFeedbackPriority.LOW to "Niedrig",
                        ),
                        selected = selectedPriority,
                        onSelect = { selectedPriority = it },
                    )
                    InboxFilterRow(
                        title = "Art",
                        allLabel = "Alle",
                        allSelected = selectedType == null,
                        onAll = { selectedType = null },
                        choices = listOf(
                            DeveloperFeedbackType.BUG to "Bug",
                            DeveloperFeedbackType.UI to "UI",
                            DeveloperFeedbackType.CHANGE to "Änderung",
                            DeveloperFeedbackType.IDEA to "Idee",
                            DeveloperFeedbackType.QUESTION to "Frage",
                        ),
                        selected = selectedType,
                        onSelect = { selectedType = it },
                    )

                    state.error?.let { error ->
                        Spacer(Modifier.height(6.dp))
                        Text(error, color = HarmonyPink, fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(8.dp))
                    if (state.isRefreshingInbox && state.feedbackItems.isEmpty()) {
                        InboxCenteredMessage("Inbox wird geladen …", showSpinner = true)
                    } else if (groups.isEmpty()) {
                        InboxCenteredMessage(
                            if (state.feedbackItems.isEmpty()) "Noch keine Developer-Notizen vorhanden." else "Keine Hinweise passen zu diesem Filter.",
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            groups.forEach { group ->
                                item(key = "header_${group.key}") {
                                    Text(
                                        group.key,
                                        color = HarmonyPurpleLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                    )
                                }
                                items(group.items, key = { it.id }) { item ->
                                    DeveloperFeedbackCard(
                                        item = item,
                                        busy = state.isBusy,
                                        onAdvanceStatus = { next ->
                                            reviewViewModel.updateStatus(item.id, next)
                                        },
                                    )
                                }
                            }
                            item { Spacer(Modifier.height(20.dp)) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun InboxCenteredMessage(
    text: String,
    showSpinner: Boolean = false,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (showSpinner) {
            CircularProgressIndicator(color = HarmonyPurpleLight)
            Spacer(Modifier.height(10.dp))
        }
        Text(text, color = HarmonyMuted, fontSize = 13.sp)
    }
}

@Composable
private fun <T> InboxFilterRow(
    title: String,
    allLabel: String,
    allSelected: Boolean,
    onAll: () -> Unit,
    choices: List<Pair<T, String>>,
    selected: T?,
    onSelect: (T) -> Unit,
) {
    Text(title, color = HarmonyMuted, fontSize = 10.5.sp)
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        item {
            InboxFilterChip(
                label = allLabel,
                selected = allSelected,
                onClick = onAll,
            )
        }
        items(choices) { (value, label) ->
            InboxFilterChip(
                label = label,
                selected = selected == value,
                onClick = { onSelect(value) },
            )
        }
    }
    Spacer(Modifier.height(5.dp))
}

@Composable
private fun InboxFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(50),
        color = if (selected) HarmonyPurple.copy(alpha = 0.58f) else HarmonySurface,
        border = BorderStroke(1.dp, if (selected) HarmonyPurpleLight else HarmonyLine),
    ) {
        Text(
            label,
            color = if (selected) Color.White else HarmonyMuted,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
        )
    }
}

@Composable
private fun DeveloperFeedbackCard(
    item: DeveloperFeedbackItem,
    busy: Boolean,
    onAdvanceStatus: (DeveloperFeedbackStatus) -> Unit,
) {
    val nextStatus = nextReviewStatus(item.status)
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = HarmonySurface,
        border = BorderStroke(
            1.dp,
            if (item.priority == DeveloperFeedbackPriority.BLOCKER) HarmonyPink else HarmonyLine,
        ),
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "${priorityLabel(item.priority)} · ${typeLabel(item.type)}",
                    color = if (item.priority == DeveloperFeedbackPriority.BLOCKER) HarmonyPink else HarmonyGold,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.weight(1f),
                )
                Text(statusLabel(item.status), color = HarmonyPurpleLight, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(Modifier.height(6.dp))
            Text(item.note, color = HarmonyText, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

            item.transcript?.takeIf { it.isNotBlank() }?.let { transcript ->
                Spacer(Modifier.height(5.dp))
                Text("🎙 ${transcript.take(220)}", color = HarmonyMuted, fontSize = 11.5.sp)
            }

            feedbackContextLine(item)?.let { context ->
                Spacer(Modifier.height(7.dp))
                Text(context, color = HarmonyMuted, fontSize = 10.5.sp)
            }

            if (item.screenshotPath != null || item.audioPath != null) {
                Spacer(Modifier.height(5.dp))
                Text(
                    buildList {
                        if (item.screenshotPath != null) add("📷 Screenshot")
                        if (item.audioPath != null) add("🎙 Audio")
                    }.joinToString(" · "),
                    color = HarmonyGold,
                    fontSize = 10.5.sp,
                )
            }

            if (item.githubPr != null || item.fixedCommit != null) {
                Spacer(Modifier.height(5.dp))
                Text(
                    buildList {
                        item.githubPr?.let { add("PR #$it") }
                        item.githubBranch?.let { add(it) }
                        item.fixedCommit?.let { add(it.take(9)) }
                    }.joinToString(" · "),
                    color = HarmonyPurpleLight,
                    fontSize = 10.5.sp,
                )
            }

            nextStatus?.let { next ->
                Spacer(Modifier.height(8.dp))
                TextButton(
                    enabled = !busy,
                    onClick = { onAdvanceStatus(next) },
                    modifier = Modifier.align(Alignment.End),
                ) {
                    Text(statusActionLabel(next), color = HarmonyGold, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun nextReviewStatus(status: DeveloperFeedbackStatus): DeveloperFeedbackStatus? = when (status) {
    DeveloperFeedbackStatus.NEW -> DeveloperFeedbackStatus.REVIEWED
    DeveloperFeedbackStatus.REVIEWED -> DeveloperFeedbackStatus.IN_PROGRESS
    DeveloperFeedbackStatus.FIXED -> DeveloperFeedbackStatus.VERIFIED
    DeveloperFeedbackStatus.IN_PROGRESS,
    DeveloperFeedbackStatus.VERIFIED -> null
}

private fun feedbackContextLine(item: DeveloperFeedbackItem): String? = buildList {
    item.context.screen?.let { add(it) }
    item.context.round?.let { add("Runde $it") }
    item.context.questionText?.takeIf { it.isNotBlank() }?.let { add(it.take(90)) }
    item.context.elementId?.let { add("Element $it") }
    item.appVersion?.let { add("v$it") }
}.takeIf { it.isNotEmpty() }?.joinToString(" · ")

private fun statusLabel(status: DeveloperFeedbackStatus): String = when (status) {
    DeveloperFeedbackStatus.NEW -> "NEU"
    DeveloperFeedbackStatus.REVIEWED -> "GEPRÜFT"
    DeveloperFeedbackStatus.IN_PROGRESS -> "IN ARBEIT"
    DeveloperFeedbackStatus.FIXED -> "BEHOBEN"
    DeveloperFeedbackStatus.VERIFIED -> "VERIFIZIERT"
}

private fun statusActionLabel(status: DeveloperFeedbackStatus): String = when (status) {
    DeveloperFeedbackStatus.REVIEWED -> "Als geprüft markieren"
    DeveloperFeedbackStatus.IN_PROGRESS -> "In Arbeit"
    DeveloperFeedbackStatus.VERIFIED -> "Als verifiziert markieren"
    else -> statusLabel(status)
}

private fun priorityLabel(priority: DeveloperFeedbackPriority): String = when (priority) {
    DeveloperFeedbackPriority.BLOCKER -> "BLOCKER"
    DeveloperFeedbackPriority.HIGH -> "HOCH"
    DeveloperFeedbackPriority.MEDIUM -> "NORMAL"
    DeveloperFeedbackPriority.LOW -> "NIEDRIG"
}

private fun typeLabel(type: DeveloperFeedbackType): String = when (type) {
    DeveloperFeedbackType.BUG -> "BUG"
    DeveloperFeedbackType.UI -> "UI"
    DeveloperFeedbackType.CHANGE -> "ÄNDERUNG"
    DeveloperFeedbackType.IDEA -> "IDEE"
    DeveloperFeedbackType.QUESTION -> "FRAGE"
}
