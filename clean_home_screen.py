import re

hs_file = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(hs_file, 'r') as f:
    text = f.read()

# Remove parameters
text = re.sub(r'\s*brainEnabled: Boolean = false,', '', text)
text = re.sub(r'\s*brainInterests: List<com\.example\.data\.model\.BrainInterestEntity> = emptyList\(\),', '', text)
text = re.sub(r'\s*brainSuggestions: List<com\.example\.data\.model\.BrainSuggestionEntity> = emptyList\(\),', '', text)
text = re.sub(r'\s*brainQuestions: List<com\.example\.data\.model\.BrainQuestionEntity> = emptyList\(\),', '', text)
text = re.sub(r'\s*onSuggestionFeedback: \(String, String\) -> Unit = \{ _, _ -> \},', '', text)
text = re.sub(r'\s*onAnswerBrainQuestion: \(String, String\) -> Unit = \{ _, _ -> \},', '', text)
text = re.sub(r'\s*onOpenBrainChat: \(\) -> Unit = \{\},', '', text)

# Remove usage
text = re.sub(r'\s*if \(brainEnabled\) \{[\s\S]*?\} // end brainEnabled', '', text)

with open(hs_file, 'w') as f:
    f.write(text)
