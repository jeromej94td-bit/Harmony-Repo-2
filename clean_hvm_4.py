import re
with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    text = f.read()

text = re.sub(r',\s*generatedGames = publishedGames\n', '\n', text)

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "w") as f:
    f.write(text)
