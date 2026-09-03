import re

ds_file = "app/src/main/java/com/example/ui/screens/DevStudioScreen.kt"
with open(ds_file, 'r') as f:
    text = f.read()

text = re.sub(r'\s*7 -> DevBrainTab\([\s\S]*?\)', '', text)

# Fix commas or other things if necessary. What about tabs?
text = text.replace('"Dev Studio", "Profil", "Design QA", "Moments", "Debug", "Generierung", "Migration", "Harmony Brain"', '"Dev Studio", "Profil", "Design QA", "Moments", "Debug", "Generierung", "Migration"')

with open(ds_file, 'w') as f:
    f.write(text)
print("Updated DevStudioScreen.kt")
