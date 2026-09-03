import re

gs_file = "app/src/main/java/com/example/ui/screens/GamesScreen.kt"
with open(gs_file, 'r') as f:
    text = f.read()

# Add empty generatedGames to GamesScreen so we don't change the API
text = re.sub(
    r'brainEnabled: Boolean = false,',
    'brainEnabled: Boolean = false,\n    generatedGames: List<Any> = emptyList(),',
    text
)

with open(gs_file, 'w') as f:
    f.write(text)
