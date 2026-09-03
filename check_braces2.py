with open("app/src/main/java/com/example/util/GeminiGameGenerator.kt", "r") as f:
    lines = f.readlines()

count = 0
for line_num, line in enumerate(lines, 1):
    for char in line:
        if char == '{':
            count += 1
        elif char == '}':
            count -= 1
            if count == 0:
                print(f"Top level reached at line {line_num}")
