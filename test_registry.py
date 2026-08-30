import re

files = [
    "app/src/main/java/com/example/data/GeneratedContentRegistry.kt"
]

with open(files[0], 'r') as f:
    text = f.read()
    print("Does TopLevelTopicPlacementPolicy.apply exist?", "TopLevelTopicPlacementPolicy.apply" in text)
