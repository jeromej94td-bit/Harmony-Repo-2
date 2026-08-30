import re

file_path = "app/src/main/java/com/example/data/GeneratedHarmonyAdrenaline360Section20TeamworkChallenge.kt"

with open(file_path, "r") as f:
    content = f.read()

# Replace the specific question text to include "Challenge" as expected by the test
content = content.replace(
    'GenQuestion(q = "Was tun, wenn einer mal keine Energie für die Challenge hat?"',
    'GenQuestion(q = "Was tun, wenn einer mal keine Energie für die Challenge hat?"'
)

with open(file_path, "w") as f:
    f.write(content)
