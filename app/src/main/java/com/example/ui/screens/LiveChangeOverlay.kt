package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.LiveQuestionEditing
import com.example.data.LiveQuestionKind
import com.example.data.model.Question
import com.example.data.model.QuestionPack
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonyText
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Developer-only passive long-press observer. It listens in the Initial/Final passes and
 * does not consume normal game taps, so the production runner keeps its original behavior.
 */
fun Modifier.liveChangeLongPressObserver(
    enabled: Boolean,
    onLongPress: () -> Unit
): Modifier {
    if (!enabled) return this
    return pointerInput(enabled) {
        val timeout = viewConfiguration.longPressTimeoutMillis
        awaitEachGesture {
            val down = awaitFirstDown(
                requireUnconsumed = false,
                pass = PointerEventPass.Initial
            )
            val releasedBeforeTimeout = withTimeoutOrNull(timeout) {
                var pressed = true
                while (pressed) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    pressed = event.changes.firstOrNull { it.id == down.id }?.pressed == true
                }
                true
            }
            if (releasedBeforeTimeout == null) {
                onLongPress()
                var stillPressed = true
                while (stillPressed) {
                    val event = awaitPointerEvent(PointerEventPass.Final)
                    stillPressed = event.changes.firstOrNull { it.id == down.id }?.pressed == true
                }
            }
        }
    }
}

@Composable
fun LiveChangeLauncher(
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    if (!isVisible) return

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = Color(0xF21A1022),
        border = BorderStroke(1.dp, HarmonyPurpleLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    "⚡ Live Change",
                    color = HarmonyText,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
                TextButton(
                    onClick = { isVisible = false },
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .testTag("live_change_dismiss_button")
                ) {
                    Text("✕", color = HarmonyMuted, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(Modifier.height(4.dp))
            Text(
                "Spiel öffnen · aktuelle Frage gedrückt halten · direkt ändern",
                color = HarmonyMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = onStart,
                colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurple),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Live Change starten", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun LiveChangeHud(
    changeCount: Int,
    hasActiveItem: Boolean,
    onEditCurrent: () -> Unit,
    onStop: () -> Unit,
    onExportTxt: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xF2171020),
        border = BorderStroke(1.dp, HarmonyGold)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(7.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .background(HarmonyGold, CircleShape)
            )
            Column {
                Text("LIVE CHANGE", color = HarmonyGold, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                Text("$changeCount Änderung(en)", color = HarmonyMuted, fontSize = 9.sp)
            }
            DeveloperReviewQuickNote()
            if (hasActiveItem) {
                TextButton(onClick = onEditCurrent) {
                    Text("Bearbeiten", color = HarmonyPurpleLight, fontSize = 11.sp)
                }
            }
            if (onExportTxt != null && changeCount > 0) {
                TextButton(onClick = onExportTxt) {
                    Text("TXT 📄", color = HarmonyGold, fontSize = 11.sp)
                }
            }
            TextButton(onClick = onStop) {
                Text("Beenden", color = HarmonyPink, fontSize = 11.sp)
            }
        }
    }
}

private enum class LiveEditorMode {
    ACTIONS,
    EDIT,
    INSERT_BEFORE,
    INSERT_AFTER
}

@Composable
fun LiveChangeEditor(
    pack: QuestionPack,
    currentIndex: Int,
    onDismiss: () -> Unit,
    onSave: (QuestionPack, Int, String) -> Unit
) {
    if (pack.type == "tot") {
        LivePairEditor(pack, currentIndex, onDismiss, onSave)
    } else {
        LiveQuestionEditor(pack, currentIndex, onDismiss, onSave)
    }
}

@Composable
private fun LiveQuestionEditor(
    pack: QuestionPack,
    currentIndex: Int,
    onDismiss: () -> Unit,
    onSave: (QuestionPack, Int, String) -> Unit
) {
    val startIndex = currentIndex.coerceIn(0, (pack.questions.size - 1).coerceAtLeast(0))
    var selectedIndex by remember(pack.id, currentIndex, pack.questions.size) { mutableStateOf(startIndex) }
    var mode by remember(pack.id, selectedIndex) { mutableStateOf(LiveEditorMode.ACTIONS) }
    val current = pack.questions.getOrNull(selectedIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(22.dp),
            color = HarmonyBg,
            border = BorderStroke(1.dp, HarmonyLine)
        ) {
            if (mode == LiveEditorMode.ACTIONS) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    LiveHeader(
                        title = if (current == null) "⚡ Neue Frage" else "⚡ Frage ${selectedIndex + 1}",
                        subtitle = pack.title,
                        onDismiss = onDismiss
                    )

                    if (pack.questions.size > 1) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                enabled = selectedIndex > 0,
                                onClick = {
                                    selectedIndex--
                                    mode = LiveEditorMode.ACTIONS
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("← Vorherige") }
                            OutlinedButton(
                                enabled = selectedIndex < pack.questions.lastIndex,
                                onClick = {
                                    selectedIndex++
                                    mode = LiveEditorMode.ACTIONS
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("Nächste →") }
                        }
                    }

                    Spacer(Modifier.height(14.dp))
                    current?.let {
                        Text(it.q, color = HarmonyText, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            when (LiveQuestionEditing.effectiveKind(it, pack.type)) {
                                LiveQuestionKind.CHOICE -> "Auswahlfrage · ${it.options.size} Antwortmöglichkeiten"
                                LiveQuestionKind.FREE_TEXT -> "Freie Antwort"
                                LiveQuestionKind.THIS_OR_THAT -> "Das oder Das"
                            },
                            color = HarmonyMuted,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(Modifier.height(18.dp))
                    if (current != null) {
                        ActionButton("✏️ Aktuelle Frage bearbeiten", onClick = { mode = LiveEditorMode.EDIT })
                    }
                    ActionButton("➕ Frage direkt davor einfügen", onClick = { mode = LiveEditorMode.INSERT_BEFORE })
                    ActionButton("➕ Frage direkt danach einfügen", onClick = { mode = LiveEditorMode.INSERT_AFTER })

                    if (current != null) {
                        ActionButton("⧉ Frage duplizieren") {
                            val updated = LiveQuestionEditing.duplicateQuestion(pack, selectedIndex)
                            onSave(updated, selectedIndex + 1, "Frage dupliziert")
                        }
                        if (selectedIndex > 0) {
                            ActionButton("↑ Eine Position nach oben") {
                                onSave(
                                    LiveQuestionEditing.moveQuestion(pack, selectedIndex, selectedIndex - 1),
                                    selectedIndex - 1,
                                    "Frage verschoben"
                                )
                            }
                        }
                        if (selectedIndex < pack.questions.lastIndex) {
                            ActionButton("↓ Eine Position nach unten") {
                                onSave(
                                    LiveQuestionEditing.moveQuestion(pack, selectedIndex, selectedIndex + 1),
                                    selectedIndex + 1,
                                    "Frage verschoben"
                                )
                            }
                        }
                        if (pack.questions.size > 1) {
                            ActionButton("🗑 Frage löschen", danger = true) {
                                val next = selectedIndex.coerceAtMost(pack.questions.lastIndex - 1).coerceAtLeast(0)
                                onSave(LiveQuestionEditing.deleteQuestion(pack, selectedIndex), next, "Frage gelöscht")
                            }
                        }
                    }
                }
            } else {
                val initial = if (mode == LiveEditorMode.EDIT) current else null
                LiveQuestionForm(
                    packType = pack.type,
                    mode = mode,
                    initialQuestion = initial,
                    onBack = { mode = LiveEditorMode.ACTIONS },
                    onSaveQuestion = { question ->
                        val result = when (mode) {
                            LiveEditorMode.EDIT -> LiveQuestionEditing.replaceQuestion(pack, selectedIndex, question)
                            LiveEditorMode.INSERT_BEFORE -> LiveQuestionEditing.insertQuestion(pack, selectedIndex, question)
                            LiveEditorMode.INSERT_AFTER -> LiveQuestionEditing.insertQuestion(pack, selectedIndex + 1, question)
                            LiveEditorMode.ACTIONS -> pack
                        }
                        val target = if (mode == LiveEditorMode.INSERT_AFTER) selectedIndex + 1 else selectedIndex
                        onSave(
                            result,
                            target,
                            if (mode == LiveEditorMode.EDIT) "Frage geändert" else "Frage eingefügt"
                        )
                    }
                )
            }
        }
    }
}

@Composable
private fun LiveQuestionForm(
    packType: String,
    mode: LiveEditorMode,
    initialQuestion: Question?,
    onBack: () -> Unit,
    onSaveQuestion: (Question) -> Unit
) {
    val initialKind = initialQuestion?.let { LiveQuestionEditing.effectiveKind(it, packType) }
        ?: if (packType == "disc") LiveQuestionKind.FREE_TEXT else LiveQuestionKind.CHOICE
    var kind by remember(initialQuestion, mode, packType) { mutableStateOf(initialKind) }
    var text by remember(initialQuestion, mode) { mutableStateOf(initialQuestion?.q ?: "") }
    val options = remember(initialQuestion, mode) {
        mutableStateListOf<String>().apply {
            val existing = initialQuestion?.let { LiveQuestionEditing.visibleOptions(it) }.orEmpty()
            if (existing.isNotEmpty()) {
                addAll(existing)
            } else if (initialKind == LiveQuestionKind.CHOICE) {
                addAll(listOf("", ""))
            }
        }
    }
    var error by remember(initialQuestion, mode) { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        LiveHeader(
            title = if (mode == LiveEditorMode.EDIT) "Frage bearbeiten" else "Neue Frage",
            subtitle = "Position wird exakt übernommen",
            onDismiss = onBack
        )
        Spacer(Modifier.height(14.dp))

        if (packType != "disc") {
            Text("Fragetyp", color = HarmonyMuted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TypeButton(
                    label = "Auswahl",
                    selected = kind == LiveQuestionKind.CHOICE,
                    onClick = {
                        kind = LiveQuestionKind.CHOICE
                        if (options.isEmpty()) options.addAll(listOf("", ""))
                    },
                    modifier = Modifier.weight(1f)
                )
                TypeButton(
                    label = "Freie Antwort",
                    selected = kind == LiveQuestionKind.FREE_TEXT,
                    onClick = { kind = LiveQuestionKind.FREE_TEXT },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            Text("Fragetyp: Freie Antwort", color = HarmonyPurpleLight, fontWeight = FontWeight.Bold)
        }

        Spacer(Modifier.height(14.dp))
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Frage") },
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        if (kind == LiveQuestionKind.CHOICE) {
            Spacer(Modifier.height(18.dp))
            Text("Antwortmöglichkeiten", color = HarmonyText, fontWeight = FontWeight.Bold)
            Text("Beliebig viele hinzufügen, löschen und sortieren", color = HarmonyMuted, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))

            options.forEachIndexed { index, value ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { options[index] = it },
                        label = { Text("Antwort ${index + 1}") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            enabled = index > 0,
                            onClick = {
                                val previous = options[index - 1]
                                options[index - 1] = options[index]
                                options[index] = previous
                            }
                        ) { Text("↑ Hoch") }
                        TextButton(
                            enabled = index < options.lastIndex,
                            onClick = {
                                val next = options[index + 1]
                                options[index + 1] = options[index]
                                options[index] = next
                            }
                        ) { Text("↓ Runter") }
                        TextButton(
                            enabled = options.size > 1,
                            onClick = { options.removeAt(index) }
                        ) { Text("✕ Löschen", color = HarmonyPink) }
                    }
                }
            }

            OutlinedButton(
                onClick = { options.add("") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("+ Weitere Antwortmöglichkeit")
            }
        }

        error?.let {
            Spacer(Modifier.height(10.dp))
            Text(it, color = HarmonyPink, fontSize = 12.sp)
        }

        Spacer(Modifier.height(20.dp))
        Button(
            onClick = {
                runCatching {
                    LiveQuestionEditing.createQuestion(
                        kind = kind,
                        text = text,
                        options = options.toList(),
                        defaultMine = initialQuestion?.defaultMine
                    )
                }.onSuccess(onSaveQuestion)
                    .onFailure { error = it.message ?: "Bitte Eingaben prüfen" }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurple),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text("Speichern", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun LivePairEditor(
    pack: QuestionPack,
    currentIndex: Int,
    onDismiss: () -> Unit,
    onSave: (QuestionPack, Int, String) -> Unit
) {
    val startIndex = currentIndex.coerceIn(0, (pack.pairs.size - 1).coerceAtLeast(0))
    var selectedIndex by remember(pack.id, currentIndex, pack.pairs.size) { mutableStateOf(startIndex) }
    var mode by remember(pack.id, selectedIndex) { mutableStateOf(LiveEditorMode.ACTIONS) }
    val current = pack.pairs.getOrNull(selectedIndex)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            shape = RoundedCornerShape(22.dp),
            color = HarmonyBg,
            border = BorderStroke(1.dp, HarmonyLine)
        ) {
            if (mode == LiveEditorMode.ACTIONS) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    LiveHeader(
                        title = if (current == null) "⚡ Neues Das oder Das" else "⚡ Paar ${selectedIndex + 1}",
                        subtitle = pack.title,
                        onDismiss = onDismiss
                    )
                    if (pack.pairs.size > 1) {
                        Spacer(Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                enabled = selectedIndex > 0,
                                onClick = { selectedIndex-- },
                                modifier = Modifier.weight(1f)
                            ) { Text("← Vorheriges") }
                            OutlinedButton(
                                enabled = selectedIndex < pack.pairs.lastIndex,
                                onClick = { selectedIndex++ },
                                modifier = Modifier.weight(1f)
                            ) { Text("Nächstes →") }
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    current?.let {
                        Text("${it.first}  ↔  ${it.second}", color = HarmonyText, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    Spacer(Modifier.height(18.dp))
                    if (current != null) ActionButton("✏️ Paar bearbeiten") { mode = LiveEditorMode.EDIT }
                    ActionButton("➕ Paar direkt davor") { mode = LiveEditorMode.INSERT_BEFORE }
                    ActionButton("➕ Paar direkt danach") { mode = LiveEditorMode.INSERT_AFTER }
                    if (current != null) {
                        ActionButton("⧉ Paar duplizieren") {
                            onSave(LiveQuestionEditing.duplicatePair(pack, selectedIndex), selectedIndex + 1, "Paar dupliziert")
                        }
                        if (selectedIndex > 0) {
                            ActionButton("↑ Eine Position nach oben") {
                                onSave(
                                    LiveQuestionEditing.movePair(pack, selectedIndex, selectedIndex - 1),
                                    selectedIndex - 1,
                                    "Paar verschoben"
                                )
                            }
                        }
                        if (selectedIndex < pack.pairs.lastIndex) {
                            ActionButton("↓ Eine Position nach unten") {
                                onSave(
                                    LiveQuestionEditing.movePair(pack, selectedIndex, selectedIndex + 1),
                                    selectedIndex + 1,
                                    "Paar verschoben"
                                )
                            }
                        }
                        if (pack.pairs.size > 1) {
                            ActionButton("🗑 Paar löschen", danger = true) {
                                val next = selectedIndex.coerceAtMost(pack.pairs.lastIndex - 1).coerceAtLeast(0)
                                onSave(LiveQuestionEditing.deletePair(pack, selectedIndex), next, "Paar gelöscht")
                            }
                        }
                    }
                }
            } else {
                val initial = if (mode == LiveEditorMode.EDIT) current else null
                var left by remember(mode, selectedIndex) { mutableStateOf(initial?.first ?: "") }
                var right by remember(mode, selectedIndex) { mutableStateOf(initial?.second ?: "") }
                var error by remember(mode, selectedIndex) { mutableStateOf<String?>(null) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    LiveHeader(
                        title = if (mode == LiveEditorMode.EDIT) "Paar bearbeiten" else "Neues Das-oder-Das-Paar",
                        subtitle = "Genau zwei Antwortseiten",
                        onDismiss = { mode = LiveEditorMode.ACTIONS }
                    )
                    Spacer(Modifier.height(18.dp))
                    OutlinedTextField(
                        value = left,
                        onValueChange = { left = it },
                        label = { Text("Option A") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedTextField(
                        value = right,
                        onValueChange = { right = it },
                        label = { Text("Option B") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    error?.let {
                        Spacer(Modifier.height(8.dp))
                        Text(it, color = HarmonyPink, fontSize = 12.sp)
                    }
                    Spacer(Modifier.height(20.dp))
                    Button(
                        onClick = {
                            runCatching {
                                val pair = left to right
                                when (mode) {
                                    LiveEditorMode.EDIT -> LiveQuestionEditing.replacePair(pack, selectedIndex, pair)
                                    LiveEditorMode.INSERT_BEFORE -> LiveQuestionEditing.insertPair(pack, selectedIndex, pair)
                                    LiveEditorMode.INSERT_AFTER -> LiveQuestionEditing.insertPair(pack, selectedIndex + 1, pair)
                                    LiveEditorMode.ACTIONS -> pack
                                }
                            }.onSuccess { updated ->
                                val target = if (mode == LiveEditorMode.INSERT_AFTER) selectedIndex + 1 else selectedIndex
                                onSave(
                                    updated,
                                    target,
                                    if (mode == LiveEditorMode.EDIT) "Paar geändert" else "Paar eingefügt"
                                )
                            }.onFailure { error = it.message ?: "Bitte beide Optionen eingeben" }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HarmonyPurple),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Speichern", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun LiveHeader(
    title: String,
    subtitle: String,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = HarmonyText, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            Text(subtitle, color = HarmonyMuted, fontSize = 11.sp)
        }
        Spacer(Modifier.width(8.dp))
        TextButton(onClick = onDismiss) {
            Text("Schließen", color = HarmonyMuted)
        }
    }
}

@Composable
private fun ActionButton(
    label: String,
    danger: Boolean = false,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(50.dp),
        shape = RoundedCornerShape(13.dp),
        border = BorderStroke(1.dp, if (danger) HarmonyPink else HarmonyLine)
    ) {
        Text(label, color = if (danger) HarmonyPink else HarmonyText, fontWeight = FontWeight.Medium)
    }
}