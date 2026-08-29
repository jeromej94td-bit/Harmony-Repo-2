package com.example.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.model.FullscreenGameMechanicKind
import com.example.data.model.ProfileEntity
import com.example.data.model.QuestionInteractionKind

/**
 * Single rendering entry point for question mechanics that own the complete interaction surface.
 * The quiz runner must not render its generic question card or numbered answer buttons alongside
 * these boards; each mechanic is responsible for its own large prompt, controls and reveal flow.
 */
@Composable
internal fun FullscreenQuestionMechanicBoard(
    kind: FullscreenGameMechanicKind,
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    when (kind) {
        FullscreenGameMechanicKind.RANK_ORDER -> QuestionInteractionBoard(
            kind = QuestionInteractionKind.RANK_ORDER,
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.PERSON_ASSIGNMENT -> QuestionInteractionBoard(
            kind = QuestionInteractionKind.PERSON_ASSIGNMENT,
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.PARTNER_PREDICTION -> PartnerPredictionBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.SECRET_CHOICE -> SecretChoiceBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.SCALE_MATCH -> ScaleMatchBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.WHO_WOULD -> WhoWouldBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.MEMORY_MATCH -> MemoryMatchBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.SCENARIO -> ScenarioBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.PRIORITY_POKER -> PriorityPokerBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.MATCH_TOURNAMENT -> MatchTournamentBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )

        FullscreenGameMechanicKind.DEEP_TALK -> DeepTalkBoard(
            question = question,
            options = options,
            selectedAnswer = selectedAnswer,
            profile = profile,
            onPick = onPick,
            modifier = modifier
        )
    }
}
