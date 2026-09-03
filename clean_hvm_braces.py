with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    lines = f.readlines()

out_lines = []
skip_mode = False
brace_count = 0

funcs_to_remove = ["fun saveSuggestionToNotes(", "fun sendVoiceBrainMessage(", "fun sendBrainMessage(", "fun resetBrainChat(", "fun answerBrainQuestion("]

for line in lines:
    if not skip_mode:
        for func in funcs_to_remove:
            if func in line:
                skip_mode = True
                brace_count = 0
                break
                
    if skip_mode:
        for char in line:
            if char == '{':
                brace_count += 1
            elif char == '}':
                brace_count -= 1
        if brace_count == 0:
            skip_mode = False
        continue

    out_lines.append(line)

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "w") as f:
    f.writelines(out_lines)
