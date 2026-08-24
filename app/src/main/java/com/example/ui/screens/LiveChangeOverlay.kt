package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

/** Passive observer: recognizes a long press without consuming the normal app gesture. */
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
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = HarmonySurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, HarmonyPurpleLight)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("⚡ Live Change", color = HarmonyText, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                "App öffnen · Element gedrückt halten · direkt ändern",
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
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color(0xF2171020),
        border = androidx.compose.foundation.BorderStroke(1.dp, HarmonyGold)
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
            if (hasActiveItem) {
                TextButton(onClick = onEditCurrent) {
                    Text("Bearbeiten", color = HarmonyPurpleLight, fontSize = 11.sp)
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
    val safeIndex = currentIndex.coerceIn(0, (pack.questions.size - 1).coerceAtLeast(0))
    val current = pack.questions.getOrNull(safeIndex)
    var mode by remember(pack.id, safeIndex) { mutableStateOf(LiveEditorMode.ACTIONS) }

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
            border = androidx.compose.foundation.BorderStroke(1.dp, HarmonyLine)
        ) {
            if (mode == LiveEditorMode.ACTIONS) {
                LiveQuestionActions(
                    pack = pack,
                    index = safeIndex,
                    question = current,
                    onDismiss = onDismiss,
                    onEdit = { mode = LiveEditorMode.EDIT },
                    onInsertBefore = { mode = LiveEditorMode.INSERT_BEFORE },
                    onInsertAfter = { mode = LiveEditorMode.INSERT_AFTER },
                    onSave = onSave
                )
            } else {
                val initial = if (mode == LiveEditorMode.EDIT) current else null
                LiveQuestionForm(
                    packType = pack.type,
                    mode = mode,
                    initialQuestion = initial,
                    onBack = { mode = LiveEditorMode.ACTIONS },
                    onSaveQuestion = { question ->
                        val result = when (mode) {
                            LiveEditorMode.EDIT -> LiveQuestionEditing.replaceQuestion(pack, safeIndex, question)
                            LiveEditorMode.INSERT_BEFORE -> LiveQuestionEditing.insertQuestion(pack, safeIndex, question)
                            LiveEditorMode.INSERT_AFTER -> LiveQuestionEditing.insertQuestion(pack, safeIndex + 1, question)
                            LiveEditorMode.ACTIONS -> pack
                        }
                        val target = when (mode) {
                            LiveEditorMode.INSERT_AFTER -> safeIndex + 1
                            else -> safeIndex
                        }
                        onSave(result, target, if (mode == LiveEditorMode.EDIT) "Frage geändert" else "Frage eingefügt")
                    }
                )
            }
        }
    }
}

@Composable
private fun LiveQuestionActions(
    pack: QuestionPack,
    index: Int,
    question: Question?,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onInsertBefore: () -> Unit,
    onInsertAfter: () -> Unit,
    onSave: (QuestionPack, Int, String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        LiveHeader("⚡ Frage ${index + 1} bearbeiten", pack.title, onDismiss)
        Spacer(Modifier.height(14.dp))
        question?.let {
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
        if (question != null) {
            ActionButton("✏️ Aktuelle Frage bearbeiten", onClick = onEdit)
        }
        ActionButton("➕ Frage direkt davor einfügen", onClick = onInsertBefore)
        ActionButton("➕ Frage direkt danach einfügen", onClick = onInsertAfter)

        if (question != null) {
            ActionButton("⧉ Frage duplizieren") {
                val updated = LiveQuestionEditing.duplicateQuestion(pack, index)
                onSave(updated, index + 1, "Frage dupliziert")
            }
            if (index > 0) {
                ActionButton("↑ Eine Position nach oben") {
                    onSave(LiveQuestionEditing.moveQuestion(pack, index, index - 1), index - 1, "Frage verschoben")
                }
            }
            if (index < pack.questions.lastIndex) {
                ActionButton("↓ Eine Position nach unten") {
                    onSave(LiveQuestionEditing.moveQuestion(pack, index, index + 1), index + 1, "Frage verschoben")
                }
            }
            if (pack.questions.size > 1) {
                ActionButton("🗑 Frage löschen", danger = true) {
                    val nextIndex = index.coerceAtMost(pack.questions.lastIndex - 1).coerceAtLeast(0)
                    onSave(LiveQuestionEditing.deleteQuestion(pack, index), nextIndex, "Frage gelöscht")
                }
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
            val initial = initialQuestion?.let { LiveQuestionEditing.visibleOptions(it) }.orEmpty()
            if (initial.isNotEmpty()) addAll(initial) else if (initialKind == LiveQuestionKind.CHOICE) addAll(listOf("", ""))
        }
    }
    var error by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(18.dp)
    ) {
        LiveHeader(
            if (mode == LiveEditorMode.EDIT) "Frage bearbeiten" else "Neue Frage",
            "Position bleibt exakt erhalten",
            onBack
        )
        Spacer(Modifier.height(14.dp))

        if (packType != "disc") {
            Text("Fragetyp", color = HarmonyMuted, fontSize = 12.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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
            Spacer(Modifier.height(14.dp))
        } else {
            Text("Fragetyp: Freie Antwort", color = HarmonyPurpleLight, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(12.dp))
        }

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
            Text("Beliebig viele hinzufügen und frei sortieren", color = HarmonyMuted, fontSize = 11.sp)
            Spacer(Modifier.height(8.dp))

            options.forEachIndexed { index, value ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = value,
                        onValueChange = { options[index] = it },
                        label = { Text("Antwort ${index + 1}") },
                        singleLine = true,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(5.dp))
                    Column {
                        TextButton(
                            enabled = index > 0,
                            onClick = {
                                val tmp = options[index - 1]
                                options[index - 1] = options[index]
                                options[index] = tmp
                            }
                        ) { Text("↑") }
                        TextButton(
                            enabled = index < options.lastIndex,
                            onClick = {
                                val tmp = options[index + 1]
                                options[index + 1] = options[index]
                                options[index] = tmp
                            }
                        ) { Text("↓") }
                    }
                    TextButton(onClick = { if (options.size > 1) options.removeAt(index) }) {
                        Text("✕", color = HarmonyPink)
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
    val safeIndex = currentIndex.coerceIn(0, (pack.pairs.size - 1).coerceAtLeast(0))
    val current = pack.pairs.getOrNull(safeIndex)
    var mode by remember(pack.id, safeIndex) { mutableStateOf(LiveEditorMode.ACTIONS) }

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
            border = androidx.compose.foundation.BorderStroke(1.dp, HarmonyLine)
        ) {
            if (mode == LiveEditorMode.ACTIONS) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    LiveHeader("⚡ Das oder Das ${safeIndex + 1}", pack.title, onDismiss)
                    Spacer(Modifier.height(14.dp))
                    current?.let {
                        Text("${it.first}  ↔  ${it.second}", color = HarmonyText, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                    Spacer(Modifier.height(18.dp))
                    if (current != null) {
                        ActionButton("✏️ Paar bearbeiten") { mode = LiveEditorMode.EDIT }
                    }
                    ActionButton("➕ Paar direkt davor") { mode = LiveEditorMode.INSERT_BEFORE }
                    ActionButton("➕ Paar direkt danach") { mode = LiveEditorMode.INSERT_AFTER }
                    if (current != null) {
                        ActionButton("⧉ Paar duplizieren") {
                            onSave(LiveQuestionEditing.duplicatePair(pack, safeIndex), safeIndex + 1, "Paar dupliziert")
                        }
                        if (safeIndex > 0) {
                            ActionButton("↑ Eine Position nach oben") {
                                onSave(LiveQuestionEditing.movePair(pack, safeIndex, safeIndex - 1), safeIndex - 1, "Paar verschoben")
                            }
                        }
                        if (safeIndex < pack.pairs.lastIndex) {
                            ActionButton("↓ Eine Position nach unten") {
                                onSave(LiveQuestionEditing.movePair(pack, safeIndex, safeIndex + 1), safeIndex + 1, "Paar verschoben")
                            }
                        }
                        if (pack.pairs.size > 1) {
                            ActionButton("🗑 Paar löschen", danger = true) {
                                val nextIndex = safeIndex.coerceAtMost(pack.pairs.lastIndex - 1).coerceAtLeast(0)
                                onSave(LiveQuestionEditing.deletePair(pack, safeIndex), nextIndex, "Paar gelöscht")
                            }
                        }
                    }
                }
            } else {
                val pair = if (mode == LiveEditorMode.EDIT) current else null
                var left by remember(mode, current) { mutableStateOf(pair?.first ?: "") }
                var right by remember(mode, current) { mutableStateOf(pair?.second ?: "") }
                var error by remember { mutableStateOf<String?>(null) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(18.dp)
                ) {
                    LiveHeader(
                        if (mode == LiveEditorMode.EDIT) "Paar bearbeiten" else "Neues Das-oder-Das-Paar",
                        "Zwei Antwortseiten",
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
                            val newPair = left.trim() to right.trim()
                            runCatching {
                                when (mode) {
                                    LiveEditorMode.EDIT -> LiveQuestionEditing.replacePair(pack, safeIndex, newPair)
                                    LiveEditorMode.INSERT_BEFORE -> LiveQuestionEditing.insertPair(pack, safeIndex, newPair)
                                    LiveEditorMode.INSERT_AFTER -> LiveQuestionEditing.insertPair(pack, safeIndex + 1, newPair)
                                    LiveEditorMode.ACTIONS -> pack
                                }
                            }.onSuccess { updated ->
                                val target = if (mode == LiveEditorMode.INSERT_AFTER) safeIndex + 1 else safeIndex
                                onSave(updated, target, if (mode == LiveEditorMode.EDIT) "Paar geändert" else "Paar eingefügt")
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
        border = androidx.compose.foundation.BorderStroke(1.dp, if (danger) HarmonyPink else HarmonyLine)
    ) {
        Text(label, color = if (danger) HarmonyPink else HarmonyText, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TypeButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) HarmonyPurple else HarmonySurface
        ),
        shape = RoundedCornerShape(12.dp),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, HarmonyLine)
    ) {
        Text(label, color = if (selected) Color.White else HarmonyMuted, fontSize = 12.sp)
    }
}
