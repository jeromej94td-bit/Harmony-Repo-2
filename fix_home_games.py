import re

hs_file = "app/src/main/java/com/example/ui/screens/HomeScreen.kt"
with open(hs_file, 'r') as f:
    text = f.read()
text = re.sub(r'onPinWidget: \(\) -> Unit,', 'onPinWidget: () -> Unit,\n    brainEnabled: Boolean = false,', text)
with open(hs_file, 'w') as f:
    f.write(text)

gs_file = "app/src/main/java/com/example/ui/screens/GamesScreen.kt"
with open(gs_file, 'r') as f:
    text = f.read()
text = re.sub(r'appLanguage: String,', 'appLanguage: String,\n    brainEnabled: Boolean = false,', text)
with open(gs_file, 'w') as f:
    f.write(text)
