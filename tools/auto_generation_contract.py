from pathlib import Path
root=Path(__file__).resolve().parents[1]
fg=(root/'app/src/main/java/com/example/data/brain/ForegroundGameGenerator.kt').read_text()
repo=(root/'app/src/main/java/com/example/data/brain/repository/BrainRepository.kt').read_text()
games=(root/'app/src/main/java/com/example/ui/screens/GamesScreen.kt').read_text()
alt=(root/'app/src/main/java/com/example/data/brain/HarmonyAutoGenerationManager.kt').read_text()
assert 'AutoGenerationPolicy.DAILY_LIMIT' in fg, 'foreground generator must use shared 20/day policy'
assert 'AutoGenerationPolicy.INTERVAL_MS' in fg, 'foreground generator must use shared one-minute interval'
assert 'getBoolean("startup_batch_done"' not in fg and 'putBoolean("startup_batch_done"' not in fg and 'repeat(2)' not in fg, 'no startup batch: one game per minute only'
assert 'count >= 10' not in fg, 'legacy 10/day limit must be removed'
assert 'HarmonyDuplicateDetector.isDuplicate' in repo, 'generated games must reject duplicate questions across history/static packs'
assert 'unreadGeneratedCount' in games, 'Games UI must compute unread generated games'
assert 'generatedGames.size} Neu' not in games, 'badge must not show all games as new'
assert 'HarmonyAutoGenerationManager' in alt and 'AutoGenerationPolicy.DAILY_LIMIT' in alt, 'legacy manager must share same policy if retained'
print('Auto-generation contract passed')
