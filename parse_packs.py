import os
import re

files = []
for root, dirs, filenames in os.walk('app/src/main/java/com/example/data'):
    for filename in filenames:
        if filename.endswith('.kt'):
            files.append(os.path.join(root, filename))

ids = []
for file in files:
    with open(file, 'r') as f:
        content = f.read()
        # Very simple regex to find id = "..." and topic = "essen" in the same block.
        # Actually let's just find all ids
