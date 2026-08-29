package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
 * Ranking questions use one compact vertical board: numbered drop lanes first, then the
 * full-width card pool. This keeps long answer labels readable on phone screens while retaining
 * the existing drag/drop and answer-encoding behavior.
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

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("ranking_slot_board")
    ) {
        val compactHeight = maxHeight < 620.dp
        val questionSize = if (compactHeight) 21.sp else 24.sp
        val questionLineHeight = if (compactHeight) 26.sp else 30.sp
        val sectionGap = if (compactHeight) 8.dp else 11.dp
        val rowGap = if (compactHeight) 6.dp else 8.dp

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = prompt,
                color = HarmonyText,
                fontSize = questionSize,
                lineHeight = questionLineHeight,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = if (compactHeight) 2.dp else 6.dp)
                    .testTag("ranking_question")
            )

            Spacer(Modifier.height(sectionGap))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.88f),
                verticalArrangement = Arrangement.spacedBy(rowGap)
            ) {
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

            Spacer(Modifier.height(sectionGap))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .height(1.dp)
                    .background(
                        Brush.horizontalGradient(
                            listOf(
                                Color.Transparent,
                                HarmonyPink.copy(alpha = 0.34f),
                                HarmonyPurpleLight.copy(alpha = 0.28f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Spacer(Modifier.height(sectionGap))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.08f),
                verticalArrangement = Arrangement.spacedBy(rowGap)
            ) {
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

                repeat((options.size - unassigned.size).coerceAtLeast(0)) { index ->
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .testTag("ranking_pool_spacer_$index")
                    )
                }
            }

            Spacer(Modifier.height(if (compactHeight) 8.dp else 12.dp))

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
                testTag = "ranking_submit",
                compact = compactHeight
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
    val laneShape = RoundedCornerShape(20.dp)
    val accent = if (position == 0) HarmonyPinkSoft else HarmonyPurpleLight

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .testTag("ranking_slot_$position"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        if (raw == null) {
                            listOf(
                                HarmonyPurple.copy(alpha = 0.34f),
                                HarmonySurface2.copy(alpha = 0.84f)
                            )
                        } else {
                            listOf(HarmonyPink, HarmonyPurpleLight)
                        }
                    )
                )
                .border(
                    width = if (highlighted) 1.8.dp else 1.dp,
                    color = if (highlighted) HarmonyPinkSoft else accent.copy(alpha = 0.58f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${position + 1}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .heightIn(min = 50.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            HarmonySurface.copy(alpha = if (highlighted) 0.72f else 0.44f),
                            HarmonyPurple.copy(alpha = if (highlighted) 0.34f else 0.17f),
                            HarmonyBg.copy(alpha = 0.70f)
                        )
                    ),
                    laneShape
                )
                .border(
                    width = if (highlighted) 1.8.dp else 1.dp,
                    color = if (highlighted) HarmonyPinkSoft else HarmonyPink.copy(alpha = 0.32f),
                    shape = laneShape
                )
                .onGloballyPositioned { coordinates ->
                    val rect = coordinates.boundsInRoot()
                    onBounds(DropRect(rect.left, rect.top, rect.right, rect.bottom))
                }
                .padding(4.dp)
                .testTag("ranking_slot_lane_$position"),
            contentAlignment = Alignment.CenterStart
        ) {
            if (raw != null) {
                RankingDraggableCard(
                    raw = raw,
                    label = label ?: raw,
                    fromSlot = position,
                    resolveSlot = resolveSlot,
                    onHover = onHover,
                    onDrop = { target -> onMove(raw, position, target) },
                    compact = true,
                    modifier = Modifier.fillMaxSize()
                )
            }
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
    val shape = RoundedCornerShape(if (compact) 16.dp else 19.dp)

    fun updateHover(pointer: Offset?) = onHover(resolveSlot(pointer))

    Row(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = if (compact) 42.dp else 54.dp)
            .zIndex(if (dragging) 60f else 0f)
            .graphicsLayer {
                translationX = dragOffset.x
                translationY = dragOffset.y
                scaleX = if (dragging) 1.045f else 1f
                scaleY = if (dragging) 1.045f else 1f
                shadowElevation = if (dragging) 28f else 6f
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
                        HarmonyPurple.copy(alpha = if (dragging) 0.52f else 0.30f),
                        HarmonySurface2
                    )
                )
            )
            .border(
                if (dragging) 1.8.dp else 1.dp,
                if (dragging) HarmonyPinkSoft else HarmonyPink.copy(alpha = 0.46f),
                shape
            )
            .padding(
                horizontal = if (compact) 8.dp else 11.dp,
                vertical = if (compact) 6.dp else 8.dp
            )
            .testTag(if (fromSlot == null) "ranking_pool_${raw.hashCode()}" else "ranking_placed_$fromSlot"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(5.dp)
                .height(if (compact) 26.dp else 32.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.verticalGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
        )

        Spacer(Modifier.width(if (compact) 7.dp else 10.dp))

        Text(
            text = label,
            color = HarmonyText,
            fontSize = if (compact) 14.sp else 16.sp,
            lineHeight = if (compact) 17.sp else 20.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = if (compact) 3 else 4,
            overflow = TextOverflow.Clip,
            modifier = Modifier.weight(1f)
        )

        Spacer(Modifier.width(6.dp))

        Text(
            text = "☰",
            color = if (dragging) HarmonyPinkSoft else HarmonyMuted,
            fontSize = if (compact) 18.sp else 22.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
