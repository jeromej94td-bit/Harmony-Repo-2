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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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

        QuestionInteractionKind.RANK_ORDER -> RankingSlotBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
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
    val configuration = context.resources.configuration
    val layoutMetrics = PersonAssignmentLayoutPolicy.metrics(
        screenHeightDp = configuration.screenHeightDp,
        fontScale = configuration.fontScale
    )
    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
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

    val dropRole: (String, PersonSide) -> Unit = { role, side ->
        assignments[role] = side
        hoveredSide = null
        triggerMiniVibration(context, 48L)
    }

    FullscreenMechanicShell(
        kicker = tr("👥 ROLLEN-DUELL", "👥 ROLE DUEL"),
        question = prompt,
        instruction = tr(
            "Ziehe jede Karte zu der Person, zu der sie am besten passt.",
            "Drag each card to the person it fits best."
        ),
        modifier = modifier.testTag("person_assignment_board")
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(layoutMetrics.columnGapDp.dp),
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
                    complete = complete,
                    layoutMetrics = layoutMetrics,
                    modifier = Modifier.weight(1f).fillMaxSize()
                )

                Column(
                    modifier = Modifier
                        .weight(1.08f)
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color.White.copy(alpha = 0.035f))
                        .border(1.dp, HarmonyLine, RoundedCornerShape(24.dp))
                        .padding(horizontal = 8.dp, vertical = layoutMetrics.centerVerticalPaddingDp.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(layoutMetrics.verticalSpacingDp.dp)
                ) {
                    Text(
                        text = tr("KARTEN", "CARDS"),
                        color = HarmonyPinkSoft,
                        fontSize = layoutMetrics.headerSizeSp.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    if (unassignedRoles.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tr("Alles verteilt", "All assigned"),
                                color = HarmonyPinkSoft,
                                fontSize = if (layoutMetrics.hideAvatarWhenComplete) 13.sp else 16.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        unassignedRoles.forEach { role ->
                            key(role) {
                                DraggableRoleChip(
                                    role = role,
                                    roleIndex = options.indexOf(role),
                                    userBounds = { userBounds },
                                    partnerBounds = { partnerBounds },
                                    onHover = { hoveredSide = it },
                                    onDrop = { side -> dropRole(role, side) },
                                    layoutMetrics = layoutMetrics
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
                    highlighted = hoveredSide == PersonSide.PARTNER,
                    targetTag = "assignment_target_partner",
                    onBounds = { partnerBounds = it },
                    userBounds = { userBounds },
                    partnerBounds = { partnerBounds },
                    onHover = { hoveredSide = it },
                    onDrop = dropRole,
                    complete = complete,
                    layoutMetrics = layoutMetrics,
                    modifier = Modifier.weight(1f).fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(layoutMetrics.submitGapDp.dp))
            PrimaryMechanicButton(
                text = if (complete) {
                    tr("Zuordnung speichern & weiter", "Save assignment & continue")
                } else {
                    tr("Verteile alle Karten", "Assign every card")
                },
                enabled = complete,
                onClick = {
                    triggerMiniVibration(context, 50L)
                    onPick(PersonAssignmentCodec.encode(options, assignments))
                },
                testTag = "assignment_submit"
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
    complete: Boolean,
    layoutMetrics: PersonAssignmentLayoutMetrics,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(26.dp)
    val accent = if (side == PersonSide.USER) HarmonyPink else HarmonyPurpleLight
    val showAvatar = !(complete && layoutMetrics.hideAvatarWhenComplete)

    Column(
        modifier = modifier
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        accent.copy(alpha = if (highlighted) 0.38f else 0.24f),
                        HarmonySurface2.copy(alpha = 0.96f),
                        HarmonyBg.copy(alpha = 0.94f)
                    )
                )
            )
            .border(
                if (highlighted) 2.4.dp else 1.2.dp,
                if (highlighted) HarmonyPinkSoft else Color.White.copy(alpha = 0.18f),
                shape
            )
            .onGloballyPositioned { coordinates ->
                val rect = coordinates.boundsInRoot()
                onBounds(DropRect(rect.left, rect.top, rect.right, rect.bottom))
            }
            .padding(horizontal = 9.dp, vertical = layoutMetrics.targetVerticalPaddingDp.dp)
            .testTag(targetTag),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(layoutMetrics.verticalSpacingDp.dp)
    ) {
        if (showAvatar) {
            ProfileAvatar(name = name, avatarPath = avatarPath, layoutMetrics = layoutMetrics)
        }
        Text(
            text = name,
            color = HarmonyText,
            fontSize = layoutMetrics.nameSizeSp.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        if (!layoutMetrics.hideAvatarWhenComplete) {
            Spacer(modifier = Modifier.height(2.dp))
        }
        if (roles.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color.White.copy(alpha = if (highlighted) 0.12f else 0.045f))
                    .border(
                        1.dp,
                        if (highlighted) HarmonyPinkSoft else HarmonyLine,
                        RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = tr("Hier ablegen", "Drop here"),
                    color = if (highlighted) HarmonyText else HarmonyMuted,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
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
                        assigned = true,
                        layoutMetrics = layoutMetrics
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileAvatar(
    name: String,
    avatarPath: String?,
    layoutMetrics: PersonAssignmentLayoutMetrics
) {
    Box(
        modifier = Modifier
            .size(layoutMetrics.avatarSizeDp.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
            .border(2.dp, Color.White.copy(alpha = 0.62f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        if (!avatarPath.isNullOrBlank()) {
            AsyncImage(
                model = avatarPath,
                contentDescription = name,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        } else {
            Text(
                text = name.trim().take(1).uppercase().ifBlank { "?" },
                color = Color.White,
                fontSize = layoutMetrics.avatarInitialSizeSp.sp,
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
    assigned: Boolean = false,
    layoutMetrics: PersonAssignmentLayoutMetrics
) {
    val density = LocalDensity.current
    val hitSlop = with(density) { 24.dp.toPx() }
    var dragOffset by remember(role) { mutableStateOf(Offset.Zero) }
    var sourceBounds by remember(role) { mutableStateOf<DropRect?>(null) }
    var pointerInRoot by remember(role) { mutableStateOf<Offset?>(null) }
    var dragging by remember(role) { mutableStateOf(false) }
    val shape = RoundedCornerShape(18.dp)

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
            .heightIn(
                min = if (assigned) {
                    layoutMetrics.assignedChipMinHeightDp.dp
                } else {
                    layoutMetrics.unassignedChipMinHeightDp.dp
                }
            )
            .zIndex(if (dragging) 40f else 0f)
            .graphicsLayer {
                translationX = dragOffset.x
                translationY = dragOffset.y
                scaleX = if (dragging) 1.055f else 1f
                scaleY = if (dragging) 1.055f else 1f
                shadowElevation = if (dragging) 28f else 6f
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
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        HarmonySurface,
                        HarmonyPurple.copy(alpha = if (dragging) 0.52f else 0.34f),
                        HarmonySurface2
                    )
                )
            )
            .border(
                if (dragging) 2.dp else 1.dp,
                if (dragging) HarmonyPinkSoft else HarmonyLine,
                shape
            )
            .padding(horizontal = 10.dp, vertical = layoutMetrics.chipVerticalPaddingDp.dp)
            .testTag("assignment_role_$roleIndex"),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(5.dp)
                .height(34.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(Brush.verticalGradient(listOf(HarmonyPink, HarmonyPurpleLight)))
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = contentText(role),
            color = HarmonyText,
            fontSize = if (assigned) layoutMetrics.assignedTextSizeSp.sp else layoutMetrics.unassignedTextSizeSp.sp,
            fontWeight = FontWeight.Bold,
            lineHeight = if (assigned) layoutMetrics.assignedLineHeightSp.sp else layoutMetrics.unassignedLineHeightSp.sp,
            textAlign = TextAlign.Start,
            maxLines = layoutMetrics.roleMaxLines,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "☰",
            color = if (dragging) HarmonyPinkSoft else HarmonyMuted,
            fontSize = if (assigned) layoutMetrics.assignedHandleSizeSp.sp else layoutMetrics.unassignedHandleSizeSp.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
