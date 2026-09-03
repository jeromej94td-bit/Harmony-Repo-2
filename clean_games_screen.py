import re

gs_file = "app/src/main/java/com/example/ui/screens/GamesScreen.kt"
with open(gs_file, 'r') as f:
    text = f.read()

text = re.sub(r'import com\.example\.data\.brain\.model\.GeneratedGamePayload\n', '', text)
text = re.sub(r'import com\.example\.data\.brain\.db\.BrainGeneratedContentEntity\n', '', text)

# Remove generatedGames parameter
text = re.sub(r'\s*generatedGames: List<BrainGeneratedContentEntity> = emptyList\(\),', '', text)
text = re.sub(r'\s*generatedGames: List<com\.example\.data\.brain\.db\.BrainGeneratedContentEntity> = emptyList\(\),', '', text)

# Remove the whole "if (brainEnabled && generatedGames.isNotEmpty())" block
text = re.sub(r'\s*if \(brainEnabled && generatedGames\.isNotEmpty\(\)\) \{[\s\S]*?\}\s*\}\s*\}', '', text)

with open(gs_file, 'w') as f:
    f.write(text)
