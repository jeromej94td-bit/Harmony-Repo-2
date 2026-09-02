from pathlib import Path

root = Path(__file__).resolve().parents[1]
vm = (root / 'app/src/main/java/com/example/ui/memory/MemoryViewModel.kt').read_text()
editor = (root / 'app/src/main/java/com/example/ui/screens/MemoryEditorSheet.kt').read_text()
cards = (root / 'app/src/main/java/com/example/ui/screens/MemoryCards.kt').read_text()
codec = (root / 'app/src/main/java/com/example/data/model/MemoryChecklist.kt').read_text()

start = vm.index('    fun saveList(')
end = vm.index('    fun saveLink(', start)
save_list = vm[start:end]

# One user Save action creates/updates one MemoryEntryEntity containing the encoded checklist.
assert save_list.count('repository.insertEntries(listOf(entry))') == 1, 'new checklist must insert exactly one memory row'
assert 'MemoryChecklistCodec.encode(normalizedItems)' in save_list, 'all list rows must be encoded into that single memory row'
assert 'normalizedItems.map {' not in save_list and 'normalizedItems.forEach' not in save_list, 'saveList must not fan out rows into separate notes'

# The editor sends the whole normalized list once and the card renders all decoded rows inside one card.
assert 'MemoryEditorMode.LIST -> onSaveList(' in editor, 'editor must call one list save action'
assert 'normalizedItems' in editor[editor.index('MemoryEditorMode.LIST -> onSaveList('):], 'editor must pass the complete list payload'
assert 'MemoryChecklistCodec.decode(entry.body)' in cards, 'one card must decode all list rows'
assert 'checklistItems.take(5).forEach' in cards, 'list rows must render inside a single card body'
assert 'fun encode(items: List<MemoryChecklistItem>): String = adapter.toJson(items)' in codec

print('Memory list contract passed: current source stores one multiline checklist as one MemoryEntryEntity; no source-level row split is present.')
