with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "r") as f:
    lines = f.readlines()

out_lines = []
skip_mode = False
brace_count = 0

for line in lines:
    if "// --- HARMONY BRAIN ANALYZER ---" in line:
        skip_mode = True
        brace_count = 0 # It doesn't have braces, but the line after has "if (HARMONY_BRAIN_ENABLED) {"
        continue
    
    if skip_mode:
        if "if (HARMONY_BRAIN_ENABLED) {" in line:
            brace_count = 1
        elif "}" in line and " // End HARMONY_BRAIN_ENABLED" in line:
            pass # We rely on counting braces instead of specific comments
            
        for char in line:
            if char == '{':
                brace_count += 1
            elif char == '}':
                brace_count -= 1
        
        if brace_count <= 0 and "}" in line: # Found end of block
            skip_mode = False
        continue
        
    out_lines.append(line)

with open("app/src/main/java/com/example/ui/HarmonyViewModel.kt", "w") as f:
    f.writelines(out_lines)
