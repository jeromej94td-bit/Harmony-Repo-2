with open("app/src/main/java/com/example/ui/screens/DevStudioScreen.kt", "r") as f:
    text = f.read()

text = text.replace('", "🧠 Brain"', '')

with open("app/src/main/java/com/example/ui/screens/DevStudioScreen.kt", "w") as f:
    f.write(text)
