import re
with open("app/src/main/java/com/example/util/GeminiGameGenerator.kt", "r") as f:
    text = f.read()

# Fix the extra brace
text = text.replace("""                }
                    append("\\n")
                }""", """                }""")

with open("app/src/main/java/com/example/util/GeminiGameGenerator.kt", "w") as f:
    f.write(text)
