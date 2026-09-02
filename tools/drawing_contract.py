from pathlib import Path
root=Path(__file__).resolve().parents[1]
models=(root/'app/src/main/java/com/example/data/model/Models.kt').read_text()
runner=(root/'app/src/main/java/com/example/ui/screens/QuizRunnerScreen.kt').read_text()
canvas=(root/'app/src/main/java/com/example/ui/screens/DrawingPromptCanvas.kt')
block=models[models.index('id = "zeichnen"'):models.index('id = "zustimmen"')]
assert 'type = "draw"' in block, 'drawing pack must use draw runner type'
assert block.count('Question(') == 5, 'drawing pack must contain five prompts'
assert 'Zauberhut' in block and 'Roboter' in block, 'drawing prompts should be playful, not answer questions'
assert canvas.exists(), 'drawing canvas component must exist'
text=canvas.read_text()
assert 'Canvas(' in text and 'detectDragGestures' in text and 'Farben' in text, 'drawing canvas needs paint gestures and palette'
assert '.clickable { selectedColor = color }' in text, 'palette colors must work with a normal tap'
assert 'pack.type == "draw"' in runner and 'DrawingPromptCanvas(' in runner, 'quiz runner must route draw packs to paint canvas'
print('Drawing contract passed')
