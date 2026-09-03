import re
with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    text = f.read()

text = text.replace("    fun selectTab(tabIndex: Int) {", "    }\n\n    fun selectTab(tabIndex: Int) {")

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "w") as f:
    f.write(text)
