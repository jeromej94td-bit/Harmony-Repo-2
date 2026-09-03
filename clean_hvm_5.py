import re
with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    text = f.read()

text = re.sub(r'\s*private var foregroundGameGenerator: ForegroundGameGenerator\? = null\n', '', text)
text = re.sub(r'\s*fun attachAutoGeneration\(lifecycleOwner: LifecycleOwner\) \{[\s\S]*?\}\n\s*\}\n', '', text)
text = re.sub(r'\s*fun startGeneratedGame\(gameId: String\) \{[\s\S]*?\}\n\s*\}\n', '', text)

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "w") as f:
    f.write(text)
