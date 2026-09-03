with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "r") as f:
    lines = f.readlines()

out_lines = []
in_brain = False
brain_braces = 0

for line in lines:
    if "if (brainEnabled) {" in line:
        in_brain = True
        brain_braces = 1
        continue
    
    if in_brain:
        for char in line:
            if char == '{':
                brain_braces += 1
            elif char == '}':
                brain_braces -= 1
        if brain_braces == 0:
            in_brain = False
        continue
    
    out_lines.append(line)

with open("app/src/main/java/com/example/ui/screens/HomeScreen.kt", "w") as f:
    f.writelines(out_lines)
