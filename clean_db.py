import re

db_file = "app/src/main/java/com/example/data/db/AppDatabase.kt"
with open(db_file, 'r') as f:
    text = f.read()

# Remove imports
text = re.sub(r'import com\.example\.data\.model\.Brain.*\n', '', text)
text = re.sub(r'import com\.example\.data\.brain\.db\.Brain.*\n', '', text)

# Remove entities from @Database list
text = re.sub(r'\s*BrainInterestEntity::class,', '', text)
text = re.sub(r'\s*BrainSuggestionEntity::class,', '', text)
text = re.sub(r'\s*BrainQuestionEntity::class,', '', text)
text = re.sub(r'\s*BrainAnswerHistoryEntity::class,', '', text)
text = re.sub(r'\s*BrainPreferenceEntity::class,', '', text)
text = re.sub(r'\s*BrainInteractionEntity::class,', '', text)
text = re.sub(r'\s*BrainMemoryFactEntity::class,', '', text)
text = re.sub(r'\s*BrainGeneratedContentEntity::class,', '', text)
text = re.sub(r'\s*BrainPendingGenerationEntity::class,?', '', text)
text = re.sub(r',\s*\]', ']', text)  # Fix trailing comma if any

# Remove dao functions
text = re.sub(r'\s*abstract fun brainDao\(\): BrainDao\n', '', text)
text = re.sub(r'\s*abstract fun brainRoomDao\(\): BrainRoomDao\n', '', text)

with open(db_file, 'w') as f:
    f.write(text)
print("Updated AppDatabase.kt")
