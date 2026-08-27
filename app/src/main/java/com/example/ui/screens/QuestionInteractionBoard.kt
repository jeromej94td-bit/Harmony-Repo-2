package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.data.model.PersonAssignmentCodec
import com.example.data.model.PersonSide
import com.example.data.model.ProfileEntity
import com.example.data.model.QuestionInteractionKind
import com.example.data.model.RankingAnswerCodec
import com.example.ui.contentText
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

@Composable
internal fun QuestionInteractionBoard(
    kind: QuestionInteractionKind,
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (kind) {
        QuestionInteractionKind.PERSON_ASSIGNMENT -> PersonAssignmentBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        QuestionInteractionKind.RANK_ORDER -> RankingDragBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            onPick = onPick,
            modifier = modifier
        )

        QuestionInteractionKind.STANDARD -> Unit
    }
}

@Composable
private fun PersonAssignmentBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val initialAssignments = remember(selectedAnswer, options) {
        selectedAnswer?.let { PersonAssignmentCodec.decode(it, options) }.orEmpty()
    }
    val assignments = remember(selectedAnswer, options) {
        mutableStateMapOf<String, PersonSide>().apply { putAll(initialAssignments) }
    }
    var userBounds by remember { mutableStateOf<Rect?>(null) }
    var partnerBounds by remember { mutableStateOf<Rect?>(null) }

    val userRoles = options.filter { assignments[it] == PersonSide.USER }
    val partnerRoles = options.filter { assignments[it] == PersonSide.PARTNER }
    val unassignedRoles = options.filter { assignments[it] == null }
    val complete = options.isNotEmpty() && assignments.keys.containsAll(options)

    InteractionShell(
        question = question,
        subtitle = tr(
            "Ziehe jede Rolle zu der Person, die sie bei euch eher übernimmt.",
            "Drag every role to the person who is more likely to take it on."
        ),
        modifier = modifier.testTag("person_assignment_board")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.Top
        ) {
            AssignmentTarget(
                name = profile.userName,
                avatarPath = profile.userAvatarPath,
                roles = userRoles,
                options = options,
                side = PersonSide.USER,
                targetTag = "assignment_target_user",
                onBounds = { userBounds = it },
                userBounds = { userBounds },
                partnerBounds = { partnerBounds },
                onDrop = { role, side ->
                    assignments[role] = side
                    triggerMiniVibration(context, 38L)
                },
                modifier = Modifier.weight(1f)
            )

            Column(
                modifier = Modifier
                    .weight(1.05f)
                    .heightIn(min = 235.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = tr("Rollen", "Roles"),
                    color = HarmonyMuted,
                    fontSize = 10.5.sp,
                    fontWeight = FontWeight.Bold
                )
                if (unassignedRoles.isEmpty()) {
                    Text(
                        text = tr("Alles verteilt", "All assigned"),
                        color = HarmonyPinkSoft,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 28.dp)
                    )
                } else {
                    unassignedRoles.forEach { role ->
                        key(role) {
                            DraggableRoleChip(
                                role = role,
                                roleIndex = options.indexOf(role),
                                userBounds = { userBounds },
                                partnerBounds = { partnerBounds },
                                onDrop = { side ->
                                    assignments[role] = side
                                    triggerMiniVibration(context, 38L)
                                }
                            )
                        }
                    }
                }
            }

            AssignmentTarget(
                name = profile.partnerName,
                avatarPath = profile.partnerAvatarPath,
                roles = partnerRoles,
                options = options,
                side = PersonSide.PARTNER,
                targetTag = "assignment_target_partner",
                onBounds = { partnerBounds = it },
                userBounds = { userBounds },
                partnerBounds = { partnerBounds },
                onDrop = { role, side ->
                    assignments[role] = side
                    triggerMiniVibration(context, 38L)
                },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            enabled = complete,
            onClick = {
                triggerMiniVibration(context, 45L)
                onPick(PersonAssignmentCodec.encode(options, assignments))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("assignment_submit"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HarmonyPink,
                disabledContainerColor = Color.White.copy(alpha = 0.08f),
                disabledContentColor = HarmonyMuted
            )
        ) {
            Text(
                text = if (complete) tr("Zuordnung speichern", "Save assignment") else tr("Verteile alle Rollen", "Assign every role"),
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun AssignmentTarget(
    name: String,
    avatarPath: String?,
    roles: List<String>,
    options: List<String>,
    side: PersonSide,
    targetTag: String,
    onBounds: (Rect) -> Unit,
    userBounds: () -> Rect?,
    partnerBounds: () -> Rect?,
    onDrop: (String, PersonSide) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(22.dp)
    Column(
        modifier = modifier
            .heightIn(min = 235.dp)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        if (side == PersonSide.USER) HarmonyPink.copy(alpha = 0.19f) else HarmonyPurple.copy(alpha = 0.22f),
                        HarmonySurface2.copy(alpha = 0.95f),
                        HarmonyBg.copy(alpha = 0.92f)
                    )
                )
            )
            .border(1.dp, Color.White.copy(alpha = 0.16f), shape)
            .onGloballyPositioned { onBounds(it.boundsInRoot()) }
            .padding(horizontal = 7.dp, vertical = 10.dp)
            .testTag(targetTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(7.dp)
    ) {
        ProfileAvatar(name = name, avatarPath = avatarPath)
        Text(
            text = name,
            color = HarmonyText,
            fontSize = 11.5.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(modifier = Modifier.height(2.dp))
        roles.forEach { role ->
            key(role) {
                DraggableRoleChip(
                    role = role,
                    roleIndex = options.indexOf(role),
                    userBounds = userBounds,
                    partnerBounds = partnerBounds,
                    onDrop = { target -> onDrop(role, target) }
                )
            }
        }
    }
}

@Composable
private fun ProfileAvatar(name: String, avatarPath: String?) {
    Box(
        modifier = Modifier
            .size(54.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
            .border(1.5.dp, Color.White.copy(alpha = 0.55f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarPath.isNullOrBlank()) {
            AsyncImage(
                model = avatarPath,
                contentDescription = name,
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = name.trim().take(1).uppercase().ifBlank { "?" },
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun DraggableRoleChip(
    role: String,
    roleIndex: Int,
    userBounds: () -> Rect?,
    partnerBounds: () -> Rect?,
    onDrop: (PersonSide) -> Unit
) {
    var dragOffset by remember(role) { mutableStateOf(Offset.Zero) }
    var sourceBounds by remember(role) { mutableStateOf<Rect?>(null) }
    var dragging by remember(role) { mutableStateOf(false) }
    val shape = RoundedCornerShape(15.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (dragging) 20f else 0f)
            .graphicsLayer {
                translationX = dragOffset.x
                translationY = dragOffset.y
                scaleX = if (dragging) 1.06f else 1f
                scaleY = if (dragging) 1.06f else 1f
                shadowElevation = if (dragging) 20f else 4f
            }
            .onGloballyPositioned { sourceBounds = it.boundsInRoot() }
            .pointerInput(role) {
                detectDragGestures(
                    onDragStart = {
                        dragging = true
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        dragging = false
                        dragOffset = Offset.Zero
                    },
                    onDragEnd = {
                        val dropPoint = sourceBounds?.center?.plus(dragOffset)
                        when {
                            dropPoint != null && userBounds()?.contains(dropPoint) == true -> onDrop(PersonSide.USER)
                            dropPoint != null && partnerBounds()?.contains(dropPoint) == true -> onDrop(PersonSide.PARTNER)
                        }
                        dragging = false
                        dragOffset = Offset.Zero
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        dragOffset += amount
                    }
                )
            }
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(HarmonySurface, HarmonyPurple.copy(alpha = 0.32f), HarmonySurface2)
                )
            )
            .border(
                if (dragging) 1.7.dp else 1.dp,
                if (dragging) HarmonyPinkSoft else HarmonyLine,
                shape
            )
            .padding(horizontal = 7.dp, vertical = 10.dp)
            .testTag("assignment_role_$roleIndex"),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = contentText(role),
            color = HarmonyText,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RankingDragBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val initialOrder = remember(selectedAnswer, options) {
        selectedAnswer?.let { RankingAnswerCodec.decode(it, options) } ?: options
    }
    val order = remember(selectedAnswer, options) {
        mutableStateListOf<String>().apply { addAll(initialOrder) }
    }

    InteractionShell(
        question = question,
        subtitle = tr(
            "Halte eine Karte gedrückt und ziehe sie an die passende Position.",
            "Hold a card and drag it into the right position."
        ),
        modifier = modifier.testTag("ranking_drag_board")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
            order.toList().forEach { item ->
                key(item) {
                    RankingDragItem(
                        item = item,
                        originalIndex = options.indexOf(item),
                        position = order.indexOf(item),
                        itemCount = order.size,
                        onMove = { delta ->
                            val current = order.indexOf(item)
                            val target = (current + delta).coerceIn(0, order.lastIndex)
                            if (current != target) {
                                order.removeAt(current)
                                order.add(target, item)
                                triggerMiniVibration(context, 24L)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            enabled = order.isNotEmpty(),
            onClick = {
                triggerMiniVibration(context, 45L)
                onPick(RankingAnswerCodec.encode(order.toList()))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("ranking_submit"),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
        ) {
            Text(text = tr("Reihenfolge speichern", "Save order"), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun RankingDragItem(
    item: String,
    originalIndex: Int,
    position: Int,
    itemCount: Int,
    onMove: (Int) -> Unit
) {
    val density = LocalDensity.current
    val threshold = with(density) { 47.dp.toPx() }
    var dragY by remember(item) { mutableStateOf(0f) }
    var dragging by remember(item) { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .zIndex(if (dragging) 15f else 0f)
            .graphicsLayer {
                translationY = dragY
                scaleX = if (dragging) 1.025f else 1f
                scaleY = if (dragging) 1.025f else 1f
                shadowElevation = if (dragging) 18f else 3f
            }
            .pointerInput(item, position, itemCount) {
                detectDragGestures(
                    onDragStart = {
                        dragging = true
                        dragY = 0f
                    },
                    onDragCancel = {
                        dragging = false
                        dragY = 0f
                    },
                    onDragEnd = {
                        dragging = false
                        dragY = 0f
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        dragY += amount.y
                        if (dragY > threshold && position < itemCount - 1) {
                            onMove(1)
                            dragY -= threshold
                        } else if (dragY < -threshold && position > 0) {
                            onMove(-1)
                            dragY += threshold
                        }
                    }
                )
            }
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        HarmonySurface,
                        HarmonyPurple.copy(alpha = if (dragging) 0.42f else 0.23f),
                        HarmonySurface2
                    )
                )
            )
            .border(if (dragging) 1.5.dp else 1.dp, if (dragging) HarmonyPinkSoft else HarmonyLine, shape)
            .padding(horizontal = 12.dp, vertical = 13.dp)
            .testTag("ranking_item_$originalIndex"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${position + 1}",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = contentText(item),
            color = HarmonyText,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = "☰",
            color = HarmonyMuted,
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InteractionShell(
    question: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    val shape = RoundedCornerShape(28.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.radialGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.45f),
                        HarmonyPink.copy(alpha = 0.14f),
                        HarmonySurface2.copy(alpha = 0.97f),
                        HarmonyBg
                    )
                )
            )
            .border(1.2.dp, HarmonyPink.copy(alpha = 0.42f), shape)
            .padding(horizontal = 12.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = contentText(question),
            color = HarmonyText,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 24.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            text = subtitle,
            color = HarmonyMuted,
            fontSize = 11.5.sp,
            lineHeight = 15.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(15.dp))
        content()
    }
}
