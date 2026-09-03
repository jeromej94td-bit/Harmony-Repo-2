import re

repo_file = "app/src/main/java/com/example/data/repository/HarmonyRepository.kt"
with open(repo_file, 'r') as f:
    text = f.read()

# Remove imports
text = re.sub(r'import com\.example\.data\.brain.*\n', '', text)
text = re.sub(r'import com\.example\.data\.model\.Brain.*\n', '', text)

# Remove properties
text = re.sub(r'\s*val brainInterestsFlow.*?\n', '', text)
text = re.sub(r'\s*val brainSuggestionsFlow.*?\n', '', text)
text = re.sub(r'\s*val brainQuestionsFlow.*?\n', '', text)
text = re.sub(r'\s*val brainRepository.*?\n', '', text)

# Remove brain skip and finish
text = re.sub(r'\s*suspend fun recordBrainSkip\([\s\S]*?\}', '', text)
text = re.sub(r'\s*suspend fun recordBrainPackFinished\([\s\S]*?\}', '', text)

# Remove brain getters and savers
text = re.sub(r'\s*suspend fun getAllInterests[\s\S]*?insertInterests\(interests\)', '', text)
text = re.sub(r'\s*suspend fun getAllSuggestions[\s\S]*?updateSuggestion\(suggestion\)', '', text)
text = re.sub(r'\s*suspend fun getAllQuestions[\s\S]*?updateQuestion\(question\)', '', text)

# Remove the backfill blocks in triggerInitialBackfill
text = re.sub(r'// Perform initial idempotent backfill of legacy answers into BrainAnswerHistory[\s\S]*?// The durable answer exists but its Brain history may have been interrupted\s*// between the Room write and the append-only Brain write\. Repair only the.*?\}', '', text)
text = re.sub(r'private fun brainHistoryMatches\(\s*history: BrainAnswerHistoryEntity\?,\s*answer: String\s*\): Boolean \{\s*return history \!= null && history\.answerText == answer\s*\}', '', text)

with open(repo_file, 'w') as f:
    f.write(text)
print("Updated HarmonyRepository.kt")
