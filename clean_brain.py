import re

def process_file(filepath, replacements):
    with open(filepath, 'r') as f:
        content = f.read()
    
    original = content
    for target, replacement in replacements:
        content = content.replace(target, replacement)
    
    if content != original:
        with open(filepath, 'w') as f:
            f.write(content)
        print(f"Updated {filepath}")
    else:
        print(f"No changes in {filepath}")

# HarmonyViewModelSkip.kt
vm_skip = "app/src/main/java/com/example/ui/HarmonyViewModelSkip.kt"
with open(vm_skip, 'r') as f:
    text = f.read()
text = re.sub(r'// .*?Harmony Brain.*?\n', '', text)
text = re.sub(r'\s*HarmonyRepository\(db, app\)\.recordBrainSkip\(packId, questionIndex\)', '', text)
with open(vm_skip, 'w') as f:
    f.write(text)

# MainActivity.kt
ma = "app/src/main/java/com/example/MainActivity.kt"
with open(ma, 'r') as f:
    text = f.read()
text = re.sub(r'\s*onAnswerBrainQuestion = \{ id, text -> viewModel\.answerBrainQuestion\(id, text\) \},', '', text)
text = re.sub(r'\s*onOpenBrainChat = \{[\s\S]*?\},', '', text)
text = re.sub(r'\s*isBrainChatMode = uiState\.isBrainChatMode,', '', text)
text = re.sub(r'\s*isBrainGenerating = uiState\.isBrainGenerating,', '', text)
text = re.sub(r'\s*onToggleBrainChatMode = \{ enabled -> viewModel\.setBrainChatMode\(enabled\) \},', '', text)
text = re.sub(r'\s*onSendBrainMessage = \{ text -> viewModel\.sendBrainMessage\(text\) \},', '', text)
text = re.sub(r'\s*onResetBrainChat = \{ viewModel\.resetBrainChat\(\) \},', '', text)
text = re.sub(r'\s*onSendVoiceBrainMessage = \{ path, duration -> viewModel\.sendVoiceBrainMessage\(path, duration\) \},', '', text)
with open(ma, 'w') as f:
    f.write(text)

# HarmonyGameNotifier.kt
hgn = "app/src/main/java/com/example/notifications/HarmonyGameNotifier.kt"
with open(hgn, 'r') as f:
    text = f.read()
text = text.replace("Euer Harmony Brain hat ein neues Spiel für euch erstellt", "Harmony hat ein neues Spiel für euch erstellt")
with open(hgn, 'w') as f:
    f.write(text)

