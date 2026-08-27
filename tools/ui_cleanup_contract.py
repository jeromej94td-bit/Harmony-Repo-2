from pathlib import Path
root=Path(__file__).resolve().parents[1]
home=(root/'app/src/main/java/com/example/ui/screens/HomeScreen.kt').read_text()
games=(root/'app/src/main/java/com/example/ui/screens/GamesScreen.kt').read_text()
live=(root/'app/src/main/java/com/example/ui/screens/LiveChangeOverlay.kt').read_text()
main=(root/'app/src/main/java/com/example/MainActivity.kt').read_text()
assert 'HARMONY BRAIN COACH' not in home, 'unfinished Harmony Brain coach block must be removed from Home'
assert 'Tägliche Aktivität' not in games, 'daily activity banner must be removed from Games'
assert 'TimerPill()' not in games, 'daily timer banner must be removed'
assert 'onDismiss: () -> Unit' in live[live.index('fun LiveChangeLauncher'):live.index('fun LiveChangeHud')], 'launcher must accept dismiss callback'
assert 'contentDescription = "Live Change ausblenden"' in live, 'launcher must expose close button'
assert 'isLiveChangeLauncherVisible' in main, 'MainActivity must retain dismissed launcher state'
print('UI cleanup contract passed')
