import re
with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    text = f.read()

# Replace all brainEnabled parameters with just one
text = re.sub(r'(\s*brainEnabled: Boolean = false,)+', r'\1', text)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.write(text)
