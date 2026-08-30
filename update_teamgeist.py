import re

file_path = "app/src/main/java/com/example/data/Harmony360TeamworkSectionCuration.kt"

with open(file_path, "r") as f:
    content = f.read()

# Replace the text of teamgeist
content = content.replace(
    'q("Wie gut ermutigt ihr euch, eine selbstgewählte Komfortzone zu verlassen, ohne Druck aufzubauen?"',
    'q("Wie gut ermutigt ihr euch, eine selbstgewählte Komfortzone zu verlassen, ohne Druck aufzubauen?"'
)

with open(file_path, "w") as f:
    f.write(content)
