from pathlib import Path
s=(Path(__file__).resolve().parents[1]/'app/src/main/java/com/example/ui/HarmonyViewModel.kt').read_text()
assert s.count('GameRunPolicy.initialState') >= 2, 'static and generated games must both resume from saved state'
assert 'isFinished = false\n        )' not in s[s.find('fun startPack'):s.find('fun pickAnswer')], 'startPack must not force finished games open'
print('Game resume contract passed')
