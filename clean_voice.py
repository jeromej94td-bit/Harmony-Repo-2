import re
with open("app/src/main/java/com/example/ui/components/VoiceComponents.kt", "r") as f:
    text = f.read()

text = re.sub(r'import com\.example\.data\.model\.BrainChatSuggestionItem\n', '', text)
text = re.sub(r'/\*\*[\s\S]*?fun BrainSuggestionCard\([\s\S]*?\}\n\s*\}\n', '', text)

with open("app/src/main/java/com/example/ui/components/VoiceComponents.kt", "w") as f:
    f.write(text)
