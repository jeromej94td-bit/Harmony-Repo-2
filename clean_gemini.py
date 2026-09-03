import re

gemini_file = "app/src/main/java/com/example/util/GeminiGameGenerator.kt"
with open(gemini_file, 'r') as f:
    text = f.read()

text = re.sub(r'import com\.example\.data\.HarmonyBrainEngine\n', '', text)
text = re.sub(r'\s*val interests = HarmonyBrainEngine\.analyzeAnswers\(answers\)', '', text)
text = re.sub(r'\s*if \(interests\.isNotEmpty\(\)\) \{[\s\S]*?\}\n', '\n', text)

with open(gemini_file, 'w') as f:
    f.write(text)
print("Updated GeminiGameGenerator.kt")
