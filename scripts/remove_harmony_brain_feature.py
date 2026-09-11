from pathlib import Path
import re
import shutil

ROOT = Path(__file__).resolve().parents[1]
MAIN = ROOT / "app/src/main/java/com/example"
TEST = ROOT / "app/src/test/java"
ANDROID_TEST = ROOT / "app/src/androidTest/java"


def read(rel: str) -> str:
    return (ROOT / rel).read_text(encoding="utf-8")


def write(rel: str, text: str) -> None:
    path = ROOT / rel
    path.write_text(text, encoding="utf-8")
    print("updated", rel)


def delete(path: Path) -> None:
    if path.is_dir():
        shutil.rmtree(path)
        print("deleted dir", path.relative_to(ROOT))
    elif path.exists():
        path.unlink()
        print("deleted", path.relative_to(ROOT))


def remove_function(text: str, signature: str) -> str:
    start = text.find(signature)
    if start < 0:
        return text
    # Include annotations/comments immediately above only when they are adjacent.
    line_start = text.rfind("\n", 0, start) + 1
    brace = text.find("{", start)
    if brace < 0:
        raise RuntimeError(f"No body for {signature}")
    depth = 0
    in_string = False
    escaped = False
    for i in range(brace, len(text)):
        c = text[i]
        if in_string:
            if escaped:
                escaped = False
            elif c == "\\":
                escaped = True
            elif c == '"':
                in_string = False
            continue
        if c == '"':
            in_string = True
        elif c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                end = i + 1
                while end < len(text) and text[end] in " \t":
                    end += 1
                if end < len(text) and text[end] == "\n":
                    end += 1
                return text[:line_start] + text[end:]
    raise RuntimeError(f"Unclosed body for {signature}")


def remove_if_block_after_marker(text: str, marker: str, if_token: str) -> str:
    marker_pos = text.find(marker)
    if marker_pos < 0:
        return text
    start = text.find(if_token, marker_pos)
    if start < 0:
        raise RuntimeError(f"Missing {if_token} after {marker}")
    brace = text.find("{", start)
    depth = 0
    in_string = False
    escaped = False
    for i in range(brace, len(text)):
        c = text[i]
        if in_string:
            if escaped:
                escaped = False
            elif c == "\\":
                escaped = True
            elif c == '"':
                in_string = False
            continue
        if c == '"':
            in_string = True
        elif c == "{":
            depth += 1
        elif c == "}":
            depth -= 1
            if depth == 0:
                end = i + 1
                while end < len(text) and text[end] in " \t\n":
                    end += 1
                return text[:marker_pos] + text[end:]
    raise RuntimeError(f"Unclosed block after {marker}")


# 1) Delete dedicated feature implementation and feature-specific tests.
delete(MAIN / "data/brain")
for rel in [
    "app/src/main/java/com/example/data/HarmonyBrainEngine.kt",
    "app/src/main/java/com/example/data/SupabaseBrainGateway.kt",
    "app/src/main/java/com/example/data/GeminiBrainGateway.kt",
    "app/src/main/java/com/example/data/model/HarmonyBrainModels.kt",
    "app/src/main/java/com/example/data/db/BrainDao.kt",
    "app/src/main/java/com/example/notifications/HarmonyGameNotifier.kt",
]:
    delete(ROOT / rel)

for base in [TEST, ANDROID_TEST]:
    if base.exists():
        for path in list(base.rglob("*.kt")):
            if path.name == "ProductionHarmonyBrainRemovalContractTest.kt":
                continue
            normalized = path.as_posix().lower()
            if "brain" in path.name.lower() or "/brain/" in normalized:
                delete(path)

# 2) Remove the hidden Home feature block and parameters.
home_rel = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
home = read(home_rel)
home = re.sub(
    r"\n\s*brainEnabled: Boolean = false,\n\s*brainInterests:.*?\n\s*onOpenBrainChat: \(\) -> Unit = \{\},",
    "",
    home,
    flags=re.S,
)
home = remove_if_block_after_marker(home, "if (brainEnabled) {", "if (brainEnabled)")
write(home_rel, home)

# 3) Remove the shared Brain suggestion UI from VoiceComponents.
voice_rel = "app/src/main/java/com/example/ui/components/VoiceComponents.kt"
voice = read(voice_rel)
voice = re.sub(r"^import com\.example\.data\.model\.BrainChatSuggestionItem\n", "", voice, flags=re.M)
voice = remove_function(voice, "fun BrainSuggestionCard(")
write(voice_rel, voice)

# 4) Strip feature state, generators and chat/search paths from HarmonyViewModel.
vm_rel = "app/src/main/java/com/example/ui/HarmonyViewModel.kt"
vm = read(vm_rel)
for pattern in [
    r"^import androidx\.lifecycle\.LifecycleOwner\n",
    r"^import com\.example\.data\.GeminiBrainGateway\n",
    r"^import com\.example\.data\.brain\..*\n",
    r"^import com\.example\.data\.model\.BrainChatSuggestionItem\n",
    r"^import com\.example\.data\.model\.MemoryDefaults\n",
    r"^import com\.example\.data\.model\.MemoryEntryEntity\n",
    r"^import com\.example\.data\.model\.MemoryEntryKind\n",
    r"^import com\.example\.data\.repository\.MemoryRepository\n",
    r"^import com\.example\.data\.repository\.RoomMemoryRepository\n",
    r"^import com\.example\.util\.GeminiAudioTranscriber\n",
    r"^import kotlinx\.coroutines\.async\n",
    r"^import kotlinx\.coroutines\.awaitAll\n",
    r"^import kotlinx\.coroutines\.coroutineScope\n",
    r"^import kotlinx\.coroutines\.flow\.firstOrNull\n",
    r"^import kotlinx\.serialization\.json\.Json\n",
    r"^import java\.io\.File\n",
    r"^import java\.net\.URLEncoder\n",
    r"^import java\.util\.UUID\n",
]:
    vm = re.sub(pattern, "", vm, flags=re.M)
vm = re.sub(r"\nprivate const val HARMONY_BRAIN_ENABLED = false\n", "\n", vm)
vm = re.sub(r"^\s*val brain(?:Interests|Suggestions|Questions|Messages):.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*val isBrain(?:ChatMode|Generating):.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*val generatedGames:.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private val brainRepository =.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private val brainGateway =.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private val generatedJson =.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private var foregroundGameGenerator:.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private val _brainMessages =.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private val _isBrainChatMode =.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private val _isBrainGenerating =.*\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*private val memoryRepository:.*\n", "", vm, flags=re.M)

combine_start = vm.find('    @Suppress("UNCHECKED_CAST")\n    val uiState: StateFlow<HarmonyUiState> = combine(')
combine_end_marker = "\n\n    private fun installDriveTotImages"
combine_end = vm.find(combine_end_marker, combine_start)
if combine_start < 0 or combine_end < 0:
    raise RuntimeError("Could not locate HarmonyUiState combine block")
new_combine = '''    @Suppress("UNCHECKED_CAST")
    val uiState: StateFlow<HarmonyUiState> = combine(
        _selectedTab,
        repository.profileFlow,
        repository.answersFlow,
        repository.chatMessagesFlow,
        repository.sharedPicsFlow,
        repository.momentsFlow,
        repository.statsFlow,
        _packFilter,
        _selectedTopicId,
        _selectedCategoryId,
        _activeRun,
        _isExitConfirmOpen,
        _isOwnAnswerDialogOpen,
        _isProfileSheetOpen,
        _isEditProfileOpen,
        _isAddMomentOpen,
        _toastMessage,
        _isRefreshing,
        _isDarkMode,
        _appLanguage
    ) { values ->
        HarmonyUiState(
            selectedTab = values[0] as Int,
            profile = (values[1] as? ProfileEntity) ?: ProfileEntity(),
            answers = (values[2] as? List<AnswerEntity>) ?: emptyList(),
            messages = (values[3] as? List<ChatMessageEntity>) ?: emptyList(),
            sharedPics = (values[4] as? List<SharedPicEntity>) ?: emptyList(),
            moments = (values[5] as? List<MomentEntity>) ?: emptyList(),
            stats = (values[6] as? CoupleStatsEntity) ?: CoupleStatsEntity(),
            packFilter = values[7] as String,
            selectedTopicId = values[8] as? String,
            selectedCategoryId = values[9] as? String,
            activeRun = values[10] as? ActivePackRun,
            isExitConfirmOpen = values[11] as Boolean,
            isOwnAnswerDialogOpen = values[12] as Boolean,
            ownAnswerTargetIndex = _ownAnswerTargetIndex.value,
            ownAnswerMode = _ownAnswerMode.value,
            isProfileSheetOpen = values[13] as Boolean,
            isEditProfileOpen = values[14] as Boolean,
            isAddMomentOpen = values[15] as Boolean,
            toastMessage = values[16] as? String,
            isRefreshing = values[17] as Boolean,
            isDarkMode = values[18] as Boolean,
            appLanguage = values[19] as String
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = HarmonyUiState()
    )'''
vm = vm[:combine_start] + new_combine + vm[combine_end:]

marker = vm.find("        // --- HARMONY BRAIN ANALYZER ---")
select_pos = vm.find("\n    fun selectTab", marker)
if marker >= 0 and select_pos >= 0:
    vm = vm[:marker] + "    }\n" + vm[select_pos:]

start = vm.find("    fun attachAutoGeneration(")
end = vm.find("    fun clearToast()", start)
if start >= 0 and end >= 0:
    vm = vm[:start] + vm[end:]
vm = re.sub(r"^\s*repository\.recordBrainPackFinished\([^\n]*\)\n", "", vm, flags=re.M)
vm = re.sub(r"^\s*repository\.recordBrainSkip\([^\n]*\)\n", "", vm, flags=re.M)
brain_section = vm.find("    // --- HARMONY BRAIN ---")
if brain_section >= 0:
    final_brace = vm.rfind("}")
    vm = vm[:brain_section].rstrip() + "\n}\n"
write(vm_rel, vm)

# 5) Remove feature persistence/signals from the normal repository.
repo_rel = "app/src/main/java/com/example/data/repository/HarmonyRepository.kt"
repo = read(repo_rel)
repo = re.sub(r"^import com\.example\.data\.brain\..*\n", "", repo, flags=re.M)
repo = re.sub(r"^import com\.example\.data\.model\.Brain.*\n", "", repo, flags=re.M)
repo = re.sub(r"^import com\.example\.data\.model\.EitherOrAnswerCodec\n", "", repo, flags=re.M)
repo = re.sub(r"^\s*val brain(?:Interests|Suggestions|Questions)Flow:.*\n", "", repo, flags=re.M)
repo = re.sub(r"^\s*val brainRepository =.*\n", "", repo, flags=re.M)
repo = re.sub(
    r"\n\s*// Perform initial idempotent backfill.*?brainRepository\.performInitialBackfillIfNeeded\(legacyAnswers\)\n",
    "\n",
    repo,
    flags=re.S,
)
save_start = repo.find("    suspend fun saveAnswer(")
send_start = repo.find("    suspend fun sendChatMessage(", save_start)
if save_start < 0 or send_start < 0:
    raise RuntimeError("Could not locate saveAnswer/sendChatMessage")
new_save = '''    suspend fun saveAnswer(packId: String, questionIndex: Int, answerText: String) {
        answerSaveMutex.withLock {
            val existing = db.answerDao().getAllAnswersDirect().firstOrNull {
                it.packId == packId && it.questionIndex == questionIndex
            }
            if (existing?.answerText == answerText) return@withLock
            db.answerDao().insertAnswer(
                AnswerEntity(
                    packId = packId,
                    questionIndex = questionIndex,
                    answerText = answerText
                )
            )
        }
    }

'''
repo = repo[:save_start] + new_save + repo[send_start:]
repo = re.sub(r"^\s*brainRepository\.recordMoment\([^\n]*\)\n", "", repo, flags=re.M)
persist_start = repo.find("    // --- HARMONY BRAIN PERSISTENCE ---")
copy_start = repo.find("    private suspend fun copyMediaToApp", persist_start)
if persist_start >= 0 and copy_start >= 0:
    repo = repo[:persist_start] + repo[copy_start:]
write(repo_rel, repo)

# 6) Remove Room entities/DAO and install an upgrade cleanup migration.
db_rel = "app/src/main/java/com/example/data/db/AppDatabase.kt"
db = read(db_rel)
db = re.sub(r"^import com\.example\.data\.model\.Brain.*\n", "", db, flags=re.M)
db = re.sub(r"^import com\.example\.data\.brain\..*\n", "", db, flags=re.M)
db = re.sub(r"^\s*Brain[A-Za-z0-9_]+::class,?\n", "", db, flags=re.M)
db = db.replace("    version = 9,", "    version = 10,")
db = re.sub(r"^\s*abstract fun brain(?:Dao|RoomDao)\(\):.*\n", "", db, flags=re.M)
db = db.replace("                        MIGRATION_8_9\n", "                        MIGRATION_8_9,\n                        MIGRATION_9_10\n")

m45 = db.find("        internal val MIGRATION_4_5")
m56 = db.find("        internal val MIGRATION_5_6", m45)
m67 = db.find("        internal val MIGRATION_6_7", m56)
if min(m45, m56, m67) < 0:
    raise RuntimeError("Could not locate old DB migrations")
noop45 = '''        internal val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) = Unit
        }

'''
noop56 = '''        internal val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(db: SupportSQLiteDatabase) = Unit
        }

'''
db = db[:m45] + noop45 + noop56 + db[m67:]
db = re.sub(
    r"\n\s*db\.execSQL\(\n\s*\"UPDATE brain_answer_history.*?\n\s*\)\n",
    "\n",
    db,
    flags=re.S,
)
last = db.rfind("    }\n}")
if last < 0:
    raise RuntimeError("Could not locate database companion close")
cleanup = '''
        internal val MIGRATION_9_10 = object : Migration(9, 10) {
            override fun migrate(db: SupportSQLiteDatabase) {
                val obsoleteTables = listOf(
                    "brain_interests",
                    "brain_suggestions",
                    "brain_questions",
                    "brain_answer_history",
                    "brain_preferences",
                    "brain_interactions",
                    "brain_memory_facts",
                    "brain_generated_content",
                    "brain_pending_generation"
                )
                obsoleteTables.forEach { table -> db.execSQL("DROP TABLE IF EXISTS `$table`") }
            }
        }
'''
db = db[:last] + cleanup + db[last:]
write(db_rel, db)

# 7) Remove remaining completion-history calls from MainActivity; answers are canonical.
main_rel = "app/src/main/java/com/example/MainActivity.kt"
main = read(main_rel)
main = re.sub(r"^\s*appDb\.brainRoomDao\(\)\.clearFinishedPack\(packId\)\n", "", main, flags=re.M)
main = re.sub(
    r"\s*runnerScope\.launch \{\n\s*if \(appDb\.brainRoomDao\(\)\.hasFinishedPack\(packId\)\) \{\n\s*resultsPackId = packId\n\s*\} else \{\n\s*openPackForPlay\(packId\)\n\s*\}\n\s*\}",
    "\n        openPackForPlay(packId)",
    main,
)
write(main_rel, main)

# 8) Delete any remaining production file whose filename is explicitly Brain-feature-specific.
for path in list(MAIN.rglob("*.kt")):
    if "brain" in path.name.lower():
        delete(path)

# 8b) Execute the hidden-path cleanup pass in the same migration context.
exec(compile((ROOT / "scripts/remove_harmony_brain_extras.py").read_text(encoding="utf-8"), "scripts/remove_harmony_brain_extras.py", "exec"))

# 9) Final source guard. Only legacy DROP TABLE tombstones may retain brain_* identifiers.
forbidden = [
    "HarmonyBrain", "Harmony Brain", "BrainRepository", "SupabaseBrain",
    "SupabaseHarmonyBrainGateway", "ForegroundGameGenerator", "HARMONY_BRAIN_ENABLED",
    "brainEnabled", "brainInterests", "brainSuggestions", "brainQuestions", "brainMessages",
    "isBrainChatMode", "isBrainGenerating", "brainRoomDao", "recordBrain",
    "BrainChatSuggestion", "HarmonyGameNotifier", "generated_game_id", "harmony_generated_games",
    "com.example.data.brain"
]
leaks = []
for path in MAIN.rglob("*.kt"):
    body = path.read_text(encoding="utf-8")
    for marker in forbidden:
        if marker in body:
            leaks.append(f"{path.relative_to(ROOT)}: {marker}")
if leaks:
    raise SystemExit("Remaining production Brain references:\n" + "\n".join(leaks))

print("Harmony Brain production feature removed; only DB tombstone names may remain in migration 9->10.")
