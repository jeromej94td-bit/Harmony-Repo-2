import re

with open('app/src/main/java/com/example/data/GeneratedHarmonyAdrenaline360.kt', 'r') as f:
    text = f.read()
    print("Has Section 01?", "GeneratedHarmonyAdrenaline360Section01BeziehungNaehe" in text)
