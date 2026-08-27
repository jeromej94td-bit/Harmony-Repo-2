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
    var userBounds by remember { mutableStateOf<DropRect?>(null) }
    var partnerBounds by remember { mutableStateOf<DropRect?>(null) }
    var hoveredSide by remember { mutableStateOf<PersonSide?>(null) }

    val userRoles = options.filter { assignments[it] == PersonSide.USER }
    val partnerRoles = options.filter { assignments[it] == PersonSide.PARTNER }
    val unassignedRoles = options.filter { assignments[it] == null }
    val complete = options.isNotEmpty() && assignments.keys.containsAll(options)
    val missingCount = (options.size - assignments.size).coerceAtLeast(0)

    val dropRole: (String, PersonSide) -> Unit = { role, side ->
        assignments[role] = side
        hoveredSide = null
        triggerMiniVibration(context, 48L)
    }

    InteractionShell(
        question = compactInteractionQuestion(question, options),
        subtitle = tr(
            "Ziehe jede Rolle zu der Person, die sie bei euch eher übernimmt.",
            "Drag every role to the person who is more likely to take it on."
        ),
        modifier = modifier.testTag("person_assignment_board")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.Top
        ) {
            AssignmentTarget(
                name = profile.userName,
                avatarPath = profile.userAvatarPath,
                roles = userRoles,
                options = options,
                side = PersonSide.USER,
                highlighted = hoveredSide == PersonSide.USER,
                targetTag = "assignment_target_user",
                onBounds = { userBounds = it },
                userBounds = { userBounds },
                partnerBounds = { partnerBounds },
                onHover = { hoveredSide = it },
                onDrop = dropRole,
                modifier = Modifier.weight(1f)
            )

            AssignmentTarget(
                name = profile.partnerName,
                avatarPath = profile.partnerAvatarPath,
                roles = partnerRoles,
                options = options,
                side = PersonSide.PARTNER,
                highlighted = hoveredSide == PersonSide.PARTNER,
                targetTag = "assignment_target_partner",
                onBounds = { partnerBounds = it },
                userBounds = { userBounds },
                partnerBounds = { partnerBounds },
                onHover = { hoveredSide = it },
                onDrop = dropRole,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(22.dp))
        Text(
            text = if (unassignedRoles.isEmpty()) tr("Alle Rollen zugeordnet", "All roles assigned") else tr("Rollen zuordnen", "Assign roles"),
            color = if (unassignedRoles.isEmpty()) HarmonyPinkSoft else HarmonyText,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(12.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            unassignedRoles.forEach { role ->
                key(role) {
                    DraggableRoleChip(
                        role = role,
                        roleIndex = options.indexOf(role),
                        userBounds = { userBounds },
                        partnerBounds = { partnerBounds },
                        onHover = { hoveredSide = it },
                        onDrop = { side -> dropRole(role, side) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            enabled = complete,
            onClick = {
                triggerMiniVibration(context, 50L)
                onPick(PersonAssignmentCodec.encode(options, assignments))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .testTag("assignment_submit"),
            shape = RoundedCornerShape(21.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = HarmonyPink,
                disabledContainerColor = Color.White.copy(alpha = 0.09f),
                disabledContentColor = HarmonyMuted
            )
        ) {
            Text(
                text = if (complete) {
                    tr("Alle Rollen sind verteilt ✓", "All roles are assigned ✓")
                } else {
                    tr("Noch $missingCount Rollen verteilen", "$missingCount roles left to assign")
                },
                fontSize = 15.5.sp,
                fontWeight = FontWeight.ExtraBold
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
    highlighted: Boolean,
    targetTag: String,
    onBounds: (DropRect) -> Unit,
    userBounds: () -> DropRect?,
    partnerBounds: () -> DropRect?,
    onHover: (PersonSide?) -> Unit,
    onDrop: (String, PersonSide) -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(26.dp)
    val accent = if (side == PersonSide.USER) HarmonyPink else HarmonyPurpleLight

    Column(
        modifier = modifier
            .heightIn(min = 190.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = if (highlighted) 0.38f else 0.20f),
                        HarmonySurface2.copy(alpha = 0.96f),
                        HarmonyBg.copy(alpha = 0.94f)
                    )
                ),
                shape = shape
            )
            .border(
                width = if (highlighted) 2.4.dp else 1.3.dp,
                color = if (highlighted) HarmonyPinkSoft else Color.White.copy(alpha = 0.20f),
                shape = shape
            )
            .onGloballyPositioned { coordinates ->
                val rect = coordinates.boundsInRoot()
                onBounds(DropRect(rect.left, rect.top, rect.right, rect.bottom))
            }
            .padding(horizontal = 10.dp, vertical = 14.dp)
            .testTag(targetTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        ProfileAvatar(name = name, avatarPath = avatarPath)
        Text(
            text = name,
            color = HarmonyText,
            fontSize = 15.5.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        if (roles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .background(Color.White.copy(alpha = if (highlighted) 0.11f else 0.045f), RoundedCornerShape(16.dp))
                    .border(1.dp, if (highlighted) HarmonyPinkSoft else HarmonyLine, RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tr("Hier ablegen", "Drop here"),
                    color = if (highlighted) HarmonyText else HarmonyMuted,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            roles.forEach { role ->
                key(role) {
                    DraggableRoleChip(
                        role = role,
                        roleIndex = options.indexOf(role),
                        userBounds = userBounds,
                        partnerBounds = partnerBounds,
                        onHover = onHover,
                        onDrop = { target -> onDrop(role, target) },
                        assigned = true
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(name: String, avatarPath: String?) {
    Box(
        modifier = Modifier
            .size(82.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
            .border(2.dp, Color.White.copy(alpha = 0.70f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarPath.isNullOrBlank()) {
            AsyncImage(
                model = avatarPath,
                contentDescription = name,
                modifier = Modifier
                    .size(82.dp)
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = name.trim().take(1).uppercase().ifBlank { "?" },
                color = Color.White,
                fontSize = 29.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
private fun DraggableRoleChip(
    role: String,
    roleIndex: Int,
    userBounds: () -> DropRect?,
    partnerBounds: () -> DropRect?,
    onHover: (PersonSide?) -> Unit,
    onDrop: (PersonSide) -> Unit,
    assigned: Boolean = false
) {
    val density = LocalDensity.current
    val hitSlop = with(density) { 24.dp.toPx() }
    var dragOffset by remember(role) { mutableStateOf(Offset.Zero) }
    var sourceBounds by remember(role) { mutableStateOf<DropRect?>(null) }
    var pointerInRoot by remember(role) { mutableStateOf<Offset?>(null) }
    var dragging by remember(role) { mutableStateOf(false) }
    val shape = RoundedCornerShape(19.dp)

    fun updateHover(pointer: Offset?) {
        if (pointer == null) {
            onHover(null)
            return
        }
        onHover(
            resolvePersonDrop(
                pointerX = pointer.x,
                pointerY = pointer.y,
                userBounds = userBounds(),
                partnerBounds = partnerBounds(),
                hitSlop = hitSlop
            )
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = if (assigned) 54.dp else 62.dp)
            .zIndex(if (dragging) 40f else 0f)
            .graphicsLayer {
                translationX = dragOffset.x
                translationY = dragOffset.y
                scaleX = if (dragging) 1.045f else 1f
                scaleY = if (dragging) 1.045f else 1f
                shadowElevation = if (dragging) 28f else 7f
            }
            .onGloballyPositioned { coordinates ->
                val rect = coordinates.boundsInRoot()
                sourceBounds = DropRect(rect.left, rect.top, rect.right, rect.bottom)
            }
            .pointerInput(role) {
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
                        updateHover(null)
                    },
                    onDragEnd = {
                        val pointer = pointerInRoot
                        val target = pointer?.let {
                            resolvePersonDrop(
                                pointerX = it.x,
                                pointerY = it.y,
                                userBounds = userBounds(),
                                partnerBounds = partnerBounds(),
                                hitSlop = hitSlop
                            )
                        }
                        if (target != null) onDrop(target)
                        dragging = false
                        dragOffset = Offset.Zero
                        pointerInRoot = null
                        updateHover(null)
                    },
                    onDrag = { change, amount ->
                        change.consume()
                        dragOffset += amount
                        pointerInRoot = pointerInRoot?.plus(amount)
                        updateHover(pointerInRoot)
                    }
                )
            }
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        HarmonySurface2,
                        HarmonyPurple.copy(alpha = if (dragging) 0.56f else 0.34f),
                        HarmonyPink.copy(alpha = if (dragging) 0.28f else 0.12f),
                        HarmonySurface
                    )
                ),
                shape = shape
            )
            .border(
                width = if (dragging) 2.2.dp else 1.4.dp,
                color = if (dragging) HarmonyPinkSoft else HarmonyPink.copy(alpha = 0.48f),
                shape = shape
            )
            .padding(horizontal = if (assigned) 10.dp else 15.dp, vertical = 11.dp)
            .testTag("assignment_role_$roleIndex"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(34.dp)
                .background(Brush.verticalGradient(listOf(HarmonyPink, HarmonyPurpleLight)), RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.width(if (assigned) 7.dp else 12.dp))
        Text(
            text = contentText(role),
            color = HarmonyText,
            fontSize = if (assigned) 12.5.sp else 15.5.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = if (assigned) 15.sp else 19.sp,
            textAlign = TextAlign.Start,
            maxLines = if (assigned) 3 else 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = "☰",
            color = if (dragging) HarmonyPinkSoft else HarmonyMuted,
            fontSize = if (assigned) 19.sp else 24.sp,
            fontWeight = FontWeight.Bold
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
        question = compactInteractionQuestion(question, options),
        subtitle = tr(
            "Halte eine Karte gedrückt und ziehe sie an die passende Position.",
            "Hold a card and drag it into the right position."
        ),
        modifier = modifier.testTag("ranking_drag_board")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(13.dp)) {
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
                                triggerMiniVibration(context, 28L)
                            }
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            enabled = order.isNotEmpty(),
            onClick = {
                triggerMiniVibration(context, 48L)
                onPick(RankingAnswerCodec.encode(order.toList()))
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp)
                .testTag("ranking_submit"),
            shape = RoundedCornerShape(21.dp),
            colors = ButtonDefaults.buttonColors(containerColor = HarmonyPink)
        ) {
            Text(
                text = tr("Reihenfolge speichern", "Save order"),
                fontSize = 15.5.sp,
                fontWeight = FontWeight.ExtraBold
            )
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
    val threshold = with(density) { 58.dp.toPx() }
    var dragY by remember(item) { mutableStateOf(0f) }
    var dragging by remember(item) { mutableStateOf(false) }
    val shape = RoundedCornerShape(22.dp)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .zIndex(if (dragging) 30f else 0f)
            .graphicsLayer {
                translationY = dragY
                scaleX = if (dragging) 1.025f else 1f
                scaleY = if (dragging) 1.025f else 1f
                shadowElevation = if (dragging) 24f else 7f
            }
            .pointerInput(item, itemCount) {
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
                        while (dragY > threshold) {
                            onMove(1)
                            dragY -= threshold
                        }
                        while (dragY < -threshold) {
                            onMove(-1)
                            dragY += threshold
                        }
                    }
                )
            }
            .background(
                brush = Brush.horizontalGradient(
                    listOf(
                        HarmonySurface2,
                        HarmonyPurple.copy(alpha = if (dragging) 0.58f else 0.34f),
                        HarmonyPink.copy(alpha = if (dragging) 0.30f else 0.12f),
                        HarmonySurface
                    )
                ),
                shape = shape
            )
            .border(
                if (dragging) 2.dp else 1.4.dp,
                if (dragging) HarmonyPinkSoft else HarmonyPink.copy(alpha = 0.42f),
                shape
            )
            .padding(horizontal = 16.dp, vertical = 13.dp)
            .testTag("ranking_item_$originalIndex"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight))),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${position + 1}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = contentText(item),
            color = HarmonyText,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "☰",
            color = if (dragging) HarmonyPinkSoft else HarmonyMuted,
            fontSize = 25.sp,
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
    val shape = RoundedCornerShape(30.dp)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.radialGradient(
                    listOf(
                        HarmonyPurple.copy(alpha = 0.50f),
                        HarmonyPink.copy(alpha = 0.17f),
                        HarmonySurface2.copy(alpha = 0.98f),
                        HarmonyBg
                    )
                )
            )
            .border(1.5.dp, HarmonyPink.copy(alpha = 0.52f), shape)
            .padding(horizontal = 18.dp, vertical = 22.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = contentText(question),
            color = HarmonyText,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 29.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(9.dp))
        Text(
            text = subtitle,
            color = HarmonyMuted,
            fontSize = 13.5.sp,
            lineHeight = 18.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(20.dp))
        content()
    }
}
