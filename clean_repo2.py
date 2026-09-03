import re

repo_file = "app/src/main/java/com/example/data/repository/HarmonyRepository.kt"
with open(repo_file, 'r') as f:
    text = f.read()

# Clean triggerInitialBackfill
text = re.sub(r'\s*// Perform initial idempotent backfill.*?brainRepository\.performInitialBackfillIfNeeded\(legacyAnswers\)', '', text, flags=re.DOTALL)

# Clean saveAnswer
text = re.sub(
    r'suspend fun saveAnswer\(.*?\{.*?answerSaveMutex\.withLock \{[\s\S]*?db\.answerDao\(\)\.insertAnswer\([\s\S]*?\}\s*\}',
    '''suspend fun saveAnswer(packId: String, questionIndex: Int, answerText: String) {
        answerSaveMutex.withLock {
            val existing = db.answerDao().getAllAnswersDirect().firstOrNull {
                it.packId == packId && it.questionIndex == questionIndex
            }
            if (existing?.answerText == answerText) {
                return@withLock
            }
            db.answerDao().insertAnswer(
                AnswerEntity(
                    packId = packId,
                    questionIndex = questionIndex,
                    answerText = answerText
                )
            )
        }
    }''',
    text,
    flags=re.DOTALL
)

# Clean brainHistoryMatches
text = re.sub(r'private fun brainHistoryMatches\([\s\S]*?\}\s*\}', '', text)

# Clean recordMoment
text = re.sub(r'\s*brainRepository\.recordMoment\(title, content\)', '', text)

# Clean HARMONY BRAIN PERSISTENCE block
text = re.sub(r'\s*// --- HARMONY BRAIN PERSISTENCE ---[\s\S]*?clearQuestions\(\)', '', text)

with open(repo_file, 'w') as f:
    f.write(text)
print("Updated HarmonyRepository.kt")
