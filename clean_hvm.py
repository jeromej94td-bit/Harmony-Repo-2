import re

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    text = f.read()

# Remove BrainMessage property from AppState
text = re.sub(r'\s*val brainMessages: List<com\.example\.data\.model\.BrainMessage> = emptyList\(\),', '', text)

# Remove _brainMessages declaration
text = re.sub(r'\s*private val _brainMessages = MutableStateFlow<List<com\.example\.data\.model\.BrainMessage>>\(emptyList\(\)\)', '', text)

# Remove _brainMessages from combine
text = re.sub(r'\s*_brainMessages,', '', text)
text = re.sub(r'\s*brainMessages = \(arrayOfValues\[23\] as\? List<com\.example\.data\.model\.BrainMessage>\) \?\: emptyList\(\),', '', text)

# Remove init reset
text = re.sub(r'\s*_brainMessages\.value = listOf\([\s\S]*?\n\s*\)', '', text)

# Remove methods
text = re.sub(r'\s*fun toggleBrainChatMode\(\) \{[\s\S]*?\}', '', text)
text = re.sub(r'\s*fun sendVoiceBrainMessage\(audioPath: String, durationSeconds: Int\) \{[\s\S]*?\}\s*\}', '', text)

# Actually, the methods are large. Let's just find and replace the whole blocks using regex or just clear out the end of the file.
