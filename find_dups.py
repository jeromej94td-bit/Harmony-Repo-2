import os
import re

id_regex = re.compile(r'id\s*=\s*"([^"]+)"')
topic_regex = re.compile(r'topic\s*=\s*"([^"]+)"')

all_packs = {}

for root, dirs, files in os.walk('app/src/main/java'):
    for file in files:
        if file.endswith('.kt'):
            path = os.path.join(root, file)
            with open(path, 'r', encoding='utf-8') as f:
                content = f.read()
                # Split by QuestionPack or GenPack
                blocks = re.split(r'(?:QuestionPack|GenPack)\s*\(', content)
                for block in blocks[1:]:
                    id_match = id_regex.search(block)
                    topic_match = topic_regex.search(block)
                    if id_match:
                        pack_id = id_match.group(1)
                        topic = topic_match.group(1) if topic_match else "unknown"
                        if topic == "essen":
                            if pack_id in all_packs:
                                print(f"DUPLICATE FOUND: {pack_id} in {path} and {all_packs[pack_id]}")
                            else:
                                all_packs[pack_id] = path

print("Finished checking.")
