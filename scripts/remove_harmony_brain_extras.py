# Executed from remove_harmony_brain_feature.py before its final source guard.
# The parent script provides ROOT, MAIN, TEST, ANDROID_TEST, read, write, delete and re.


def remove_block_from_signature(source: str, signature: str) -> str:
    pos = source.find(signature)
    if pos < 0:
        return source
    line_start = source.rfind("\n", 0, pos) + 1
    brace = source.find("{", pos)
    if brace < 0:
        raise RuntimeError(f"Missing block brace for {signature}")
    depth = 0
    for i in range(brace, len(source)):
        if source[i] == "{":
            depth += 1
        elif source[i] == "}":
            depth -= 1
            if depth == 0:
                end = i + 1
                if end < len(source) and source[end] == "\n":
                    end += 1
                return source[:line_start] + source[end:]
    raise RuntimeError(f"Unclosed block for {signature}")


def remove_composable_function(source: str, signature: str) -> str:
    pos = source.find(signature)
    if pos < 0:
        return source
    annotation = source.rfind("@Composable", 0, pos)
    if annotation >= 0 and source[annotation:pos].strip() == "@Composable":
        start = annotation
    else:
        start = source.rfind("\n", 0, pos) + 1
    brace = source.find("{", pos)
    if brace < 0:
        raise RuntimeError(f"Missing function brace for {signature}")
    depth = 0
    for i in range(brace, len(source)):
        if source[i] == "{":
            depth += 1
        elif source[i] == "}":
            depth -= 1
            if depth == 0:
                end = i + 1
                while end < len(source) and source[end] == "\n":
                    end += 1
                return source[:start] + source[end:]
    raise RuntimeError(f"Unclosed function for {signature}")


# Preserve normal skip semantics; remove only the former Brain interaction side-channel.
skip_rel = "app/src/main/java/com/example/ui/HarmonyViewModelSkip.kt"
skip = read(skip_rel)
skip = re.sub(r"^import com\.example\.data\.repository\.HarmonyRepository\n", "", skip, flags=re.M)
skip = skip.replace(
    " * Advances the current runner without inventing an answer and records the explicit skip\n * as a Harmony Brain interaction. Keeping this separate from nextStep() prevents technical\n * navigation from being misclassified as a user skip.",
    " * Advances the current runner without inventing an answer. Keeping this separate from\n * nextStep() preserves skip semantics without any removed personalization side-channel.",
)
skip = re.sub(
    r"^\s*HarmonyRepository\(db, app\)\.recordBrainSkip\(packId, questionIndex\)\n",
    "",
    skip,
    flags=re.M,
)
write(skip_rel, skip)


# This file is exclusively the old Harmony-Brain personalized-game generator.
delete(MAIN / "util/GeminiGameGenerator.kt")

# Remove its hidden Developer Studio execution button.
dev_rel = "app/src/main/java/com/example/ui/screens/DevStudioScreen.kt"
dev = read(dev_rel)
dev = dev.replace(
    "    val context = LocalContext.current\n    val coroutineScope = rememberCoroutineScope()\n    var isGenerating by remember { mutableStateOf(false) }\n    var searchQuery",
    "    val context = LocalContext.current\n    var searchQuery",
    1,
)
dev_button = re.compile(
    r"\n        Button\(\n            onClick = \{\n                if \(!isGenerating\) \{.*?\n        \}\n\n        LazyRow\(",
    re.S,
)
dev, count = dev_button.subn("\n\n        LazyRow(", dev, count=1)
if count != 1:
    raise RuntimeError("Could not remove Developer Studio Brain generator button")
write(dev_rel, dev)


# Remove generated personal Brain games from the ordinary Games screen.
games_rel = "app/src/main/java/com/example/ui/screens/GamesScreen.kt"
games = read(games_rel)
games = re.sub(r"^import com\.example\.data\.brain\..*\n", "", games, flags=re.M)
games = re.sub(r"^\s*generatedGames: List<BrainGeneratedContentEntity> = emptyList\(\),\n", "", games, flags=re.M)
games = re.sub(r"^\s*brainEnabled: Boolean = false,\n", "", games, flags=re.M)
games = re.sub(r"^\s*onStartGeneratedGame: \(String\) -> Unit = \{\},\n", "", games, flags=re.M)
games = remove_block_from_signature(games, "if (brainEnabled && generatedGames.isNotEmpty()) {")
games = remove_composable_function(games, "fun GeneratedGameCard(")
write(games_rel, games)


# BrainSuggestionCard is the final, Brain-only block in VoiceComponents. Cutting from its
# documentation marker to EOF is safer than trying to parse nested lambdas/strings inside it.
voice_rel = "app/src/main/java/com/example/ui/components/VoiceComponents.kt"
voice = read(voice_rel)
voice = re.sub(r"^import com\.example\.data\.model\.BrainChatSuggestionItem\n", "", voice, flags=re.M)
card_marker = "\n/**\n * Rich Suggestion Card with Image URL, Title, Description, and Actions"
card_pos = voice.find(card_marker)
if card_pos >= 0:
    voice = voice[:card_pos].rstrip() + "\n"
else:
    voice = remove_composable_function(voice, "fun BrainSuggestionCard(")
write(voice_rel, voice)


# The old production-isolation task assumed archived Brain code must stay compiled with a false
# feature flag. Full removal is stricter and safer, so retire only those two obsolete assertions;
# all other source-isolation/Mischung guards remain unchanged.
gradle_rel = "app/build.gradle.kts"
gradle = read(gradle_rel)
obsolete_guard = '''    if (!homeScreen.contains("brainEnabled: Boolean = false")) {
      violations += "HomeScreen.kt lost the fail-closed archived Brain default"
    }
    if (!gamesScreen.contains("brainEnabled: Boolean = false")) {
      violations += "GamesScreen.kt lost the fail-closed archived Brain default"
    }

'''
if obsolete_guard not in gradle:
    raise RuntimeError("Could not locate obsolete fail-closed Brain Gradle guard")
gradle = gradle.replace(obsolete_guard, "", 1)
write(gradle_rel, gradle)


# Tests that exclusively target deleted generated-Brain APIs cannot remain active. The permanent
# negative regression contract is deliberately preserved even though it names forbidden symbols.
for root in [TEST, ANDROID_TEST]:
    if not root.exists():
        continue
    for test_file in list(root.rglob("*.kt")):
        if test_file.name == "ProductionHarmonyBrainRemovalContractTest.kt":
            continue
        body = test_file.read_text(encoding="utf-8")
        if any(symbol in body for symbol in [
            "GeminiGameGenerator",
            "BrainGeneratedContentEntity",
            "GeneratedGamePayload",
            "HarmonyGameNotifier",
            "BrainSuggestionCard",
        ]):
            delete(test_file)


extra_forbidden = [
    "GeminiGameGenerator",
    "BrainGeneratedContentEntity",
    "GeneratedGamePayload",
    "GeneratedGameCard",
    "generatedGames",
    "onStartGeneratedGame",
]
extra_leaks = []
for source_file in MAIN.rglob("*.kt"):
    body = source_file.read_text(encoding="utf-8")
    for symbol in extra_forbidden:
        if symbol in body:
            extra_leaks.append(f"{source_file.relative_to(ROOT)}: {symbol}")
if extra_leaks:
    raise SystemExit("Remaining hidden generated-Brain references:\n" + "\n".join(extra_leaks))

print("Hidden Brain developer/game paths removed and obsolete fail-closed Brain build guard retired.")
