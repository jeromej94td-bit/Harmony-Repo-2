package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.HarmonyDatabase
import com.example.data.model.HarmonyPacksData
import com.example.data.model.ProfileEntity
import com.example.ui.theme.HarmonyGold
import com.example.ui.theme.HarmonyPink
import com.example.ui.theme.HarmonyPinkSoft
import com.example.ui.theme.HarmonyPurple
import com.example.ui.theme.HarmonySurface2
import com.example.ui.tr
import com.example.ui.util.triggerMiniVibration
import kotlinx.coroutines.flow.flowOf

internal object ScenarioAdventurePresence {
    private var activeBoards by mutableStateOf(0)

    val isActive: Boolean
        get() = activeBoards > 0

    fun enter() {
        activeBoards += 1
    }

    fun leave() {
        activeBoards = (activeBoards - 1).coerceAtLeast(0)
    }
}

private data class ScenarioStylePresentation(
    val titleDe: String,
    val titleEn: String,
    val bodyDe: String,
    val bodyEn: String,
    val traitsDe: List<String>,
    val traitsEn: List<String>
)

private val scenarioStylePresentations = listOf(
    ScenarioStylePresentation(
        titleDe = "Die Macher",
        titleEn = "The Doers",
        bodyDe = "Ihr entscheidet direkt, pragmatisch und mit Blick auf das, was jetzt zählt.",
        bodyEn = "You decide directly, pragmatically, and focus on what matters right now.",
        traitsDe = listOf("direkt", "pragmatisch", "mutig"),
        traitsEn = listOf("direct", "pragmatic", "bold")
    ),
    ScenarioStylePresentation(
        titleDe = "Die Strategen",
        titleEn = "The Strategists",
        bodyDe = "Ihr denkt voraus, wägt gemeinsam ab und baut aus Entscheidungen einen Plan.",
        bodyEn = "You think ahead, weigh choices together, and turn decisions into a plan.",
        traitsDe = listOf("vorausschauend", "klar", "teamorientiert"),
        traitsEn = listOf("forward-looking", "clear", "team-minded")
    ),
    ScenarioStylePresentation(
        titleDe = "Die Genießer",
        titleEn = "The Enjoyers",
        bodyDe = "Ihr vergesst auch mitten im Abenteuer nicht, dass euer gemeinsamer Weg Freude machen darf.",
        bodyEn = "Even in the middle of an adventure, you remember that your shared path should feel good.",
        traitsDe = listOf("verbunden", "lebensfroh", "gelassen"),
        traitsEn = listOf("connected", "joyful", "easy-going")
    ),
    ScenarioStylePresentation(
        titleDe = "Das Spontan-Duo",
        titleEn = "The Spontaneous Duo",
        bodyDe = "Ihr reagiert flexibel, überraschend und macht aus unerwarteten Wendungen euren eigenen Weg.",
        bodyEn = "You react flexibly, embrace surprises, and turn unexpected twists into your own path.",
        traitsDe = listOf("spontan", "kreativ", "flexibel"),
        traitsEn = listOf("spontaneous", "creative", "flexible")
    )
)

private fun embeddedScenarioAdventurePacks(): List<ScenarioAdventurePackRef> = HarmonyPacksData.PACKS
    .asSequence()
    .filter { pack ->
        pack.cat.equals("h360_szenario", ignoreCase = true) ||
            pack.tags.any { it.equals("mechanik_szenario", ignoreCase = true) }
    }
    .filter { it.questions.isNotEmpty() }
    .map { pack ->
        ScenarioAdventurePackRef(
            packId = pack.id,
            chapters = pack.questions.map { question ->
                ScenarioAdventureChapterRef(
                    prompt = question.q,
                    options = question.options
                )
            }
        )
    }
    .toList()

private fun scenarioSceneEmoji(prompt: String, chapterIndex: Int): String = when {
    chapterIndex >= 7 -> "🏁"
    prompt.contains("insel", ignoreCase = true) -> "🌴"
    prompt.contains("reise", ignoreCase = true) || prompt.contains("unterwegs", ignoreCase = true) -> "🗺️"
    prompt.contains("streit", ignoreCase = true) -> "🌩️"
    prompt.contains("zukunft", ignoreCase = true) -> "🔭"
    prompt.contains("nacht", ignoreCase = true) -> "🌙"
    prompt.contains("geld", ignoreCase = true) || prompt.contains("budget", ignoreCase = true) -> "💰"
    else -> "🧭"
}

@Composable
internal fun ScenarioAdventureBoard(
    question: String,
    options: List<String>,
    selectedAnswer: String?,
    profile: ProfileEntity,
    onPick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    DisposableEffect(Unit) {
        ScenarioAdventurePresence.enter()
        onDispose { ScenarioAdventurePresence.leave() }
    }

    val context = LocalContext.current
    val embeddedPacks = remember { embeddedScenarioAdventurePacks() }
    val resolvedLocation = remember(question, options, embeddedPacks) {
        resolveScenarioAdventureLocation(
            packs = embeddedPacks,
            question = question,
            options = options
        )
    }
    val fallbackPack = remember(question, options) {
        ScenarioAdventurePackRef(
            packId = "",
            chapters = listOf(ScenarioAdventureChapterRef(question, options))
        )
    }
    val activePack = resolvedLocation?.pack ?: fallbackPack
    val chapterIndex = resolvedLocation?.chapterIndex ?: 0
    val chapterCount = activePack.chapters.size.coerceAtLeast(1)
    val isFinalChapter = chapterIndex == activePack.chapters.lastIndex

    val database = remember(context) { HarmonyDatabase.getInstance(context.applicationContext) }
    val persistedFlow = remember(resolvedLocation?.pack?.packId) {
        resolvedLocation?.pack?.packId
            ?.takeIf { it.isNotBlank() }
            ?.let { database.answerDao().getAnswersForPack(it) }
            ?: flowOf(emptyList())
    }
    val persistedAnswers by persistedFlow.collectAsState(initial = emptyList())
    val persistedByIndex = remember(persistedAnswers) {
        persistedAnswers.associate { it.questionIndex to it.answerText }
    }

    val items = mechanicOptions(options, profile)
    val prompt = mechanicPrompt(question, items, profile)
    var selected by remember(question, selectedAnswer) {
        mutableStateOf(selectedAnswer?.takeIf { answer -> items.any { it.raw == answer } })
    }
    var showIntro by remember(activePack.packId, chapterIndex) {
        mutableStateOf(chapterIndex == 0 && selectedAnswer == null)
    }
    var showFinale by remember(question) { mutableStateOf(false) }

    val pendingChoiceIndex = selected?.let { answer -> scenarioChoiceIndex(options, answer) }
    val completedRoute = remember(activePack.chapters, persistedByIndex, chapterIndex) {
        scenarioRouteIndexes(
            chapters = activePack.chapters,
            answers = persistedByIndex.filterKeys { it < chapterIndex }
        )
    }
    val projectedRoute = if (pendingChoiceIndex != null) completedRoute + pendingChoiceIndex else completedRoute
    val dominantStyle = scenarioDominantStyle(projectedRoute) ?: 0
    val style = scenarioStylePresentations[dominantStyle.coerceIn(scenarioStylePresentations.indices)]

    when {
        showIntro -> ScenarioAdventureIntro(
            chapterCount = chapterCount,
            onStart = {
                triggerMiniVibration(context, 34L)
                showIntro = false
            },
            modifier = modifier
        )

        showFinale -> ScenarioAdventureFinale(
            route = projectedRoute,
            style = style,
            onFinish = {
                val answer = selected
                if (answer != null) {
                    triggerMiniVibration(context, 42L)
                    onPick(answer)
                }
            },
            modifier = modifier
        )

        else -> FullscreenMechanicShell(
            kicker = tr("🎭 KAPITEL ${chapterIndex + 1}/$chapterCount", "🎭 CHAPTER ${chapterIndex + 1}/$chapterCount"),
            question = prompt,
            instruction = tr(
                "Entscheidet euch gemeinsam – eure Wahl wird Teil eures Weges.",
                "Choose together – your decision becomes part of your path."
            ),
            modifier = modifier.testTag("scenario_adventure_board"),
            headerVisual = {
                Text(
                    text = scenarioSceneEmoji(prompt, chapterIndex),
                    fontSize = 42.sp,
                    modifier = Modifier.testTag("scenario_adventure_scene")
                )
            }
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                LargeOptionGrid(
                    items = items,
                    selectedRaw = selected,
                    onSelect = { item ->
                        triggerMiniVibration(context, 34L)
                        selected = item.raw
                    },
                    modifier = Modifier.weight(1f),
                    tagPrefix = "scenario_adventure_option"
                )
                Spacer(Modifier.height(12.dp))
                PrimaryMechanicButton(
                    text = if (isFinalChapter) {
                        tr("Finale aufdecken", "Reveal finale")
                    } else {
                        tr("Entscheidung treffen", "Make decision")
                    },
                    enabled = selected != null,
                    onClick = {
                        val answer = selected
                        if (answer != null) {
                            triggerMiniVibration(context, 42L)
                            if (isFinalChapter) showFinale = true else onPick(answer)
                        }
                    },
                    testTag = "scenario_adventure_submit"
                )
            }
        }
    }
}

@Composable
private fun ScenarioAdventureIntro(
    chapterCount: Int,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    FullscreenMechanicShell(
        kicker = tr("🎭 ABENTEUER", "🎭 ADVENTURE"),
        question = tr("Euer Abenteuer beginnt", "Your adventure begins"),
        instruction = tr(
            "$chapterCount Kapitel. Jede Entscheidung schreibt euren gemeinsamen Weg weiter.",
            "$chapterCount chapters. Every decision continues your shared path."
        ),
        modifier = modifier.testTag("scenario_adventure_intro")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(132.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(HarmonyPink.copy(alpha = 0.48f), HarmonyPurple.copy(alpha = 0.44f), HarmonySurface2)
                        )
                    )
                    .border(2.dp, Color.White.copy(alpha = 0.24f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("🧭", fontSize = 58.sp)
            }
            Spacer(Modifier.height(24.dp))
            Text(
                text = tr("Keine richtige Antwort. Nur euer Weg.", "No right answer. Just your path."),
                color = HarmonyPinkSoft,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(24.dp))
            PrimaryMechanicButton(
                text = tr("Abenteuer starten", "Start adventure"),
                onClick = onStart,
                testTag = "scenario_adventure_start"
            )
        }
    }
}

@Composable
private fun ScenarioAdventureFinale(
    route: List<Int>,
    style: ScenarioStylePresentation,
    onFinish: () -> Unit,
    modifier: Modifier = Modifier
) {
    FullscreenMechanicShell(
        kicker = tr("🏁 FINALE", "🏁 FINALE"),
        question = tr("Euer Weg durch das Abenteuer", "Your path through the adventure"),
        instruction = tr("Aus euren Entscheidungen entsteht euer gemeinsamer Spielstil.", "Your decisions reveal your shared play style."),
        modifier = modifier.testTag("scenario_adventure_finale")
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                route.forEachIndexed { index, choice ->
                    val marker = listOf("⚡", "🧠", "💗", "✨")[choice.coerceIn(0, 3)]
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(HarmonyPurple.copy(alpha = 0.46f))
                            .border(1.dp, HarmonyPink.copy(alpha = 0.50f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(marker, fontSize = 13.sp)
                    }
                    if (index != route.lastIndex) {
                        Box(
                            modifier = Modifier
                                .height(2.dp)
                                .weight(1f, fill = false)
                                .fillMaxWidth(0.04f)
                                .background(HarmonyPink.copy(alpha = 0.45f))
                        )
                    }
                }
            }
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(26.dp))
                    .background(HarmonyPurple.copy(alpha = 0.25f))
                    .border(1.dp, HarmonyPink.copy(alpha = 0.36f), RoundedCornerShape(26.dp))
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("🏆", fontSize = 44.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    text = tr(style.titleDe, style.titleEn),
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    text = tr(style.bodyDe, style.bodyEn),
                    color = Color.White.copy(alpha = 0.80f),
                    fontSize = 15.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(14.dp))
                val traits = if (tr("de", "en") == "de") style.traitsDe else style.traitsEn
                Text(
                    text = traits.joinToString("  •  "),
                    color = HarmonyGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = tr("${route.size} Entscheidungen · 1 gemeinsamer Weg", "${route.size} decisions · 1 shared path"),
                    color = HarmonyPinkSoft,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }
            Spacer(Modifier.height(12.dp))
            PrimaryMechanicButton(
                text = tr("Abenteuer abschließen", "Finish adventure"),
                onClick = onFinish,
                testTag = "scenario_adventure_finish"
            )
        }
    }
}
