from pathlib import Path
root=Path(__file__).resolve().parents[1]
models=(root/'app/src/main/java/com/example/data/model/Models.kt').read_text()
db=(root/'app/src/main/java/com/example/data/db/AppDatabase.kt').read_text()
repo=(root/'app/src/main/java/com/example/data/repository/HarmonyRepository.kt').read_text()
brain=(root/'app/src/main/java/com/example/data/brain/repository/BrainRepository.kt').read_text()
screen=(root/'app/src/main/java/com/example/ui/screens/MomentsScreen.kt').read_text()
vm=(root/'app/src/main/java/com/example/ui/HarmonyViewModel.kt').read_text()
assert 'imagePathsJson: String = "[]"' in models, 'MomentEntity must persist multiple images'
assert 'version = 8' in db and 'MIGRATION_7_8' in db and 'imagePathsJson' in db, 'Room migration 7->8 required'
assert 'copyMediaToApp(uri, "moments")' in repo, 'moment images must be copied into app-owned storage'
assert 'recordMoment(' in repo and 'suspend fun recordMoment' in brain, 'saved moments must feed Harmony Brain memory'
assert 'GetMultipleContents' in screen and 'MomentPhotoCarousel' in screen, 'Moments UI needs multi-photo picker and visual carousel'
assert 'imageUris: List<Uri> = emptyList()' in vm, 'ViewModel must accept selected images while preserving old two-arg calls'
print('Moments contract passed')
