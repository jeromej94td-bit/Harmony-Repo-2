import re

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    text = f.read()

# Remove imports
text = re.sub(r'^import com\.example\.data\.brain\..*?\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^import com\.example\.data\.GeminiBrainGateway.*?\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^import com\.example\.data\.model\.BrainChatSuggestionItem.*?\n', '', text, flags=re.MULTILINE)

# Remove HARMONY_BRAIN_ENABLED
text = re.sub(r'^\s*private const val HARMONY_BRAIN_ENABLED = false.*?\n', '', text, flags=re.MULTILINE)

# Remove AppState properties
text = re.sub(r'^\s*val brainInterests: List<com\.example\.data\.model\.BrainInterestEntity> = emptyList\(\),\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*val brainSuggestions: List<com\.example\.data\.model\.BrainSuggestionEntity> = emptyList\(\),\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*val brainQuestions: List<com\.example\.data\.model\.BrainQuestionEntity> = emptyList\(\),\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*val isBrainChatMode: Boolean = false,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*val isBrainGenerating: Boolean = false,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*val generatedGames: List<com\.example\.data\.brain\.db\.BrainGeneratedContentEntity> = emptyList\(\)\n', '', text, flags=re.MULTILINE)

# Remove class properties
text = re.sub(r'^\s*private val brainRepository = com\.example\.data\.brain\.repository\.BrainRepository\(db\.brainRoomDao\(\), application\)\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*private val brainGateway = SupabaseHarmonyBrainGateway\.getInstance\(\)\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*private val _isBrainChatMode = MutableStateFlow\(false\)\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*private val _isBrainGenerating = MutableStateFlow\(false\)\n', '', text, flags=re.MULTILINE)

# Remove from combine block
text = re.sub(r'^\s*repository\.brainInterestsFlow,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*repository\.brainSuggestionsFlow,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*repository\.brainQuestionsFlow,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*_isBrainChatMode,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*_isBrainGenerating,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*brainRepository\.generatedContentFlow\n', '', text, flags=re.MULTILINE)

# Remove mappings
text = re.sub(r'^\s*val rawGenerated = \(arrayOfValues\[26\] as\? List<com\.example\.data\.brain\.db\.BrainGeneratedContentEntity>\) \?\: emptyList\(\)\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*brainInterests = \(arrayOfValues\[20\] as\? List<com\.example\.data\.model\.BrainInterestEntity>\) \?\: emptyList\(\),\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*brainSuggestions = \(arrayOfValues\[21\] as\? List<com\.example\.data\.model\.BrainSuggestionEntity>\) \?\: emptyList\(\),\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*brainQuestions = \(arrayOfValues\[22\] as\? List<com\.example\.data\.model\.BrainQuestionEntity>\) \?\: emptyList\(\),\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*isBrainChatMode = arrayOfValues\[24\] as Boolean,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*isBrainGenerating = arrayOfValues\[25\] as Boolean,\n', '', text, flags=re.MULTILINE)
text = re.sub(r'^\s*generatedGames = rawGenerated\n', '', text, flags=re.MULTILINE)

# Remove HARMONY_BRAIN_ENABLED blocks
text = re.sub(r'^\s*if \(HARMONY_BRAIN_ENABLED\) \{[\s\S]*?\}\s*\} // End HARMONY_BRAIN_ENABLED\n', '', text, flags=re.MULTILINE)
text = re.sub(r'\s*if \(!HARMONY_BRAIN_ENABLED\) return\n', '', text, flags=re.MULTILINE)

# Remove repository.recordBrainPackFinished calls
text = re.sub(r'^\s*repository\.recordBrainPackFinished\(run\.pack\.id\)\n', '', text, flags=re.MULTILINE)

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "w") as f:
    f.write(text)
