import re

with open("app/src/main/java/com/example/ui/screens/ChatScreenLegacyBridge.kt", "r") as f:
    text = f.read()

text = re.sub(r'import com\.example\.data\.model\.BrainChatSuggestionItem\n', '', text)
text = re.sub(r'import com\.example\.data\.model\.BrainMessage\n', '', text)

text = re.sub(r'brainMessages: List<BrainMessage>', 'brainMessages: List<Any>', text)
text = re.sub(r'onSaveSuggestionToNotes: \(BrainChatSuggestionItem\) -> Unit', 'onSaveSuggestionToNotes: (Any) -> Unit', text)

with open("app/src/main/java/com/example/ui/screens/ChatScreenLegacyBridge.kt", "w") as f:
    f.write(text)
