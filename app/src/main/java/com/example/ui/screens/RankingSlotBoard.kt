package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.data.model.ProfileEntity
import com.example.data.model.RankingAnswerCodec
import com.example.ui.theme.HarmonyBg
import com.example.ui.theme.HarmonyLine
import com.example.ui.theme.HarmonyMuted
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonyPurpleLight
import com.example.ui.theme.HarmonySurface
import com.example.ui.theme.HarmonySurface2
import com.example.ui.theme.HarmonyText
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration

/**
 * New ranking questions start empty. Answers stay in the right-hand card pool until the user
 * actively drags each card into one of the numbered ranking slots.
 */
@Composable
internal fun RankingSlotBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val hitSlop = with(LocalDensity.current) { 22.dp.toPx() }
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    val labels = items.associate { it.raw to it.label }

    val restored = remember(question, selectedAnswer, options) {
        selectedAnswer?.let { RankingAnswerCodec.decode(it, options) }
    }
    val slots = remember(question, selectedAnswer, options) {
        mutableStateListOf<String?>().apply {
            if (restored != null) addAll(restored) else repeat(options.size) { add(null) }
        }
    }
    val slotBounds = remember(question) { mutableStateMapOf<Int, DropRect>() }
    var hoveredSlot by remember(question) { mutableStateOf<Int?>(null) }

    val unassigned = options.filter { option -> slots.none { it == option } }
    val complete = options.isNotEmpty() && slots.all { it != null } &&
        slots.filterNotNull().distinct().size == options.size

    fun resolveSlot(pointer: Offset?): Int? {
        if (pointer == null) return null
        slotBounds.entries.firstOrNull { (_, rect) -> rect.contains(pointer.x, pointer.y) }?.let { return it.key }
        return slotBounds.entries
            .filter { (_, rect) -> rect.contains(pointer.x, pointer.y, hitSlop) }
            .minByOrNull { (_, rect) -> rect.distanceSquaredTo(pointer.x, pointer.y) }
            ?.key
    }

    fun placeCard(raw: String, fromSlot: Int?, targetSlot: Int) {
        if (targetSlot !in slots.indices) return
        if (fromSlot == targetSlot) {
            hoveredSlot = null
            return
        }

        val displaced = slots[targetSlot]
        if (fromSlot != null && fromSlot in slots.indices) slots[fromSlot] = displaced
        slots[targetSlot] = raw
        hoveredSlot = null
        triggerMiniVibration(context, 48L)
    }

    FullscreenMechanicShell(
        kicker = tr("🏆 RANKING-DUELL", "🏆 RANKING DUEL"),
        question = prompt,
        instruction = tr(
            "Die Rangliste ist leer. Ziehe jede Karte von rechts auf Platz 1, 2, 3 …",
            "The ranking starts empty. Drag every card from the right onto place 1, 2, 3 …"
        ),
        modifier = modifier.testTag("ranking_slot_board")
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().weight(1f),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1.42f).fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = tr("DEINE RANGLISTE", "YOUR RANKING"),
                        color = HarmonyPinkSoft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    slots.indices.forEach { slotIndex ->
                        key("slot_$slotIndex") {
                            RankingDropSlot(
                                position = slotIndex,
                                raw = slots[slotIndex],
                                label = slots[slotIndex]?.let { labels[it] },
                                highlighted = hoveredSlot == slotIndex,
                                onBounds = { slotBounds[slotIndex] = it },
                                resolveSlot = ::resolveSlot,
                                onHover = { hoveredSlot = it },
                                onMove = ::placeCard,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                val poolShape = RoundedCornerShape(26.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        // Do not clip this parent: dragged cards must stay visible while crossing
                        // from the right-hand pool into the ranking slots on the left.
                        .background(Color.White.copy(alpha = 0.035f), poolShape)
                        .border(1.dp, HarmonyLine, poolShape)
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = tr("KARTEN", "CARDS"),
                        color = HarmonyPinkSoft,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (unassigned.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                text = tr("Alle Karten liegen in der Rangliste", "All cards are in the ranking"),
                                color = HarmonyPinkSoft,
                                fontSize = 15.sp,
                                lineHeight = 19.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        unassigned.forEach { raw ->
                            key("pool_$raw") {
                                RankingDraggableCard(
                                    raw = raw,
                                    label = labels[raw] ?: raw,
                                    fromSlot = null,
                                    resolveSlot = ::resolveSlot,
                                    onHover = { hoveredSlot = it },
                                    onDrop = { target -> placeCard(raw, null, target) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))
            PrimaryMechanicButton(
                text = if (complete) {
                    tr("Rangliste speichern & weiter", "Save ranking & continue")
                } else {
                    tr("Belege alle Rangplätze", "Fill every ranking slot")
                },
                enabled = complete,
                onClick = {
                    val order = slots.filterNotNull()
                    if (order.size == options.size && order.distinct().size == options.size) {
                        triggerMiniVibration(context, 52L)
                        onPick(RankingAnswerCodec.encode(order))
                    }
                },
                testTag = "ranking_submit"
            )
        }
    }
}

@Composable
private fun RankingDropSlot(
    position: Int,
    raw: String?,
    label: String?,
    highlighted: Boolean,
    onBounds: (DropRect) -> Unit,
    resolveSlot: (Offset?) -> Int?,
    onHover: (Int?) -> Unit,
    onMove: (String, Int?, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    val accent = if (position == 0) HarmonyPinkSoft else HarmonyPurpleLight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 66.dp)
            // Keep children drawable outside the slot while a placed card is being moved.
            .background(
                Brush.horizontalGradient(
                    listOf(
                        accent.copy(alpha = if (highlighted) 0.34f else 0.16f),
                        HarmonySurface2.copy(alpha = 0.96f),
                        HarmonyBg.copy(alpha = 0.94f)
                    )
                ),
                shape
            )
            .border(
                if (highlighted) 2.2.dp else 1.2.dp,
                if (highlighted) HarmonyPinkSoft else Color.White.copy(alpha = 0.16f),
                shape
            )
            .onGloballyPositioned { coordinates ->
                val rect = coordinates.boundsInRoot()
                onBounds(DropRect(rect.left, rect.top, rect.right, rect.bottom))
            }
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .testTag("ranking_slot_$position"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        if (raw == null) {
                            listOf(Color.White.copy(alpha = 0.09f), Color.White.copy(alpha = 0.04f))
                        } else {
                            listOf(HarmonyPink, HarmonyPurpleLight)
                        }
                    )
                )
                .border(1.dp, Color.White.copy(alpha = 0.22f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${position + 1}",
                color = Color.White,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
        }
        Spacer(Modifier.width(12.dp))
        if (raw == null) {
            Text(
                text = tr("Platz ${position + 1} · Karte hier ablegen", "Place ${position + 1} · drop card here"),
                color = if (highlighted) Color.White else HarmonyMuted,
                fontSize = 15.sp,
                lineHeight = 19.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        } else {
            RankingDraggableCard(
                raw = raw,
                label = label ?: raw,
                fromSlot = position,
                resolveSlot = resolveSlot,
                onHover = onHover,
                onDrop = { target -> onMove(raw, position, target) },
                compact = true,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RankingDraggableCard(
    raw: String,
    label: String,
    fromSlot: Int?,
    resolveSlot: (Offset?) -> Int?,
    onHover: (Int?) -> Unit,
    onDrop: (Int) -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false
) {
    var dragOffset by remember(raw, fromSlot) { mutableStateOf(Offset.Zero) }
    var sourceBounds by remember(raw, fromSlot) { mutableStateOf<DropRect?>(null) }
    var pointerInRoot by remember(raw, fromSlot) { mutableStateOf<Offset?>(null) }
    var dragging by remember(raw, fromSlot) { mutableStateOf(false) }
    val shape = RoundedCornerShape(if (compact) 16.dp else 20.dp)

    fun updateHover(pointer: Offset?) = onHover(resolveSlot(pointer))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 48.dp else 62.dp)
            .zIndex(if (dragging) 60f else 0f)
            .graphicsLayer {
                translationX = dragOffset.x
                translationY = dragOffset.y
                scaleX = if (dragging) 1.06f else 1f
                scaleY = if (dragging) 1.06f else 1f
                shadowElevation = if (dragging) 30f else 7f
            }
            .onGloballyPositioned { coordinates ->
                val rect = coordinates.boundsInRoot()
                sourceBounds = DropRect(rect.left, rect.top, rect.right, rect.bottom)
            }
            .pointerInput(raw, fromSlot) {
                detectDragGestures(
                    onDragStart = { localStart ->
                        dragging = true
                        dragOffset = Offset.Zero
                        pointerInRoot = sourceBounds?.let { bounds ->
                            Offset(bounds.left + localStart.x, bounds.top + localStart.y)
                        }
                        updateHover(pointerInRoot)
                    },
                    onDragCancel = {
                        dragging = false
                        dragOffset = Offset.Zero
                        pointerInRoot = null
                        onHover(null)
                    },
                    onDragEnd = {
                        resolveSlot(pointerInRoot)?.let(onDrop)
                        dragging = false
                        dragOffset = Offset.Zero
                        pointerInRoot = null
                        onHover(null)
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        dragOffset += amount
                        pointerInRoot = pointerInRoot?.plus(amount)
                        updateHover(pointerInRoot)
                    }
                )
            }
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        HarmonySurface,
                        HarmonyPurple.copy(alpha = if (dragging) 0.58f else 0.34f),
                        HarmonySurface2
                    )
                )
            )
            .border(
                if (dragging) 2.dp else 1.dp,
                if (dragging) HarmonyPinkSoft else HarmonyPink.copy(alpha = 0.34f),
                shape
            )
            .padding(horizontal = if (compact) 10.dp else 12.dp, vertical = if (compact) 8.dp else 11.dp)
            .testTag(if (fromSlot == null) "ranking_pool_${raw.hashCode()}" else "ranking_placed_$fromSlot"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(5.dp)
                .height(if (compact) 28.dp else 34.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.verticalGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
        )
        Spacer(Modifier.width(9.dp))
        Text(
            text = label,
            color = HarmonyText,
            fontSize = if (compact) 14.5.sp else 15.5.sp,
            lineHeight = if (compact) 18.sp else 20.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = if (compact) 2 else 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = "☰",
            color = if (dragging) HarmonyPinkSoft else HarmonyMuted,
            fontSize = if (compact) 20.sp else 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
