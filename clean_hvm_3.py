import re
with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    text = f.read()

text = re.sub(r'\s*val brainMessages: List<com\.example\.data\.model\.BrainMessage> = emptyList\(\),', '', text)
text = re.sub(r'\s*_brainMessages\.value = listOf\([\s\S]*?\n\s*\)', '', text)

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "w") as f:
    f.write(text)
